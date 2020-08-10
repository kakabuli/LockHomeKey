package com.kaadas.lock.utils.greenDao.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.kaadas.lock.publiclibrary.bean.SingleFireSwitchInfo;
import com.kaadas.lock.utils.greenDao.convert.SingleFireSwitchInfoConvert;

import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "WIFI_LOCK_INFO".
*/
public class WifiLockInfoDao extends AbstractDao<WifiLockInfo, Long> {

    public static final String TABLENAME = "WIFI_LOCK_INFO";

    /**
     * Properties of entity WifiLockInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DeviceID = new Property(1, String.class, "deviceID", false, "DEVICE_ID");
        public final static Property WifiSN = new Property(2, String.class, "wifiSN", false, "WIFI_SN");
        public final static Property IsAdmin = new Property(3, int.class, "isAdmin", false, "IS_ADMIN");
        public final static Property AdminUid = new Property(4, String.class, "adminUid", false, "ADMIN_UID");
        public final static Property AdminName = new Property(5, String.class, "adminName", false, "ADMIN_NAME");
        public final static Property ProductSN = new Property(6, String.class, "productSN", false, "PRODUCT_SN");
        public final static Property ProductModel = new Property(7, String.class, "productModel", false, "PRODUCT_MODEL");
        public final static Property AppId = new Property(8, int.class, "appId", false, "APP_ID");
        public final static Property LockNickname = new Property(9, String.class, "lockNickname", false, "LOCK_NICKNAME");
        public final static Property LockSoftwareVersion = new Property(10, String.class, "lockSoftwareVersion", false, "LOCK_SOFTWARE_VERSION");
        public final static Property FunctionSet = new Property(11, String.class, "functionSet", false, "FUNCTION_SET");
        public final static Property Uid = new Property(12, String.class, "uid", false, "UID");
        public final static Property Uname = new Property(13, String.class, "uname", false, "UNAME");
        public final static Property PushSwitch = new Property(14, int.class, "pushSwitch", false, "PUSH_SWITCH");
        public final static Property AmMode = new Property(15, int.class, "amMode", false, "AM_MODE");
        public final static Property SafeMode = new Property(16, int.class, "safeMode", false, "SAFE_MODE");
        public final static Property PowerSave = new Property(17, int.class, "powerSave", false, "POWER_SAVE");
        public final static Property FaceStatus = new Property(18, int.class, "faceStatus", false, "FACE_STATUS");
        public final static Property Defences = new Property(19, int.class, "defences", false, "DEFENCES");
        public final static Property Language = new Property(20, String.class, "language", false, "LANGUAGE");
        public final static Property OperatingMode = new Property(21, int.class, "operatingMode", false, "OPERATING_MODE");
        public final static Property Volume = new Property(22, int.class, "volume", false, "VOLUME");
        public final static Property BleVersion = new Property(23, String.class, "bleVersion", false, "BLE_VERSION");
        public final static Property WifiVersion = new Property(24, String.class, "wifiVersion", false, "WIFI_VERSION");
        public final static Property MqttVersion = new Property(25, String.class, "mqttVersion", false, "MQTT_VERSION");
        public final static Property FaceVersion = new Property(26, String.class, "faceVersion", false, "FACE_VERSION");
        public final static Property LockFirmwareVersion = new Property(27, String.class, "lockFirmwareVersion", false, "LOCK_FIRMWARE_VERSION");
        public final static Property RandomCode = new Property(28, String.class, "randomCode", false, "RANDOM_CODE");
        public final static Property CreateTime = new Property(29, long.class, "createTime", false, "CREATE_TIME");
        public final static Property WifiName = new Property(30, String.class, "wifiName", false, "WIFI_NAME");
        public final static Property Power = new Property(31, int.class, "power", false, "POWER");
        public final static Property UpdateTime = new Property(32, long.class, "updateTime", false, "UPDATE_TIME");
        public final static Property OpenStatus = new Property(33, int.class, "openStatus", false, "OPEN_STATUS");
        public final static Property OpenStatusTime = new Property(34, long.class, "openStatusTime", false, "OPEN_STATUS_TIME");
        public final static Property SingleFireSwitchInfo = new Property(35, String.class, "singleFireSwitchInfo", false, "SINGLE_FIRE_SWITCH_INFO");
    }

    private final SingleFireSwitchInfoConvert singleFireSwitchInfoConverter = new SingleFireSwitchInfoConvert();

    public WifiLockInfoDao(DaoConfig config) {
        super(config);
    }
    
    public WifiLockInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"WIFI_LOCK_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"DEVICE_ID\" TEXT," + // 1: deviceID
                "\"WIFI_SN\" TEXT," + // 2: wifiSN
                "\"IS_ADMIN\" INTEGER NOT NULL ," + // 3: isAdmin
                "\"ADMIN_UID\" TEXT," + // 4: adminUid
                "\"ADMIN_NAME\" TEXT," + // 5: adminName
                "\"PRODUCT_SN\" TEXT," + // 6: productSN
                "\"PRODUCT_MODEL\" TEXT," + // 7: productModel
                "\"APP_ID\" INTEGER NOT NULL ," + // 8: appId
                "\"LOCK_NICKNAME\" TEXT," + // 9: lockNickname
                "\"LOCK_SOFTWARE_VERSION\" TEXT," + // 10: lockSoftwareVersion
                "\"FUNCTION_SET\" TEXT," + // 11: functionSet
                "\"UID\" TEXT," + // 12: uid
                "\"UNAME\" TEXT," + // 13: uname
                "\"PUSH_SWITCH\" INTEGER NOT NULL ," + // 14: pushSwitch
                "\"AM_MODE\" INTEGER NOT NULL ," + // 15: amMode
                "\"SAFE_MODE\" INTEGER NOT NULL ," + // 16: safeMode
                "\"POWER_SAVE\" INTEGER NOT NULL ," + // 17: powerSave
                "\"FACE_STATUS\" INTEGER NOT NULL ," + // 18: faceStatus
                "\"DEFENCES\" INTEGER NOT NULL ," + // 19: defences
                "\"LANGUAGE\" TEXT," + // 20: language
                "\"OPERATING_MODE\" INTEGER NOT NULL ," + // 21: operatingMode
                "\"VOLUME\" INTEGER NOT NULL ," + // 22: volume
                "\"BLE_VERSION\" TEXT," + // 23: bleVersion
                "\"WIFI_VERSION\" TEXT," + // 24: wifiVersion
                "\"MQTT_VERSION\" TEXT," + // 25: mqttVersion
                "\"FACE_VERSION\" TEXT," + // 26: faceVersion
                "\"LOCK_FIRMWARE_VERSION\" TEXT," + // 27: lockFirmwareVersion
                "\"RANDOM_CODE\" TEXT," + // 28: randomCode
                "\"CREATE_TIME\" INTEGER NOT NULL ," + // 29: createTime
                "\"WIFI_NAME\" TEXT," + // 30: wifiName
                "\"POWER\" INTEGER NOT NULL ," + // 31: power
                "\"UPDATE_TIME\" INTEGER NOT NULL ," + // 32: updateTime
                "\"OPEN_STATUS\" INTEGER NOT NULL ," + // 33: openStatus
                "\"OPEN_STATUS_TIME\" INTEGER NOT NULL ," + // 34: openStatusTime
                "\"SINGLE_FIRE_SWITCH_INFO\" TEXT);"); // 35: singleFireSwitchInfo
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"WIFI_LOCK_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, WifiLockInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String deviceID = entity.getDeviceID();
        if (deviceID != null) {
            stmt.bindString(2, deviceID);
        }
 
        String wifiSN = entity.getWifiSN();
        if (wifiSN != null) {
            stmt.bindString(3, wifiSN);
        }
        stmt.bindLong(4, entity.getIsAdmin());
 
        String adminUid = entity.getAdminUid();
        if (adminUid != null) {
            stmt.bindString(5, adminUid);
        }
 
        String adminName = entity.getAdminName();
        if (adminName != null) {
            stmt.bindString(6, adminName);
        }
 
        String productSN = entity.getProductSN();
        if (productSN != null) {
            stmt.bindString(7, productSN);
        }
 
        String productModel = entity.getProductModel();
        if (productModel != null) {
            stmt.bindString(8, productModel);
        }
        stmt.bindLong(9, entity.getAppId());
 
        String lockNickname = entity.getLockNickname();
        if (lockNickname != null) {
            stmt.bindString(10, lockNickname);
        }
 
        String lockSoftwareVersion = entity.getLockSoftwareVersion();
        if (lockSoftwareVersion != null) {
            stmt.bindString(11, lockSoftwareVersion);
        }
 
        String functionSet = entity.getFunctionSet();
        if (functionSet != null) {
            stmt.bindString(12, functionSet);
        }
 
        String uid = entity.getUid();
        if (uid != null) {
            stmt.bindString(13, uid);
        }
 
        String uname = entity.getUname();
        if (uname != null) {
            stmt.bindString(14, uname);
        }
        stmt.bindLong(15, entity.getPushSwitch());
        stmt.bindLong(16, entity.getAmMode());
        stmt.bindLong(17, entity.getSafeMode());
        stmt.bindLong(18, entity.getPowerSave());
        stmt.bindLong(19, entity.getFaceStatus());
        stmt.bindLong(20, entity.getDefences());
 
        String language = entity.getLanguage();
        if (language != null) {
            stmt.bindString(21, language);
        }
        stmt.bindLong(22, entity.getOperatingMode());
        stmt.bindLong(23, entity.getVolume());
 
        String bleVersion = entity.getBleVersion();
        if (bleVersion != null) {
            stmt.bindString(24, bleVersion);
        }
 
        String wifiVersion = entity.getWifiVersion();
        if (wifiVersion != null) {
            stmt.bindString(25, wifiVersion);
        }
 
        String mqttVersion = entity.getMqttVersion();
        if (mqttVersion != null) {
            stmt.bindString(26, mqttVersion);
        }
 
        String faceVersion = entity.getFaceVersion();
        if (faceVersion != null) {
            stmt.bindString(27, faceVersion);
        }
 
        String lockFirmwareVersion = entity.getLockFirmwareVersion();
        if (lockFirmwareVersion != null) {
            stmt.bindString(28, lockFirmwareVersion);
        }
 
        String randomCode = entity.getRandomCode();
        if (randomCode != null) {
            stmt.bindString(29, randomCode);
        }
        stmt.bindLong(30, entity.getCreateTime());
 
        String wifiName = entity.getWifiName();
        if (wifiName != null) {
            stmt.bindString(31, wifiName);
        }
        stmt.bindLong(32, entity.getPower());
        stmt.bindLong(33, entity.getUpdateTime());
        stmt.bindLong(34, entity.getOpenStatus());
        stmt.bindLong(35, entity.getOpenStatusTime());
 
        SingleFireSwitchInfo singleFireSwitchInfo = entity.getSingleFireSwitchInfo();
        if (singleFireSwitchInfo != null) {
            stmt.bindString(36, singleFireSwitchInfoConverter.convertToDatabaseValue(singleFireSwitchInfo));
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, WifiLockInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String deviceID = entity.getDeviceID();
        if (deviceID != null) {
            stmt.bindString(2, deviceID);
        }
 
        String wifiSN = entity.getWifiSN();
        if (wifiSN != null) {
            stmt.bindString(3, wifiSN);
        }
        stmt.bindLong(4, entity.getIsAdmin());
 
        String adminUid = entity.getAdminUid();
        if (adminUid != null) {
            stmt.bindString(5, adminUid);
        }
 
        String adminName = entity.getAdminName();
        if (adminName != null) {
            stmt.bindString(6, adminName);
        }
 
        String productSN = entity.getProductSN();
        if (productSN != null) {
            stmt.bindString(7, productSN);
        }
 
        String productModel = entity.getProductModel();
        if (productModel != null) {
            stmt.bindString(8, productModel);
        }
        stmt.bindLong(9, entity.getAppId());
 
        String lockNickname = entity.getLockNickname();
        if (lockNickname != null) {
            stmt.bindString(10, lockNickname);
        }
 
        String lockSoftwareVersion = entity.getLockSoftwareVersion();
        if (lockSoftwareVersion != null) {
            stmt.bindString(11, lockSoftwareVersion);
        }
 
        String functionSet = entity.getFunctionSet();
        if (functionSet != null) {
            stmt.bindString(12, functionSet);
        }
 
        String uid = entity.getUid();
        if (uid != null) {
            stmt.bindString(13, uid);
        }
 
        String uname = entity.getUname();
        if (uname != null) {
            stmt.bindString(14, uname);
        }
        stmt.bindLong(15, entity.getPushSwitch());
        stmt.bindLong(16, entity.getAmMode());
        stmt.bindLong(17, entity.getSafeMode());
        stmt.bindLong(18, entity.getPowerSave());
        stmt.bindLong(19, entity.getFaceStatus());
        stmt.bindLong(20, entity.getDefences());
 
        String language = entity.getLanguage();
        if (language != null) {
            stmt.bindString(21, language);
        }
        stmt.bindLong(22, entity.getOperatingMode());
        stmt.bindLong(23, entity.getVolume());
 
        String bleVersion = entity.getBleVersion();
        if (bleVersion != null) {
            stmt.bindString(24, bleVersion);
        }
 
        String wifiVersion = entity.getWifiVersion();
        if (wifiVersion != null) {
            stmt.bindString(25, wifiVersion);
        }
 
        String mqttVersion = entity.getMqttVersion();
        if (mqttVersion != null) {
            stmt.bindString(26, mqttVersion);
        }
 
        String faceVersion = entity.getFaceVersion();
        if (faceVersion != null) {
            stmt.bindString(27, faceVersion);
        }
 
        String lockFirmwareVersion = entity.getLockFirmwareVersion();
        if (lockFirmwareVersion != null) {
            stmt.bindString(28, lockFirmwareVersion);
        }
 
        String randomCode = entity.getRandomCode();
        if (randomCode != null) {
            stmt.bindString(29, randomCode);
        }
        stmt.bindLong(30, entity.getCreateTime());
 
        String wifiName = entity.getWifiName();
        if (wifiName != null) {
            stmt.bindString(31, wifiName);
        }
        stmt.bindLong(32, entity.getPower());
        stmt.bindLong(33, entity.getUpdateTime());
        stmt.bindLong(34, entity.getOpenStatus());
        stmt.bindLong(35, entity.getOpenStatusTime());
 
        SingleFireSwitchInfo singleFireSwitchInfo = entity.getSingleFireSwitchInfo();
        if (singleFireSwitchInfo != null) {
            stmt.bindString(36, singleFireSwitchInfoConverter.convertToDatabaseValue(singleFireSwitchInfo));
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public WifiLockInfo readEntity(Cursor cursor, int offset) {
        WifiLockInfo entity = new WifiLockInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // deviceID
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // wifiSN
            cursor.getInt(offset + 3), // isAdmin
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // adminUid
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // adminName
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // productSN
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // productModel
            cursor.getInt(offset + 8), // appId
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // lockNickname
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // lockSoftwareVersion
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // functionSet
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // uid
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // uname
            cursor.getInt(offset + 14), // pushSwitch
            cursor.getInt(offset + 15), // amMode
            cursor.getInt(offset + 16), // safeMode
            cursor.getInt(offset + 17), // powerSave
            cursor.getInt(offset + 18), // faceStatus
            cursor.getInt(offset + 19), // defences
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // language
            cursor.getInt(offset + 21), // operatingMode
            cursor.getInt(offset + 22), // volume
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23), // bleVersion
            cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24), // wifiVersion
            cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25), // mqttVersion
            cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26), // faceVersion
            cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27), // lockFirmwareVersion
            cursor.isNull(offset + 28) ? null : cursor.getString(offset + 28), // randomCode
            cursor.getLong(offset + 29), // createTime
            cursor.isNull(offset + 30) ? null : cursor.getString(offset + 30), // wifiName
            cursor.getInt(offset + 31), // power
            cursor.getLong(offset + 32), // updateTime
            cursor.getInt(offset + 33), // openStatus
            cursor.getLong(offset + 34), // openStatusTime
            cursor.isNull(offset + 35) ? null : singleFireSwitchInfoConverter.convertToEntityProperty(cursor.getString(offset + 35)) // singleFireSwitchInfo
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, WifiLockInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDeviceID(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setWifiSN(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setIsAdmin(cursor.getInt(offset + 3));
        entity.setAdminUid(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setAdminName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setProductSN(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setProductModel(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setAppId(cursor.getInt(offset + 8));
        entity.setLockNickname(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setLockSoftwareVersion(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setFunctionSet(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setUid(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setUname(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setPushSwitch(cursor.getInt(offset + 14));
        entity.setAmMode(cursor.getInt(offset + 15));
        entity.setSafeMode(cursor.getInt(offset + 16));
        entity.setPowerSave(cursor.getInt(offset + 17));
        entity.setFaceStatus(cursor.getInt(offset + 18));
        entity.setDefences(cursor.getInt(offset + 19));
        entity.setLanguage(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setOperatingMode(cursor.getInt(offset + 21));
        entity.setVolume(cursor.getInt(offset + 22));
        entity.setBleVersion(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
        entity.setWifiVersion(cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24));
        entity.setMqttVersion(cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25));
        entity.setFaceVersion(cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26));
        entity.setLockFirmwareVersion(cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27));
        entity.setRandomCode(cursor.isNull(offset + 28) ? null : cursor.getString(offset + 28));
        entity.setCreateTime(cursor.getLong(offset + 29));
        entity.setWifiName(cursor.isNull(offset + 30) ? null : cursor.getString(offset + 30));
        entity.setPower(cursor.getInt(offset + 31));
        entity.setUpdateTime(cursor.getLong(offset + 32));
        entity.setOpenStatus(cursor.getInt(offset + 33));
        entity.setOpenStatusTime(cursor.getLong(offset + 34));
        entity.setSingleFireSwitchInfo(cursor.isNull(offset + 35) ? null : singleFireSwitchInfoConverter.convertToEntityProperty(cursor.getString(offset + 35)));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(WifiLockInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(WifiLockInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(WifiLockInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
