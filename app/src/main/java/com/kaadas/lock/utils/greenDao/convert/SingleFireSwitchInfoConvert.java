package com.kaadas.lock.utils.greenDao.convert;

import com.kaadas.lock.publiclibrary.bean.SingleFireSwitchInfo;

import com.alibaba.fastjson.JSON;

import org.greenrobot.greendao.converter.PropertyConverter;


/**
 * Created by hushucong
 * on 2020/6/11
 */
public class SingleFireSwitchInfoConvert implements PropertyConverter<SingleFireSwitchInfo, String>{


    @Override
    public SingleFireSwitchInfo convertToEntityProperty(String databaseValue) {
        return JSON.parseObject(databaseValue,SingleFireSwitchInfo.class);

    }

    @Override
    public String convertToDatabaseValue(SingleFireSwitchInfo entityProperty) {
        return JSON.toJSONString(entityProperty);
    }

}