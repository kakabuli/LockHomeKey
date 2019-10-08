package com.yun.software.kaadas.UI.bean;

public class CouponBean {

    public CouponBean(String couponValue,String remark){
        this.couponValue = couponValue;
        this.remark = remark;
    }

    private String remark;
    private String id;
    private String couponValue;// 优惠金额

    private String expirationDate; // 过期时间
    private String startDate;//开始时间
    private String state;// string 优惠券状态
    private String couponCondition;// 使用条件
    private String couponTypeCN;
    private boolean isCheck ;//是否使用
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getCouponTypeCN() {
        return couponTypeCN;
    }

    public void setCouponTypeCN(String couponTypeCN) {
        this.couponTypeCN = couponTypeCN;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCouponCondition() {
        return couponCondition;
    }

    public void setCouponCondition(String couponCondition) {
        this.couponCondition = couponCondition;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCouponValue() {
        return couponValue;
    }

    public void setCouponValue(String couponValue) {
        this.couponValue = couponValue;
    }
}
