package com.eteng.mobileorder;

import android.app.Application;
import android.content.Context;
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
	private DaoSession mDaoSession = null;
	private SQLiteDatabase db;
	private static MobileOrderApplication mAppInstance = null;
	private static Context mContext;


	@Override
	public void onCreate() {
		super.onCreate();
		mAppInstance = this;
		mBTService = new BlueToothService(this);
		mContext = this;
	}

	public static MobileOrderApplication getInstance() {
		return mAppInstance;
	}

	public BlueToothService getBTService() {
		return mBTService;
	}
	
	public DaoMaster getDaoMaster(){
		if(daoMaster == null){
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, "mobileorder-db", null);
	        db = helper.getWritableDatabase();
			daoMaster = new DaoMaster(db);
		}
		return daoMaster;
	}
	
	public DaoSession getDaoSession(){
		if(mDaoSession == null){
			if(daoMaster == null){
				mDaoSession = getDaoMaster().newSession();
			}
			mDaoSession = daoMaster.newSession();
		}
		return mDaoSession;
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}
}
