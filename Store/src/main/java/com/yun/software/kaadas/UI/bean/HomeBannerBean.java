package com.yun.software.kaadas.UI.bean;

public class HomeBannerBean {

    private String imgUrl;

    private String id;

    private String bannerType;
    private String bannerUrl;
    private String bannerId;

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getBannerType() {
        return bannerType;
    }

    public void setBannerType(String bannerType) {
        this.bannerType = bannerType;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "HomeBannerBean{" +
                "imgUrl='" + imgUrl + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
