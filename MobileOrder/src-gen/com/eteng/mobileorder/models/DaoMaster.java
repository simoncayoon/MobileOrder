package com.eteng.mobileorder.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

import com.eteng.mobileorder.models.SellerInfoDao;
import com.eteng.mobileorder.models.CategoryInfoDao;
import com.eteng.mobileorder.models.DishInfoDao;
import com.eteng.mobileorder.models.CustomerInfoDao;
import com.eteng.mobileorder.models.RemarkInfoDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 20150226): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 20150226;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        SellerInfoDao.createTable(db, ifNotExists);
        CategoryInfoDao.createTable(db, ifNotExists);
        DishInfoDao.createTable(db, ifNotExists);
        CustomerInfoDao.createTable(db, ifNotExists);
        RemarkInfoDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        SellerInfoDao.dropTable(db, ifExists);
        CategoryInfoDao.dropTable(db, ifExists);
        DishInfoDao.dropTable(db, ifExists);
        CustomerInfoDao.dropTable(db, ifExists);
        RemarkInfoDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(SellerInfoDao.class);
        registerDaoClass(CategoryInfoDao.class);
        registerDaoClass(DishInfoDao.class);
        registerDaoClass(CustomerInfoDao.class);
        registerDaoClass(RemarkInfoDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
