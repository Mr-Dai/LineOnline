package com.playfun.lineonline;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.NetInfoParser;
import com.playfun.lineonline.widget.BaseActivity;
import com.playfun.lineonline.widget.BookOnTouchListener;
import com.playfun.lineonline.widget.LvAdapter;
import com.playfun.lineonline.widget.MyListView;
import com.playfun.lineonline.widget.MyListView.OnRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 用于查看游玩项目所有可预约时间段的页面
 *
 * @author Robert Peng
 */
public class BookListActivity extends Activity {

	private ArrayList<View> mLineList;
	private LvAdapter mAdapter;
	private MyListView mListView;
	private LayoutInflater mInflater;
	private Bundle mBundle;
	private String attrID;
	private String userID;
	private String iconURL;
	private BookOnTouchListener mOnTouchListener = new BookOnTouchListener(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_list_activity);

		findViewById(R.id.backwardArrow).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BookListActivity.this.finish();
			}
		});
		setRate((LinearLayout) findViewById(R.id.ratingBar), 4.5f);

		mInflater = LayoutInflater.from(BookListActivity.this);
		mBundle = getIntent().getExtras();
		// 包括 userID, userName, playgroundID, attrID
		attrID = mBundle.getString("attrID");
		userID = mBundle.getString("userID");
		iconURL = mBundle.getString("iconURL");
		setImage();
		((TextView) findViewById(R.id.attrName)).setText(mBundle.getString("attrName"));

		mListView = (MyListView) findViewById(R.id.bookItemList);
		
		updateList();
	}

	void setImage() {
		((ImageView) BookListActivity.this.findViewById(R.id.attrIcon))
                .setImageBitmap(BitmapFactory.decodeFile(
                    Environment.getExternalStorageDirectory().getPath() + "/lineonline/" + iconURL,
                    new BitmapFactory.Options()
                )
        );
	}

    /** 根据评分设定星级显示图案 */
	void setRate(LinearLayout v, float rate) {
		ArrayList<ImageView> mRatingBar = new ArrayList<ImageView>();
		int i;
		for (i = 0; i < 5; i++) {
			mRatingBar.add((ImageView) v.getChildAt(i));
		}

		for (i = 0; i < rate; i++) {
			mRatingBar.get(i).setImageResource(R.drawable.full_star);
		}
		if (i != rate)
			mRatingBar.get(i - 1).setImageResource(R.drawable.half_star);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		String postResult = (String) data.getCharSequenceExtra("postResult");
		if (postResult != null)
			Debugger.DisplayToast(this, postResult);
	}

    /** 从服务器下载可预约时间段列表 */
	private void updateList() {
		new AsyncTask<Void, Void, String>() {
			protected void onPreExecute() {
				mLineList = new ArrayList<View>();
			}

			protected String doInBackground(Void... params) {
				String result = null;
				try {
					JSONObject getResult = NetInfoParser
							.showAttractionDetail(attrID);

					JSONArray bookArray = getResult.getJSONArray("timeterval");

					for (int i = 0; i < bookArray.length(); i++) {
						JSONObject iter = bookArray.getJSONObject(i);
						View listItem = mInflater.inflate(
								R.layout.book_now_list_item, null);
						((TextView) listItem.findViewById(R.id.timeInterval))
								.setText(iter.getString("starttime") + "~"
										+ iter.getString("endtime"));
						((TextView) listItem.findViewById(R.id.seatLeft))
								.setText(iter.getString("number"));
						listItem.findViewById(R.id.bookNowBtn)
								.setContentDescription(
										attrID + "|"
												+ getResult.getString("title")
												+ "|" + userID + "|"
												+ iter.getString("starttime")
												+ "~"
												+ iter.getString("endtime"));
						listItem.findViewById(R.id.bookNowBtn)
								.setOnTouchListener(mOnTouchListener);
						mLineList.add(listItem);
					}
				} catch (SocketTimeoutException e) {
					result = "Connection timeout! Please check your network connection.";
					BookListActivity.this.finish();
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
					Debugger.DisplayToast(BookListActivity.this, result);
				} else if (mAdapter == null) {
					mAdapter = new LvAdapter(BookListActivity.this, mLineList);
					mListView.setAdapter(mAdapter);

					mListView.setOnRefreshListener(new OnRefreshListener() {
						public void onRefresh() {
							updateList();
						}
					});

					mListView.hideHeader();
				} else {
					mAdapter.notifyDataSetChanged();
					mListView.onRefreshComplete();
				}
			}
		}.execute(null, null, null);
	}
}
