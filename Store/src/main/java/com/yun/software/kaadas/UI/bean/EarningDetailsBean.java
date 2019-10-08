package com.yun.software.kaadas.UI.bean;

public class EarningDetailsBean {

    private String id;

    private String orderNo;

    private String money;

    private String status;

    private String commissionType;

    private String remark;

    private String createId;

    private String createTime;

    private String commissionTypeCN;

    private String aaa;

    private String daytime;

    private String balance;

    private String groupName;

    public EarningDetailsBean(String id, String orderNo, String money, String status, String commissionType, String remark, String createId, String createTime, String commissionTypeCN, String aaa, String daytime, String balance, String groupName) {
        this.id = id;
        this.orderNo = orderNo;
        this.money = money;
        this.status = status;
        this.commissionType = commissionType;
        this.remark = remark;
        this.createId = createId;
        this.createTime = createTime;
        this.commissionTypeCN = commissionTypeCN;
        this.aaa = aaa;
        this.daytime = daytime;
        this.balance = balance;
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "EarningDetailsBean{" +
                "id='" + id + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", money='" + money + '\'' +
                ", status='" + status + '\'' +
                ", commissionType='" + commissionType + '\'' +
                ", remark='" + remark + '\'' +
                ", createId='" + createId + '\'' +
                ", createTime='" + createTime + '\'' +
                ", commissionTypeCN='" + commissionTypeCN + '\'' +
                ", aaa='" + aaa + '\'' +
                ", daytime='" + daytime + '\'' +
                ", balance='" + balance + '\'' +
                ", groupName='" + groupName + '\'' +
                '}';
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getMoney() {
        if (money.contains("-" )|| money.contains("+")){
            return money;
        }
        //commissionType  //directOne直接收益,directTwo间接收益,pearlReward介绍收益,getMoney提现
        if ("getMoney".equals(commissionType) || "moneyTransferFees".equals(commissionType)){ //提现
            money = "-" + money;
        }else {
            money = "+" + money;
        }

        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCommissionType() {
        return commissionType;
    }

    public void setCommissionType(String commissionType) {
        this.commissionType = commissionType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCommissionTypeCN() {
        return commissionTypeCN;
    }

    public void setCommissionTypeCN(String commissionTypeCN) {
        this.commissionTypeCN = commissionTypeCN;
    }

    public String getAaa() {
        return aaa;
    }

    public void setAaa(String aaa) {
        this.aaa = aaa;
    }

    public String getDaytime() {
        return daytime;
    }

    public void setDaytime(String daytime) {
        this.daytime = daytime;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
