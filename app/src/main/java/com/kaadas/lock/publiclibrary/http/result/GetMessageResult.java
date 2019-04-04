package com.kaadas.lock.publiclibrary.http.result;

import java.util.List;

public class GetMessageResult{

    /**
     * code : 200
     * msg : 成功
     * data : [{"_id":"5c80b4ba3c55461c42e971a1","type":1,"title":"ttttt","content":"ccccccccccccccc","createTime":1551938746}]
     */
    protected String code;
    protected String msg;
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
         * _id : 5c80b4ba3c55461c42e971a1
         * type : 1
         * title : ttttt
         * content : ccccccccccccccc
         * createTime : 1551938746
         */

        private String _id;
        private int type;
        private String title;
        private String content;
        private Long createTime;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }
    }
    public boolean isSuccess(){
        if ("200".equals(getCode()) || "201".equals(getCode()) || "202".equals(getCode())){
            return true;
        }

        return false;
    }
}
