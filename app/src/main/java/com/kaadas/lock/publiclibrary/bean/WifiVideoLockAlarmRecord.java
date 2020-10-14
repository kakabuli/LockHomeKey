package com.kaadas.lock.publiclibrary.bean;

import java.io.Serializable;

public class WifiVideoLockAlarmRecord implements Serializable {
    /**
     {
     "_id": "5df0abf54d27d6da12fb4c71",
     "time": "1541468973342",
     "type": 4,
     "wifiSN": "WF132231004",
     "createTime": "1576054908866",
     "productSN" : "KV51203710173",
     "eventId" : "KV512037101731484f83217d941ae9e354b3f3e68a342",
     "thumbUrl" : "https://test.juziwulian.com:8050/kx/api/upload/KV512037101731484f83217d941ae9e354b3f3e68a342.jpg",
     "fileDate" : "20200924",
     "fileName" : "152606",
     "height" : 1920,
     "startTime" : 1600917264,
     "thumbState" : true,
     "updateTime" : 1600933259,
     "vedioTime" : 10,
     "width" : 1080
     }
     */

    private String _id;
    private String time;
    private int type;
    private String wifiSN;
    private String createTime;
    private String productSN;
    private String eventId;//锁定报警，劫持报警，防撬报警，机械钥匙报警，门锁布防报警，门铃报警，pir徘徊报警有图片，视频信息,存在下列属性
    private String thumbUrl;
    private String fileDate;//视频地址
    private String fileName;
    private String devtype;
    private int height;
    private int width;
    private long startTime;
    private boolean thumbState;
    private int vedioTime;
    private long updateTime;

    private boolean first = false; // 是否是第一个
    private boolean last = false; //是否是最后一个

    private String dayTime;

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getProductSN() {
        return productSN;
    }

    public void setProductSN(String productSN) {
        this.productSN = productSN;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getFileDate() {
        return fileDate;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean isThumbState() {
        return thumbState;
    }

    public void setThumbState(boolean thumbState) {
        this.thumbState = thumbState;
    }

    public int getVedioTime() {
        return vedioTime;
    }

    public void setVedioTime(int vedioTime) {
        this.vedioTime = vedioTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getWifiSN() {
        return wifiSN;
    }

    public void setWifiSN(String wifiSN) {
        this.wifiSN = wifiSN;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime;
    }

    @Override
    public String toString() {
        return "WifiVideoLockAlarmRecord{" +
                "_id='" + _id + '\'' +
                ", time=" + time +
                ", type=" + type +
                ", wifiSN='" + wifiSN + '\'' +
                ", createTime='" + createTime + '\'' +
                ", productSN='" + productSN + '\'' +
                ", eventId='" + eventId + '\'' +
                ", thumbUrl='" + thumbUrl + '\'' +
                ", fileDate='" + fileDate + '\'' +
                ", fileName='" + fileName + '\'' +
                ", height=" + height +
                ", width=" + width +
                ", startTime=" + startTime +
                ", thumbState=" + thumbState +
                ", vedioTime=" + vedioTime +
                ", updateTime=" + updateTime +
                '}';
    }
}