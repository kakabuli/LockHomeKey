package com.kaadas.lock.publiclibrary.http.result;

import java.util.List;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class OperationRecordResult extends BaseResult {
    /**
     * nowTime : 1562232368
     * data : [{"_id":"5d1dc61763301ce694006089","devName":"BT01191910010","eventCode":3,"eventSource":3,"eventTime":"2019-07-04 12:00:02","eventType":3,"uid":"5c6fb4d014fd214910b33e80","userNum":4,"content":"sdfsadfdsa","createTime":"2019-07-04 17:25:43"}]
     */
    private String code;
    private String msg;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public void setMsg(String msg) {
        this.msg = msg;
    }

    //    @SerializedName("data")
    private List<OperationBean> data;



    public List<OperationBean> getData() {
        return data;
    }

    public void setData(List<OperationBean> data) {
        this.data = data;
    }

    public static class OperationBean {
        /**
         * _id : 5d1dc61763301ce694006089
         * devName : BT01191910010
         * eventCode : 3
         * eventSource : 3
         * eventTime : 2019-07-04 12:00:02
         * eventType : 3
         * uid : 5c6fb4d014fd214910b33e80
         * userNum : 4
         * content : sdfsadfdsa
         * createTime : 2019-07-04 17:25:43
         */

        private String _id;
        private String devName;
        private int eventCode;
        private int eventSource;
        private String eventTime;
        private int eventType;
        private String uid;
        private int userNum;
        private String content;
        private String createTime;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getDevName() {
            return devName;
        }

        public void setDevName(String devName) {
            this.devName = devName;
        }

        public int getEventCode() {
            return eventCode;
        }

        public void setEventCode(int eventCode) {
            this.eventCode = eventCode;
        }

        public int getEventSource() {
            return eventSource;
        }

        public void setEventSource(int eventSource) {
            this.eventSource = eventSource;
        }

        public String getEventTime() {
            return eventTime;
        }

        public void setEventTime(String eventTime) {
            this.eventTime = eventTime;
        }

        public int getEventType() {
            return eventType;
        }

        public void setEventType(int eventType) {
            this.eventType = eventType;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public int getUserNum() {
            return userNum;
        }

        public void setUserNum(int userNum) {
            this.userNum = userNum;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
    /**
     *     {
     *         "code": "200",
     *         "msg": "成功",
     *         "nowTime": 1562232368,
     *         "data": [
     *             {
     *                 "_id": "5d1dc61763301ce694006089",
     *                 "devName": "BT01191910010",
     *                 "eventCode": 3,
     *                 "eventSource": 3,
     *                 "eventTime": "2019-07-04 12:00:02",
     *                 "eventType": 3,
     *                 "uid": "5c6fb4d014fd214910b33e80",
     *                 "userNum": 4,
     *                 "content": "sdfsadfdsa",
     *                 "createTime": "2019-07-04 17:25:43"
     *             }
     *         ]
     *     }
     * */


}
