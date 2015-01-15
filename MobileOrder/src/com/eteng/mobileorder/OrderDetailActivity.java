package com.eteng.mobileorder;

import java.util.ArrayList;
import java.util.Date;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.adapter.DishComboAdapter;
import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.cusomview.TopNavigationBar.NaviBtnListener;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.MenuItemModel;
import com.eteng.mobileorder.service.BlueToothService;
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
	private ArrayList<MenuItemModel> dishCombo;
	private DishComboAdapter mAdapter;
	private MobileOrderApplication mApplication;
	private LinearLayout confirmLayout;
	private int orderID;
	private String orderSn = "";
	private boolean isFromWX = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_detail_layout);

		orderID = getIntent().getIntExtra("ORDER_DETAIL_ID", 0);
		if(getIntent().getBooleanExtra("IS_FROM_WX", false)){
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
		confirmLayout = (LinearLayout) findViewById(R.id.confirm_layout);
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
				R.drawable.main_fragment_add_combo_icon);// 添加配餐按钮
		addDrawable.setBounds(0, 0, 35, 35);
		addCombo.setCompoundDrawables(addDrawable, null, null, null);
		addDrawable = getResources().getDrawable(R.drawable.header_tel_icon);// 电话图标
		addDrawable.setBounds(0, 0, 25, 25);
		headerPhone.setCompoundDrawables(addDrawable, null, null, null);
		addDrawable = getResources().getDrawable(R.drawable.header_date_icon);// 日期图标
		addDrawable.setBounds(0, 0, 25, 25);
		headerDate.setCompoundDrawables(addDrawable, null, null, null);
		addDrawable = getResources().getDrawable(R.drawable.header_addr_icon);// 地址图标
		addDrawable.setBounds(0, 0, 25, 25);
		headerAddr.setCompoundDrawables(addDrawable, null, null, null);

		telEditView = (EditText) findViewById(R.id.header_tel_edit_view);
		telEditView.setEnabled(false);
		dateView = (TextView) findViewById(R.id.header_date_edit_view);
		Date data = new Date();
		// dateView.setText(new SimpleDateFormat("yy/MM/dd").format(data));
		addrEditView = (EditText) findViewById(R.id.header_addr_edit_view);
		addrEditView.setEnabled(false);
		totalPrice = (TextView) findViewById(R.id.phone_order_combo_count);// 显示总价
		confirmBtn = (Button) findViewById(R.id.phone_order_commit_btn);
		confirmBtn.setOnClickListener(this);
	}

	void initData() {
		dishCombo = new ArrayList<MenuItemModel>();
		DebugFlags.logD(TAG, "接收订单ID" + orderID);
		mApplication = MobileOrderApplication.getInstance();
		getDishCombo();
	}

	private void getDishCombo() {
		String url = Constants.HOST_HEAD + Constants.ORDER_BY_ORDERID;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("orderId", String.valueOf(orderID));
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						DebugFlags.logD(TAG, "JSON String" + respon);
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
		orderSn = jsonObj.getString("orderSn");
		telEditView.setText(jsonObj.getString("orderTel"));
		dateView.setText(jsonObj.getString("besureTime"));
		addrEditView.setText(jsonObj.getString("orderAddress"));
		this.totalPrice.setText(String.format(
				getResources().getString(R.string.total_price_text),
				jsonObj.getString("totalPay")));
	}

	void setListContent(JSONArray jsonArray) throws JSONException {
		for (int i = 0; i < jsonArray.length(); i++) {
			MenuItemModel temp = new MenuItemModel();
			JSONObject item = new JSONObject(jsonArray.getString(i));
			temp.setDiscountPrice(item.getDouble("goodsDiscountPrice"));
			temp.setItemPrice(item.getDouble("totalPrice"));
			temp.setName(item.getString("goodsName")
					+ item.getString("goodsAttachName").replace(",", "+"));
			dishCombo.add(temp);
		}
		mListView.setAdapter(new DishComboAdapter(OrderDetailActivity.this,
				dishCombo));
	}

	@Override
	public void onClick(View v) {
		if (isFromWX) {
			// update order state
			if (updateOrderState()) {// 更新状态成功
				Toast.makeText(OrderDetailActivity.this, "提交订单成功",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(OrderDetailActivity.this, "未能提交订单",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}
		if (mApplication.getBTService().getState() == BlueToothService.STATE_CONNECTED) {
			mApplication.getBTService().PrintCharacters(
					getPrintString(dishCombo));
		} else {
			Toast.makeText(OrderDetailActivity.this, "请查看打印机状态!",
					Toast.LENGTH_SHORT).show();
		}
	}

	boolean updateOrderState() {
		return true;
	}

	@Override
	public void leftBtnListener() {
		finish();
	}

	@Override
	public void rightBtnListener() {
		// TODO Auto-generated method stub

	}

	String getPrintString(ArrayList<MenuItemModel> dataSrc) {
		if (!(dataSrc.size() > 0)) {
			return "";
		}
		String printString = "";
		StringBuilder sb = new StringBuilder();
		sb.append(getHeadString());
		for (MenuItemModel item : dataSrc) {
			String temp = "";
			temp = "配餐：" + item.getName() + "\n" + "小计：" + item.getItemPrice()
					+ "\n" + "备注：" + "\r\n";
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
