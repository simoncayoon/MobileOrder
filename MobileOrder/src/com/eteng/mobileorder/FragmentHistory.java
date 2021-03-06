package com.eteng.mobileorder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.adapter.OrderListAdapter;
import com.eteng.mobileorder.cusomview.DatePicker;
import com.eteng.mobileorder.cusomview.ProgressHUD;
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
	private DatePicker mDatePicker;
	private PopupWindow popPicker = null;

	private Calendar mCalendar;
	private ArrayList<OrderWXModel> orderDataList;
	private boolean isFromStart = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		orderDataList = new ArrayList<OrderWXModel>();
		mCalendar = Calendar.getInstance();
	}

	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(savedInstanceState);
		setContentView(R.layout.order_history_layout);
		initView();
	}

	@SuppressLint("SimpleDateFormat")
	private void initView() {
		orderCountView = (TextView) findViewById(R.id.general_order_layout_order_count);
		orderCountView.setText(String.format(Locale.CHINESE, getResources()
				.getString(R.string.order_count_text), "0"));// 初始化订单数量
		orderAmountView = (TextView) findViewById(R.id.general_order_layout_order_total_price);
		orderAmountView.setText(String.format(Locale.CHINA, getResources()
				.getString(R.string.order_total_price_text), "0"));// 初始化总金额

		String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(
				new Date()).toString();
		startBtn = (Button) findViewById(R.id.order_history_query_begin_btn);
		startBtn.setText(currentDate);
		endBtn = (Button) findViewById(R.id.order_history_query_end_btn);
		endBtn.setText(currentDate);
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
		case R.id.order_history_query_begin_btn:
			isFromStart = true;
			showDatePicker();
			break;
		case R.id.order_history_query_end_btn:
			isFromStart = false;
			showDatePicker();
			break;
		case R.id.order_history_query_btn:
			getHistoryData();
			break;
		default:
			break;
		}
	}

	private void getHistoryData() {
		final ProgressHUD mProgressHUD;
		mProgressHUD = ProgressHUD.show(getActivity(), getResources()
				.getString(R.string.toast_remind_loading), true, false, null);
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
				Constants.ORDER_QUERY_TYPE_HISTORY);
		builder.appendQueryParameter("page", "00");
		builder.appendQueryParameter("pageCount", "00");
		builder.appendQueryParameter("startDate", startBtn.getText().toString());
		builder.appendQueryParameter("endDate", endBtn.getText().toString());
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
		try {
			int orderId = orderDataList.get(position).getOrderId();
			Intent mIntent = new Intent(getActivity(),
					OrderDetailActivity.class);
			mIntent.putExtra("ORDER_DETAIL_ID", orderId);
			startActivity(mIntent);
		} catch (IndexOutOfBoundsException e) {
			return;
		}
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

	void showDatePicker() {
		DatePicker newView = new DatePicker(getActivity());
		popPicker = new PopupWindow(newView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, false);
		popPicker.setAnimationStyle(R.style.popwin_anim_style);
		// 设置点击窗口外边窗口消失
		popPicker.setOutsideTouchable(true);
		popPicker.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.general_dialog_bg));
		// 设置此参数获得焦点，否则无法点击
		popPicker.setFocusable(true);
		popPicker.showAtLocation(getActivity().getWindow().getDecorView(),
				Gravity.CENTER, 0, 0);
		mDatePicker = newView;
		popPicker.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				mCalendar.set(Calendar.YEAR, mDatePicker.getYear());
				mCalendar.set(Calendar.MONTH, mDatePicker.getMonth());
				mCalendar.set(Calendar.DAY_OF_MONTH, mDatePicker.getDay());
				if (isFromStart) {
					startBtn.setText(mDatePicker.getDate());
				} else {
					endBtn.setText(mDatePicker.getDate());
				}
			}
		});

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (popPicker != null) {
			popPicker.dismiss();
			popPicker = null;
		}
	}
}
