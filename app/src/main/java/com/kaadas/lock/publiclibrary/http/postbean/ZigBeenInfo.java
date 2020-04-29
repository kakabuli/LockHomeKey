package com.kaadas.lock.publiclibrary.http.postbean;

public class ZigBeenInfo {

    public ZigBeenInfo() {
    }

    public ZigBeenInfo(String zigbeeSN, String uid, String gwSN) {
        this.zigbeeSN = zigbeeSN;
        this.uid = uid;
        this.gwSN = gwSN;
    }

    /**
     * zigbeeSN : ZG03200310001
     * uid : 5e20337f4d27d6da123c1a91
     * gwSN : GW01200310001
     */
    private String zigbeeSN;
    private String uid;
    private String gwSN;

    public void setZigbeeSN(String zigbeeSN) {
        this.zigbeeSN = zigbeeSN;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setGwSN(String gwSN) {
        this.gwSN = gwSN;
    }

    public String getZigbeeSN() {
        return zigbeeSN;
    }

    public String getUid() {
        return uid;
    }

    public String getGwSN() {
        return gwSN;
    }

    @Override
    public String toString() {
        return "ZigBeenInfo{" +
                "zigbeeSN='" + zigbeeSN + '\'' +
                ", uid='" + uid + '\'' +
                ", gwSN='" + gwSN + '\'' +
                '}';
    }
}
