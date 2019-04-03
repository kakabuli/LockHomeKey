package com.kaadas.lock.publiclibrary.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Create By lxj  on 2019/3/7
 * Describe
 */
public class ForeverPassword implements Serializable {
    /**
     * num	String	密钥编号
     * nickName	String	密钥昵称
     * createTime	timestamp	添加时间
     * type	int	密钥周期类型：1永久 2时间段 3周期 4 24小时
     * startTime	timestamp	时间段密钥开始时间
     * endTime	timestamp	时间段密钥结束时间
     * items	list	周期密码星期几
     */
    private String num;
    private String nickName;
    private long createTime;
    private int type;  //密码类型
    private long startTime;
    private long endTime;
    private List<String> items;

    public ForeverPassword() {
    }

    public ForeverPassword(String num, String nickName, long createTime, int type, long startTime, long endTime, List<String> items) {
        this.num = num;
        this.nickName = nickName;
        this.createTime = createTime;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.items = items;
    }




    public ForeverPassword(String num, String nickName, int type) {
        this.num = num;
        this.nickName = nickName;
        this.type = type;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForeverPassword that = (ForeverPassword) o;
        return Objects.equals(num, that.num);
    }

    @Override
    public int hashCode() {
        return Objects.hash(num);
    }


    @Override
    public String toString() {
        return "ForeverPassword{" +
                "num='" + num + '\'' +
                ", nickName='" + nickName + '\'' +
                ", createTime=" + createTime +
                ", type=" + type +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", items=" + items +
                '}';
    }
}
