package com.playfun.lineonline;

import java.net.SocketTimeoutException;

import org.json.JSONException;
import org.json.JSONObject;
import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.NetInfoParser;
import com.playfun.lineonline.widget.BaseActivity;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 注册页面<br />
 * 可跳转至登陆页面
 *
 * @author Robert Peng, Xu WeiYuan
 */
public class SignUpActivity extends BaseActivity {
    /** 用于调用二维码扫描页面的request code */
    private static final int ANONY_REQUEST_CODE = 1;
    /** 用于调用登录页面的request code */
    private static final int SIGNIN_REQUEST_CODE = 2;
	
	private TextView userNameView;
	private TextView passwordView;
	private TextView userEmailView;
	private TextView confirmView;
    public ImageView qqbtn;
    public String thirdToken;
    public String thirdId;
    public static Tencent mTencent;
	private String userName;
	private String password;
	private String userEmail;
	private String confirm;
	public boolean qqlogin = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_main);
		
		userNameView = (TextView) findViewById(R.id.userName);
		passwordView = (TextView) findViewById(R.id.userPassword);
		userEmailView = (TextView) findViewById(R.id.userEmail);
		confirmView = (TextView) findViewById(R.id.userPasswordConfirm);

		qqbtn = (ImageView)findViewById(R.id.loginqq);
		qqbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				mTencent = Tencent.createInstance("1103590489", SignUpActivity.this);	
				  onQQClickLogin();
				  qqlogin=true;
				new Thread(runnableQQ).start();
			}
		});

        // 跳转至二维码扫描页面
		findViewById(R.id.logInAnony).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(SignUpActivity.this, DecoderActivity.class), ANONY_REQUEST_CODE);
			}
		});

        // 跳转至登陆页面
		findViewById(R.id.signInBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(SignUpActivity.this, SignInActivity.class), SIGNIN_REQUEST_CODE);
			}
		});

        // 注册
		findViewById(R.id.signUpBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				userName = userNameView.getText().toString();
				password = passwordView.getText().toString();
				userEmail = userEmailView.getText().toString();
				confirm = confirmView.getText().toString();

                // 检测用户输入是否合法
				if (userName.equals("") || password.equals("") || userEmail.equals("") || confirm.equals("")) {
					Debugger.DisplayToast(SignUpActivity.this, "上述信息不能为空");
					return;
				}
				if (!password.equals(confirm)) {
					Debugger.DisplayToast(SignUpActivity.this, "输入密码不匹配");
					return;
				}

                // 尝试注册
				new AsyncTask<Void, Void, String>() {
					private boolean isSuccess = false;
					
					@Override
					protected String doInBackground(Void... params) {
						String userID = null;
						try {
                            // 向服务器发送请求
							String result = NetInfoParser.signUp(userName, password, userEmail);
                            // 处理服务器回复
							if (result.split(" ")[0].equals("True")) {
								isSuccess = true;
								userID = result.split(" ")[1];
							} else {
								userID = result;
							}
						} catch (SocketTimeoutException e) {
							Debugger.DisplayToast(SignUpActivity.this, "Connection timeout!");
						}
						return userID;
					}
					
					protected void onPostExecute(String userID) {
						if (isSuccess) {
                            // 注册成功，跳转至主页
							Bundle mBundle = new Bundle();
							mBundle.putString("userID", userID);
							mBundle.putString("userName", userName);
							Intent mIntent = new Intent(SignUpActivity.this, HomePage.class);
							mIntent.putExtras(mBundle);
							startActivity(mIntent);
							Debugger.DisplayToast(SignUpActivity.this, "注册成功");
							SharedPreferences settings = getSharedPreferences("userLogin", Activity.MODE_PRIVATE);
							SharedPreferences.Editor editor = settings.edit();
							editor.putString("userName", userName);
							editor.putString("userPassword", password);
							editor.putBoolean("autoLogIn", true);
							editor.commit();
							SignUpActivity.this.finish();
						} else {
							Debugger.DisplayToast(SignUpActivity.this, userID);
						}
					}
				}.execute(null, null, null);
			}
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

		if(qqlogin == true){
		    mTencent.onActivityResult(requestCode, resultCode, data);
		    return;
		}

		switch(requestCode) {
		case ANONY_REQUEST_CODE:
            // TODO pass
			break;
		case SIGNIN_REQUEST_CODE:
            // 登录成功，跳转至主页
			Bundle mBundle = new Bundle();
			mBundle.putString("userID", (String) data.getCharSequenceExtra("userID"));
			mBundle.putString("userName", (String) data.getCharSequenceExtra("userName"));
			Intent mIntent = new Intent(SignUpActivity.this, HomePage.class);
			mIntent.putExtras(mBundle);
			Debugger.DisplayToast(SignUpActivity.this, "登陆成功");
			startActivity(mIntent);
			SharedPreferences settings = getSharedPreferences("userLogin", Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("userName", (String) data.getCharSequenceExtra("userName"));
			editor.putString("userPassword", (String) data.getCharSequenceExtra("userPassword"));
			editor.putBoolean("autoLogIn", true);
			editor.commit();
			SignUpActivity.this.finish();
			break;
		}
	}


    /**
     * @author Xu WeiYuan
     */
	final Handler handlerQQ = new Handler(){
		  public void handleMessage(Message msg){
			 //Log.e("userinfo2",userinformation.toString());
				new AsyncTask<Void, Void, String>() {
					
					private boolean isSuccess = false;
					
					@Override
					protected String doInBackground(Void... params) {
						String userID = null;
						try {
							String result = NetInfoParser.signUp("QQlogin", "123456", "xxxxxxxx@qq.com");
							if (result.split(" ")[0].equals("True")) {
								isSuccess = true;
								userID = result.split(" ")[1];
							} else {
								userID = result;
							}
						} catch (SocketTimeoutException e) {
							Debugger.DisplayToast(SignUpActivity.this, "Connection timeout!");
						}
						return userID;
					}
					
					protected void onPostExecute(String userID) {
						if (isSuccess) {
							Bundle mBundle = new Bundle();
							mBundle.putString("userID", userID);
							mBundle.putString("userName", userName);
							Intent mIntent = new Intent(SignUpActivity.this, HomePage.class);
							mIntent.putExtras(mBundle);
							startActivity(mIntent);
							Debugger.DisplayToast(SignUpActivity.this, "注册成功");
							SharedPreferences settings = getSharedPreferences("userLogin", Activity.MODE_PRIVATE);
							SharedPreferences.Editor editor = settings.edit();
							editor.putString("userName", userName);
							editor.putString("userPassword", password);
							editor.putBoolean("autoLogIn", true);
							editor.commit();
							SignUpActivity.this.finish();
						} else {
							Debugger.DisplayToast(SignUpActivity.this, userID);
						}
					}
				}.execute(null, null, null);
			  startActivityForResult(new Intent(SignUpActivity.this,HomePage.class),2);
		  }
	  };
	final Runnable runnableQQ = new Runnable(){
		
		  public void run(){
	   			 while(TextUtils.isEmpty(thirdToken)||TextUtils.isEmpty(thirdId)){
	  			   thirdToken = mTencent.getAccessToken();
	  			   thirdId = mTencent.getOpenId(); 
	  		   }
	   			
	   		    Bundle data = new Bundle();
	   			Message msg = new Message();
	   			data.putString("value", "sddf");
	   			msg.setData(data);
	   			handlerQQ.sendMessage(msg); 
		     }  
	  
};
	 private void onQQClickLogin() {
			// 检测实例是否已经登录
			if (!mTencent.isSessionValid()) {
				// 如果没有登录的话，执行登录操作
				mTencent.login(this, "All", loginListener);
				Toast.makeText(this, "正在登陆...", Toast.LENGTH_SHORT).show();
			} else {
				// 登录的话就退出，并且更新用户信息和按钮
				mTencent.logout(this);
			
			}
			
		 }
	  private void initOpenidAndToken(JSONObject jsonObject) {
			try {
				String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
				String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
				String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
				if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
						&& !TextUtils.isEmpty(openId)) {
					mTencent.setAccessToken(token, expires);
					mTencent.setOpenId(openId);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	  IUiListener loginListener = new BaseUiListener() {
			
			protected void doComplete(JSONObject values) {
				Log.d("SDKQQAgentPref",
						"AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
				// 初始化openid和token
				initOpenidAndToken(values);
				
			}
		};
		private class BaseUiListener implements IUiListener {

			@Override
			public void onCancel() {
				Util.toastMessage(SignUpActivity.this, "onCancel: ");
				Util.dismissDialog();
			}
			private Context mContext;  
	        private String mScope;  
	  
	        public BaseUiListener() {  
	        }  
	  
	        public BaseUiListener(Context mContext) {  
	            super();  
	            this.mContext = mContext;  
	        }  
	  
	        public BaseUiListener(Context mContext, String mScope) {  
	            super();  
	            this.mContext = mContext;  
	            this.mScope = mScope;  
	        }  
			@Override
			public void onComplete(Object arg0) {
				//Util.showResultDialog(login.this, arg0.toString(), "登录成功");
			    //Log.e("userinfo",arg0.toString());
			    //userinformation = (JSONObject)arg0;
				doComplete((JSONObject) arg0);
			}

			protected void doComplete(JSONObject values) {
			
			   

	          //Log.e("JSONObject",values.toString());
			}

			@Override
			public void onError(UiError arg0) {
				Util.toastMessage(SignUpActivity.this, "onError: " + arg0.errorDetail);
				Util.dismissDialog();
			}

		}
}
