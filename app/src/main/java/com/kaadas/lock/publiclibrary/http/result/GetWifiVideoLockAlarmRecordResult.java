package com.kaadas.lock.publiclibrary.http.result;

import com.kaadas.lock.publiclibrary.bean.WifiLockAlarmRecord;
import com.kaadas.lock.publiclibrary.bean.WifiVideoLockAlarmRecord;

import java.util.List;

public class GetWifiVideoLockAlarmRecordResult extends BaseResult {

    /**
     * {
     *     "code": "200",
     *     "msg": "成功",
     *     "nowTime": 1576656504,
     *     "data": [
     *         {
     *             "_id": "5df0abf54d27d6da12fb4c71",
     *             "time": "1541468973342",
     *             "type": 4,
     *             "wifiSN": "WF132231004",
     *             "createTime": "1576054908866",
     *             "productSN" : "KV51203710173",
     *             "eventId" : "KV512037101731484f83217d941ae9e354b3f3e68a342",
     *             "thumbUrl" : "https://test.juziwulian.com:8050/kx/api/upload/KV512037101731484f83217d941ae9e354b3f3e68a342.jpg",
     *             "fileDate" : "20200924",
     *             "fileName" : "152606",
     *             "height" : 1920,
     *             "startTime" : 1600917264,
     *             "thumbState" : true,
     *             "updateTime" : 1600933259,
     *             "vedioTime" : 10,
     *             "width" : 1080
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

    @Override
    public String toString() {
        return "GetWifiVideoLockAlarmRecordResult{" +
                "nowTime=" + nowTime +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
