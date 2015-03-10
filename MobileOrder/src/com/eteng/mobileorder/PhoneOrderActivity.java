package com.eteng.mobileorder;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.cusomview.TopNavigationBar.NaviBtnListener;
import com.eteng.mobileorder.models.CategoryInfo;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.DishInfo;
import com.eteng.mobileorder.models.OrderDetailModel;
import com.eteng.mobileorder.utils.DbHelper;
import com.eteng.mobileorder.utils.GetRemoteDateHelper;
import com.eteng.mobileorder.utils.GetRemoteDateHelper.ShowData;
import com.eteng.mobileorder.utils.StringMaker;
import com.eteng.mobileorder.utils.TempDataManager;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

public class PhoneOrderActivity extends FragmentActivity implements
		OrderPhoneFragment.Callbacks, NaviBtnListener, OnClickListener,
		ShowData {

	@SuppressWarnings("unused")
	private static final String TAG = "PhoneOrderActivity";
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;
	private ViewPager viewPager;
	private ScrollIndicatorView indicator;
	private Button addToComboList;
	// private BadgeView countView;

	private ArrayList<CategoryInfo> menuArray;
	private ArrayList<OrderDetailModel> comboList;
	private ArrayList<String> attachStringList;
	private MyAdapter mAdapter;
	private LinkedHashSet<ArrayList<OrderDetailModel>> allComboList;
	private boolean hasDish = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_phone_layout);
		initView();
		menuArray = new ArrayList<CategoryInfo>();
		comboList = new ArrayList<OrderDetailModel>();
		allComboList = new LinkedHashSet<ArrayList<OrderDetailModel>>();
		inflate = LayoutInflater.from(getApplicationContext());
		getMenuCategory();// 获取菜单种类
	}

	private void initView() {
		TopNavigationBar navi = (TopNavigationBar) findViewById(R.id.general_navi_view);
		navi.setLeftImg(R.drawable.order_phone_navi_left_btn_selector);
		navi.setTitle("电话订餐");// 设置标题

		addToComboList = (Button) findViewById(R.id.add_combolist_btn);
		addToComboList.setOnClickListener(this);
		initMenuView();
	}

	private void initMenuView() {

		indicator = (ScrollIndicatorView) findViewById(R.id.moretab_indicator);

		indicator.setScrollBar(new ColorBar(PhoneOrderActivity.this, Color.RED,
				5));
		// 设置滚动监听
		int selectColorId = R.color.tab_text_color_selector;
		int unSelectColorId = R.color.OPTION_TEXT_COLOR_DEFAULT;
		indicator.setOnTransitionListener(new OnTransitionTextListener()
				.setColorId(PhoneOrderActivity.this, selectColorId,
						unSelectColorId));
	}

	private class MyAdapter extends IndicatorFragmentPagerAdapter {

		public MyAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return menuArray.size();
		}

		@Override
		public View getViewForTab(int position, View convertView,
				ViewGroup container) {
			if (convertView == null) {
				convertView = inflate.inflate(R.layout.tab_text_view,
						container, false);
			}
			TextView textView = (TextView) convertView;
			textView.setText(menuArray.get(position).getCategoryName());
			textView.setPadding(30, 0, 30, 0);
			textView.setTextColor(getResources().getColor(
					R.color.GENERAL_TEXT_COLOR));
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			return convertView;
		}

		@Override
		public Fragment getFragmentForPage(int position) {
			OrderPhoneFragment fragment = new OrderPhoneFragment();
			Bundle bundle = new Bundle();
			bundle.putLong(OrderPhoneFragment.INTENT_INT_CATEGORY_ID, menuArray
					.get(position).getCategoryId());
			bundle.putBoolean(OrderPhoneFragment.INTENT_IS_NOODLE, menuArray
					.get(position).getIsNoodle());
			if (menuArray.get(position).getIsNoodle()) {// 保存粉面的ID
				TempDataManager.getInstance(PhoneOrderActivity.this)
						.saveNoodleid(menuArray.get(position).getCategoryId());
			}
			fragment.setArguments(bundle);
			return fragment;
		}
	}

	void getMenuCategory() {

		if (TempDataManager.getInstance(this).getIsFirstVisit()) {// 第一次登陆，同步数据
			GetRemoteDateHelper getRemote = new GetRemoteDateHelper(this,
					getApplicationContext());
			getRemote.getRemoteDate();
			TempDataManager.getInstance(this).setIsFirstVisit(false);
		} else {// 加载本地数据
			refreshData();
		}

	}

	void refreshData() {
		menuArray = (ArrayList<CategoryInfo>) DbHelper.getInstance(this)
				.getLocalCategory();
		mAdapter = new MyAdapter(getSupportFragmentManager());
		if (menuArray != null && menuArray.size() > 0) {
			viewPager = (ViewPager) findViewById(R.id.moretab_viewPager);
			viewPager.setOffscreenPageLimit(menuArray.size());
			indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
			indicatorViewPager.setAdapter(mAdapter);
		}
	}

	@Override
	public void onItemSelected(int position) {

	};

	@Override
	public void leftBtnListener() {
		if (hasDish) {
			Intent mIntent = new Intent();
			Bundle mBundle = new Bundle();
			mBundle.putParcelableArrayList(Constants.DISH_COMBO_RESULT,
					comboList);
			mIntent.putExtras(mBundle);
			// 提交到配餐列表
			setResult(Constants.RESULT_CODE, mIntent);// resultCode错误
		}
		finish();
	}

	@Override
	public void rightBtnListener() {

	}

	@Override
	public void onClick(View v) {
		/**
		 * 收集所有fragment中的数据
		 */
		OrderPhoneFragment tempFragment = null;
		attachStringList = new ArrayList<String>();
		for (int i = 0; i < menuArray.size(); i++) {// 遍历每个类目

			tempFragment = getFragWithposition(i);
			if (tempFragment == null) {
				continue;
			}
			if (tempFragment.categoryId == null) {
				continue;
			}
			if (tempFragment.mAdapter == null) {
				continue;
			}
			if (tempFragment.categoryId.equals(0)
					|| tempFragment.mAdapter == null) {// 没有实例化
				continue;
			}
			ArrayList<DishInfo> tempList = new ArrayList<DishInfo>();
			tempList = tempFragment.mAdapter.getSelectList();
			int listSize = tempList.size();
			if (listSize > 0) {
				if (tempFragment.isSingleSelect) {// 如果是粉面类，将名称和总价合并
					if (!tempFragment.getAdapter().isMainCheck
							&& tempList.size() > 0) {// 当粉面有选项，但未选择主食
						Toast.makeText(PhoneOrderActivity.this, "未选择粉面主食",
								Toast.LENGTH_SHORT).show();
						return;
					}
					OrderDetailModel orderItem = new OrderDetailModel();
					StringBuilder sb = new StringBuilder();
					Double totalPrice = 0.0;// 总价
					double attachPrice = 0.0;// 附加价
					DishInfo mainDish = new DishInfo();// 主食（一个）
					for (int j = 0; j < listSize; j++) {
						if (!tempList.get(j).getDishType().equals("1")) {
							attachStringList.add(tempList.get(j).getDishName());
							attachPrice += tempList.get(j).getPrice();
						} else {
							mainDish = tempList.get(j);
						}
						if (j < (listSize - 1)) {
							sb = sb.append(tempList.get(j).getDishName()
									+ " + ");// 追加“+”
							totalPrice += tempList.get(j).getPrice();// 单项价格
							continue;
						}
						sb = sb.append(tempList.get(j).getDishName());
						totalPrice += tempList.get(j).getPrice();// 计算总价
					}
					if (tempFragment.mRemarkAdapter != null) {
						orderItem.setAskFor(StringMaker.divWithSymbol(",",
								tempFragment.mRemarkAdapter.getSelectList()));// 填充备注信息
						orderItem.setRemarkName(StringMaker.divWithSymbol(
								" + ",
								tempFragment.mRemarkAdapter.getSelectList()));// 填充备注展示信息
						tempFragment.mRemarkAdapter.resetDataDefault();
					}

					orderItem
							.setGoodsDiscountPrice(mainDish.getDiscountPrice());
					orderItem.setGoodsSinglePrice(mainDish.getPrice());
					orderItem.setAttachName(StringMaker.divWithSymbol(",",
							attachStringList));// 附加产品组合
					orderItem.setAttachPrice(attachPrice);
					orderItem.setOrderId("");
					orderItem.setGoodsId(mainDish.getDishId());
					orderItem.setGoodsName(mainDish.getDishName());
					orderItem.setComboName(sb.toString());// 填充展示组合名称
					orderItem.setTotalPrice(totalPrice);// 填充单项订单总价
					comboList.add(orderItem);
					tempFragment.mAdapter.isMainCheck = false;// 重置主食选择状态
					continue;
				}
				for (DishInfo item : tempList) {
					OrderDetailModel orderItem = new OrderDetailModel();
					orderItem.setAskFor("");
					orderItem.setGoodsDiscountPrice(item.getDiscountPrice());
					orderItem.setGoodsSinglePrice(item.getPrice());
					orderItem.setAttachName("");
					orderItem.setAttachPrice(0.0);
					orderItem.setGoodsId(item.getDishId());
					orderItem.setGoodsName(item.getDishName());
					orderItem.setTotalPrice(item.getPrice());
					orderItem.setGoodsId(item.getDishId());
					orderItem.setComboName(item.getDishName());
					comboList.add(orderItem);
				}
			}
		}
		if (comboList.size() == 0) {
			Toast.makeText(PhoneOrderActivity.this, "没有选择菜品",
					Toast.LENGTH_SHORT).show();
		} else {
			hasDish = true;
			allComboList.add(comboList);
			for (int i = 0; i < menuArray.size(); i++) {
				tempFragment = getFragWithposition(i);
				if (tempFragment.categoryId == null) {// 没有实例化
					continue;
				}
				tempFragment.mAdapter.resetDataDefault();// 清除选中效果
			}
		}

	}

	private OrderPhoneFragment getFragWithposition(int position) {
		OrderPhoneFragment mFragment = (OrderPhoneFragment) indicatorViewPager
				.getViewPager().getAdapter()
				.instantiateItem(viewPager, position);
		return mFragment;
	}

	@Override
	public void showData() {
		refreshData();
	}
}
