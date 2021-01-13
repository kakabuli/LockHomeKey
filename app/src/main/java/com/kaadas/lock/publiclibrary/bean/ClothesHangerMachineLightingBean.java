package com.kaadas.lock.publiclibrary.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ClothesHangerMachineLightingBean implements Serializable {
    @SerializedName("switch")
    private int single;
    private int countdown;

    public int getSingle() {
        return single;
    }

    public void setSingle(int single) {
        this.single = single;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }
}
