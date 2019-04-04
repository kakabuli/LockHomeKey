package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class BatchModifyLockNumberInformationBean {
  private String uid;
  private String devname;
  private String infoList;

    public BatchModifyLockNumberInformationBean(String uid, String devname, String infoList) {
        this.uid = uid;
        this.devname = devname;
        this.infoList = infoList;
    }
}
