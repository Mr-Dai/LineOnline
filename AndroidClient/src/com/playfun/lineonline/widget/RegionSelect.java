package com.playfun.lineonline.widget;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.playfun.lineonline.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * 地区选择控件
 */
public class RegionSelect extends LinearLayout {

	private ListView regionListView;

	private ListView plateListView;

	private ArrayList<String> groups = new ArrayList<String>();

	private LinkedList<String> childrenItem = new LinkedList<String>();

	private SparseArray<LinkedList<String>> children = new SparseArray<LinkedList<String>>();

	private TextAdapter plateListViewAdapter;

	private TextAdapter earaListViewAdapter;

	private OnSelectListener mOnSelectListener;

	private int tEaraPosition = 0;

	private int tBlockPosition = 0;

	private String showString = "不限";

	public RegionSelect(Context context) {
		super(context);
		init(context);
	}

	public RegionSelect(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void updateShowText(String showArea, String showBlock) {
		if (showArea == null || showBlock == null) {
			return;
		}
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).equals(showArea)) {
				earaListViewAdapter.setSelectedPosition(i);
				childrenItem.clear();
				if (i < children.size()) {
					childrenItem.addAll(children.get(i));
				}
				tEaraPosition = i;
				break;
			}
		}
		for (int j = 0; j < childrenItem.size(); j++) {
			if (childrenItem.get(j).replace("不限", "").equals(showBlock.trim())) {
				plateListViewAdapter.setSelectedPosition(j);
				tBlockPosition = j;
				break;
			}
		}
		setDefaultSelect();
	}

	private void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_region, this, true);
		regionListView = (ListView) findViewById(R.id.listView);
		plateListView = (ListView) findViewById(R.id.listView2);
		setBackgroundDrawable(getResources().getDrawable(
				R.drawable.choosearea_bg_left_most));

		// 获取地区分类json
		String result = "";
		JSONArray jarray = null;
		JSONObject place;

		try {
			InputStream stream = context.getResources().getAssets()
					.open("placeExport.json");
			int size = stream.available();
			byte[] buffer = new byte[size];
			stream.read(buffer);
			stream.close();
			result = new String(buffer);
			stream.close();
		} catch (Exception e) {
			Log.e("youtour", "Error reading file from assets: " + e.toString());
		}

		try {
			jarray = new JSONArray(result);
		} catch (JSONException e) {
			Log.e("youtour", "Error parsing data form file: " + e.toString());
		}
		try {
			for (int i = 0; i < jarray.length(); i++) {
				place = jarray.getJSONObject(i);
				groups.add(place.getString("catalogue"));
				JSONArray placeDetail = place.getJSONArray("includes");
				LinkedList<String> tItem = new LinkedList<String>();
				for (int j = 0; j < placeDetail.length(); j++) {
					tItem.add(placeDetail.getString(j));
				}
				children.put(i, tItem);

			}
		} catch (JSONException e) {
			Log.e("youtour", "JSONException:" + e.toString());
		}

		earaListViewAdapter = new TextAdapter(context, groups,
				R.drawable.choose_item_selected,
				R.drawable.choose_eara_item_selector);
		earaListViewAdapter.setTextSize(17);
		earaListViewAdapter.setSelectedPositionNoNotify(tEaraPosition);
		regionListView.setAdapter(earaListViewAdapter);
		earaListViewAdapter
				.setOnItemClickListener(new TextAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(View view, int position) {
						if (position < children.size()) {
							childrenItem.clear();
							childrenItem.addAll(children.get(position));
							plateListViewAdapter.notifyDataSetChanged();
						}
					}
				});
		if (tEaraPosition < children.size())
			childrenItem.addAll(children.get(tEaraPosition));
		plateListViewAdapter = new TextAdapter(context, childrenItem,
				R.drawable.choose_item_right,
				R.drawable.choose_plate_item_selector);
		plateListViewAdapter.setTextSize(15);
		plateListViewAdapter.setSelectedPositionNoNotify(tBlockPosition);
		plateListView.setAdapter(plateListViewAdapter);
		plateListViewAdapter
				.setOnItemClickListener(new TextAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(View view, final int position) {
						showString = childrenItem.get(position);
						if (mOnSelectListener != null) {
							mOnSelectListener.getValue(showString);
						}
					}
				});
		if (tBlockPosition < childrenItem.size())
			showString = childrenItem.get(tBlockPosition);
		if (showString.contains("不限")) {
			showString = showString.replace("不限", "");
		}
		setDefaultSelect();

	}

	public void setDefaultSelect() {
		regionListView.setSelection(tEaraPosition);
		plateListView.setSelection(tBlockPosition);
	}

	public String getShowText() {
		return showString;
	}

	public void setOnSelectListener(OnSelectListener onSelectListener) {
		mOnSelectListener = onSelectListener;
	}

	public interface OnSelectListener {
		public void getValue(String showText);
	}
}
