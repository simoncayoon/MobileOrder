package com.eteng.mobileorder.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.eteng.mobileorder.models.MenuItemModel;

public class DishComboAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<MenuItemModel> dataSrc;

	public DishComboAdapter(Context context, ArrayList<MenuItemModel> dataSrc) {
		this.mContext = context;
		this.dataSrc = dataSrc;
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
		if (convertView == null) {
			

		}
		return null;
	}

	class ViewHolder {
	}

}
