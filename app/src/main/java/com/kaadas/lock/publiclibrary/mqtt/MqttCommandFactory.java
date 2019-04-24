package com.kaadas.lock.publiclibrary.mqtt;

import com.google.gson.Gson;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.BindGatewayBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetBindGatewayListBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetWifiBasicBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetJoinAllowBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttCommandFactory {

    //MessageId
    public static int MESSAGE_ID = 0;


    public synchronized static int getMessageId(){
        return MESSAGE_ID++;
    }
    /**
     * 绑定网关
     *
     * @param uid
     * @param Sn  网关的SN
     * @return
     */
    public static MqttMessage bindGateway(String uid, String Sn) {
        int messageId = getMessageId();
        BindGatewayBean bindGatewayBean = new BindGatewayBean(uid, MqttConstant.BIND_GATEWAY, Sn);
        return getMessage(bindGatewayBean,messageId);
    }

    /**
     * 获取绑定网关的列表
     *
     * @param uid
     * @return
     */
    public static MqttMessage getGatewayList(String uid) {
        int messageId = getMessageId();
        GetBindGatewayListBean bindGatewayBean = new GetBindGatewayListBean(uid, MqttConstant.GET_BIND_GATEWAY_LIST);
        return getMessage(bindGatewayBean,messageId);
    }

    /**
     * 获取网关WIFI无线设置
     * @param userId
     * @param gwId
     * @param deviceId
     * @return
     */
    public static MqttMessage getWiFiBasic(String userId, String gwId, String deviceId ){
        int messageId = getMessageId();
        GetWifiBasicBean getWifiBasicBean = new GetWifiBasicBean("request",userId,messageId , gwId, deviceId, "getWiFiBasic",
                new GetWifiBasicBean.ParamsBean(),0,new  GetWifiBasicBean.ReturnDataBean(),"0");
        return getMessage(getWifiBasicBean,messageId);
    }

    public static MqttMessage setJoinAllow(String userId,String gwId,String deviceId){
        int messageId = getMessageId();
        SetJoinAllowBean.ParamsBean paramsBean=new SetJoinAllowBean.ParamsBean();
        paramsBean.setMode("zigbee");
        SetJoinAllowBean setJoinAllowBean=new SetJoinAllowBean(MqttConstant.MSG_TYPE_REQUEST,userId,messageId,gwId,deviceId,MqttConstant.SET_JOIN_ALLOW,paramsBean,"0",new SetJoinAllowBean.ReturnDataBean(),System.currentTimeMillis()+"");
        return  getMessage(setJoinAllowBean,messageId);

    }




    public static MqttMessage getMessage(Object o,int messageID) {
        String payload = new Gson().toJson(o);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(2);
        mqttMessage.setRetained(false);
        mqttMessage.setId(messageID);
        mqttMessage.setPayload(payload.getBytes());
        return mqttMessage;
    }
}
