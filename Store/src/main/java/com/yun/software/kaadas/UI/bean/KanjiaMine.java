package com.yun.software.kaadas.UI.bean;

public class KanjiaMine {

    private String id; //活动id
    private String logo;
    private String name;
    private String agentProductId;//渠道id
    private String bargainProductId;//用户发起的砍价id
    private String plusPrice;//剩余价格
    private String cutPrice;//已经砍的价格
    private String price;//价格
    private String beginTime;//用户开始时间
    private String endTime;//用户结束时间
    private long second;


    public long getSecond() {
        return second * 1000 ;
    }

    public void setSecond(long second) {
        this.second = second;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAgentProductId() {
        return agentProductId;
    }

    public void setAgentProductId(String agentProductId) {
        this.agentProductId = agentProductId;
    }

    public String getBargainProductId() {
        return bargainProductId;
    }

    public void setBargainProductId(String bargainProductId) {
        this.bargainProductId = bargainProductId;
    }

    public String getPlusPrice() {
        return plusPrice;
    }

    public void setPlusPrice(String plusPrice) {
        this.plusPrice = plusPrice;
    }

    public String getCutPrice() {
        return cutPrice;
    }

    public void setCutPrice(String cutPrice) {
        this.cutPrice = cutPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


}
