package com.eteng.mobileorder;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
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

public class FragmentHistory extends BaseFragment implements OnClickListener,
		OnItemClickListener {

	private static final String TAG = "FragmentHistory";

	private TextView orderCountView, orderAmountView;
	private ImageButton queryBtn;
	private Button startBtn, endBtn;
	private ListView mListView;
	
	private ArrayList<OrderWXModel> orderDataList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		orderDataList = new ArrayList<OrderWXModel>();
	}

	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(savedInstanceState);
		setContentView(R.layout.order_history_layout);
		initView();
	}

	private void initView() {
		orderCountView = (TextView) findViewById(R.id.general_order_layout_order_count);
		orderCountView.setText(String.format(Locale.CHINESE, getResources()
				.getString(R.string.order_count_text), "0"));// 初始化订单数量
		orderAmountView = (TextView) findViewById(R.id.general_order_layout_order_total_price);
		orderAmountView.setText(String.format(Locale.CHINA, getResources()
				.getString(R.string.order_total_price_text), "0"));// 初始化总金额

		startBtn = (Button) findViewById(R.id.order_history_query_begin_btn);
		endBtn = (Button) findViewById(R.id.order_history_query_end_btn);
		queryBtn = (ImageButton) findViewById(R.id.order_history_query_btn);
		startBtn.setOnClickListener(this);
		endBtn.setOnClickListener(this);
		queryBtn.setOnClickListener(this);

		mListView = (ListView) findViewById(R.id.order_history_list_view);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int vId = v.getId();
		switch (vId) {
		case R.id.order_history_query_begin_btn
				| R.id.order_history_query_end_btn:
			// 弹出日期选择界面
			break;
		case R.id.order_history_query_btn:
			getHistoryData();
			break;
		default:
			break;
		}
	}

	private void getHistoryData() {
		String url = Constants.HOST_HEAD + Constants.ORDER_BY_ID;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("sellerId", Constants.SELLER_ID);// 测试ID，以后用shareperference保存
		builder.appendQueryParameter("queryType",
				Constants.ORDER_QUERY_TYPE_HISTORY);
		builder.appendQueryParameter("page", "00");
		builder.appendQueryParameter("pageCount", "00");
		builder.appendQueryParameter("startDate", "2015-1-1");
		builder.appendQueryParameter("endDate", "2015-1-5");
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
												respon.getString("totalMoney"));
								orderAmountView.setText(strTotalMoney);// 设置总金额
								 getDataList(new JSONArray(respon
								 .getString("orderList")));
								 mListView.setAdapter(new OrderListAdapter(
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
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						DebugFlags.logD(TAG, "oops!!! " + arg0.getMessage());
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
		orderCountView.setText(style);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		

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
}
