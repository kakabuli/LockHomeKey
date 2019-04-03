package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class ModifyUserNicknameBean {
    private String uid;

    public ModifyUserNicknameBean(String uid, String nickname) {
        this.uid = uid;
        this.nickname = nickname;
    }

    private String nickname;
}
