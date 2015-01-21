package com.eteng.mobileorder;

import android.app.Application;
import android.content.BroadcastReceiver;

import com.eteng.mobileorder.service.BlueToothService;

public class MobileOrderApplication extends Application{

	private static final String TAG = "MobileOrderApplication";
//	private static final int REQUEST_EX = 1;
//	public static final int MESSAGE_STATE_CHANGE = 1;
//	public static final int MESSAGE_READ = 2;
//	public static final int MESSAGE_WRITE = 3;
//	public static final int MESSAGE_DEVICE_NAME = 4;
//	public static final int MESSAGE_TOAST = 5;
//	
//	// Constants that indicate the current connection state
//	public static final int STATE_NONE = 0; // we're doing nothing
//	public static final int STATE_LISTEN = 1; // now listening for incoming
//												// connections
//	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
//													// connection
//	public static final int STATE_CONNECTED = 3; // now connected to a remote
//													// device
//	public static final int LOSE_CONNECT = 4;
//	public static final int FAILED_CONNECT = 5;
//	public static final int SUCCESS_CONNECT = 6; // now connected to a remote
//
////	public static final int MESSAGE_STATE_CHANGE = 1;
////	public static final int MESSAGE_READ = 2;
////	public static final int MESSAGE_WRITE = 3;
//	public static final int STATE_SCANING = 0;// ɨ��״̬
//	public static final int STATE_SCAN_STOP = 1;
//
//	private static final int WRITE_READ = 2;
//	private static final int WRITE_WAIT = 3;
//
//	private static final String blueState = "";

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
//


}
