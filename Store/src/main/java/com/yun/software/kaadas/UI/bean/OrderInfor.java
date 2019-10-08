package com.yun.software.kaadas.UI.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by yanliang
 * on 2018/9/7 18:19
 */

public class OrderInfor implements Parcelable {

    private String id;
    private String installationDate;// 门店安装时间
    private String businessId;// 活动Id,基础订单为null
    private String indentType;// 订单类型
    private String createDate;// 创建时间;
    private String discusPay; // 折扣价格
    private String realPay; // 实际支付价格
    private String totalPrice; // 订单总价
    private String paymentType;// 支付方式（支付宝，微信

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    private String payType;
    private String buyerMsg;// 买家留言;
    private String transportAddress; // 收件人地址;
    private String transportPhone;// 收件人联系电话;
    private String transportName;// 收件人姓名
    private String indentStatus;// 订单状态;
    private String customerId; // 用户Id;
    private String tradeNo;// 支付单号
    private String orderNo;// 订单号
    private String shopId;// 门店Id;
    private String indentStatusCN; // 订单状态
    private String indentTypeCN; // 订单类型

    public List<IndentInfo> indentInfo;
    private String couponPrice;//优惠券金额
    private String productNum;//共几件商品
    private String payDate;//付款时间


    protected OrderInfor(Parcel in) {
        id = in.readString();
        installationDate = in.readString();
        businessId = in.readString();
        indentType = in.readString();
        createDate = in.readString();
        discusPay = in.readString();
        realPay = in.readString();
        totalPrice = in.readString();
        paymentType = in.readString();
        buyerMsg = in.readString();
        transportAddress = in.readString();
        transportPhone = in.readString();
        transportName = in.readString();
        indentStatus = in.readString();
        customerId = in.readString();
        tradeNo = in.readString();
        orderNo = in.readString();
        shopId = in.readString();
        indentStatusCN = in.readString();
        indentTypeCN = in.readString();
        couponPrice = in.readString();
        productNum = in.readString();
        indentInfo = in.createTypedArrayList(IndentInfo.CREATOR);
        payDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(installationDate);
        dest.writeString(businessId);
        dest.writeString(indentType);
        dest.writeString(createDate);
        dest.writeString(discusPay);
        dest.writeString(realPay);
        dest.writeString(totalPrice);
        dest.writeString(paymentType);
        dest.writeString(buyerMsg);
        dest.writeString(transportAddress);
        dest.writeString(transportPhone);
        dest.writeString(transportName);
        dest.writeString(indentStatus);
        dest.writeString(customerId);
        dest.writeString(tradeNo);
        dest.writeString(orderNo);
        dest.writeString(shopId);
        dest.writeString(indentStatusCN);
        dest.writeString(indentTypeCN);
        dest.writeString(couponPrice);
        dest.writeString(productNum);
        dest.writeTypedList(indentInfo);
        dest.writeString(payDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderInfor> CREATOR = new Creator<OrderInfor>() {
        @Override
        public OrderInfor createFromParcel(Parcel in) {
            return new OrderInfor(in);
        }

        @Override
        public OrderInfor[] newArray(int size) {
            return new OrderInfor[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(String installationDate) {
        this.installationDate = installationDate;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getProductNum() {
        return productNum;
    }

    public void setProductNum(String productNum) {
        this.productNum = productNum;
    }


    public String getCouponPrice() {
        return couponPrice;
    }

    public void setCouponPrice(String couponPrice) {
        this.couponPrice = couponPrice;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getIndentType() {
        return indentType;
    }

    public void setIndentType(String indentType) {
        this.indentType = indentType;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDiscusPay() {
        return discusPay;
    }

    public void setDiscusPay(String discusPay) {
        this.discusPay = discusPay;
    }

    public String getRealPay() {
        return realPay;
    }

    public void setRealPay(String realPay) {
        this.realPay = realPay;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getBuyerMsg() {
        return buyerMsg;
    }

    public void setBuyerMsg(String buyerMsg) {
        this.buyerMsg = buyerMsg;
    }

    public String getTransportAddress() {
        return transportAddress;
    }

    public void setTransportAddress(String transportAddress) {
        this.transportAddress = transportAddress;
    }

    public String getTransportPhone() {
        return transportPhone;
    }

    public void setTransportPhone(String transportPhone) {
        this.transportPhone = transportPhone;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    public String getIndentStatus() {
        return indentStatus;
    }

    public void setIndentStatus(String indentStatus) {
        this.indentStatus = indentStatus;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getIndentStatusCN() {
        return indentStatusCN;
    }

    public void setIndentStatusCN(String indentStatusCN) {
        this.indentStatusCN = indentStatusCN;
    }

    public String getIndentTypeCN() {
        return indentTypeCN;
    }

    public void setIndentTypeCN(String indentTypeCN) {
        this.indentTypeCN = indentTypeCN;
    }

    public List<IndentInfo> getIndentInfo() {
        return indentInfo;
    }

    public void setIndentInfo(List<IndentInfo> indentInfo) {
        this.indentInfo = indentInfo;
    }




}
