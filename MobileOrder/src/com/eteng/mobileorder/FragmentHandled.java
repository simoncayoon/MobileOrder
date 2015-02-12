package com.eteng.mobileorder;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.adapter.OrderListAdapter;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.OrderWXModel;
import com.eteng.mobileorder.utils.JsonUTF8Request;
import com.eteng.mobileorder.utils.NetController;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class FragmentHandled extends BaseFragment implements
		OnRefreshListener<ListView>, OnItemClickListener {

	private static final String TAG = "FragmentWxOrder";
	private TextView orderCount, totalPrice;
	private PullToRefreshListView mList;
	private ArrayList<OrderWXModel> orderDataList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(savedInstanceState);
		setContentView(R.layout.general_order_list_layout);
		orderDataList = new ArrayList<OrderWXModel>();
		initView();
	}

	private void initView() {
		orderCount = (TextView) findViewById(R.id.general_order_layout_order_count);
		orderCount.setText(String.format(Locale.CHINESE, getResources()
				.getString(R.string.order_count_text), "0"));
		totalPrice = (TextView) findViewById(R.id.general_order_layout_order_total_price);
		totalPrice.setText(String.format(Locale.CHINA, getResources()
				.getString(R.string.order_total_price_text), "0"));
		mList = (PullToRefreshListView) findViewById(R.id.general_pull_refresh_list);
		mList.setOnRefreshListener(this);
		mList.setOnItemClickListener(this);
	}

	private void getData() {
		String url = Constants.HOST_HEAD + Constants.ORDER_BY_ID;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter(
				"sellerId",
				getActivity()
						.getSharedPreferences(
								Constants.SP_GENERAL_PROFILE_NAME,
								Context.MODE_PRIVATE).getString(
								Constants.SP_SELLER_ID, ""));
		builder.appendQueryParameter("queryType",
				Constants.ORDER_QUERY_TYPE_DAY);
		builder.appendQueryParameter("page", "00");
		builder.appendQueryParameter("pageCount", "00");
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							if (respon.getString("code").equals("0")) {
								setOrderCount(respon.getString("totalCount"));
								String strTotalMoney = String
										.format(Locale.CHINA,
												getResources()
														.getString(
																R.string.order_total_price_text),
												respon.getString("totalMoney"));// 设置总金额
								totalPrice.setText(strTotalMoney);// 设置总单数
								getDataList(new JSONArray(respon
										.getString("orderList")));
								mList.setAdapter(new OrderListAdapter(
										getActivity(), orderDataList));
							} else {
								DebugFlags.logD(
										TAG,
										"oops! the server msg is :"
												+ respon.getString("msg"));
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mList.onRefreshComplete();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						DebugFlags.logD(TAG, "oops!!! " + arg0.getMessage());
						mList.onRefreshComplete();
					}
				});
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getMenuRequest, TAG);
	}

	void setOrderCount(String count) {
		String str = String.format(Locale.CHINESE,
				getResources().getString(R.string.order_count_text), count);
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.setSpan(new ForegroundColorSpan(Color.RED), 3, str.length() - 1,
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		style.setSpan(new RelativeSizeSpan(1.0f), 3, str.length() - 1,
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		orderCount.setText(style);
	}

	/**
	 * 将JSON中的数据解析到列表
	 * 
	 * @param jsonArray
	 * @throws JSONException
	 */
	void getDataList(JSONArray jsonArray) throws JSONException {
		orderDataList.clear();
		for (int i = 0; i < jsonArray.length(); i++) {
			OrderWXModel temp = new OrderWXModel();
			JSONObject item = new JSONObject(jsonArray.get(i).toString());
			temp.setAddressId(item.getInt("addressId"));
			temp.setBuyerUserId(item.getInt("buyerUserId"));
			temp.setCreateTime(item.getString("createTime"));
			temp.setOrderAddress(item.getString("orderAddress"));
			temp.setOrderId(item.getInt("orderId"));
			temp.setOrderOpenId(item.getString("orderOpenId"));
			temp.setOrderSn(item.getString("orderSn"));
			temp.setOrderSource(item.getString("orderSource"));
			temp.setOrderStatus(item.getString("orderStatus"));
			temp.setOrderTel(item.getString("orderTel"));
			temp.setSellerUserId(item.getInt("sellerUserId"));
			temp.setShouldPay(item.getDouble("shouldPay"));
			temp.setTotalPay(item.getDouble("totalPay"));
			orderDataList.add(temp);
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(getApplicationContext(),
				System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
						| DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		getData();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 跳转到详细页面
		try {
			int orderId = orderDataList.get(position - 1).getOrderId();
			Intent mIntent = new Intent(getActivity(),
					OrderDetailActivity.class);
			mIntent.putExtra("ORDER_DETAIL_ID", orderId);
			startActivity(mIntent);
		} catch (IndexOutOfBoundsException e) {
			return;
		}

	}
}
