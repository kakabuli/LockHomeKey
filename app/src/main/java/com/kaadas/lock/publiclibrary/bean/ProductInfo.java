package com.kaadas.lock.publiclibrary.bean;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

@Entity
public class ProductInfo implements Serializable {

    private static final long serialVersionUID = -155154562342239L;

    /**
     * "_id": "5d66619c497ecf326f25469b",
     * "developmentModel": "1",
     * "productModel": "k8",
     * "snHead": "WF1",
     * "adminUrl": "47.106.94.189/deviceModelFiles/1566990166590/android_admin_xxx.png",
     * "deviceListUrl": "47.106.94.189/deviceModelFiles/1566990488685/android_device_list_xxx.png",
     * "authUrl": "47.106.94.189/deviceModelFiles/1566990492106/android_auth_xxx.png",
     * "adminUrl@1x": "47.106.94.189/deviceModelFiles/1566990495762/ios_admin_xxx@1x.png",
     * "deviceListUrl@1x": "47.106.94.189/deviceModelFiles/1566990500347/ios_device_list_xxx@1x.png",
     * "authUrl@1x": "47.106.94.189/deviceModelFiles/1566990503571/ios_auth_xxx@1x.png",
     * "adminUrl@2x": "47.106.94.189/deviceModelFiles/1566990506856/ios_admin_xxx@2x.png",
     * "deviceListUrl@2x": "47.106.94.189/deviceModelFiles/1566990510246/ios_device_list_xxx@2x.png",
     * "authUrl@2x": "47.106.94.189/deviceModelFiles/1566990513628/ios_auth_xxx@2x.png",
     * "adminUrl@3x": "47.106.94.189/deviceModelFiles/1566990518026/ios_admin_xxx@3x.png",
     * "deviceListUrl@3x": "47.106.94.189/deviceModelFiles/1566990522179/ios_device_list_xxx@3x.png",
     * "authUrl@3x": "47.106.94.189/deviceModelFiles/1566990530690/ios_auth_xxx@3x.png",
     * "createTime": "2019-08-28 19:12:28.060"
     */
    @Id(autoincrement = true)
    private Long id;

    @SerializedName("_id")
    private String deviceID;
    private String developmentModel;
    private String productModel;
    private String snHead;

    //    android图片地址
    private String adminUrl;
    private String deviceListUrl;
    private String authUrl;
//    IOS图片地址
    @SerializedName("adminUrl@1x")
    private String adminUrl1x;
    @SerializedName("deviceListUrl@1x")
    private String deviceListUrl1x;
    @SerializedName("authUrl@1x")
    private String authUrl1x;
    @SerializedName("adminUrl@2x")
    private String adminUrl2x;
    @SerializedName("deviceListUrl@2x")
    private String deviceListUrl2x;
    @SerializedName("authUrl@2x")
    private String authUrl2x;
    @SerializedName("adminUrl@3x")
    private String adminUrl3x;
    @SerializedName("deviceListUrl@3x")
    private String deviceListUrl3x;
    @SerializedName("authUrl@3x")
    private String authUrl3x;

    /**
     * createTime  创建时间
     */
    private String createTime;

@Generated(hash = 1413390901)
public ProductInfo(Long id, String deviceID, String developmentModel, String productModel, String snHead, String adminUrl, String deviceListUrl,
        String authUrl, String adminUrl1x, String deviceListUrl1x, String authUrl1x, String adminUrl2x, String deviceListUrl2x, String authUrl2x,
        String adminUrl3x, String deviceListUrl3x, String authUrl3x, String createTime) {
    this.id = id;
    this.deviceID = deviceID;
    this.developmentModel = developmentModel;
    this.productModel = productModel;
    this.snHead = snHead;
    this.adminUrl = adminUrl;
    this.deviceListUrl = deviceListUrl;
    this.authUrl = authUrl;
    this.adminUrl1x = adminUrl1x;
    this.deviceListUrl1x = deviceListUrl1x;
    this.authUrl1x = authUrl1x;
    this.adminUrl2x = adminUrl2x;
    this.deviceListUrl2x = deviceListUrl2x;
    this.authUrl2x = authUrl2x;
    this.adminUrl3x = adminUrl3x;
    this.deviceListUrl3x = deviceListUrl3x;
    this.authUrl3x = authUrl3x;
    this.createTime = createTime;
}

