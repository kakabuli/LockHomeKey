package com.kaadas.lock.utils.greenDao.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.kaadas.lock.utils.greenDao.bean.GatewayLockRecord;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "GATEWAY_LOCK_RECORD".
*/
public class GatewayLockRecordDao extends AbstractDao<GatewayLockRecord, String> {

    public static final String TABLENAME = "GATEWAY_LOCK_RECORD";

    /**
     * Properties of entity GatewayLockRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property DeviceIdUid = new Property(0, String.class, "deviceIdUid", true, "DEVICE_ID_UID");
        public final static Property DeviceId = new Property(1, String.class, "deviceId", false, "DEVICE_ID");
        public final static Property GatewayId = new Property(2, String.class, "gatewayId", false, "GATEWAY_ID");
        public final static Property Uid = new Property(3, String.class, "uid", false, "UID");
        public final static Property LockName = new Property(4, String.class, "lockName", false, "LOCK_NAME");
        public final static Property VersionType = new Property(5, String.class, "versionType", false, "VERSION_TYPE");
        public final static Property LockNickName = new Property(6, String.class, "lockNickName", false, "LOCK_NICK_NAME");
        public final static Property NickName = new Property(7, String.class, "nickName", false, "NICK_NAME");
        public final static Property Uname = new Property(8, String.class, "uname", false, "UNAME");
        public final static Property Open_purview = new Property(9, String.class, "open_purview", false, "OPEN_PURVIEW");
        public final static Property Open_time = new Property(10, String.class, "open_time", false, "OPEN_TIME");
        public final static Property Open_type = new Property(11, String.class, "open_type", false, "OPEN_TYPE");
    }


    public GatewayLockRecordDao(DaoConfig config) {
        super(config);
    }
    
    public GatewayLockRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"GATEWAY_LOCK_RECORD\" (" + //
                "\"DEVICE_ID_UID\" TEXT PRIMARY KEY NOT NULL ," + // 0: deviceIdUid
                "\"DEVICE_ID\" TEXT," + // 1: deviceId
                "\"GATEWAY_ID\" TEXT," + // 2: gatewayId
                "\"UID\" TEXT," + // 3: uid
                "\"LOCK_NAME\" TEXT," + // 4: lockName
                "\"VERSION_TYPE\" TEXT," + // 5: versionType
                "\"LOCK_NICK_NAME\" TEXT," + // 6: lockNickName
                "\"NICK_NAME\" TEXT," + // 7: nickName
                "\"UNAME\" TEXT," + // 8: uname
                "\"OPEN_PURVIEW\" TEXT," + // 9: open_purview
                "\"OPEN_TIME\" TEXT," + // 10: open_time
                "\"OPEN_TYPE\" TEXT);"); // 11: open_type
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"GATEWAY_LOCK_RECORD\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, GatewayLockRecord entity) {
        stmt.clearBindings();
 
        String deviceIdUid = entity.getDeviceIdUid();
        if (deviceIdUid != null) {
            stmt.bindString(1, deviceIdUid);
        }
 
        String deviceId = entity.getDeviceId();
        if (deviceId != null) {
            stmt.bindString(2, deviceId);
        }
 
        String gatewayId = entity.getGatewayId();
        if (gatewayId != null) {
            stmt.bindString(3, gatewayId);
        }
 
        String uid = entity.getUid();
        if (uid != null) {
            stmt.bindString(4, uid);
        }
 
        String lockName = entity.getLockName();
        if (lockName != null) {
            stmt.bindString(5, lockName);
        }
 
        String versionType = entity.getVersionType();
        if (versionType != null) {
            stmt.bindString(6, versionType);
        }
 
        String lockNickName = entity.getLockNickName();
        if (lockNickName != null) {
            stmt.bindString(7, lockNickName);
        }
 
        String nickName = entity.getNickName();
        if (nickName != null) {
            stmt.bindString(8, nickName);
        }
 
        String uname = entity.getUname();
        if (uname != null) {
            stmt.bindString(9, uname);
        }
 
        String open_purview = entity.getOpen_purview();
        if (open_purview != null) {
            stmt.bindString(10, open_purview);
        }
 
        String open_time = entity.getOpen_time();
        if (open_time != null) {
            stmt.bindString(11, open_time);
        }
 
        String open_type = entity.getOpen_type();
        if (open_type != null) {
            stmt.bindString(12, open_type);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, GatewayLockRecord entity) {
        stmt.clearBindings();
 
        String deviceIdUid = entity.getDeviceIdUid();
        if (deviceIdUid != null) {
            stmt.bindString(1, deviceIdUid);
        }
 
        String deviceId = entity.getDeviceId();
        if (deviceId != null) {
            stmt.bindString(2, deviceId);
        }
 
        String gatewayId = entity.getGatewayId();
        if (gatewayId != null) {
            stmt.bindString(3, gatewayId);
        }
 
        String uid = entity.getUid();
        if (uid != null) {
            stmt.bindString(4, uid);
        }
 
        String lockName = entity.getLockName();
        if (lockName != null) {
            stmt.bindString(5, lockName);
        }
 
        String versionType = entity.getVersionType();
        if (versionType != null) {
            stmt.bindString(6, versionType);
        }
 
        String lockNickName = entity.getLockNickName();
        if (lockNickName != null) {
            stmt.bindString(7, lockNickName);
        }
 
        String nickName = entity.getNickName();
        if (nickName != null) {
            stmt.bindString(8, nickName);
        }
 
        String uname = entity.getUname();
        if (uname != null) {
            stmt.bindString(9, uname);
        }
 
        String open_purview = entity.getOpen_purview();
        if (open_purview != null) {
            stmt.bindString(10, open_purview);
        }
 
        String open_time = entity.getOpen_time();
        if (open_time != null) {
            stmt.bindString(11, open_time);
        }
 
        String open_type = entity.getOpen_type();
        if (open_type != null) {
            stmt.bindString(12, open_type);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public GatewayLockRecord readEntity(Cursor cursor, int offset) {
        GatewayLockRecord entity = new GatewayLockRecord( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // deviceIdUid
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // deviceId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // gatewayId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // uid
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // lockName
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // versionType
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // lockNickName
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // nickName
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // uname
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // open_purview
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // open_time
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11) // open_type
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, GatewayLockRecord entity, int offset) {
        entity.setDeviceIdUid(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setDeviceId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setGatewayId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setUid(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setLockName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setVersionType(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setLockNickName(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setNickName(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setUname(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setOpen_purview(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setOpen_time(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setOpen_type(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
     }
    
    @Override
    protected final String updateKeyAfterInsert(GatewayLockRecord entity, long rowId) {
        return entity.getDeviceIdUid();
    }
    
    @Override
    public String getKey(GatewayLockRecord entity) {
        if(entity != null) {
            return entity.getDeviceIdUid();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(GatewayLockRecord entity) {
        return entity.getDeviceIdUid() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
