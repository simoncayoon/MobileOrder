package com.eteng.mobileorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.cusomview.ProgressHUD;
import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.cusomview.TopNavigationBar.NaviBtnListener;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.CustomerInfoModel;
import com.eteng.mobileorder.utils.JsonUTF8Request;
import com.eteng.mobileorder.utils.NetController;

public class CustomerInfoActivity extends Activity implements NaviBtnListener {

	private static final String TAG = CustomerInfoActivity.class.getName();

	private TopNavigationBar topBar;
	private ListView contentList;
	private Button commitBtn;

	@SuppressWarnings("unused")
	private ArrayList<CustomerInfoModel> dataList;
	private ArrayList<Map<String, String>> dataMaps;
	SimpleAdapter mAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customer_info_layout);
		topBar = (TopNavigationBar) findViewById(R.id.general_navi_view);
		topBar.setTitle("客户信息");
		topBar.setLeftImg(R.drawable.setting_back_btn_bg);
		contentList = (ListView) findViewById(R.id.general_pull_refresh_list);
		commitBtn = (Button) findViewById(R.id.setting_own_profile_save_btn);
		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getData();
				mAdapter.notifyDataSetChanged();
			}
		});

		dataList = new ArrayList<CustomerInfoModel>();
		dataMaps = new ArrayList<Map<String, String>>();
		getData();
	}

	private void getData() {
		final ProgressHUD mProgressHUD;
		mProgressHUD = ProgressHUD.show(CustomerInfoActivity.this,
				getResources().getString(R.string.toast_remind_loading), true,
				false, new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {

					}
				});
		String url = Constants.HOST_HEAD + Constants.QUERY_CUSTOMER_INFO;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("sellerId", Constants.SELLER_ID);// 测试ID，以后用shareperference保存
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							if (respon.getString("code").equals("0")) {
								parseSrcData(respon.getString("customerList"));
								mAdapter = new SimpleAdapter(
										CustomerInfoActivity.this,
										dataMaps,
										R.layout.customer_info_list_item_layout,
										new String[] { "tel", "address" },
										new int[] { R.id.customer_tel_view,
												R.id.customer_addr_view });
								contentList.setAdapter(mAdapter);
							} else {
								DebugFlags.logD(
										TAG,
										"oops! the server msg is :"
												+ respon.getString("msg"));
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
						mProgressHUD.dismiss();
					}
				});
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getMenuRequest, TAG);

	}

	private void parseSrcData(String data) throws JSONException {
		JSONArray dataList = new JSONArray(data);
		dataMaps.clear();
		for (int i = 0; i < dataList.length(); i++) {
			JSONObject temp = new JSONObject(dataList.getString(i));
			// CustomerInfoModel item = new CustomerInfoModel();
			// item.setTel(temp.getString("tel"));
			// item.setAddr(temp.getString("address"));
			// item.setTotalNumber(temp.getString("totalNumber"));
			// item.setTotalMoney(temp.getString("totalMoney"));
			// this.dataList.add(item);
			Map<String, String> mapItem = new HashMap<String, String>();
			mapItem.put("tel", temp.getString("tel"));
			mapItem.put("address", temp.getString("address"));
			dataMaps.add(mapItem);
		}
	}

	@Override
	public void leftBtnListener() {
		finish();

	}

	@Override
	public void rightBtnListener() {

	}
}
