package com.kaadas.lock.publiclibrary.http.result;

import com.kaadas.lock.publiclibrary.bean.WifiLockAlarmRecord;

import java.util.List;

public class GetWifiLockAlarmRecordResult extends BaseResult {

    /**
     * code : 200
     * msg : 成功
     * nowTime : 1576656504
     * data : [{"_id":"5df0abf54d27d6da12fb4c71","time":"1541468973342","type":4,"wifiSN":"WF132231004","createTime":"1576054908866"}]
     */

    private int nowTime;
    private List<WifiLockAlarmRecord> data;

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

    public int getNowTime() {
        return nowTime;
    }

    public void setNowTime(int nowTime) {
        this.nowTime = nowTime;
    }

    public List<WifiLockAlarmRecord> getData() {
        return data;
    }

    public void setData(List<WifiLockAlarmRecord> data) {
        this.data = data;
    }

}
