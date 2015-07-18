package com.playfun.lineonline;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.NetInfoParser;
import com.playfun.lineonline.widget.BookOnTouchListener;
import com.playfun.lineonline.widget.LvAdapter;
import com.playfun.lineonline.widget.MyListView;
import com.playfun.lineonline.widget.MyListView.OnRefreshListener;
import com.playfun.lineonline.widget.MyOnClickListener;
import com.playfun.lineonline.widget.SlidingMenu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 游玩项目预订页面<br/>
 * 显示所有可供预订的游玩项目及时间段<br/>
 *
 * @author Robert Peng
 */
public class BookActivity extends Activity {

	private ArrayList<View> mLineList;
	private LvAdapter mAdapter;
	private MyListView mListView;
	private LayoutInflater mInflater;
	private String playgroundID;
	private String userID;
	private Bundle mBundle;
	private BookOnTouchListener mBookOnTouchListener = new BookOnTouchListener(this);
	private String bookedNum;
	private TextView bookedView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_main);
		mInflater = LayoutInflater.from(this);

		mBundle = getIntent().getExtras();
		// 包括 userID, userName, playgroundID
		
		bookedView = (TextView) findViewById(R.id.bookedNum);
        // 绑定侧滑菜单按钮
		findViewById(R.id.listBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SlidingMenu.getInstance().show();
			}
		});
        // 绑定地图页面跳转按钮
		findViewById(R.id.mapBtn).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent mapIntent = new Intent(BookActivity.this, MapActivity.class);
				mapIntent.putExtras(new Bundle(mBundle));
				startActivity(mapIntent);
			}
		});

        // 为搜索框设定小图标
		EditText mEditText = (EditText) findViewById(R.id.attrSearch);
		Drawable img = getResources().getDrawable(R.drawable.search);
		img.setBounds(0, 0, (int) mEditText.getTextSize(), (int) mEditText.getTextSize());
		mEditText.setCompoundDrawables(img, null, null, null);

        mListView = (MyListView) findViewById(R.id.attrList);
		userID = mBundle.getString("userID");
		((TextView)findViewById(R.id.userName)).setText(mBundle.getString("userName"));
		playgroundID = mBundle.getString("playgroundID");

		updateList();

		mListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				updateList();
			}
		});
		mListView.hideHeader();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		String postResult = (String) data.getCharSequenceExtra("postResult");
		if (postResult != null)
			Debugger.DisplayToast(this, postResult);
	}

    /** 从服务器更新列表 */
	private void updateList() {
		new AsyncTask<Void, Void, String>() {
			protected void onPreExecute() {
				mLineList = new ArrayList<View>();
			}

			protected String doInBackground(Void... params) {
				bookedNum = "0";
                // 从服务器获取用户已预约的项目数
				try {
					bookedNum = NetInfoParser.getAppointmentNum(userID);
				} catch (Exception e) {
					bookedNum = "-1";
					e.printStackTrace();
				}
				
				String result = null;
				try {
					JSONArray getResult = NetInfoParser.getAttractionList(playgroundID);
					JSONObject iter;
					for (int index = 0; index < getResult.length(); index++) {
						iter = getResult.getJSONObject(index);
						View listItemView = mInflater.inflate(
								R.layout.book_list_item, mListView, false);

						String gameID = iter.getString("gameid");
						listItemView.setContentDescription(gameID);

						((TextView) listItemView.findViewById(R.id.attrName))
								.setText(iter.getString("title"));
						((ImageView) listItemView.findViewById(R.id.attrIcon))
								.setImageBitmap(BitmapFactory.decodeFile(
										Environment
												.getExternalStorageDirectory()
												.getPath()
												+ "/lineonline/"
												+ iter.getString("photo"),
										new BitmapFactory.Options()));
                        Log.e("path", Environment.getExternalStorageDirectory().getPath());
						LinearLayout bookListContainer = ((LinearLayout) listItemView
								.findViewById(R.id.listItemDetailList));
						JSONArray bookArray = iter.getJSONArray("timeterval");
						JSONObject iiter;
						for (int i = 0; i < bookArray.length(); i++) {
							iiter = bookArray.getJSONObject(i);
							View bookNowView = mInflater.inflate(
									R.layout.book_now_list_item, null);

							String startTime = iiter.getString("starttime");
							String endTime = iiter.getString("endtime");

							if (i == 0)
								((TextView) listItemView
										.findViewById(R.id.attrClosestTime))
										.setText(startTime);

							((TextView) bookNowView
									.findViewById(R.id.timeInterval))
									.setText(startTime + "~" + endTime);

							((TextView) bookNowView.findViewById(R.id.seatLeft))
									.setText(iiter.getString("number"));

							bookNowView.findViewById(R.id.bookNowBtn)
									   .setContentDescription(gameID
                                                + "|" + iter.getString("title")
												+ "|" + userID
                                                + "|" + startTime
                                                + "~" + endTime);

							if (Integer.parseInt(iiter.getString("number")) > 0) {
								bookNowView.findViewById(R.id.bookNowBtn)
                                           .setOnTouchListener(mBookOnTouchListener);
							}

							bookListContainer.addView(bookNowView);
						}

						if (index == 0) {
							listItemView.setPadding(
								listItemView.getPaddingLeft(),
                                0,
								listItemView.getPaddingRight(),
								listItemView.getPaddingBottom()
                            );
						}

						MyOnClickListener mOnClickListener = new MyOnClickListener(listItemView);

						listItemView.findViewById(R.id.listItemArrow).setOnClickListener(mOnClickListener);

						listItemView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent mIntent = new Intent(BookActivity.this, DetailActivity.class);
								mBundle.putString("attrID", (String) v.getContentDescription());
								mIntent.putExtras(mBundle);
								startActivity(mIntent);
							}
						});

						mLineList.add(listItemView);
					}
				} catch (SocketTimeoutException e) {
					result = "Connection timeout!";
				} catch (JSONException e) {
					result = "Illegal data format!";
				} catch (Exception e) {
					result = "Unknown error occured!";
					e.printStackTrace();
				}

				return result;
			}

			protected void onPostExecute(String result) {
				if (result != null) {
					Debugger.DisplayToast(BookActivity.this, result);
				} else if (mAdapter == null) {
					mAdapter = new LvAdapter(BookActivity.this, mLineList);
					mListView.setAdapter(mAdapter);
				} else {
					mAdapter.setList(mLineList);
					mAdapter.notifyDataSetChanged();
					mListView.onRefreshComplete();
				}
				bookedView.setText(bookedNum);
			}
		}.execute(null, null, null);
	}
}