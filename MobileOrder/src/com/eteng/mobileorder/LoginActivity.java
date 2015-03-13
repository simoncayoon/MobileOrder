package com.eteng.mobileorder;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.cusomview.ProgressHUD;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.SellerInfo;
import com.eteng.mobileorder.utils.DbHelper;
import com.eteng.mobileorder.utils.DisplayMetrics;
import com.eteng.mobileorder.utils.FileCacheManager;
import com.eteng.mobileorder.utils.JsonUTF8Request;
import com.eteng.mobileorder.utils.NetController;
import com.eteng.mobileorder.utils.TempDataManager;

public class LoginActivity extends Activity implements OnClickListener {

	private static final String TAG = "LoginActivity";

	private TextView titleView;
	private EditText accountEdit, pwdEdit;
	private Button loginBtn;
	private CheckBox saveCheck;

	private String accountName = "";
	private String pwd = "";
	private boolean isSaveState = false;
	private boolean isExit = false;

//	private SellerInfoDao sellerInfoDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		initView();
		initData();
	}

	private void initView() {
		titleView = (TextView) findViewById(R.id.login_title_view);
		accountEdit = (EditText) findViewById(R.id.login_account_edit_box);
		pwdEdit = (EditText) findViewById(R.id.login_pwd_edit_box);
		loginBtn = (Button) findViewById(R.id.login_btn);
		saveCheck = (CheckBox) findViewById(R.id.login_save_account);
	}

	private void initData() {
		Drawable titleIcon, accountLeft, pwdLeft, check;
		titleIcon = getResources().getDrawable(R.drawable.logo);
		titleIcon.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(this, 100),
				DisplayMetrics.dip2px(this, 100)));
		titleView.setCompoundDrawables(null, titleIcon, null, null);// 设置登陆LOGO
		accountLeft = getResources().getDrawable(
				R.drawable.login_account_icon_left);
		accountLeft.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(this, 18),
				DisplayMetrics.dip2px(this, 20)));
		accountEdit.setCompoundDrawables(accountLeft, null, null, null);// 设置账户图标
		pwdLeft = getResources().getDrawable(R.drawable.login_pwd_icon_left);
		pwdLeft.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(this, 18),
				DisplayMetrics.dip2px(this, 20)));
		pwdEdit.setCompoundDrawables(pwdLeft, null, null, null);// 设置密码图标
		check = getResources().getDrawable(R.drawable.login_check_selector);
		check.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(this, 18),
				DisplayMetrics.dip2px(this, 18)));
		saveCheck.setCompoundDrawables(check, null, null, null);// 设置密码图标
		loginBtn.setOnClickListener(this);

		isSaveState = TempDataManager.getInstance(this).getPwdSaveState();
		accountName = TempDataManager.getInstance(this).getAccountName();
		pwd = TempDataManager.getInstance(this).getPwd();
		if (isSaveState && !accountName.equals("") && !pwd.equals("")) {
			accountEdit.setText(accountName);
			pwdEdit.setText(pwd);
			saveCheck.setChecked(true);
		}
	}

	@Override
	public void onClick(View v) {
		if (accountEdit.getText().toString().length() == 0) {
			Toast.makeText(LoginActivity.this, "请输入账户信息！", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (pwdEdit.getText().toString().length() == 0) {
			Toast.makeText(LoginActivity.this, "请输入密码信息！", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		accountName = accountEdit.getText().toString();
		pwd = pwdEdit.getText().toString();
		loginRemote();
	}

	void loginRemote() {
		final ProgressHUD mProgressHUD;
		mProgressHUD = ProgressHUD.show(LoginActivity.this, getResources()
				.getString(R.string.toast_remind_logining), true, false,
				new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {

					}
				});

		String url = Constants.HOST_HEAD + Constants.LOGIN;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("account", accountName);
		builder.appendQueryParameter("pwd", pwd);
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							if (respon.getString("code").equals("0")) {

								TempDataManager.getInstance(LoginActivity.this)
										.setCurrentInfo(
												new JSONObject(respon
														.getString("seller"))
														.getLong("sellerId"),
												accountName, pwd);
								;
								if (saveCheck.isChecked()) {// 添加保存状态
									TempDataManager.getInstance(
											LoginActivity.this)
											.setPwdSaveState(true);
								} else {
									TempDataManager.getInstance(
											LoginActivity.this)
											.setPwdSaveState(false);
								}
								startActivity(new Intent(LoginActivity.this,
										MainNaviActivity.class));
								saveInfo(respon);
								finish();
							} else {
								Toast.makeText(
										LoginActivity.this,
										getResources()
												.getString(
														R.string.toast_remind_logining_failed),
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						mProgressHUD.dismiss();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						Toast.makeText(
								LoginActivity.this,
								getResources().getString(
										R.string.toast_remind_logining_failed),
								Toast.LENGTH_SHORT).show();
						mProgressHUD.dismiss();
					}
				});
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getMenuRequest, TAG);
	}

	/**
	 * 获取二维码信息
	 */
	protected void getQrCodeInfo(String imgPath) {
		TempDataManager.getInstance(this).setQrcodePath(imgPath);
		DebugFlags.logD(TAG, "imagePath " + imgPath);
		if (imgPath.equals("")) {// 远程没有二维码图片
			// 删除本地原有的相关二维码图片
			FileCacheManager.getInstance(this).deleteImgById(imgPath);
			return;
		}
		if (!FileCacheManager.getInstance(this).isExists(imgPath)) {// 本地不存在二维码图片
			FileCacheManager.getInstance(this).saveRemoteImg(imgPath);
		}
	}

	void saveInfo(JSONObject respon) throws JSONException {

		JSONObject infoJson = new JSONObject(respon.getString("seller"));
		SellerInfo currentSellerInfo = new SellerInfo();
		currentSellerInfo.setSellerId(infoJson.getLong("sellerId"));
		currentSellerInfo.setSellerName(infoJson.getString("sellerName"));
		currentSellerInfo.setSellerTel(infoJson.getString("linkTel"));
		currentSellerInfo.setSellerDetail(infoJson.getString("sellerDetail"));
		currentSellerInfo.setSellerScope(infoJson.getString("sellerCircle"));
		currentSellerInfo.setSellerAddr(infoJson.getString("address"));
		currentSellerInfo.setSellerImg(infoJson.getString("sellerImg"));
		currentSellerInfo.setSellerAccount(infoJson.getString("sellerAacount"));
		currentSellerInfo.setQrcodePath(infoJson.getString("qrcode"));
		currentSellerInfo.setQrcodeText(infoJson.getString("qrcodejs"));
		try {
			DbHelper.getInstance(this).saveSellerInfo(currentSellerInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		getQrCodeInfo(infoJson.getString("qrcode"));
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
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			System.exit(0);
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
}
