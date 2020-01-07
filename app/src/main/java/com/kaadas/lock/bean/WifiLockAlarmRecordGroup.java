package com.kaadas.lock.bean;

import com.kaadas.lock.publiclibrary.bean.WifiLockAlarmRecord;

import java.util.List;

public class WifiLockAlarmRecordGroup {
    @Override
    public String toString() {
        return "BluetoothRecordBean{" +
                "time='" + time + '\'' +
                ", list=" + list +
                '}';
    }
    public WifiLockAlarmRecordGroup(String time, List<WifiLockAlarmRecord> list, boolean lastData) {
        this.time = time;
        this.list = list;
    }
    String time;
    List<WifiLockAlarmRecord> list;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<WifiLockAlarmRecord> getList() {
        return list;
    }

    public void setList(List<WifiLockAlarmRecord> list) {
        this.list = list;
    }
}
