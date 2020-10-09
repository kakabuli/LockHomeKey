package com.kaadas.lock.publiclibrary.xm.bean;

import java.io.Serializable;

public class QrCodeBean implements Serializable {
    private String s;
    private String u;
    private String p;

    public QrCodeBean(String s, String u, String p) {
        this.s = s;
        this.u = u;
        this.p = p;
    }

    public void setS(String s) {
        this.s = s;
    }

    public void setU(String u) {
        this.u = u;
    }

    public void setP(String p) {
        this.p = p;
    }
}
