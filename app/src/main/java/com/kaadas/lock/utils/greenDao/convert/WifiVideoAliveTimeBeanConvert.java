package com.kaadas.lock.utils.greenDao.convert;

import com.alibaba.fastjson.JSON;
import com.kaadas.lock.publiclibrary.bean.WifiVideoLockAliveTimeBean;
import com.kaadas.lock.publiclibrary.bean.WifiVideoLockSetPirBean;

import org.greenrobot.greendao.converter.PropertyConverter;


public class WifiVideoAliveTimeBeanConvert implements PropertyConverter<WifiVideoLockAliveTimeBean, String>{


    @Override
    public WifiVideoLockAliveTimeBean convertToEntityProperty(String databaseValue) {
        return JSON.parseObject(databaseValue,WifiVideoLockAliveTimeBean.class);

    }

    @Override
    public String convertToDatabaseValue(WifiVideoLockAliveTimeBean entityProperty) {
        return JSON.toJSONString(entityProperty);
    }

}