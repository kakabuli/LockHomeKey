package com.kaadas.lock.publiclibrary.http.result;

import java.io.Serializable;
import java.util.List;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class FamilyMemberBean implements Serializable{
    @Override
    public String toString() {
        return "FamilyMemberBean{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    /**
     * code : 200
     * msg : 成功
     * data : [{"_id":"5c70e3d23c554639ea93cc93","adminname":"8618954359824","uname":"8618954359825","unickname":"8618954359825","open_purview":"3","datestart":"string","dateend":"string","items":["string"],"createTime":1551425386}]
     */

    private String code;
    private String msg;
    private List<DataBean> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }
    public boolean isSuccess(){
        if ("200".equals(getCode()) || "201".equals(getCode()) || "202".equals(getCode())){
            return true;
        }

        return false;
    }
    public static class DataBean implements Serializable {
        @Override
        public String toString() {
            return "GatewayServiceInfo{" +
                    "_id='" + _id + '\'' +
                    ", adminname='" + adminname + '\'' +
                    ", uname='" + uname + '\'' +
                    ", unickname='" + unickname + '\'' +
                    ", open_purview='" + open_purview + '\'' +
                    ", datestart='" + datestart + '\'' +
                    ", dateend='" + dateend + '\'' +
                    ", createTime=" + createTime +
                    ", items=" + items +
                    '}';
        }

        /**
         * _id : 5c70e3d23c554639ea93cc93
         * adminname : 8618954359824
         * uname : 8618954359825
         * unickname : 8618954359825
         * open_purview : 3
         * datestart : string
         * dateend : string
         * items : ["string"]
         * createTime : 1551425386
         */

        private String _id;
        private String adminname;
        private String uname;
        private String unickname;
        private String open_purview;
        private String datestart;
        private String dateend;
        private int createTime;
        private List<String> items;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getAdminname() {
            return adminname;
        }

        public void setAdminname(String adminname) {
            this.adminname = adminname;
        }

        public String getUname() {
            return uname;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }

        public String getUnickname() {
            return unickname;
        }

        public void setUnickname(String unickname) {
            this.unickname = unickname;
        }

        public String getOpen_purview() {
            return open_purview;
        }

        public void setOpen_purview(String open_purview) {
            this.open_purview = open_purview;
        }

        public String getDatestart() {
            return datestart;
        }

        public void setDatestart(String datestart) {
            this.datestart = datestart;
        }

        public String getDateend() {
            return dateend;
        }

        public void setDateend(String dateend) {
            this.dateend = dateend;
        }

        public int getCreateTime() {
            return createTime;
        }

        public void setCreateTime(int createTime) {
            this.createTime = createTime;
        }

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }
    }
}
