package com.eteng.mobileorder.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.eteng.mobileorder.R;
import com.eteng.mobileorder.models.DishInfo;
import com.eteng.mobileorder.utils.MyClickListener;
import com.eteng.mobileorder.utils.NetController;

public class SettingMenuCategoryAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private static final String TAG = SettingMenuCategoryAdapter.class
			.getSimpleName();

	private LayoutInflater mInflater;
	private ArrayList<DishInfo> dataSrc;
	private Context context;

	public SettingMenuCategoryAdapter(Context context,
			ArrayList<DishInfo> dataSrc) {
		this.context = context;
		this.dataSrc = dataSrc;
		mInflater = LayoutInflater.from(context);
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
			convertView = mInflater.inflate(
					R.layout.setting_menu_by_category_item_layout, null);
			holder = new ViewHolder();
			holder.dishName = (TextView) convertView
					.findViewById(R.id.menu_with_category_name);
			holder.dishPrice = (TextView) convertView
					.findViewById(R.id.menu_with_category_price);
			holder.dishImg = (NetworkImageView) convertView
					.findViewById(R.id.menu_with_category_img);
			holder.shadowImg = (ImageView) convertView
					.findViewById(R.id.menu_with_category_shadow_img);
			holder.putOnBtn = (TextView) convertView
					.findViewById(R.id.menu_put_on_shelves_btn);
			holder.putOffBtn = (TextView) convertView
					.findViewById(R.id.menu_put_off_shelves_btn);
			holder.putOnBtn.setTag(R.id.KEY_POSITION, position);
			holder.putOffBtn.setTag(R.id.KEY_POSITION, position);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		DishInfo item = dataSrc.get(position);
		holder.dishName.setText(((DishInfo) item).getDishName());
		holder.dishPrice.setText("￥ "
				+ String.valueOf(((DishInfo) item).getPrice()));
		holder.dishImg.setImageUrl(((DishInfo) item).getDishImgPath(),
				NetController.getInstance(context.getApplicationContext())
						.getImageLoader());
		if (item.getDishStatus().equals("1")) {// 当前为可显示状态
			holder.putOnBtn
					.setBackgroundResource(R.drawable.setting_general_corner_gray_frame);
			holder.putOffBtn
					.setBackgroundResource(R.drawable.setting_general_corner_red_frame);
			holder.putOnBtn.setClickable(false);
			holder.putOffBtn.setOnClickListener(MyClickListener.getInstance(context));
		} else {
			holder.putOnBtn
					.setBackgroundResource(R.drawable.setting_general_corner_red_frame);
			holder.putOffBtn
					.setBackgroundResource(R.drawable.setting_general_corner_gray_frame);
			holder.putOffBtn.setClickable(false);
			holder.putOnBtn.setOnClickListener(MyClickListener.getInstance(context));
		}
		if (((DishInfo) item).isChoiceState()) {
			holder.shadowImg.setBackgroundColor(Color.parseColor("#49000000"));
		} else {
			holder.shadowImg.setBackgroundColor(Color.parseColor("#00000000"));
		}
		return convertView;
	}

	protected class ViewHolder {
		public TextView dishName, dishPrice;
		public NetworkImageView dishImg;
		public ImageView shadowImg;
		public TextView putOnBtn, putOffBtn;
	}
}
