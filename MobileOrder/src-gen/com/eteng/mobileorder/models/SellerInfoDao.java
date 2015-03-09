package com.eteng.mobileorder.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.eteng.mobileorder.models.SellerInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table SELLER_INFO.
*/
public class SellerInfoDao extends AbstractDao<SellerInfo, Long> {

    public static final String TABLENAME = "SELLER_INFO";

    /**
     * Properties of entity SellerInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property SellerId = new Property(0, long.class, "sellerId", true, "SELLER_ID");
        public final static Property SellerName = new Property(1, String.class, "sellerName", false, "SELLER_NAME");
        public final static Property SellerTel = new Property(2, String.class, "sellerTel", false, "SELLER_TEL");
        public final static Property SellerDetail = new Property(3, String.class, "sellerDetail", false, "SELLER_DETAIL");
        public final static Property SellerScope = new Property(4, String.class, "sellerScope", false, "SELLER_SCOPE");
        public final static Property SellerAddr = new Property(5, String.class, "sellerAddr", false, "SELLER_ADDR");
        public final static Property SellerImg = new Property(6, String.class, "sellerImg", false, "SELLER_IMG");
        public final static Property SellerAccount = new Property(7, String.class, "sellerAccount", false, "SELLER_ACCOUNT");
    };

    private DaoSession daoSession;


    public SellerInfoDao(DaoConfig config) {
        super(config);
    }
    
    public SellerInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'SELLER_INFO' (" + //
                "'SELLER_ID' INTEGER PRIMARY KEY NOT NULL ," + // 0: sellerId
                "'SELLER_NAME' TEXT," + // 1: sellerName
                "'SELLER_TEL' TEXT," + // 2: sellerTel
                "'SELLER_DETAIL' TEXT," + // 3: sellerDetail
                "'SELLER_SCOPE' TEXT," + // 4: sellerScope
                "'SELLER_ADDR' TEXT," + // 5: sellerAddr
                "'SELLER_IMG' TEXT," + // 6: sellerImg
                "'SELLER_ACCOUNT' TEXT);"); // 7: sellerAccount
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SELLER_INFO'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SellerInfo entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getSellerId());
 
        String sellerName = entity.getSellerName();
        if (sellerName != null) {
            stmt.bindString(2, sellerName);
        }
 
        String sellerTel = entity.getSellerTel();
        if (sellerTel != null) {
            stmt.bindString(3, sellerTel);
        }
 
        String sellerDetail = entity.getSellerDetail();
        if (sellerDetail != null) {
            stmt.bindString(4, sellerDetail);
        }
 
        String sellerScope = entity.getSellerScope();
        if (sellerScope != null) {
            stmt.bindString(5, sellerScope);
        }
 
        String sellerAddr = entity.getSellerAddr();
        if (sellerAddr != null) {
            stmt.bindString(6, sellerAddr);
        }
 
        String sellerImg = entity.getSellerImg();
        if (sellerImg != null) {
            stmt.bindString(7, sellerImg);
        }
 
        String sellerAccount = entity.getSellerAccount();
        if (sellerAccount != null) {
            stmt.bindString(8, sellerAccount);
        }
    }

    @Override
    protected void attachEntity(SellerInfo entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public SellerInfo readEntity(Cursor cursor, int offset) {
        SellerInfo entity = new SellerInfo( //
            cursor.getLong(offset + 0), // sellerId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // sellerName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // sellerTel
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // sellerDetail
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // sellerScope
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // sellerAddr
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // sellerImg
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // sellerAccount
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SellerInfo entity, int offset) {
        entity.setSellerId(cursor.getLong(offset + 0));
        entity.setSellerName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSellerTel(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSellerDetail(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSellerScope(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setSellerAddr(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSellerImg(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setSellerAccount(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SellerInfo entity, long rowId) {
        entity.setSellerId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SellerInfo entity) {
        if(entity != null) {
            return entity.getSellerId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}