    @Generated(hash = 49329718)
    public ProductInfo() {
    }

    @Override
    public String toString() {
        return "productInfo{" +
                "_id=" + deviceID +
                ", developmentModel='" + developmentModel + '\'' +
                ", productModel=" + productModel +
                ", adminUrl='" + adminUrl + '\'' +
                ", deviceListUrl='" + deviceListUrl + '\'' +
                ", authUrl='" + authUrl + '\'' +
                ", productModel='" + productModel + '\'' +
                ", adminUrl1x=" + adminUrl1x +
                ", deviceListUrl1x='" + deviceListUrl1x + '\'' +
                ", authUrl1x='" + authUrl1x + '\'' +
                ", adminUrl2x='" + adminUrl2x + '\'' +
                ", deviceListUrl2x='" + deviceListUrl2x + '\'' +
                ", authUrl2x='" + authUrl2x + '\'' +
                ", adminUrl3x=" + adminUrl3x +
                ", deviceListUrl3x=" + deviceListUrl3x +
                ", authUrl3x=" + authUrl3x +
                ", createTime='" + createTime + '\'' +
                '}';
    }


        public Long getId() {
            return this.id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getDeviceID() {
            return this.deviceID;
        }

        public void setDeviceID(String deviceID) {
            this.deviceID = deviceID;
        }

        public String getDevelopmentModel() {
            return developmentModel;
        }

        public void setDevelopmentModel(String developmentModel) {
            this.developmentModel = developmentModel;
        }

        public String getProductModel() {
            return productModel;
        }

        public void setProductModel(String productModel) {
            this.productModel = productModel;
        }

        public String getAdminUrl() {
            return adminUrl;
        }

        public void setAdminUrl(String adminUrl) {
            this.adminUrl = adminUrl;
        }

        public String getDeviceListUrl() {
            return deviceListUrl;
        }

        public void setDeviceListUrl(String deviceListUrl) {
            this.deviceListUrl = deviceListUrl;
        }

        public String getAuthUrl() {
            return authUrl;
        }

        public void setAuthUrl(String authUrl) {
            this.authUrl = authUrl;
        }

        public String getAdminUrl1x() {
            return adminUrl1x;
        }

        public void setAdminUrl1x(String adminUrl1x) {
            this.adminUrl1x = adminUrl1x;
        }

        public String getDeviceListUrl1x() {
            return deviceListUrl1x;
        }

        public void setDeviceListUrl1x(String deviceListUrl1x) {
            this.deviceListUrl1x = deviceListUrl1x;
        }

        public String getAuthUrl1x() {
            return authUrl1x;
        }

        public void setAuthUrl1x(String authUrl1x) {
            this.authUrl1x = authUrl1x;
        }

        public String getAdminUrl2x() {
            return adminUrl2x;
        }

        public void setAdminUrl2x(String adminUrl2x) {
            this.adminUrl2x = adminUrl2x;
        }

        public String getDeviceListUrl2x() {
            return deviceListUrl2x;
        }

        public void setDeviceListUrl2x(String deviceListUrl2x) {
            this.deviceListUrl2x = deviceListUrl2x;
        }

        public String getAuthUrl2x() {
            return authUrl2x;
        }

        public void setAuthUrl2x(String authUrl2x) {
            this.authUrl2x = authUrl2x;
        }

        public String getAdminUrl3x() {
            return adminUrl3x;
        }

        public void setAdminUrl3x(String adminUrl3x) {
            this.adminUrl3x = adminUrl3x;
        }

        public String getDeviceListUrl3x() {
            return deviceListUrl3x;
        }

        public void setDeviceListUrl3x(String deviceListUrl3x) {
            this.deviceListUrl3x = deviceListUrl3x;
        }

        public String getAuthUrl3x() {
            return authUrl3x;
        }

        public void setAuthUrl3x(String authUrl3x) {
            this.authUrl3x = authUrl3x;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getSnHead() {
            return this.snHead;
        }

        public void setSnHead(String snHead) {
            this.snHead = snHead;
        }

}
