package com.yun.software.kaadas.UI.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class IndentInfo implements Parcelable {

    private int id;// 主键
    private String price;// 购买单价
    private int qty;// 购买数量
    private String productLabels;// 属性值组合
    private String productName;// 产品名称
    private int productId;// 渠道产品Id
    private int indentId;// 订单Id
    private String logo;//图标
    private boolean isShowShouhou; //是否显示售后
    private String indentStatus;//订单状态
    private String status;
    private String indentInfoStatus;
    private String orderNo;
    private String indentType;// 订单类型

    public String getIndentType() {
        return indentType;
    }

    public void setIndentType(String indentType) {
        this.indentType = indentType;
    }

    public String getIndentInfoStatus() {
        return indentInfoStatus;
    }

    public void setIndentInfoStatus(String indentInfoStatus) {
        this.indentInfoStatus = indentInfoStatus;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getIndentDetailId() {
        return indentDetailId;
    }

    public void setIndentDetailId(Integer indentDetailId) {
        this.indentDetailId = indentDetailId;
    }

    private Integer indentDetailId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIndentStatus() {
        return indentStatus;
    }

    public void setIndentStatus(String indentStatus) {
        this.indentStatus = indentStatus;
    }

    protected IndentInfo(Parcel in) {
        id = in.readInt();
        price = in.readString();
        qty = in.readInt();
        productLabels = in.readString();
        productName = in.readString();
        productId = in.readInt();
        indentId = in.readInt();
        logo = in.readString();
        isShowShouhou = in.readByte() != 0;
    }

    public static final Creator<IndentInfo> CREATOR = new Creator<IndentInfo>() {
        @Override
        public IndentInfo createFromParcel(Parcel in) {
            return new IndentInfo(in);
        }

        @Override
        public IndentInfo[] newArray(int size) {
            return new IndentInfo[size];
        }
    };

    public boolean isShowShouhou() {
        return isShowShouhou;
    }

    public void setShowShouhou(boolean showShouhou) {
        isShowShouhou = showShouhou;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String  price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getProductLabels() {
        return productLabels;
    }

    public void setProductLabels(String productLabels) {
        this.productLabels = productLabels;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getIndentId() {
        return indentId;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setIndentId(int indentId) {
        this.indentId = indentId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(price);
        dest.writeInt(qty);
        dest.writeString(productLabels);
        dest.writeString(productName);
        dest.writeInt(productId);
        dest.writeInt(indentId);
        dest.writeString(logo);
        dest.writeByte((byte) (isShowShouhou ? 1 : 0));
    }
}
