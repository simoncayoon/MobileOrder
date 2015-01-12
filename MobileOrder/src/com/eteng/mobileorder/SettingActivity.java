package com.eteng.mobileorder;

import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.service.BlueToothService;
import com.kyleduo.switchbutton.SwitchButton;

public class SettingActivity extends Activity implements
		OnCheckedChangeListener, OnItemClickListener,
		TopNavigationBar.NaviBtnListener, OnClickListener {

	public static final String TAG = "SettingActivity";
	private static final int REQUEST_EX = 1;
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	private BlueToothService mBTService = null;
	private MobileOrderApplication mApplication;

	private TopNavigationBar topBar;
	private SwitchButton printerConnector;
	private ListView devList;
	private AlertDialog mDialog;

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
		topBar.setLeftImg(R.drawable.setting_back_btn_bg);;
		topBar.setTitle("设置");
		printerConnector = (SwitchButton) findViewById(R.id.printer_connector_switcher);
		printerConnector.setOnCheckedChangeListener(this);
		devList = (ListView) findViewById(R.id.devices_list_view);
		devList.setAdapter(devDataAdapter);
		devList.setOnItemClickListener(this);
	}

	private void initData() {
		mApplication = MobileOrderApplication.getInstance();
		mBTService = mApplication.getBTService();
		if(mBTService.IsOpen()){
			printerConnector.setChecked(true);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 获取蓝牙物理地址
		if (!mBTService.IsOpen()) {// 判断蓝牙是否打开
			mBTService.OpenDevice();
			return;
		}
		if (mBTService.GetScanState() == mBTService.STATE_SCANING) {
			Message msg = new Message();
			msg.what = 2;
//			mhandler.sendMessage(msg);
		}
		if (mBTService.getState() == mBTService.STATE_CONNECTING) {
			return;
		}

		String info = ((TextView) view).getText().toString();
		String address = info.substring(info.length() - 17);
		mBTService.DisConnected();
		mBTService.ConnectToDevice(address);// 连接蓝牙

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			devList.setVisibility(View.VISIBLE);
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
						DebugFlags.logD(TAG,
								device.getName() + "\n" + device.getAddress());
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
		} else {
			devList.setVisibility(View.GONE);
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
		if(id == R.id.printer_connector_switcher){
			DebugFlags.logD(TAG, "点击了switch");
			if(!printerConnector.isChecked()){//为选择
				printerConnector.setChecked(false);
				//打开选择列表弹出框
				
			}
		}
		
	}
	
	private void showProcessDialog(Context mContext, View layout) {
		OnKeyListener keyListener = new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface arg0, int keyCode,
					KeyEvent arg2) {
				if (keyCode == KeyEvent.KEYCODE_HOME
						|| keyCode == KeyEvent.KEYCODE_SEARCH) {
					return true;
				}
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					finish();
					return true;
				}
				return false;
			}
		};

		mDialog = new AlertDialog.Builder(mContext).create();
		mDialog.setOnKeyListener(keyListener);

		Window mWindow = ((Activity) mContext).getWindow();
		WindowManager.LayoutParams lp = mWindow.getAttributes();
		lp.x = Gravity.CENTER_HORIZONTAL; // 新位置X坐标
		lp.y = Gravity.BOTTOM; // 新位置Y坐标
		mDialog.onWindowAttributesChanged(lp);

		mDialog.show();
		mDialog.setContentView(layout);
	}
}
