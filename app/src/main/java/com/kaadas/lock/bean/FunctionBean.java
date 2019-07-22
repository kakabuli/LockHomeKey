package com.kaadas.lock.bean;

public class FunctionBean {
    private String name;
    private int imageID;
    private int type;

    public FunctionBean(String name, int imageID, int type) {
        this.name = name;
        this.imageID = imageID;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
