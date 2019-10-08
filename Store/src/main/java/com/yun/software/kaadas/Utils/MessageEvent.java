package com.yun.software.kaadas.Utils;

public class MessageEvent {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String userName;

    private String userImage;

    private boolean updataUserInfo;

    public boolean isUpdataUserInfo() {
        return updataUserInfo;
    }

    public void setUpdataUserInfo(boolean updataUserInfo) {
        this.updataUserInfo = updataUserInfo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public MessageEvent(String message) {
        this.message = message;
    }
}
