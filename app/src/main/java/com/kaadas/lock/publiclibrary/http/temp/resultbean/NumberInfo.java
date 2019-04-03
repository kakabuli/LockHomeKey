package com.kaadas.lock.publiclibrary.http.temp.resultbean;

import java.util.List;

/**
 * Create By lxj  on 2019/1/29
 * Describe
 */
public class NumberInfo {
    private List<InfoListBean> infoList;

    public List<InfoListBean> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<InfoListBean> infoList) {
        this.infoList = infoList;
    }



    public static class InfoListBean {
        /**
         * num : 08
         * numNickname : Hi
         * time : 2019-01-29 09:24:49
         */

        private String num;
        private String numNickname;
        private String time;

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getNumNickname() {
            return numNickname;
        }

        public void setNumNickname(String numNickname) {
            this.numNickname = numNickname;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "InfoListBean{" +
                    "num='" + num + '\'' +
                    ", numNickname='" + numNickname + '\'' +
                    ", time='" + time + '\'' +
                    '}';
        }
    }


    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (InfoListBean infoListBean :infoList){
            buffer.append(infoListBean.toString());
        }
        return "NumberInfo{" +
                "infoList=" + buffer.toString() +
                '}';
    }
}
