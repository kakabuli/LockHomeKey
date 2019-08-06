package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class SendEmailBean {
    private String mail;
    private int world=1;

    public SendEmailBean(String mail) {
        this.mail = mail;
    }

    public SendEmailBean(String mail, int world) {
        this.mail = mail;
        this.world = world;
    }

    public int getWorld() {
        return world;
    }

    public void setWorld(int world) {
        this.world = world;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
