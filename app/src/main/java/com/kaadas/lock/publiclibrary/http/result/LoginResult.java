package com.kaadas.lock.publiclibrary.http.result;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class LoginResult extends BaseResult {

    /**
     * code : 200
     * msg : 成功
     * data : {"uid":"5c6fb4d014fd214910b33e80","token":"eyJfaWQiOiI1YzZmYjRkMDE0ZmQyMTQ5MTBiMzNlODAiLCJ1c2VybmFtZSI6Ijg2MTg5NTQzNTk4MjIiLCJpYXQiOjE1NTA4ODgyMDl9","meUsername":null,"mePwd":null}
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
         * uid : 5c6fb4d014fd214910b33e80
         * token : eyJfaWQiOiI1YzZmYjRkMDE0ZmQyMTQ5MTBiMzNlODAiLCJ1c2VybmFtZSI6Ijg2MTg5NTQzNTk4MjIiLCJpYXQiOjE1NTA4ODgyMDl9
         * meUsername : null
         * mePwd : null
         * storeToken : eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI5NDgiLCJpc3MiOiJodHRwczovL3d3dy5rYW5nYXJvb2JhYnljYXIuY29tIiwiaWF0IjoxNTY5ODA4MTk2fQ.nim1clpDSCInVFekcoy9ZfUK5fzgJWik85RietVaRlc
         */

        private String uid;
        private String token;
        private Object meUsername;
        private Object mePwd;
        private String storeToken;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Object getMeUsername() {
            return meUsername;
        }

        public void setMeUsername(Object meUsername) {
            this.meUsername = meUsername;
        }

        public Object getMePwd() {
            return mePwd;
        }

        public void setMePwd(Object mePwd) {
            this.mePwd = mePwd;
        }

        public String getStoreToken() {
            return storeToken;
        }

        public void setStoreToken(String storeToken) {
            this.storeToken = storeToken;
        }
    }
}
