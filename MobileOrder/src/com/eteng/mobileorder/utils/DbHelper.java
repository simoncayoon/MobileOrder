package com.eteng.mobileorder.utils;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eteng.mobileorder.MobileOrderApplication;
import com.eteng.mobileorder.debug.DebugFlags;
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

	private static final String TAG = DbHelper.class.getSimpleName();
	
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

		if (!isExist(info.getSellerId())) {// 不存在该用户
			sellerDao.insert(info);
		} else {
			sellerDao.update(info);
			;
		}
	}

	public boolean isExist(long id) {
		QueryBuilder<SellerInfo> qb = sellerDao.queryBuilder();
		qb.where(Properties.SellerId.eq(id));
		qb.buildCount().count();
		return qb.buildCount().count() > 0 ? true : false;
	}

	/** 查询本地的菜单类目 */
	public List<CategoryInfo> getLocalCategory() {
		DebugFlags.logD(TAG, "getLocalCategory   " + TempDataManager.getInstance(mContext).getSellerId());
		QueryBuilder<CategoryInfo> qb = categoryDao.queryBuilder();
		qb.orderAsc(com.eteng.mobileorder.models.CategoryInfoDao.Properties.CategoryOrder);
		qb.where(com.eteng.mobileorder.models.CategoryInfoDao.Properties.CategoryStatus
				.eq("1"));
		qb.where(com.eteng.mobileorder.models.CategoryInfoDao.Properties.SellerId
				.eq(TempDataManager.getInstance(mContext).getSellerId()));
		try {
			return qb.list();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 交换排序
	 */
	public void exchangeCategorySort(Long fromId, Long toId) {
		Integer temp = -1;
		QueryBuilder<CategoryInfo> qb_From = categoryDao.queryBuilder();
		qb_From.where(com.eteng.mobileorder.models.CategoryInfoDao.Properties.CategoryId
				.eq(fromId));
		QueryBuilder<CategoryInfo> qb_To = categoryDao.queryBuilder();
		qb_To.where(com.eteng.mobileorder.models.CategoryInfoDao.Properties.CategoryId
				.eq(toId));
		try {
			CategoryInfo fromEntity = qb_From.list().get(0);
			CategoryInfo toEntity = qb_To.list().get(0);
			temp = fromEntity.getCategoryOrder();
			fromEntity.setCategoryOrder(toEntity.getCategoryOrder());
			toEntity.setCategoryOrder(temp);
			categoryDao.update(toEntity);
			categoryDao.update(fromEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeShownState(CategoryInfo item) {
		try {
			categoryDao.update(item);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 添加菜单类目到本地 */
	public void insertCategory(CategoryInfo item) {
		try {
			categoryDao.insert(item);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void categoryNameEdit(Long categoryId, String newName){
		try {
			QueryBuilder<CategoryInfo> qb = categoryDao.queryBuilder();
			qb.where(com.eteng.mobileorder.models.CategoryInfoDao.Properties.SellerId
					.eq(TempDataManager.getInstance(mContext).getSellerId()));
			qb.where(com.eteng.mobileorder.models.CategoryInfoDao.Properties.CategoryId
					.eq(categoryId));
			CategoryInfo temp = qb.list().get(0);
			temp.setCategoryName(newName);
			categoryDao.update(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	/**
	 * 根据菜品ID删除本地数据
	 * @param Id
	 */
	public void deleteDishById(Long Id){
		try {
			dishDao.deleteByKey(Id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void exchangeDishOrder(Long fromId, Long toId){
		Integer temp = -1;
		QueryBuilder<DishInfo> qb_From = dishDao.queryBuilder();
		qb_From.where(com.eteng.mobileorder.models.DishInfoDao.Properties.DishId
				.eq(fromId));
		QueryBuilder<DishInfo> qb_To = dishDao.queryBuilder();
		qb_To.where(com.eteng.mobileorder.models.DishInfoDao.Properties.DishId
				.eq(toId));
		try {
			DishInfo fromEntity = qb_From.list().get(0);
			DishInfo toEntity = qb_To.list().get(0);
			temp = fromEntity.getDishOrder();
			fromEntity.setDishOrder(toEntity.getDishOrder());
			toEntity.setDishOrder(temp);
			dishDao.update(toEntity);
			dishDao.update(fromEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveRemark(RemarkInfo remarkItem) {
		if (!isExist(remarkItem.getId())) {
			remarkDao.insert(remarkItem);
		}
	}

	public List<RemarkInfo> getRemarkInfos(Long categoryId) {
		QueryBuilder<RemarkInfo> qb = remarkDao.queryBuilder();
		qb.where(com.eteng.mobileorder.models.RemarkInfoDao.Properties.BelongsToId
				.eq(categoryId));
		qb.orderAsc(com.eteng.mobileorder.models.RemarkInfoDao.Properties.Order);
		return qb.list();
	}

	public void saveCustomerInfo(CustomerInfo customerInfo) {
		customerDao.insertOrReplace(customerInfo);
	}

	public void saveCustomerInfos(List<CustomerInfo> customerInfos) {
		// customerDao.deleteAll();// 清空数据库
		Long currentSellerId = customerInfos.get(0).getSellerId();
		try {
			customerDao.getDatabase().delete("CUSTOMER_INFO", "SELLER_ID=?",
					new String[] { String.valueOf(currentSellerId) });
			customerDao.insertInTx(customerInfos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<CustomerInfo> getCustomerInfos(Long sellerId) {
		ArrayList<CustomerInfo> listCustomer = new ArrayList<CustomerInfo>();
		QueryBuilder<CustomerInfo> qb = customerDao.queryBuilder();
		qb.where(com.eteng.mobileorder.models.CustomerInfoDao.Properties.SellerId
				.eq(sellerId));
		try {
			listCustomer = (ArrayList<CustomerInfo>) qb.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listCustomer;
	}

	public String getIncomingAddr(String tel) {
		QueryBuilder<CustomerInfo> qb = customerDao.queryBuilder();
		qb.where(com.eteng.mobileorder.models.CustomerInfoDao.Properties.CustomerTel
				.eq(tel));
		try {
			return qb.unique().getCustomerAddr();
		} catch (Exception e) {
			return "";
		}
	}

	public void clearAllDataAboutDish() {
		categoryDao.deleteAll();
		dishDao.deleteAll();
		remarkDao.deleteAll();
	}
}
