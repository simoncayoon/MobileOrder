package com.eteng.mobileorder.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.eteng.mobileorder.R;
import com.eteng.mobileorder.models.MenuItemModel;
import com.eteng.mobileorder.utils.NetController;

public class SettingMenuCategoryAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<MenuItemModel> dataSrc;
	private Context context;

	public SettingMenuCategoryAdapter(Context context,
			ArrayList<MenuItemModel> dataSrc) {
		this.context = context;
		this.dataSrc = dataSrc;
		mInflater = LayoutInflater.from(context);
//		if (context instanceof PutBtnListener) {
//			mCallBack = (PutBtnListener) context;
//		}
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
			holder.putOnBtn = (Button) convertView
					.findViewById(R.id.menu_put_on_shelves_btn);
			holder.putOffBtn = (Button) convertView
					.findViewById(R.id.menu_put_off_shelves_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MenuItemModel item = dataSrc.get(position);
		holder.dishName.setText(((MenuItemModel) item).getName());
		holder.dishPrice.setText("￥ "
				+ String.valueOf(((MenuItemModel) item).getItemPrice()));
		holder.dishImg.setImageUrl(((MenuItemModel) item).getImgUrl(),
				NetController.getInstance(context.getApplicationContext())
						.getImageLoader());
		
		if (((MenuItemModel) item).isChoiceState()) {
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
		public Button putOnBtn, putOffBtn;
	}

//	public interface PutBtnListener {
//		public void putShelvesListener(int position);
//
////		public void putOffShelvesListener();
//	}
}
