package com.eteng.mobileorder;

import android.app.Application;
import android.content.BroadcastReceiver;

import com.eteng.mobileorder.service.BlueToothService;

public class MobileOrderApplication extends Application{

	@SuppressWarnings("unused")
	private static final String TAG = "MobileOrderApplication";

	private BlueToothService mBTService = null;
	private static MobileOrderApplication mAppInstance = null;

	private BroadcastReceiver test;

	@Override
	public void onCreate() {
		super.onCreate();
		mAppInstance = this;
		mBTService = new BlueToothService(this);
	}

	public static MobileOrderApplication getInstance() {
		return mAppInstance;
	}

	public BlueToothService getBTService() {
		return mBTService;
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		unregisterReceiver(test);
	}

	
}
