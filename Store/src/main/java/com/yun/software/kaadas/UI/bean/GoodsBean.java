package com.yun.software.kaadas.UI.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

public class GoodsBean implements Parcelable, MultiItemEntity {

    private String id;
    private String logo;
    private String name;
    private String price;
    private String businessType;
    private String agentProductId;
    private String productId;
    private String count;
    private String purchaseNumber;
    private String percentage;
    private String beginTime;
    private String endTime; //现在的价格
    private String oldPrice; //划线价格
    private List<ImgUrlBean> banners;  //轮播图
    private List<ImgUrlBean> detailImgs; //详情图片
    private String activityProductId; //活动id
    private String countNum;//月销
    private long second;//秒杀剩余秒数
    private String residueNum;//活动商品 剩余可以购买的数量
    private String status;//拼团状态


    //众筹
    private String crowdMoney; //众筹总价
    private String saleMoney;//已筹金额
    private String saleQty; //支持人数  //已团件数

    //拼团
    private String discount; //折扣
    private String maxQty; //成团人数

    private String attributeComboName;//默认商品属性

    private String colloctFlag;//1，已经收藏，0，未收藏
    private boolean flg;//是否已经参加砍价
    private String customerBargainId ;//砍价id
    private String sendNumber;//砍价送的人数
    private String maxPrice;
    private String cutPrice;//已砍价格

    public GoodsBean(){}

    protected GoodsBean(Parcel in) {
        id = in.readString();
        logo = in.readString();
        name = in.readString();
        price = in.readString();
        businessType = in.readString();
        agentProductId = in.readString();
        productId = in.readString();
        count = in.readString();
        purchaseNumber = in.readString();
        percentage = in.readString();
        beginTime = in.readString();
        endTime = in.readString();
        oldPrice = in.readString();
        activityProductId = in.readString();
        countNum = in.readString();
        second = in.readLong();
        residueNum = in.readString();
        status = in.readString();
        crowdMoney = in.readString();
        saleMoney = in.readString();
        saleQty = in.readString();
        discount = in.readString();
        maxQty = in.readString();
        attributeComboName = in.readString();
        colloctFlag = in.readString();
        flg = in.readByte() != 0;
        customerBargainId = in.readString();
        sendNumber = in.readString();
        maxPrice = in.readString();
        cutPrice = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(logo);
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(businessType);
        dest.writeString(agentProductId);
        dest.writeString(productId);
        dest.writeString(count);
        dest.writeString(purchaseNumber);
        dest.writeString(percentage);
        dest.writeString(beginTime);
        dest.writeString(endTime);
        dest.writeString(oldPrice);
        dest.writeString(activityProductId);
        dest.writeString(countNum);
        dest.writeLong(second);
        dest.writeString(residueNum);
        dest.writeString(status);
        dest.writeString(crowdMoney);
        dest.writeString(saleMoney);
        dest.writeString(saleQty);
        dest.writeString(discount);
        dest.writeString(maxQty);
        dest.writeString(attributeComboName);
        dest.writeString(colloctFlag);
        dest.writeByte((byte) (flg ? 1 : 0));
        dest.writeString(customerBargainId);
        dest.writeString(sendNumber);
        dest.writeString(maxPrice);
        dest.writeString(cutPrice);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GoodsBean> CREATOR = new Creator<GoodsBean>() {
        @Override
        public GoodsBean createFromParcel(Parcel in) {
            return new GoodsBean(in);
        }

        @Override
        public GoodsBean[] newArray(int size) {
            return new GoodsBean[size];
        }
    };

    public String getCutPrice() {
        return cutPrice;
    }

    public void setCutPrice(String cutPrice) {
        this.cutPrice = cutPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getSendNumber() {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber) {
        this.sendNumber = sendNumber;
    }

    public String getCustomerBargainId() {
        return customerBargainId;
    }

    public void setCustomerBargainId(String customerBargainId) {
        this.customerBargainId = customerBargainId;
    }

    public boolean isFlg() {
        return flg;
    }

    public void setFlg(boolean flg) {
        this.flg = flg;
    }

    public String getColloctFlag() {
        return colloctFlag;
    }

    public void setColloctFlag(String colloctFlag) {
        this.colloctFlag = colloctFlag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttributeComboName() {
        return attributeComboName;
    }

    public void setAttributeComboName(String attributeComboName) {
        this.attributeComboName = attributeComboName;
    }


    public String getCountNum() {
        return countNum;
    }

    public void setCountNum(String countNum) {
        this.countNum = countNum;
    }

    public String getMaxQty() {
        return maxQty;
    }

    public void setMaxQty(String maxQty) {
        this.maxQty = maxQty;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCrowdMoney() {
        return crowdMoney;
    }

    public void setCrowdMoney(String crowdMoney) {
        this.crowdMoney = crowdMoney;
    }

    public String getSaleMoney() {
        return saleMoney;
    }

    public void setSaleMoney(String saleMoney) {
        this.saleMoney = saleMoney;
    }

    public String getSaleQty() {
        return saleQty;
    }

    public void setSaleQty(String saleQty) {
        this.saleQty = saleQty;
    }

    public String getActivityProductId() {
        return activityProductId;
    }

    public void setActivityProductId(String activityProductId) {
        this.activityProductId = activityProductId;
    }

    public List<ImgUrlBean> getBanners() {
        return banners;
    }

    public void setBanners(List<ImgUrlBean> banners) {
        this.banners = banners;
    }

    public List<ImgUrlBean> getDetailImgs() {
        return detailImgs;
    }

    public void setDetailImgs(List<ImgUrlBean> detailImgs) {
        this.detailImgs = detailImgs;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getAgentProductId() {
        return agentProductId;
    }

    public void setAgentProductId(String agentProductId) {
        this.agentProductId = agentProductId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPurchaseNumber() {
        return purchaseNumber;
    }

    public void setPurchaseNumber(String purchaseNumber) {
        this.purchaseNumber = purchaseNumber;
    }

    public String  getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
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

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public long getSecond() {
        return second * 1000;
    }

    public void setSecond(long second) {
        this.second = second;
    }

    public String getResidueNum() {
        return residueNum;
    }

    public void setResidueNum(String residueNum) {
        this.residueNum = residueNum;
    }

    @Override
    public String toString() {
        return "GoodsBean{" +
                "id='" + id + '\'' +
                ", logo='" + logo + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", businessType='" + businessType + '\'' +
                ", agentProductId='" + agentProductId + '\'' +
                ", productId='" + productId + '\'' +
                ", count='" + count + '\'' +
                ", purchaseNumber='" + purchaseNumber + '\'' +
                ", percentage='" + percentage + '\'' +
                ", beginTime='" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", oldPrice='" + oldPrice + '\'' +
                '}';
    }


    @Override
    public int getItemType() {
        return 0;
    }
}
