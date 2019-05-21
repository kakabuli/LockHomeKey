package com.kaadas.lock.bean;

import java.io.Serializable;

public class GatewaySettingItemBean implements Serializable {
    private String title;
    private String content;
    private boolean isSetting;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSetting() {
        return isSetting;
    }

    public void setSetting(boolean setting) {
        isSetting = setting;
    }
}
