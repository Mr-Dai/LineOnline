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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SignInActivity extends Activity {
	private TextView userNameView;
	private TextView passwordView;
	
	private String userName;
	private String password;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signin_main);
		
		userNameView = (TextView) findViewById(R.id.userName);
		passwordView = (TextView) findViewById(R.id.userPassword);
		
		// 将用户以前的账号密码设定到输入框中，方便直接登录
		SharedPreferences settings = getSharedPreferences("userLogin", Activity.MODE_PRIVATE);
		userName = settings.getString("userName", "");
		password = settings.getString("userPassword", "");
		userNameView.setText(userName);
		passwordView.setText(password);
		
		Log.e("Signin userName", userName);
		Log.e("Signin userPassword", password);
		
		findViewById(R.id.backwardArrow).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SignInActivity.this.finish();
			}
		});
		
		findViewById(R.id.signInBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				userName = userNameView.getText().toString();
				password = passwordView.getText().toString();
			
				if (userName.equals("") || password.equals("")) {
					Debugger.DisplayToast(SignInActivity.this, "上述信息不能为空");
					return;
				}
				
				new AsyncTask<Void, Void, String>() {
					private boolean isSuccess = false;
					
					@Override
					protected String doInBackground(Void... params) {
						String userID = null;
						try {
							String result = NetInfoParser.signIn(userName, password);
							if (result.split(" ")[0].equals("True")) {
								isSuccess = true;
								userID = result.split(" ")[1];
							} else {
								userID = result;
							}
						} catch (SocketTimeoutException e) {
							Debugger.DisplayToast(SignInActivity.this, "Connection timeout!");
						}
						return userID;
					}
					
					protected void onPostExecute(String userID) {
						if (isSuccess) {
							Intent resultIntent = new Intent();
							resultIntent.putExtra("userID", userID);
							resultIntent.putExtra("userName", userName);
							resultIntent.putExtra("userPassword", password);
							SignInActivity.this.setResult(RESULT_OK, resultIntent);
							SignInActivity.this.finish();
						} else {
							Debugger.DisplayToast(SignInActivity.this, userID);
						}
					}
				}.execute(null, null, null);
			}
		});
	}
}
