package com.eteng.mobileorder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.eteng.mobileorder.models.Constants;

public class TempDataManager {

	private static TempDataManager instance = null;
	private Context mContext = null; 
	private SharedPreferences sp = null;
	private Editor mEditor = null;
	
	private TempDataManager(Context context){
		mContext = context;
		sp = mContext.getSharedPreferences(Constants.SP_GENERAL_PROFILE_NAME,
				Context.MODE_PRIVATE);
		mEditor = sp.edit();
	}
	
	public static TempDataManager getInstance(Context context){
		if(instance == null){
			instance = new TempDataManager(context);
		}
		return instance;
	}
	
	/**
	 * 保存账户名称，密码
	 */
	public void setCurrentInfo(Long accountId, String accountName, String pwd){
		mEditor.putLong(Constants.SP_SELLER_ID, accountId);
		mEditor.putString(Constants.SP_LOGIN_ACCOUNT, accountName);
		mEditor.putString(Constants.SP_LOGIN_PWD, pwd);
		mEditor.commit();
	}
	
	public void setPwdSaveState(Boolean state){
		mEditor.putBoolean(Constants.SP_SAVE_PWD_STATE, state);
		mEditor.commit();
	}
	
	public Boolean getPwdSaveState(){
		return sp.getBoolean(Constants.SP_SAVE_PWD_STATE, false);
	}
	
	/**
	 * 获取当前登录账户的ID
	 * @return 如果是-1 则表示当前没有用户登录
	 */
	public Long getSellerId(){
		Long currentId = sp.getLong(Constants.SP_SELLER_ID, -1);
		return currentId;
	}
	
	public String getAccountName(){
		return sp.getString(Constants.SP_LOGIN_ACCOUNT, "");
	}
	
	public String getPwd(){
		return sp.getString(Constants.SP_LOGIN_PWD, "");
	}
	
	/**
	 * 设置是否第一次登录
	 * @param visitState
	 */
	public void setIsFirstVisit(Boolean visitState){
		mEditor.putBoolean(Constants.KEY_IS_FIRST_VISIT, visitState);
		mEditor.commit();
	}
	
	/**
	 * 获取是否第一次访问应用
	 * @return
	 */
	public Boolean getIsFirstVisit(){
		return sp.getBoolean(Constants.KEY_IS_FIRST_VISIT, true);
	}
	
	public void saveNoodleid(Long noodleId){
		mEditor.putLong(Constants.NOODLE_ID, noodleId);
		mEditor.commit();
	}
	
	public void setQrcodePath(String path){
		mEditor.putString(Constants.QRCODE_PATH, path);
		mEditor.commit();
	}
	
	public String getQrCodePath(){
		return sp.getString(Constants.QRCODE_PATH, "");
	}
	
	/**
	 * 清空当前临时信息
	 */
	public void clearCurrentTemp(){
		mEditor.clear();
		mEditor.commit();
	}
	

}
