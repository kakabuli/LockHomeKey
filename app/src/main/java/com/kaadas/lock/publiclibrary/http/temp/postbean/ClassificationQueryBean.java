package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class ClassificationQueryBean {
    private String start_time;
    private String content;
    private String uid;
    private String end_time;
    private String devname;
    private String page;
    private String pageNum;

    public ClassificationQueryBean(String start_time, String content, String uid, String end_time, String devname, String page, String pageNum) {
        this.start_time = start_time;
        this.content = content;
        this.uid = uid;
        this.end_time = end_time;
        this.devname = devname;
        this.page = page;
        this.pageNum = pageNum;
    }
}
