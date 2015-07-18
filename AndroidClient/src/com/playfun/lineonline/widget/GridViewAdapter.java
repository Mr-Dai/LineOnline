package com.playfun.lineonline.widget;

import com.playfun.lineonline.R;

import android.content.Context;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

public class GridViewAdapter extends BaseAdapter {
	private Context context;
	private Integer[] mImageIds = {
			  R.drawable.canteen,
			  R.drawable.phone,
			  R.drawable.wc,
			  R.drawable.wifi

	};

	public GridViewAdapter(Context c) {
		context = c;
	}

	// ��ȡͼƬ�ĸ���
	public int getCount() {
		return mImageIds.length;
	}

	// ��ȡͼƬ�ڿ��е�λ��
	public Object getItem(int position) {
		return position;
	}

	// ��ȡͼƬID
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			// ��ImageView������Դ
			imageView = new ImageView(context);
			// ���ò��� ͼƬ120��120��ʾ
			imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
			// ������ʾ��������
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		} else {
			imageView = (ImageView) convertView;
		}

		imageView.setImageResource(mImageIds[position]);
		return imageView;
	}
}
