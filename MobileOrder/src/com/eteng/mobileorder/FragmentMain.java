package com.eteng.mobileorder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.SimpleFormatter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.eteng.mobileorder.adapter.DishComboAdapter;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.MenuItemModel;

public class FragmentMain extends BaseFragment implements OnClickListener {

	private static final String TAG = "FragmentMain";
	private TextView addCombo, headerPhone, headerDate, headerAddr, totalPrice, dateView;
	private EditText telEditView, addrEditView;
	private Button confirmBtn;
	private ListView mListView;
	private ArrayList<MenuItemModel> dishCombo;
	private DishComboAdapter mAdapter;

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
	}

	private void initList() {
		mListView = (ListView) findViewById(R.id.phone_order_dish_combo_list_view);
		mAdapter = new DishComboAdapter(getActivity());
		mListView.setAdapter(mAdapter);
	}

	private void initHeader() {
		headerPhone = (TextView) getContentView().findViewById(
				R.id.main_fragment_header_phone);
		headerDate = (TextView) getContentView().findViewById(
				R.id.main_fragment_header_date);
		headerAddr = (TextView) getContentView().findViewById(
				R.id.main_fragment_header_addr);
		addCombo = (TextView) getContentView().findViewById(R.id.order_add_btn);
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
		addCombo.setOnClickListener(this);
		
		telEditView = (EditText) findViewById(R.id.header_tel_edit_view);
		dateView = (TextView) findViewById(R.id.header_date_edit_view);
		Date data = new Date();
		dateView.setText(new SimpleDateFormat("yy/MM/dd").format(data));
		addrEditView = (EditText) findViewById(R.id.header_addr_edit_view);
//		headerPhone.setText("18685613451");
//		headerDate.setText("15/01/10");
//		headerAddr.setText("中华北路神奇世纪商务城1803");
		totalPrice = (TextView) findViewById(R.id.phone_order_combo_count);// 显示总价
		confirmBtn = (Button) findViewById(R.id.phone_order_commit_btn);
		
	}

	@Override
	public void onClick(View v) {
		int vId = v.getId();
		if (vId == R.id.order_add_btn) {
			startActivityForResult(new Intent(getActivity(),
					PhoneOrderActivity.class), Constants.REQUEST_CODE);
		}
		if (vId == R.id.phone_order_commit_btn) {//提交订单&打印订单
			/**
			 * 步骤：1、判断可否打印
			 * 2、网络提交返回
			 * 3、打印
			 */
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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
		}
		// 列表加载数据
	}
}
