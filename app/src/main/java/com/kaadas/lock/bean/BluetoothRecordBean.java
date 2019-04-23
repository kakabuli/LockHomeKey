package com.kaadas.lock.bean;

import java.util.List;

/**
 * Created by David on 2019/4/19
 */
public class BluetoothRecordBean {
    public BluetoothRecordBean(String time, List<BluetoothItemRecordBean> list, boolean lastData) {
        this.time = time;
        this.list = list;
        this.lastData = lastData;
    }

    String time;
    List<BluetoothItemRecordBean> list;
    private boolean lastData;

    public boolean isLastData() {
        return lastData;
    }

    public void setLastData(boolean lastData) {
        this.lastData = lastData;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<BluetoothItemRecordBean> getList() {
        return list;
    }

    public void setList(List<BluetoothItemRecordBean> list) {
        this.list = list;
    }
}
