package com.kaadas.lock.utils.greenDao.convert;

import com.google.gson.Gson;
import com.kaadas.lock.publiclibrary.bean.ClothesHangerMachineLightingBean;

import org.greenrobot.greendao.converter.PropertyConverter;


public class ClothesHangerMachineLightConvert implements PropertyConverter<ClothesHangerMachineLightingBean, String>{


    @Override
    public ClothesHangerMachineLightingBean convertToEntityProperty(String databaseValue) {
//        return JSON.parseObject(databaseValue,ClothesHangerMachineLightingBean.class);
        return new Gson().fromJson(databaseValue,ClothesHangerMachineLightingBean.class);
    }

    @Override
    public String convertToDatabaseValue(ClothesHangerMachineLightingBean entityProperty) {
//        return JSON.toJSONString(entityProperty);
        return new Gson().toJson(entityProperty);
    }

}