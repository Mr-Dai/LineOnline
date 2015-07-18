package com.playfun.lineonline;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.playfun.lineonline.util.Communicator;
import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.MD5Encoder;
import com.playfun.lineonline.util.NetInfoParser;
import com.playfun.lineonline.widget.BaseActivity;
import com.playfun.lineonline.widget.LvAdapter;
import com.playfun.lineonline.widget.RegionSelect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

public class HomePage extends BaseActivity implements OnTouchListener,
		OnDismissListener {

	private ArrayList<Button> navigators = new ArrayList<Button>();
	private PopupWindow popupWindow;
	private RelativeLayout mPopupView;
	private int displayWidth;
	private int displayHeight;
	private TextView popupAnchor;
	private RegionSelect mViewLeft;
	private ListView mListView;
	private LvAdapter mAdapter;
	private ArrayList<View> mViewList;
	private Bundle mBundle;
	private EditText mEditText;
	private String region = "广州";
	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_page);

		mListView = (ListView) findViewById(R.id.playgroundList);
		mInflater = LayoutInflater.from(this);

		updateList();

		mBundle = getIntent().getExtras();
		
		mEditText = (EditText) findViewById(R.id.playgroundSearch);
		Drawable img = getResources().getDrawable(R.drawable.search);
		img.setBounds(0, 0, (int) mEditText.getTextSize(),
				(int) mEditText.getTextSize());
		mEditText.setCompoundDrawables(img, null, null, null);
		mEditText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (event != null
								&& event.getAction() != KeyEvent.ACTION_DOWN)
							return false;
						if (actionId == EditorInfo.IME_ACTION_SEND
								|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
							searchJump();
							return true;
						}
						return false;
					}
				});

		displayWidth = getWindowManager().getDefaultDisplay().getWidth();
		displayHeight = getWindowManager().getDefaultDisplay().getHeight();

		popupAnchor = (TextView) findViewById(R.id.citySelector);
		OnClickListener mOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!popupWindow.isShowing())
					startAnimation();
				else
					onPressBack();
			}
		};
		popupAnchor.setOnClickListener(mOnClickListener);
		findViewById(R.id.citySelectorArrow).setOnClickListener(
				mOnClickListener);

		mPopupView = new RelativeLayout(this);
		int maxHeight = (int) (displayHeight * 0.7);
		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, maxHeight);
		rl.leftMargin = 10;
		rl.rightMargin = 10;
		mViewLeft = new RegionSelect(this);
		mViewLeft.setOnSelectListener(new RegionSelect.OnSelectListener() {
			@Override
			public void getValue(String showText) {
				// 填充请求URL
				onRefresh(mViewLeft, showText);
			}
		});
		mPopupView.addView(mViewLeft, rl);
		mPopupView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onPressBack();
			}
		});
		mPopupView.setBackgroundColor(getResources().getColor(
				R.color.popup_main_background));

		popupWindow = new PopupWindow(mPopupView, displayWidth, displayHeight);
		popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
		popupWindow.setFocusable(false);
		popupWindow.setOutsideTouchable(true);
		findViewById(R.id.twoDScanMenuItem).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(HomePage.this,
								DecoderActivity.class));
					}
				});
	}

	public boolean onPressBack() {
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
			return true;
		} else {
			return false;
		}
	}

	private void searchJump() {
		String searchField = mEditText.getText().toString();
		Bundle mBundle = new Bundle();
		mBundle.putString("searchField", searchField);
		Intent mIntent = new Intent(HomePage.this, ProjectSearchActivity.class);
		mIntent.putExtras(mBundle);
		startActivityForResult(mIntent, 1);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		jumpToPlayground((String) data.getCharSequenceExtra("playgroundID"));
	}

	private void jumpToPlayground(String playgroundID) {
		Intent mIntent = new Intent(HomePage.this, MainActivity.class);
		String NativePhoneNumber = null;
		mBundle.putString("playgroundID", playgroundID);
		mIntent.putExtras(mBundle);

		startActivity(mIntent);
	}

	private void startAnimation() {
		if (!popupWindow.isShowing()) {
			popupWindow.showAsDropDown(popupAnchor, 0, 0);
		} else {
			popupWindow.setOnDismissListener(this);
			popupWindow.dismiss();
		}
	}

	@Override
	public void onDismiss() {
		popupWindow.showAsDropDown(popupAnchor, 0, 0);
		popupWindow.setOnDismissListener(null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!onPressBack())
				return super.onKeyDown(keyCode, event);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void updateList() {
		new AsyncTask<Void, Void, String>() {
			protected String doInBackground(Void... params) {
				mViewList = new ArrayList<View>();
				String result = null;
				try {
					JSONArray getResult = NetInfoParser
							.getPlayGroundList(region);
					JSONObject iter;
					for (int i = 0; i < getResult.length(); i++) {
						iter = getResult.getJSONObject(i);
						View v = mInflater.inflate(R.layout.playground_item,
								null);
						v.setContentDescription(iter.getString("pgid"));
						((TextView) v.findViewById(R.id.playgroundName))
								.setText(iter.getString("name"));
						((ImageView) v.findViewById(R.id.playgroundIcon))
								.setImageBitmap(BitmapFactory.decodeFile(
										Environment
												.getExternalStorageDirectory()
												.getPath()
												+ "/lineonline/"
												+ iter.getString("icon"),
										new BitmapFactory.Options()));

						v.setOnTouchListener(HomePage.this);
						if (i == getResult.length() - 1) {
							v.findViewById(R.id.banner)
									.setVisibility(View.GONE);
						}
						mViewList.add(v);
					}
				} catch (SocketTimeoutException e) {
					result = "Connection timeout!";
				} catch (JSONException e) {
					result = "Data format illegal!";
				} catch (Exception e) {
					result = "Unknown error occured!";
					e.printStackTrace();
				}

				return result == null ? null : result.trim();
			}

			protected void onPostExecute(String result) {
				if (result != null) {
					Debugger.DisplayToast(HomePage.this, result);
				} else if (mAdapter == null) {
					mAdapter = new LvAdapter(HomePage.this, mViewList);
					mListView.setAdapter(mAdapter);
				} else {
					mAdapter.setList(mViewList);
					mAdapter.notifyDataSetChanged();
				}
			}
		}.execute(null, null, null);
	}

	private void onRefresh(View view, String showText) {
		onPressBack();
		if (!popupAnchor.getText().equals(showText)) {
			popupAnchor.setText(showText);
			updateList();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			jumpToPlayground((String) v.getContentDescription());
			return true;
		}
		return false;
	}
}
