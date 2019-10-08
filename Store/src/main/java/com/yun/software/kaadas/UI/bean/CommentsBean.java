package com.yun.software.kaadas.UI.bean;


import java.util.List;

public class CommentsBean {

    private String logo;//评论人图像
    private String content;//评论内容
    private String score;//评价分数
    private String productLabels;//产品属性
    private String customerId; //用户id
    private String productId;//产品id
    private String userName; // 用户名
    private List<String> urls;// 用户评论上传的图片
    private String createDate;//评论时间

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getProductLabels() {
        return productLabels;
    }

    public void setProductLabels(String productLabels) {
        this.productLabels = productLabels;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


}
