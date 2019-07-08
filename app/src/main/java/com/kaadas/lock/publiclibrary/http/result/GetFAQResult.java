package com.kaadas.lock.publiclibrary.http.result;


import java.util.List;

public class GetFAQResult {

    /**
     * code : 200
     * msg : 成功
     * data : [{"_id":"5c807e6db170903740a02ff6","question":"怎么开始","answer":"打APP呀","sortNum":1,"createTime":1551882060}]
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
         * _id : 5c807e6db170903740a02ff6
         * question : 怎么开始
         * answer : 打APP呀
         * sortNum : 1
         * createTime : 1551882060
         */

        private String _id;
        private String question;
        private String answer;
        private int sortNum;
        private long createTime;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public int getSortNum() {
            return sortNum;
        }

        public void setSortNum(int sortNum) {
            this.sortNum = sortNum;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }
}
