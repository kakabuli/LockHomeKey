package com.kaadas.lock.utils.greenDao.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.kaadas.lock.publiclibrary.bean.SingleFireSwitchInfo;
import com.kaadas.lock.publiclibrary.bean.WifiVideoLockAliveTimeBean;
import com.kaadas.lock.publiclibrary.bean.WifiVideoLockSetPirBean;
import com.kaadas.lock.utils.greenDao.convert.SingleFireSwitchInfoConvert;
import com.kaadas.lock.utils.greenDao.convert.WifiVideoAliveTimeBeanConvert;
import com.kaadas.lock.utils.greenDao.convert.WifiVideoLockSetPirConvert;

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
        public final static Property HoverAlarm = new Property(23, int.class, "hoverAlarm", false, "HOVER_ALARM");
        public final static Property HoverAlarmLevel = new Property(24, int.class, "hoverAlarmLevel", false, "HOVER_ALARM_LEVEL");
        public final static Property BleVersion = new Property(25, String.class, "bleVersion", false, "BLE_VERSION");
        public final static Property WifiVersion = new Property(26, String.class, "wifiVersion", false, "WIFI_VERSION");
        public final static Property MqttVersion = new Property(27, String.class, "mqttVersion", false, "MQTT_VERSION");
        public final static Property FaceVersion = new Property(28, String.class, "faceVersion", false, "FACE_VERSION");
        public final static Property LockFirmwareVersion = new Property(29, String.class, "lockFirmwareVersion", false, "LOCK_FIRMWARE_VERSION");
        public final static Property RandomCode = new Property(30, String.class, "randomCode", false, "RANDOM_CODE");
        public final static Property DistributionNetwork = new Property(31, int.class, "distributionNetwork", false, "DISTRIBUTION_NETWORK");
        public final static Property CreateTime = new Property(32, long.class, "createTime", false, "CREATE_TIME");
        public final static Property WifiName = new Property(33, String.class, "wifiName", false, "WIFI_NAME");
        public final static Property Power = new Property(34, int.class, "power", false, "POWER");
        public final static Property UpdateTime = new Property(35, long.class, "updateTime", false, "UPDATE_TIME");
        public final static Property OpenStatus = new Property(36, int.class, "openStatus", false, "OPEN_STATUS");
        public final static Property OpenStatusTime = new Property(37, long.class, "openStatusTime", false, "OPEN_STATUS_TIME");
        public final static Property Device_did = new Property(38, String.class, "device_did", false, "DEVICE_DID");
        public final static Property Device_sn = new Property(39, String.class, "device_sn", false, "DEVICE_SN");
        public final static Property P2p_password = new Property(40, String.class, "p2p_password", false, "P2P_PASSWORD");
        public final static Property SingleFireSwitchInfo = new Property(41, String.class, "singleFireSwitchInfo", false, "SINGLE_FIRE_SWITCH_INFO");
        public final static Property SetPir = new Property(42, String.class, "setPir", false, "SET_PIR");
        public final static Property Alive_time = new Property(43, String.class, "alive_time", false, "ALIVE_TIME");
        public final static Property Stay_status = new Property(44, int.class, "stay_status", false, "STAY_STATUS");
        public final static Property Camera_version = new Property(45, String.class, "camera_version", false, "CAMERA_VERSION");
        public final static Property Mcu_version = new Property(46, String.class, "mcu_version", false, "MCU_VERSION");
        public final static Property Device_model = new Property(47, String.class, "device_model", false, "DEVICE_MODEL");
        public final static Property Keep_alive_status = new Property(48, int.class, "keep_alive_status", false, "KEEP_ALIVE_STATUS");
        public final static Property Mac = new Property(49, String.class, "mac", false, "MAC");
        public final static Property LockMac = new Property(50, String.class, "lockMac", false, "LOCK_MAC");
        public final static Property RSSI = new Property(51, String.class, "RSSI", false, "RSSI");
        public final static Property WifiStrength = new Property(52, int.class, "wifiStrength", false, "WIFI_STRENGTH");
        public final static Property OpenDirection = new Property(53, int.class, "openDirection", false, "OPEN_DIRECTION");
        public final static Property OpenForce = new Property(54, int.class, "openForce", false, "OPEN_FORCE");
        public final static Property LockingMethod = new Property(55, int.class, "lockingMethod", false, "LOCKING_METHOD");
        public final static Property FrontPanelVersion = new Property(56, String.class, "frontPanelVersion", false, "FRONT_PANEL_VERSION");
        public final static Property BackPanelVersion = new Property(57, String.class, "backPanelVersion", false, "BACK_PANEL_VERSION");
        public final static Property VolLevel = new Property(58, int.class, "volLevel", false, "VOL_LEVEL");
        public final static Property ScreemLightLevel = new Property(59, int.class, "screemLightLevel", false, "SCREEM_LIGHT_LEVEL");
        public final static Property ScreemLightTime = new Property(60, int.class, "screemLightTime", false, "SCREEM_LIGHT_TIME");
        public final static Property ScreemLightSwitch = new Property(61, int.class, "screemLightSwitch", false, "SCREEM_LIGHT_SWITCH");
    }

    private final SingleFireSwitchInfoConvert singleFireSwitchInfoConverter = new SingleFireSwitchInfoConvert();
    private final WifiVideoLockSetPirConvert setPirConverter = new WifiVideoLockSetPirConvert();
    private final WifiVideoAliveTimeBeanConvert alive_timeConverter = new WifiVideoAliveTimeBeanConvert();

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
                "\"HOVER_ALARM\" INTEGER NOT NULL ," + // 23: hoverAlarm
                "\"HOVER_ALARM_LEVEL\" INTEGER NOT NULL ," + // 24: hoverAlarmLevel
                "\"BLE_VERSION\" TEXT," + // 25: bleVersion
                "\"WIFI_VERSION\" TEXT," + // 26: wifiVersion
                "\"MQTT_VERSION\" TEXT," + // 27: mqttVersion
                "\"FACE_VERSION\" TEXT," + // 28: faceVersion
                "\"LOCK_FIRMWARE_VERSION\" TEXT," + // 29: lockFirmwareVersion
                "\"RANDOM_CODE\" TEXT," + // 30: randomCode
                "\"DISTRIBUTION_NETWORK\" INTEGER NOT NULL ," + // 31: distributionNetwork
                "\"CREATE_TIME\" INTEGER NOT NULL ," + // 32: createTime
                "\"WIFI_NAME\" TEXT," + // 33: wifiName
                "\"POWER\" INTEGER NOT NULL ," + // 34: power
                "\"UPDATE_TIME\" INTEGER NOT NULL ," + // 35: updateTime
                "\"OPEN_STATUS\" INTEGER NOT NULL ," + // 36: openStatus
                "\"OPEN_STATUS_TIME\" INTEGER NOT NULL ," + // 37: openStatusTime
                "\"DEVICE_DID\" TEXT," + // 38: device_did
                "\"DEVICE_SN\" TEXT," + // 39: device_sn
                "\"P2P_PASSWORD\" TEXT," + // 40: p2p_password
                "\"SINGLE_FIRE_SWITCH_INFO\" TEXT," + // 41: singleFireSwitchInfo
                "\"SET_PIR\" TEXT," + // 42: setPir
                "\"ALIVE_TIME\" TEXT," + // 43: alive_time
                "\"STAY_STATUS\" INTEGER NOT NULL ," + // 44: stay_status
                "\"CAMERA_VERSION\" TEXT," + // 45: camera_version
                "\"MCU_VERSION\" TEXT," + // 46: mcu_version
                "\"DEVICE_MODEL\" TEXT," + // 47: device_model
                "\"KEEP_ALIVE_STATUS\" INTEGER NOT NULL ," + // 48: keep_alive_status
                "\"MAC\" TEXT," + // 49: mac
                "\"LOCK_MAC\" TEXT," + // 50: lockMac
                "\"RSSI\" TEXT," + // 51: RSSI
                "\"WIFI_STRENGTH\" INTEGER NOT NULL ," + // 52: wifiStrength
                "\"OPEN_DIRECTION\" INTEGER NOT NULL ," + // 53: openDirection
                "\"OPEN_FORCE\" INTEGER NOT NULL ," + // 54: openForce
                "\"LOCKING_METHOD\" INTEGER NOT NULL ," + // 55: lockingMethod
                "\"FRONT_PANEL_VERSION\" TEXT," + // 56: frontPanelVersion
                "\"BACK_PANEL_VERSION\" TEXT," + // 57: backPanelVersion
                "\"VOL_LEVEL\" INTEGER NOT NULL ," + // 58: volLevel
                "\"SCREEM_LIGHT_LEVEL\" INTEGER NOT NULL ," + // 59: screemLightLevel
                "\"SCREEM_LIGHT_TIME\" INTEGER NOT NULL ," + // 60: screemLightTime
                "\"SCREEM_LIGHT_SWITCH\" INTEGER NOT NULL );"); // 61: screemLightSwitch
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
        stmt.bindLong(24, entity.getHoverAlarm());
        stmt.bindLong(25, entity.getHoverAlarmLevel());
 
        String bleVersion = entity.getBleVersion();
        if (bleVersion != null) {
            stmt.bindString(26, bleVersion);
        }
 
        String wifiVersion = entity.getWifiVersion();
        if (wifiVersion != null) {
            stmt.bindString(27, wifiVersion);
        }
 
        String mqttVersion = entity.getMqttVersion();
        if (mqttVersion != null) {
            stmt.bindString(28, mqttVersion);
        }
 
        String faceVersion = entity.getFaceVersion();
        if (faceVersion != null) {
            stmt.bindString(29, faceVersion);
        }
 
        String lockFirmwareVersion = entity.getLockFirmwareVersion();
        if (lockFirmwareVersion != null) {
            stmt.bindString(30, lockFirmwareVersion);
        }
 
        String randomCode = entity.getRandomCode();
        if (randomCode != null) {
            stmt.bindString(31, randomCode);
        }
        stmt.bindLong(32, entity.getDistributionNetwork());
        stmt.bindLong(33, entity.getCreateTime());
 
        String wifiName = entity.getWifiName();
        if (wifiName != null) {
            stmt.bindString(34, wifiName);
        }
        stmt.bindLong(35, entity.getPower());
        stmt.bindLong(36, entity.getUpdateTime());
        stmt.bindLong(37, entity.getOpenStatus());
        stmt.bindLong(38, entity.getOpenStatusTime());
 
        String device_did = entity.getDevice_did();
        if (device_did != null) {
            stmt.bindString(39, device_did);
        }
 
        String device_sn = entity.getDevice_sn();
        if (device_sn != null) {
            stmt.bindString(40, device_sn);
        }
 
        String p2p_password = entity.getP2p_password();
        if (p2p_password != null) {
            stmt.bindString(41, p2p_password);
        }
 
        SingleFireSwitchInfo singleFireSwitchInfo = entity.getSingleFireSwitchInfo();
        if (singleFireSwitchInfo != null) {
            stmt.bindString(42, singleFireSwitchInfoConverter.convertToDatabaseValue(singleFireSwitchInfo));
        }
 
        WifiVideoLockSetPirBean setPir = entity.getSetPir();
        if (setPir != null) {
            stmt.bindString(43, setPirConverter.convertToDatabaseValue(setPir));
        }
 
        WifiVideoLockAliveTimeBean alive_time = entity.getAlive_time();
        if (alive_time != null) {
            stmt.bindString(44, alive_timeConverter.convertToDatabaseValue(alive_time));
        }
        stmt.bindLong(45, entity.getStay_status());
 
        String camera_version = entity.getCamera_version();
        if (camera_version != null) {
            stmt.bindString(46, camera_version);
        }
 
        String mcu_version = entity.getMcu_version();
        if (mcu_version != null) {
            stmt.bindString(47, mcu_version);
        }
 
        String device_model = entity.getDevice_model();
        if (device_model != null) {
            stmt.bindString(48, device_model);
        }
        stmt.bindLong(49, entity.getKeep_alive_status());
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(50, mac);
        }
 
        String lockMac = entity.getLockMac();
        if (lockMac != null) {
            stmt.bindString(51, lockMac);
        }
 
        String RSSI = entity.getRSSI();
        if (RSSI != null) {
            stmt.bindString(52, RSSI);
        }
        stmt.bindLong(53, entity.getWifiStrength());
        stmt.bindLong(54, entity.getOpenDirection());
        stmt.bindLong(55, entity.getOpenForce());
        stmt.bindLong(56, entity.getLockingMethod());
 
        String frontPanelVersion = entity.getFrontPanelVersion();
        if (frontPanelVersion != null) {
            stmt.bindString(57, frontPanelVersion);
        }
 
        String backPanelVersion = entity.getBackPanelVersion();
        if (backPanelVersion != null) {
            stmt.bindString(58, backPanelVersion);
        }
        stmt.bindLong(59, entity.getVolLevel());
        stmt.bindLong(60, entity.getScreemLightLevel());
        stmt.bindLong(61, entity.getScreemLightTime());
        stmt.bindLong(62, entity.getScreemLightSwitch());
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
        stmt.bindLong(24, entity.getHoverAlarm());
        stmt.bindLong(25, entity.getHoverAlarmLevel());
 
        String bleVersion = entity.getBleVersion();
        if (bleVersion != null) {
            stmt.bindString(26, bleVersion);
        }
 
        String wifiVersion = entity.getWifiVersion();
        if (wifiVersion != null) {
            stmt.bindString(27, wifiVersion);
        }
 
        String mqttVersion = entity.getMqttVersion();
        if (mqttVersion != null) {
            stmt.bindString(28, mqttVersion);
        }
 
        String faceVersion = entity.getFaceVersion();
        if (faceVersion != null) {
            stmt.bindString(29, faceVersion);
        }
 
        String lockFirmwareVersion = entity.getLockFirmwareVersion();
        if (lockFirmwareVersion != null) {
            stmt.bindString(30, lockFirmwareVersion);
        }
 
        String randomCode = entity.getRandomCode();
        if (randomCode != null) {
            stmt.bindString(31, randomCode);
        }
        stmt.bindLong(32, entity.getDistributionNetwork());
        stmt.bindLong(33, entity.getCreateTime());
 
        String wifiName = entity.getWifiName();
        if (wifiName != null) {
            stmt.bindString(34, wifiName);
        }
        stmt.bindLong(35, entity.getPower());
        stmt.bindLong(36, entity.getUpdateTime());
        stmt.bindLong(37, entity.getOpenStatus());
        stmt.bindLong(38, entity.getOpenStatusTime());
 
        String device_did = entity.getDevice_did();
        if (device_did != null) {
            stmt.bindString(39, device_did);
        }
 
        String device_sn = entity.getDevice_sn();
        if (device_sn != null) {
            stmt.bindString(40, device_sn);
        }
 
        String p2p_password = entity.getP2p_password();
        if (p2p_password != null) {
            stmt.bindString(41, p2p_password);
        }
 
        SingleFireSwitchInfo singleFireSwitchInfo = entity.getSingleFireSwitchInfo();
        if (singleFireSwitchInfo != null) {
            stmt.bindString(42, singleFireSwitchInfoConverter.convertToDatabaseValue(singleFireSwitchInfo));
        }
 
        WifiVideoLockSetPirBean setPir = entity.getSetPir();
        if (setPir != null) {
            stmt.bindString(43, setPirConverter.convertToDatabaseValue(setPir));
        }
 
        WifiVideoLockAliveTimeBean alive_time = entity.getAlive_time();
        if (alive_time != null) {
            stmt.bindString(44, alive_timeConverter.convertToDatabaseValue(alive_time));
        }
        stmt.bindLong(45, entity.getStay_status());
 
        String camera_version = entity.getCamera_version();
        if (camera_version != null) {
            stmt.bindString(46, camera_version);
        }
 
        String mcu_version = entity.getMcu_version();
        if (mcu_version != null) {
            stmt.bindString(47, mcu_version);
        }
 
        String device_model = entity.getDevice_model();
        if (device_model != null) {
            stmt.bindString(48, device_model);
        }
        stmt.bindLong(49, entity.getKeep_alive_status());
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(50, mac);
        }
 
        String lockMac = entity.getLockMac();
        if (lockMac != null) {
            stmt.bindString(51, lockMac);
        }
 
        String RSSI = entity.getRSSI();
        if (RSSI != null) {
            stmt.bindString(52, RSSI);
        }
        stmt.bindLong(53, entity.getWifiStrength());
        stmt.bindLong(54, entity.getOpenDirection());
        stmt.bindLong(55, entity.getOpenForce());
        stmt.bindLong(56, entity.getLockingMethod());
 
        String frontPanelVersion = entity.getFrontPanelVersion();
        if (frontPanelVersion != null) {
            stmt.bindString(57, frontPanelVersion);
        }
 
        String backPanelVersion = entity.getBackPanelVersion();
        if (backPanelVersion != null) {
            stmt.bindString(58, backPanelVersion);
        }
        stmt.bindLong(59, entity.getVolLevel());
        stmt.bindLong(60, entity.getScreemLightLevel());
        stmt.bindLong(61, entity.getScreemLightTime());
        stmt.bindLong(62, entity.getScreemLightSwitch());
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
            cursor.getInt(offset + 23), // hoverAlarm
            cursor.getInt(offset + 24), // hoverAlarmLevel
            cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25), // bleVersion
            cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26), // wifiVersion
            cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27), // mqttVersion
            cursor.isNull(offset + 28) ? null : cursor.getString(offset + 28), // faceVersion
            cursor.isNull(offset + 29) ? null : cursor.getString(offset + 29), // lockFirmwareVersion
            cursor.isNull(offset + 30) ? null : cursor.getString(offset + 30), // randomCode
            cursor.getInt(offset + 31), // distributionNetwork
            cursor.getLong(offset + 32), // createTime
            cursor.isNull(offset + 33) ? null : cursor.getString(offset + 33), // wifiName
            cursor.getInt(offset + 34), // power
            cursor.getLong(offset + 35), // updateTime
            cursor.getInt(offset + 36), // openStatus
            cursor.getLong(offset + 37), // openStatusTime
            cursor.isNull(offset + 38) ? null : cursor.getString(offset + 38), // device_did
            cursor.isNull(offset + 39) ? null : cursor.getString(offset + 39), // device_sn
            cursor.isNull(offset + 40) ? null : cursor.getString(offset + 40), // p2p_password
            cursor.isNull(offset + 41) ? null : singleFireSwitchInfoConverter.convertToEntityProperty(cursor.getString(offset + 41)), // singleFireSwitchInfo
            cursor.isNull(offset + 42) ? null : setPirConverter.convertToEntityProperty(cursor.getString(offset + 42)), // setPir
            cursor.isNull(offset + 43) ? null : alive_timeConverter.convertToEntityProperty(cursor.getString(offset + 43)), // alive_time
            cursor.getInt(offset + 44), // stay_status
            cursor.isNull(offset + 45) ? null : cursor.getString(offset + 45), // camera_version
            cursor.isNull(offset + 46) ? null : cursor.getString(offset + 46), // mcu_version
            cursor.isNull(offset + 47) ? null : cursor.getString(offset + 47), // device_model
            cursor.getInt(offset + 48), // keep_alive_status
            cursor.isNull(offset + 49) ? null : cursor.getString(offset + 49), // mac
            cursor.isNull(offset + 50) ? null : cursor.getString(offset + 50), // lockMac
            cursor.isNull(offset + 51) ? null : cursor.getString(offset + 51), // RSSI
            cursor.getInt(offset + 52), // wifiStrength
            cursor.getInt(offset + 53), // openDirection
            cursor.getInt(offset + 54), // openForce
            cursor.getInt(offset + 55), // lockingMethod
            cursor.isNull(offset + 56) ? null : cursor.getString(offset + 56), // frontPanelVersion
            cursor.isNull(offset + 57) ? null : cursor.getString(offset + 57), // backPanelVersion
            cursor.getInt(offset + 58), // volLevel
            cursor.getInt(offset + 59), // screemLightLevel
            cursor.getInt(offset + 60), // screemLightTime
            cursor.getInt(offset + 61) // screemLightSwitch
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
        entity.setHoverAlarm(cursor.getInt(offset + 23));
        entity.setHoverAlarmLevel(cursor.getInt(offset + 24));
        entity.setBleVersion(cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25));
        entity.setWifiVersion(cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26));
        entity.setMqttVersion(cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27));
        entity.setFaceVersion(cursor.isNull(offset + 28) ? null : cursor.getString(offset + 28));
        entity.setLockFirmwareVersion(cursor.isNull(offset + 29) ? null : cursor.getString(offset + 29));
        entity.setRandomCode(cursor.isNull(offset + 30) ? null : cursor.getString(offset + 30));
        entity.setDistributionNetwork(cursor.getInt(offset + 31));
        entity.setCreateTime(cursor.getLong(offset + 32));
        entity.setWifiName(cursor.isNull(offset + 33) ? null : cursor.getString(offset + 33));
        entity.setPower(cursor.getInt(offset + 34));
        entity.setUpdateTime(cursor.getLong(offset + 35));
        entity.setOpenStatus(cursor.getInt(offset + 36));
        entity.setOpenStatusTime(cursor.getLong(offset + 37));
        entity.setDevice_did(cursor.isNull(offset + 38) ? null : cursor.getString(offset + 38));
        entity.setDevice_sn(cursor.isNull(offset + 39) ? null : cursor.getString(offset + 39));
        entity.setP2p_password(cursor.isNull(offset + 40) ? null : cursor.getString(offset + 40));
        entity.setSingleFireSwitchInfo(cursor.isNull(offset + 41) ? null : singleFireSwitchInfoConverter.convertToEntityProperty(cursor.getString(offset + 41)));
        entity.setSetPir(cursor.isNull(offset + 42) ? null : setPirConverter.convertToEntityProperty(cursor.getString(offset + 42)));
        entity.setAlive_time(cursor.isNull(offset + 43) ? null : alive_timeConverter.convertToEntityProperty(cursor.getString(offset + 43)));
        entity.setStay_status(cursor.getInt(offset + 44));
        entity.setCamera_version(cursor.isNull(offset + 45) ? null : cursor.getString(offset + 45));
        entity.setMcu_version(cursor.isNull(offset + 46) ? null : cursor.getString(offset + 46));
        entity.setDevice_model(cursor.isNull(offset + 47) ? null : cursor.getString(offset + 47));
        entity.setKeep_alive_status(cursor.getInt(offset + 48));
        entity.setMac(cursor.isNull(offset + 49) ? null : cursor.getString(offset + 49));
        entity.setLockMac(cursor.isNull(offset + 50) ? null : cursor.getString(offset + 50));
        entity.setRSSI(cursor.isNull(offset + 51) ? null : cursor.getString(offset + 51));
        entity.setWifiStrength(cursor.getInt(offset + 52));
        entity.setOpenDirection(cursor.getInt(offset + 53));
        entity.setOpenForce(cursor.getInt(offset + 54));
        entity.setLockingMethod(cursor.getInt(offset + 55));
        entity.setFrontPanelVersion(cursor.isNull(offset + 56) ? null : cursor.getString(offset + 56));
        entity.setBackPanelVersion(cursor.isNull(offset + 57) ? null : cursor.getString(offset + 57));
        entity.setVolLevel(cursor.getInt(offset + 58));
        entity.setScreemLightLevel(cursor.getInt(offset + 59));
        entity.setScreemLightTime(cursor.getInt(offset + 60));
        entity.setScreemLightSwitch(cursor.getInt(offset + 61));
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
