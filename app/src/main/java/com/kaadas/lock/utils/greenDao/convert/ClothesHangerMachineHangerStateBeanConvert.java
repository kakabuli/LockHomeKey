package com.kaadas.lock.utils.greenDao.convert;

import com.google.gson.Gson;
import com.kaadas.lock.publiclibrary.bean.ClothesHangerMachineHangerStateBean;

import org.greenrobot.greendao.converter.PropertyConverter;


public class ClothesHangerMachineHangerStateBeanConvert implements PropertyConverter<ClothesHangerMachineHangerStateBean, String>{


    @Override
    public ClothesHangerMachineHangerStateBean convertToEntityProperty(String databaseValue) {
//        return JSON.parseObject(databaseValue,ClothesHangerMachineHangerStateBean.class);
        return new Gson().fromJson(databaseValue,ClothesHangerMachineHangerStateBean.class);
    }

    @Override
    public String convertToDatabaseValue(ClothesHangerMachineHangerStateBean entityProperty) {
//        return JSON.toJSONString(entityProperty);
        return new Gson().toJson(entityProperty);
    }

}