package com.eteng.mobileorder.adapter;

import java.util.ArrayList;

import com.eteng.mobileorder.R;
import com.eteng.mobileorder.utils.DisplayMetrics;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HorizontalListViewAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<String> dataSrc;
	private LayoutInflater mInflater;

	public HorizontalListViewAdapter(Context context, ArrayList<String> dataSrc) {
		this.mContext = context;
		this.dataSrc = dataSrc;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return dataSrc.size();
	}

	@Override
	public Object getItem(int position) {
		return dataSrc.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.tab_text_view, null);
			LayoutParams mParams = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			holder.mTitle = (TextView) convertView;
			holder.mTitle.setTextSize(DisplayMetrics.sp2px(mContext, 6));
			holder.mTitle.setLayoutParams(mParams);
			holder.mTitle.setPadding(DisplayMetrics.dip2px(mContext, 5),
					DisplayMetrics.dip2px(mContext, 5),
					DisplayMetrics.dip2px(mContext, 5),
					DisplayMetrics.dip2px(mContext, 5));
			holder.mTitle.setTextColor(mContext.getResources().getColor(
					R.color.GENERAL_TEXT_COLOR));
			holder.mTitle.setGravity(Gravity.CENTER);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.mTitle.setText(dataSrc.get(position));

		return convertView;
	}

	private static class ViewHolder {
		private TextView mTitle;
		boolean isSelect = false;
	}

}