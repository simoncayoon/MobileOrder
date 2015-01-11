package com.eteng.mobileorder;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.service.BlueToothService;
import com.kyleduo.switchbutton.SwitchButton;

public class SettingActivity extends Activity implements
		OnCheckedChangeListener, OnItemClickListener,
		TopNavigationBar.NaviBtnListener {

	public static final String TAG = "SettingActivity";
	private static final int REQUEST_EX = 1;
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	private BlueToothService mBTService = null;

	private TopNavigationBar topBar;
	private SwitchButton printerConnector;
	private ListView devList;

	private String[] menuList = new String[] { "我的资料", "菜单上传", "备注信息", "客户信息" };
	private ArrayAdapter<String> devDataAdapter = null;
	private Set<BluetoothDevice> devices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_layout);
		initView();
		initData();
	}

	private void initView() {
		topBar = (TopNavigationBar) findViewById(R.id.general_navi_view);
		topBar.setLeftBtnText("返回");
		printerConnector = (SwitchButton) findViewById(R.id.printer_connector_switcher);
		printerConnector.setOnCheckedChangeListener(this);
		devList = (ListView) findViewById(R.id.devices_list_view);
		devList.setAdapter(devDataAdapter);
	}

	private void initData() {
		mBTService = new BlueToothService(this, mhandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			// 打开蓝牙>寻找设备>建立连接>修改全局连接状态
			if (mBTService.HasDevice()) {
				if (!mBTService.IsOpen()) {
					mBTService.OpenDevice();
				}
				devDataAdapter = new ArrayAdapter<String>(SettingActivity.this,
						R.layout.blue_dev_name_layout);
				devices = mBTService.GetBondedDevice();
				if (devices.size() > 0) {
					
					for (BluetoothDevice device : devices) {
						devDataAdapter.add(device.getName() + "\n"
								+ device.getAddress());
						DebugFlags.logD(TAG, device.getName() + "\n"
								+ device.getAddress());
					}
				} else {
					String noDevices = "没有设备";
					DebugFlags.logD(noDevices, noDevices);
					devDataAdapter.add(noDevices);
				}
				devDataAdapter.notifyDataSetChanged();
				devList.setAdapter(devDataAdapter);
			} else {// 该设备没有蓝牙装置
				
			}
		}
	}

	@Override
	public void leftBtnListener() {
		finish();
	}

	@Override
	public void rightBtnListener() {

	}

	private Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:// 蓝牙连接状态
				switch (msg.arg1) {
				case BlueToothService.STATE_CONNECTED:// 已经连接
					break;
				case BlueToothService.STATE_CONNECTING:// 正在连接
					break;
				case BlueToothService.STATE_LISTEN:
				case BlueToothService.STATE_NONE:
					break;
				case BlueToothService.SUCCESS_CONNECT:// 连接成功

					break;
				case BlueToothService.FAILED_CONNECT:// 连接失败

					break;
				case BlueToothService.LOSE_CONNECT:
					switch (msg.arg2) {
					case -1:// 连接断开

						break;
					case 0:
						break;
					}
				}
				break;
			case MESSAGE_READ:
				// sendFlag = false;//缓冲区已满
				break;
			case MESSAGE_WRITE:// 缓冲区未满
				// sendFlag = true;
				break;

			}
		}
	};
}
