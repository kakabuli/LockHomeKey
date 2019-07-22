package com.kaadas.lock.publiclibrary.ble.bean;

public class OperationLockRecord {
    @Override
    public String toString() {
        return "OperationLockRecord{" +
                "eventType=" + eventType +
                ", eventSource=" + eventSource +
                ", eventCode=" + eventCode +
                ", userNum=" + userNum +
                ", eventTime='" + eventTime + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }

    private int eventType; //事件类型
    private int eventSource;//事件内容
    private int eventCode;//事件code
    private int userNum;//用户编号
    private String eventTime;//事件时间
    private String uid;//用户uid

    public OperationLockRecord(int eventType, int eventSource, int eventCode, int userNum, String eventTime, String uid) {
        this.eventType = eventType;
        this.eventSource = eventSource;
        this.eventCode = eventCode;
        this.userNum = userNum;
        this.eventTime = eventTime;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
