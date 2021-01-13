package com.kaadas.lock.publiclibrary.http.result;

public class ClothesHangerMachineBindResult extends BaseResult {
    private String code;
    private String msg;
    private long nowTime;
    private Params data;

    private class Params{
        private String _id;
        private String uid;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
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
