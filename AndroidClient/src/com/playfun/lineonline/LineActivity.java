package com.playfun.lineonline;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.content.Context;
import android.content.Intent;

public class LineActivity extends Activity implements OnTabChangeListener {
	LocalActivityManager manager = null;
	Context context = null;
	Bundle mBundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.line_main);
		
		LayoutInflater inflater = LayoutInflater.from(this);
		
		mBundle = getIntent().getExtras();
		// includes userID, playgroundID
		
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);
		TabHost tabhost = (TabHost) findViewById(android.R.id.tabhost);
		tabhost.setup();
		tabhost.setup(manager);
		RelativeLayout tabIndicator1 = (RelativeLayout) inflater.inflate(
				R.layout.tabwidget_for_line, null);
		TextView tvTab1 = (TextView) tabIndicator1.findViewById(R.id.tv_title);
		tvTab1.setText("正在排");

		RelativeLayout tabIndicator2 = (RelativeLayout) inflater.inflate(
				R.layout.tabwidget_for_line, null);
		TextView tvTab2 = (TextView) tabIndicator2.findViewById(R.id.tv_title);
		tvTab2.setText("已经玩");
		
		Intent waitingIntent = new Intent(LineActivity.this, WaitingActivity.class);
		waitingIntent.putExtras(mBundle);
		tabhost.addTab(tabhost.newTabSpec("1").setIndicator(tabIndicator1)
				.setContent(waitingIntent));
		
		Intent playedIntent = new Intent(LineActivity.this, RateActivity.class);
		playedIntent.putExtras(mBundle);
		tabhost.addTab(tabhost.newTabSpec("2").setIndicator(tabIndicator2)
				.setContent(playedIntent));
	}

	public void onAttach(Activity activity) {}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		
	}

}
