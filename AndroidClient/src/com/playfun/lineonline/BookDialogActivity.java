package com.playfun.lineonline;

import java.net.SocketTimeoutException;

import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.NetInfoParser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 点击预定按钮后弹出的对话框
 *
 * @author Robert Peng
 */
public class BookDialogActivity extends Activity {
	private String attrID;
	private String attrName;
	private String userID;
	private String startTime;
	private String endTime;
	private String timeInterval;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_dialog);
		
		Bundle mBundle = getIntent().getExtras();
		
		attrID = mBundle.getString("attrID");
		attrName = mBundle.getString("attrName");
		userID = mBundle.getString("userID");
		timeInterval = mBundle.getString("timeInterval");
		startTime = timeInterval.split("~")[0];
		endTime = timeInterval.split("~")[1];

		((TextView)findViewById(R.id.bookAttrName)).setText(attrName);
		((TextView)findViewById(R.id.bookInterval)).setText(timeInterval);
	
		mBundle = null;
		
		findViewById(R.id.confirmButton).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				new AsyncTask<Void, Void, String>() {
					
					protected String doInBackground(Void... params) {
						String result = null;
						try {
							result = NetInfoParser.bookAttraction(userID, attrID, startTime, endTime);
						} catch (SocketTimeoutException e) {
							result = "Connection timeout!";
						} catch (Exception e) {
							result = "Unkown error occured!";
						}
						return result;
					}
					
					protected void onPostExecute(String result) {
						if (result.equals("true")) {
							Intent resultIntent = new Intent();
							resultIntent.putExtra("postResult", result);
							BookDialogActivity.this.setResult(RESULT_OK, resultIntent);
						} else {
							Debugger.DisplayToast(BookDialogActivity.this, result);
						}
						BookDialogActivity.this.finish();
					}
					
				}.execute(null, null, null);
			}
		});
		
		findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BookDialogActivity.this.finish();
			}
		});
	}
	
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if(keyCode == KeyEvent.KEYCODE_BACK){
			 this.finish();
			 return true;
		 }
		 return super.onKeyDown(keyCode, event);
	}
}
