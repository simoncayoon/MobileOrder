package com.eteng.mobileorder.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import android.content.Context;

import com.eteng.mobileorder.MobileOrderApplication;
import com.eteng.mobileorder.models.CategoryInfo;
import com.eteng.mobileorder.models.CategoryInfoDao;
import com.eteng.mobileorder.models.CustomerInfo;
import com.eteng.mobileorder.models.CustomerInfoDao;
import com.eteng.mobileorder.models.DaoSession;
import com.eteng.mobileorder.models.DishInfo;
import com.eteng.mobileorder.models.DishInfoDao;
import com.eteng.mobileorder.models.RemarkInfo;
import com.eteng.mobileorder.models.RemarkInfoDao;
import com.eteng.mobileorder.models.SellerInfo;
import com.eteng.mobileorder.models.SellerInfoDao;
import com.eteng.mobileorder.models.SellerInfoDao.Properties;

import de.greenrobot.dao.query.QueryBuilder;

public class DbHelper {

	private static DbHelper instance = null;
	private static Context mContext = null;
	private static DaoSession daoSession;

	private CategoryInfoDao categoryDao;
	private CustomerInfoDao customerDao;
	private DishInfoDao dishDao;
	private RemarkInfoDao remarkDao;
	private SellerInfoDao sellerDao;

	private DbHelper() {

	}

	public static DbHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DbHelper();
			if (mContext == null) {
				mContext = context;
			}
			daoSession = MobileOrderApplication.getInstance().getDaoSession();
			instance.categoryDao = daoSession.getCategoryInfoDao();
			instance.customerDao = daoSession.getCustomerInfoDao();
			instance.dishDao = daoSession.getDishInfoDao();
			instance.remarkDao = daoSession.getRemarkInfoDao();
			instance.sellerDao = daoSession.getSellerInfoDao();
		}
		return instance;
	}

	public void saveSellerInfo(SellerInfo info) throws Exception {

		if (!isExist(info.getSellerId())) {
			sellerDao.insert(info);
		}
	}

	private boolean isExist(long id) {

		QueryBuilder<SellerInfo> qb = sellerDao.queryBuilder();
		qb.where(Properties.SellerId.eq(id));
		qb.buildCount().count();
		return qb.buildCount().count() > 0 ? true : false;
	}

	/** 查询本地的菜单类目 */
	public List<CategoryInfo> getLocalCategory() {
		QueryBuilder<CategoryInfo> qb = categoryDao.queryBuilder();
		qb.orderAsc(com.eteng.mobileorder.models.CategoryInfoDao.Properties.CategoryOrder);
		qb.where(com.eteng.mobileorder.models.CategoryInfoDao.Properties.CategoryStatus
				.eq("1"));
		try{
			return qb.list();
		} catch (Exception e){
			return null;
		}
		
	}

	/** 添加菜单类目到本地 */
	public void insertCategory(CategoryInfo item) {
		categoryDao.insert(item);
	}

	/** 添加菜品到本地 */
	public void saveDish(DishInfo dishItem) {
		if (!isExist(dishItem.getDishId())) {
			dishDao.insert(dishItem);
		}
	}

	/** 查询对应类目的菜品 */
	public List<DishInfo> getLocalDish(Long categoryId, String flag) {
		QueryBuilder<DishInfo> qb = dishDao.queryBuilder();
		qb.where(com.eteng.mobileorder.models.DishInfoDao.Properties.DishCategory
				.eq(categoryId));
		qb.where(com.eteng.mobileorder.models.DishInfoDao.Properties.DishStatus
				.eq("1"));
		if (flag.equals("1")) {
			qb.where(com.eteng.mobileorder.models.DishInfoDao.Properties.DishType
					.eq(flag));
		} else {
			qb.where(com.eteng.mobileorder.models.DishInfoDao.Properties.DishType
					.notEq("1"));
		}
		qb.orderAsc(com.eteng.mobileorder.models.DishInfoDao.Properties.DishOrder);
		return qb.list();
	}

	public void saveRemark(RemarkInfo remarkItem) {
		if (!isExist(remarkItem.getId())) {
			remarkDao.insert(remarkItem);
		}
	}

	public List<RemarkInfo> getRemarkInfos(Long categoryId) {
		QueryBuilder<RemarkInfo> qb = remarkDao.queryBuilder();
		qb.where(com.eteng.mobileorder.models.RemarkInfoDao.Properties.RemarkStatus
				.eq("2"));
		qb.where(com.eteng.mobileorder.models.RemarkInfoDao.Properties.BelongsToId.eq(categoryId));
		qb.orderAsc(com.eteng.mobileorder.models.RemarkInfoDao.Properties.Order);
		return qb.list();
	}
	
	public void saveCustomerInfo(CustomerInfo customerInfo){
		customerDao.insertOrReplace(customerInfo);
	}
	
	public void saveCustomerInfos(List<CustomerInfo> customerInfos){
		customerDao.deleteAll();//清空数据库
		customerDao.insertInTx(customerInfos);
	}
	
	public List<CustomerInfo> getCustomerInfos(){
		QueryBuilder<CustomerInfo> qb = customerDao.queryBuilder();
		return qb.list();
	}
	
	public String getIncomingAddr(String tel){
		QueryBuilder<CustomerInfo> qb = customerDao.queryBuilder();
		qb.where(com.eteng.mobileorder.models.CustomerInfoDao.Properties.CustomerTel.eq(tel));
		try{
			return qb.unique().getCustomerAddr();
		}catch(Exception e){
			return "";
		}
		
	}

	
	public void clearAllDataAboutDish() {
		categoryDao.deleteAll();
		dishDao.deleteAll();
		remarkDao.deleteAll();
	}
}
