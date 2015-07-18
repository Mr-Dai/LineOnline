package com.playfun.lineonline.widget;

import com.playfun.lineonline.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

// 基于ScrollView的下拉刷新列表
public class SlideRefreshView extends ScrollView {
	public boolean isShowing = false;

	// ScrollView内部的LinearLayout
	private LinearLayout mWrapper;
	// 显示“下拉刷新”信息的头部
	private ViewGroup mHeader;
	// 实际列表内容
	private ListView mContent;

	private int mContentHeight;
	private int startY;

	// 单位dp
	private int mHeaderHeight = 50;

	public SlideRefreshView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mWrapper = (LinearLayout) getChildAt(0);
		mHeader = (ViewGroup) mWrapper.getChildAt(0);
		mContent = (ListView) mWrapper.getChildAt(1);

		mHeaderHeight = mHeader.getLayoutParams().height = mContentHeight;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
