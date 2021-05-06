package com.kaadas.lock.utils.greenDao.convert;

import com.google.gson.Gson;
import com.kaadas.lock.publiclibrary.bean.WifiVideoLockAliveTimeBean;

import org.greenrobot.greendao.converter.PropertyConverter;


public class WifiVideoAliveTimeBeanConvert implements PropertyConverter<WifiVideoLockAliveTimeBean, String>{


    @Override
    public WifiVideoLockAliveTimeBean convertToEntityProperty(String databaseValue) {
//        return JSON.parseObject(databaseValue,WifiVideoLockAliveTimeBean.class);
        return new Gson().fromJson(databaseValue,WifiVideoLockAliveTimeBean.class);
    }

    @Override
    public String convertToDatabaseValue(WifiVideoLockAliveTimeBean entityProperty) {
//        return JSON.toJSONString(entityProperty);
        return new Gson().toJson(entityProperty);
    }

}