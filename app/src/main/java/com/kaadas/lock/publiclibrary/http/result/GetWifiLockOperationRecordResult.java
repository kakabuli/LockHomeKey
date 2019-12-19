package com.kaadas.lock.publiclibrary.http.result;

import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;

import java.util.List;

public class GetWifiLockOperationRecordResult extends BaseResult {

    /**
     * code : 200
     * msg : 成功
     * nowTime : 1576655959
     * data : [{"_id":"5dde33754d27d6da12f51637","time":"1541468973342","type":1,"wifiSN":"WF132231004","bleSN":"BT01192910010","pwdType":4,"pwdNum":2,"createTime":1576058537426,"appId":1,"uid":"5c4fe492dc93897aa7d8600b","uname":"8618954359822","userNickname":"ahaha"}]
     */

    private int nowTime;
    private List<WifiLockOperationRecord> data;

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

    public List<WifiLockOperationRecord> getData() {
        return data;
    }

    public void setData(List<WifiLockOperationRecord> data) {
        this.data = data;
    }


}
