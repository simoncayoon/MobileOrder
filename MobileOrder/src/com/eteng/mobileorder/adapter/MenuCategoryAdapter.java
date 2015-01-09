package com.eteng.mobileorder.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.eteng.mobileorder.models.MenuItemModel;
import com.eteng.mobileorder.utils.NetController;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

public class MenuCategoryAdapter<T> extends BaseAdapter implements
		StickyGridHeadersSimpleAdapter {

	protected static final String TAG = MenuCategoryAdapter.class
			.getSimpleName();

	private int mHeaderResId;

	private LayoutInflater mInflater;

	private int mItemResId;

	private List<T> mItems;

	private Context mContext;

	public MenuCategoryAdapter(Context context, List<T> items, int headerResId,
			int itemResId) {
		init(context, items, headerResId, itemResId);
	}

	public MenuCategoryAdapter(Context context, T[] items, int headerResId,
			int itemResId) {
		init(context, Arrays.asList(items), headerResId, itemResId);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public long getHeaderId(int position) {
		return -1;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mInflater.inflate(mHeaderResId, parent, false);
			if (position == 0) {

			} else {
				convertView
						.setBackgroundResource(R.drawable.general_dotted_line);
			}
		}
		return convertView;
	}

	@Override
	public T getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	@SuppressWarnings("unchecked")
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(mItemResId, parent, false);
			holder = new ViewHolder();
			holder.dishName = (TextView) convertView
					.findViewById(R.id.menu_with_category_name);
			holder.dishPrice = (TextView) convertView
					.findViewById(R.id.menu_with_category_price);
			holder.dishImg = (NetworkImageView) convertView
					.findViewById(R.id.menu_with_category_img);
			holder.shadowImg = (ImageView) convertView
					.findViewById(R.id.menu_with_category_shadow_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		T item = getItem(position);
		if (item instanceof MenuItemModel) {
			holder.dishName.setText(((MenuItemModel) item).getName());
			holder.dishPrice.setText("￥ "
					+ String.valueOf(((MenuItemModel) item).getItemPrice()));
			holder.dishImg.setImageUrl(((MenuItemModel) item).getImgUrl(),
					NetController.getInstance(mContext.getApplicationContext())
							.getImageLoader());
			if (((MenuItemModel) item).isChoiceState()) {
				holder.shadowImg.setBackgroundColor(Color
						.parseColor("#49000000"));
			} else {
				holder.shadowImg.setBackgroundColor(Color
						.parseColor("#00000000"));
			}
		}
		return convertView;
	}

	private void init(Context context, List<T> items, int headerResId,
			int itemResId) {
		this.mItems = items;
		this.mHeaderResId = headerResId;
		this.mItemResId = itemResId;
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	protected class HeaderViewHolder {
		public ImageView textView;
	}

	protected class ViewHolder {
		public TextView dishName, dishPrice;
		public NetworkImageView dishImg;
		public ImageView shadowImg;
	}

	/**
	 * 选中指定位置
	 * 
	 * @param position
	 */
	public void setChoiceState(int position) {
		MenuItemModel temp = (MenuItemModel) getItem(position);
		temp.setChoiceState(temp.isChoiceState() ? false : true);
		this.notifyDataSetChanged();
	}

	/**
	 * 获取选中数据
	 */
	public ArrayList<MenuItemModel> getSelectList() {
		ArrayList<MenuItemModel> selectList = new ArrayList<MenuItemModel>();
		for (T temp : mItems) {
			if (temp instanceof MenuItemModel) {
				if (((MenuItemModel) temp).isChoiceState()) {
					selectList.add((MenuItemModel) temp);
				}
			}
		}
		return selectList;
	}

	public void resetDataDefault() {
		for (T temp : mItems) {
			if (temp instanceof MenuItemModel) {
				((MenuItemModel) temp).setChoiceState(false);
			}
		}
		this.notifyDataSetChanged();
	}
}
