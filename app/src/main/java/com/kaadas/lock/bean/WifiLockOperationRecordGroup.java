package com.kaadas.lock.bean;

import com.kaadas.lock.publiclibrary.bean.WifiLockAlarmRecord;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;

import java.util.List;

public class WifiLockOperationRecordGroup {
    @Override
    public String toString() {
        return "BluetoothRecordBean{" +
                "time='" + time + '\'' +
                ", list=" + list +
                '}';
    }
    public WifiLockOperationRecordGroup(String time, List<WifiLockOperationRecord> list ) {
        this.time = time;
        this.list = list;
    }
    String time;
    List<WifiLockOperationRecord> list;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<WifiLockOperationRecord> getList() {
        return list;
    }

    public void setList(List<WifiLockOperationRecord> list) {
        this.list = list;
    }
}
