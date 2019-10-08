package com.yun.software.kaadas.UI.bean;

public class HotkeyBean {

    private String value;

    private String key;

    private boolean isCheck;

    public HotkeyBean(String value, String key, boolean isCheck) {
        this.value = value;
        this.key = key;
        this.isCheck = isCheck;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
