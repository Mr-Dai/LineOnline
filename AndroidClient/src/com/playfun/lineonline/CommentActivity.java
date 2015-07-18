package com.playfun.lineonline;

import java.net.SocketTimeoutException;

import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.NetInfoParser;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class CommentActivity extends Activity {
	private Bundle mBundle;
	private boolean isAnswer;
	private String answerName;
	private String answerID;
	private String userID;
	private String bubbleID;
	private EditText content;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_main);
		
		mBundle = getIntent().getExtras();
		isAnswer = mBundle.getBoolean("isAnswer");
		userID = mBundle.getString("userID");
		bubbleID = mBundle.getString("bubbleID");
		
		content = (EditText)findViewById(R.id.editcontent);
		
		if (isAnswer) {
			answerName = mBundle.getString("answerName");
			answerID = mBundle.getString("answerID");
			content.setHint("回复@" + answerName + ":...");
		}
		
		findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CommentActivity.this.finish();
			}
		});
		
		findViewById(R.id.submit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (content.getText().toString().equals("")) {
					Debugger.DisplayToast(CommentActivity.this, "回复内容不能为空");
					return;
				}
				new AsyncTask<Void, Void, String>() {
					@Override
					protected String doInBackground(Void... params) {
						String result = null;
						try {
							result = NetInfoParser.postComment(bubbleID, userID, isAnswer, answerID, content.getText().toString());
						} catch (SocketTimeoutException e) {
							Debugger.DisplayToast(CommentActivity.this, "连接超时");
						} catch (Exception e) {
							Debugger.DisplayToast(CommentActivity.this, "未知错误");
						}
						return result;
					}
					
					protected void onPostExecute(String result) {
						CommentActivity.this.finish();
					}
				}.execute(null, null, null);
			}
		});
	}
}
