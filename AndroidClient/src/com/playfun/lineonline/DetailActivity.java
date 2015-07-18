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

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 游玩项目介绍页面<br/>
 * 包含一个底部预约菜单
 *
 * @author Robert Peng
 */
public class DetailActivity extends BaseActivity {

	private int bookNowListSize;
	private ArrayList<String> detailInfo;
	private ArrayList<View> mLineList;
	private ListView bookNowItemList;
	private LayoutInflater mInflater;
	private int bookNowListHeight;
	private boolean isShowing = false;
	private ImageView layerMask;
	private ScaleAnimation listShow;
	private ScaleAnimation listHide;
	private AlphaAnimation maskShow;
	private AlphaAnimation maskHide;
	private TextView bookListEmergeBtn;
	private Bundle mBundle;
	private BookOnTouchListener mBookOnTouchListener = new BookOnTouchListener(
			this);
	private String attrID;
	private String userID;
	private String iconURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_main);
		init(savedInstanceState);
	}

	private void init(Bundle savedInstanceState) {
		getValue();
	}

	private void getValue() {
		mBundle = getIntent().getExtras();
		// 包括 userID, userName, playgroundID, attrID

        // 从服务器获取游玩项目详情
		new AsyncTask<Void, Void, String>() {
			protected void onPreExecute() {
				mInflater = LayoutInflater.from(DetailActivity.this);
				attrID = mBundle.getString("attrID");
				userID = mBundle.getString("userID");
				detailInfo = new ArrayList<String>();
				mLineList = new ArrayList<View>();
			}

			@Override
			protected String doInBackground(Void... params) {
				String result = null;
				try {
					JSONObject detailResult = NetInfoParser.showAttractionDetail(attrID);

					detailInfo.add(detailResult.getString("title"));
					detailInfo.add(detailResult.getString("introduction"));
					detailInfo.add(detailResult.getString("caution"));

					iconURL = detailResult.getString("photo");

					JSONArray bookArray = detailResult.getJSONArray("timeterval");
					bookNowListSize = bookArray.length() > 3 ? 3 : bookArray.length();

					for (int i = 0; i < 3 && i < bookArray.length(); i++) {
						JSONObject iter = bookArray.getJSONObject(i);
						View listItem = mInflater.inflate(
                            R.layout.book_now_list_item,
                            bookNowItemList,
						    false
                        );
						((TextView) listItem.findViewById(R.id.timeInterval))
								.setText(iter.getString("starttime") + "~" + iter.getString("endtime"));
						((TextView) listItem.findViewById(R.id.seatLeft)).setText(iter.getString("number"));
						listItem.findViewById(R.id.bookNowBtn)
								.setContentDescription(attrID
												+ "|" + detailResult.getString("title")
												+ "|" + userID
                                                + "|" + iter.getString("starttime")
												+ "~" + iter.getString("endtime"));
						listItem.findViewById(R.id.bookNowBtn).setOnTouchListener(mBookOnTouchListener);
						mLineList.add(listItem);
					}
				} catch (SocketTimeoutException e) {
					result = "Connection timeout! Please check your network connection.";
					DetailActivity.this.finish();
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
					Debugger.DisplayToast(DetailActivity.this, result);
				} else {
					setValue();
				}
			}

		}.execute(null, null, null);

        // 设定底部菜单动画
		Interpolator a = new AccelerateDecelerateInterpolator();
		Interpolator b = new AccelerateInterpolator();
		listShow = new ScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF,
				0, Animation.RELATIVE_TO_SELF, 1);
		listShow.setDuration(200);
		listShow.setInterpolator(a);
		listShow.setFillAfter(true);
		maskShow = new AlphaAnimation(0, 0.8f);
		maskShow.setDuration(200);
		maskShow.setInterpolator(a);
		listHide = new ScaleAnimation(1, 1, 1, 0, Animation.RELATIVE_TO_SELF,
				0, Animation.RELATIVE_TO_SELF, 1);
		listHide.setDuration(200);
		listHide.setInterpolator(b);
		listHide.setFillAfter(true);
		maskHide = new AlphaAnimation(0.8f, 0);
		maskHide.setDuration(200);
		maskHide.setInterpolator(b);
	}

	private void setValue() {
		((TextView) DetailActivity.this.findViewById(R.id.attrName))
				.setText(detailInfo.get(0));
		((TextView) DetailActivity.this.findViewById(R.id.attrDetail))
				.setText(detailInfo.get(1));
		if (!detailInfo.get(2).equals("") && detailInfo.get(2) != null) {
			((TextView) DetailActivity.this.findViewById(R.id.cautionInfo))
					.setText(detailInfo.get(2));
		} else {
			DetailActivity.this.findViewById(R.id.cautionBar).setVisibility(
					View.GONE);
		}
		((ImageView) DetailActivity.this.findViewById(R.id.attrIcon))
				.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()
								.getPath() + "/lineonline/" + iconURL,
								new BitmapFactory.Options()));

		bookNowItemList = (ListView) findViewById(R.id.bookNowList);
		View loadMoreItem = mInflater.inflate(R.layout.load_more_list_item,
				bookNowItemList, false);

        // 查看更多可预约时间段
		loadMoreItem.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Intent mIntent = new Intent(getApplicationContext(), BookListActivity.class);

					mBundle.putString("attrName",
                            (String) ((TextView) findViewById(R.id.attrName)).getText());
					mBundle.putString("iconURL", iconURL);
					mIntent.putExtras(mBundle);

					startActivity(mIntent);
				}
				return false;
			}
		});

		mLineList.add(loadMoreItem);

		bookListEmergeBtn = (TextView) findViewById(R.id.bookNowEmergeBtn);
		bookListEmergeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isShowing)
					showBookNow();
				else
					dismiss();
			}
		});

		bookNowItemList.setAdapter(new LvAdapter(this, mLineList));
		measureView(bookNowItemList);
		measureView(loadMoreItem);
		bookNowListHeight = bookNowItemList.getMeasuredHeight()
				* bookNowListSize + loadMoreItem.getMeasuredHeight();
		bookNowItemList.setPadding(0, 0, 0, -1 * bookNowListHeight);
		layerMask = (ImageView) findViewById(R.id.layerMask);

		layerMask.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		findViewById(R.id.backArrow).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DetailActivity.this.finish();
			}
		});
	}

    /** 显示底部菜单 */
	private void showBookNow() {
		if (!isShowing) {
			layerMask.setVisibility(View.VISIBLE);
			layerMask.startAnimation(maskShow);
			isShowing = true;
			bookNowItemList.setPadding(0, 0, 0, 0);
		}
	}

    /** 隐藏底部菜单 */
	private void dismiss() {
		if (isShowing) {
			bookNowItemList.setPadding(0, 0, 0, -1 * bookNowListHeight);
			layerMask.startAnimation(maskHide);
			layerMask.setVisibility(View.GONE);
			isShowing = false;
		}
	}

	public boolean onPressBack() {
		if (isShowing) {
			dismiss();
			return true;
		} else
			return false;

	}

	private void measureView(View child) {
		ViewGroup.LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
            );
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, params.width);
		int lpHeight = params.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!onPressBack())
				return super.onKeyDown(keyCode, event);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
