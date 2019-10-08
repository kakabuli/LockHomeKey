package com.yun.software.kaadas.UI.bean;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by yanliang
 * on 2018/9/10 11:41
 */

public class AddressItem implements Serializable {
    private static final long serialVersionUID = 888336895632L;

    /**
     * id : 11
     * customerId : 3
     * name : 彭学云
     * tel : 13476296651
     * province : 湖北
     * city : 武汉市
     * county : null
     * latitude : 30.4998
     * longitude : 114.3425
     * addressDetail : 湖北省武汉市洪山区珞狮南路
     * defaultAddress : false
     * createDate : 2018-09-04 16:21:33
     */

    private int id;
    private int customerId;
    private String name;
    private String tel;
    private String province;
    private String city;
    private Object county;
    private double latitude;
    private double longitude;
    private String addressDetail;
    private String addressPre;
    private boolean defaultAddress;
    private String createDate;

    public static AddressItem objectFromData(String str) {

        return new Gson().fromJson(str, AddressItem.class);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Object getCounty() {
        return county;
    }

    public void setCounty(Object county) {
        this.county = county;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getAddressPre() {
        return addressPre;
    }

    public void setAddressPre(String addressPre) {
        this.addressPre = addressPre;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
