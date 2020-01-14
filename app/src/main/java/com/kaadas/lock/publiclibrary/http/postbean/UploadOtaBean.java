package com.kaadas.lock.publiclibrary.http.postbean;

public class UploadOtaBean {

    /**
     * wifiSN : WF132231004
     * fileLen : 20850
     * fileUrl : http://121.201.57.214/otaUpgradeFile/2ddf2f0dde8d406c840207a8d3c3006b.png
     * fileMd5 : 48d6d2a38e295611aa68959ccd6b3771
     * devNum : 1
     * fileVersion : 1.1.0
     */

    private String wifiSN;
    private int fileLen;
    private String fileUrl;
    private String fileMd5;
    private int devNum;
    private String fileVersion;

    public UploadOtaBean(String wifiSN, int fileLen, String fileUrl, String fileMd5, int devNum, String fileVersion) {
        this.wifiSN = wifiSN;
        this.fileLen = fileLen;
        this.fileUrl = fileUrl;
        this.fileMd5 = fileMd5;
        this.devNum = devNum;
        this.fileVersion = fileVersion;
    }

    public String getWifiSN() {
        return wifiSN;
    }

    public void setWifiSN(String wifiSN) {
        this.wifiSN = wifiSN;
    }

    public int getFileLen() {
        return fileLen;
    }

    public void setFileLen(int fileLen) {
        this.fileLen = fileLen;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public int getDevNum() {
        return devNum;
    }

    public void setDevNum(int devNum) {
        this.devNum = devNum;
    }

    public String getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(String fileVersion) {
        this.fileVersion = fileVersion;
    }
}
