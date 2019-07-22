package com.kaadas.lock.publiclibrary.http.postbean;

import java.util.List;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class UploadOperationRecordBean {
    public UploadOperationRecordBean(String devName, List<OperationListBean> operationList) {
        this.devName = devName;
        this.operationList = operationList;
    }

    /**
     * devName : BT01191910010
     * operationList : [{"uid":"5c6fb4d014fd214910b33e80","eventType":4,"eventSource":3,"eventCode":3,"userNum":4,"eventTime":"2019-07-04 12:00:03"}]
     */

    private String devName;
    private List<OperationListBean> operationList;

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public List<OperationListBean> getOperationList() {
        return operationList;
    }

    public void setOperationList(List<OperationListBean> operationList) {
        this.operationList = operationList;
    }

    public static class OperationListBean {
        public OperationListBean(String uid, int eventType, int eventSource, int eventCode, int userNum, String eventTime) {
            this.uid = uid;
            this.eventType = eventType;
            this.eventSource = eventSource;
            this.eventCode = eventCode;
            this.userNum = userNum;
            this.eventTime = eventTime;
        }

        /**
         * uid : 5c6fb4d014fd214910b33e80
         * eventType : 4
         * eventSource : 3
         * eventCode : 3
         * userNum : 4
         * eventTime : 2019-07-04 12:00:03
         */

        private String uid;
        private int eventType;
        private int eventSource;
        private int eventCode;
        private int userNum;
        private String eventTime;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public int getEventType() {
            return eventType;
        }

        public void setEventType(int eventType) {
            this.eventType = eventType;
        }

        public int getEventSource() {
            return eventSource;
        }

        public void setEventSource(int eventSource) {
            this.eventSource = eventSource;
        }

        public int getEventCode() {
            return eventCode;
        }

        public void setEventCode(int eventCode) {
            this.eventCode = eventCode;
        }

        public int getUserNum() {
            return userNum;
        }

        public void setUserNum(int userNum) {
            this.userNum = userNum;
        }

        public String getEventTime() {
            return eventTime;
        }

        public void setEventTime(String eventTime) {
            this.eventTime = eventTime;
        }
    }
    /**
     *     {
     *         "devName":"BT01191910010",
     *         "operationList":[
     *             {
     *                 "uid":"5c6fb4d014fd214910b33e80",
     *                 "eventType":4,
     *                 "eventSource":3,
     *                 "eventCode":3,
     *                 "userNum":4,
     *                 "eventTime":"2019-07-04 12:00:03"
     *             }
     *         ]
     *     }
     * */

}
