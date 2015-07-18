package com.playfun.lineonline;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.NetInfoParser;
import com.playfun.lineonline.widget.LvAdapter;
import com.playfun.lineonline.widget.MyListView;
import com.playfun.lineonline.widget.MyListView.OnRefreshListener;

import android.view.View.OnClickListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 正在排 页面
 *
 * @author Robert Peng
 */
public class WaitingActivity extends Activity {

	private ArrayList<View> mLineList;
	private LvAdapter mAdapter;
	private MyListView mListView;
	private Bundle mBundle;
	private String userID;
	private LayoutInflater mInflater;
	private int currentHour;
	private int currentMinu;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	public void init() {
		setContentView(R.layout.waiting);
		mInflater = LayoutInflater.from(this);
		mListView = (MyListView) findViewById(R.id.myListView);
		mBundle = getIntent().getExtras();
		userID = mBundle.getString("userID");
		
		updateList();
		
		mListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				updateList();
			}
		});

		mListView.hideHeader();
	}
	
	private void updateList() {
		new AsyncTask<Void, Void, String>() {
			
			protected void onPreExecute() {
				mLineList = new ArrayList<View>();
				SimpleDateFormat sDateFormat = new SimpleDateFormat("HH-mm");       
				String currentTime = sDateFormat.format(new java.util.Date());
				currentHour = Integer.parseInt(currentTime.split("-")[0]);
				currentMinu = Integer.parseInt(currentTime.split("-")[1]);
			}
			
			protected String doInBackground(Void... params) {
				String result = null;
				try {
					JSONArray getResult = NetInfoParser.showWaitingProject(userID);
					JSONObject iter, game;
					for (int i = 0; i < getResult.length(); i++) {
						iter = getResult.getJSONObject(i);
						game = iter.getJSONObject("game");
						
						int distance = 500;
						// TODO should be set according to current position
						int minutes = distance / 100;
						
						String startTime = iter.getString("starttime");
						String endTime = iter.getString("endtime");
						String startHour = startTime.split(" ")[1].split(":")[0];
						String startMinu = startTime.split(" ")[1].split(":")[1];
						String endHour = endTime.split(" ")[1].split(":")[0];
						String endMinu = endTime.split(" ")[1].split(":")[1];
						
						int timeLeft = (Integer.parseInt(startHour) - currentHour) * 60
					               + Integer.parseInt(startMinu) - currentMinu;
						
						if (timeLeft < 0)
							continue;
						
						View listItemView = mInflater.inflate(R.layout.waiting_item, mListView,
								false);
						
						((ImageView) listItemView.findViewById(R.id.attrIcon))
						.setImageBitmap(BitmapFactory.decodeFile(
								Environment
										.getExternalStorageDirectory()
										.getPath()
										+ "/lineonline/"
										+ game.getString("photo"),
								new BitmapFactory.Options()));
						((TextView) listItemView.findViewById(R.id.attrName))
							.setText(game.getString("title"));
						((TextView) listItemView.findViewById(R.id.timeInterval))
							.setText(startHour + ":" + startMinu + "~" + endHour + ":" + endMinu);
						((TextView) listItemView.findViewById(R.id.bookID))
							.setText(iter.getString("aid"));
						((TextView) listItemView.findViewById(R.id.predictnum))
						.setText(String.valueOf(minutes));
						((TextView) listItemView.findViewById(R.id.distancenum))
						.setText(String.valueOf(distance));
						((TextView) listItemView.findViewById(R.id.minuteLeft))
						.setText(String.valueOf(timeLeft));
						

						final String str = game.getString("gid");
						((Button) listItemView.findViewById(R.id.go)).setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent mIntent = new Intent(WaitingActivity.this, MapActivity.class);
								Bundle mBundle = new Bundle();
								mBundle.putString("gameID", str);
								mIntent.putExtras(mBundle);
								startActivity(mIntent);
							}
						});
						
						if (i == 0) {
							listItemView.setPadding(listItemView.getPaddingLeft(), 0,
									listItemView.getPaddingRight(),
									listItemView.getPaddingBottom());
						}
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
					Debugger.DisplayToast(WaitingActivity.this, result);
				} else if (mAdapter == null) {
					mAdapter = new LvAdapter(WaitingActivity.this, mLineList);
					mListView.setAdapter(mAdapter);
				} else {
					mAdapter.setList(mLineList);
					mAdapter.notifyDataSetChanged();
					mListView.onRefreshComplete();
				}
			}
		}.execute(null, null, null);
	}
}
