package com.kaadas.lock.activity.choosewifi;

/**
 * Created by hushucong
 * on 2020/7/3
 */
public class WifiBean {
    public int ImageId; //图片
    public String name;  //第一行的数据

    public WifiBean(int imageId, String wifiName) {  //构造方法
        ImageId = imageId;
        name = wifiName;
    }
}