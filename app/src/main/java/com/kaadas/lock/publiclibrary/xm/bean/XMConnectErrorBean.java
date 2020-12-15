package com.kaadas.lock.publiclibrary.xm.bean;

import java.io.Serializable;

public class XMConnectErrorBean implements Serializable {
    private String result;
    private int errno;
    private String devdid;
    private String devtype;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getDevdid() {
        return devdid;
    }

    public void setDevdid(String devdid) {
        this.devdid = devdid;
    }

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype;
    }
}
