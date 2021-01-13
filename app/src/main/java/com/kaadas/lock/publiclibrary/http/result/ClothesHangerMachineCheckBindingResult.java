package com.kaadas.lock.publiclibrary.http.result;

public class ClothesHangerMachineCheckBindingResult extends BaseResult {
    /**
        "code": "202",
    "msg": "已绑定",
    "nowTime": 1609146944,
    "data": {
        "_id": "5fe98f3914403be33d05242d",
        "uid": "5c8a08e7dc9389866835a666"
    }
     */

    private String code;
    private String msg;
    private long nowTime;
    private Params data;

    public class Params{
        private String uname;

        public String getUname() {
            return uname;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getNowTime() {
        return nowTime;
    }

    public void setNowTime(long nowTime) {
        this.nowTime = nowTime;
    }

    public Params getData() {
        return data;
    }

    public void setData(Params data) {
        this.data = data;
    }
}
