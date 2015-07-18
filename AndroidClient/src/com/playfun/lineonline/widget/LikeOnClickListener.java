package com.playfun.lineonline.widget;

import java.net.SocketTimeoutException;

import com.playfun.lineonline.R;
import com.playfun.lineonline.util.Debugger;
import com.playfun.lineonline.util.NetInfoParser;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LikeOnClickListener implements OnClickListener {
	private ImageView likeIcon;
	private TextView listNum;
	private String userID;
	private String bubbleID;
	private Context context;

	private boolean hasLiked = false;

	public LikeOnClickListener(Context context, View v, boolean l, String userID, String bubbleID) {

		likeIcon = (ImageView) v.findViewById(R.id.likeBtn);
		listNum = (TextView) v.findViewById(R.id.likeNum);
		
		this.context = context;
		this.userID = userID;
		this.bubbleID = bubbleID;

		hasLiked = l;
	}
	
	public void setBool(boolean l) {
		hasLiked = l;
	}

	@Override
	public void onClick(View v) {
		if (!hasLiked) {
			likeIcon.setImageResource(R.drawable.like);
			listNum.setText(String.valueOf(Integer.parseInt((String) listNum
					.getText()) + 1));
			hasLiked = true;
			
			new AsyncTask<Void, Void, String>() {
				protected String doInBackground(Void... params) {
					String result = null;
					try {
						result = NetInfoParser.like(userID, bubbleID);
					} catch (SocketTimeoutException e) {
						Debugger.DisplayToast(context, "Connection timeout!");
						likeIcon.setImageResource(R.drawable.unlike);
						listNum.setText(String.valueOf(Integer.parseInt((String) listNum
								.getText()) - 1));
						hasLiked = false;
					}
					return result;
				}
			}.execute(null, null, null);
		} else {
			likeIcon.setImageResource(R.drawable.unlike);
			listNum.setText(String.valueOf(Integer.parseInt((String) listNum
					.getText()) - 1));
			hasLiked = false;
			
			new AsyncTask<Void, Void, String>() {
				protected String doInBackground(Void... params) {
					String result = null;
					try {
						result = NetInfoParser.unLike(userID, bubbleID);
					} catch (SocketTimeoutException e) {
						Debugger.DisplayToast(context, "Connection timeout!");
						likeIcon.setImageResource(R.drawable.like);
						listNum.setText(String.valueOf(Integer.parseInt((String) listNum
								.getText()) + 1));
						hasLiked = true;
					}
					return result;
				}
			}.execute(null, null, null);
		}
	}

}
