package com.yun.software.kaadas.UI.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yanliang
 * on 2019/6/19
 */
public class SaleAfaterDetailItem implements Parcelable {
    private int id;
    private String receiveDate;
    private String sendDate;
    private String createDate;
    private String approveResult;
    private String approveDate;
    private String refundImg;
    private String tradeNo;
    private String trackingNo;
    private String status;
    private String imgs;
    private String reason;
    private String indentDetailId;
    private String indentId;
    private String reasonType;
    private String customerId;
    private String contactName;
    private String contactTel;
    private String imgsCN;
    private String refundImgCN;
    private String customerIdCN;
    private String statusCN;
    private String reasonTypeCN;
    private String tbIndentInfo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getApproveResult() {
        return approveResult;
    }

    public void setApproveResult(String approveResult) {
        this.approveResult = approveResult;
    }

    public String getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(String approveDate) {
        this.approveDate = approveDate;
    }

    public String getRefundImg() {
        return refundImg;
    }

    public void setRefundImg(String refundImg) {
        this.refundImg = refundImg;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getIndentDetailId() {
        return indentDetailId;
    }

    public void setIndentDetailId(String indentDetailId) {
        this.indentDetailId = indentDetailId;
    }

    public String getIndentId() {
        return indentId;
    }

    public void setIndentId(String indentId) {
        this.indentId = indentId;
    }

    public String getReasonType() {
        return reasonType;
    }

    public void setReasonType(String reasonType) {
        this.reasonType = reasonType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactTel() {
        return contactTel;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    public String getImgsCN() {
        return imgsCN;
    }

    public void setImgsCN(String imgsCN) {
        this.imgsCN = imgsCN;
    }

    public String getRefundImgCN() {
        return refundImgCN;
    }

    public void setRefundImgCN(String refundImgCN) {
        this.refundImgCN = refundImgCN;
    }

    public String getCustomerIdCN() {
        return customerIdCN;
    }

    public void setCustomerIdCN(String customerIdCN) {
        this.customerIdCN = customerIdCN;
    }

    public String getStatusCN() {
        return statusCN;
    }

    public void setStatusCN(String statusCN) {
        this.statusCN = statusCN;
    }

    public String getReasonTypeCN() {
        return reasonTypeCN;
    }

    public void setReasonTypeCN(String reasonTypeCN) {
        this.reasonTypeCN = reasonTypeCN;
    }

    public String getTbIndentInfo() {
        return tbIndentInfo;
    }

    public void setTbIndentInfo(String tbIndentInfo) {
        this.tbIndentInfo = tbIndentInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.receiveDate);
        dest.writeString(this.sendDate);
        dest.writeString(this.createDate);
        dest.writeString(this.approveResult);
        dest.writeString(this.approveDate);
        dest.writeString(this.refundImg);
        dest.writeString(this.tradeNo);
        dest.writeString(this.trackingNo);
        dest.writeString(this.status);
        dest.writeString(this.imgs);
        dest.writeString(this.reason);
        dest.writeString(this.indentDetailId);
        dest.writeString(this.indentId);
        dest.writeString(this.reasonType);
        dest.writeString(this.customerId);
        dest.writeString(this.contactName);
        dest.writeString(this.contactTel);
        dest.writeString(this.imgsCN);
        dest.writeString(this.refundImgCN);
        dest.writeString(this.customerIdCN);
        dest.writeString(this.statusCN);
        dest.writeString(this.reasonTypeCN);
        dest.writeString(this.tbIndentInfo);
    }

    public SaleAfaterDetailItem() {
    }

    protected SaleAfaterDetailItem(Parcel in) {
        this.id = in.readInt();
        this.receiveDate = in.readString();
        this.sendDate = in.readString();
        this.createDate = in.readString();
        this.approveResult = in.readString();
        this.approveDate = in.readString();
        this.refundImg = in.readString();
        this.tradeNo = in.readString();
        this.trackingNo = in.readString();
        this.status = in.readString();
        this.imgs = in.readString();
        this.reason = in.readString();
        this.indentDetailId = in.readString();
        this.indentId = in.readString();
        this.reasonType = in.readString();
        this.customerId = in.readString();
        this.contactName = in.readString();
        this.contactTel = in.readString();
        this.imgsCN = in.readString();
        this.refundImgCN = in.readString();
        this.customerIdCN = in.readString();
        this.statusCN = in.readString();
        this.reasonTypeCN = in.readString();
        this.tbIndentInfo =  in.readString();
    }

    public static final Creator<SaleAfaterDetailItem> CREATOR = new Creator<SaleAfaterDetailItem>() {
        @Override
        public SaleAfaterDetailItem createFromParcel(Parcel source) {
            return new SaleAfaterDetailItem(source);
        }

        @Override
        public SaleAfaterDetailItem[] newArray(int size) {
            return new SaleAfaterDetailItem[size];
        }
    };
}
