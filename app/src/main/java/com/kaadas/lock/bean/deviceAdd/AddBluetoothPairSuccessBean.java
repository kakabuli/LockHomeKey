package com.kaadas.lock.bean.deviceAdd;

import java.io.Serializable;

/**
 * Created by David on 2019/2/18
 */
public class AddBluetoothPairSuccessBean implements Serializable {

    public AddBluetoothPairSuccessBean(String name, boolean isSelected) {
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
