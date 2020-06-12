package com.kaadas.lock.publiclibrary.bean;

import java.io.Serializable;

/**
 * Created by hushucong
 * on 2020/6/11
 */
public class SwitchNumberBean implements Serializable {

        /**
         * "type": 1, 			    //键号
         * "startTime": 1440, 		//开始时间0-1440分
         * "stopTime": 1440, 		//结束时间0-1440分
         * "week": 0xFF 			//周策略 0B 0111 1111 如：bit0->周日 bit1->周一 bit6->周六 无策略时全为0 （就是策励开关全为0）
         */
        private String type;
        private String startTime;
        private String stopTime;
        private String week;
        private String timeEn;
        private String nickname;

        public SwitchNumberBean(String type, String startTime,String stopTime, String week, String timeEn, String nickname) {
            this.type = type;
            this.startTime = startTime;
            this.stopTime = stopTime;
            this.week = week;
            this.timeEn = timeEn;
            this.nickname = nickname;

        }

        public SwitchNumberBean() {
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }


        public String getStopTime() {
            return stopTime;
        }

        public void setStopTime(String stopTime) {
            this.stopTime = stopTime;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

    public String getTimeEn() {
        return timeEn;
    }

    public void setTimeEn(String timeEn) {
        this.timeEn = timeEn;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "{" +
                "type='" + type + '\'' +
                ", startTime=" + startTime +
                ", stopTime=" + stopTime +
                ", week=" + week +
                ", timeEn=" + timeEn +
                ", nickname=" + nickname +
                '}';
    }
}
