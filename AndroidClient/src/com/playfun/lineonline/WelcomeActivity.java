package com.playfun.lineonline;

import java.net.SocketTimeoutException;

import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.NetInfoParser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class WelcomeActivity extends Activity {
	private Bundle mBundle = new Bundle();
	private String userName;
	private String userPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_main);
		
		SharedPreferences settings = getSharedPreferences("userLogin", Activity.MODE_PRIVATE);
		userName = settings.getString("userName", "");
		userPassword = settings.getString("userPassword", "");
		
		mBundle.putString("userName", userName);
		
		if (settings.getBoolean("autoLogIn", false)) {
			new AsyncTask<Void, Void, String>() {
				private boolean isSuccess = false;
				
				@Override
				protected String doInBackground(Void... params) {
					String userID = null;
					try {
						String result = NetInfoParser.signIn(userName, userPassword);
						if (result.split(" ")[0].equals("True")) {
							isSuccess = true;
							userID = result.split(" ")[1];
						} else {
							userID = result;
						}
					} catch (SocketTimeoutException e) {
						Debugger.DisplayToast(WelcomeActivity.this, "Connection timeout!");
					}
					return userID;
				}
				
				protected void onPostExecute(String userID) {
					// 暂停3秒
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					if (isSuccess) {
						Intent mIntent = new Intent(WelcomeActivity.this, HomePage.class);
						Bundle mBundle = new Bundle();
						mBundle.putString("userID", userID);
						mBundle.putString("userName", userName);
						mIntent.putExtras(mBundle);
						startActivity(mIntent);
						Debugger.DisplayToast(WelcomeActivity.this, "登陆成功！");
						WelcomeActivity.this.finish();
					} else {
						Debugger.DisplayToast(WelcomeActivity.this, userID);
						Intent mIntent = new Intent(WelcomeActivity.this, SignUpActivity.class);
						startActivity(mIntent);
						WelcomeActivity.this.finish();
					}
				}
			}.execute(null, null, null);
		} else {
			Intent mIntent = new Intent(WelcomeActivity.this, SignUpActivity.class);
			mIntent.putExtras(mBundle);
			startActivity(mIntent);
			WelcomeActivity.this.finish();
		}
	}
}
