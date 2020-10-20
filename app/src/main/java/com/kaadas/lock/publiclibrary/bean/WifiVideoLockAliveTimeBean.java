package com.kaadas.lock.publiclibrary.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class WifiVideoLockAliveTimeBean implements Serializable {
    /**
     * "alive_time": {
     *                 "keep_alive_snooze":[1,2,3,4,5,6,7],
     *                 "snooze_start_time":0,
     *                 "snooze_end_time":86400
     *             },
     */

    private int[] keep_alive_snooze;
    private int snooze_start_time;
    private int snooze_end_time;


    public int[] getKeep_alive_snooze() {
        return keep_alive_snooze;
    }

    public void setKeep_alive_snooze(int[] keep_alive_snooze) {
        this.keep_alive_snooze = keep_alive_snooze;
    }

    public int getSnooze_start_time() {
        return snooze_start_time;
    }

    public void setSnooze_start_time(int snooze_start_time) {
        this.snooze_start_time = snooze_start_time;
    }

    public int getSnooze_end_time() {
        return snooze_end_time;
    }

    public void setSnooze_end_time(int snooze_end_time) {
        this.snooze_end_time = snooze_end_time;
    }
}
