package com.playfun.lineonline.widget;

import com.playfun.lineonline.R;
import com.playfun.lineonline.util.Debugger;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;

/**
 * 箭头按钮旋转OnClickListener
 */
public class MyOnClickListener implements OnClickListener {

	private RotateAnimation arrowRotate;
	private RotateAnimation arrowRotateReverse;

	private LinearLayout detailList;

	public MyOnClickListener(View v) {
		detailList = (LinearLayout) v.findViewById(R.id.listItemDetailList);
		arrowRotate = new RotateAnimation(0, 90,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		arrowRotate.setInterpolator(new LinearInterpolator());
		arrowRotate.setDuration(150);
		arrowRotate.setFillAfter(true);

		arrowRotateReverse = new RotateAnimation(90, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		arrowRotateReverse.setInterpolator(new LinearInterpolator());
		arrowRotateReverse.setDuration(150);
		arrowRotateReverse.setFillAfter(true);
	}

	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.listItemArrow:
			if (detailList.getVisibility() == View.GONE) {
				v.clearAnimation();
				v.startAnimation(arrowRotate);
				detailList.setVisibility(View.VISIBLE);
			} else {
				v.clearAnimation();
				v.startAnimation(arrowRotateReverse);
				detailList.setVisibility(View.GONE);
			}
			break;
		}
	}
}
