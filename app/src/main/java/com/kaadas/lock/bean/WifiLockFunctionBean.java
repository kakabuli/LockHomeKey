package com.kaadas.lock.bean;

public class WifiLockFunctionBean {

    private int image;

    private String name;

    private int type;

    int number;

    public WifiLockFunctionBean(String name, int image, int type) {
        this.image = image;
        this.name = name;
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
