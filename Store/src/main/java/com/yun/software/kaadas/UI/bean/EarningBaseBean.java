package com.yun.software.kaadas.UI.bean;

import java.util.List;

public class EarningBaseBean {

    private String date ;

    private List<EarningDetailsBean> list;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<EarningDetailsBean> getList() {
        return list;
    }

    public void setList(List<EarningDetailsBean> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "EarningBaseBean{" +
                "date='" + date + '\'' +
                ", list=" + list +
                '}';
    }
}
