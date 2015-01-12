package com.eteng.mobileorder;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.service.BlueToothService;

public class MobileOrderApplication extends Application implements BlueToothService.CustomBTStateListener{
	
	private static final String TAG = "MobileOrderApplication";
	private static final int REQUEST_EX = 1;
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	private BlueToothService mBTService = null;
	private static MobileOrderApplication mAppInstance = null;
	
	private BroadcastReceiver test;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mAppInstance = this;
		mBTService = new BlueToothService(this);
	}
	
	public static MobileOrderApplication getInstance(){
		return mAppInstance;
	}
	
	public BlueToothService getBTService(){		
		return mBTService;
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		unregisterReceiver(test);
	}

	@Override
	public void connectSucceed() {
		// TODO Auto-generated method stub
//		Toast.makeText(this, TAG + "连接成功", Toast.LENGTH_SHORT).show();
		DebugFlags.logD(TAG, TAG + "连接成功");
	}

	@Override
	public void connectFailed() {
		// TODO Auto-generated method stub
//		Toast.makeText(this, TAG + "连接失败", Toast.LENGTH_SHORT).show();
		DebugFlags.logD(TAG, TAG + "连接失败");
	}

	@Override
	public void connectLose() {
		// TODO Auto-generated method stub
//		Toast.makeText(this, TAG + "连接丢失", Toast.LENGTH_SHORT).show();
		DebugFlags.logD(TAG, TAG + "连接丢失");
	}

	@Override
	public void connectting() {
		// TODO Auto-generated method stub
//		Toast.makeText(this, TAG + "正在连接", Toast.LENGTH_SHORT).show();
		DebugFlags.logD(TAG, TAG + "连接中。。。");
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
//			if(msg.what == )
		}
		
	};
}
