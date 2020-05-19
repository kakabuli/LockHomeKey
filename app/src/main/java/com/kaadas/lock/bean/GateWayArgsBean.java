package com.kaadas.lock.bean;

public class GateWayArgsBean {

    int pwdType;
    long zLocalEndT;
    long zLocalStartT;
    int dayMaskBits;
    int endHour;
    int endMinute;
    int startHour;
    int startMinute;

    public GateWayArgsBean() {
    }

    public GateWayArgsBean(int pwdType, long zLocalEndT, long zLocalStartT, int dayMaskBits, int endHour, int endMinute, int startHour, int startMinute) {
        this.pwdType = pwdType;
        this.zLocalEndT = zLocalEndT;
        this.zLocalStartT = zLocalStartT;
        this.dayMaskBits = dayMaskBits;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.startHour = startHour;
        this.startMinute = startMinute;
    }

    public int getPwdType() {
        return pwdType;
    }

    public void setPwdType(int pwdType) {
        this.pwdType = pwdType;
    }

    public long getzLocalEndT() {
        return zLocalEndT;
    }

    public void setzLocalEndT(long zLocalEndT) {
        this.zLocalEndT = zLocalEndT;
    }

    public long getzLocalStartT() {
        return zLocalStartT;
    }

    public void setzLocalStartT(long zLocalStartT) {
        this.zLocalStartT = zLocalStartT;
    }

    public int getDayMaskBits() {
        return dayMaskBits;
    }

    public void setDayMaskBits(int dayMaskBits) {
        this.dayMaskBits = dayMaskBits;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }
}
