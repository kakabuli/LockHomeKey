package com.kaadas.lock.bean;

import java.io.Serializable;

/**
 * Created by David on 2019/2/18
 */
public class TemporaryPasswordBean implements Serializable {
    private String name;
    private String time;

    public TemporaryPasswordBean(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
