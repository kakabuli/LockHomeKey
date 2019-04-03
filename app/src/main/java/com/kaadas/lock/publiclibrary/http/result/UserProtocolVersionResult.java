package com.kaadas.lock.publiclibrary.http.result;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class UserProtocolVersionResult extends BaseResult {

    /**
     * code : 200
     * msg : 成功
     * data : {"_id":"5c6df80da3965c0b5c2d4019","name":"协议1.1","version":"1.1","tag":"标签"}
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
         * _id : 5c6df80da3965c0b5c2d4019
         * name : 协议1.1
         * version : 1.1
         * tag : 标签
         */

        private String _id;
        private String name;
        private String version;
        private String tag;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }
}
