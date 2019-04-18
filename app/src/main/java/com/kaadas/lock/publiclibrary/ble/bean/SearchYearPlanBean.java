package com.kaadas.lock.publiclibrary.ble.bean;


import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.utils.DateUtils;

/**
 * Create By lxj  on 2019/1/29
 * Describe
 */
public class SearchYearPlanBean {

    int status = 0; //默认是成功的
    int scheduleId;
    int userId;
    int codeType;
    long startTime;
    long endTime;

    public SearchYearPlanBean() {
    }

    public SearchYearPlanBean(int scheduleId, int userId, int codeType, long startTime, long endTime) {
        this.scheduleId = scheduleId;
        this.userId = userId;
        this.codeType = codeType;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCodeType() {
        return codeType;
    }

    public void setCodeType(int codeType) {
        this.codeType = codeType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getStringStartTime(){
        return DateUtils.getDateTimeFromMillisecond((startTime + BleCommandFactory.defineTime) * 1000);
    }

    public String getStringEndTime(){
        return DateUtils.getDateTimeFromMillisecond((startTime + BleCommandFactory.defineTime) * 1000);
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
