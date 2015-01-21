package com.eteng.mobileorder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.eteng.mobileorder.utils.DisplayMetrics;

public class LoginActivity extends Activity {

	private TextView titleView;
	private EditText accountEdit, pwdEdit;
	private Button loginBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		titleView = (TextView) findViewById(R.id.login_title_view);
		Drawable titleIcon, accountLeft, pwdLeft;
		titleIcon = getResources().getDrawable(R.drawable.logo);
		titleIcon.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(this, 100),
				DisplayMetrics.dip2px(this, 100)));
		titleView.setCompoundDrawables(null, titleIcon, null, null);

		accountLeft = getResources().getDrawable(
				R.drawable.login_account_icon_left);
		accountEdit = (EditText) findViewById(R.id.login_account_edit_box);
		accountLeft.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(this, 18),
				DisplayMetrics.dip2px(this, 20)));
		accountEdit.setCompoundDrawables(accountLeft, null, null, null);

		pwdEdit = (EditText) findViewById(R.id.login_pwd_edit_box);
		pwdLeft = getResources().getDrawable(R.drawable.login_pwd_icon_left);
		pwdLeft.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(this, 18),
				DisplayMetrics.dip2px(this, 20)));
		pwdEdit.setCompoundDrawables(pwdLeft, null, null, null);

		loginBtn = (Button) findViewById(R.id.login_btn);
		loginBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this,
						MainNaviActivity.class));
			}
		});
	}
}
