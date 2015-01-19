package com.eteng.mobileorder;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.cusomview.TopNavigationBar.NaviBtnListener;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.utils.JsonUTF8Request;
import com.eteng.mobileorder.utils.NetController;
import com.mobeta.android.dslv.DragSortController;

public class SettingUploadActivity extends FragmentActivity implements NaviBtnListener{

	private static final String TAG = "SettingUploadActivity";

	private TopNavigationBar topBar;
	
	private int mDragStartMode = DragSortController.ON_DRAG;
	private boolean mRemoveEnabled = true;
	private int mRemoveMode = DragSortController.FLING_REMOVE;
	private boolean mSortEnabled = true;
	private boolean mDragEnabled = true;
	private String mTag = "dslvTag";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_upload_layout);
		initView();
		initData();
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.upload_fragment_container, getNewDslvFragment(), mTag).commit();
		}
	}
	
	private Fragment getNewDslvFragment() {
		DSLVFragmentClicks f = DSLVFragmentClicks.newInstance(0,
				0);
		f.removeMode = mRemoveMode;
		f.removeEnabled = mRemoveEnabled;
		f.dragStartMode = mDragStartMode;
		f.sortEnabled = mSortEnabled;
		f.dragEnabled = mDragEnabled;
		return f;
	}

	/**
	 * 实例化视图控件
	 */
	private void initView() {
		topBar = (TopNavigationBar) findViewById(R.id.general_navi_view);
		topBar.setLeftImg(R.drawable.setting_back_btn_bg);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		topBar.setTitle("菜品编辑");
		topBar.setRightBtnText("编辑");
//		getListData();
	}

	@Override
	public void leftBtnListener() {
		finish();

	}

	@Override
	public void rightBtnListener() {
		// TODO Auto-generated method stub

	}
}
