package com.kaadas.lock.bean;

/**
 * Created by David on 2019/4/19
 */
public class BluetoothItemRecordBean {
    public BluetoothItemRecordBean() {
    }

    @Override
    public String toString() {
        return "BluetoothItemRecordBean{" +
                "content='" + content + '\'' +
                ", strRight='" + strRight + '\'' +
                ", iconImg='" + iconImg + '\'' +
                ", open_time='" + open_time + '\'' +
                ", firstData=" + firstData +
                ", lastData=" + lastData +
                '}';
    }

    private String content;//中间的数据
    private String strRight;//右边的数据

    public String getStrRight() {
        return strRight;
    }

    public void setStrRight(String strRight) {
        this.strRight = strRight;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    //用户编号
    String iconImg;//图标类型
    String open_time;//开门时间
    boolean firstData;//第一条数据
    boolean lastData;//最后一条数据

    public String getIconImg() {
        return iconImg;
    }

    public void setIconImg(String iconImg) {
        this.iconImg = iconImg;
    }

    public BluetoothItemRecordBean(String content,String strRight, String iconImg, String open_time, boolean firstData, boolean lastData) {
        this.content = content;
        this.strRight=strRight;
        this.iconImg = iconImg;
        this.open_time = open_time;
        this.firstData = firstData;
        this.lastData = lastData;
    }


    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public boolean isFirstData() {
        return firstData;
    }

    public void setFirstData(boolean firstData) {
        this.firstData = firstData;
    }

    public boolean isLastData() {
        return lastData;
    }

    public void setLastData(boolean lastData) {
        this.lastData = lastData;
    }
}
