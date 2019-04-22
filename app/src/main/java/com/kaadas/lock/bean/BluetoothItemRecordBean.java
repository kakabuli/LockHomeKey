package com.kaadas.lock.bean;

/**
 * Created by David on 2019/4/19
 */
public class BluetoothItemRecordBean {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    //用户编号
    String open_type;//开门类型
    String open_time;//开门时间
    boolean firstData;//第一条数据
    boolean lastData;//最后一条数据

    public BluetoothItemRecordBean(String content, String open_type, String open_time, boolean firstData, boolean lastData) {
        this.content = content;
        this.open_type = open_type;
        this.open_time = open_time;
        this.firstData = firstData;
        this.lastData = lastData;
    }


    public String getOpen_type() {
        return open_type;
    }

    public void setOpen_type(String open_type) {
        this.open_type = open_type;
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
