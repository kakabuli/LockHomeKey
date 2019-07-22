package com.kaadas.lock.publiclibrary.http.postbean;

public class ModifyFunctionSetBean {

    /**
     * devname : BT01191910010
     * user_id : 5c4fe492dc93897aa7d8600b
     * functionSet : 01
     */

    private String devname;
    private String user_id;
    private String functionSet;

    public ModifyFunctionSetBean(String devname, String user_id, String functionSet) {
        this.devname = devname;
        this.user_id = user_id;
        this.functionSet = functionSet;
    }

    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFunctionSet() {
        return functionSet;
    }

    public void setFunctionSet(String functionSet) {
        this.functionSet = functionSet;
    }
}
