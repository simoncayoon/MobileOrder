package com.eteng.mobileorder;

import java.util.Set;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.service.BlueToothService;
import com.eteng.mobileorder.service.BlueToothService.CustomBTStateListener;
import com.eteng.mobileorder.service.BlueToothService.OnReceiveDataHandleEvent;
import com.kyleduo.switchbutton.SwitchButton;

public class SettingActivity extends Activity implements
		OnCheckedChangeListener, OnItemClickListener,
		TopNavigationBar.NaviBtnListener, OnClickListener,
		CustomBTStateListener {

	public static final String TAG = "SettingActivity";
	private static String GET_BONDED_DEVICES = "get_bonded_devices";
	private static String GET_NEW_DEVICES = "get_new_devices";
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final int STATE_NONE = 0; // we're doing nothing
	public static final int STATE_LISTEN = 1; // now listening for incoming
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
	public static final int STATE_CONNECTED = 3; // now connected to a remote
	public static final int LOSE_CONNECT = 4;
	public static final int FAILED_CONNECT = 5;
	public static final int SUCCESS_CONNECT = 6; // now connected to a remote

	private BlueToothService mBTService = null;
	private MobileOrderApplication mApplication;

	private TopNavigationBar topBar;
	private SwitchButton printerConnector;
	private ListView devList, settingFuncList;
	private LinearLayout scanDevLayout, promptlayout;
	private Button summyBtn, scanNewDevBtn, closeBtn, exitBtn;
	private TextView promptText;

	private String[] menuList = new String[] { "我的资料", "菜单上传", "备注信息", "客户信息" };
	private int[] drawList = new int[] { R.drawable.setting_my_profile,
			R.drawable.setting_upload_dish_func, R.drawable.setting_remark,
			R.drawable.setting_custom_info };
	private ArrayAdapter<String> devDataAdapter = null;// 已配对列表
	private ArrayAdapter<String> mNewDevicesArrayAdapter = null;// 新搜索列表
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
		topBar.setLeftImg(R.drawable.setting_back_btn_bg);
		topBar.setTitle("设置");
		printerConnector = (SwitchButton) findViewById(R.id.printer_connector_switcher);
		printerConnector.setOnCheckedChangeListener(this);
		summyBtn = (Button) findViewById(R.id.summySwitchBtn);
		summyBtn.setOnClickListener(this);
		scanDevLayout = (LinearLayout) findViewById(R.id.devices_list_scan_layout);
		devList = (ListView) scanDevLayout.findViewById(R.id.devices_list_view);
		devList.setAdapter(devDataAdapter);
		devList.setOnItemClickListener(this);
		scanNewDevBtn = (Button) scanDevLayout
				.findViewById(R.id.start_scan_btn);
		scanNewDevBtn.setOnClickListener(this);
		closeBtn = (Button) findViewById(R.id.close_scan_btn);
		closeBtn.setOnClickListener(this);
		closeBtn.setText(getResources().getString(R.string.scan_invisible_text));
		promptlayout = (LinearLayout) scanDevLayout
				.findViewById(R.id.prompt_layout);
		promptText = (TextView) promptlayout
				.findViewById(R.id.scan_prompt_text);
		settingFuncList = (ListView) findViewById(R.id.setting_func_list__view);
		settingFuncList.setOnItemClickListener(this);
		settingFuncList.setAdapter(new FuncListAdapter());
		exitBtn = (Button) findViewById(R.id.exit_app_btn);
		exitBtn.setOnClickListener(this);
	}

	private void initData() {
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.blue_dev_name_layout);
		devDataAdapter = new ArrayAdapter<String>(SettingActivity.this,
				R.layout.blue_dev_name_layout);
		mApplication = MobileOrderApplication.getInstance();
		mBTService = mApplication.getBTService();
		if (mBTService.getState() == BlueToothService.STATE_CONNECTED) {
			printerConnector.setChecked(true);
		}
		mBTService.setConnStateChangeListener(this);
		mBTService.setOnReceive(new OnReceiveDataHandleEvent() {

			@Override
			public void OnReceive(BluetoothDevice device) {
				if (device != null) {
					mNewDevicesArrayAdapter.add(device.getName() + "\n"
							+ device.getAddress());
				} else {
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
				}
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int vId = parent.getId();
		DebugFlags.logD(TAG, "the id is " + vId);
		if (vId == R.id.devices_list_view) {// 设备列表
			// 获取蓝牙物理地址
			if (!mBTService.IsOpen()) {// 判断蓝牙是否打开
				mBTService.OpenDevice();
				return;
			}
			if (mBTService.GetScanState() == BlueToothService.STATE_SCANING) {
				mBTService.StopScan();
				Message msg = new Message();
				msg.what = 2;
				handler.sendMessage(msg);
			}
			if (mBTService.getState() == BlueToothService.STATE_CONNECTING) {
				promptlayout.setVisibility(View.VISIBLE);
				promptText.setText("正在连接...");
				return;
			}
			if (mBTService.getState() == BlueToothService.STATE_NONE) {
				promptlayout.setVisibility(View.VISIBLE);
				promptText.setText("正在连接...");
			}

			String info = ((TextView) view).getText().toString();
			if (info.length() < 17) {
				return;
			}
			String address = info.substring(info.length() - 17);
			mBTService.DisConnected();
			mBTService.ConnectToDevice(address);// 连接蓝牙

		} else if (vId == R.id.setting_func_list__view) {// 功能列表
			if (position == 0) {// 我的资料
				startActivity(new Intent(SettingActivity.this,
						SettingOwnProfile.class));
			}
			if (position == 1) {// 菜单上传
				startActivity(new Intent(SettingActivity.this,
						SettingUploadActivity.class));
			}
			if (position == 2) {// 备注信息
				startActivity(new Intent(SettingActivity.this,
						RemarkEditActivity.class));
			}
			if (position == 3) {// 客户信息
				startActivity(new Intent(SettingActivity.this,
						CustomerInfoActivity.class));
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			// devList.setVisibility(View.VISIBLE);

		} else {
			DebugFlags.logD(TAG, "关闭触发");
			if (scanDevLayout.isShown()) {
				scanDevLayout.setVisibility(View.GONE);
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

	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.summySwitchBtn) {
			if (!printerConnector.isChecked()) {// 为选择
				if (mBTService.getState() == BlueToothService.STATE_CONNECTED) {
					printerConnector.setChecked(true);
				}
				if (mBTService.GetScanState() == BlueToothService.STATE_SCANING) {
					mBTService.StopScan();
				}
				// 打开选择列表弹出框
				scanDevLayout.setVisibility(View.VISIBLE);
				// 打开蓝牙>寻找设备>建立连接>修改全局连接状态
				if (mBTService.HasDevice()) {
					getAvailableDev(GET_BONDED_DEVICES);
				} else {// 该设备没有蓝牙装置
					Toast.makeText(SettingActivity.this, "未在该设备上发现蓝牙装置！",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				mBTService.DisConnected();
				printerConnector.setChecked(false);
			}
		}
		if (id == R.id.start_scan_btn) {

			getAvailableDev(GET_NEW_DEVICES);

		}

		if (id == R.id.close_scan_btn) {
			if (scanDevLayout.isShown()) {
				scanDevLayout.setVisibility(View.GONE);
			}
			if (mBTService.GetScanState() == BlueToothService.STATE_SCANING) {
				mBTService.StopScan();
			}
		}
		if(id == R.id.exit_app_btn){
			SharedPreferences sp = getSharedPreferences(Constants.SP_GENERAL_PROFILE_NAME,
					Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putBoolean(Constants.SP_SAVE_PWD_STATE, false);
			editor.putString(Constants.SP_LOGIN_ACCOUNT, "");
			editor.putString(Constants.SP_LOGIN_PWD, "");
			editor.commit();
			startActivity(new Intent(SettingActivity.this, LoginActivity.class));
			finish();
		}
	}

	/**
	 * 获取可用的蓝牙列表
	 */
	void getAvailableDev(String flag) {
		if (!mBTService.IsOpen()) {
			mBTService.OpenDevice();
			return;
		}
		if (flag.equals(GET_BONDED_DEVICES)) {// 产看已配对的设配
			scanNewDevBtn.setText(getResources().getString(
					R.string.scan_dev_text));
			promptlayout.setVisibility(View.GONE);
			devices = mBTService.GetBondedDevice();
			devDataAdapter.clear();
			if (devices.size() > 0) {
				for (BluetoothDevice device : devices) {
					devDataAdapter.add(device.getName() + "\n"
							+ device.getAddress());
					DebugFlags.logD(TAG,
							device.getName() + "\n" + device.getAddress());
				}
			} else {
				String noDevices = "没有已配对的设备";
				DebugFlags.logD(noDevices, noDevices);
				devDataAdapter.add(noDevices);
			}
			devDataAdapter.notifyDataSetChanged();
			devList.setAdapter(devDataAdapter);
		}
		if (flag.equals(GET_NEW_DEVICES)) {
			promptlayout.setVisibility(View.VISIBLE);
			if (mBTService.GetScanState() == BlueToothService.STATE_SCANING) {
				return;
			}
			mNewDevicesArrayAdapter.clear();
			devices = mBTService.GetBondedDevice();

			if (devices.size() > 0) {
				for (BluetoothDevice device : devices) {
					mNewDevicesArrayAdapter.add(device.getName() + "\n"
							+ device.getAddress());
				}
			}
			mNewDevicesArrayAdapter.notifyDataSetChanged();
			devList.setAdapter(mNewDevicesArrayAdapter);
			new Thread() {
				public void run() {
					mBTService.ScanDevice();
				}
			}.start();
		}

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:

				break;
			case 1:// 扫描完毕
				mBTService.StopScan();
				promptlayout.setVisibility(View.GONE);
				Toast.makeText(SettingActivity.this, "扫描完毕", Toast.LENGTH_SHORT)
						.show();
				scanNewDevBtn.setText(getResources().getString(
						R.string.scan_again_text));
				break;
			case 2:// 停止扫描
				promptText.setText("正在连接...");
				break;
			}
		}
	};

	class FuncListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return menuList.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = getLayoutInflater().inflate(
					R.layout.setting_func_list_item_layout, null);
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.setting_func_list_img);
			icon.setBackgroundResource(drawList[position]);
			TextView text = (TextView) convertView
					.findViewById(R.id.setting_func_list_text);
			text.setText(menuList[position]);
			return convertView;
		}

	}

	@Override
	public void connectSucceed() {
		// TODO Auto-generated method stub
		DebugFlags.logD(TAG, "connectSucceed()");
		mHandler.obtainMessage(MESSAGE_STATE_CHANGE, SUCCESS_CONNECT, -1)
				.sendToTarget();
	}

	@Override
	public void connectFailed() {
		// TODO Auto-generated method stub
		DebugFlags.logD(TAG, "connectFailed() ");
		mHandler.obtainMessage(MESSAGE_STATE_CHANGE, FAILED_CONNECT, -1)
				.sendToTarget();
	}

	@Override
	public void connectLose() {
		// TODO Auto-generated method stub
		DebugFlags.logD(TAG, "connectLose() ");
		mHandler.obtainMessage(MESSAGE_STATE_CHANGE, LOSE_CONNECT, -1)
				.sendToTarget();
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void connectting() {
		// TODO Auto-generated method stub
		DebugFlags.logD(TAG, "connectting()");
		mHandler.obtainMessage(MESSAGE_STATE_CHANGE, LOSE_CONNECT, 0)
				.sendToTarget();
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
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
				case BlueToothService.SUCCESS_CONNECT:
					Toast.makeText(SettingActivity.this, "连接打印机成功！",
							Toast.LENGTH_SHORT).show();
					printerConnector.setChecked(true);
					scanDevLayout.setVisibility(View.GONE);
					break;
				case BlueToothService.FAILED_CONNECT:
					Toast.makeText(SettingActivity.this, "未能连接打印机！",
							Toast.LENGTH_SHORT).show();
					promptlayout.setVisibility(View.GONE);

					break;
				case BlueToothService.LOSE_CONNECT:
					switch (msg.arg2) {
					case -1:
						DebugFlags.logD(TAG, "xxxxx丢失-1！！");
						printerConnector.setChecked(false);
						break;
					case 0:
						DebugFlags.logD(TAG, "xxxxx丢失0！！");
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
