package com.kaadas.lock.publiclibrary.http.result;

import java.util.List;

public class GetHelpLogResult extends BaseResult{


    /**
     * code : 200
     * msg : 成功
     * data : [{"_id":"5c85d19a3c55465ac4a10a8a","uid":"5c70ac053c554639ea93cc85","devName":"GI132231004","devModel":"k8","content":"有个错误，你自己找吧","command":"addErr","code":"1206","createTime":1552273818}]
     */
    private String code;
    private String msg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * _id : 5c85d19a3c55465ac4a10a8a
         * uid : 5c70ac053c554639ea93cc85
         * devName : GI132231004
         * devModel : k8
         * content : 有个错误，你自己找吧
         * command : addErr
         * code : 1206
         * createTime : 1552273818
         */

        private String _id;
        private String uid;
        private String devName;
        private String devModel;
        private String content;
        private String command;
        private String code;
        private Long createTime;

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

        public String getDevName() {
            return devName;
        }

        public void setDevName(String devName) {
            this.devName = devName;
        }

        public String getDevModel() {
            return devModel;
        }

        public void setDevModel(String devModel) {
            this.devModel = devModel;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }
    }
}
