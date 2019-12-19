package com.kaadas.lock.publiclibrary.http.result;

import com.google.gson.annotations.SerializedName;
import com.kaadas.lock.publiclibrary.bean.WiFiLockPassword;

import java.util.List;

public class WifiLockGetPasswordListResult extends BaseResult {


    /**
     * nowTime : 1575360897
     * data : {"pwdList":[{"createTime":1551785021,"endTime":1551774543,"items":["1","3","6"],"num":12,"startTime":1551774543,"type":1}],"fingerprintList":[{"createTime":1551785021,"num":1}],"cardList":[{"createTime":1551785021,"num":1}],"pwdNickname":[{"num":1,"nickName":"啊啊啊"}],"fingerprintNickname":[{"num":1,"nickName":"密码1"}],"cardNickname":[{"num":1,"nickName":"密码1"}]}
     */

    private int nowTime;
    @SerializedName("data")
    private WiFiLockPassword dataX;



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

    public WiFiLockPassword getDataX() {
        return dataX;
    }

    public void setDataX(WiFiLockPassword dataX) {
        this.dataX = dataX;
    }


}
