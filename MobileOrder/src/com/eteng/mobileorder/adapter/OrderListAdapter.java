package com.eteng.mobileorder.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eteng.mobileorder.R;
import com.eteng.mobileorder.models.OrderWXModel;

public class OrderListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<OrderWXModel> dataSrc = null;
	private LayoutInflater inflater = null;

	public OrderListAdapter(Context mContext, ArrayList<OrderWXModel> dataSrc) {
		this.mContext = mContext;
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
		ViewHolder mViewHolder = null;
		if (convertView == null) {
			mViewHolder = new ViewHolder();
			inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.order_list_item_layout,
					null);
			mViewHolder.telView = (TextView) convertView
					.findViewById(R.id.general_order_list_item_tel_text);
			mViewHolder.addrView = (TextView) convertView
					.findViewById(R.id.general_order_list_item_addr_text);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		mViewHolder.telView.setText(dataSrc.get(position).getOrderTel());
		mViewHolder.addrView.setText(dataSrc.get(position).getOrderAddress());
		return convertView;
	}

	class ViewHolder {
		TextView telView, addrView;
	}
}
