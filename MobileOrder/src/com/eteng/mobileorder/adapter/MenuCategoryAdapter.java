package com.eteng.mobileorder.adapter;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
		// if(position == 1){
		// return -1;
		// }
		// T item = getItem(position);
		// if (item instanceof MenuItemModel) {
		// if(((MenuItemModel) item).getType().equals("2")){
		// return position;
		// }
		// }
		return -1;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			convertView = mInflater.inflate(mHeaderResId, parent, false);
			if (position == 0){
			
			}else{
				convertView.setBackgroundResource(R.drawable.general_dotted_line);
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
		public TextView textView;
	}

	protected class ViewHolder {
		public TextView dishName, dishPrice;
		public NetworkImageView dishImg;
	}
}
