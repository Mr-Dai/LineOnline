package com.playfun.lineonline;

import com.playfun.lineonline.widget.BaseActivity;
import com.playfun.lineonline.widget.SlidingMenu;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements OnTabChangeListener {
	LocalActivityManager manager = null;
	private TabHost mTabHost;
	private LayoutInflater inflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_frame);
        findViewById(R.id.planBtn).setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Intent mIntent = new Intent(MainActivity.this, PlanActivity.class);
        		mIntent.putExtras(MainActivity.this.getIntent().getExtras());
        		startActivity(mIntent);
        	}
        });
		Bundle mBundle = getIntent().getExtras();
		// includes userID, playgroundID
		
		// Initialization
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mTabHost.setup(manager);
		inflater = LayoutInflater.from(this);
		
		// 点击遮罩层时，隐藏侧滑菜单
		findViewById(R.id.mask).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SlidingMenu.getInstance().hide();
			}
		});
		
		RelativeLayout tabIndicator1 = (RelativeLayout) inflater.inflate(
				R.layout.tabwidget_for_main, null);
		TextView tvTab1 = (TextView) tabIndicator1.findViewById(R.id.tv_title);
		ImageView ivTab1 = (ImageView) tabIndicator1.findViewById(R.id.iv_mark);
		tvTab1.setText("预约");
		ivTab1.setImageResource(R.drawable.book_navigator_icon);
		RelativeLayout tabIndicator2 = (RelativeLayout) inflater.inflate(
				R.layout.tabwidget_for_main, null);
		TextView tvTab2 = (TextView) tabIndicator2.findViewById(R.id.tv_title);
		ImageView ivTab2 = (ImageView) tabIndicator2.findViewById(R.id.iv_mark);
		tvTab2.setText("广场");
		ivTab2.setImageResource(R.drawable.square_navigator_icon);
		RelativeLayout tabIndicator3 = (RelativeLayout) inflater.inflate(
				R.layout.tabwidget_for_main, null);
		ImageView ivTab3 = (ImageView) tabIndicator3.findViewById(R.id.iv_mark);
		TextView tvTab3 = (TextView) tabIndicator3.findViewById(R.id.tv_title);
		tvTab3.setText("排队");
		ivTab3.setImageResource(R.drawable.line_navigator_icon);
		
		Intent bookIntent = new Intent(this, BookActivity.class);
		bookIntent.putExtras(new Bundle(mBundle));
		mTabHost.addTab(mTabHost.newTabSpec("1").setIndicator(tabIndicator1)
				.setContent(bookIntent));
		
		Intent squareIntent = new Intent(this, SquareActivity.class);
		squareIntent.putExtras(new Bundle(mBundle));
		mTabHost.addTab(mTabHost.newTabSpec("2").setIndicator(tabIndicator2)
				.setContent(squareIntent));
		
		Intent lineIntent = new Intent(this, LineActivity.class);
		lineIntent.putExtras(new Bundle(mBundle));
		mTabHost.addTab(mTabHost.newTabSpec("3").setIndicator(tabIndicator3)
				.setContent(lineIntent));
		
		mTabHost.setOnTabChangedListener(this);
		
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
		updateTab();
	}

	@Override
	public void onTabChanged(String tabId) {
		mTabHost.setCurrentTabByTag(tabId);
		updateTab();
	}
	
	private void updateTab() {
		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
			TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(R.id.tv_title);
			if (mTabHost.getCurrentTab() == i)
				tv.setTextColor(Color.WHITE);
			else
				tv.setTextColor(Color.parseColor(getResources().getString(R.color.text_deep_gray)));
		}
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e("", "MainActivity.onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK && SlidingMenu.getInstance().isShowing == true ) {
    		Log.e("", "isShowing");
        	SlidingMenu.getInstance().hide();
        	return true;
        } else
        	return super.onKeyDown(keyCode, event);
    }
}
