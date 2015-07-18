package com.playfun.lineonline;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.playfun.lineonline.util.Communicator;
import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.MD5Encoder;
import com.playfun.lineonline.util.NetInfoParser;
import com.playfun.lineonline.widget.LvAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProjectSearchActivity extends Activity implements OnClickListener {

	private LvAdapter mAdapter;
	private ListView mListView;
	private ArrayList<View> mList;
	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project_search);

		mListView = (ListView) findViewById(R.id.projectListView);
		mInflater = LayoutInflater.from(this);

		new AsyncTask<Void, Void, String>() {
			protected String doInBackground(Void... params) {
				mList = new ArrayList<View>();
				String result = null;
				try {
					JSONArray getResult = NetInfoParser
							.searchPlayground(getIntent().getExtras()
									.getString("searchField"));
					JSONObject iter;
					for (int i = 0; i < getResult.length(); i++) {
						iter = getResult.getJSONObject(i);
						View v = mInflater.inflate(R.layout.playground_item,
								null);
						v.setContentDescription(iter.getString("pgid"));
						((TextView) v.findViewById(R.id.playgroundName))
								.setText(iter.getString("name"));
						((ImageView) v.findViewById(R.id.playgroundIcon))
						.setImageBitmap(BitmapFactory.decodeFile(
								Environment
										.getExternalStorageDirectory()
										.getPath()
										+ "/lineonline/"
										+ iter.getString("icon"),
								new BitmapFactory.Options()));

						v.setOnClickListener(ProjectSearchActivity.this);
						if (i == getResult.length() - 1) {
							v.findViewById(R.id.banner).setVisibility(View.GONE);
						}
						mList.add(v);
					}
				} catch (SocketTimeoutException e) {
					result = "Connection timeout!";
				} catch (JSONException e) {
					result = "Data format illegal!";
				} catch (Exception e) {
					result = "Unknows error occured!";
					e.printStackTrace();
				}
				return result == null ? null : result.trim();
			}

			protected void onPostExecute(String result) {
				if (result != null) {
					Debugger.DisplayToast(ProjectSearchActivity.this, result);
					ProjectSearchActivity.this.finish();
				} else {
					mAdapter = new LvAdapter(ProjectSearchActivity.this, mList);
					mListView.setAdapter(mAdapter);
				}
			}
		}.execute(null, null, null);
		findViewById(R.id.backwardArrow).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						ProjectSearchActivity.this.finish();
					}
				});
	}

	@Override
	public void onClick(View v) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("playgroundID", v.getContentDescription());
		ProjectSearchActivity.this.setResult(RESULT_OK, resultIntent);
		ProjectSearchActivity.this.finish();
	}
}
