package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class GetOperationRecordBean {
    /**
     *     {
     *         "devName":"BT01191910010",
     *         "page":2
     *     }
     */


    private String devName;
    private int page;

    public GetOperationRecordBean(String devName, int page) {
        this.devName = devName;
        this.page = page;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
