package com.yun.software.kaadas.UI.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {


    private int id;//登录账号id(基本信息)
    private String userName;//昵称
    private String tel;//(必填)(string) ID:注册电话
    private String logo;//头像url
    private String token;
    private String inviteCode ;//邀请码;(邀请码为8位系统生成的数字),有邀请码表示已绑定,没有表示未绑定
    private String levelCN;//客户等级;初出茅庐(ordinary_customer);销售总监(saler_customer);分公司(partner_customer);
    private int gender;//1:男;0:女
    private String genderCN;
    private String age;
    private String randomCode;//自己的邀请码
    private String birthday;//生日
    private String qrImgUrl; //我的二维码
    private String taskNum; //任务数
    private String couponNum;//优惠券数量
    private String score;//积分数量
    private String taskTotalMoney;//总金额
    private String taskHaveMoney;//待解锁金额
    private String inviteNum; //还剩余推荐
    private String userCouponUseNum; //没有使用的
    private String userCouponOverdueNum;//已经过期的
    private boolean isGetTimeDelay;//延保卡

    protected User(Parcel in) {
        id = in.readInt();
        userName = in.readString();
        tel = in.readString();
        logo = in.readString();
        token = in.readString();
        inviteCode = in.readString();
        levelCN = in.readString();
        gender = in.readInt();
        genderCN = in.readString();
        age = in.readString();
        randomCode = in.readString();
        birthday = in.readString();
        qrImgUrl = in.readString();
        taskNum = in.readString();
        couponNum = in.readString();
        score = in.readString();
        taskTotalMoney = in.readString();
        taskHaveMoney = in.readString();
        inviteNum = in.readString();
        userCouponUseNum = in.readString();
        userCouponOverdueNum = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(userName);
        dest.writeString(tel);
        dest.writeString(logo);
        dest.writeString(token);
        dest.writeString(inviteCode);
        dest.writeString(levelCN);
        dest.writeInt(gender);
        dest.writeString(genderCN);
        dest.writeString(age);
        dest.writeString(randomCode);
        dest.writeString(birthday);
        dest.writeString(qrImgUrl);
        dest.writeString(taskNum);
        dest.writeString(couponNum);
        dest.writeString(score);
        dest.writeString(taskTotalMoney);
        dest.writeString(taskHaveMoney);
        dest.writeString(inviteNum);
        dest.writeString(userCouponUseNum);
        dest.writeString(userCouponOverdueNum);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public boolean isGetTimeDelay() {
        return isGetTimeDelay;
    }

    public void setGetTimeDelay(boolean getTimeDelay) {
        isGetTimeDelay = getTimeDelay;
    }

    public String getUserCouponUseNum() {
        return userCouponUseNum;
    }

    public void setUserCouponUseNum(String userCouponUseNum) {
        this.userCouponUseNum = userCouponUseNum;
    }

    public String getUserCouponOverdueNum() {
        return userCouponOverdueNum;
    }

    public void setUserCouponOverdueNum(String userCouponOverdueNum) {
        this.userCouponOverdueNum = userCouponOverdueNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getLevelCN() {
        return levelCN;
    }

    public void setLevelCN(String levelCN) {
        this.levelCN = levelCN;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getGenderCN() {
        return genderCN;
    }

    public void setGenderCN(String genderCN) {
        this.genderCN = genderCN;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getRandomCode() {
        return randomCode;
    }

    public void setRandomCode(String randomCode) {
        this.randomCode = randomCode;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getQrImgUrl() {
        return qrImgUrl;
    }

    public void setQrImgUrl(String qrImgUrl) {
        this.qrImgUrl = qrImgUrl;
    }

    public String getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(String taskNum) {
        this.taskNum = taskNum;
    }

    public String getCouponNum() {
        return couponNum;
    }

    public void setCouponNum(String couponNum) {
        this.couponNum = couponNum;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTaskTotalMoney() {
        return taskTotalMoney;
    }

    public void setTaskTotalMoney(String taskTotalMoney) {
        this.taskTotalMoney = taskTotalMoney;
    }

    public String getTaskHaveMoney() {
        return taskHaveMoney;
    }

    public void setTaskHaveMoney(String taskHaveMoney) {
        this.taskHaveMoney = taskHaveMoney;
    }

    public String getInviteNum() {
        return inviteNum;
    }

    public void setInviteNum(String inviteNum) {
        this.inviteNum = inviteNum;
    }
}
