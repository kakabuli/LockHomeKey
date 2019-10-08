package com.yun.software.kaadas.UI.bean;

import java.util.List;

public class CommitOrderBean {

    private String totalPrice;
    private String totalQty;

    private AddressBean address;
    private CouponBean coupon;
    private List<CarItem> shopcar;

    private ActivityProduct activity;
    private AgentProduct agentProduct;



    public AgentProduct getAgentProduct() {
        return agentProduct;
    }

    public void setAgentProduct(AgentProduct agentProduct) {
        this.agentProduct = agentProduct;
    }

    public ActivityProduct getActivity() {
        return activity;
    }

    public void setActivity(ActivityProduct activity) {
        this.activity = activity;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(String totalQty) {
        this.totalQty = totalQty;
    }


    public AddressBean getAddress() {
        return address;
    }

    public void setAddress(AddressBean address) {
        this.address = address;
    }

    public CouponBean getCoupon() {
        return coupon;
    }

    public void setCoupon(CouponBean coupon) {
        this.coupon = coupon;
    }

    public List<CarItem> getShopcar() {
        return shopcar;
    }

    public void setShopcar(List<CarItem> shopcar) {
        this.shopcar = shopcar;
    }
}
