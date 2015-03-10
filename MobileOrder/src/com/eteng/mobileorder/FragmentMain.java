package com.eteng.mobileorder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
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
import com.eteng.mobileorder.cusomview.ProgressHUD;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.CustomerInfo;
import com.eteng.mobileorder.models.OrderDetailModel;
import com.eteng.mobileorder.models.OrderInfoModel;
import com.eteng.mobileorder.service.BlueToothService;
import com.eteng.mobileorder.utils.DbHelper;
import com.eteng.mobileorder.utils.DisplayMetrics;
import com.eteng.mobileorder.utils.JsonPostRequest;
import com.eteng.mobileorder.utils.NetController;
import com.eteng.mobileorder.utils.PrintHelper;

public class FragmentMain extends BaseFragment implements OnClickListener,
		OnItemLongClickListener {

	private static final String TAG = "FragmentMain";

	private TextView addCombo, headerPhone, headerDate, headerAddr, totalPrice,
			dateView;
	private EditText telEditView, addrEditView;
	private Button confirmBtn;
	private ListView mListView;
	private LinearLayout confirmLayout;

	private ArrayList<OrderDetailModel> dishCombo;
	private DishComboAdapter mAdapter;
	private MobileOrderApplication mApplication;
	private OrderInfoModel mOrderInfo;

	private Double totalPriceNum = 0.0;
	public String callNumber = "";
	public String callAddr = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dishCombo = new ArrayList<OrderDetailModel>();
	}

	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		super.onCreateView(savedInstanceState);
		setContentView(R.layout.main_fragment_container);
		initHeader();
		initList();
		initData();
	}

	private void initList() {
		mListView = (ListView) findViewById(R.id.phone_order_dish_combo_list_view);
		mAdapter = new DishComboAdapter(getActivity());
		mListView.setAdapter(mAdapter);
		mListView.setOnItemLongClickListener(this);
		confirmLayout = (LinearLayout) findViewById(R.id.confirm_layout);
	}

	@SuppressLint("SimpleDateFormat")
	private void initHeader() {
		headerPhone = (TextView) getContentView().findViewById(
				R.id.main_fragment_header_phone);
		headerDate = (TextView) getContentView().findViewById(
				R.id.main_fragment_header_date);
		headerAddr = (TextView) getContentView().findViewById(
				R.id.main_fragment_header_addr);
		addCombo = (TextView) getContentView().findViewById(R.id.order_add_btn);
		Drawable addDrawable = getResources().getDrawable(
				R.drawable.main_fragment_add_combo_normal_icon_selector);// 添加配餐按钮
		int addDrawableSize = DisplayMetrics.dip2px(getActivity(), 30);
		int drawableSize = DisplayMetrics.dip2px(getActivity(), 22);
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
		addCombo.setOnClickListener(this);

		telEditView = (EditText) findViewById(R.id.header_tel_edit_view);
		dateView = (TextView) findViewById(R.id.header_date_edit_view);
		Date data = new Date();
		dateView.setText(new SimpleDateFormat("yy/MM/dd").format(data));
		addrEditView = (EditText) findViewById(R.id.header_addr_edit_view);
		totalPrice = (TextView) findViewById(R.id.phone_order_combo_count);// 显示总价
		confirmBtn = (Button) findViewById(R.id.phone_order_commit_btn);
		confirmBtn.setOnClickListener(this);
	}

	void initData() {
		mApplication = MobileOrderApplication.getInstance();
		if (!(dishCombo.size() > 0)) {
			confirmLayout.setVisibility(View.INVISIBLE);
		}

		telEditView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				callNumber = s.toString();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		addrEditView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				callAddr = s.toString();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onClick(View v) {
		int vId = v.getId();
		if (vId == R.id.order_add_btn) {
			startActivityForResult(new Intent(getActivity(),
					PhoneOrderActivity.class), Constants.REQUEST_CODE);
		}
		if (vId == R.id.phone_order_commit_btn) {// 提交订单&打印订单
			/**
			 * 步骤：1、判断可否打印 2、网络提交返回 3、打印
			 */

			if (telEditView.getText().toString().length() == 0) {// 判断是否输入电话号码
				Toast.makeText(getActivity(), "请输入电话号码！", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			if (addrEditView.getText().toString().length() == 0) {// 判断是否输入地址
				Toast.makeText(getActivity(), "请输订单地址！", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			if (!(dishCombo.size() > 0)) {
				Toast.makeText(getActivity(), "没有菜品！", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			try {
				pushOrderInfo();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void printAction() {
		String printString = "";
		PrintHelper ph = new PrintHelper(getActivity());
		printString = ph.getPrintString(mOrderInfo, dishCombo);
		if (printString.equals("")) {
			Toast.makeText(getActivity(), "没有数据", Toast.LENGTH_SHORT).show();
			return;
		}
		CustomerInfo ci = new CustomerInfo();
		ci.setCustomerTel(mOrderInfo.getOrderTel());
		ci.setCustomerAddr(mOrderInfo.getAddress());
		DbHelper.getInstance(getActivity()).saveCustomerInfo(ci);
		BlueToothService btService = mApplication.getBTService();
		if (btService.IsOpen()) {
			if (btService.getState() == BlueToothService.STATE_CONNECTED) {
			}
			if (btService.getState() == BlueToothService.STATE_CONNECTING) {
			}
			if (btService.getState() == BlueToothService.STATE_LISTEN) {
			}
			if (btService.getState() == BlueToothService.STATE_NONE) {
				Toast.makeText(getActivity(), "请查看打印机状态", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			if (btService.getState() == BlueToothService.STATE_SCAN_STOP) {
			}
			if (btService.getState() == BlueToothService.STATE_SCANING) {
				Toast.makeText(getActivity(), "请查看打印机状态", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			btService.PrintCharacters(printString);
		} else {
			Toast.makeText(getActivity(), "请查看蓝牙状态", Toast.LENGTH_SHORT).show();
		}
		clearDish();
	}

	private void pushOrderInfo() throws JSONException {
		final String orderInfo = comboOrderInfo();
		final String orderDetail = comboOrderDetail();
		final String orderAddr = comboAddr();
		final ProgressHUD mProgressHUD;
		mProgressHUD = ProgressHUD.show(getActivity(), getResources()
				.getString(R.string.toast_remind_commiting), true, false, null);
		String url = Constants.HOST_HEAD + Constants.COMMIT_ORDER_INFO;
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderInfo", orderInfo);
		params.put("orderDetailsList", orderDetail);
		params.put("addressInfo", orderAddr);
		JsonPostRequest getOrderInfoRequest = new JsonPostRequest(
				Request.Method.POST, url, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							if (respon.getString("code").equals("0")) {
								saveOrderInfo(respon.getString("order"));
								printAction();
							} else {
								Toast.makeText(getActivity(), "提交失败!",
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						mProgressHUD.dismiss();
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						Toast.makeText(getActivity(), "VolleyError 提交失败!",
								Toast.LENGTH_SHORT).show();
						mProgressHUD.dismiss();
					}
				}, params);
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getOrderInfoRequest, TAG);
	}

	private void saveOrderInfo(String orderInfo) throws JSONException {
		JSONObject orderJson = new JSONObject(orderInfo);
		mOrderInfo = new OrderInfoModel();
		mOrderInfo.setAddress("");
		mOrderInfo.setAddrId(orderJson.getString("addressId"));
		mOrderInfo.setCreateTime(orderJson.getString("createTime"));
		mOrderInfo.setOrderAddr(orderJson.getString("orderAddress"));
		mOrderInfo.setOrderId(orderJson.getString("orderId"));
		mOrderInfo.setOrderSn(orderJson.getString("orderSn"));
		mOrderInfo.setOrderStatus(orderJson.getString("orderStatus"));
		mOrderInfo.setOrderTel(orderJson.getString("orderTel"));
		mOrderInfo.setTotalPay(orderJson.getDouble("totalPay"));
	}

	/**
	 * 组合地址JSON地址
	 * 
	 * @return 地址JSON串
	 */
	private String comboAddr() throws JSONException {
		JSONObject addrJson = new JSONObject();
		addrJson.put("addressId", "");
		addrJson.put("buyerId", "");
		addrJson.put("buyerName", "");
		addrJson.put("buyerTel", telEditView.getText().toString());
		addrJson.put("createPerson", "");
		addrJson.put("detailAddress", addrEditView.getText().toString());
		addrJson.put("createDate", "");
		addrJson.put("addStatus", "1");
		return addrJson.toString();
	}

	/**
	 * 组合订单详情
	 * 
	 * @return 订单详情JSON串
	 */
	private String comboOrderDetail() throws JSONException {
		JSONArray detailList = new JSONArray();
		for (int i = 0; i < dishCombo.size(); i++) {
			JSONObject item = new JSONObject();
			OrderDetailModel dataItem = dishCombo.get(i);
			item.put("askFor", dataItem.getAskFor());
			item.put("goodsDiscountPrice", dataItem.getGoodsDiscountPrice());
			item.put("createPerson", "");
			item.put("goodsSinglePrice", dataItem.getGoodsSinglePrice());
			item.put("goodsAttachName", dataItem.getAttachName());
			item.put("goodsNumber", "1");// 默认购买数量
			item.put("goodsAttachPrice", dataItem.getAttachPrice());
			item.put("orderId", "");
			item.put("detailId", "");
			item.put("goodsName", dataItem.getGoodsName());
			item.put("createTime", "");
			item.put("totalPrice", dataItem.getTotalPrice());
			item.put("goodsId", dataItem.getGoodsId());
			detailList.put(item);
		}
		return detailList.toString();
	}

	/**
	 * 订单预览信息
	 * 
	 * @return 返回订单字符串
	 */
	private String comboOrderInfo() throws JSONException {

		JSONObject infoJson = new JSONObject();
		infoJson.put(
				"sellerUserId",
				getActivity()
						.getSharedPreferences(
								Constants.SP_GENERAL_PROFILE_NAME,
								Context.MODE_PRIVATE).getString(
								Constants.SP_SELLER_ID, ""));
		infoJson.put("orderSn", "");
		infoJson.put("shouldPay", totalPriceNum);
		infoJson.put("buyerUserId", "");
		infoJson.put("totalPay", totalPriceNum);
		infoJson.put("createPerson", "");
		infoJson.put("orderSource", "1");// 电话来源
		infoJson.put("orderTel", telEditView.getText().toString());
		infoJson.put("addressId", "");
		infoJson.put("createTime", "");
		infoJson.put("orderId", "");
		infoJson.put("orderStatus", "1");
		infoJson.put("besurePerson", "");
		infoJson.put("orderAddress", addrEditView.getText().toString());
		infoJson.put("besureTime", "");
		return infoJson.toString();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			ArrayList<OrderDetailModel> transferData = data.getExtras()
					.getParcelableArrayList(Constants.DISH_COMBO_RESULT);
			dishCombo.addAll(transferData);
			mAdapter.setDataSrc(dishCombo);
			confirmLayout.setVisibility(View.VISIBLE);
		}
		// 列表加载数据
	}

	@Override
	public void onResume() {
		super.onResume();
		telEditView.setText(callNumber);
		addrEditView.setText(callAddr);
		if (!(dishCombo.size() > 0))
			return;
		double totalPrice = 0.0;
		for (OrderDetailModel item : dishCombo) {
			totalPrice += item.getTotalPrice();
		}
		totalPriceNum = totalPrice;
		setCurrentTotalPrice(totalPriceNum);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		showSelectDialog(position, "真的要删除这道菜品吗？");
		return true;
	}

	void showSelectDialog(final int position, String promptText) {
		Builder builder = new android.app.AlertDialog.Builder(getActivity());
		builder.create().requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 0: 默认第一个单选按钮被选中
		TextView promptView = new TextView(getActivity());
		LinearLayout linearLayout = new LinearLayout(getActivity());
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		linearLayout.setLayoutParams(layoutParams);
		android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT);
		lp.setMargins(0, DisplayMetrics.dip2px(getActivity(), 20), 0,
				DisplayMetrics.dip2px(getActivity(), 20));
		promptView.setLayoutParams(lp);
		promptView.setGravity(Gravity.CENTER);
		promptView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		promptView.setText(promptText);
		linearLayout.addView(promptView);
		builder.setView(linearLayout);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				totalPriceNum -= dishCombo.get(position).getTotalPrice();
				setCurrentTotalPrice(totalPriceNum);
				dishCombo.remove(position);
				if (dishCombo.size() == 0) {
					totalPriceNum = 0.0;
					confirmLayout.setVisibility(View.GONE);
					return;
				}
				mAdapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		// 创建一个单选按钮对话框
		builder.create().show();
	}

	/**
	 * 设置当前菜单的总价
	 * 
	 * @param total
	 */
	void setCurrentTotalPrice(Double total) {
		totalPrice.setText(String.format(
				getResources().getString(R.string.total_price_text),
				String.valueOf(total)));
	}

	/**
	 * 清空当前已点菜品
	 */
	public void clearDish() {
		try {
			dishCombo.clear();// 清空点单数据
			mAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
