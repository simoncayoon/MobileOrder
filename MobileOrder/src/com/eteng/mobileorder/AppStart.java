package com.eteng.mobileorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.server.PhoneCallListenerService;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorPagerAdapter;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorViewPagerAdapter;

public class AppStart extends Activity {

	private static final String TAG = "AppStart";
	

	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;

	private SharedPreferences sp;
	private boolean isFistVisit = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_start);
		sp = getSharedPreferences(Constants.SP_GENERAL_PROFILE_NAME,
				Context.MODE_PRIVATE);
		isFistVisit = sp.getBoolean(Constants.KEY_IS_FIRST_VISIT, true);
		
//		if (isFistVisit) {// 加载引导页面
//			ViewPager viewPager = (ViewPager) findViewById(R.id.guide_viewPager);
//			Indicator indicator = (Indicator) findViewById(R.id.guide_indicator);
//			indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
//			inflate = LayoutInflater.from(getApplicationContext());
//			indicatorViewPager.setAdapter(adapter);
//		} else {// 加载启动页面
			new Handler().postDelayed(new Runnable() {
				public void run() {
					/*
					 * Create an Intent that will start the Main WordPress
					 * Activity.
					 */
					Intent serviceIntent = new Intent(getApplicationContext(),
							PhoneCallListenerService.class);
					startService(serviceIntent);
					Intent mainIntent = new Intent(AppStart.this,
							LoginActivity.class);
					AppStart.this.startActivity(mainIntent);
					overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
					AppStart.this.finish();
				}
			}, 2000); // 2900 for release
			

//		}

	}
	
	private IndicatorPagerAdapter adapter = new IndicatorViewPagerAdapter() {
		private int[] images = new int[] { R.drawable.indicate_01,
				R.drawable.indicate_02, R.drawable.indicate_03,
				R.drawable.indicate_04, R.drawable.indicate_05 };

		@Override
		public View getViewForTab(int position, View convertView,
				ViewGroup container) {
			if (convertView == null) {
				// 设置显示图标
				convertView = inflate.inflate(R.layout.appstart_guide,
						container, false);
			}
			return convertView;
		}

		@Override
		public View getViewForPage(int position, View convertView,
				ViewGroup container) {
			if (convertView == null) {
				convertView = new View(getApplicationContext());
				convertView.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}
			convertView.setBackgroundResource(images[position]);
			return convertView;
		}

		@Override
		public int getCount() {
			return images.length;
		}
	};
}