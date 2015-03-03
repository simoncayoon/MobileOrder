package com.eteng.mobileorder.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.eteng.mobileorder.models.CustomerInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table CUSTOMER_INFO.
*/
public class CustomerInfoDao extends AbstractDao<CustomerInfo, Long> {

    public static final String TABLENAME = "CUSTOMER_INFO";

    /**
     * Properties of entity CustomerInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CustomerTel = new Property(1, String.class, "customerTel", false, "CUSTOMER_TEL");
        public final static Property CustomerAddr = new Property(2, String.class, "customerAddr", false, "CUSTOMER_ADDR");
    };


    public CustomerInfoDao(DaoConfig config) {
        super(config);
    }
    
    public CustomerInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'CUSTOMER_INFO' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'CUSTOMER_TEL' TEXT UNIQUE ," + // 1: customerTel
                "'CUSTOMER_ADDR' TEXT);"); // 2: customerAddr
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'CUSTOMER_INFO'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, CustomerInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String customerTel = entity.getCustomerTel();
        if (customerTel != null) {
            stmt.bindString(2, customerTel);
        }
 
        String customerAddr = entity.getCustomerAddr();
        if (customerAddr != null) {
            stmt.bindString(3, customerAddr);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public CustomerInfo readEntity(Cursor cursor, int offset) {
        CustomerInfo entity = new CustomerInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // customerTel
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // customerAddr
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, CustomerInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCustomerTel(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCustomerAddr(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(CustomerInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(CustomerInfo entity) {
        if(entity != null) {
            return entity.getId();
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
