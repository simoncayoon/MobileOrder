package com.eteng.mobileorder.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eteng.mobileorder.R;
import com.eteng.mobileorder.models.MenuItemModel;

public class DishComboAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<MenuItemModel> dataSrc;
	private LayoutInflater inflater;

	public DishComboAdapter(Context context) {
		dataSrc = new ArrayList<MenuItemModel>();
		this.mContext = context;
	}

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

		mHolder.comboName.setText(dataSrc.get(position).getName());
		mHolder.comboCount.setText(String.valueOf(dataSrc.get(position)
				.getItemPrice()));
		mHolder.remark.setText("");
		return convertView;
	}

	class ViewHolder {
		TextView comboName, comboCount, remark;
	}
	
	public void setDataSrc(ArrayList<MenuItemModel> dataSrc){
		this.dataSrc = dataSrc;
		notifyDataSetChanged();
	} 
}
