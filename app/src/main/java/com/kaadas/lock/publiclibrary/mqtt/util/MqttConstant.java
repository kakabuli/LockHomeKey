package com.kaadas.lock.publiclibrary.mqtt.util;
/**
* 常量
*@author FJH
*@company kaadas
*created at 2019/3/27 17:09
*/
public class MqttConstant {

    //public static String MQTT_BASE_URL = "tcp://121.201.57.214:1883";//测试服务器
    public static String MQTT_BASE_URL = "tcp://mqtt-kaadas.juziwulian.com:1883";//正式服务器

    public static String MQTT_REQUEST_APP="/request/app/func";

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

    //msgtype---request
    public static final String MSG_TYPE_REQUEST="request";




    public static String getSubscribeTopic(String userId){
        String topic="/" + userId + "/rpc/reply";
        return topic;
    }

    public static String getCallTopic(String userId){
        return "/" + userId + "/rpc/call";
    }


    //1 绑定网关
    public static final String BIND_GATEWAY = "bindGatewayByUser";

    //2 获取网关列表
    public static final String GET_BIND_GATEWAY_LIST = "gatewayBindList";

    //3获取网关状态
    public static final String GATEWAY_STATE = "gatewayState";

    //4网关开启允许设备入网模式
    public static final String SET_JOIN_ALLOW="setJoinAllow";



}
