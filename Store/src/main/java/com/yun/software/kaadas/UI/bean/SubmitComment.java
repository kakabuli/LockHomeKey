package com.yun.software.kaadas.UI.bean;

import java.util.ArrayList;
import java.util.List;

public class SubmitComment {

    public SubmitComment(String productId,String logo,String indentId,String productLabels){
        this.productId = productId;
        this.logo = logo;
        this.indentId = indentId;
        this.productLabels = productLabels;
        pictures = new ArrayList<>();
        this.pictures.add(new FeedPicture("temp", false,""));
        score = 5;
        conformityScore = 5;
        dispatchingScore = 5;
        installSatisfiedScore = 5;
    }




    private String productLabels;
    private String productId;//   产品ID
    private int score;//  评分
    private String content; //    评论内容
    private String imgs;//  非必填 评论图片
    private boolean isAnonymous;//	是否匿名
    private int conformityScore;//商品符合度未选
    private int dispatchingScore;//配送评分未选
    private int installSatisfiedScore;//安装满意度未选
    private String indentId;



    private List<FeedPicture> pictures;
    private String logo;

    public String getIndentId() {
        return indentId;
    }

    public void setIndentId(String indentId) {
        this.indentId = indentId;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<FeedPicture> getPictures() {
        return pictures;
    }

    public void setPictures(List<FeedPicture> pictures) {
        this.pictures = pictures;
    }

    public String getProductLabels() {
        return productLabels;
    }

    public void setProductLabels(String productLabels) {
        this.productLabels = productLabels;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public int getConformityScore() {
        return conformityScore;
    }

    public void setConformityScore(int conformityScore) {
        this.conformityScore = conformityScore;
    }

    public int getDispatchingScore() {
        return dispatchingScore;
    }

    public void setDispatchingScore(int dispatchingScore) {
        this.dispatchingScore = dispatchingScore;
    }

    public int getInstallSatisfiedScore() {
        return installSatisfiedScore;
    }

    public void setInstallSatisfiedScore(int installSatisfiedScore) {
        this.installSatisfiedScore = installSatisfiedScore;
    }
}


