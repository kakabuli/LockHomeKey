package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class GetRandomByPhone {

    /**
     * tel : string
     * code : string
     */

    private String tel;
    private String code;

    public GetRandomByPhone(String tel, String code) {
        this.tel = tel;
        this.code = code;
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
