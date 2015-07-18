package com.playfun.lineonline;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.NetInfoParser;
import com.playfun.lineonline.widget.BaseActivity;
import com.playfun.lineonline.widget.LikeOnClickListener;
import com.playfun.lineonline.widget.LvAdapter;
import com.playfun.lineonline.widget.MyListView;
import com.playfun.lineonline.widget.MyOnClickListener;
import com.playfun.lineonline.widget.MyListView.OnRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View.OnClickListener;

/**
 * 广场页面
 *
 * @author Robert Peng
 */
public class SquareActivity extends BaseActivity {
	private LvAdapter mAdapter;
	private MyListView mListView;
	private ArrayList<View> mLineList;
	private Bundle mBundle;
	private String userID;
	private String userName;
	private String playgroundID;
	private LayoutInflater mInflater;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.square_main);
		mInflater = LayoutInflater.from(this);

		mBundle = getIntent().getExtras();
		// 包括 userID, userName, playgroundID
		
		userID = mBundle.getString("userID");
		userName = mBundle.getString("userName");
		playgroundID = mBundle.getString("playgroundID");

        // 判断用户是否是匿名登录
		if (userName.length() < 7) {
			((TextView) findViewById(R.id.userID)).setText(userName);
		} else {
			((TextView) findViewById(R.id.userID)).setText("游客" + userName.substring(0, 6));
		}

		findViewById(R.id.editComment).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent mIntent = new Intent(SquareActivity.this, BubbleActivity.class);
				mIntent.putExtras(mBundle);
				startActivityForResult(mIntent, 1);
			}
		});
		
		// 展示 我的朋友圈
		findViewById(R.id.messageInfo).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent(SquareActivity.this, MyBubbleActivity.class);
				mIntent.putExtras(mBundle);
				startActivity(mIntent);
			}
		});
		
		findViewById(R.id.foodTicket).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
				Debugger.DisplayToast(SquareActivity.this, "功能暂不可用，敬请期待！");
			}
		});
		
		mListView = (MyListView) findViewById(R.id.myListView);
		updateList();
		mListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				updateList();
			}
		});
		
		mListView.hideHeader();

		findViewById(R.id.visitlayout).bringToFront();
		findViewById(R.id.sortLayout).bringToFront();
	}

    /** 从服务器更新朋友圈内容 */
	private void updateList() {
		new AsyncTask<Void, Void, String>() {
			protected void onPreExecute() {
				mLineList = new ArrayList<View>();
			}

			@Override
			protected String doInBackground(Void... params) {
				String result = null;
				try {
					JSONArray getResult = NetInfoParser.getBubbleList(playgroundID, userID);
					JSONObject iter;
					for (int i = 0; i < getResult.length(); i++) {
						iter = getResult.getJSONObject(i);
						
						View v = mInflater.inflate(R.layout.square_item, null);
						((TextView) v.findViewById(R.id.bubbleAuthor))
							.setText(iter.getString("username"));
						
						((TextView) v.findViewById(R.id.postTime))
							.setText(iter.getString("createtime"));
						((TextView) v.findViewById(R.id.content))
							.setText(iter.getString("content"));
						if (!iter.isNull("photo")) {
							((ImageView) v.findViewById(R.id.sharePic))
							.setImageBitmap(BitmapFactory.decodeFile(
									Environment.getExternalStorageDirectory().getPath()
									+ "/lineonline/" + iter.getString("photo"),
									new BitmapFactory.Options()));
						} else {
							v.findViewById(R.id.sharePic).setVisibility(View.GONE);
						}
						((TextView) v.findViewById(R.id.likeNum))
							.setText(iter.getString("good"));
						((TextView) v.findViewById(R.id.cmmNum))
							.setText(iter.getString("commentNumber"));
						
						
						// 为点赞按钮设定监听
						View likeLayout = v.findViewById(R.id.likeLayout);
						LikeOnClickListener mOnClickListener =
								new LikeOnClickListener(SquareActivity.this,
										                likeLayout, false,
								                        userID, iter.getString("bid"));
						// 根据用户是否已点赞，动态设定点赞按钮的状态
						if (iter.getString("isLike").equals("1")) {
							((ImageView)likeLayout.findViewById(R.id.likeBtn)).setImageResource(R.drawable.like);
							mOnClickListener.setBool(true);
						}
						likeLayout.setOnClickListener(mOnClickListener);
						
						// 为评论按钮设定监听，按下后跳转至detail页
						v.findViewById(R.id.cmmLayout).setContentDescription(iter.getString("bid"));
						v.findViewById(R.id.cmmLayout).setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent mIntent = new Intent(SquareActivity.this, CmDetailActivity.class);
								mBundle.putString("bubbleID", (String) v.getContentDescription());
								mIntent.putExtras(mBundle);
								startActivity(mIntent);
							}
						});
						mLineList.add(v);
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
					Debugger.DisplayToast(SquareActivity.this, result);
				} else if (mAdapter == null) {
					mAdapter = new LvAdapter(SquareActivity.this, mLineList);
					mListView.setAdapter(mAdapter);
				} else {
					mAdapter.setList(mLineList);
					mAdapter.notifyDataSetChanged();
					mListView.onRefreshComplete();
				}
			}

		}.execute(null, null, null);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		if (requestCode == 1 && data.getBooleanExtra("succeed", false)) {
			updateList();
		}
	}
}