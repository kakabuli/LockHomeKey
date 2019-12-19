package com.kaadas.lock.bean;

/**
 * 路由器数据
 */
public class RouteBean {
    private String product; //品牌
    private String typeList; //型号列表


    public RouteBean(String product, String typeList) {
        this.product = product;
        this.typeList = typeList;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getTypeList() {
        return typeList;
    }

    public void setTypeList(String typeList) {
        this.typeList = typeList;
    }
}
