package com.eteng.mobileorder.receiver;

import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.server.PhoneCallListenerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootActionReceiver extends BroadcastReceiver {

	private static final String TAG = BootActionReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		DebugFlags.logD(TAG, "!@#$%^%^&*(  " + "开机！");
//		PhoneCallListenerService inComingService = new PhoneCallListenerService();
		
		Intent service = new Intent(context, PhoneCallListenerService.class);
		context.startService(service);
	}
}
