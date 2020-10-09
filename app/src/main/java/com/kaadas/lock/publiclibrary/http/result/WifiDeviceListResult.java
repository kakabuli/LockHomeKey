package com.kaadas.lock.publiclibrary.http.result;

import com.google.gson.annotations.SerializedName;
import com.kaadas.lock.publiclibrary.bean.WifiVideoLockAlarmRecord;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class WifiDeviceListResult extends BaseResult {

    /**
     * {
     *     "code": "200",
     *     "msg": "成功",
     *     "nowTime": 1553158379,
     *     "data": [
     *         {
     *             "_id": "5de4c32a33cc1949441265ca",
     *             "wifiSN": "WF132231004",
     *             "isAdmin": 1,
     *             "adminUid": "5c4fe492dc93897aa7d8600b",
     *             "adminName": "8618954359822",
     *             "productSN": "s10001192910010",
     *             "productModel": "k8",
     *             "appId": 1,
     *             "lockNickname": "wode",
     *             "lockSoftwareVersion": "22222",
     *             "functionSet": "00",
     *             "uid": "5c4fe492dc93897aa7d8600b",
     *             "uname": "8618954359822",
     *             "pushSwitch": 1,
     *             "amMode": 1,
     *             "safeMode": 1,
     *             "defences": 0,
     *             "language": "zh",
     *             "operatingMode": 0,
     *             "volume": 1,
     *             "bleVersion": "33333",
     *             "wifiVersion": "44444",
     *             "mqttVersion": "55555",
     *             "faceVersion": "66666",
     *             "lockFirmwareVersion": "11111",
     *             "randomCode": "randomCode666",
     *             "distributionNetwork": 1,
     *             "wifiName": "wodewifi",
     *             "switch": {
     *                 "switchEn": 1,
     *                 "switchArray": [
     *                     {
     *                         "type": 1,
     *                         "startTime": 1440,
     *                         "stopTime": 1440,
     *                         "week": 2
     *                     }
     *                 ]
     *             },
     *             "keep_alive_status":1,
     *             "alive_time": {
     *                 "keep_alive_snooze":[1,2,3,4,5,6,7],
     *                 "snooze_start_time":0,
     *                 "snooze_end_time":86400
     *             },
     *             "stay_status":1,
     *             "setPir":{
     *                  "stay_time":10,
     *                  "pir_sen":100
     *             }
     *         }
     *     ]
     * }
     */
    private long nowTime;
    private List<WifiVideoLockAlarmRecord> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getNowTime() {
        return nowTime;
    }

    public void setNowTime(long nowTime) {
        this.nowTime = nowTime;
    }

    public List<WifiVideoLockAlarmRecord> getData() {
        return data;
    }

    public void setData(List<WifiVideoLockAlarmRecord> data) {
        this.data = data;
    }

    public class WifiDeviceListBean{
        /**
         *     "data": [
         *         {
         *             "_id": "5de4c32a33cc1949441265ca",
         *             "wifiSN": "WF132231004",
         *             "isAdmin": 1,
         *             "adminUid": "5c4fe492dc93897aa7d8600b",
         *             "adminName": "8618954359822",
         *             "productSN": "s10001192910010",
         *             "productModel": "k8",
         *             "appId": 1,
         *             "lockNickname": "wode",
         *             "lockSoftwareVersion": "22222",
         *             "functionSet": "00",
         *             "uid": "5c4fe492dc93897aa7d8600b",
         *             "uname": "8618954359822",
         *             "pushSwitch": 1,
         *             "amMode": 1,
         *             "safeMode": 1,
         *             "defences": 0,
         *             "language": "zh",
         *             "operatingMode": 0,
         *             "volume": 1,
         *             "bleVersion": "33333",
         *             "wifiVersion": "44444",
         *             "mqttVersion": "55555",
         *             "faceVersion": "66666",
         *             "lockFirmwareVersion": "11111",
         *             "randomCode": "randomCode666",
         *             "distributionNetwork": 1,
         *             "wifiName": "wodewifi",
         *             "switch": {
         *                 "switchEn": 1,
         *                 "switchArray": [
         *                     {
         *                         "type": 1,
         *                         "startTime": 1440,
         *                         "stopTime": 1440,
         *                         "week": 2
         *                     }
         *                 ]
         *             },
         *             "keep_alive_status":1,
         *             "alive_time": {
         *                 "keep_alive_snooze":[1,2,3,4,5,6,7],
         *                 "snooze_start_time":0,
         *                 "snooze_end_time":86400
         *             },
         *             "stay_status":1,
         *             "setPir":{
         *                  "stay_time":10,
         *                  "pir_sen":100
         *             }
         */

        private String _id;
        private String wifiSN;
        private int isAdmin;
        private String adminUid;
        private String adminName;
        private String productSN;
        private String productModel;
        private int appId;
        private String lockNickname;
        private String lockSoftwareVersion;
        private String functionSet;
        private String uid;
        private String uname;
        private int pushSwitch;
        private int amMode;
        private int safeMode;
        private int defences;
        private String language;
        private int operatingMode;
        private int volume;
        private String bleVersion;
        private String wifiVersion;
        private String mqttVersion;
        private String faceVersion;
        private String lockFirmwareVersion;
        private String randomCode;
        private int distributionNetwork;
        private String wifiName;
        @SerializedName("switch")
        private WifiSwitchBean switchX;
        private int keep_alive_status;
        private AliveTimeBean alive_time;
        private int stay_status;
        private SetPirBean setPir;


        public WifiDeviceListBean(String _id, String wifiSN, int isAdmin, String adminUid, String adminName, String productSN, String productModel, int appId, String lockNickname, String lockSoftwareVersion, String functionSet, String uid, String uname, int pushSwitch, int amMode, int safeMode, int defences, String language, int operatingMode, int volume, String bleVersion, String wifiVersion, String mqttVersion, String faceVersion, String lockFirmwareVersion, String randomCode, int distributionNetwork, String wifiName, WifiSwitchBean switchX, int keep_alive_status, AliveTimeBean alive_time, int stay_status, SetPirBean setPir) {
            this._id = _id;
            this.wifiSN = wifiSN;
            this.isAdmin = isAdmin;
            this.adminUid = adminUid;
            this.adminName = adminName;
            this.productSN = productSN;
            this.productModel = productModel;
            this.appId = appId;
            this.lockNickname = lockNickname;
            this.lockSoftwareVersion = lockSoftwareVersion;
            this.functionSet = functionSet;
            this.uid = uid;
            this.uname = uname;
            this.pushSwitch = pushSwitch;
            this.amMode = amMode;
            this.safeMode = safeMode;
            this.defences = defences;
            this.language = language;
            this.operatingMode = operatingMode;
            this.volume = volume;
            this.bleVersion = bleVersion;
            this.wifiVersion = wifiVersion;
            this.mqttVersion = mqttVersion;
            this.faceVersion = faceVersion;
            this.lockFirmwareVersion = lockFirmwareVersion;
            this.randomCode = randomCode;
            this.distributionNetwork = distributionNetwork;
            this.wifiName = wifiName;
            this.switchX = switchX;
            this.keep_alive_status = keep_alive_status;
            this.alive_time = alive_time;
            this.stay_status = stay_status;
            this.setPir = setPir;
        }


    }

    public class SetPirBean{
        /**
         *             "setPir":{
         *                  "stay_time":10,
         *                  "pir_sen":100
         *             }
         */

        private int stay_time;
        private int pir_sen;

        public SetPirBean(int stay_time, int pir_sen) {
            this.stay_time = stay_time;
            this.pir_sen = pir_sen;
        }

        public int getStay_time() {
            return stay_time;
        }

        public void setStay_time(int stay_time) {
            this.stay_time = stay_time;
        }

        public int getPir_sen() {
            return pir_sen;
        }

        public void setPir_sen(int pir_sen) {
            this.pir_sen = pir_sen;
        }

        @Override
        public String toString() {
            return "SetPirBean{" +
                    "stay_time=" + stay_time +
                    ", pir_sen=" + pir_sen +
                    '}';
        }
    }

    public class AliveTimeBean{
        /**
         *             "alive_time": {
         *                 "keep_alive_snooze":[1,2,3,4,5,6,7],
         *                 "snooze_start_time":0,
         *                 "snooze_end_time":86400
         *             },
         */
        private int[] keep_alive_snooze;
        private int snooze_start_time;
        private int snooze_end_time;

        public AliveTimeBean(int[] keep_alive_snooze, int snooze_start_time, int snooze_end_time) {
            this.keep_alive_snooze = keep_alive_snooze;
            this.snooze_start_time = snooze_start_time;
            this.snooze_end_time = snooze_end_time;
        }

        public int[] getKeep_alive_snooze() {


            return keep_alive_snooze;
        }

        public void setKeep_alive_snooze(int[] keep_alive_snooze) {
            this.keep_alive_snooze = keep_alive_snooze;
        }

        public int getSnooze_start_time() {
            return snooze_start_time;
        }

        public void setSnooze_start_time(int snooze_start_time) {
            this.snooze_start_time = snooze_start_time;
        }

        public int getSnooze_end_time() {
            return snooze_end_time;
        }

        public void setSnooze_end_time(int snooze_end_time) {
            this.snooze_end_time = snooze_end_time;
        }

        @Override
        public String toString() {
            return "AliveTimeBean{" +
                    "keep_alive_snooze=" + Arrays.toString(keep_alive_snooze) +
                    ", snooze_start_time=" + snooze_start_time +
                    ", snooze_end_time=" + snooze_end_time +
                    '}';
        }
    }

    public class WifiSwitchBean{
        /**
         *             "switch": {
         *                 "switchEn": 1,
         *                 "switchArray": [
         *                     {
         *                         "type": 1,
         *                         "startTime": 1440,
         *                         "stopTime": 1440,
         *                         "week": 2
         *                     }
         *                 ]
         *             },
         */

        private int switchEn;
        private List<WifiSwitchDetailBean> switchArray;

        public WifiSwitchBean(int switchEn, List<WifiSwitchDetailBean> switchArray) {
            this.switchEn = switchEn;
            this.switchArray = switchArray;
        }

        public int getSwitchEn() {
            return switchEn;
        }

        public void setSwitchEn(int switchEn) {
            this.switchEn = switchEn;
        }

        public List<WifiSwitchDetailBean> getSwitchArray() {
            return switchArray;
        }

        public void setSwitchArray(List<WifiSwitchDetailBean> switchArray) {
            this.switchArray = switchArray;
        }
    }

    public class WifiSwitchDetailBean{
        private int type;
        private int startTime;
        private int stopTime;
        private int week;


        public WifiSwitchDetailBean(int type, int startTime, int stopTime, int week) {
            this.type = type;
            this.startTime = startTime;
            this.stopTime = stopTime;
            this.week = week;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getStartTime() {
            return startTime;
        }

        public void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        public int getStopTime() {
            return stopTime;
        }

        public void setStopTime(int stopTime) {
            this.stopTime = stopTime;
        }

        public int getWeek() {
            return week;
        }

        public void setWeek(int week) {
            this.week = week;
        }

        @Override
        public String toString() {
            return "WifiSwitchDetailBean{" +
                    "type=" + type +
                    ", startTime=" + startTime +
                    ", stopTime=" + stopTime +
                    ", week=" + week +
                    '}';
        }
    }



}
