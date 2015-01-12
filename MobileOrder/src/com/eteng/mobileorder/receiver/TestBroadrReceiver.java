package com.eteng.mobileorder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class TestBroadrReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())){
			Toast.makeText(context, "test", Toast.LENGTH_LONG).show();
		}
	}
}
