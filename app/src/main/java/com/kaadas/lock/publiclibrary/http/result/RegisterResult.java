package com.kaadas.lock.publiclibrary.http.result;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class RegisterResult  extends BaseResult {


    /**
     * code : 200
     * data : {"token":"eyJfaWQiOiI1YzcwYWMwNTNjNTU0NjM5ZWE5M2NjODUiLCJ1c2VybmFtZSI6Ijg2MTg5NTQzNTk4MjQiLCJpYXQiOjE1NTA4ODc5NDF9","uid":"5c70ac053c554639ea93cc85"}
     * msg : 成功
     */

    private ResisterResult data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ResisterResult getData() {
        return data;
    }

    public void setData(ResisterResult data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class ResisterResult {
        /**
         * token : eyJfaWQiOiI1YzcwYWMwNTNjNTU0NjM5ZWE5M2NjODUiLCJ1c2VybmFtZSI6Ijg2MTg5NTQzNTk4MjQiLCJpYXQiOjE1NTA4ODc5NDF9
         * uid : 5c70ac053c554639ea93cc85
         */

        private String token;
        private String uid;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        @Override
        public String toString() {
            return "ResisterResult{" +
                    "token='" + token + '\'' +
                    ", uid='" + uid + '\'' +
                    '}';
        }
    }
}
