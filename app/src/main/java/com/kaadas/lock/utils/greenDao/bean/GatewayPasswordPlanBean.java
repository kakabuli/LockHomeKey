package com.kaadas.lock.utils.greenDao.bean;

import com.kaadas.lock.publiclibrary.bean.ForeverPassword;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

@Entity
public class GatewayPasswordPlanBean implements Serializable,Comparable<GatewayPasswordPlanBean>  {

    private static final long serialVersionUID = -190734710746841476L;

    @Id(autoincrement = true)
    private Long id;
    private int userType;  //userType: 0 永久性密钥   userType: 1 策略密钥   userType: 3 管理员密钥   userType: 4 无权限密钥
    private int passwordNumber;   //密码编号
    private String planType;    //  week 周计划    year 年计划
    private String deviceId;
    private String gatewayId;
    private String uid;
    /**
     * 周计划  相关数据
     * daysMask : 2
     * startHour : 1
     * startMinute : 2
     * endHour : 19
     * endMinute : 57
     */
    private int daysMask;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    /**
     * scheduleID : 1
     * userID : 1
     * scheduleStatus : 0
     * zigBeeLocalStartTime : 167772160
     * zigBeeLocalEndTime : 134217728
     */

    /**
     * 年计划  参数
     */
    private long zigBeeLocalStartTime;
    private long zigBeeLocalEndTime;




    public GatewayPasswordPlanBean(String deviceId, String gatewayId, String uid) {
        this.deviceId = deviceId;
        this.gatewayId = gatewayId;
        this.uid = uid;
    }

    public GatewayPasswordPlanBean(String deviceId, String gatewayId, String uid,int number) {
        this.deviceId = deviceId;
        this.gatewayId = gatewayId;
        this.uid = uid;
        this.passwordNumber = number;
    }

    @Generated(hash = 1339580019)
    public GatewayPasswordPlanBean(Long id, int userType, int passwordNumber, String planType, String deviceId,
            String gatewayId, String uid, int daysMask, int startHour, int startMinute, int endHour,
            int endMinute, long zigBeeLocalStartTime, long zigBeeLocalEndTime) {
        this.id = id;
        this.userType = userType;
        this.passwordNumber = passwordNumber;
        this.planType = planType;
        this.deviceId = deviceId;
        this.gatewayId = gatewayId;
        this.uid = uid;
        this.daysMask = daysMask;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.zigBeeLocalStartTime = zigBeeLocalStartTime;
        this.zigBeeLocalEndTime = zigBeeLocalEndTime;
    }

    @Generated(hash = 2095490717)
    public GatewayPasswordPlanBean() {
    }


    public void setYearPlan(long zigBeeLocalStartTime, long zigBeeLocalEndTime){
        this.zigBeeLocalEndTime = zigBeeLocalEndTime;
        this.zigBeeLocalStartTime = zigBeeLocalStartTime;

    }

    public void setWeekPlan(int daysMask, int startHour, int startMinute, int endHour, int endMinute) {
        this.daysMask = daysMask;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getPasswordNumber() {
        return passwordNumber;
    }

    public void setPasswordNumber(int passwordNumber) {
        this.passwordNumber = passwordNumber;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }


    public int getDaysMask() {
        return daysMask;
    }

    public void setDaysMask(int daysMask) {
        this.daysMask = daysMask;
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

    public long getZigBeeLocalStartTime() {
        return zigBeeLocalStartTime;
    }

    public void setZigBeeLocalStartTime(long zigBeeLocalStartTime) {
        this.zigBeeLocalStartTime = zigBeeLocalStartTime;
    }
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public long getZigBeeLocalEndTime() {
        return zigBeeLocalEndTime;
    }

    public void setZigBeeLocalEndTime(long zigBeeLocalEndTime) {
        this.zigBeeLocalEndTime = zigBeeLocalEndTime;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "GatewayPasswordPlanBean{" +
                "id=" + id +
                ", userType=" + userType +
                ", passwordNumber=" + passwordNumber +
                ", planType='" + planType + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", gatewayId='" + gatewayId + '\'' +
                ", uid='" + uid + '\'' +
                ", daysMask=" + daysMask +
                ", startHour=" + startHour +
                ", startMinute=" + startMinute +
                ", endHour=" + endHour +
                ", endMinute=" + endMinute +
                ", zigBeeLocalStartTime=" + zigBeeLocalStartTime +
                ", zigBeeLocalEndTime=" + zigBeeLocalEndTime +
                '}';
    }

    @Override
    public int compareTo(GatewayPasswordPlanBean o) {
        if (passwordNumber<o.getPasswordNumber()){
            return -1;
        }
        return 1;
    }
}
