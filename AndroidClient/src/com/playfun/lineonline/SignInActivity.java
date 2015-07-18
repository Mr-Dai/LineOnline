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

/**
 * 登录页面<br/>
 *
 * @author  Robert Peng
 */
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
		
		// 将用户以前的账号密码放入到输入框中，方便直接登录
		SharedPreferences settings = getSharedPreferences("userLogin", Activity.MODE_PRIVATE);
		userName = settings.getString("userName", "");
		password = settings.getString("userPassword", "");
		userNameView.setText(userName);
		passwordView.setText(password);
		
		Log.d("Signin userName", userName);
		Log.d("Signin userPassword", password);
		
		findViewById(R.id.backwardArrow).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SignInActivity.this.finish();
			}
		});

        // 登录
		findViewById(R.id.signInBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				userName = userNameView.getText().toString();
				password = passwordView.getText().toString();

                // 验证用户输入
				if (userName.equals("") || password.equals("")) {
					Debugger.DisplayToast(SignInActivity.this, "上述信息不能为空");
					return;
				}

                // 尝试登录
				new AsyncTask<Void, Void, String>() {
					private boolean isSuccess = false;
					
					@Override
					protected String doInBackground(Void... params) {
						String userID = null;
						try {
                            // 向服务器发出请求
							String result = NetInfoParser.signIn(userName, password);
                            // 处理服务器回复
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
                            // 登录成功，向注册页面返回成功的结果
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
