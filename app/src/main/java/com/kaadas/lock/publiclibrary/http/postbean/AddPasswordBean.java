package com.kaadas.lock.publiclibrary.http.postbean;

import com.kaadas.lock.publiclibrary.bean.ForeverPassword;

import java.io.Serializable;
import java.util.List;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class AddPasswordBean {

    /**
     * uid : 5c70d9493c554639ea93cc90
     * devName : GI132231004
     * pwdList : [{"pwdType":1,"num":"01","nickName":"密码1","type":1,"startTime":1551774543,"endTime":1551774543,"items":["1","3"]}]
     */

    private String uid;
    private String devName;
    private List<Password> pwdList;

    public AddPasswordBean(String uid, String devName, List<Password> pwdList) {
        this.uid = uid;
        this.devName = devName;
        this.pwdList = pwdList;
    }

    public AddPasswordBean() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public List<Password> getPwdList() {
        return pwdList;
    }

    public void setPwdList(List<Password> pwdList) {
        this.pwdList = pwdList;
    }

    public static class Password  implements Serializable {
        /**
         * pwdType	是	int	密钥类型：1密码 2临时密码 3指纹密码 4卡片密码
         * num	是	String	密钥编号
         * nickName	是	String	密钥昵称
         * type	否	int	密钥周期类型：1永久 2时间段 3周期 4 24小时
         * startTime	否	timestamp	时间段密钥开始时间
         * endTime	否	timestamp	时间段密钥结束时间
         * items	否	list	周期密码星期几
         */

        private int pwdType;
        private String num;
        private String nickName;
        private int type;
        private long startTime;
        private long endTime;
        private List<String> items;

        public Password(int pwdType, String num, String nickName, int type, int startTime, int endTime, List<String> items) {
            this.pwdType = pwdType;
            this.num = num;
            this.nickName = nickName;
            this.type = type;
            this.startTime = startTime;
            this.endTime = endTime;
            this.items = items;
        }

        public Password() {
        }

        public Password(int pwdType, ForeverPassword foreverPassword) {
            this.pwdType = pwdType;
            this.num = foreverPassword.getNum();
            this.nickName = foreverPassword.getNickName();
            this.type = foreverPassword.getType();
            this.startTime = foreverPassword.getStartTime();
            this.endTime = foreverPassword.getEndTime();
            this.items = foreverPassword.getItems();

        }

        public Password(int pwdType, String num, String nickName, int type) {
            this.pwdType = pwdType;
            this.num = num;
            this.nickName = nickName;
            this.type = type;
        }

        public Password(int pwdType, String num, String nickName, int type, long startTime, long endTime, List<String> items) {
            this.pwdType = pwdType;
            this.num = num;
            this.nickName = nickName;
            this.type = type;
            this.startTime = startTime;
            this.endTime = endTime;
            this.items = items;
        }

        public int getPwdType() {
            return pwdType;
        }

        public void setPwdType(int pwdType) {
            this.pwdType = pwdType;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }
    }
}
