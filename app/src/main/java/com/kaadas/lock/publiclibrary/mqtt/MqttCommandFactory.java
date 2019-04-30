package com.kaadas.lock.publiclibrary.mqtt;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.AllowCateyeJoinBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.BindGatewayBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetAllBindDeviceBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetBindGatewayListBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetDevicePowerBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetLockLang;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetWifiBasicBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdFuncBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdInfoBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.OpenLockBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetJoinAllowBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.UpdateDevNickNameBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttCommandFactory {

    //MessageId
    public static int MESSAGE_ID = 0;


    public synchronized static int getMessageId() {
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
        return getMessage(bindGatewayBean, messageId);
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
        return getMessage(bindGatewayBean, messageId);
    }

    /**
     * 获取网关WIFI无线设置
     *
     * @param userId
     * @param gwId
     * @param deviceId
     * @return
     */
    public static MqttMessage getWiFiBasic(String userId, String gwId, String deviceId) {
        int messageId = getMessageId();
        GetWifiBasicBean getWifiBasicBean = new GetWifiBasicBean("request", userId, messageId, gwId, deviceId,
                MqttConstant.GET_WIFI_BASIC, new GetWifiBasicBean.ParamsBean(), 0, new GetWifiBasicBean.ReturnDataBean(), "0");
        return getMessage(getWifiBasicBean, messageId);
    }

    /**
     * @param userId
     * @param gwId
     * @return
     */
    public static MqttMessage allowCateyeJoin(String userId, String gwId, String SN, String mac,int timeout) {
        int messageId = getMessageId();
        AllowCateyeJoinBean getWifiBasicBean = new AllowCateyeJoinBean("request", userId, messageId,
                gwId, MqttConstant.ALLOW_GATEWAY_JOIN, SN, mac, timeout, 0, "0");
        return getMessage(getWifiBasicBean, messageId);
    }

    /**
     * 设置让猫眼入网
     * @param userId
     * @param gwId
     * @param deviceId
     * @return
     */

    public static MqttMessage setJoinAllow(String userId,String gwId,String deviceId){
        int messageId = getMessageId();
        SetJoinAllowBean.ParamsBean paramsBean=new SetJoinAllowBean.ParamsBean();
        paramsBean.setMode("zigbee");
        SetJoinAllowBean setJoinAllowBean=new SetJoinAllowBean(MqttConstant.MSG_TYPE_REQUEST,userId,messageId,gwId,deviceId,MqttConstant.SET_JOIN_ALLOW,paramsBean,"0",new SetJoinAllowBean.ReturnDataBean(),System.currentTimeMillis()+"");
        return  getMessage(setJoinAllowBean,messageId);

    }

    /**
     * 修改设备昵称
     * @param uid
     * @param devuuid
     * @param deviceId
     * @param nickName
     * @return
     */
    public static MqttMessage updateDeviceNickName(String uid,String devuuid,String deviceId,String nickName ){
        int messageId=getMessageId();
        UpdateDevNickNameBean updateDevNickNameBean=new UpdateDevNickNameBean(MqttConstant.UPDATE_DEV_NICK_NAME,uid,devuuid,deviceId,nickName);
        return getMessage(updateDevNickNameBean,messageId);
    }



    /**
     * 获取所有的设备信息
     * @return
     */
    public static MqttMessage getAllBindDevice(String uid){
        int messageId = getMessageId();
        GetAllBindDeviceBean getAllBindDeviceBean = new GetAllBindDeviceBean(messageId,"request",MqttConstant.GET_ALL_BIND_DEVICE,uid);
        return  getMessage(getAllBindDeviceBean,messageId);

    }

    /**
     * 获取设备电量
     * @param gatewayId
     * @param deviceId
     * @return
     */

    public static MqttMessage getDevicePower(String gatewayId,String deviceId){
        int messageId = getMessageId();
        GetDevicePowerBean getDevicePowerBean=new GetDevicePowerBean(MqttConstant.MSG_TYPE_REQUEST, MyApplication.getInstance().getUid(),messageId,gatewayId,deviceId,MqttConstant.GET_POWER,new GetDevicePowerBean.ParamsBean(),"0",new GetDevicePowerBean.ReturnDataBean(),System.currentTimeMillis()+"");
        return  getMessage(getDevicePowerBean,messageId);
    }

    /**
     * 开锁
     * @param gatewayId
     * @param deviceId
     * @param opType
     * @param type
     * @param pwd
     * @return
     */

    public static MqttMessage openLock(String gatewayId,String deviceId,String opType,String type,String pwd){
        String uid=MyApplication.getInstance().getUid();
        int messageId = getMessageId();
        OpenLockBean.ParamsBean paramsBean=new OpenLockBean.ParamsBean();
        paramsBean.setOptype(opType);
        paramsBean.setUserid(uid);
        paramsBean.setType(type);
        paramsBean.setPin(pwd);
        OpenLockBean openLockBean=new OpenLockBean(MqttConstant.MSG_TYPE_REQUEST,uid,messageId,gatewayId,deviceId,MqttConstant.OPEN_LOCK,paramsBean,"0",new OpenLockBean.ReturnDataBean(),System.currentTimeMillis()+"");
        return getMessage(openLockBean,messageId);
    }

    /**
     * 密码的设置，查询，清除
      * @param gatewayId
     * @param deviceId
     * @param action
     * @param type
     * @param pwdId
     * @param pwdValue
     * @return
     */

  public static MqttMessage lockPwdFunc(String gatewayId,String deviceId,String action,String type,String pwdId,String pwdValue){
        int messageId=getMessageId();
        LockPwdFuncBean.ParamsBean paramsBean=new LockPwdFuncBean.ParamsBean();
        paramsBean.setAction(action);
        paramsBean.setType(type);
        paramsBean.setPwdid(pwdId);
        paramsBean.setPwdvalue(pwdValue);
        LockPwdFuncBean lockPwdFuncBean=new LockPwdFuncBean(MqttConstant.MSG_TYPE_REQUEST,MyApplication.getInstance().getUid(),messageId,gatewayId,deviceId,MqttConstant.SET_PWD,paramsBean,"0",new LockPwdFuncBean.ReturnDataBean(),System.currentTimeMillis()+"");
        return getMessage(lockPwdFuncBean,messageId);
    }

    /**
     * 获取锁密码和RFID基本信息
     * @param gatewayId
     * @param deviceId
     * @return
     */

    public static MqttMessage getLockPwdInfo(String gatewayId,String deviceId){
      int messageId=getMessageId();
      LockPwdInfoBean lockPwdInfoBean=new LockPwdInfoBean(MqttConstant.MSG_TYPE_REQUEST,MyApplication.getInstance().getUid(),messageId,gatewayId,deviceId,MqttConstant.LOCK_PWD_INFO,new LockPwdInfoBean.ParamsBean(),"0",new LockPwdInfoBean.ReturnDataBean(),System.currentTimeMillis()+"");
      return getMessage(lockPwdInfoBean,messageId);
    }

    /**
     * 获取语言
     * @param gatewayId
     * @param deviceId
     * @return
     */
    public static MqttMessage getLockLang(String gatewayId,String deviceId){
        int messageId=getMessageId();
        GetLockLang lockLang=new GetLockLang(MqttConstant.MSG_TYPE_REQUEST,MyApplication.getInstance().getUid(),messageId,gatewayId,deviceId,MqttConstant.GET_LANG,new GetLockLang.ParamsBean(),"0",new GetLockLang.ReturnDataBean(),System.currentTimeMillis()+"");
        return getMessage(lockLang,messageId);
    }






    public static MqttMessage getMessage(Object o, int messageID) {
        String payload = new Gson().toJson(o);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(2);
        mqttMessage.setRetained(false);
        mqttMessage.setId(messageID);
        mqttMessage.setPayload(payload.getBytes());
        return mqttMessage;
    }
}
