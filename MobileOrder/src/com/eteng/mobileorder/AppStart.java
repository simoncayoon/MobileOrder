package com.eteng.mobileorder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.utils.DownloadHelper;
import com.eteng.mobileorder.utils.NetController;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorPagerAdapter;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorViewPagerAdapter;

public class AppStart extends Activity {

	private static final String TAG = "AppStart";
	private static final String KEY_IS_FIRST_VISIT = "IS_FRIST_VISIT";

	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;
	private Dialog mDialog;
	private TextView rateTextView;
	private ProgressBar upAppBar;
	private SharedPreferences sp;

	private boolean isFistVisit = false;
	private boolean DIALOG_SHOW = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_start);
//		checkUpdate();
		sp = getSharedPreferences(Constants.SP_GENERAL_PROFILE_NAME,
				Context.MODE_PRIVATE);
		isFistVisit = sp.getBoolean(KEY_IS_FIRST_VISIT, false);
		if (isFistVisit) {// 加载引导页面
			ViewPager viewPager = (ViewPager) findViewById(R.id.guide_viewPager);
			Indicator indicator = (Indicator) findViewById(R.id.guide_indicator);
			indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
			inflate = LayoutInflater.from(getApplicationContext());
			indicatorViewPager.setAdapter(adapter);
		} else {// 加载启动页面
			ImageView actionImg = new ImageView(this);
			LayoutParams layoutParams = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			actionImg.setLayoutParams(layoutParams);
			// actionImg.setBackground(getResources().getDrawable(R.drawable.app_action_page));
			actionImg.setBackgroundResource(R.drawable.app_action_page);
			this.addContentView(actionImg, layoutParams);
			new Handler().postDelayed(new Runnable() {
				public void run() {
					/*
					 * Create an Intent that will start the Main WordPress
					 * Activity.
					 */
					Intent mainIntent = new Intent(AppStart.this,
							LoginActivity.class);
					AppStart.this.startActivity(mainIntent);
					AppStart.this.finish();
				}
			}, 2900); // 2900 for release
		}

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

	/**
	 * 检查更新
	 */
	void checkUpdate() {
		String updateUrl = Constants.HOST_HEAD;// 更新接口URL
		Uri.Builder updateBuilder = Uri.parse(updateUrl).buildUpon();
		JsonObjectRequest updateInfoRequest = new JsonObjectRequest(
				Request.Method.GET, updateBuilder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
//						startActivity(new Intent(AppStart.this,
//								MainNaviActivity.class));
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						// TODO Auto-generated method stub
						Toast.makeText(AppStart.this, "Nothing check",
								Toast.LENGTH_SHORT).show();
//						startActivity(new Intent(AppStart.this,
//								MainNaviActivity.class));
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
		if (code != 0) {
			final int netCode = object.getInt("version");// 网络版本号
			final String appUrl = object.getString("verUrl");// APP压缩包url地址
			final String msg = object.getString("explain");// 更新提示内容
			// 调用download方法开始下载
			setUpgrade(code, netCode, msg, 1, appUrl);
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
		if (netCode > code) {
			View view = LayoutInflater.from(AppStart.this).inflate(
					R.layout.upgrade_dialog, null);
			Button upButton = (Button) view.findViewById(R.id.btn_upgrade);
			// Button cancelButton = (Button)
			// view.findViewById(R.id.btn_cannel);
			TextView msgTextView = (TextView) view.findViewById(R.id.tv_msg);

			msgTextView.setText(msg);

			upButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mDialog.dismiss();
					View upView = LayoutInflater.from(AppStart.this).inflate(
							R.layout.upgrade_app_dialog, null);
					rateTextView = (TextView) upView.findViewById(R.id.tv_rate);
					upAppBar = (ProgressBar) upView
							.findViewById(R.id.pb_dialog);
					showProcessDialog(AppStart.this, upView);
					DownloadHelper.downloadApp(AppStart.this, url, mDialog,
							upAppBar, rateTextView);
				}
			});

			showProcessDialog(AppStart.this, view);
		} else {

		}
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
					finish();
					return true;
				}
				return false;
			}
			//

		};

		mDialog = new AlertDialog.Builder(mContext).create();
		mDialog.setOnKeyListener(keyListener);

		Window mWindow = ((Activity) mContext).getWindow();
		WindowManager.LayoutParams lp = mWindow.getAttributes();
		lp.x = Gravity.CENTER_HORIZONTAL; // 新位置X坐标
		lp.y = Gravity.BOTTOM; // 新位置Y坐标
		mDialog.onWindowAttributesChanged(lp);

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