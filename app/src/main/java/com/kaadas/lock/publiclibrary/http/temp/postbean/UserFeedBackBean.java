package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class UserFeedBackBean {
    /**
     * uid*	string
     *
     * 用户登录返回的uid
     * suggest*	string
     *
     * 留言的内容
     * */
    private String uid;
    private String suggest;

    public UserFeedBackBean(String uid, String suggest) {
        this.uid = uid;
        this.suggest = suggest;
    }
}
