package com.yun.software.kaadas.UI.bean;

public class KanjiaHelp {

    private String name;
    private String logo;
    private String cutPrice;//砍了多少钱
    private String time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCutPrice() {
        return cutPrice;
    }

    public void setCutPrice(String cutPrice) {
        this.cutPrice = cutPrice;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "KanjiaHelp{" +
                "name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", cutPrice='" + cutPrice + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
