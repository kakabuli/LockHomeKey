package com.yun.software.kaadas.UI.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class AddressBean implements Parcelable {

    private String id;
    private String customerId;
    private String name;
    private String phone;
    private String zipCode;
    private String province;
    private String city;
    private String area;
    private String address;
    private String isDefault;
    private String createDate;

    protected AddressBean(Parcel in) {
        id = in.readString();
        customerId = in.readString();
        name = in.readString();
        phone = in.readString();
        zipCode = in.readString();
        province = in.readString();
        city = in.readString();
        area = in.readString();
        address = in.readString();
        isDefault = in.readString();
        createDate = in.readString();
    }

    public static final Creator<AddressBean> CREATOR = new Creator<AddressBean>() {
        @Override
        public AddressBean createFromParcel(Parcel in) {
            return new AddressBean(in);
        }

        @Override
        public AddressBean[] newArray(int size) {
            return new AddressBean[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(customerId);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(zipCode);
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(area);
        dest.writeString(address);
        dest.writeString(isDefault);
        dest.writeString(createDate);
    }
}
