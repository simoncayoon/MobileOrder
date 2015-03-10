package com.eteng.mobileorder;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;

import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.SellerInfoDao;
import com.eteng.mobileorder.utils.TempDataManager;

public class SettingOwnProfile extends Activity implements
		TopNavigationBar.NaviBtnListener {

	@SuppressWarnings("unused")
	private static final String TAG = "SettingOwnProfile";

	private EditText nameEdit, telEdit, dishEdit, scopeEdit, addrEdit;
	private Button saveBtn;
	private TopNavigationBar topBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_own_profile_layout);
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
		dishEdit.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
	}

	void initData() {
		topBar.setTitle("我的资料");
		topBar.setLeftImg(R.drawable.setting_back_btn_bg);
		getSellerInfo();

		saveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editRemoteInfo();
			}
		});
	}

	private void getSellerInfo() {
		SellerInfoDao infoDao = MobileOrderApplication.getInstance()
				.getDaoSession().getSellerInfoDao();
		Cursor cursor = infoDao.getDatabase().query(
				infoDao.getTablename(),
				infoDao.getAllColumns(),
				"SELLER_ID = "
						+ String.valueOf(TempDataManager.getInstance(this)
								.getSellerId()), null, null, null, null);
		cursor.moveToFirst();
		for (int i = 0; i < infoDao.getAllColumns().length; i++) {
			String columnName = cursor.getColumnName(cursor
					.getColumnIndex(infoDao.getAllColumns()[i]));
			String content = cursor.getString(cursor.getColumnIndex(infoDao
					.getAllColumns()[i]));
			if (columnName.equals("SELLER_NAME")) {
				nameEdit.setText(content);
			} else if (columnName.equals("SELLER_TEL")) {
				telEdit.setText(content);
			} else if (columnName.equals("SELLER_DETAIL")) {
				dishEdit.setText(content);
			} else if (columnName.equals("SELLER_SCOPE")) {
				scopeEdit.setText(content);
			} else if (columnName.equals("SELLER_ADDR")) {
				addrEdit.setText(content);
			}
		}
		
		cursor.close();
	}

	@Override
	public void leftBtnListener() {
		finish();
	}

	@Override
	public void rightBtnListener() {

	}

	void editRemoteInfo() {

	}
}
