package com.eteng.mobileorder.models;

import java.util.List;
import com.eteng.mobileorder.models.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table SELLER_INFO.
 */
public class SellerInfo {

    private long sellerId;
    private String sellerName;
    private String sellerTel;
    private String sellerDetail;
    private String sellerScope;
    private String sellerAddr;
    private String sellerImg;
    private String sellerAccount;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient SellerInfoDao myDao;

    private List<CategoryInfo> category;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public SellerInfo() {
    }

    public SellerInfo(long sellerId) {
        this.sellerId = sellerId;
    }

    public SellerInfo(long sellerId, String sellerName, String sellerTel, String sellerDetail, String sellerScope, String sellerAddr, String sellerImg, String sellerAccount) {
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.sellerTel = sellerTel;
        this.sellerDetail = sellerDetail;
        this.sellerScope = sellerScope;
        this.sellerAddr = sellerAddr;
        this.sellerImg = sellerImg;
        this.sellerAccount = sellerAccount;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSellerInfoDao() : null;
    }

    public long getSellerId() {
        return sellerId;
    }

    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerTel() {
        return sellerTel;
    }

    public void setSellerTel(String sellerTel) {
        this.sellerTel = sellerTel;
    }

    public String getSellerDetail() {
        return sellerDetail;
    }

    public void setSellerDetail(String sellerDetail) {
        this.sellerDetail = sellerDetail;
    }

    public String getSellerScope() {
        return sellerScope;
    }

    public void setSellerScope(String sellerScope) {
        this.sellerScope = sellerScope;
    }

    public String getSellerAddr() {
        return sellerAddr;
    }

    public void setSellerAddr(String sellerAddr) {
        this.sellerAddr = sellerAddr;
    }

    public String getSellerImg() {
        return sellerImg;
    }

    public void setSellerImg(String sellerImg) {
        this.sellerImg = sellerImg;
    }

    public String getSellerAccount() {
        return sellerAccount;
    }

    public void setSellerAccount(String sellerAccount) {
        this.sellerAccount = sellerAccount;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<CategoryInfo> getCategory() {
        if (category == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CategoryInfoDao targetDao = daoSession.getCategoryInfoDao();
            List<CategoryInfo> categoryNew = targetDao._querySellerInfo_Category(sellerId);
            synchronized (this) {
                if(category == null) {
                    category = categoryNew;
                }
            }
        }
        return category;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetCategory() {
        category = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}