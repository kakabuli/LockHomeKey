package com.kaadas.lock.publiclibrary.http.result;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 * code	String	状态码
 * msg	String	提示语
 * data	JsonObject
 * num	String	密钥编号
 * nickName	String	密钥昵称
 */
public class SinglePasswordResult extends BaseResult {

    /**
     * code : 200
     * msg : 成功
     * data : {"num":"01","nickName":"密码1的昵称啊"}
     */

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
         * num : 01
         * nickName : 密码1的昵称啊
         */

        private String num;
        private String nickName;

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
    }
}
