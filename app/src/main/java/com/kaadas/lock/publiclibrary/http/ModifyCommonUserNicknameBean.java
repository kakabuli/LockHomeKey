package com.kaadas.lock.publiclibrary.http;

/**
 * Create By David
 */
public class ModifyCommonUserNicknameBean {

//        "ndId": "GI132231004",
//            "userNickName": "老二"
    private String ndId;

    public ModifyCommonUserNicknameBean(String ndId, String userNickName) {
        this.ndId = ndId;
        this.userNickName = userNickName;
    }

    public String getNdId() {
        return ndId;
    }

    public void setNdId(String ndId) {
        this.ndId = ndId;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    private String userNickName;
}
