package com.playfun.lineonline.widget;

import com.playfun.lineonline.R;
import com.playfun.lineonline.util.Debugger;

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyListView extends ListView implements OnScrollListener {

	private final static int RELEASE_To_REFRESH = 0;// 下拉过程的状态值
	private final static int PULL_To_REFRESH = 1; // 从下拉返回到不刷新的状态值
	private final static int REFRESHING = 2;// 正在刷新的状态值
	private final static int DONE = 3;
	private final static int LOADING = 4;

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 2;
	private LayoutInflater inflater;

	// ListView头部下拉刷新的布局
	private LinearLayout headerView;
	private TextView lvHeaderTipsTv;
	private TextView lvHeaderLastUpdatedTv;
	private ImageView lvHeaderArrowIv;
	private ProgressBar lvHeaderProgressBar;

	private int refreshBoundary;

	// 定义头部下拉刷新的布局的高度
	private int headerContentHeight;
	private int headerContentPaddingTop;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private int startY;
	private int state;

	// is the arrow icon turned upside down
	private boolean isBack = false;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecorded;

	private OnRefreshListener refreshListener;

	private boolean isRefreshable;

	public MyListView(Context context) {
		super(context);
		init(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setCacheColorHint(context.getResources().getColor(
				android.R.color.transparent));
		inflater = LayoutInflater.from(context);
		headerView = (LinearLayout) inflater.inflate(R.layout.lv_header, this,
				false);
		lvHeaderTipsTv = (TextView) headerView
				.findViewById(R.id.lvHeaderTipsTv);
		lvHeaderLastUpdatedTv = (TextView) headerView
				.findViewById(R.id.lvHeaderLastUpdatedTv);

		lvHeaderArrowIv = (ImageView) headerView
				.findViewById(R.id.lvHeaderArrowIv);
		// 设置下拉刷新图标的最小高度和宽度
		lvHeaderArrowIv.setMinimumWidth(70);
		lvHeaderArrowIv.setMinimumHeight(50);

		lvHeaderProgressBar = (ProgressBar) headerView
				.findViewById(R.id.lvHeaderProgressBar);
		measureView(headerView);
		headerContentHeight = headerView.getMeasuredHeight();
		headerContentPaddingTop = headerView.getPaddingTop();
		// 将下拉刷新的布局加入ListView的顶部
		addHeaderView(headerView, null, false);
		// 设置滚动监听事件
		setOnScrollListener(this);

		// 设置旋转动画事件
		animation = new RotateAnimation(0, 180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		// 一开始的状态就是下拉刷新完的状态，所以为DONE
		state = DONE;
		// 是否正在刷新
		isRefreshable = false;

		refreshBoundary = headerContentHeight - headerContentPaddingTop;

	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem == 0) {
			isRefreshable = true;
		} else {
			isRefreshable = false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isRefreshable) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!isRecorded) {
					isRecorded = true;
					startY = (int) ev.getY();// 手指按下时记录当前位置
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onLvRefresh();
					}
				}
				isRecorded = false;
				isBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) ev.getY();
				if (!isRecorded) {
					isRecorded = true;
					startY = tempY;
				}
				if (state != REFRESHING && isRecorded && state != LOADING) {
					// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
					// 可以松手去刷新了
					if (state == RELEASE_To_REFRESH) {
						setSelection(0);
						// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((tempY - startY) / RATIO < refreshBoundary)// 由松开刷新状态转变到下拉刷新状态
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
						// 一下子推到顶了
						else if (tempY - startY <= 0) {// 由松开刷新状态转变到done状态
							state = DONE;
							changeHeaderViewByState();
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (state == PULL_To_REFRESH) {
						setSelection(0);
						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= refreshBoundary) {// 由done或者下拉刷新状态转变到松开刷新
							state = RELEASE_To_REFRESH;
							changeHeaderViewByState();
						}
						// 上推到顶了
						else if (tempY - startY <= 0) {// 由DOne或者下拉刷新状态转变到done状态
							state = DONE;
							changeHeaderViewByState();
						}
					}
					// done状态下
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}
					// 更新headView的padding
					if (state == PULL_To_REFRESH || state == RELEASE_To_REFRESH) {
						headerView.setPadding(0, headerContentPaddingTop
								+ (tempY - startY) / RATIO, 0, 0);
					}
				}
				break;
			default:
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			lvHeaderTipsTv.setText("松开刷新");
			lvHeaderArrowIv.clearAnimation();// 清除动画
			lvHeaderArrowIv.startAnimation(animation);// 开始动画效果
			isBack = true;
			break;
		case PULL_To_REFRESH:
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				lvHeaderArrowIv.clearAnimation();
				lvHeaderArrowIv.startAnimation(reverseAnimation);
				isBack = false;
			}
			lvHeaderTipsTv.setText("下拉刷新");
			break;

		case REFRESHING:
			headerView.setPadding(0, headerContentPaddingTop + refreshBoundary,
					0, 0);
			lvHeaderProgressBar.setVisibility(View.VISIBLE);
			lvHeaderArrowIv.clearAnimation();
			lvHeaderArrowIv.setVisibility(View.GONE);
			lvHeaderTipsTv.setText("正在刷新...");
			lvHeaderLastUpdatedTv.setVisibility(View.VISIBLE);
			break;
		case DONE:
			headerView.setPadding(0, headerContentPaddingTop, 0, 0);
			lvHeaderProgressBar.setVisibility(View.GONE);
			lvHeaderArrowIv.clearAnimation();
			lvHeaderArrowIv.setVisibility(View.VISIBLE);
			lvHeaderArrowIv.setImageResource(R.drawable.arrow_down);
			lvHeaderTipsTv.setText("下拉刷新");
			lvHeaderLastUpdatedTv.setVisibility(View.VISIBLE);
			break;
		}
	}

	// 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
	private void measureView(View child) {
		ViewGroup.LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0,
				params.width);
		int lpHeight = params.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public void onRefreshComplete() {
		state = DONE;
		lvHeaderLastUpdatedTv.setText("最近更新:" + new Date().toLocaleString());
		changeHeaderViewByState();
	}

	private void onLvRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	public void setAdapter(LvAdapter adapter) {
		lvHeaderLastUpdatedTv.setText("最近更新:" + new Date().toLocaleString());
		super.setAdapter(adapter);
	}

	public void setHeaderPaddingTop(int px) {
		headerView.setPadding(headerView.getPaddingLeft(), px,
				headerView.getPaddingRight(), headerView.getPaddingBottom());
		headerContentPaddingTop = px;
	}

	public void hideHeader() {
		setHeaderPaddingTop(-1 * refreshBoundary);
	}

	public int getHeaderPaddingTop() {
		return headerContentPaddingTop;
	}

	public int getHeaderHeight() {
		return headerContentHeight;
	}

}
