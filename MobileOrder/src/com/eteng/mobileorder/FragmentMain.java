package com.eteng.mobileorder;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.eteng.mobileorder.adapter.DishComboAdapter;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.MenuItemModel;

public class FragmentMain extends BaseFragment implements OnClickListener {

	private static final String TAG = "FragmentMain";
	private TextView addCombo, headerPhone, headerDate, headerAddr;
	private ListView mListView;
	public static final int requestCode = 0x1111;
	private ArrayList<MenuItemModel> dishCombo;

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
	}

	@Override
	public void onClick(View v) {
		startActivityForResult(new Intent(getActivity(),
				PhoneOrderActivity.class), requestCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		DebugFlags.logD(TAG, "返回方法：onActivityResult");
		if (data != null) {
			dishCombo = data
					.getParcelableArrayListExtra(Constants.DISH_COMBO_RESULT);
			DebugFlags.logD(TAG, "返回的数组：" + dishCombo.size());
			if(dishCombo.size() > 0){
				mListView.setAdapter(new DishComboAdapter(getActivity(), dishCombo));
			}
		}
		// 列表加载数据
	}
}
