package com.kaadas.lock.publiclibrary.http.temp.resultbean;

/**
 * Create By lxj  on 2019/1/10
 * Describe
 */
public class CheckBindResult {


    /**
     * code : 202
     * msg : 已绑定
     * nowTime : 1561630357
     * data : {"_id":"5cf34165457411492b74da11","adminname":"8613786399316"}
     */

    private String code;
    private String msg;
    private int nowTime;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * _id : 5cf34165457411492b74da11
         * adminname : 8613786399316
         */

        private String _id;
        private String adminname;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getAdminname() {
            return adminname;
        }

        public void setAdminname(String adminname) {
            this.adminname = adminname;
        }
    }
}
