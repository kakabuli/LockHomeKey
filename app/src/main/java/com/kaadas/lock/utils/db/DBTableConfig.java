package com.kaadas.lock.utils.db;

/**
 * Created by ${howard} on 2018/4/16.
 */

public interface DBTableConfig {

    /**
     * 数据库名称
     **/
    String DATABASE_NAME = "kaadas.db";
    /**
     * 数据库版本
     **/
    int VERSION = 1;

    /**
     * 媒体库信息
     */
    public interface MediaFile {
        String TABLE_NAME = "media";
        String COLUMN_ID = "_id";
        String NAME = "name";
        String CREATE_TIME = "time"; //文件生成时间
        String TYPE = "type";         //文件类型，1：视频，2：图片
        String PATH = "path";         //文件路径

        String CREATE_TABLE_MEDIA = "create table " + TABLE_NAME + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + NAME + " text, " + CREATE_TIME + " text, "
                + TYPE + " integer, " + PATH + " text " + ")";
    }

    /**
     * 猫眼信息
     */
    interface CatEyeInfo {
        String TABLE_NAME = "cateye";
        String COLUMN_ID = "_id";
        String NAME = "name";
        String DEVICE_TYPE = "type";
        String GATEWAY_ID = "gateway";
        String DEVICE_ID = "deviceId";
        String JOIN_TIME = "time";
        String CREATE_TABLE_CATEYE = "create table " + TABLE_NAME + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + NAME + " text, " + DEVICE_TYPE + " text, " + GATEWAY_ID + " text, "
                + DEVICE_ID + " text, " + JOIN_TIME + " text " + ")";
    }

    /**
     * zigbee锁开锁记录
     */
    interface OpenLockRecord {
        String TABLE_NAME = "zigbeelock";
        String COLUMN_ID = "_id";
        String GATEWAY_ID = "gatewayId";
        String DEVICE_ID = "deviceId";
        String DEVICE_NAME = "deviceName";
        String DEVICE_NICKNAME = "deviceNickname";
        String USER_NUM="userNum";
        String USER_NAME = "userName";
        String OPEN_TYPE="openType";
        String OPEN_TIME="time";
        String CREATE_TABLE_ZIGBEE = "create table " + TABLE_NAME + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + GATEWAY_ID + " text, " + DEVICE_ID + " text, " + DEVICE_NAME + " text, "
                + DEVICE_NICKNAME + " text, " + USER_NUM + " text, " +USER_NAME + " text, " +
                OPEN_TYPE + " text, " +OPEN_TIME + " text " + ")";
    }

    /**
     * 获取网关列表
     */
    interface GatewayList {


        String TABLE_NAME = "gateway_list";
        String COLUMN_ID = "_id";
        String GATEWAY_ID = "gatewayId";
        String DEVICE_SN = "DeviceSN";
        String DEVICE_NICKNAME = "DeviceNickName";
        String ANDMIN = "IsAdmin";
        String CREATE_TABLE_GATEWAYLIST = "create table " + TABLE_NAME + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + GATEWAY_ID + " text, " + DEVICE_SN + " text, " + DEVICE_NICKNAME + " text, "
                + ANDMIN + " text " + ")";
    }
    /**
     * 获取网关下設備列表
     */
    interface GatewayDownDevList {

        String TABLE_NAME = "gateway_downdev_list";
        String COLUMN_ID = "_id";
        String GATEWAY_ID = "gwUUid";
        String DEVICE_ID = "deviceId";
        String DEVICE_TYPE = "device_type";
        String DEVICE_NICKNAME = "nickName";
        String DEVICE_ONLINE = "online";

        String CREATE_TABLE_GATEWAYDOWNDEV = "create table " + TABLE_NAME + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + GATEWAY_ID + " text, "  + DEVICE_ID + " text, "+ DEVICE_TYPE + " text, "+ DEVICE_NICKNAME + " text, "+ DEVICE_ONLINE + " text "+ ")";
    }
}
