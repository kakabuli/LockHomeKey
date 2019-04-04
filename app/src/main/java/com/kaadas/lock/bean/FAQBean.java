package com.kaadas.lock.bean;

import java.io.Serializable;

public class FAQBean implements Serializable {

    private String title;

    private String content;

    private Boolean flag=false;

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

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }
}
