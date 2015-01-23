package com.eteng.mobileorder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eteng.mobileorder.adapter.DishComboAdapter;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.MenuItemModel;
import com.eteng.mobileorder.service.BlueToothService;
import com.eteng.mobileorder.utils.DisplayMetrics;

public class FragmentMain extends BaseFragment implements OnClickListener {

	private static final String TAG = "FragmentMain";
	private TextView addCombo, headerPhone, headerDate, headerAddr, totalPrice,
			dateView;
	private EditText telEditView, addrEditView;
	private Button confirmBtn;
	private ListView mListView;
	private ArrayList<MenuItemModel> dishCombo;
	private DishComboAdapter mAdapter;
	private MobileOrderApplication mApplication;
	private LinearLayout confirmLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dishCombo = new ArrayList<MenuItemModel>();
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
			String printString = "";
			printString = getPrintString(dishCombo);
			if (printString.equals("")) {
				Toast.makeText(getActivity(), "没有数据", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			BlueToothService btService = mApplication.getBTService();
			if (btService.IsOpen()) {
				if (btService.getState() == BlueToothService.STATE_CONNECTED) {
					DebugFlags.logD(TAG, "STATE_CONNECTED");
				}
				if (btService.getState() == BlueToothService.STATE_CONNECTING) {
					DebugFlags.logD(TAG, "STATE_CONNECTING");
				}
				if (btService.getState() == BlueToothService.STATE_LISTEN) {
					DebugFlags.logD(TAG, "STATE_LISTEN");
				}
				if (btService.getState() == BlueToothService.STATE_NONE) {
					DebugFlags.logD(TAG, "LOSE_CONNECT");
				}
				if (btService.getState() == BlueToothService.STATE_SCAN_STOP) {
					DebugFlags.logD(TAG, "STATE_SCAN_STOP");
				}
				if (btService.getState() == BlueToothService.STATE_SCANING) {
					DebugFlags.logD(TAG, "STATE_SCANING");
				}
				DebugFlags.logD(TAG, "PrintString " + printString);
				btService.PrintCharacters(printString);
			} else {
				Toast.makeText(getActivity(), "请查看打印机状态", Toast.LENGTH_SHORT)
				.show();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		DebugFlags.logD(TAG, "onActivityResult");
		if (data != null) {
			dishCombo = data.getExtras().getParcelableArrayList(
					Constants.DISH_COMBO_RESULT);
			for (MenuItemModel item : dishCombo) {
				DebugFlags.logD(
						TAG,
						"各项数据是:" + item.getName() + item.getImgUrl()
								+ item.getItemPrice());
			}
			mAdapter.setDataSrc(dishCombo);
			confirmLayout.setVisibility(View.VISIBLE);
		}
		// 列表加载数据
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
		String orderId = "订单编号:" + "LF1419915475819458157\n";
		String tel = "电话：" + telEditView.getText() + "\n";
		String date = "时间：" + dateView.getText() + "\n";
		String addr = "地址：" + addrEditView.getText() + "\n";
		headerString = orderId + tel + date + addr + "\r\n";
		return headerString;
	}

	@Override
	public void onResume() {
		super.onResume();
		DebugFlags.logD(TAG, "onResume");
		if (!(dishCombo.size() > 0))
			return;
		double totalPrice = 0.0;
		for (MenuItemModel item : dishCombo) {
			totalPrice += item.getItemPrice();
		}
		this.totalPrice.setText(String.format(
				getResources().getString(R.string.total_price_text),
				String.valueOf(totalPrice)));
	}
}
