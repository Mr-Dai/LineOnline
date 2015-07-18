package com.playfun.lineonline;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.NetInfoParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 游玩项目评分页面
 *
 * @author  Robert Peng
 */
public class RatingActivity extends Activity implements OnClickListener {
	private ArrayList<ImageView> mRatingBar = new ArrayList<ImageView>();
	private String attrID;
	private String userID;
	private Bundle mBundle;
	public ImageView img;
	public Calendar calendar;
	public EditText edit;
	private int rate = 0;
	public ImageView upload;
	private Bitmap bmp;
	private Uri fileUri;
	public static String str;
	private TextView submit;
	private File file;
	private String saveDir = Environment.getExternalStorageDirectory()
			.getPath() + "/tmp_image";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rate_pop_up_window);
		
		mBundle = getIntent().getExtras();
		attrID = mBundle.getString("attrID");
		userID = mBundle.getString("userID");
		
		LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.ratingBar);
		for (int i = 0; i < 5; i++) {
			mRatingBar.add((ImageView) mLinearLayout.getChildAt(i));
		}
		
		for (ImageView iv : mRatingBar) {
			iv.setOnClickListener(this);
		}
		calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		Date date = calendar.getTime();
		DateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
		str = d.format(date) + ".jpg";
		img = (ImageView) findViewById(R.id.uploadpic);
		upload = (ImageView) findViewById(R.id.upload);
		edit = (EditText) findViewById(R.id.editcontent);
		submit = (TextView) findViewById(R.id.submit);
		File savePath = new File(saveDir);
		if (!savePath.exists()) {
			savePath.mkdirs();
		}
		upload.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(RatingActivity.this)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setItems(new String[] { "从相册选择", "拍照" },
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										if (which == 0) {
											Intent intent = new Intent(
													Intent.ACTION_PICK,
													android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);// 调用android的图库
											startActivityForResult(intent, 1);
										} else {

											destoryImage();
											String state = Environment
													.getExternalStorageState();
											if (state
													.equals(Environment.MEDIA_MOUNTED)) {
												file = new File(saveDir, str);

												if (!file.exists()) {
													try {
														file.createNewFile();
													} catch (IOException e) {
														e.printStackTrace();

														return;
													}
												}
												Intent intent = new Intent(
														"android.media.action.IMAGE_CAPTURE");
												intent.putExtra(
														MediaStore.EXTRA_OUTPUT,
														Uri.fromFile(file));
												startActivityForResult(intent,
														2);
											} else {
												Toast.makeText(
														RatingActivity.this,
														"sdcard无效或没有插入!",
														Toast.LENGTH_SHORT)
														.show();
											}

										}
									}
								}).show();
			}
		});
		submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (edit.getText().toString().equals("")) {
					Debugger.DisplayToast(RatingActivity.this, "评价内容不能为空");
					return;
				}
				new AsyncTask<Void, Void, String>() {
					@Override
					protected String doInBackground(Void... params) {
						String result = null;
						String content = edit.getText().toString();
						try {
							result = NetInfoParser.rate(userID, attrID, rate, content);
						} catch (SocketTimeoutException e) {
							Debugger.DisplayToast(RatingActivity.this, "连接超时");
						} catch (Exception e) {
							Debugger.DisplayToast(RatingActivity.this, "发生了未知错误");
						}
						return result;
					}
					protected void onPostExecute(String result) {
						if (result != null && result.equals("true")) {
							Debugger.DisplayToast(RatingActivity.this, "发布成功");
							RatingActivity.this.finish();
						} else {
							Debugger.DisplayToast(RatingActivity.this, result);
							RatingActivity.this.finish();
						}
					}
				}.execute(null, null, null);
			}
		});

		findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				RatingActivity.this.finish();
			}
		});
	}

	@Override
	public void onClick(View v) {
		int i;
		rate = Integer.parseInt((String)v.getContentDescription());
		for (i = 0; i < rate; i++)
			mRatingBar.get(i).setImageResource(R.drawable.full_star);
		for (; i < 5; i++)
			mRatingBar.get(i).setImageResource(R.drawable.empty_star);
	}
	
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if(keyCode == KeyEvent.KEYCODE_BACK){
			 this.finish();
			 return true;
		 }
		 return super.onKeyDown(keyCode, event);
	}
	
	private void destoryImage() {
		bmp = null;
	}
}
