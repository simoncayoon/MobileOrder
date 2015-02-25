package com.eteng.mobileorder;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.FragmentMain.GetCallNum;
import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.utils.DisplayMetrics;
import com.eteng.mobileorder.utils.DownloadHelper;
import com.eteng.mobileorder.utils.JsonUTF8Request;
import com.eteng.mobileorder.utils.NetController;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;
import com.shizhefei.view.indicator.IndicatorViewPager.OnIndicatorPageChangeListener;

public class MainNaviActivity extends FragmentActivity implements
		TopNavigationBar.NaviBtnListener, GetCallNum {

	@SuppressWarnings("unused")
	private static final String TAG = "MainNaviActivity";

	private IndicatorViewPager indicatorViewPager;
	private TopNavigationBar naviTitleView;
	private Dialog mDialog;
	private TextView rateTextView;
	private ProgressBar upAppBar;

	private String[] tabNames;
	private boolean isExit = false;
	private MyAdapter mAdapter;
	private String callNum = "";
	private boolean DIALOG_SHOW = false;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.main_framework_layout);
		tabNames = new String[] { "电话订单", "微信订单", "已处理", "历史订单" };
		initView();
		checkUpdate();
	}

	private void initView() {
		naviTitleView = (TopNavigationBar) findViewById(R.id.general_navi_view);
		ViewPager viewPager = (ViewPager) findViewById(R.id.tabmain_viewPager);
		Indicator indicator = (Indicator) findViewById(R.id.tabmain_indicator);
		mAdapter = new MyAdapter(getSupportFragmentManager());
		indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
		indicatorViewPager.setAdapter(mAdapter);
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
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
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
	}

	@Override
	public void rightBtnListener() {
		// 跳转到设置页面
		startActivity(new Intent(MainNaviActivity.this, SettingActivity.class));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			finish();
		}
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			isExit = false;
		}

	};

	protected void onNewIntent(Intent intent) {
		callNum = intent.getStringExtra("incoming_call_number");
	}

	@Override
	public String getCallNum() {
		return callNum;
	}

	/**
	 * 检查更新
	 */
	void checkUpdate() {
		String updateUrl = Constants.HOST_HEAD + Constants.GET_VERSION_INFO;// 更新接口URL
		Uri.Builder updateBuilder = Uri.parse(updateUrl).buildUpon();
		updateBuilder.appendQueryParameter("sysType", "1");// android
		JsonUTF8Request updateInfoRequest = new JsonUTF8Request(
				Request.Method.GET, updateBuilder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							parseData(respon);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {

					}
				});
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				updateInfoRequest, TAG);
	}

	/**
	 * 初始化数据
	 * 
	 * @throws JSONException
	 */
	private void parseData(JSONObject object) throws JSONException {
		final int code = getCurrentVersion();
		if (code == 0) {// 未能获取到本地版本号
			return;
		}
		if (object.getString("code").equals("0")) {// 查询成功
			JSONObject verInfo = new JSONObject(object.getString("version"));
			final int netCode = Integer.valueOf(verInfo
					.getString("updateVersionCode"));// 网络版本号
			if (netCode > code) {
				final String appUrl = verInfo.getString("dowloadPath");// APP压缩包url地址
				final String msg = verInfo.getString("versionNote");// 更新提示内容
				// 调用download方法开始下载
				setUpgrade(code, netCode, msg, 1, appUrl);
			}

		}
	}

	/**
	 * 判断是否更改下
	 * 
	 * @param code
	 *            本地版本号
	 * @param netCode
	 *            远程版本号
	 */
	private void setUpgrade(int code, int netCode, String msg, int upgradeType,
			String appUrl) {
		final String url = appUrl;
		DIALOG_SHOW = true;
		// 判断是否存在更新
		/*
		 * netCode=0; code=1;
		 */
		final View view = LayoutInflater.from(MainNaviActivity.this).inflate(
				R.layout.upgrade_dialog, null);
		final Button upButton = (Button) view.findViewById(R.id.btn_upgrade);
		// Button cancelButton = (Button)
		// view.findViewById(R.id.btn_cannel);
		TextView msgTextView = (TextView) view.findViewById(R.id.tv_msg);

		msgTextView.setText(msg);

		upButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				upButton.setVisibility(View.INVISIBLE);
				view.findViewById(R.id.progressbar_layout).setVisibility(View.VISIBLE);
				rateTextView = (TextView) view.findViewById(R.id.tv_rate);
				upAppBar = (ProgressBar) view.findViewById(R.id.pb_dialog);
				DownloadHelper.downloadApp(getApplicationContext(), url,
						mDialog, upAppBar, rateTextView);
			}
		});

		showProcessDialog(MainNaviActivity.this, view);

	}

	/**
	 * 更新对话框
	 * 
	 * @param mContext
	 * @param layout
	 */
	public void showProcessDialog(Context mContext, View layout) {
		OnKeyListener keyListener = new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface arg0, int keyCode,
					KeyEvent arg2) {
				if (keyCode == KeyEvent.KEYCODE_HOME
						|| keyCode == KeyEvent.KEYCODE_SEARCH) {
					return true;
				}
				if (keyCode == KeyEvent.KEYCODE_BACK) {
//					mDialog.dismiss();
					arg0.dismiss();
					return true;
				}
				return false;
			}
			//

		};

		mDialog = new AlertDialog.Builder(mContext).create();
		mDialog.setOnKeyListener(keyListener);

		Window mWindow = MainNaviActivity.this.getWindow();
		WindowManager.LayoutParams lp = mWindow.getAttributes();
		lp.x = Gravity.CENTER_HORIZONTAL; // 新位置X坐标
		lp.y = Gravity.BOTTOM; // 新位置Y坐标
		mDialog.onWindowAttributesChanged(lp);
		mDialog.setCancelable(false);
		mDialog.show();
		mDialog.setContentView(layout);
	}

	/**
	 * 获取当前版本号
	 */
	public int getCurrentVersion() {
		try {
			PackageInfo info = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
