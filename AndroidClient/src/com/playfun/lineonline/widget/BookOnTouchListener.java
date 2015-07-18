package com.playfun.lineonline.widget;

import com.playfun.lineonline.BookDialogActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 预约按钮的OnTouchListener
 *
 * @author Robert Peng
 */
public class BookOnTouchListener implements OnTouchListener {
	
	private Activity context;
	
	public BookOnTouchListener(Activity context) {
		this.context = context;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Intent mIntent = new Intent(context, BookDialogActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putString("attrID", ((String)v.getContentDescription()).split("\\|")[0]);
			mBundle.putString("attrName", ((String)v.getContentDescription()).split("\\|")[1]);
			mBundle.putString("userID", ((String)v.getContentDescription()).split("\\|")[2]);
			mBundle.putString("timeInterval", ((String)v.getContentDescription()).split("\\|")[3]);
			mIntent.putExtras(mBundle);
			context.startActivityForResult(mIntent, 1);
		}
		return false;
	}
}
