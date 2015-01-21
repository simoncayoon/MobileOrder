package com.eteng.mobileorder;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;

public class SettingOwnProfile extends Activity implements
		TopNavigationBar.NaviBtnListener {

	private static final String TAG = "SettingOwnProfile";
	private static final String OWN_PROFILE_NAME = "own_profile_name";
	private static final String OWN_PROFILE_TEL = "own_profile_tel";
	private static final String OWN_PROFILE_DISH = "own_profile_dish";
	private static final String OWN_PROFILE_SCOPE = "own_profile_scope";
	private static final String OWN_PROFILE_ADDR = "own_profile_addr";

	private EditText nameEdit, telEdit, dishEdit, scopeEdit, addrEdit;
	private Button saveBtn;
	private SharedPreferences sp;
	private TopNavigationBar topBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_own_profile_layout);
		sp = getSharedPreferences(Constants.SP_OWN_PROFILE_NAME,
				Activity.MODE_PRIVATE);
		if (sp == null) {
			DebugFlags.logD(TAG, "sp null");
		} else {
			DebugFlags.logD(TAG, "sp null xxxx");
		}

		initView();
		initData();
	}

	private void initView() {
		topBar = (TopNavigationBar) findViewById(R.id.general_navi_view);
		nameEdit = (EditText) findViewById(R.id.setting_own_profile_name_edit);
		telEdit = (EditText) findViewById(R.id.setting_own_profile_tel_edit);
		dishEdit = (EditText) findViewById(R.id.setting_own_profile_dish_edit);
		scopeEdit = (EditText) findViewById(R.id.setting_own_profile_scope_edit);
		addrEdit = (EditText) findViewById(R.id.setting_own_profile_addr_edit);
		saveBtn = (Button) findViewById(R.id.setting_own_profile_save_btn);
	}

	void initData() {
		topBar.setTitle("我的资料");
		topBar.setLeftImg(getResources().getDrawable(
				R.drawable.setting_back_btn_bg));
		nameEdit.setText(sp.getString(OWN_PROFILE_NAME, ""));
		telEdit.setText(sp.getString(OWN_PROFILE_TEL, ""));
		dishEdit.setText(sp.getString(OWN_PROFILE_DISH, ""));
		scopeEdit.setText(sp.getString(OWN_PROFILE_SCOPE, ""));
		addrEdit.setText(sp.getString(OWN_PROFILE_ADDR, ""));
		saveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editRemoteInfo();
			}
		});
	}

	@Override
	public void leftBtnListener() {
		finish();
	}

	@Override
	public void rightBtnListener() {

	}
	
	void editRemoteInfo(){
		
	}
}
