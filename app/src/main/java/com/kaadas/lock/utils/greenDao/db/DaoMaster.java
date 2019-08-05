package com.kaadas.lock.utils.greenDao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;
import org.greenrobot.greendao.identityscope.IdentityScopeType;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/**
 * Master of DAO (schema version 13): knows all DAOs.
 */
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 13;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(Database db, boolean ifNotExists) {
        BleLockServiceInfoDao.createTable(db, ifNotExists);
        CateEyeInfoBaseDao.createTable(db, ifNotExists);
        CatEyeEventDao.createTable(db, ifNotExists);
        CatEyeServiceInfoDao.createTable(db, ifNotExists);
        DBOpenLockRecordDao.createTable(db, ifNotExists);
        DevicePowerDao.createTable(db, ifNotExists);
        GatewayBaseInfoDao.createTable(db, ifNotExists);
        GatewayLockAlarmEventDaoDao.createTable(db, ifNotExists);
        GatewayLockBaseInfoDao.createTable(db, ifNotExists);
        GatewayLockPwdDao.createTable(db, ifNotExists);
        GatewayLockRecordDao.createTable(db, ifNotExists);
        GatewayLockServiceInfoDao.createTable(db, ifNotExists);
        GatewayServiceInfoDao.createTable(db, ifNotExists);
        HistoryInfoDao.createTable(db, ifNotExists);
        PirDefaultDao.createTable(db, ifNotExists);
    }

    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(Database db, boolean ifExists) {
        BleLockServiceInfoDao.dropTable(db, ifExists);
        CateEyeInfoBaseDao.dropTable(db, ifExists);
        CatEyeEventDao.dropTable(db, ifExists);
        CatEyeServiceInfoDao.dropTable(db, ifExists);
        DBOpenLockRecordDao.dropTable(db, ifExists);
        DevicePowerDao.dropTable(db, ifExists);
        GatewayBaseInfoDao.dropTable(db, ifExists);
        GatewayLockAlarmEventDaoDao.dropTable(db, ifExists);
        GatewayLockBaseInfoDao.dropTable(db, ifExists);
        GatewayLockPwdDao.dropTable(db, ifExists);
        GatewayLockRecordDao.dropTable(db, ifExists);
        GatewayLockServiceInfoDao.dropTable(db, ifExists);
        GatewayServiceInfoDao.dropTable(db, ifExists);
        HistoryInfoDao.dropTable(db, ifExists);
        PirDefaultDao.dropTable(db, ifExists);
    }

    /**
     * WARNING: Drops all table on Upgrade! Use only during development.
     * Convenience method using a {@link DevOpenHelper}.
     */
    public static DaoSession newDevSession(Context context, String name) {
        Database db = new DevOpenHelper(context, name).getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }

    public DaoMaster(SQLiteDatabase db) {
        this(new StandardDatabase(db));
    }

    public DaoMaster(Database db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(BleLockServiceInfoDao.class);
        registerDaoClass(CateEyeInfoBaseDao.class);
        registerDaoClass(CatEyeEventDao.class);
        registerDaoClass(CatEyeServiceInfoDao.class);
        registerDaoClass(DBOpenLockRecordDao.class);
        registerDaoClass(DevicePowerDao.class);
        registerDaoClass(GatewayBaseInfoDao.class);
        registerDaoClass(GatewayLockAlarmEventDaoDao.class);
        registerDaoClass(GatewayLockBaseInfoDao.class);
        registerDaoClass(GatewayLockPwdDao.class);
        registerDaoClass(GatewayLockRecordDao.class);
        registerDaoClass(GatewayLockServiceInfoDao.class);
        registerDaoClass(GatewayServiceInfoDao.class);
        registerDaoClass(HistoryInfoDao.class);
        registerDaoClass(PirDefaultDao.class);
    }

    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }

    /**
     * Calls {@link #createAllTables(Database, boolean)} in {@link #onCreate(Database)} -
     */
    public static abstract class OpenHelper extends DatabaseOpenHelper {
        public OpenHelper(Context context, String name) {
            super(context, name, SCHEMA_VERSION);
        }

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(Database db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }

    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name) {
            super(context, name);
        }

        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

}
