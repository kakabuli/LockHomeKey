package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class ChangeNicknameBean {
    /**
     * uid*	string
     *
     * 用户登录返回的uid
     * nickname*	string
     *
     * 用户昵称
     * */
    private String uid;

    public ChangeNicknameBean(String uid, String nickname) {
        this.uid = uid;
        this.nickname = nickname;
    }

    private String nickname;
}
