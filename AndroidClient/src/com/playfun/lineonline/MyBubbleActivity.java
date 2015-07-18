package com.playfun.lineonline;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.NetInfoParser;
import com.playfun.lineonline.widget.LikeOnClickListener;
import com.playfun.lineonline.widget.LvAdapter;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MyBubbleActivity extends Activity {
	private LayoutInflater mInflater;
	private ArrayList<View> mLineList;
	private ListView mListView;
	private LvAdapter mAdapter;
	private String userID;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_center);
		mInflater = LayoutInflater.from(this);
		userID = getIntent().getExtras().getString("userID");

		((TextView) findViewById(R.id.header)).setText("我的朋友圈");
		mListView = (ListView) findViewById(R.id.myListView);
		
		findViewById(R.id.backArrow).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyBubbleActivity.this.finish();
			}
		});

		new AsyncTask<Void, Void, String>() {
			protected void onPreExecute() {
				mLineList = new ArrayList<View>();
			}
			@Override
			protected String doInBackground(Void... params) {
				String result = null;

				try {
					JSONArray getResult = NetInfoParser.getMyBubbleList(userID);
					JSONObject iter = null;
					for (int i = 0; i < getResult.length(); i++) {
						iter = getResult.getJSONObject(i);

						View v = mInflater.inflate(R.layout.square_item, null);
						((TextView) v.findViewById(R.id.bubbleAuthor)).setVisibility(View.INVISIBLE);

						((TextView) v.findViewById(R.id.postTime)).setText(iter
								.getString("createtime"));
						((TextView) v.findViewById(R.id.content)).setText(iter
								.getString("content"));
						if (!iter.isNull("photo")) {
							((ImageView) v.findViewById(R.id.sharePic))
									.setImageBitmap(BitmapFactory
											.decodeFile(
													Environment
															.getExternalStorageDirectory()
															.getPath()
															+ "/lineonline/"
															+ iter.getString("photo"),
													new BitmapFactory.Options()));
						} else {
							v.findViewById(R.id.sharePic).setVisibility(
									View.GONE);
						}
						((TextView) v.findViewById(R.id.likeNum)).setText(iter
								.getString("good"));
						((TextView) v.findViewById(R.id.cmmNum)).setText(iter
								.getString("commentnumber"));

						mLineList.add(v);
					}

				} catch (SocketTimeoutException e) {
					result = "连接超时";
					e.printStackTrace();
				} catch (JSONException e) {
					result = "非法数据";
					e.printStackTrace();
				} catch (Exception e) {
					result = "未知错误";
					e.printStackTrace();
				}
				return result;
			}

			protected void onPostExecute(String result) {
				if (result != null) {
					Debugger.DisplayToast(MyBubbleActivity.this, result);
				} else {
					mAdapter = new LvAdapter(MyBubbleActivity.this, mLineList);
					mListView.setAdapter(mAdapter);
				}
			}
		}.execute(null, null, null);

	}
}
