package com.eteng.mobileorder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eteng.mobileorder.server.PhoneCallListenerService;

public class BootActionReceiver extends BroadcastReceiver {

	@SuppressWarnings("unused")
	private static final String TAG = BootActionReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Intent service = new Intent(context, PhoneCallListenerService.class);
		context.startService(service);
	}
}
