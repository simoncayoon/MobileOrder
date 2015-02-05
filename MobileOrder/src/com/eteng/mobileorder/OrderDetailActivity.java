package com.eteng.mobileorder;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.adapter.DishComboAdapter;
import com.eteng.mobileorder.cusomview.ProgressHUD;
import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.cusomview.TopNavigationBar.NaviBtnListener;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.OrderDetailModel;
import com.eteng.mobileorder.models.OrderInfoModel;
import com.eteng.mobileorder.service.BlueToothService;
import com.eteng.mobileorder.utils.DisplayMetrics;
import com.eteng.mobileorder.utils.JsonUTF8Request;
import com.eteng.mobileorder.utils.NetController;

public class OrderDetailActivity extends Activity implements OnClickListener,
		NaviBtnListener {

	private static final String TAG = "OrderDetailActivity";
	private TextView addCombo, headerPhone, headerDate, headerAddr, totalPrice,
			dateView;
	private EditText telEditView, addrEditView;
	private TopNavigationBar topBar;
	private Button confirmBtn;
	private ListView mListView;
	private ArrayList<OrderDetailModel> dishCombo;
	private DishComboAdapter mAdapter;
	private MobileOrderApplication mApplication;
	// private LinearLayout confirmLayout;
	private OrderInfoModel orderInfo;
	private int orderID;
	private String orderSn = "";
	private boolean isFromWX = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_detail_layout);

		orderID = getIntent().getIntExtra("ORDER_DETAIL_ID", 0);
		if (getIntent().getBooleanExtra("IS_FROM_WX", false)) {
			isFromWX = true;
		}
		initHeader();
		initList();
		initData();
	}

	private void initList() {
		mListView = (ListView) findViewById(R.id.phone_order_dish_combo_list_view);
		mAdapter = new DishComboAdapter(OrderDetailActivity.this);
		mListView.setAdapter(mAdapter);
		// confirmLayout = (LinearLayout) findViewById(R.id.confirm_layout);
	}

	private void initHeader() {
		topBar = (TopNavigationBar) findViewById(R.id.general_navi_view);
		topBar.setTitle("订单详情");
		topBar.setLeftImg(R.drawable.setting_back_btn_bg);
		headerPhone = (TextView) findViewById(R.id.main_fragment_header_phone);
		headerDate = (TextView) findViewById(R.id.main_fragment_header_date);
		headerAddr = (TextView) findViewById(R.id.main_fragment_header_addr);
		addCombo = (TextView) findViewById(R.id.order_add_btn);
		Drawable addDrawable = getResources().getDrawable(
				R.drawable.main_fragment_add_combo_normal_icon);// 添加配餐按钮
		int addDrawableSize = DisplayMetrics.dip2px(this, 30);
		int drawableSize = DisplayMetrics.dip2px(this, 22);
		addDrawable.setBounds(0, 0, addDrawableSize, addDrawableSize);
		addCombo.setCompoundDrawables(addDrawable, null, null, null);
		addDrawable = getResources().getDrawable(R.drawable.header_tel_icon);// 电话图标
		addDrawable.setBounds(0, 0, drawableSize, drawableSize);
		headerPhone.setCompoundDrawables(addDrawable, null, null, null);
		addDrawable = getResources().getDrawable(R.drawable.header_date_icon);// 日期图标
		addDrawable.setBounds(0, 0, drawableSize, drawableSize);
		headerDate.setCompoundDrawables(addDrawable, null, null, null);
		addDrawable = getResources().getDrawable(R.drawable.header_addr_icon);// 地址图标
		addDrawable.setBounds(0, 0, drawableSize, drawableSize);
		headerAddr.setCompoundDrawables(addDrawable, null, null, null);
		telEditView = (EditText) findViewById(R.id.header_tel_edit_view);
		telEditView.setEnabled(false);
		dateView = (TextView) findViewById(R.id.header_date_edit_view);
		addrEditView = (EditText) findViewById(R.id.header_addr_edit_view);
		addrEditView.setEnabled(false);
		totalPrice = (TextView) findViewById(R.id.phone_order_combo_count);// 显示总价
		confirmBtn = (Button) findViewById(R.id.phone_order_commit_btn);
		confirmBtn.setOnClickListener(this);
	}

	void initData() {
		dishCombo = new ArrayList<OrderDetailModel>();
		mApplication = MobileOrderApplication.getInstance();
		getDishCombo();
	}

	private void getDishCombo() {
		final ProgressHUD mProgressHUD;
		mProgressHUD = ProgressHUD.show(OrderDetailActivity.this, getResources().getString(R.string.toast_remind_loading), true,
				true, null);
		String url = Constants.HOST_HEAD + Constants.ORDER_BY_ORDERID;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("orderId", String.valueOf(orderID));
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							if (respon.getString("code").equals("0")) {
								setHaderContent(new JSONObject(
										respon.getString("orderInfo")));// 设置头部信息
								setListContent(new JSONArray(
										respon.getString("orderDetailsList")));// 设置订单详情内容
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
					}
				});
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getMenuRequest, TAG);
	}

	private void setHaderContent(JSONObject jsonObj) throws JSONException {

		orderInfo = new OrderInfoModel();
		orderInfo.setAddress(jsonObj.getString("address"));
		orderInfo.setAddrId(jsonObj.getString("addressId"));
		orderInfo.setCreateTime(jsonObj.getString("createTime"));
		orderInfo.setOrderAddr(jsonObj.getString("orderAddress"));
		orderInfo.setOrderId(jsonObj.getString("orderId"));
		orderInfo.setOrderSn(jsonObj.getString("orderSn"));
		orderInfo.setOrderStatus(jsonObj.getString("orderStatus"));
		orderInfo.setOrderTel(jsonObj.getString("orderTel"));
		orderInfo.setTotalPay(jsonObj.getDouble("totalPay"));

		telEditView.setText(orderInfo.getOrderTel());
		dateView.setText(orderInfo.getCreateTime());
		addrEditView.setText(orderInfo.getOrderAddr());

		this.totalPrice.setText(String.format(
				getResources().getString(R.string.total_price_text),
				String.valueOf(orderInfo.getTotalPay())));
	}

	void setListContent(JSONArray jsonArray) throws JSONException {
		for (int i = 0; i < jsonArray.length(); i++) {
			OrderDetailModel temp = new OrderDetailModel();
			JSONObject item = new JSONObject(jsonArray.getString(i));
			temp.setAttachName(item.getString("goodsAttachName"));
			temp.setAskFor(item.getString("askFor"));
			temp.setTotalPrice(item.getDouble("totalPrice"));
			temp.setGoodsName(item.getString("goodsName"));

			if (temp.getAttachName().length() > 0) {// 设置菜单组合
				temp.setComboName(temp.getGoodsName() + " + "
						+ temp.getAttachName().replace(",", " + "));
			} else {
				temp.setComboName(temp.getGoodsName());
			}
			temp.setRemarkName(temp.getAskFor().replace(",", " + "));
			dishCombo.add(temp);
		}
		mListView.setAdapter(new DishComboAdapter(OrderDetailActivity.this,
				dishCombo));
	}

	@Override
	public void onClick(View v) {
		if (isFromWX) {
			// update order state
			try {
				updateOrderState();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {// 直接打印
			printAction();
		}

	}

	void printAction() {
		if (mApplication.getBTService().getState() == BlueToothService.STATE_CONNECTED) {
			mApplication.getBTService().PrintCharacters(
					getPrintString(dishCombo));
		} else {
			Toast.makeText(OrderDetailActivity.this, "请查看打印机状态!",
					Toast.LENGTH_SHORT).show();
		}
	}

	void updateOrderState() throws JSONException {
		final ProgressHUD mProgressHUD;
		mProgressHUD = ProgressHUD.show(OrderDetailActivity.this, getResources().getString(R.string.toast_remind_commiting), true,
				true, null);
		String url = Constants.HOST_HEAD + Constants.UPDATE_ORDER_STATUS;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("orderId", orderInfo.getOrderId());
		builder.appendQueryParameter("orderStatus", String.valueOf(1));
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							if (respon.getString("code").equals("0")) {
								Toast.makeText(OrderDetailActivity.this,
										"提交成功!", Toast.LENGTH_SHORT).show();
								printAction();
							} else {
								Toast.makeText(OrderDetailActivity.this,
										"提交失败!", Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						mProgressHUD.dismiss();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						Toast.makeText(OrderDetailActivity.this,
								"VolleyError 提交失败!", Toast.LENGTH_SHORT).show();
						mProgressHUD.dismiss();
					}
				});
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getMenuRequest, TAG);
	}

	@Override
	public void leftBtnListener() {
		finish();
	}

	@Override
	public void rightBtnListener() {

	}

	String getPrintString(ArrayList<OrderDetailModel> dataSrc) {
		if (!(dataSrc.size() > 0)) {
			return "";
		}
		String printString = "";
		StringBuilder sb = new StringBuilder();
		sb.append(getHeadString());
		for (OrderDetailModel item : dataSrc) {
			String temp = "";
			temp = "配餐：" + item.getComboName() + "\n" + "小计："
					+ item.getTotalPrice() + "\n" + "备注：" + "\r\n";
			sb.append(temp);
		}
		sb.append("\r\n\r\n\r\n");
		printString = sb.toString();
		return printString;
	}

	private String getHeadString() {
		String headerString = "";
		String orderId = "订单编号:" + orderSn + "\n";
		String tel = "电话：" + telEditView.getText() + "\n";
		String date = "时间：" + dateView.getText() + "\n";
		String addr = "地址：" + addrEditView.getText() + "\n";
		headerString = orderId + tel + date + addr + "\r\n";
		return headerString;
	}
}
