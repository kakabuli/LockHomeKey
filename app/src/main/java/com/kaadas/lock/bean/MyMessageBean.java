package com.kaadas.lock.bean;

import java.io.Serializable;

public class MyMessageBean implements Serializable {
    private String headPortrait;
    private String title;
    private Long time;
    private String content;
    private String id;


    public MyMessageBean(String headPortrait, String title, Long time, String content, String id, int type) {
        this.headPortrait = headPortrait;
        this.title = title;
        this.time = time;
        this.content = content;
        this.id = id;
        this.type = type;
    }

    /**
     * 消息类型：1系统消息 2共享设备授权信息 3网关授权信息
     */

    private int type;

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
