package com.kaadas.lock.publiclibrary.ble.bean;

/**
 * Create By lxj  on 2019/3/14
 * Describe
 */
public class WarringRecord {

    /**
     * warningType : 2
     * warningTime : 1561557361639
     * content : 预警内容
     */

    private int warningType;
    private long warningTime;
    private String content = "1";


    public WarringRecord(int warningType, long warningTime) {
        this.warningType = warningType;
        this.warningTime = warningTime;
    }

    public WarringRecord() {
    }

    public WarringRecord(int warningType, long warningTime, String content) {
        this.warningType = warningType;
        this.warningTime = warningTime;
        this.content = content;
    }

    public int getWarningType() {
        return warningType;
    }

    public void setWarningType(int warningType) {
        this.warningType = warningType;
    }

    public long getWarningTime() {
        return warningTime;
    }

    public void setWarningTime(long warningTime) {
        this.warningTime = warningTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "WarringRecord{" +
                "warningType=" + warningType +
                ", warningTime=" + warningTime +
                ", content='" + content + '\'' +
                '}';
    }
}
