package com.kaadas.lock.utils.greenDao.convert;

import com.google.gson.Gson;
import com.kaadas.lock.publiclibrary.bean.SingleFireSwitchInfo;


import org.greenrobot.greendao.converter.PropertyConverter;


/**
 * Created by hushucong
 * on 2020/6/11
 */
public class SingleFireSwitchInfoConvert implements PropertyConverter<SingleFireSwitchInfo, String>{


    @Override
    public SingleFireSwitchInfo convertToEntityProperty(String databaseValue) {
//         JSON.parseObject(databaseValue,SingleFireSwitchInfo.class);
        return new Gson().fromJson(databaseValue,SingleFireSwitchInfo.class);
    }

    @Override
    public String convertToDatabaseValue(SingleFireSwitchInfo entityProperty) {
//        return JSON.toJSONString();
        return new Gson().toJson(entityProperty);
    }

}