package com.kaadas.lock.utils;

import com.kaadas.lock.BuildConfig;

public class ConstantConfig {

    public static final String HTTP_BASE_URL = BuildConfig.HTTP_HOST;//凯迪仕正式服务器
    public static final String OTA_INFO_URL = BuildConfig.HTTP_HOST;//凯迪仕正式服务器
    public static final String OTA_RESULT_UPLOAD_URL = OTA_INFO_URL + "ota/btResultAdd";  //正式OTA上报服务器
    public static final String MQTT_BASE_URL = BuildConfig.MQTT_HOST;//正式服务器
    public static final String LINPHONE_URL = BuildConfig.SIP_HOST;//正式sip

}
