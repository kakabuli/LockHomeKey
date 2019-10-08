package com.yun.software.kaadas.UI.bean;

import java.util.List;

public class MyCollectionBaseBean {

    private String date ;

    private List<MyCollection> list;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<MyCollection> getList() {
        return list;
    }

    public void setList(List<MyCollection> list) {
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
