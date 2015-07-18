package com.playfun.lineonline.widget;

import com.playfun.lineonline.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;

public class LineListItem extends LinearLayout {
	
	private RotateAnimation arrowRotate;
	private RotateAnimation arrowRotateReverse;

	public LineListItem(Context context) {
		super(context);
		init(context);
	}
	
	public LineListItem(Context context, AttributeSet set) {
		super(context, set);
		init(context);
	}
	
	private void init(Context context) {
		
		arrowRotate = new RotateAnimation(0, 90,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		arrowRotate.setInterpolator(new LinearInterpolator());
		arrowRotate.setDuration(250);
		arrowRotate.setFillAfter(true);
	
		arrowRotateReverse = new RotateAnimation(90, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		arrowRotateReverse.setInterpolator(new LinearInterpolator());
		arrowRotateReverse.setDuration(250);
		arrowRotateReverse.setFillAfter(true);
		
		findViewById(R.id.listItemArrow).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LinearLayout detailList = (LinearLayout)findViewById(R.id.listItemDetailList);
				if(detailList.getVisibility() == View.GONE) {
					v.clearAnimation();
					v.startAnimation(arrowRotate);
					detailList.setVisibility(View.VISIBLE);
				} else {
					v.clearAnimation();
					v.startAnimation(arrowRotateReverse);
					detailList.setVisibility(View.GONE);
				}
			}
		});
		
	}

}
