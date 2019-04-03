package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class WhetherEnabledAutomaticOpenLockBean {
    private String user_id;
    private String devname;
    private String auto_lock;

    public WhetherEnabledAutomaticOpenLockBean(String user_id, String devname, String auto_lock) {
        this.user_id = user_id;
        this.devname = devname;
        this.auto_lock = auto_lock;
    }
}
