package com.eteng.mobileorder;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.cusomview.TopNavigationBar.NaviBtnListener;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.MenuItemModel;
import com.eteng.mobileorder.models.OrderDetailModel;
import com.eteng.mobileorder.utils.DisplayMetrics;
import com.eteng.mobileorder.utils.JsonUTF8Request;
import com.eteng.mobileorder.utils.NetController;
import com.eteng.mobileorder.utils.StringMaker;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

public class PhoneOrderActivity extends FragmentActivity implements
		OrderPhoneFragment.Callbacks, NaviBtnListener, OnClickListener {

	private static final String TAG = "PhoneOrderActivity";
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;
	private ViewPager viewPager;
	private ScrollIndicatorView indicator;
	private Button addToComboList;

	private ArrayList<MenuCategory> menuArray;
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
		menuArray = new ArrayList<MenuCategory>();
		comboList = new ArrayList<OrderDetailModel>();
		allComboList = new LinkedHashSet<ArrayList<OrderDetailModel>>();
		mAdapter = new MyAdapter(getSupportFragmentManager());
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
		viewPager = (ViewPager) findViewById(R.id.moretab_viewPager);
		indicator = (ScrollIndicatorView) findViewById(R.id.moretab_indicator);

		indicator.setScrollBar(new ColorBar(PhoneOrderActivity.this, Color.RED,
				5));
		// 设置滚动监听
		int selectColorId = R.color.tab_text_color_selector;
		int unSelectColorId = R.color.OPTION_TEXT_COLOR_DEFAULT;
		indicator.setOnTransitionListener(new OnTransitionTextListener()
				.setColorId(PhoneOrderActivity.this, selectColorId,
						unSelectColorId));
		viewPager.setOffscreenPageLimit(10);
		indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
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
			textView.setText(menuArray.get(position).getMenuName());
			textView.setPadding(30, 0, 30, 0);
			textView.setTextColor(getResources().getColor(
					R.color.GENERAL_TEXT_COLOR));
			textView.setTextSize(DisplayMetrics.sp2px(PhoneOrderActivity.this,
					8));
			return convertView;
		}

		@Override
		public Fragment getFragmentForPage(int position) {

			OrderPhoneFragment fragment = new OrderPhoneFragment();
			Bundle bundle = new Bundle();
			bundle.putInt(OrderPhoneFragment.INTENT_INT_CATEGORY_ID, menuArray
					.get(position).getMenuId());
			bundle.putBoolean(OrderPhoneFragment.INTENT_IS_NOODLE, menuArray
					.get(position).isNoodle());
			if (menuArray.get(position).isNoodle()) {// 保存粉面的ID
				Editor editor = getSharedPreferences(
						Constants.SP_GENERAL_PROFILE_NAME, Context.MODE_PRIVATE)
						.edit();
				editor.putString(Constants.NOODLE_ID,
						String.valueOf(menuArray.get(position).getMenuId()));
				editor.commit();
			}
			fragment.setArguments(bundle);
			return fragment;
		}

	}

	@Override
	public void onItemSelected(int position) {

	};

	/**
	 * 获取菜单类型
	 */
	void getMenuCategory() {
		String url = Constants.HOST_HEAD + Constants.MENU_BY_ID;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("sellerId", Constants.SELLER_ID);// 测试ID，以后用shareperference保存
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						menuArray = getMenuList(respon);
						indicatorViewPager.setAdapter(mAdapter);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						DebugFlags.logD(TAG, "oops!!! " + arg0.getMessage());
					}
				});
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getMenuRequest, TAG);
	}

	ArrayList<MenuCategory> getMenuList(JSONObject JsonString) {
		ArrayList<MenuCategory> dataList = new ArrayList<MenuCategory>();
		try {
			JSONArray jsonArray = new JSONArray(
					JsonString.getString("classList"));
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject tmp = new JSONObject(jsonArray.getString(i));
				MenuCategory item = new MenuCategory();
				item.setMenuId(tmp.getInt("classId"));
				item.setMenuName(tmp.getString("className"));
				if (tmp.getInt("isNoodle") == 1) {
					item.setNoodle(true);
				}
				dataList.add(item);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataList;
	}

	class MenuCategory {
		private String menuName;
		private int menuId;
		private boolean isNoodle = false;

		public String getMenuName() {
			return menuName;
		}

		public void setMenuName(String menuName) {
			this.menuName = menuName;
		}

		public int getMenuId() {
			return menuId;
		}

		public void setMenuId(int menuId) {
			this.menuId = menuId;
		}

		public boolean isNoodle() {
			return isNoodle;
		}

		public void setNoodle(boolean isNoodle) {
			this.isNoodle = isNoodle;
		}
	}

	@Override
	public void leftBtnListener() {
		if (hasDish) {
			Intent mIntent = new Intent();
			Bundle mBundle = new Bundle();
			mBundle.putParcelableArrayList(Constants.DISH_COMBO_RESULT,
					comboList);
			// mBundle.putParcelableArrayList(Constants.DISH_COMBO_RESULT_ATTACH,
			// attachList);
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
			if (tempFragment.categoryId == 0 || tempFragment.mAdapter == null) {// 没有实例化
				continue;
			}
			ArrayList<MenuItemModel> tempList = new ArrayList<MenuItemModel>();
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
					MenuItemModel mainDish = null;// 主食（一个）
					for (int j = 0; j < listSize; j++) {
						if (!tempList.get(j).getType().equals("1")) {
							attachStringList.add(tempList.get(j).getName());
							attachPrice += tempList.get(j).getPrice();
						} else {
							mainDish = tempList.get(j);
						}
						if (j < (listSize - 1)) {
							sb = sb.append(tempList.get(j).getName() + " + ");// 追加“+”
							totalPrice += tempList.get(j).getPrice();// 单项价格
							continue;
						}
						sb = sb.append(tempList.get(j).getName());
						totalPrice += tempList.get(j).getPrice();// 计算总价
					}
					orderItem.setAskFor(StringMaker.divWithSymbol(",",
							tempFragment.mRemarkAdapter.getSelectList()));// 填充备注信息
					orderItem
							.setGoodsDiscountPrice(mainDish.getDiscountPrice());
					orderItem.setGoodsSinglePrice(mainDish.getPrice());
					orderItem.setAttachName(StringMaker.divWithSymbol(",",
							attachStringList));// 附加产品组合
					orderItem.setAttachPrice(attachPrice);
					orderItem.setOrderId("");
					orderItem.setGoodsId(mainDish.getId());
					orderItem.setGoodsName(mainDish.getName());
					orderItem.setComboName(sb.toString());// 填充展示组合名称
					orderItem.setTotalPrice(totalPrice);// 填充单项订单总价
					orderItem.setRemarkName(StringMaker.divWithSymbol(" + ",
							tempFragment.mRemarkAdapter.getSelectList()));// 填充备注展示信息
					comboList.add(orderItem);
					tempFragment.mRemarkAdapter.resetDataDefault();
					continue;
				}
				for (MenuItemModel item : tempList) {
					OrderDetailModel orderItem = new OrderDetailModel();
					orderItem.setAskFor("");
					orderItem.setGoodsDiscountPrice(item.getDiscountPrice());
					orderItem.setGoodsSinglePrice(item.getPrice());
					orderItem.setAttachName("");
					orderItem.setAttachPrice(0.0);
					orderItem.setGoodsId(item.getId());
					orderItem.setGoodsName(item.getName());
					orderItem.setTotalPrice(item.getPrice());
					orderItem.setGoodsId(item.getId());
					orderItem.setComboName(item.getName());
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
			// comboList.clear();// 清空所选菜品
			for (int i = 0; i < menuArray.size(); i++) {
				tempFragment = getFragWithposition(i);
				if (tempFragment.categoryId == 0) {// 没有实例化
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
}
