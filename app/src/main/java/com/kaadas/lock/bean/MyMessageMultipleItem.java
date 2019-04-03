package com.kaadas.lock.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by David on 2019/4/2
 */
public class MyMessageMultipleItem implements MultiItemEntity {
    //1.系统消息
    //2.共享设备授权信息
    //3.网关授权信息
    public static final int SYSTEM_MESSAGE = 1;
    public static final int SHARE_DEVICE_AUTHORIZATION_MESSAGE = 2;
    public static final int GATEWAY_AUTHORIZATION_MESSAGE = 3;
    private int itemType;

    public MyMessageMultipleItem(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
