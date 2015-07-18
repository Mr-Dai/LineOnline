package com.playfun.lineonline;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.NetInfoParser;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.graphics.Matrix;

public class BubbleActivity extends Activity {
	public ImageView img;
	public ImageView upload;
	private TextView submit;
	public EditText edit;
	private Uri fileUri;
	private Bitmap bmp;
	public Calendar calendar;
	public static String str;
	private File file;
	private String saveDir = Environment.getExternalStorageDirectory()
			.getPath() + "/tmp_image";
	
	private String userID;
	private String playgroundID;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userID = getIntent().getExtras().getString("userID");
		playgroundID = getIntent().getExtras().getString("playgroundID");
		init();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == 1) {
			ContentResolver resolver = this.getContentResolver();
			try {
				Uri originalUrl = data.getData();
				bmp = BitmapFactory.decodeStream(resolver
						.openInputStream(originalUrl));
				img.setVisibility(View.VISIBLE);
				img.setImageBitmap(bmp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (requestCode == 2 && resultCode == RESULT_OK) {

			if (file != null && file.exists()) {
				BitmapFactory.Options option = new BitmapFactory.Options();
				option.inSampleSize = 2;
				bmp = BitmapFactory.decodeFile(file.getPath(), option);
				Matrix matrix = new Matrix();
				matrix.postScale(1f, 1f);
				matrix.postRotate(90);
				bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
						bmp.getHeight(), matrix, true);
				int w = bmp.getWidth();
				int h = (int) ((1 - 0.4f) * bmp.getHeight());
				bmp = Bitmap.createBitmap(bmp, 0, 0, w, h, null, false);
				img.setVisibility(View.VISIBLE);
				img.setImageBitmap(bmp);
			}
		}
	}

	public void init() {
		setContentView(R.layout.bubble_main);
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
				new AlertDialog.Builder(BubbleActivity.this)
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
														BubbleActivity.this,
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
					Debugger.DisplayToast(BubbleActivity.this, "朋友圈内容不能为空");
					return;
				}
				new AsyncTask<Void, Void, String>() {
					@Override
					protected String doInBackground(Void... params) {
						String result = null;
						String content = edit.getText().toString();
						try {
							result = NetInfoParser.postBubble(userID, playgroundID, content);
						} catch (SocketTimeoutException e) {
							Debugger.DisplayToast(BubbleActivity.this, "连接超时");
						} catch (Exception e) {
							Debugger.DisplayToast(BubbleActivity.this, "发生了未知错误");
						}
						return result;
					}
					protected void onPostExecute(String result) {
						if (result != null && result.split("\\{")[0].equals("true")) {
							Debugger.DisplayToast(BubbleActivity.this, "发布成功");
							Intent resultIntent = new Intent();
							resultIntent.putExtra("succeed", true);
							BubbleActivity.this.setResult(RESULT_OK, resultIntent);
							BubbleActivity.this.finish();
						} else {
							Debugger.DisplayToast(BubbleActivity.this, "发布失败");
							Intent resultIntent = new Intent();
							resultIntent.putExtra("succeed", false);
							BubbleActivity.this.setResult(RESULT_OK, resultIntent);
							BubbleActivity.this.finish();
						}
					}
				}.execute(null, null, null);
			}
		});

		findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				BubbleActivity.this.finish();
			}
		});
	}

	public void onDestroy() {
		destoryImage();
		super.onDestroy();
	}

	private void destoryImage() {
		bmp = null;
	}
}
