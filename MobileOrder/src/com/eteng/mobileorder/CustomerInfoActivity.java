package com.eteng.mobileorder;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.cusomview.ProgressHUD;
import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.cusomview.TopNavigationBar.NaviBtnListener;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.CustomerInfo;
import com.eteng.mobileorder.utils.DbHelper;
import com.eteng.mobileorder.utils.JsonUTF8Request;
import com.eteng.mobileorder.utils.NetController;

public class CustomerInfoActivity extends Activity implements NaviBtnListener {

	private static final String TAG = CustomerInfoActivity.class.getName();
	private static final String GET_LOCAL = "data_from_local";

	private TopNavigationBar topBar;
	private ListView contentList;
	private Button commitBtn;

	private ArrayList<CustomerInfo> dataList;
	// private ArrayList<Map<String, String>> dataMaps;
	BaseAdapter mAdapter = null;

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
				getData("");
				mAdapter.notifyDataSetChanged();
			}
		});

		dataList = new ArrayList<CustomerInfo>();
		// dataMaps = new ArrayList<Map<String, String>>();
		getData(GET_LOCAL);
	}

	private void getData(String flag) {
		if (flag.equals(GET_LOCAL)) {
			setAdapter();
		} else {
			final ProgressHUD mProgressHUD;
			mProgressHUD = ProgressHUD.show(CustomerInfoActivity.this,
					getResources().getString(R.string.toast_remind_loading),
					true, false, new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {

						}
					});
			String url = Constants.HOST_HEAD + Constants.QUERY_CUSTOMER_INFO;
			Uri.Builder builder = Uri.parse(url).buildUpon();
			builder.appendQueryParameter(
					"sellerId",
					getSharedPreferences(Constants.SP_GENERAL_PROFILE_NAME,
							Context.MODE_PRIVATE).getString(
							Constants.SP_SELLER_ID, ""));
			JsonUTF8Request getMenuRequest = new JsonUTF8Request(
					Request.Method.GET, builder.toString(), null,
					new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject respon) {
							try {
								if (respon.getString("code").equals("0")) {
									parseSrcData(respon
											.getString("customerList"));
									setAdapter();
								} else {
									DebugFlags.logD(TAG,
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
			NetController.getInstance(getApplicationContext())
					.addToRequestQueue(getMenuRequest, TAG);
		}

	}

	private void parseSrcData(String data) throws JSONException {
		JSONArray dataListJson = new JSONArray(data);
		dataList.clear();
		for (int i = 0; i < dataListJson.length(); i++) {
			JSONObject temp = new JSONObject(dataListJson.getString(i));
			CustomerInfo item = new CustomerInfo();
			item.setCustomerTel(temp.getString("tel"));
			item.setCustomerAddr(temp.getString("address"));
			dataList.add(item);
		}
		DbHelper.getInstance(this).saveCustomerInfos(dataList);// 保存客户信息
	}

	void setAdapter() {
		dataList = (ArrayList<CustomerInfo>) DbHelper.getInstance(this)
				.getCustomerInfos();
		mAdapter = new CustomerAdapter();
		contentList.setAdapter(mAdapter);
	}

	@Override
	public void leftBtnListener() {
		finish();

	}

	@Override
	public void rightBtnListener() {

	}

	class CustomerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.customer_info_list_item_layout, null);
				viewHolder.viewTel = (TextView) convertView
						.findViewById(R.id.customer_tel_view);
				viewHolder.viewAddr = (TextView) convertView
						.findViewById(R.id.customer_addr_view);
				convertView.setTag(viewHolder);
			}
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.viewTel.setText(dataList.get(position).getCustomerTel());
			viewHolder.viewAddr.setText(dataList.get(position)
					.getCustomerAddr());
			return convertView;
		}

		class ViewHolder {
			TextView viewTel, viewAddr;
		}
	}
}
