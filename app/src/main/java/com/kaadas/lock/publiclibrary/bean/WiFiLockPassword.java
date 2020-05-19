package com.kaadas.lock.publiclibrary.bean;

import java.util.List;

public class WiFiLockPassword {


    private List<PwdListBean> pwdList;
    private List<FingerprintListBean> fingerprintList;
    private List<CardListBean> cardList;
    private List<FaceListBean> faceList;

    private List<PwdNicknameBean> pwdNickname;
    private List<FingerprintNicknameBean> fingerprintNickname;
    private List<CardNicknameBean> cardNickname;
    private List<FaceNicknameBean> faceNickname;


    public WiFiLockPassword() {
    }

    public WiFiLockPassword(List<PwdListBean> pwdList, List<FingerprintListBean> fingerprintList, List<CardListBean> cardList,List<FaceListBean> faceList, List<PwdNicknameBean> pwdNickname, List<FingerprintNicknameBean> fingerprintNickname, List<CardNicknameBean> cardNickname, List<FaceNicknameBean> faceNickname) {
        this.pwdList = pwdList;
        this.fingerprintList = fingerprintList;
        this.cardList = cardList;
        this.faceList = faceList;

        this.pwdNickname = pwdNickname;
        this.fingerprintNickname = fingerprintNickname;
        this.cardNickname = cardNickname;
        this.faceNickname = faceNickname;

    }

    public List<PwdListBean> getPwdList() {
        return pwdList;
    }

    public void setPwdList(List<PwdListBean> pwdList) {
        this.pwdList = pwdList;
    }

    public List<FingerprintListBean> getFingerprintList() {
        return fingerprintList;
    }

    public void setFingerprintList(List<FingerprintListBean> fingerprintList) {
        this.fingerprintList = fingerprintList;
    }

    public List<CardListBean> getCardList() {
        return cardList;
    }

    public void setCardList(List<CardListBean> cardList) {
        this.cardList = cardList;
    }

    public List<FaceListBean> getFaceList() {
        return faceList;
    }

    public void setFaceList(List<FaceListBean> faceList) {
        this.faceList = faceList;
    }

    public List<PwdNicknameBean> getPwdNickname() {
        return pwdNickname;
    }

    public void setPwdNickname(List<PwdNicknameBean> pwdNickname) {
        this.pwdNickname = pwdNickname;
    }

    public List<FingerprintNicknameBean> getFingerprintNickname() {
        return fingerprintNickname;
    }

    public void setFingerprintNickname(List<FingerprintNicknameBean> fingerprintNickname) {
        this.fingerprintNickname = fingerprintNickname;
    }

    public List<CardNicknameBean> getCardNickname() {
        return cardNickname;
    }

    public void setCardNickname(List<CardNicknameBean> cardNickname) {
        this.cardNickname = cardNickname;
    }

    public List<FaceNicknameBean> getFaceNickname() {
        return faceNickname;
    }

    public void settFaceNickname(List<FaceNicknameBean> faceNickname) {
        this.faceNickname = faceNickname;
    }

    public static class PwdListBean {
        /**
         * createTime : 1551785021
         * endTime : 1551774543
         * items : ["1","3","6"]
         * num : 12
         * startTime : 1551774543
         * type : 1
         */

        private long createTime;
        private int endTime;
        private int num;
        private long startTime;
        private int type;
        private List<String> items;

        public PwdListBean(long createTime, int endTime, int num, long startTime, int type, List<String> items) {
            this.createTime = createTime;
            this.endTime = endTime;
            this.num = num;
            this.startTime = startTime;
            this.type = type;
            this.items = items;
        }

        public PwdListBean() {
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getEndTime() {
            return endTime;
        }

        public void setEndTime(int endTime) {
            this.endTime = endTime;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }
    }

    public static class FingerprintListBean {
        /**
         * createTime : 1551785021
         * num : 1
         */

        private long createTime;
        private int num;

        public FingerprintListBean(long createTime, int num) {
            this.createTime = createTime;
            this.num = num;
        }

        public FingerprintListBean() {
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }

    public static class CardListBean {
        public CardListBean(long createTime, int num) {
            this.createTime = createTime;
            this.num = num;
        }

        public CardListBean() {
        }

        /**
         * createTime : 1551785021
         * num : 1
         */



        private long createTime;
        private int num;

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }

    public static class FaceListBean {
        /**
         * "type":0,
         * "num":0,
         * "createTime":1589613141,
         * "startTime":1589613141,
         * "endTime":1589613141,
         * "item":["1","3","6"]
         */

        private long createTime;
        private int endTime;
        private int num;
        private long startTime;
        private int type;
        private List<String> items;

        public FaceListBean(long createTime, int endTime, int num, long startTime, int type, List<String> items) {
            this.createTime = createTime;
            this.endTime = endTime;
            this.num = num;
            this.startTime = startTime;
            this.type = type;
            this.items = items;
        }

        public FaceListBean() {
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getEndTime() {
            return endTime;
        }

        public void setEndTime(int endTime) {
            this.endTime = endTime;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }
    }

    public static class PwdNicknameBean {
        public PwdNicknameBean(int num, String nickName) {
            this.num = num;
            this.nickName = nickName;
        }

        public PwdNicknameBean() {
        }

        /**
         * num : 1
         * nickName : 啊啊啊
         */



        private int num;
        private String nickName;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
    }

    public static class FingerprintNicknameBean {
        public FingerprintNicknameBean(int num, String nickName) {
            this.num = num;
            this.nickName = nickName;
        }

        public FingerprintNicknameBean() {
        }

        /**
         * num : 1
         * nickName : 密码1
         */


        private int num;
        private String nickName;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
    }

    public static class CardNicknameBean {
        public CardNicknameBean(int num, String nickName) {
            this.num = num;
            this.nickName = nickName;
        }

        public CardNicknameBean() {
        }

        /**
         * num : 1
         * nickName : 密码1
         */



        private int num;
        private String nickName;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
    }

    public static class FaceNicknameBean {
        public FaceNicknameBean(int num, String nickName) {
            this.num = num;
            this.nickName = nickName;
        }

        public FaceNicknameBean() {
        }

        /**
         * num : 1
         * nickName : 啊啊啊
         */


        private int num;
        private String nickName;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
    }

}
