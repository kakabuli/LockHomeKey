package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class GetRandomByEmail {

    /**
     * mail : string
     */

    private String mail;




    public GetRandomByEmail(String mail) {
        this.mail = mail;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
