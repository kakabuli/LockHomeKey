package com.kaadas.lock.publiclibrary.http.result;

import com.kaadas.lock.publiclibrary.bean.ForeverPassword;

import java.io.Serializable;
import java.util.List;


public class GetPasswordResult  extends BaseResult  {


    /**
     * code : 200
     * msg : 成功
     * data : {"_id":"5c76326914fd2110c84df474","pwdList":[{"num":"12","nickName":"mima12","createTime":1551785021,"type":1,"startTime":1551774543,"endTime":1551774543,"items":["1","3","6"]}],"tempPwdList":[{"num":"01","nickName":"指纹密码1","createTime":1551785021}],"fingerprintList":[{"num":"01","nickName":"指纹密码1","createTime":1551785021}],"cardList":[{"num":"01","nickName":"卡片密码1","createTime":1551785021}]}
     */

    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        @Override
        public String toString() {
            return "WifiLockShareUser{" +
                    "_id='" + _id + '\'' +
                    ", pwdList=" + pwdList +
                    ", tempPwdList=" + tempPwdList +
                    ", fingerprintList=" + fingerprintList +
                    ", cardList=" + cardList +
                    '}';
        }

        /**
         * _id : 5c76326914fd2110c84df474
         * pwdList : [{"num":"12","nickName":"mima12","createTime":1551785021,"type":1,"startTime":1551774543,"endTime":1551774543,"items":["1","3","6"]}]
         * tempPwdList : [{"num":"01","nickName":"指纹密码1","createTime":1551785021}]
         * fingerprintList : [{"num":"01","nickName":"指纹密码1","createTime":1551785021}]
         * cardList : [{"num":"01","nickName":"卡片密码1","createTime":1551785021}]
         */

        private String _id;
        private List<ForeverPassword> pwdList;
        private List<TempPassword> tempPwdList;
        private List<Fingerprint> fingerprintList;
        private List<Card> cardList;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public List<ForeverPassword> getPwdList() {
            return pwdList;
        }

        public void setPwdList(List<ForeverPassword> pwdList) {
            this.pwdList = pwdList;
        }

        public List<TempPassword> getTempPwdList() {
            return tempPwdList;
        }

        public void setTempPwdList(List<TempPassword> tempPwdList) {
            this.tempPwdList = tempPwdList;
        }

        public List<Fingerprint> getFingerprintList() {
            return fingerprintList;
        }

        public void setFingerprintList(List<Fingerprint> fingerprintList) {
            this.fingerprintList = fingerprintList;
        }

        public List<Card> getCardList() {
            return cardList;
        }

        public void setCardList(List<Card> cardList) {
            this.cardList = cardList;
        }

        public static class PwdListBean {
            /**
             * num : 12
             * nickName : mima12
             * createTime : 1551785021
             * type : 1
             * startTime : 1551774543
             * endTime : 1551774543
             * items : ["1","3","6"]
             */

            private String num;
            private String nickName;
            private long createTime;
            private int type;
            private long startTime;
            private long endTime;
            private List<String> items;

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

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
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

        public static class TempPassword implements Serializable,Comparable<TempPassword>  {
            @Override
            public String toString() {
                return "TempPassword{" +
                        "num='" + num + '\'' +
                        ", nickName='" + nickName + '\'' +
                        ", createTime=" + createTime +
                        '}';
            }

            @Override
            public int compareTo(TempPassword o) {
                int iNum = Integer.parseInt(num );
                int iNum2 = Integer.parseInt(o.getNum());
                if (iNum<iNum2){
                    return -1;
                }
                return 1;
            }
            /**
             * num : 01
             * nickName : 指纹密码1
             * createTime : 1551785021
             */

            private String num;
            private String nickName;
            private long createTime;

            public TempPassword() {
            }

            public TempPassword(String num, String nickName, long createTime) {
                this.num = num;
                this.nickName = nickName;
                this.createTime = createTime;
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

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }
        }

        public static class Fingerprint implements Serializable,Comparable<Fingerprint> {

            @Override
            public int compareTo(Fingerprint o) {
                int iNum = Integer.parseInt(num );
                int iNum2 = Integer.parseInt(o.getNum());
                if (iNum<iNum2){
                    return -1;
                }
                return 1;
            }
            /**
             * num : 01
             * nickName : 指纹密码1
             * createTime : 1551785021
             */

            private String num;
            private String nickName;
            private long createTime;

            public Fingerprint(String num, String nickName, long createTime) {
                this.num = num;
                this.nickName = nickName;
                this.createTime = createTime;
            }

            public Fingerprint() {
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

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }
        }

        public static class Card implements Serializable,Comparable<Card>{


            @Override
            public int compareTo(Card o) {
                int iNum = Integer.parseInt(num );
                int iNum2 = Integer.parseInt(o.getNum());
                if (iNum<iNum2){
                    return -1;
                }
                return 1;
            }
            /**
             * num : 01
             * nickName : 卡片密码1
             * createTime : 1551785021
             */

            private String num;
            private String nickName;
            private long createTime;

            public Card() {
            }

            public Card(String num, String nickName, long createTime) {
                this.num = num;
                this.nickName = nickName;
                this.createTime = createTime;
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

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }
        }
    }
}
