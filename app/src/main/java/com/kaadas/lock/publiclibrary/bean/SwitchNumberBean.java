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
        private int type;
        private int startTime;
        private int stopTime;
        private int week;
        private int timeEn;
        private String nickname;

        public SwitchNumberBean(int type, int startTime,int stopTime, int week, int timeEn, String nickname) {
            this.type = type;
            this.startTime = startTime;
            this.stopTime = stopTime;
            this.week = week;
            this.timeEn = timeEn;
            this.nickname = nickname;

        }

        public SwitchNumberBean() {
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getStartTime() {
            return startTime;
        }

        public void setStartTime(int startTime) {
            this.startTime = startTime;
        }


        public int getStopTime() {
            return stopTime;
        }

        public void setStopTime(int stopTime) {
            this.stopTime = stopTime;
        }

        public int getWeek() {
            return week;
        }

        public void setWeek(int week) {
            this.week = week;
        }

    public int getTimeEn() {
        return timeEn;
    }

    public void setTimeEn(int timeEn) {
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
