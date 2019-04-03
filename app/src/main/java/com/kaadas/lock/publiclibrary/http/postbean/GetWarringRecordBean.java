package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class GetWarringRecordBean {

    /**
     * devName : GI132231004
     * pageNum : 1
     */

    private String devName;
    private int pageNum;

    public GetWarringRecordBean(String devName, int pageNum) {
        this.devName = devName;
        this.pageNum = pageNum;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
}
