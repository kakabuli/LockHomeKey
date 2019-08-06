package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class SendMessageBean {


    /**
     * tel : 18954359822
     * code : 86
     */

    private String tel;
    private String code;
    private int world=1;

    public SendMessageBean(String tel, String code) {
        this.tel = tel;
        this.code = code;
    }

    public SendMessageBean(String tel, String code, int world) {
        this.tel = tel;
        this.code = code;
        this.world = world;
    }

    public int getWorld() {
        return world;
    }

    public void setWorld(int world) {
        this.world = world;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
