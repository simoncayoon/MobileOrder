package com.eteng.mobileorder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.eteng.mobileorder.utils.DisplayMetrics;
import com.eteng.mobileorder.utils.JsonUTF8Request;
import com.eteng.mobileorder.utils.NetController;

public class LoginActivity extends Activity implements OnClickListener {

	private static final String TAG = "LoginActivity";
	private static final String SP_SAVE_PWD_STATE = "sp_save_pwd_state";
	private static final String SP_LOGIN_ACCOUNT = "sp_login_account";
	private static final String SP_LOGIN_PWD = "sp_login_pwd";

	private TextView titleView;
	private EditText accountEdit, pwdEdit;
	private Button loginBtn;
	private CheckBox saveCheck;

	private String accountName = "";
	private String pwd = "";
	private boolean isSaveState = false;

	private SharedPreferences sp;

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

		sp = getSharedPreferences(Constants.SP_GENERAL_PROFILE_NAME,
				Context.MODE_PRIVATE);
		isSaveState = sp.getBoolean(SP_SAVE_PWD_STATE, false);
		accountName = sp.getString(SP_LOGIN_ACCOUNT, "");
		pwd = sp.getString(SP_LOGIN_PWD, "");
		if (isSaveState) {
			accountEdit.setText(accountName);
			pwd = sp.getString(SP_LOGIN_PWD, "");
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
		mProgressHUD = ProgressHUD.show(LoginActivity.this, "正在登陆", true, true,
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
//						DebugFlags.logD(TAG, "JSON String" + respon);
						try {
							if (respon.getString("code").equals("0")) {
								Editor inputSp = sp.edit();
								inputSp.putString(SP_LOGIN_ACCOUNT, accountName);
								inputSp.putString(SP_LOGIN_PWD, pwd);
								inputSp.putString(
										Constants.SELLER_ID,
										new JSONObject(respon
												.getString("seller"))
												.getString("sellerId"));
								if (saveCheck.isChecked()) {// 添加保存状态
									inputSp.putBoolean(SP_SAVE_PWD_STATE, true);
								} else {
									inputSp.putBoolean(SP_SAVE_PWD_STATE, false);
								}
								inputSp.commit();
								startActivity(new Intent(LoginActivity.this,
										MainNaviActivity.class));
								finish();
							} else {
								Toast.makeText(LoginActivity.this, "登陆失败",
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
						DebugFlags.logD(TAG, "oops!!! " + arg0.getMessage());
						Toast.makeText(LoginActivity.this, "登陆失败",
								Toast.LENGTH_SHORT).show();
						mProgressHUD.dismiss();
					}
				});
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getMenuRequest, TAG);
	}
}
