package com.eteng.mobileorder;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.utils.DisplayMetrics;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;
import com.shizhefei.view.indicator.IndicatorViewPager.OnIndicatorPageChangeListener;

public class MainNaviActivity extends FragmentActivity implements
		TopNavigationBar.NaviBtnListener {

	private static final String TAG = "MainNaviActivity";

	private IndicatorViewPager indicatorViewPager;
	private TopNavigationBar naviTitleView;

	private String[] tabNames;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.main_framework_layout);
		tabNames = new String[] { "电话订单", "微信订单", "已处理", "历史订单" };
		initView();
	}

	private void initView() {
		naviTitleView = (TopNavigationBar) findViewById(R.id.general_navi_view);
		ViewPager viewPager = (ViewPager) findViewById(R.id.tabmain_viewPager);
		Indicator indicator = (Indicator) findViewById(R.id.tabmain_indicator);
		indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
		indicatorViewPager
				.setAdapter(new MyAdapter(getSupportFragmentManager()));
		// 禁止viewpager的滑动事件
		viewPager.setCanScroll(false);
		// 设置viewpager保留界面不重新加载的页面数量
		viewPager.setOffscreenPageLimit(4);
		// 默认是1,，自动预加载左右两边的界面。设置viewpager预加载数为0。只加载加载当前界面。
		viewPager.setPrepareNumber(0);
		naviTitleView.setTitle(tabNames[0]);// 默认第一个页面
		naviTitleView.setRightImg(R.drawable.setting_btn_bg_selector);
		indicatorViewPager
				.setOnIndicatorPageChangeListener(new OnIndicatorPageChangeListener() {

					@Override
					public void onIndicatorPageChange(int preItem,
							int currentItem) {
						naviTitleView.setTitle(tabNames[currentItem]);
					}
				});
	}

	private class MyAdapter extends IndicatorFragmentPagerAdapter {
		private int[] tabIcons = { R.drawable.tab_order_phone_selector,
				R.drawable.tab_order_wx_selector,
				R.drawable.tab_has_deal_selector,
				R.drawable.tab_order_history_selector };
		private LayoutInflater inflater;

		public MyAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
			inflater = LayoutInflater.from(getApplicationContext());
		}

		@Override
		public int getCount() {
			return tabNames.length;
		}

		@Override
		public View getViewForTab(int position, View convertView,
				ViewGroup container) {
			if (convertView == null) {
				convertView = (TextView) inflater.inflate(
						R.layout.tab_text_view, container, false);
			}
			TextView textView = (TextView) convertView;
			textView.setText(tabNames[position]);
			textView.setTextSize((float) DisplayMetrics.sp2px(
					MainNaviActivity.this, 4));
			Drawable top = getResources().getDrawable(tabIcons[position]);
			top.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(
					MainNaviActivity.this, 30), DisplayMetrics.dip2px(
					MainNaviActivity.this, 30)));
			textView.setCompoundDrawables(null, top, null, null);
			return textView;
		}

		@Override
		public Fragment getFragmentForPage(int position) {
			BaseFragment tarFragment = null;
			switch (position) {
			case 0:
				tarFragment = new FragmentMain();
				break;
			case 1:
				tarFragment = new FragmentWxOrder();
				break;
			case 2:
				tarFragment = new FragmentHandled();
				break;
			case 3:
				tarFragment = new FragmentHistory();
				break;
			}
			return tarFragment;
		}
	}

	@Override
	public void leftBtnListener() {
		DebugFlags.logD(TAG, "leftBtnListener");
	}

	@Override
	public void rightBtnListener() {
		DebugFlags.logD(TAG, "test");
		// 跳转到设置页面
		startActivity(new Intent(MainNaviActivity.this, SettingActivity.class));
	}
}
