package com.kaadas.lock.bean;

import android.text.TextUtils;

import com.kaadas.lock.publiclibrary.bean.WifiLockAlarmRecord;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;

import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;

public class WifiLockOperationRecordGroup {
    @Override
    public String toString() {
        return "BluetoothRecordBean{" +
                "time='" + time + '\'' +
                ", list=" + list +
                '}';
    }

    public WifiLockOperationRecordGroup(String time, List<WifiLockOperationRecord> list) {
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

    @Override
    public boolean equals(@Nullable Object obj) {
//        if (obj != null && obj instanceof WifiLockOperationRecordGroup) {
//            WifiLockOperationRecordGroup old = (WifiLockOperationRecordGroup) obj;
//            if (old.list != null && TextUtils.isEmpty(old.time) && this == obj && time.equals(old.getTime()) && list.size() == old.list.size()) {
//                return true;
//            }
//        }
        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hash(time, list);
    }
}
