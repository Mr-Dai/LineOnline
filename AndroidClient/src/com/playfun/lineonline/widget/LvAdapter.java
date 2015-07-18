package com.playfun.lineonline.widget;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LvAdapter extends BaseAdapter {
	private List<View> list;
	private Context context;

	public LvAdapter(Context context, List<View> list) {
		this.list = list;
		this.context = context;
	}
	
	public void setList(List<View> list) {
		this.list = list;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return list.get(position);
	}

}
