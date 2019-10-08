package com.yun.software.kaadas.UI.bean;

/**
 * Created by yanliang
 * on 2019/1/9 13:57
 */
public class FeedPicture {
    private String path;
    private boolean isShowDelete=false;
    private String url;

    public FeedPicture(String path, boolean isShowDelete,String url) {
        this.path = path;
        this.isShowDelete = isShowDelete;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isShowDelete() {
        return isShowDelete;
    }

    public void setShowDelete(boolean showDelete) {
        isShowDelete = showDelete;
    }
}
