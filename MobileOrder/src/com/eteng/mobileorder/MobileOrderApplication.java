package com.eteng.mobileorder;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.eteng.mobileorder.models.DaoMaster;
import com.eteng.mobileorder.models.DaoSession;
import com.eteng.mobileorder.models.DaoMaster.DevOpenHelper;
import com.eteng.mobileorder.service.BlueToothService;

public class MobileOrderApplication extends Application{

	@SuppressWarnings("unused")
	private static final String TAG = "MobileOrderApplication";

	private BlueToothService mBTService = null;
	private DaoMaster daoMaster = null;
	private SQLiteDatabase db;
	private static MobileOrderApplication mAppInstance = null;


	@Override
	public void onCreate() {
		super.onCreate();
		mAppInstance = this;
		mBTService = new BlueToothService(this);
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mobileorder-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
	}

	public static MobileOrderApplication getInstance() {
		return mAppInstance;
	}

	public BlueToothService getBTService() {
		return mBTService;
	}
	
	public DaoSession getDaoSession(){
		return daoMaster.newSession();
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}
}
