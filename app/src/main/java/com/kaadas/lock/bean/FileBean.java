package com.kaadas.lock.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileBean implements Serializable {

    private String date;

    private List<FileItemBean> item = new ArrayList<>();

    public FileBean(){

    }

    public FileBean(String date,List<FileItemBean> item){
          this.date = date;
          if(this.item != null && item.size() > 0){
              this.item.clear();
              this.item.addAll(item);
          }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<FileItemBean> getItem() {
        return item;
    }

    public void setItem(List<FileItemBean> item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "FileBean{" +
                "date='" + date + '\'' +
                ", item=" + item +
                '}';
    }
}
