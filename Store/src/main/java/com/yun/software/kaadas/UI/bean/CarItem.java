package com.yun.software.kaadas.UI.bean;

import java.text.DecimalFormat;

/**
 * Created by yanliang
 * on 2018/8/31 09:28
 */

public class CarItem {

    /**
     * total : 2
     * userName : 华夏进出口贸易有限公司
     * list : [{"id":52,"productId":8,"productName":null,"qty":4,"customerId":3,"price":null,"traffickerId":2,"createDate":"2018-09-11 17:34:35","product":{"id":8,"logo":null,"qty":1213,"unitId":"rise","unitIdCN":"升","userId":2,"realUserId":2,"userName":"华夏进出口贸易有限公司","priceType":"fixed","priceTypeCN":"固定价","price":1231,"arrivedPort":null,"arrivedDate":null,"attachIds":"278","attachNames":"88dc7fcc74be4e24f1e0bacbd8bef48d.jpg","status":"product_up","statusCN":"上架","minBuyQty":1,"maxBuyQty":100,"storeAddress":"湖北省武汉市汉阳区洗马长街87号 汉中,黄鹤楼,建桥","storeLatitude":30.5811,"storeLongitude":114.3162,"releaseDate":null,"createBy":2,"createDate":"2018-08-24 10:17:38","updateBy":1,"updateDate":"2018-08-30 09:40:49","publicity":null,"name":"曹妃甸","englistName":"Merry","categoryId":null,"productAgio":null},"productAgio":[]},{"id":49,"productId":18,"productName":null,"qty":1,"customerId":3,"price":null,"traffickerId":2,"createDate":"2018-09-11 17:33:07","product":{"id":18,"logo":null,"qty":2000,"unitId":"ton","unitIdCN":"吨","userId":2,"realUserId":2,"userName":"华夏进出口贸易有限公司","priceType":"brent","priceTypeCN":"布伦特","price":1000,"arrivedPort":8,"arrivedDate":"2018-08-31","attachIds":"312,314","attachNames":"附件1,附件2","status":"product_up","statusCN":"上架","minBuyQty":1,"maxBuyQty":100,"storeAddress":"湖北省武汉市洪山区珞狮路318号 珞狮南路,红旗,陈家湾","storeLatitude":30.5811,"storeLongitude":114.3162,"releaseDate":"2018-09-07 00:00:00","createBy":2,"createDate":"2018-08-29 19:30:18","updateBy":1,"updateDate":"2018-08-30 09:41:12","publicity":null,"name":"测试124","englistName":"test123","categoryId":null,"productAgio":null},"productAgio":[{"id":24,"productId":18,"agio":1,"month":"2018-09","createBy":2,"createDate":"2018-08-29 19:30:41"},{"id":25,"productId":18,"agio":2,"month":"2018-10","createBy":2,"createDate":"2018-08-29 19:30:41"},{"id":26,"productId":18,"agio":3,"month":"2018-11","createBy":2,"createDate":"2018-08-29 19:30:41"},{"id":27,"productId":18,"agio":4,"month":"2018-12","createBy":2,"createDate":"2018-08-29 19:30:41"},{"id":28,"productId":18,"agio":5,"month":"2019-01","createBy":2,"createDate":"2018-08-29 19:30:41"},{"id":29,"productId":18,"agio":6,"month":"2019-02","createBy":2,"createDate":"2018-08-29 19:30:41"},{"id":30,"productId":18,"agio":7,"month":"2019-03","createBy":2,"createDate":"2018-08-29 19:30:41"},{"id":31,"productId":18,"agio":8,"month":"2019-04","createBy":2,"createDate":"2018-08-29 19:30:41"},{"id":32,"productId":18,"agio":9,"month":"2019-05","createBy":2,"createDate":"2018-08-29 19:30:41"},{"id":33,"productId":18,"agio":10,"month":"2019-06","createBy":2,"createDate":"2018-08-29 19:30:41"},{"id":34,"productId":18,"agio":11,"month":"2019-07","createBy":2,"createDate":"2018-08-29 19:30:41"}]}]
     * traffickerId : 2
     */

    private String userName;
    private int traffickerId;
    private boolean ischeck=false;
    private boolean isEdite=false;
    private String totalMoney;


    private String id;//购物车ID
    private String agentProductId;//渠道产品ID
    private String skuProductName;//渠道产品名称
    private String attributeComboName;//属性组合值
    private String logo;
    private String price; //价格
    private String qty;//购买数量

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgentProductId() {
        return agentProductId;
    }

    public void setAgentProductId(String agentProductId) {
        this.agentProductId = agentProductId;
    }

    public String getSkuProductName() {
        return skuProductName;
    }

    public void setSkuProductName(String skuProductName) {
        this.skuProductName = skuProductName;
    }

    public String getAttributeComboName() {
        return attributeComboName;
    }

    public void setAttributeComboName(String attributeComboName) {
        this.attributeComboName = attributeComboName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }


    public String getTotalMoney() {
        double totoalmoney=Integer.parseInt(qty)*Double.parseDouble(price);
        DecimalFormat mFormat = new DecimalFormat("#0.00");
        totalMoney=mFormat.format((double)totoalmoney);
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {

        this.totalMoney = totalMoney;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getTraffickerId() {
        return traffickerId;
    }

    public void setTraffickerId(int traffickerId) {
        this.traffickerId = traffickerId;
    }

    public boolean isIscheck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public boolean isEdite() {
        return isEdite;
    }

    public void setEdite(boolean edite) {
        isEdite = edite;
    }

    public void addOne() {
    }

    public void plusOne() {
    }

    public void changeState() {
        ischeck=!ischeck;
    }

    /**
     * 增减数量，操作通用，数据不通用
     */
    public void addOrReduceGoodsNum(boolean isPlus) {
        if (isPlus) {
            qty = String.valueOf(Integer.parseInt(qty) + 1);
        } else {
            int i = Integer.parseInt(qty);
            if (i > 1) {
                qty = String.valueOf(i - 1);
            } else {
                qty = "1";
            }
        }
    }

}
