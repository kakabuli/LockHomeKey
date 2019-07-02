package com.kaadas.lock.utils.greenDao.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.kaadas.lock.utils.greenDao.bean.GatewayBaseInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "GATEWAY_BASE_INFO".
*/
public class GatewayBaseInfoDao extends AbstractDao<GatewayBaseInfo, String> {

    public static final String TABLENAME = "GATEWAY_BASE_INFO";

    /**
     * Properties of entity GatewayBaseInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property DeviceIdUid = new Property(0, String.class, "deviceIdUid", true, "DEVICE_ID_UID");
        public final static Property GatewayId = new Property(1, String.class, "gatewayId", false, "GATEWAY_ID");
        public final static Property SW = new Property(2, String.class, "SW", false, "SW");
        public final static Property ZnpVersion = new Property(3, String.class, "znpVersion", false, "ZNP_VERSION");
        public final static Property LanIp = new Property(4, String.class, "lanIp", false, "LAN_IP");
        public final static Property LanNetmask = new Property(5, String.class, "lanNetmask", false, "LAN_NETMASK");
        public final static Property WanIp = new Property(6, String.class, "wanIp", false, "WAN_IP");
        public final static Property WanNetmask = new Property(7, String.class, "wanNetmask", false, "WAN_NETMASK");
        public final static Property WanType = new Property(8, String.class, "wanType", false, "WAN_TYPE");
        public final static Property Channel = new Property(9, String.class, "channel", false, "CHANNEL");
        public final static Property GatewayName = new Property(10, String.class, "gatewayName", false, "GATEWAY_NAME");
        public final static Property Encryption = new Property(11, String.class, "encryption", false, "ENCRYPTION");
        public final static Property Pwd = new Property(12, String.class, "pwd", false, "PWD");
        public final static Property Ssid = new Property(13, String.class, "ssid", false, "SSID");
        public final static Property Uid = new Property(14, String.class, "uid", false, "UID");
    }


    public GatewayBaseInfoDao(DaoConfig config) {
        super(config);
    }
    
    public GatewayBaseInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"GATEWAY_BASE_INFO\" (" + //
                "\"DEVICE_ID_UID\" TEXT PRIMARY KEY NOT NULL ," + // 0: deviceIdUid
                "\"GATEWAY_ID\" TEXT," + // 1: gatewayId
                "\"SW\" TEXT," + // 2: SW
                "\"ZNP_VERSION\" TEXT," + // 3: znpVersion
                "\"LAN_IP\" TEXT," + // 4: lanIp
                "\"LAN_NETMASK\" TEXT," + // 5: lanNetmask
                "\"WAN_IP\" TEXT," + // 6: wanIp
                "\"WAN_NETMASK\" TEXT," + // 7: wanNetmask
                "\"WAN_TYPE\" TEXT," + // 8: wanType
                "\"CHANNEL\" TEXT," + // 9: channel
                "\"GATEWAY_NAME\" TEXT," + // 10: gatewayName
                "\"ENCRYPTION\" TEXT," + // 11: encryption
                "\"PWD\" TEXT," + // 12: pwd
                "\"SSID\" TEXT," + // 13: ssid
                "\"UID\" TEXT);"); // 14: uid
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"GATEWAY_BASE_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, GatewayBaseInfo entity) {
        stmt.clearBindings();
 
        String deviceIdUid = entity.getDeviceIdUid();
        if (deviceIdUid != null) {
            stmt.bindString(1, deviceIdUid);
        }
 
        String gatewayId = entity.getGatewayId();
        if (gatewayId != null) {
            stmt.bindString(2, gatewayId);
        }
 
        String SW = entity.getSW();
        if (SW != null) {
            stmt.bindString(3, SW);
        }
 
        String znpVersion = entity.getZnpVersion();
        if (znpVersion != null) {
            stmt.bindString(4, znpVersion);
        }
 
        String lanIp = entity.getLanIp();
        if (lanIp != null) {
            stmt.bindString(5, lanIp);
        }
 
        String lanNetmask = entity.getLanNetmask();
        if (lanNetmask != null) {
            stmt.bindString(6, lanNetmask);
        }
 
        String wanIp = entity.getWanIp();
        if (wanIp != null) {
            stmt.bindString(7, wanIp);
        }
 
        String wanNetmask = entity.getWanNetmask();
        if (wanNetmask != null) {
            stmt.bindString(8, wanNetmask);
        }
 
        String wanType = entity.getWanType();
        if (wanType != null) {
            stmt.bindString(9, wanType);
        }
 
        String channel = entity.getChannel();
        if (channel != null) {
            stmt.bindString(10, channel);
        }
 
        String gatewayName = entity.getGatewayName();
        if (gatewayName != null) {
            stmt.bindString(11, gatewayName);
        }
 
        String encryption = entity.getEncryption();
        if (encryption != null) {
            stmt.bindString(12, encryption);
        }
 
        String pwd = entity.getPwd();
        if (pwd != null) {
            stmt.bindString(13, pwd);
        }
 
        String ssid = entity.getSsid();
        if (ssid != null) {
            stmt.bindString(14, ssid);
        }
 
        String uid = entity.getUid();
        if (uid != null) {
            stmt.bindString(15, uid);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, GatewayBaseInfo entity) {
        stmt.clearBindings();
 
        String deviceIdUid = entity.getDeviceIdUid();
        if (deviceIdUid != null) {
            stmt.bindString(1, deviceIdUid);
        }
 
        String gatewayId = entity.getGatewayId();
        if (gatewayId != null) {
            stmt.bindString(2, gatewayId);
        }
 
        String SW = entity.getSW();
        if (SW != null) {
            stmt.bindString(3, SW);
        }
 
        String znpVersion = entity.getZnpVersion();
        if (znpVersion != null) {
            stmt.bindString(4, znpVersion);
        }
 
        String lanIp = entity.getLanIp();
        if (lanIp != null) {
            stmt.bindString(5, lanIp);
        }
 
        String lanNetmask = entity.getLanNetmask();
        if (lanNetmask != null) {
            stmt.bindString(6, lanNetmask);
        }
 
        String wanIp = entity.getWanIp();
        if (wanIp != null) {
            stmt.bindString(7, wanIp);
        }
 
        String wanNetmask = entity.getWanNetmask();
        if (wanNetmask != null) {
            stmt.bindString(8, wanNetmask);
        }
 
        String wanType = entity.getWanType();
        if (wanType != null) {
            stmt.bindString(9, wanType);
        }
 
        String channel = entity.getChannel();
        if (channel != null) {
            stmt.bindString(10, channel);
        }
 
        String gatewayName = entity.getGatewayName();
        if (gatewayName != null) {
            stmt.bindString(11, gatewayName);
        }
 
        String encryption = entity.getEncryption();
        if (encryption != null) {
            stmt.bindString(12, encryption);
        }
 
        String pwd = entity.getPwd();
        if (pwd != null) {
            stmt.bindString(13, pwd);
        }
 
        String ssid = entity.getSsid();
        if (ssid != null) {
            stmt.bindString(14, ssid);
        }
 
        String uid = entity.getUid();
        if (uid != null) {
            stmt.bindString(15, uid);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public GatewayBaseInfo readEntity(Cursor cursor, int offset) {
        GatewayBaseInfo entity = new GatewayBaseInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // deviceIdUid
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // gatewayId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // SW
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // znpVersion
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // lanIp
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // lanNetmask
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // wanIp
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // wanNetmask
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // wanType
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // channel
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // gatewayName
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // encryption
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // pwd
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // ssid
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14) // uid
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, GatewayBaseInfo entity, int offset) {
        entity.setDeviceIdUid(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setGatewayId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSW(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setZnpVersion(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setLanIp(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setLanNetmask(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setWanIp(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setWanNetmask(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setWanType(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setChannel(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setGatewayName(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setEncryption(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setPwd(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setSsid(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setUid(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
     }
    
    @Override
    protected final String updateKeyAfterInsert(GatewayBaseInfo entity, long rowId) {
        return entity.getDeviceIdUid();
    }
    
    @Override
    public String getKey(GatewayBaseInfo entity) {
        if(entity != null) {
            return entity.getDeviceIdUid();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(GatewayBaseInfo entity) {
        return entity.getDeviceIdUid() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
