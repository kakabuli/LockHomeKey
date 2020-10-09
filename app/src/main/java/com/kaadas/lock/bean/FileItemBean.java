package com.kaadas.lock.bean;

import android.net.Uri;

import com.kaadas.lock.utils.DateUtils;

import java.io.Serializable;
import java.net.URI;

public class FileItemBean implements Serializable {
    private URI uri;

    private String name;

    private long lastModified;

    private String path;

    private int type;//1图片，2视频

    private String suffix;

    public FileItemBean(String name , String path , URI uri, long lastModified,String suffix, int type){
        this.name = name;
        this.path = path;
        this.uri = uri;
        this.lastModified = lastModified;
        this.type = type;
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "FileItemBean{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                ", lastModified=" + DateUtils.getDateTimeFromMillisecond(lastModified) +
                ", path='" + path + '\'' +
                ", type=" + type +
                ", suffix='" + suffix + '\'' +
                '}';
    }
}
