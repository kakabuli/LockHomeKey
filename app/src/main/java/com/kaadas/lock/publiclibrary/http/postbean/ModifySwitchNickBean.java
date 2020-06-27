package com.kaadas.lock.publiclibrary.http.postbean;

import java.io.Serializable;
import java.util.List;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class ModifySwitchNickBean {


    /**
     * "wifiSN": "WF01201010001",
     * "uid": "5c4fe492dc93897aa7d8600b",
     * "switchNickname": [{
     *         "type": 1,
     *         "nickname": "第一个开关昵称"
     *     },
     *     {
     *         "type": 2,
     *        "nickname": "第二个开关昵称"
     *    }
     * ]
     *
     */

    private String wifiSN;
    private String uid;
    private List<nickname> switchNickname;

    public ModifySwitchNickBean(String wifiSN, String uid, List<nickname> switchNickname) {
        this.wifiSN = wifiSN;
        this.uid = uid;
        this.switchNickname = switchNickname;
    }

    public String getWifiSN() {
        return wifiSN;
    }

    public void setWifiSN(String wifiSN) {
        this.wifiSN = wifiSN;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<nickname> getSwitchNickname() {
        return switchNickname;
    }

    public void setSwitchNickname(List<nickname> switchNickname) {
        this.switchNickname = switchNickname;
    }
    public static class nickname  implements Serializable {
        /**
         * "type": 1,
         * "nickname": "第一个开关昵称"
         */

        private int type;
        private String nickname;

        public nickname(String nickName, int type) {

            this.nickname = nickName;
            this.type = type;

        }

        public nickname() {
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }


    }
}
