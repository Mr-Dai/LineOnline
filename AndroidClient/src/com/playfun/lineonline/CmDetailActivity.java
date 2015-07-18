package com.playfun.lineonline;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.NetInfoParser;
import com.playfun.lineonline.widget.BaseActivity;
import com.playfun.lineonline.widget.LikeOnClickListener;
import com.playfun.lineonline.widget.LvAdapter;

/**
 * 朋友圈评论页面
 *
 * @author Robert Peng
 */
public class CmDetailActivity extends BaseActivity {
	private ListView mListView;
	private Bundle mBundle;
	private LvAdapter mAdapter;
	private ArrayList<View> mLineList;
	private Button commitBtn;
	private EditText inputField;
	private LinearLayout inputLayout;
	private View mask;
	private String bubbleID;
	private String userID;
	private LayoutInflater mInflater;
	private JSONObject getResult;

	private class CommentListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			mask.setVisibility(View.VISIBLE);
			inputField.requestFocus();
			inputLayout.setVisibility(View.VISIBLE);
			inputField.setHint("回复:");
			inputField.setContentDescription("0"); // replyUserID = 0即回复楼主
			// 显示键盘
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.showSoftInput(inputField, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	private class ReplyListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mask.setVisibility(View.VISIBLE);
				String userInfo = String.valueOf(v.getContentDescription());
				inputLayout.setVisibility(View.VISIBLE);
				inputField.requestFocus();
				inputField.setHint("回复@" + userInfo.split("\\|")[1] + ":");
				inputField.setContentDescription(userInfo.split("\\|")[0]);
				// 显示键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.showSoftInput(inputField, InputMethodManager.SHOW_IMPLICIT);
				return true;
			}
			return false;
		}
	}

	private class CommitListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			mask.setVisibility(View.GONE);
			inputLayout.setVisibility(View.GONE);
			// 关闭键盘
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.hideSoftInputFromWindow(getWindow().peekDecorView().getWindowToken(), 0);
			new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... params) {
					String result = null;
					try {
						result = NetInfoParser.postBubbleComment(bubbleID,
								userID,
								(String) inputField.getContentDescription(),
								inputField.getText().toString());
					} catch (SocketTimeoutException e) {
						result = "Connection timeout!";
					} catch (Exception e) {
						result = "Unknown error occured!";
						e.printStackTrace();
					}
					return result;
				}

				protected void onPostExecute(String result) {
					if (!result.substring(0, 4).equals("true")) {
						Debugger.DisplayToast(CmDetailActivity.this, result);
					} else {
						Debugger.DisplayToast(CmDetailActivity.this, "回复成功！");
						updateList();
					}
					inputField.setText("");
				}
			}.execute(null, null, null);
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_info);
		mInflater = LayoutInflater.from(this);

		// 从Bundle中获取信息
		mBundle = getIntent().getExtras();
		bubbleID = mBundle.getString("bubbleID");
		userID = mBundle.getString("userID");

		// 初始化组件
		mListView = (ListView) findViewById(R.id.commentList);
		inputField = (EditText) findViewById(R.id.inputField);
		commitBtn = (Button) findViewById(R.id.commitBtn);
		inputLayout = (LinearLayout) findViewById(R.id.inputLayout);
		mask = findViewById(R.id.mask);

		// 设定返回箭头监听
		findViewById(R.id.backArrow).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CmDetailActivity.this.finish();
			}
		});

		// 设定回复按钮监听
		findViewById(R.id.cmmLayout).setOnClickListener(new CommentListener());

		// 设定提交按钮监听
		commitBtn.setOnClickListener(new CommitListener());

		// 设定遮罩层监听
		mask.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				inputLayout.setVisibility(View.GONE);
				inputField.setText("");
				mask.setVisibility(View.GONE);
				// 关闭键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(CmDetailActivity.this.getWindow().peekDecorView().getWindowToken(), 0);
			}
		});

		updateList();
	}

	private void updateList() {
		new AsyncTask<Void, Void, String>() {
			protected void onPreExecute() {
				mLineList = new ArrayList<View>();
			}

			@Override
			protected String doInBackground(Void... params) {
				String result = null;
				try {
					getResult = NetInfoParser.getBubbleCommentList(bubbleID,
							userID);
					JSONObject iter;
					if (!getResult.isNull("comments")) {
						// 设定评论内容
						for (int i = 0; i < getResult.getJSONArray("comments")
								.length(); i++) {
							iter = getResult.getJSONArray("comments")
									.getJSONObject(i);

							TextView v = (TextView) mInflater.inflate(
									R.layout.comment_item, null);
							if (iter.isNull("replyUsername")) {
								// 普通回复
								v.setText(Html
										.fromHtml("<font color=\'#91b8d9\'>"
												+ iter.getString("username")
												+ "</font>: "
												+ iter.getString("content")));
							} else {
								// 楼中楼回复
								v.setText(Html.fromHtml("<font color=\'#91b8d9\'>"
										+ iter.getString("username")
										+ "</font>回复<font color=\'#91b8d9\'>"
										+ iter.getString("replyUsername")
										+ "</font>: "
										+ iter.getString("content")));
							}

							// 标定每个评论条目的属性
							v.setContentDescription(iter.getString("uid") + "|"
									+ iter.getString("username"));
							// 绑定监听
							v.setOnTouchListener(new ReplyListener());

							mLineList.add(v);
						}
					}
				} catch (SocketTimeoutException e) {
					result = "Connection timeout!";
				} catch (JSONException e) {
					result = "Illegal data format!";
					e.printStackTrace();
				} catch (Exception e) {
					result = "Unknown error occured!";
					e.printStackTrace();
				}
				return result;
			}

			protected void onPostExecute(String result) {
				if (result != null) {
					Debugger.DisplayToast(CmDetailActivity.this, result);
				} else if (mAdapter == null) {
					mAdapter = new LvAdapter(CmDetailActivity.this, mLineList);
					mListView.setAdapter(mAdapter);
					updateBubble();
				} else {
					mAdapter.setList(mLineList);
					mAdapter.notifyDataSetChanged();
				}
			}
		}.execute(null, null, null);
	}

	private void updateBubble() {
		try {
			// 设定朋友圈内容
			((TextView) CmDetailActivity.this.findViewById(R.id.bubbleAuthor))
					.setText(getResult.getString("username"));
			((TextView) CmDetailActivity.this.findViewById(R.id.postTime))
					.setText(getResult.getString("createtime"));
			((TextView) CmDetailActivity.this.findViewById(R.id.content))
					.setText(getResult.getString("content"));
			if (!getResult.isNull("photo")) {
				((ImageView) CmDetailActivity.this.findViewById(R.id.sharePic))
						.setImageBitmap(BitmapFactory.decodeFile(
								Environment.getExternalStorageDirectory()
										.getPath()
										+ "/lineonline/"
										+ getResult.getString("photo"),
								new BitmapFactory.Options()));
			} else {
				CmDetailActivity.this.findViewById(R.id.sharePic)
						.setVisibility(View.GONE);
			}
			((TextView) CmDetailActivity.this.findViewById(R.id.likeNum))
					.setText(getResult.getString("good"));
			((TextView) CmDetailActivity.this.findViewById(R.id.cmmNum))
					.setText(getResult.getString("commentNumber"));

			// 为点赞按钮设定监听
			View likeLayout = CmDetailActivity.this
					.findViewById(R.id.likeLayout);
			LikeOnClickListener mOnClickListener = new LikeOnClickListener(
					CmDetailActivity.this, likeLayout, false, userID,
					getResult.getString("bid"));
			// 根据用户是否已点赞，动态设定点赞按钮的状态
			if (getResult.getString("islike").equals("1")) {
				((ImageView) likeLayout.findViewById(R.id.likeBtn))
						.setImageResource(R.drawable.like);
				mOnClickListener.setBool(true);
			}
			likeLayout.setOnClickListener(mOnClickListener);
			
			if (getResult.isNull("comments") || getResult.getJSONArray("comments").length() == 0)
				CmDetailActivity.this.findViewById(R.id.commentHeader)
						.setVisibility(View.GONE);
		} catch (JSONException e) {
			Debugger.DisplayToast(this, "Illegal data format!");
			e.printStackTrace();
		}

	}
}
