package com.eteng.mobileorder.adapter;

import java.util.ArrayList;
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
import com.eteng.mobileorder.models.DishInfo;
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

	private List<T> mainItems;

	@SuppressWarnings("unused")
	private List<T> attachItems;

	private Context mContext;

	private boolean isSingleSelect = false;

	public boolean isMainCheck = false;// 粉面类是否选中主食

	public MenuCategoryAdapter(Context context, List<T> items, int headerResId,
			int itemResId, boolean isSingleSelect) {
		init(context, items, headerResId, itemResId, isSingleSelect);
	}

	public MenuCategoryAdapter(Context context, List<T> mainItems,
			List<T> attachItems, int headerResId, int itemResId,
			boolean isSingleSelect) {
		init(context, mainItems, attachItems, headerResId, itemResId,
				isSingleSelect);
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
		// if(position == 0){
		// return -1;
		// }
		return 5;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			if (position == 0) {
				View nullView = new View(mContext);
				convertView = nullView;
				return convertView;
			}
			convertView = mInflater.inflate(mHeaderResId, parent, false);
			convertView.setBackgroundResource(R.drawable.general_dotted_line);
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
		if (item instanceof DishInfo) {
			holder.dishName.setText(((DishInfo) item).getDishName());
			holder.dishPrice.setText("￥ "
					+ String.valueOf(((DishInfo) item).getPrice()));
			holder.dishImg.setImageUrl(((DishInfo) item).getDishImgPath(),
					NetController.getInstance(mContext.getApplicationContext())
							.getImageLoader());
			if (((DishInfo) item).isChoiceState()) {
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
			int itemResId, boolean isSingleSelect) {
		this.mItems = items;
		this.mHeaderResId = headerResId;
		this.mItemResId = itemResId;
		this.mContext = context;
		this.isSingleSelect = isSingleSelect;
		mInflater = LayoutInflater.from(context);
	}

	private void init(Context context, List<T> mainItems, List<T> attachItems,
			int headerResId, int itemResId, boolean isSingleSelect) {
		this.mainItems = mainItems;
		this.attachItems = attachItems;
		mainItems.addAll(attachItems);
		this.mItems = mainItems;
		this.mHeaderResId = headerResId;
		this.mItemResId = itemResId;
		this.mContext = context;
		this.isSingleSelect = isSingleSelect;
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
	 * 选中后取消指定位置
	 * 
	 * @param position
	 */
	public void setChoiceState(int position) {
		DishInfo temp = (DishInfo) getItem(position);
		temp.setChoiceState(temp.isChoiceState() ? false : true);
		if (isSingleSelect && temp.getDishType().equals("1")) {// 主餐单选 清除其他主餐选项

			isMainCheck = temp.isChoiceState() ? true : false;
			for (int i = 0; i < mainItems.size(); i++) {
				DishInfo item = (DishInfo) getItem(i);
				if (item.getDishType().equals("1") && !item.equals(temp)) {
					item.setChoiceState(false);
				}
			}

		}
		this.notifyDataSetChanged();
	}

	/**
	 * 获取选中数据
	 */
	public ArrayList<DishInfo> getSelectList() {
		ArrayList<DishInfo> selectList = new ArrayList<DishInfo>();
		for (T temp : mItems) {
			if (temp instanceof DishInfo) {
				if (((DishInfo) temp).isChoiceState()) {
					selectList.add((DishInfo) temp);
				}

			}
		}
		return selectList;
	}

	public ArrayList<DishInfo> getAttachList() {
		ArrayList<DishInfo> attachList = new ArrayList<DishInfo>();
		for (T temp : mItems) {
			if (temp instanceof DishInfo) {
				if (((DishInfo) temp).isChoiceState()
						&& ((DishInfo) temp).getDishType().equals("2")) {
					attachList.add((DishInfo) temp);
				}
			}
		}
		return attachList;
	}

	/**
	 * 清除选中效果
	 * 
	 * @param isSingle
	 */
	public void resetDataDefault() {
		for (T temp : mItems) {
			if (temp instanceof DishInfo) {
				((DishInfo) temp).setChoiceState(false);
			}
		}
		this.notifyDataSetChanged();
	}

	int getHeaderIndex() {
		for (int index = 1; index < mItems.size(); index++) {
			DishInfo currentItem = (DishInfo) mItems.get(index);
			DishInfo lastItem = (DishInfo) mItems.get(index - 1);
			if (currentItem.getDishType().equals(lastItem.getDishType())) {
				return index;
			}
		}
		return -1;
	}
}
