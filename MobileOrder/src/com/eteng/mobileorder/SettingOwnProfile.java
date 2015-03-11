package com.eteng.mobileorder;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.cusomview.ProgressHUD;
import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.SellerInfo;
import com.eteng.mobileorder.models.SellerInfoDao;
import com.eteng.mobileorder.utils.DbHelper;
import com.eteng.mobileorder.utils.JsonPostRequest;
import com.eteng.mobileorder.utils.NetController;
import com.eteng.mobileorder.utils.TempDataManager;

public class SettingOwnProfile extends Activity implements
		TopNavigationBar.NaviBtnListener {

	@SuppressWarnings("unused")
	private static final String TAG = "SettingOwnProfile";

	private EditText nameEdit, telEdit, briefEdit, scopeEdit, addrEdit;
	private TextView shopName, shopTel, shopBrief, scope, addr;
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
		initTitle();

		topBar = (TopNavigationBar) findViewById(R.id.general_navi_view);
		nameEdit = (EditText) findViewById(R.id.setting_own_profile_name_edit);
		telEdit = (EditText) findViewById(R.id.setting_own_profile_tel_edit);
		briefEdit = (EditText) findViewById(R.id.setting_own_profile_dish_edit);
		scopeEdit = (EditText) findViewById(R.id.setting_own_profile_scope_edit);
		addrEdit = (EditText) findViewById(R.id.setting_own_profile_addr_edit);
		saveBtn = (Button) findViewById(R.id.setting_own_profile_save_btn);
		briefEdit.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
	}

	private void initTitle() {
		shopName = (TextView) findViewById(R.id.shop_name);
		shopTel = (TextView) findViewById(R.id.shop_tel);
		shopBrief = (TextView) findViewById(R.id.shop_brief);
		scope = (TextView) findViewById(R.id.scope);
		addr = (TextView) findViewById(R.id.addr);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int padding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 10, metrics);
		
		int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				22, metrics);
		Drawable img_shop_name = getResources().getDrawable(
				R.drawable.setting_own_profile_name_icon);
		Drawable img_shop_tel = getResources().getDrawable(
				R.drawable.setting_own_profile_tel_icon);
		Drawable img_shop_brief = getResources().getDrawable(
				R.drawable.setting_own_profile_dish_icon);
		Drawable img_shop_scope = getResources().getDrawable(
				R.drawable.setting_own_profile_scope_icon);
		Drawable img_shop_addr = getResources().getDrawable(
				R.drawable.setting_own_profile_addr_icon);
		img_shop_name.setBounds(0, 0, size, size);
		img_shop_tel.setBounds(0, 0, size, size);
		img_shop_brief.setBounds(0, 0, size, size);
		img_shop_scope.setBounds(0, 0, size, size);
		img_shop_addr.setBounds(0, 0, size, size);

		shopName.setCompoundDrawables(img_shop_name, null, null, null);
		shopTel.setCompoundDrawables(img_shop_tel, null, null, null);
		shopBrief.setCompoundDrawables(img_shop_brief, null, null, null);
		scope.setCompoundDrawables(img_shop_scope, null, null, null);
		addr.setCompoundDrawables(img_shop_addr, null, null, null);
		shopName.setCompoundDrawablePadding(padding);
		shopTel.setCompoundDrawablePadding(padding);
		shopBrief.setCompoundDrawablePadding(padding);
		scope.setCompoundDrawablePadding(padding);
		addr.setCompoundDrawablePadding(padding);
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
				briefEdit.setText(content);
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
		final ProgressHUD mProgressHUD = ProgressHUD.show(this, getResources()
				.getString(R.string.toast_remind_loading), true, false,
				new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {

					}
				});
		String url = Constants.HOST_HEAD + Constants.SELLER_INFO_EDIT;

		Map<String, String> params = new HashMap<String, String>();
		params.put("sellerId",
				String.valueOf(TempDataManager.getInstance(this).getSellerId()));
		params.put("sellerName", nameEdit.getText().toString());
		params.put("linkTel", telEdit.getText().toString());
		params.put("sellerDetail", briefEdit.getText().toString());
		params.put("sellerCircle", scopeEdit.getText().toString());
		params.put("address", addrEdit.getText().toString());
		JsonPostRequest getMenuRequest = new JsonPostRequest(
				Request.Method.POST, url, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							saveInfo(respon);
							Toast.makeText(SettingOwnProfile.this, "保存成功",
									Toast.LENGTH_SHORT).show();
							getSellerInfo();
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(SettingOwnProfile.this, "保存失败",
									Toast.LENGTH_SHORT).show();
						}
						mProgressHUD.dismiss();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						Toast.makeText(SettingOwnProfile.this, "保存失败",
								Toast.LENGTH_SHORT).show();
						mProgressHUD.dismiss();
					}
				}, params);

		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getMenuRequest, TAG);
	}

	void saveInfo(JSONObject respon) throws JSONException {

		JSONObject infoJson = new JSONObject(respon.getString("seller"));
		int id = infoJson.getInt("sellerId");
		String name = infoJson.getString("sellerName");
		String tel = infoJson.getString("linkTel");
		String detail = infoJson.getString("sellerDetail");
		String scope = infoJson.getString("sellerCircle");
		String addr = infoJson.getString("address");
		String imgPath = infoJson.getString("sellerImg");
		String account = infoJson.getString("sellerAacount");
		SellerInfo sellerInfo = new SellerInfo(id, name, tel, detail, scope,
				addr, imgPath, account);
		try {
			DbHelper.getInstance(this).saveSellerInfo(sellerInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
