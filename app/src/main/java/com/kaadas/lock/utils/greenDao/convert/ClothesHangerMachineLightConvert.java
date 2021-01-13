package com.kaadas.lock.utils.greenDao.convert;

import com.alibaba.fastjson.JSON;
import com.kaadas.lock.publiclibrary.bean.ClothesHangerMachineLightingBean;
import com.kaadas.lock.publiclibrary.bean.ClothesHangerMachineMotorBean;

import org.greenrobot.greendao.converter.PropertyConverter;


public class ClothesHangerMachineLightConvert implements PropertyConverter<ClothesHangerMachineLightingBean, String>{


    @Override
    public ClothesHangerMachineLightingBean convertToEntityProperty(String databaseValue) {
        return JSON.parseObject(databaseValue,ClothesHangerMachineLightingBean.class);

    }

    @Override
    public String convertToDatabaseValue(ClothesHangerMachineLightingBean entityProperty) {
        return JSON.toJSONString(entityProperty);
    }

}