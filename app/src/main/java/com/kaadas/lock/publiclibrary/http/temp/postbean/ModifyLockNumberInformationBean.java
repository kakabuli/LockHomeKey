package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class ModifyLockNumberInformationBean {
    private String uid;
    private String  devname;
    private String num;
    private String numNickname;

    public ModifyLockNumberInformationBean(String uid, String devname, String num, String numNickname) {
        this.uid = uid;
        this.devname = devname;
        this.num = num;
        this.numNickname = numNickname;
    }
}
