package com.kaadas.lock.bean;

import java.io.Serializable;

/**
 * Created by David on 2019/2/18
 */
public class AddTemporaryPasswordBean implements Serializable {
    public AddTemporaryPasswordBean(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }

    private String name;
    private boolean isSelected;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
