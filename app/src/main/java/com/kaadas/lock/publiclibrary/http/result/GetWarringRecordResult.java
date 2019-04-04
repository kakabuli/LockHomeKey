package com.kaadas.lock.publiclibrary.http.result;

import java.util.List;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class GetWarringRecordResult extends BaseResult {

    /**
     * code : 200
     * msg : 成功
     * data : [{"_id":"5c70efa33c55461c04842138","devame":"GI132231004","warning_type":2,"warning_time":1550557361635,"content":"钥匙开门","status":0}]
     *
     *
     * code	String	状态码
     * msg	String	提示语
     * data	JsonObject
     * _d	String	预警记录ID
     * devName	String	设备SN
     * warningType	int	预警类型：1低电量 2钥匙开门 3验证错误 4防撬提醒 5即时性推送消息
     * warningTime	timestamp（s）	预警时间
     * content	String	预警内容
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
         * _id : 5c8b705063301ce694ed3179
         * content : 1
         * devName : XKA434F1A9F118
         * warningTime : 1552008565000
         * warningType : 3
         */

        private String _id;
        private String content;
        private String devName;
        private long warningTime;
        private int warningType;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDevName() {
            return devName;
        }

        public void setDevName(String devName) {
            this.devName = devName;
        }

        public long getWarningTime() {
            return warningTime;
        }

        public void setWarningTime(long warningTime) {
            this.warningTime = warningTime;
        }

        public int getWarningType() {
            return warningType;
        }

        public void setWarningType(int warningType) {
            this.warningType = warningType;
        }
    }
}
