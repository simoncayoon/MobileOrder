package com.eteng.mobileorder.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.eteng.mobileorder.MainNaviActivity;

public class PhoneCallListenerService extends Service {

	private static final String TAG = PhoneCallListenerService.class
			.getSimpleName();
	TelephonyManager telephonyManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);// 获得电话服务
		telephonyManager.listen(new TeleListen(),
				PhoneStateListener.LISTEN_CALL_STATE);// 对来电状态进行监听

		super.onCreate();
	}

	private class TeleListen extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			if (state == TelephonyManager.CALL_STATE_RINGING) {// 来电
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				// Here the activity class in Question "JobDetail"
				intent.setClassName(getPackageName(),
						MainNaviActivity.class.getCanonicalName());

				// These extras I add and then use them in my
				// JobDetail class to show the specific details for the caller
				intent.putExtra("incoming_call_number", incomingNumber);
				startActivity(intent);
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}
}
