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
         * storeToken : eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI5NTAiLCJpc3MiOiJodHRwczovL3d3dy5rYW5nYXJvb2JhYnljYXIuY29tIiwiaWF0IjoxNTY5NzQ2MTQwfQ.3uoz632-uN20cpAnz9M2BqCVxysUwV2q6Jgma8y8FrM
         */

        private String token;
        private String uid;
        private String storeToken;

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

        public String getStoreToken() {
            return storeToken;
        }

        public void setStoreToken(String storeToken) {
            this.storeToken = storeToken;
        }
    }
}
