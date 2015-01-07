package com.eteng.mobileorder;

import android.app.Activity;
import android.os.Bundle;

import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.cusomview.TopNavigationBar.NaviBtnListener;
import com.eteng.mobileorder.debug.DebugFlags;

public class TestActivity extends Activity implements NaviBtnListener{

	private static final String TAG = "TestActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_layout);
		TopNavigationBar test = (TopNavigationBar) findViewById(R.id.general_navi_view);
		test.setLeftImg(R.drawable.logo);
	}

	@Override
	public void leftBtnListener() {
		// TODO Auto-generated method stub
		DebugFlags.logD(TAG, "left btn click");
	}

	@Override
	public void rightBtnListener() {
		// TODO Auto-generated method stub
		DebugFlags.logD(TAG, "right btn click");
	}
}
