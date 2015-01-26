package com.eteng.mobileorder.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eteng.mobileorder.OrderPhoneFragment.RemarkModel;
import com.eteng.mobileorder.R;
import com.eteng.mobileorder.cusomview.RemarkListInterface.RemarkNameAdapter;

public class RemarkListAdapter extends RemarkNameAdapter {

	private Context mContext;
	private ArrayList<RemarkModel> dataSrc;
	private LayoutInflater mInflater;

	public RemarkListAdapter(Context ctx, ArrayList<RemarkModel> dataSrc) {
		this.mContext = ctx;
		this.dataSrc = dataSrc;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return dataSrc.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.order_phone_remark_item_layout, null);
			holder.nameText = (TextView) convertView
					.findViewById(R.id.remark_item_name);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		holder.nameText.setText(dataSrc.get(position).getRemarkName());
		holder.nameText.setSelected(dataSrc.get(position).isSelectStat());
		return convertView;
	}

	class ViewHolder {
		TextView nameText;
	}

	/**
	 * 选中或取消指定位置
	 * 
	 * @param position
	 */
	public void setChoiceState(int position) {

		RemarkModel item = (RemarkModel) getItem(position);
		item.setSelectStat(item.isSelectStat() ? false : true);
		this.notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return dataSrc.get(position);
	}
}
