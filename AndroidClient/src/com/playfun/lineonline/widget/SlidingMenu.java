package com.playfun.lineonline.widget;

import com.playfun.lineonline.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Interpolator;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SlidingMenu extends HorizontalScrollView {
	public boolean isShowing = false;

	private LinearLayout mWrapper;
	private ViewGroup mMenu;
	private ViewGroup mContent;
	
	private View mask;
	
	private AlphaAnimation maskShow;
	private AlphaAnimation maskHide;

	private int mScreenWidth;
	private int mMenuWidth;
	private int startX;

	private static SlidingMenu mInstance = null;

	// 单位dp
	private int mMenuRightPadding = 50;

	private boolean once = false;

	/**
	 * 未使用自定义属性时，调用
	 * 
	 * @param context
	 * @param attrs
	 */
	public SlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		mInstance = this;
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);

		mScreenWidth = outMetrics.widthPixels;

		// 把dp转换为px
		mMenuRightPadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources()
						.getDisplayMetrics());

		setHorizontalScrollBarEnabled(false);
		
		// 设定遮罩层出现与消失的动画
		Interpolator a = new AccelerateDecelerateInterpolator();
		Interpolator b = new AccelerateInterpolator();
		maskShow = new AlphaAnimation(0, 0.8f);
		maskShow.setDuration(100);
		maskShow.setInterpolator(a);
		maskHide = new AlphaAnimation(0.8f, 0);
		maskHide.setDuration(100);
		maskHide.setInterpolator(b);
	}

	public static SlidingMenu getInstance() {
		return mInstance;
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!once) {
			mWrapper = (LinearLayout) getChildAt(0);
			mMenu = (ViewGroup) mWrapper.getChildAt(0);
			mContent = (ViewGroup) mWrapper.getChildAt(1);

			mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth
					- mMenuRightPadding;
			mContent.getLayoutParams().width = mScreenWidth;
			once = true;
			
			// 初始化遮罩层
			mask = mContent.findViewById(R.id.mask);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 通过设置偏移量，将menu隐藏
	 */
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			this.scrollTo(mMenuWidth, 0);
		}
	}

	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_UP:
			// 隐藏在左边的宽度
			int scrollX = getScrollX();
			if (isShowing && scrollX > mMenuWidth / 4) {
				hide();
			} else if (!isShowing && scrollX > mMenuWidth / 2) {
				this.smoothScrollTo(mMenuWidth, 0);	// 此处不使用hide()以避免触发遮罩层动画
			} else {
				show();
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	public void show() {
		this.smoothScrollTo(0, 0);
		isShowing = true;
		if (mask != null) {
			mask.setVisibility(View.VISIBLE);
			mask.startAnimation(maskShow);
		}
	}

	public void hide() {
		this.smoothScrollTo(mMenuWidth, 0);
		isShowing = false;
		if (mask != null) {
			mask.startAnimation(maskHide);
			mask.setVisibility(View.GONE);
		}
	}

}
