package com.kaadas.lock.publiclibrary.http.temp.resultbean;

/**
 * Create By lxj  on 2019/1/10
 * Describe
 */
public class GetPwd1Result {

    /**
     * code : 200
     * msg : 成功
     * data : {"password1":"f564967d578934ab33bc9496"}
     */

    private String code;
    private String msg;
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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * password1 : f564967d578934ab33bc9496
         */

        private String password1;

        public String getPassword1() {
            return password1;
        }

        public void setPassword1(String password1) {
            this.password1 = password1;
        }
    }

    @Override
    public String toString() {
        return "GetPwd1Result{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
