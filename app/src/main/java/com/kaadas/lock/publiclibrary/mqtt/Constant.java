package com.kaadas.lock.publiclibrary.mqtt;
/**
* 常量
*@author FJH
*@company kaadas
*created at 2019/3/27 17:09
*/
public class Constant {

    //断开后，是否自动连接
    public static final boolean MQTT_AUTOMATIC_RECONNECT=true;

    //是否清空客户端的连接记录。若为true，则断开后，broker将自动清除该客户端连接信息
    public static final boolean MQTT_CLEANSE_SSION=false;

    //设置超时时间，单位为秒
    public static final int MQTT_CONNECTION_TIMEOUT=10;

    //设置心跳时间，单位为秒
    public static final int MQTT_KEEP_ALIVE_INTERVAL=20;

    //允许同时发送几条消息（未收到broker确认信息）
    public static final int MQTT_MAX_INFLIGHT=10;

    public static String getSubscribeTopic(String userId){
        String topic="/" + userId + "/rpc/reply";
        return topic;
    }
}
