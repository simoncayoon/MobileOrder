package com.eteng.mobileorder.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eteng.mobileorder.R;
import com.eteng.mobileorder.models.OrderDetailModel;

public class DishComboAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<OrderDetailModel> dataSrc;
	private LayoutInflater inflater;

	public DishComboAdapter(Context context) {
		dataSrc = new ArrayList<OrderDetailModel>();
		this.mContext = context;
	}

	public DishComboAdapter(Context context, ArrayList<OrderDetailModel> dataSrc) {
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
		ViewHolder mHolder = null;
		if (convertView == null) {
			inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mHolder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.order_phone_combo_list_item_layout, parent, false);
			mHolder.comboName = (TextView) convertView
					.findViewById(R.id.order_phone_dish_name);
			mHolder.comboCount = (TextView) convertView
					.findViewById(R.id.order_phone_dish_count);
			mHolder.remark = (TextView) convertView
					.findViewById(R.id.order_phone_dish_remark);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		mHolder.comboName.setText(dataSrc.get(position).getComboName());//填充名字
		mHolder.comboCount.setText(String.valueOf(dataSrc.get(position)
				.getTotalPrice()));//填充价钱
		String remarkString = dataSrc.get(position).getRemarkName();//填充备注信息
		if (remarkString == null) {
			mHolder.remark.setText("");
		} else {
			mHolder.remark.setText(remarkString);
		}

		return convertView;
	}

	class ViewHolder {
		TextView comboName, comboCount, remark;
	}

	public void setDataSrc(ArrayList<OrderDetailModel> dataSrc) {
		this.dataSrc = dataSrc;
		notifyDataSetChanged();
	}
}
