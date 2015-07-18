package com.playfun.lineonline;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.NetInfoParser;
import com.playfun.lineonline.widget.BookOnTouchListener;
import com.playfun.lineonline.widget.LvAdapter;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PlanActivity extends Activity {
	
	private Bundle mBundle;
	private String userID;
	private ArrayList<View> mLineList;
	private LvAdapter mAdapter;
	private ListView mListView;
	private int currentHour;
	private int currentMinu;
	private LayoutInflater mInflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan_main);
		mInflater = LayoutInflater.from(this);
	
		mBundle = getIntent().getExtras();
		userID = mBundle.getString("userID");
		findViewById(R.id.backArrow).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PlanActivity.this.finish();
			}
		});
		
		mListView = (ListView)findViewById(R.id.planResult);
		
		new AsyncTask<Void, Void, String>() {
			protected void onPreExecute() {
				mLineList = new ArrayList<View>();
				SimpleDateFormat sDateFormat = new SimpleDateFormat("HH-mm");       
				String currentTime = sDateFormat.format(new java.util.Date());
				currentHour = Integer.parseInt(currentTime.split("-")[0]);
				currentMinu = Integer.parseInt(currentTime.split("-")[1]);
			}
			
			@Override
			protected String doInBackground(Void... params) {
				String result = null;
				try {
					JSONArray getResult = NetInfoParser.planNow();
					JSONObject iter, game;
					for (int i = 0; i < getResult.length(); i++) {
						iter = getResult.getJSONObject(i);
						
						int distance = 500;
						// TODO should be set according to current position
						int minutes = distance / 100;
						
						String startTime = iter.getJSONObject("timeterval").getString("starttime");
						String endTime = iter.getJSONObject("timeterval").getString("endtime");
						String startHour = startTime.split(":")[0];
						String startMinu = startTime.split(":")[1];
						String endHour = endTime.split(":")[0];
						String endMinu = endTime.split(":")[1];
						
						String gameID = iter.getString("gid");
						
						int timeLeft = (Integer.parseInt(startHour) - currentHour) * 60
					               + Integer.parseInt(startMinu) - currentMinu;
						
						View listItemView = mInflater.inflate(R.layout.plan_item, mListView,
								false);
						
						((ImageView) listItemView.findViewById(R.id.attrIcon))
						.setImageBitmap(BitmapFactory.decodeFile(
								Environment
										.getExternalStorageDirectory()
										.getPath()
										+ "/lineonline/"
										+ iter.getString("photo"),
								new BitmapFactory.Options()));
						((TextView) listItemView.findViewById(R.id.attrName))
							.setText(iter.getString("title"));
						((TextView) listItemView.findViewById(R.id.timeInterval))
							.setText(startHour + ":" + startMinu + "~" + endHour + ":" + endMinu);
						((TextView) listItemView.findViewById(R.id.predictnum))
						.setText(String.valueOf(minutes));
						((TextView) listItemView.findViewById(R.id.distancenum))
						.setText(String.valueOf(distance));
						((TextView) listItemView.findViewById(R.id.seatLeft))
						.setText(String.valueOf(iter.getJSONObject("timeterval").getString("number")));
						((TextView) listItemView.findViewById(R.id.minuteLeft))
						.setText(String.valueOf(timeLeft));
						
						listItemView
						.findViewById(R.id.bookNow)
						.setContentDescription(
								 gameID + "|"
										+ iter.getString("title")
										+ "|" + userID + "|"
										+ startTime + "~" + endTime);
						
						listItemView.findViewById(R.id.bookNow).setOnTouchListener(new BookOnTouchListener(PlanActivity.this));
						
						if (i == 0) {
							listItemView.setPadding(listItemView.getPaddingLeft(), 0,
									listItemView.getPaddingRight(),
									listItemView.getPaddingBottom());
						}
						mLineList.add(listItemView);
					}
				} catch (JSONException e) {
					result = "Illegal data format!";
					e.printStackTrace();
				} catch (Exception e) {
					result = "Unknown error occured!";
					e.printStackTrace();
				}
				return result;
			}
			protected void onPostExecute(String result) {
				if (result != null) {
					Debugger.DisplayToast(PlanActivity.this, result);
				} else {
					mAdapter = new LvAdapter(PlanActivity.this, mLineList);
					mListView.setAdapter(mAdapter);
				}
			}
		}.execute(null, null, null);
	}
}
