package com.playfun.lineonline;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.NetInfoParser;
import com.playfun.lineonline.widget.MyListView;
import com.playfun.lineonline.widget.LvAdapter;
import com.playfun.lineonline.widget.MyListView.OnRefreshListener;

public class RateActivity extends Activity implements OnTouchListener {
	private ArrayList<View> mLineList;
	private LvAdapter mAdapter;
	private MyListView mListView;
	private LayoutInflater mInflater;
	private String userID;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mInflater = LayoutInflater.from(this);
		setContentView(R.layout.rate_main);
		mListView = (MyListView) findViewById(R.id.myListView);
		userID = getIntent().getExtras().getString("userID");
		
		updateList();

		mListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				updateList();
			}
		});

		mListView.hideHeader();
	}

	void setRate(LinearLayout v, float rate) {
		ArrayList<ImageView> mRatingBar = new ArrayList<ImageView>();
		int i;
		for (i = 0; i < 5; i++) {
			mRatingBar.add((ImageView) v.getChildAt(i));
		}

		for (i = 0; i < rate && i < 5; i++) {
			mRatingBar.get(i).setImageResource(R.drawable.full_star);
		}
		if (i != rate)
			mRatingBar.get(i - 1).setImageResource(R.drawable.half_star);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Bundle mBundle = new Bundle();
			mBundle.putString("userID",
					((String) v.getContentDescription()).split("\\|")[0]);
			mBundle.putString("attrID",
					((String) v.getContentDescription()).split("\\|")[1]);
			mBundle.putString("attrName",
					((String) v.getContentDescription()).split("\\|")[2]);
			Intent mIntent = new Intent(getApplicationContext(),
					RatingActivity.class);
			mIntent.putExtras(mBundle);
			startActivityForResult(mIntent, 1);
			return true;
		}
		return false;
	}

	void updateList() {
		new AsyncTask<Void, Void, String>() {
			
			protected void onPreExecute() {
				mLineList = new ArrayList<View>();
			}
			
			protected String doInBackground(Void... params) {
				String result = null;
				
				try {
					JSONArray getResult = NetInfoParser.listInrateAttraction(userID);
					JSONObject iter, game;
					for (int i = 0; i < getResult.length(); i++) {
						iter = getResult.getJSONObject(i);
						game = iter.getJSONObject("game");
						View listItemView = mInflater.inflate(R.layout.played_item,
								mListView, false);
					
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
						
						setRate((LinearLayout) listItemView.findViewById(R.id.ratingBar),
								(float) game.getDouble("rank"));
						listItemView.findViewById(R.id.estimate).setContentDescription(
								userID + "|" + iter.getString("gid") + "|" + game.getString("title"));
						listItemView.findViewById(R.id.estimate).setOnTouchListener(RateActivity.this);
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
					Debugger.DisplayToast(RateActivity.this, result);
				} else if (mAdapter == null) {
					mAdapter = new LvAdapter(RateActivity.this, mLineList);
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
		String postResult = (String) data.getCharSequenceExtra("postResult");
		if (postResult != null)
			Debugger.DisplayToast(this, postResult);
	}
}
