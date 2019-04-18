package com.kaadas.lock.publiclibrary.ble.bean;

/**
 * Create By lxj  on 2019/1/29
 * Describe
 */
public class SearchWeekPlanBean {
    int status = 0; //状态值  默认是查询成功的
    int DayMask;
    int scheduleId;
    int userId;
    int codeType;
    int startHour;
    int startMin;
    int endHour;
    int endMin;

    public SearchWeekPlanBean() {
    }

    public SearchWeekPlanBean(int dayMask, int scheduleId, int userId, int codeType, int startHour, int startMin, int endHour, int endMin) {
        DayMask = dayMask;
        this.scheduleId = scheduleId;
        this.userId = userId;
        this.codeType = codeType;
        this.startHour = startHour;
        this.startMin = startMin;
        this.endHour = endHour;
        this.endMin = endMin;
    }

    public int getDayMask() {
        return DayMask;
    }

    public void setDayMask(int dayMask) {
        DayMask = dayMask;
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

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
