package com.kaadas.lock.utils.greenDao.convert;

import com.alibaba.fastjson.JSON;
import com.kaadas.lock.publiclibrary.bean.ClothesHangerMachineHangerStateBean;

import org.greenrobot.greendao.converter.PropertyConverter;


public class ClothesHangerMachineHangerStateBeanConvert implements PropertyConverter<ClothesHangerMachineHangerStateBean, String>{


    @Override
    public ClothesHangerMachineHangerStateBean convertToEntityProperty(String databaseValue) {
        return JSON.parseObject(databaseValue,ClothesHangerMachineHangerStateBean.class);

    }

    @Override
    public String convertToDatabaseValue(ClothesHangerMachineHangerStateBean entityProperty) {
        return JSON.toJSONString(entityProperty);
    }

}