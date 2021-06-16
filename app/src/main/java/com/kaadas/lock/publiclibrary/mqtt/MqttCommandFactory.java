package com.kaadas.lock.publiclibrary.mqtt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.publiclibrary.bean.SingleFireSwitchInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.AddSingleFireSwitchBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.AllowCateyeJoinBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.BindGatewayBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.BindGatewayMemeBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.CatEyeInfoBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.CatEyeInfoBeanProperty;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.CateEyeInfoBeanPropertyUpdate;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.ClothesHangerMachineBean.GetHangerAllStatus;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.ClothesHangerMachineBean.SetHangerLightingBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.ClothesHangerMachineBean.SetHangerMotorBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.DeleteGatewayLockDeviceBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.DeviceShareBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.DeviceShareUserBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.FtpEnableBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GatewayComfirmOtaUpgradeBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetAMBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetAlarmListBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetAllBindDeviceBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetArmLockedBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetBindGatewayListBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetDevicePowerBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetGatewayLockInfoBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetLockLang;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetLockRecordTotal;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetNetBasicBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetPirSlientBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetSoundVolume;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetWifiBasicBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetZbChannelBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdFuncBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdInfoBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.OpenLockBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SelectOpenLockRecordBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetAMBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetArmLockedBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetCatEyeBellCountBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetCatEyeBellVolume;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetJoinAllowBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetLockLang;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetNetBasicBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetPasswordPlanBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetPirEnableBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetPirSlientBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetPirWanderBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetSingleFireSwitchBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetUserTypeBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetVedioResBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetVideoLockAmMode;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetVideoLockLang;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetVideoLockSafeMode;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetVideoLockVolume;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetWiFiBasic;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetZBChannel;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SettingAMSBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SettingFaceWanderingAlarm;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SettingLockingMethod;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SettingOpenDirection;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SettingOpenForce;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SettingScreenBrightness;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SettingScreenTime;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SettingVideoLockAliveTime;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SettingVideoLockPir;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SettingVoiceQuality;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.UnBindGatewayBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.UpdateDevNickNameBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.UpdateDevPushSwitchBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.UpdateGatewayNickNameBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.WakeupCameraBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GatewayOtaNotifyBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.utils.LogUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;
import java.util.List;

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
        BindGatewayBean bindGatewayBean = new BindGatewayBean(messageId, uid, MqttConstant.BIND_GATEWAY, Sn);
        return getMessage(bindGatewayBean, messageId);
    }

    /**
     * 绑定咪咪网
     *
     * @param uid
     * @param Sn
     * @return
     */
    public static MqttMessage registerMemeAndBind(String uid, String gatewayId, String Sn) {
        int messageId = getMessageId();
        BindGatewayMemeBean bindGatewayMemeBean = new BindGatewayMemeBean(messageId, MqttConstant.REGISTER_MIMI_BIND, uid, gatewayId, Sn);
        return getMessage(bindGatewayMemeBean, messageId);
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
    public static MqttMessage allowCateyeJoin(String userId, String gwId, String SN, String mac, int timeout) {
        int messageId = getMessageId();
        AllowCateyeJoinBean getWifiBasicBean = new AllowCateyeJoinBean("request", userId, messageId,
                gwId, MqttConstant.ALLOW_GATEWAY_JOIN, SN, mac, timeout, 0, "0");
        return getMessage(getWifiBasicBean, messageId);
    }

    /**
     * 设置让猫眼入网
     *
     * @param userId
     * @param gwId
     * @param deviceId
     * @return
     */

    public static MqttMessage setJoinAllow(String userId, String gwId, String deviceId) {
        int messageId = getMessageId();
        SetJoinAllowBean.ParamsBean paramsBean = new SetJoinAllowBean.ParamsBean();
        paramsBean.setMode("zigbee");
        SetJoinAllowBean setJoinAllowBean = new SetJoinAllowBean(MqttConstant.MSG_TYPE_REQUEST, userId, messageId, gwId, deviceId, MqttConstant.SET_JOIN_ALLOW, paramsBean, "0", new SetJoinAllowBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(setJoinAllowBean, messageId);

    }

    /**
     * 修改网关下设备昵称
     *
     * @param uid
     * @param devuuid
     * @param deviceId
     * @param nickName
     * @return
     */
    public static MqttMessage updateDeviceNickName(String uid, String devuuid, String deviceId, String nickName) {
        int messageId = getMessageId();
        UpdateDevNickNameBean updateDevNickNameBean = new UpdateDevNickNameBean(MqttConstant.UPDATE_DEV_NICK_NAME, uid, devuuid, deviceId, nickName);
        return getMessage(updateDevNickNameBean, messageId);
    }

    /**
     * 修改网关昵称
     *
     * @param uid
     * @param gatewayId
     * @param nickName
     * @return
     */
    public static MqttMessage updateGatewayNickName(String uid, String gatewayId, String nickName) {
        int messageId = getMessageId();
        UpdateGatewayNickNameBean updateGatewayNickNameBean = new UpdateGatewayNickNameBean(messageId, MqttConstant.UPDATE_GATEWAY_NICK_NAME, uid, gatewayId, nickName);
        return getMessage(updateGatewayNickNameBean, messageId);
    }


    /**
     * 获取所有的设备信息
     *
     * @return
     */
    public static MqttMessage getAllBindDevice(String uid) {
        int messageId = getMessageId();
        GetAllBindDeviceBean getAllBindDeviceBean = new GetAllBindDeviceBean(messageId, "request", MqttConstant.GET_ALL_BIND_DEVICE, uid,2);
        return getMessage(getAllBindDeviceBean, messageId);

    }

    /**
     * 获取设备电量
     *
     * @param gatewayId
     * @param deviceId
     * @return
     */

    public static MqttMessage getDevicePower(String gatewayId, String deviceId, String uid) {
        int messageId = getMessageId();
        GetDevicePowerBean getDevicePowerBean = new GetDevicePowerBean(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.GET_POWER, new GetDevicePowerBean.ParamsBean(), "0", new GetDevicePowerBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(getDevicePowerBean, messageId);
    }

    /**
     * 开锁
     *
     * @param gatewayId
     * @param deviceId
     * @param opType
     * @param type
     * @param pwd
     * @return
     */

    public static MqttMessage openLock(String gatewayId, String deviceId, String opType, String type, String pwd) {
        String uid = MyApplication.getInstance().getUid();
        int messageId = getMessageId();
        OpenLockBean.ParamsBean paramsBean = new OpenLockBean.ParamsBean();
        paramsBean.setOptype(opType);
        paramsBean.setUserid(uid);
        paramsBean.setType(type);
        paramsBean.setPin(pwd);
        OpenLockBean openLockBean = new OpenLockBean(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.OPEN_LOCK, paramsBean, "0", new OpenLockBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(openLockBean, messageId);
    }

    /**
     * 密码的设置，查询，清除
     *
     * @param gatewayId
     * @param deviceId
     * @param action
     * @param type
     * @param pwdId
     * @param pwdValue
     * @return
     */

    public static MqttMessage lockPwdFunc(String gatewayId, String deviceId, String action, String type, String pwdId, String pwdValue) {
        int messageId = getMessageId();
        LockPwdFuncBean.ParamsBean paramsBean = new LockPwdFuncBean.ParamsBean();
        paramsBean.setAction(action);
        paramsBean.setType(type);
        paramsBean.setPwdid(pwdId);
        paramsBean.setPwdvalue(pwdValue);
        LockPwdFuncBean lockPwdFuncBean = new LockPwdFuncBean(MqttConstant.MSG_TYPE_REQUEST, MyApplication.getInstance().getUid(),
                messageId, gatewayId, deviceId, MqttConstant.SET_PWD, paramsBean, "0", new LockPwdFuncBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(lockPwdFuncBean, messageId);
    }

    /**
     * 获取锁密码和RFID基本信息
     *
     * @param gatewayId
     * @param deviceId
     * @return
     */

    public static MqttMessage getLockPwdInfo(String gatewayId, String deviceId) {
        int messageId = getMessageId();
        LockPwdInfoBean lockPwdInfoBean = new LockPwdInfoBean(MqttConstant.MSG_TYPE_REQUEST,
                MyApplication.getInstance().getUid(), messageId, gatewayId, deviceId,
                MqttConstant.LOCK_PWD_INFO, new LockPwdInfoBean.ParamsBean(), "0",
                new LockPwdInfoBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(lockPwdInfoBean, messageId);
    }

    /**
     * 获取语言
     *
     * @param gatewayId
     * @param deviceId
     * @return
     */
    public static MqttMessage getLockLang(String gatewayId, String deviceId) {
        int messageId = getMessageId();
        GetLockLang lockLang = new GetLockLang(MqttConstant.MSG_TYPE_REQUEST, MyApplication.getInstance().getUid(), messageId, gatewayId, deviceId, MqttConstant.GET_LANG, new GetLockLang.ParamsBean(), "0", new GetLockLang.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(lockLang, messageId);
    }

    /**
     * 设置锁的语言
     *
     * @param gatewayId
     * @param deviceId
     * @return
     */
    public static MqttMessage setLockLang(String gatewayId, String deviceId, String lang) {
        int messageId = getMessageId();
        SetLockLang.ParamsBean paramsBean = new SetLockLang.ParamsBean();
        paramsBean.setLanguage(lang);
        SetLockLang lockLang = new SetLockLang(MqttConstant.MSG_TYPE_REQUEST, MyApplication.getInstance().getUid(), messageId, gatewayId, deviceId, MqttConstant.SET_LANG, paramsBean, "0", new SetLockLang.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(lockLang, messageId);
    }

    /**
     * 获取锁的音量
     *
     * @param gatewayId
     * @param deviceId
     * @return
     */
    public static MqttMessage getSoundVolume(String gatewayId, String deviceId) {
        int messageId = getMessageId();
        GetSoundVolume lockLang = new GetSoundVolume(MqttConstant.MSG_TYPE_REQUEST, MyApplication.getInstance().getUid(), messageId, gatewayId, deviceId, MqttConstant.SOUND_VOLUME, new GetSoundVolume.ParamsBean(), "0", new GetSoundVolume.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(lockLang, messageId);

    }

    /**
     * 设置锁的音量
     *
     * @param gatewayId
     * @param deviceId
     * @return
     */
    public static MqttMessage setSoundVolume(String gatewayId, String deviceId, int volume) {
        int messageId = getMessageId();
        GetSoundVolume.ParamsBean paramsBean = new GetSoundVolume.ParamsBean();
        paramsBean.setVolume(volume);
        GetSoundVolume lockLang = new GetSoundVolume(MqttConstant.MSG_TYPE_REQUEST, MyApplication.getInstance().getUid(), messageId, gatewayId, deviceId, MqttConstant.SET_SOUND_VOLUME, paramsBean, "0", new GetSoundVolume.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(lockLang, messageId);

    }


    /**
     * 删除设备
     *
     * @param gatewayId
     * @param deviceId
     * @param bustType
     * @return
     */

    public static MqttMessage deleteDevice(String gatewayId, String deviceId, String bustType) {
        int messageId = getMessageId();
        DeleteGatewayLockDeviceBean.ParamsBean paramsBean = new DeleteGatewayLockDeviceBean.ParamsBean();
        paramsBean.setBustype(bustType);
        paramsBean.setDeviceId(deviceId);
        DeleteGatewayLockDeviceBean gatewayLockDeviceBean = new DeleteGatewayLockDeviceBean(MqttConstant.MSG_TYPE_REQUEST, MyApplication.getInstance().getUid(), messageId, gatewayId, gatewayId, MqttConstant.DELETE_GATEWAY_LOCK, paramsBean, "0", new DeleteGatewayLockDeviceBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(gatewayLockDeviceBean, messageId);
    }

    /**
     * 唤醒FTP
     *
     * @param gatewayId
     * @param deviceId
     * @returne
     */
    public static MqttMessage setEnableFTP(String gatewayId, String deviceId) {
        int messageId = getMessageId();

        //(String msgtype, String userId, int msgId, String gwId, String deviceId, String func, ParamsBean params, String returnCode, ReturnDataBean returnData, String timestamp)
        FtpEnableBean ftpEnableBean = new FtpEnableBean(MqttConstant.MSG_TYPE_REQUEST, MyApplication.getInstance().getUid(), messageId, gatewayId, deviceId, MqttConstant.SET_FTP_ENABLE, new FtpEnableBean.ParamsBean(), "0", new FtpEnableBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(ftpEnableBean, messageId);
    }


    /**
     * 获取网关锁信息
     *
     * @param gatewayId
     * @param deviceId
     * @return
     */
    public static MqttMessage getGatewayLockInformation(String gatewayId, String deviceId) {
        int messageId = getMessageId();
        GetGatewayLockInfoBean getGatewayLockInfoBean = new GetGatewayLockInfoBean(MqttConstant.MSG_TYPE_REQUEST, MyApplication.getInstance().getUid(),
                messageId, gatewayId, deviceId, MqttConstant.GET_LOCK_INFO, new GetGatewayLockInfoBean.ParamsBean(), "0", new GetGatewayLockInfoBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(getGatewayLockInfoBean, messageId);
    }


    /**
     * 唤醒猫眼
     *
     * @param deviceId
     * @param gwId
     * @param uid
     * @return
     */
    public static MqttMessage wakeupCamera(String deviceId, String gwId, String uid) {
        int messageId = getMessageId();
        WakeupCameraBean.ReturnDataBean returnDataBean = new WakeupCameraBean.ReturnDataBean();
        WakeupCameraBean.ParamsBean paramsBean = new WakeupCameraBean.ParamsBean();
        //(String deviceId, String func, String gwId, int msgId, String msgtype, ParamsBean params, int returnCode, ReturnDataBean returnData, String timestamp, String userId) {

        WakeupCameraBean getGatewayLockInfoBean = new WakeupCameraBean(deviceId, MqttConstant.WAKEUP_CAMERA,
                gwId, messageId, MqttConstant.MSG_TYPE_REQUEST, paramsBean, 0, returnDataBean, "" + System.currentTimeMillis(), uid);
        return getMessage(getGatewayLockInfoBean, messageId);
    }

    /**
     * 获取猫眼基本信息
     *
     * @param gatewayId
     * @param deviceId
     * @param uid
     * @return
     */

    public static MqttMessage getCatEyeInfo(String gatewayId, String deviceId, String uid) {
        int messageId = getMessageId();
        CatEyeInfoBean catEyeInfoBean = new CatEyeInfoBean(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.BASIC_INFO, new CatEyeInfoBean.ParamsBean(), "0", new CatEyeInfoBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(catEyeInfoBean, messageId);

    }

    /**
     * 获取猫眼夜市功能
     *
     * @param gatewayId
     * @param deviceId
     * @param uid
     * @return
     */

    public static MqttMessage getCatNightSight(String gatewayId, String deviceId, String uid) {
        int messageId = getMessageId();
        //  CatEyeInfoBean catEyeInfoBean=new CatEyeInfoBean(MqttConstant.MSG_TYPE_REQUEST,uid,messageId,gatewayId,deviceId,MqttConstant.CATEYE_NIGHT_SIGHT,new CatEyeInfoBean.ParamsBean(),"0",new CatEyeInfoBean.ReturnDataBean(),System.currentTimeMillis()+"");

        //  (String msgtype, String userId, String msgId, String gwId, String deviceId, String func, ParamsEntity params, int returnCode, ReturnDataEntity returnData, String timestamp)
        CatEyeInfoBeanProperty.ParamsEntity paramsEntity = new CatEyeInfoBeanProperty.ParamsEntity();
        List<String> result = Arrays.asList("CamInfrared");
        paramsEntity.setPropertys(result);
        CatEyeInfoBeanProperty catEyeInfoBean = new CatEyeInfoBeanProperty(MqttConstant.MSG_TYPE_REQUEST, uid, messageId + "", gatewayId, deviceId, MqttConstant.CATEYE_NIGHT_SIGHT, paramsEntity, 0, new CatEyeInfoBeanProperty.ReturnDataEntity(), System.currentTimeMillis() + "");
        return getMessage(catEyeInfoBean, messageId);

    }


    /**
     * 更新猫眼夜市功能
     *
     * @param gatewayId
     * @param deviceId
     * @param uid
     * @return
     */

    public static MqttMessage updateCatNightSight(String gatewayId, String deviceId, String uid, List<String> values) {
        int messageId = getMessageId();
        //  CatEyeInfoBean catEyeInfoBean=new CatEyeInfoBean(MqttConstant.MSG_TYPE_REQUEST,uid,messageId,gatewayId,deviceId,MqttConstant.CATEYE_NIGHT_SIGHT,new CatEyeInfoBean.ParamsBean(),"0",new CatEyeInfoBean.ReturnDataBean(),System.currentTimeMillis()+"");

        //  (String msgtype, String userId, String msgId, String gwId, String deviceId, String func, ParamsEntity params, int returnCode, ReturnDataEntity returnData, String timestamp)
        CateEyeInfoBeanPropertyUpdate.ParamsEntity paramsEntity = new CateEyeInfoBeanPropertyUpdate.ParamsEntity();
        List<String> result = Arrays.asList("CamInfrared");
        paramsEntity.setPropertys(result);
        paramsEntity.setValues(values);
        //(int returnCode, String func, ReturnDataEntity returnData, String msgId, String gwId, ParamsEntity params, String msgtype, String userId, String deviceId, String timestamp)
        CateEyeInfoBeanPropertyUpdate cateEyeInfoBeanPropertyUpdate = new CateEyeInfoBeanPropertyUpdate(0, MqttConstant.SET_NIGHT_SIGHT, new CateEyeInfoBeanPropertyUpdate.ReturnDataEntity(), messageId, gatewayId, paramsEntity, MqttConstant.MSG_TYPE_REQUEST, uid, deviceId, System.currentTimeMillis() + "");
        //  CateEyeInfoBeanPropertyUpdate catEyeInfoBean=new CateEyeInfoBeanPropertyUpdate(MqttConstant.MSG_TYPE_REQUEST,uid,messageId+"",gatewayId,deviceId,MqttConstant.SET_NIGHT_SIGHT,paramsEntity,0,new CatEyeInfoBeanProperty.ReturnDataEntity(),System.currentTimeMillis()+"");
        return getMessage(cateEyeInfoBeanPropertyUpdate, messageId);

    }

    /**
     * 设置智能监测
     *
     * @param gatewayId
     * @param deivceId
     * @param uid
     * @param status
     * @return
     */
    public static MqttMessage setPirEnable(String gatewayId, String deivceId, String uid, int status) {
        int messageId = getMessageId();
        SetPirEnableBean.ParamsBean paramsBean = new SetPirEnableBean.ParamsBean();
        paramsBean.setPirStatus(status);
        SetPirEnableBean setPirEnableBean = new SetPirEnableBean(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deivceId, MqttConstant.SET_PIR_ENABLE, paramsBean, "0", new SetPirEnableBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(setPirEnableBean, messageId);
    }

    /**
     * 设置响铃次数
     *
     * @param gatewayId
     * @param deviceId
     * @param uid
     * @param number
     * @return
     */
    public static MqttMessage setCatEyeBellCount(String gatewayId, String deviceId, String uid, int number) {
        int messageId = getMessageId();
        SetCatEyeBellCountBean.ParamsBean paramsBean = new SetCatEyeBellCountBean.ParamsBean();
        paramsBean.setBellCount(number);
        SetCatEyeBellCountBean setCatEyeBellCount = new SetCatEyeBellCountBean(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.SET_BELL_COUNT, paramsBean, "0", new SetCatEyeBellCountBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(setCatEyeBellCount, messageId);
    }

    /**
     * 设置猫眼的分辨率
     *
     * @param gatewayId
     * @param deviceId
     * @param uid
     * @param resolution
     * @return
     */
    public static MqttMessage setVedioRes(String gatewayId, String deviceId, String uid, String resolution) {
        int messageId = getMessageId();
        SetVedioResBean.ParamsBean paramsBean = new SetVedioResBean.ParamsBean();
        paramsBean.setResolution(resolution);
        SetVedioResBean setVedioResBean = new SetVedioResBean(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.SET_VEDIO_RES, paramsBean, "0", new SetVedioResBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(setVedioResBean, messageId);
    }

    /**
     * 设置铃声音量
     *
     * @param gatewayId
     * @param deviceId
     * @param uid
     * @param bellVolume
     * @return
     */

    public static MqttMessage setBellVolume(String gatewayId, String deviceId, String uid, int bellVolume) {
        int messageId = getMessageId();
        SetCatEyeBellVolume.ParamsBean paramsBean = new SetCatEyeBellVolume.ParamsBean();
        paramsBean.setBellVolume(bellVolume);
        SetCatEyeBellVolume setCatEyeBellVolume = new SetCatEyeBellVolume(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.SET_BELL_VOLUME, paramsBean, "0", new SetCatEyeBellVolume.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(setCatEyeBellVolume, messageId);
    }

    /**
     * 开锁记录
     *
     * @param gatewayId
     * @param deviceId
     * @param uid
     * @param page
     * @param pageNum
     * @return
     */

    public static MqttMessage selectOpenLockRecord(String gatewayId, String deviceId, String uid, int page, int pageNum) {
        int messageId = getMessageId();
        SelectOpenLockRecordBean selectOpenLockRecordBean = new SelectOpenLockRecordBean(messageId, MqttConstant.GET_OPEN_LOCK_RECORD, uid, gatewayId, deviceId, page, pageNum);
        return getMessage(selectOpenLockRecordBean, messageId);

    }




    /**
     * 报警记录
     *
     * @param deviceId
     * @param pageNum
     * @return
     */

    public static MqttMessage getAlarmList(  String userId, String gwId, String deviceId, int pageNum) {
        int messageId = getMessageId();
        GetAlarmListBean selectOpenLockRecordBean = new GetAlarmListBean(messageId, "request",MqttConstant.GET_ALARM_LIST  , userId, gwId, deviceId, pageNum);
        return getMessage(selectOpenLockRecordBean, messageId);

    }

    /**
     * pir徘徊监测
     *
     * @param uid
     * @param gatewayId
     * @param deviceId
     * @param wander
     * @return
     */
    public static MqttMessage setPirWander(String uid, String gatewayId, String deviceId, String wander) {
        int messageId = getMessageId();
        SetPirWanderBean.ParamsBean paramsBean = new SetPirWanderBean.ParamsBean();
        paramsBean.setWander(wander);
        SetPirWanderBean setPirWanderBean = new SetPirWanderBean(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.SET_PIR_WANDER, paramsBean, "0", new SetPirWanderBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(setPirWanderBean, messageId);
    }

    /**
     * 获取静默参数
     *
     * @param uid
     * @param gatewayId
     * @param deviceId
     * @return
     */
    public static MqttMessage getPirSlient(String uid, String gatewayId, String deviceId) {
        int messageId = getMessageId();
        GetPirSlientBean getPirSlientBean = new GetPirSlientBean(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.GET_PIR_SLIENT, "0", null, System.currentTimeMillis() + "");
        return getMessage(getPirSlientBean, messageId);
    }

    /**
     * 设置静默参数
     *
     * @param uid
     * @param gatewayId
     * @param deviceId
     * @param ust
     * @param enable
     * @param maxprohibition
     * @param periodtime
     * @param protecttime
     * @param threshold
     * @return
     */
    public static MqttMessage setPirSlient(String uid, String gatewayId, String deviceId, int ust, int enable, int maxprohibition, int periodtime, int protecttime, int threshold) {
        int messageId = getMessageId();
        SetPirSlientBean.ParamsBean paramsBean = new SetPirSlientBean.ParamsBean();
        paramsBean.setUst(ust);
        paramsBean.setEnable(enable);
        paramsBean.setMaxprohibition(maxprohibition);
        paramsBean.setPeriodtime(periodtime);
        paramsBean.setProtecttime(protecttime);
        paramsBean.setThreshold(threshold);
        SetPirSlientBean setPirSlientBean = new SetPirSlientBean(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.SET_PIR_SLIENT, paramsBean, "0", new SetPirSlientBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(setPirSlientBean, messageId);
    }

    /**
     * 解绑网关
     *
     * @param uid
     * @param gatewayId
     * @return
     */
    public static MqttMessage unBindGateway(String uid, String gatewayId) {
        int messageId = getMessageId();
        UnBindGatewayBean unBindGatewayBean = new UnBindGatewayBean(messageId, uid, MqttConstant.UNBIND_GATEWAY, gatewayId);
        return getMessage(unBindGatewayBean, messageId);
    }

    /**
     * 解绑测试网关
     *
     * @param uid
     * @param gatewayId
     * @param devuuid
     * @return
     */
    public static MqttMessage unBindTestGateway(String uid, String gatewayId, String devuuid) {
        int messageId = getMessageId();
        UnBindGatewayBean unBindTestGatewayBean = new UnBindGatewayBean(messageId, uid, gatewayId, MqttConstant.UNBIND_TEST_GATEWAY, devuuid);
        return getMessage(unBindTestGatewayBean, messageId);
    }


    /**
     * 设置布防
     *
     * @param deviceId
     * @param gatewayId
     * @param operatingMode
     * @param uid
     * @return
     */
    public static MqttMessage setArmLocked(String deviceId, String gatewayId, int operatingMode, String uid) {
        int messageId = getMessageId();
        SetArmLockedBean.ParamsBean paramsBean = new SetArmLockedBean.ParamsBean();
        paramsBean.setOperatingMode(operatingMode);
        SetArmLockedBean setArmLockedBean = new SetArmLockedBean(deviceId, MqttConstant.SET_ARM_LOCKED, gatewayId, messageId, MqttConstant.MSG_TYPE_REQUEST, paramsBean, "0", new SetArmLockedBean.ReturnDataBean(), System.currentTimeMillis() + "", uid);
        return getMessage(setArmLockedBean, messageId);
    }

    /**
     * 设置AM
     *
     * @param uid
     * @param gatewayId
     * @param deviceId
     * @param autoRelockTime
     * @return
     */
    public static MqttMessage setAM(String uid, String gatewayId, String deviceId, int autoRelockTime) {
        int messageId = getMessageId();
        SetAMBean.ParamsBean paramsBean = new SetAMBean.ParamsBean();
        paramsBean.setAutoRelockTime(autoRelockTime);
        SetAMBean setAMBean = new SetAMBean(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.SET_AM, paramsBean, "200", new SetAMBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(setAMBean, messageId);
    }

    /**
     * 获取布防
     *
     * @param uid
     * @param gatewayId
     * @param deviceId
     * @return
     */
    public static MqttMessage getArmLocked(String uid, String gatewayId, String deviceId) {
        int messageId = getMessageId();
        GetArmLockedBean getArmLockedBean = new GetArmLockedBean(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.GET_ALRAM_LOCK, new GetArmLockedBean.ParamsBean(), "0", new GetArmLockedBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(getArmLockedBean, messageId);
    }

    /**
     * 获取布防
     *
     * @param uid
     * @param gatewayId
     * @param deviceId
     * @return
     */
    public static MqttMessage getAM(String uid, String gatewayId, String deviceId) {
        int messageId = getMessageId();
        GetAMBean getAMBean = new GetAMBean(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.GET_AM, new GetAMBean.ParamsBean(), "0", new GetAMBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(getAMBean, messageId);
    }

    /**
     * 网络设置基本信息
     *
     * @param uid
     * @param gatewayId
     * @param deviceId
     * @return
     */
    public static MqttMessage getNetBasic(String uid, String gatewayId, String deviceId) {
        int messageId = getMessageId();
        GetNetBasicBean getNetBasicBean = new GetNetBasicBean(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.GET_NET_BASIC, new GetNetBasicBean.ParamsBean(), "0", new GetNetBasicBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(getNetBasicBean, messageId);
    }

    /**
     * 网关协调器信道获取
     *
     * @param uid
     * @param gatewayId
     * @param deviceId
     * @return
     */
    public static MqttMessage getZbChannel(String uid, String gatewayId, String deviceId) {
        int messageId = getMessageId();
        GetZbChannelBean getZbChannelBean = new GetZbChannelBean(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.GET_ZB_Channel, "0", new GetZbChannelBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(getZbChannelBean, messageId);
    }

    /**
     * wifi_无线设置
     *
     * @param uid
     * @param gatewayId
     * @param deviceId
     * @param ssid
     * @param pwd
     * @param encryption
     * @return
     */
    public static MqttMessage setWiFiBasic(String uid, String gatewayId, String deviceId, String ssid, String pwd, String encryption) {
        int messageId = getMessageId();
        SetWiFiBasic.ParamsBean paramsBean = new SetWiFiBasic.ParamsBean();
        paramsBean.setSsid(ssid);
        paramsBean.setPwd(pwd);
        paramsBean.setEncryption(encryption);
        SetWiFiBasic setWiFiBasic = new SetWiFiBasic(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.SET_WIFI_BASIC, paramsBean, "0", new SetWiFiBasic.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(setWiFiBasic, messageId);
    }

    /**
     * 网络设置
     *
     * @param uid
     * @param gatewayId
     * @param deviceId
     * @param lanIp
     * @param lanNetmask
     * @return
     */
    public static MqttMessage setNetBasic(String uid, String gatewayId, String deviceId, String lanIp, String lanNetmask) {
        int messageId = getMessageId();
        SetNetBasicBean.ParamsBean paramsBean = new SetNetBasicBean.ParamsBean();
        paramsBean.setLanIp(lanIp);
        paramsBean.setLanNetmask(lanNetmask);
        SetNetBasicBean setNetBasicBean = new SetNetBasicBean(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.SET_NET_BASIC, paramsBean, "0", new SetNetBasicBean.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(setNetBasicBean, messageId);
    }

    /**
     * 网关协调器信道设置
     *
     * @param uid
     * @param gatewayId
     * @param deviceId
     * @param channel
     * @return
     */
    public static MqttMessage setZBChannel(String uid, String gatewayId, String deviceId, String channel) {
        int messageId = getMessageId();
        SetZBChannel.ParamsBean paramsBean = new SetZBChannel.ParamsBean();
        paramsBean.setChannel(channel);
        SetZBChannel setZBChannel = new SetZBChannel(MqttConstant.MSG_TYPE_REQUEST, uid, messageId, gatewayId, deviceId, MqttConstant.SET_ZB_CHANNEL, paramsBean, "0", new SetZBChannel.ReturnDataBean(), System.currentTimeMillis() + "");
        return getMessage(setZBChannel, messageId);
    }

    /**
     * 获取守护次数
     *
     * @param uid
     * @param gatewayId
     * @param deviceId
     * @return
     */

    public static MqttMessage getGatewayLockTotal(String uid, String gatewayId, String deviceId) {
        int messageId = getMessageId();
        GetLockRecordTotal getLockRecordTotal = new GetLockRecordTotal(messageId, MqttConstant.COUNT_OPEN_LOCK_RECORD, uid, gatewayId, deviceId);
        return getMessage(getLockRecordTotal, messageId);
    }

    /**
     * 网关确认ota升级
     *
     * @param notifyBean
     * @param uid
     * @return
     */
    public static MqttMessage gatewayOtaUpgrade(GatewayOtaNotifyBean notifyBean, String uid) {
        int messageId = getMessageId();
        GatewayOtaNotifyBean.ParamsBean notifyParamsBean = notifyBean.getParams();
        LogUtils.e("ota升级地址" + notifyParamsBean.getFileUrl());
        GatewayComfirmOtaUpgradeBean.ParamsBean paramsBean = new GatewayComfirmOtaUpgradeBean.ParamsBean(notifyParamsBean.getModelCode(), notifyParamsBean.getChildCode(), notifyParamsBean.getFileUrl(), notifyParamsBean.getSW(), notifyParamsBean.getFileMd5(), notifyParamsBean.getFileLen(), notifyParamsBean.getOtaType(), 1, notifyParamsBean.getDeviceList(), notifyParamsBean.getDevNum());
        GatewayComfirmOtaUpgradeBean otaUpgradeBean = new GatewayComfirmOtaUpgradeBean(MqttConstant.CONFIRM_GATEWAY_OTA, notifyBean.getGwId(), notifyBean.getDeviceId(), System.currentTimeMillis(), messageId, uid, uid, paramsBean);
        LogUtils.e("ota升级地址" + paramsBean.getFileUrl());
        return getMe(otaUpgradeBean, messageId);
    }


    /**
     * 设置用户类型
     *
     * @param deviceId
     * @param gwId
     * @param userId
     * @param pwdId
     * @param pwdType  // 0 = Unrestricted User(default)  1 = Year Day Schedule Use
     *                 //2 = Week Day Schedule User  3 = Master User  4 = Non Access User
     *                 //0xff = Not Supported
     * @return
     */
    public static MqttMessage setUserType(String deviceId, String gwId, String userId, int pwdId, int pwdType) {
        int messageId = getMessageId();
        SetUserTypeBean.ParamsBean paramsBean = new SetUserTypeBean.ParamsBean(pwdId, pwdType);
        SetUserTypeBean setUserTypeBean = new SetUserTypeBean(deviceId, MqttConstant.SET_USER_TYPE, gwId, messageId,
                MqttConstant.MSG_TYPE_REQUEST, paramsBean, 0, new SetUserTypeBean.ReturnDataBean(), "" + System.currentTimeMillis(), userId);
        return getMessage(setUserTypeBean, messageId);
    }


    /**
     * 设置时间策略
     * @param deviceId
     * @param gwId
     * @param userId
     * @param action
     * @param scheduleId
     * @param type
     * @param pwdId
     * @param zLocalEndT
     * @param zLocalStartT
     * @param dayMaskBits
     * @param endHour
     * @param endMinute
     * @param startHour
     * @param startMinute
     * @return
     */
    public static MqttMessage setPasswordPlan(String deviceId, String gwId, String userId,
                                              String action, int scheduleId, String type, int pwdId, long zLocalEndT, long zLocalStartT,
                                              int dayMaskBits, int endHour, int endMinute, int startHour, int startMinute) {
        int messageId = getMessageId();
        SetPasswordPlanBean.ParamsBean paramsBean = new SetPasswordPlanBean.ParamsBean(action, dayMaskBits, endHour, endMinute, scheduleId,
                startHour, startMinute, type, pwdId, zLocalEndT, zLocalStartT);
        SetPasswordPlanBean setUserTypeBean = new SetPasswordPlanBean(deviceId, MqttConstant.SCHEDULE, gwId, messageId, MqttConstant.MSG_TYPE_REQUEST
                , paramsBean, 0, new SetPasswordPlanBean.ReturnDataBean(), "" + System.currentTimeMillis(), userId
        );
        return getMessage(setUserTypeBean, messageId);
    }


    /**
     * 分享和删除分享用户
     *
     * @param type
     * @param gatewayId
     * @param deviceId
     * @param uid
     * @param shareUser
     * @param userName
     * @param shareFlag
     * @return
     */
    public static MqttMessage shareDevice(int type, String gatewayId, String deviceId, String uid, String shareUser, String userName, int shareFlag) {
        int messageId = getMessageId();
        DeviceShareBean shareBean = new DeviceShareBean(messageId, MqttConstant.SHARE_DEVICE, type, gatewayId, deviceId, uid, shareUser, userName, shareFlag);
        return getMessage(shareBean, messageId);
    }

    /**
     * 更新推送开关
     * @param uid
     * @param gwId
     * @param deviceId
     * @param pushSwitch   1开启推送  2关闭推送
     * @return
     */
    public static MqttMessage updateDevPushSwitch(  String uid, String gwId, String deviceId, int pushSwitch) {
        int messageId = getMessageId();
        UpdateDevPushSwitchBean shareBean = new UpdateDevPushSwitchBean(messageId, MqttConstant.UPDATE_DEV_PUSH_SWITCH, uid, gwId, deviceId, pushSwitch  );
        return getMessage(shareBean, messageId);
    }


    /**
     * 查找分享用户的列表
     *
     * @param gatewayId
     * @param deviceId
     * @param adminId
     * @return
     */

    public static MqttMessage getShareUser(String gatewayId, String deviceId, String adminId) {
        int messageId = getMessageId();
        DeviceShareUserBean shareUserBean = new DeviceShareUserBean(messageId, MqttConstant.MSG_TYPE_REQUEST, MqttConstant.SHARE_USER_LIST, gatewayId, deviceId, adminId);
        return getMessage(shareUserBean, messageId);
    }
//////////
    /**
     * 设置锁外围（开关）
     *
     * @param userId
     * @param wfId
     * @param singleFireSwitchInfoChange
     * @return
     */

    public static MqttMessage setSingleFireSwitch(String userId, String wfId, SingleFireSwitchInfo singleFireSwitchInfoChange) {
        int messageId = getMessageId();
        SingleFireSwitchInfo params = new SingleFireSwitchInfo();
        params.setSwitchEn(singleFireSwitchInfoChange.getSwitchEn());
        params.setTotal(singleFireSwitchInfoChange.getTotal());
        params.setSwitchBind(singleFireSwitchInfoChange.getSwitchBind());
        params.setSwitchNumber(singleFireSwitchInfoChange.getSwitchNumber());
        
        SetSingleFireSwitchBean setSingleFireSwitchBean = new SetSingleFireSwitchBean(MqttConstant.MSG_TYPE_REQUEST, userId, messageId, wfId, MqttConstant.SETTING_DEVICE, params, System.currentTimeMillis() + "");
        return getMessage(setSingleFireSwitchBean, messageId);

    }

//    /**
//     * 查询锁外围（开关）
//     *
//     * @param userId
//     * @param gwId
//     * @param deviceId
//     * @return
//     */
//
//    public static MqttMessage getSingleFireSwitch(String userId, String gwId, String deviceId) {
//
//    }
//
    /**
     * 添加锁外围（开关）
     *
     * @param userId
     * @param wfId
     * @return
     */

    public static MqttMessage addSingleFireSwitch(String userId, String wfId) {
        int messageId = getMessageId();
        SingleFireSwitchInfo params = new SingleFireSwitchInfo();//空
        AddSingleFireSwitchBean addSingleFireSwitchBean = new AddSingleFireSwitchBean(MqttConstant.MSG_TYPE_REQUEST, userId, messageId, wfId, MqttConstant.ADD_DEVICE, params, System.currentTimeMillis()/ 1000);
        return getMessage(addSingleFireSwitchBean, messageId);



    }

    /////////////////////X9////////////////////////////
    /**
     * 设置开门方向
     */
    public static MqttMessage settingOpenDirection(String wifiID,int openDirection , String func) {
        int messageId = getMessageId();
        SettingOpenDirection.ParamsBean paramsBean = new SettingOpenDirection.ParamsBean();
        paramsBean.setOpenDirection(openDirection);
        SettingOpenDirection mSettingOpenDirection = new SettingOpenDirection(MqttConstant.MSG_TYPE_REQUEST,MyApplication.getInstance().getUid(),messageId,
                wifiID,func,paramsBean,System.currentTimeMillis()+"");
        return getMessage(mSettingOpenDirection, messageId,2);
    }

    /**
     * 设置开门力量
     */
    public static MqttMessage settingOpenForce(String wifiID,int openForce,String func) {
        int messageId = getMessageId();
        SettingOpenForce.ParamsBean paramsBean = new SettingOpenForce.ParamsBean();
        paramsBean.setOpenForce(openForce);
        SettingOpenForce mSettingOpenDirection = new SettingOpenForce(MqttConstant.MSG_TYPE_REQUEST,messageId,MyApplication.getInstance().getUid(),
                wifiID,func,paramsBean,System.currentTimeMillis()+"");
        return getMessage(mSettingOpenDirection, messageId,2);
    }

    /**
     * 设置上锁方式
     */
    public static MqttMessage settingLockingMethod(String wifiID,int lockingMethod,String func) {
        int messageId = getMessageId();
        SettingLockingMethod.ParamsBean paramsBean = new SettingLockingMethod.ParamsBean();
        paramsBean.setLockingMethod(lockingMethod);
        SettingLockingMethod mSettingOpenDirection = new SettingLockingMethod(MqttConstant.MSG_TYPE_REQUEST,messageId,MyApplication.getInstance().getUid(),
                wifiID,func,paramsBean,System.currentTimeMillis()+"");
        return getMessage(mSettingOpenDirection, messageId,2);
    }

    ///////////////////////视频锁///////////////////////
    /**
     * 设置视频模组 视频长/短连接设置
     *
     */
    public static MqttMessage settingVideoLockAliveTime(String wifiID,int keep_alive_status,int[] keep_alive_snooze, int snooze_start_time, int snooze_end_time ) {
        int messageId = getMessageId();
        SettingVideoLockAliveTime.AliveTimeBean aliveTimeBean = new SettingVideoLockAliveTime.AliveTimeBean(keep_alive_snooze, snooze_start_time, snooze_end_time);
        SettingVideoLockAliveTime.ParamsBean paramsBean = new SettingVideoLockAliveTime.ParamsBean(keep_alive_status,aliveTimeBean);
        SettingVideoLockAliveTime mSettingVideoLockAliveTime = new SettingVideoLockAliveTime(MqttConstant.MSG_TYPE_REQUEST, messageId,MyApplication.getInstance().getUid(),
                wifiID,MqttConstant.SET_CAMERA,paramsBean,System.currentTimeMillis()+"",0);
        return getMessage(mSettingVideoLockAliveTime, messageId,2);
    }

    /**
     *  设置视频模组 徘徊报警设置
     */
    public static MqttMessage settingVideoLockPir(String wifiID,int stayStatu,int stayTime,int pirSen) {
        int messageId = getMessageId();
        SettingVideoLockPir.PIRBean pirBean = new SettingVideoLockPir.PIRBean(stayTime,pirSen);
        SettingVideoLockPir.ParamsBean paramsBean = new SettingVideoLockPir.ParamsBean(stayStatu,pirBean);
        SettingVideoLockPir mSettingVideoLockAliveTime = new SettingVideoLockPir(MqttConstant.MSG_TYPE_REQUEST, messageId,MyApplication.getInstance().getUid(),
                wifiID,MqttConstant.SET_CAMERA,paramsBean,System.currentTimeMillis()+"",0);
        return getMessage(mSettingVideoLockAliveTime, messageId);
    }

    /**
     * 设置视频模组 屏幕亮度
     *
     */
    public static MqttMessage settingScreenBrightness(String wifiID,int screenBrightness ) {
        int messageId = getMessageId();
        SettingScreenBrightness.ParamsBean paramsBean = new SettingScreenBrightness.ParamsBean(screenBrightness);
        SettingScreenBrightness mSettingScreenBrightness = new SettingScreenBrightness(MqttConstant.MSG_TYPE_REQUEST, messageId,MyApplication.getInstance().getUid(),
                wifiID,MqttConstant.SET_CAMERA,paramsBean,System.currentTimeMillis()+"",0);
        return getMessage(mSettingScreenBrightness, messageId,2);
    }

    /**
     * 设置视频模组 屏幕时长
     *
     */
    public static MqttMessage settingScreenTime(String wifiID,int screenLightSwitch,int screenLightTime ) {
        int messageId = getMessageId();
        SettingScreenTime.ParamsBean paramsBean = new SettingScreenTime.ParamsBean(screenLightSwitch,screenLightTime);
        SettingScreenTime mSettingScreenTime = new SettingScreenTime(MqttConstant.MSG_TYPE_REQUEST, messageId,MyApplication.getInstance().getUid(),
                wifiID,MqttConstant.SET_CAMERA,paramsBean,System.currentTimeMillis()+"",0);
        return getMessage(mSettingScreenTime, messageId,2);
    }

    /**
     * 设置红外传感器
     *
     */
    public static MqttMessage settingAMSSensting(String wifiID,int settingAMSSensting ) {
        int messageId = getMessageId();
        SettingAMSBean.ParamsBean paramsBean = new SettingAMSBean.ParamsBean(settingAMSSensting);
        SettingAMSBean settingAMSBean = new SettingAMSBean(MqttConstant.MSG_TYPE_REQUEST, messageId,MyApplication.getInstance().getUid(),
                wifiID,MqttConstant.SET_LOCK,paramsBean,System.currentTimeMillis()+"",0);
        return getMessage(settingAMSBean, messageId,2);
    }

    /**
     *  设置人脸识别模组 人脸徘徊报警设置
     */
    public static MqttMessage settingFaceWanderingAlarm(String wifiID,int hoverAlarm,int hoverAlarmLevel) {
        int messageId = getMessageId();

        SettingFaceWanderingAlarm.ParamsBean paramsBean = new SettingFaceWanderingAlarm.ParamsBean(hoverAlarm,hoverAlarmLevel);
        SettingFaceWanderingAlarm mSettingFaceWanderingAlarm = new SettingFaceWanderingAlarm(MqttConstant.MSG_TYPE_REQUEST, messageId,MyApplication.getInstance().getUid(),
                wifiID,MqttConstant.SET_LOCK,paramsBean,System.currentTimeMillis()+"");
        return getMessage(mSettingFaceWanderingAlarm, messageId);
    }

    /**
     * 设置锁的语言
     *
     */
    public static MqttMessage setVideoLockLang(String wifiID, String lang) {
        int messageId = getMessageId();
        SetVideoLockLang.ParamsBean paramsBean = new SetVideoLockLang.ParamsBean();
        paramsBean.setLanguage(lang);
        SetVideoLockLang lockLang = new SetVideoLockLang(MqttConstant.MSG_TYPE_REQUEST, MyApplication.getInstance().getUid(),
                messageId, wifiID, MqttConstant.SET_LOCK, paramsBean,  System.currentTimeMillis() + "",0);
        return getMessage(lockLang, messageId);
    }

    /**
     *  设置语音质量
     */
    public static MqttMessage settingVoiceQuality(String wifiID,int voiceQuality) {
        int messageId = getMessageId();

        SettingVoiceQuality.ParamsBean paramsBean = new SettingVoiceQuality.ParamsBean(voiceQuality);
        SettingVoiceQuality mSettingFaceWanderingAlarm = new SettingVoiceQuality(MqttConstant.MSG_TYPE_REQUEST, messageId,MyApplication.getInstance().getUid(),
                wifiID,MqttConstant.SET_LOCK,paramsBean,System.currentTimeMillis()+"");
        return getMessage(mSettingFaceWanderingAlarm, messageId);
    }

    /**
     * 设置安全 模式
     *
     */
    public static MqttMessage setVideoLockSafeMode(String wifiID,int safeMode) {
        int messageId = getMessageId();
        SetVideoLockSafeMode.ParamsBean paramsBean = new SetVideoLockSafeMode.ParamsBean();
        paramsBean.setSafeMode(safeMode);
        SetVideoLockSafeMode lockLang = new SetVideoLockSafeMode(MqttConstant.MSG_TYPE_REQUEST, MyApplication.getInstance().getUid(),
                messageId, wifiID, MqttConstant.SET_LOCK, paramsBean,  System.currentTimeMillis() + "",0);
        return getMessage(lockLang, messageId);
    }

    /**
     * 设置静音 模式
     *
     */
    public static MqttMessage setVideoLockVolume(String wifiID,int volume) {
        int messageId = getMessageId();
        SetVideoLockVolume.ParamsBean paramsBean = new SetVideoLockVolume.ParamsBean();
        paramsBean.setVolume(volume);
        SetVideoLockVolume lockLang = new SetVideoLockVolume(MqttConstant.MSG_TYPE_REQUEST, MyApplication.getInstance().getUid(),
                messageId, wifiID, MqttConstant.SET_LOCK, paramsBean,  System.currentTimeMillis() + "",0);
        return getMessage(lockLang, messageId);
    }

    /**
     * 设置自动 模式
     *
     */
    public static MqttMessage setVideoLockAmMode(String wifiID,int amMode) {
        int messageId = getMessageId();
        SetVideoLockAmMode.ParamsBean paramsBean = new SetVideoLockAmMode.ParamsBean();
        paramsBean.setAmMode(amMode);
        SetVideoLockAmMode lockLang = new SetVideoLockAmMode(MqttConstant.MSG_TYPE_REQUEST, MyApplication.getInstance().getUid(),
                messageId, wifiID, MqttConstant.SET_LOCK, paramsBean,  System.currentTimeMillis() + "");
        return getMessage(lockLang, messageId);
    }

    ////////////////////////////////晾衣机//////////////////////////////////////
    /**
     * 设置晾衣机照明接口
     */
    public static MqttMessage setClothesHangerMachineLighting(String wifiID,int single){
        int messageId = getMessageId();
        SetHangerLightingBean.SetHangerLightingParam paramsBean = new SetHangerLightingBean.SetHangerLightingParam();
        paramsBean.setSingle(single);
        SetHangerLightingBean setHangerLighting = new SetHangerLightingBean(MqttConstant.MSG_TYPE_REQUEST,messageId,
                MyApplication.getInstance().getUid(),wifiID,MqttConstant.CLOTHES_HANGER_MACHINE_SET_LIGHTING,
                paramsBean,MqttConstant.CLOTHES_HANGER_MACHINE_MX_CHIP,System.currentTimeMillis() + "");
        return getMessage(setHangerLighting, messageId,0);
    }

    /**
     * 设置晾衣机电机上升/暂停/下降接口
     */
    public static MqttMessage setClothesHangerMachineMotor(String wifiID,int action){
        int messageId = getMessageId();
        SetHangerMotorBean.SetHangerMotorParam paramsBean = new SetHangerMotorBean.SetHangerMotorParam();
        paramsBean.setAction(action);
        SetHangerMotorBean setHangerLighting = new SetHangerMotorBean(MqttConstant.MSG_TYPE_REQUEST,messageId,
                MyApplication.getInstance().getUid(),wifiID,MqttConstant.CLOTHES_HANGER_MACHINE_SET_MOTOR,
                paramsBean,MqttConstant.CLOTHES_HANGER_MACHINE_MX_CHIP,System.currentTimeMillis() + "");
        return getMessage(setHangerLighting, messageId,0);
    }

    /**
     * 设置晾衣机风干接口
     */
    public static MqttMessage setClothesHangerMachineAirDry(String wifiID,int single){
        int messageId = getMessageId();
        SetHangerLightingBean.SetHangerLightingParam paramsBean = new SetHangerLightingBean.SetHangerLightingParam();
        paramsBean.setSingle(single);
        SetHangerLightingBean setHangerLighting = new SetHangerLightingBean(MqttConstant.MSG_TYPE_REQUEST,messageId,
                MyApplication.getInstance().getUid(),wifiID,MqttConstant.CLOTHES_HANGER_MACHINE_SET_AIR_DRY,
                paramsBean,MqttConstant.CLOTHES_HANGER_MACHINE_MX_CHIP,System.currentTimeMillis() + "");
        return getMessage(setHangerLighting, messageId,0);
    }

    /**
     * 设置晾衣机烘干接口
     */
    public static MqttMessage setClothesHangerMachineBaking(String wifiID,int single){
        int messageId = getMessageId();
        SetHangerLightingBean.SetHangerLightingParam paramsBean = new SetHangerLightingBean.SetHangerLightingParam();
        paramsBean.setSingle(single);
        SetHangerLightingBean setHangerLighting = new SetHangerLightingBean(MqttConstant.MSG_TYPE_REQUEST,messageId,
                MyApplication.getInstance().getUid(),wifiID,MqttConstant.CLOTHES_HANGER_MACHINE_SET_BACKING,
                paramsBean,MqttConstant.CLOTHES_HANGER_MACHINE_MX_CHIP,System.currentTimeMillis() + "");
        return getMessage(setHangerLighting, messageId,0);
    }

    /**
     * 设置晾衣机消毒接口
     */
    public static MqttMessage setClothesHangerMachineUV(String wifiID,int single){
        int messageId = getMessageId();
        SetHangerLightingBean.SetHangerLightingParam paramsBean = new SetHangerLightingBean.SetHangerLightingParam();
        paramsBean.setSingle(single);
        SetHangerLightingBean setHangerLighting = new SetHangerLightingBean(MqttConstant.MSG_TYPE_REQUEST,messageId,
                MyApplication.getInstance().getUid(),wifiID,MqttConstant.CLOTHES_HANGER_MACHINE_SET_UV,
                paramsBean,MqttConstant.CLOTHES_HANGER_MACHINE_MX_CHIP,System.currentTimeMillis() + "");
        return getMessage(setHangerLighting, messageId,0);
    }

    /**
     * 设置晾衣机童锁接口
     */
    public static MqttMessage setClothesHangerMachineChildLock(String wifiID,int single){
        int messageId = getMessageId();
        SetHangerLightingBean.SetHangerLightingParam paramsBean = new SetHangerLightingBean.SetHangerLightingParam();
        paramsBean.setSingle(single);
        SetHangerLightingBean setHangerLighting = new SetHangerLightingBean(MqttConstant.MSG_TYPE_REQUEST,messageId,
                MyApplication.getInstance().getUid(),wifiID,MqttConstant.CLOTHES_HANGER_MACHINE_SET_CHILD_LOCK,
                paramsBean,MqttConstant.CLOTHES_HANGER_MACHINE_MX_CHIP,System.currentTimeMillis() + "");
        return getMessage(setHangerLighting, messageId,0);
    }

    /**
     * 设置晾衣机语音控制开关接口
     */
    public static MqttMessage setClothesHangerMachineLoudspeaker(String wifiID,int single){
        int messageId = getMessageId();
        SetHangerLightingBean.SetHangerLightingParam paramsBean = new SetHangerLightingBean.SetHangerLightingParam();
        paramsBean.setSingle(single);
        SetHangerLightingBean setHangerLighting = new SetHangerLightingBean(MqttConstant.MSG_TYPE_REQUEST,messageId,
                MyApplication.getInstance().getUid(),wifiID,MqttConstant.CLOTHES_HANGER_MACHINE_SET_LOUD_SPEAKER,
                paramsBean,MqttConstant.CLOTHES_HANGER_MACHINE_MX_CHIP,System.currentTimeMillis() + "");
        return getMessage(setHangerLighting, messageId,0);
    }

    /**
     * 获取晾衣机所有状态接口
     */
    public static MqttMessage getHangerAllStatus(String wifiID){
        int messageId = getMessageId();
        GetHangerAllStatus.Params paramsBean = new GetHangerAllStatus.Params();
        GetHangerAllStatus setHangerLighting = new GetHangerAllStatus(MqttConstant.MSG_TYPE_REQUEST,messageId,
                MyApplication.getInstance().getUid(),wifiID,MqttConstant.CLOTHES_HANGER_MACHINE_GET_ALL_STATUS,
                paramsBean,MqttConstant.CLOTHES_HANGER_MACHINE_MX_CHIP,System.currentTimeMillis() + "");
        return getMessage(setHangerLighting, messageId,0);
    }

    /**
     * mqtt公用方法
     *
     * @param o
     * @param messageID
     * @return
     */

    public static MqttMessage getMessage(Object o, int messageID) {
        String payload = new Gson().toJson(o);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(2);
        mqttMessage.setRetained(false);
        mqttMessage.setId(messageID);
        mqttMessage.setPayload(payload.getBytes());
        return mqttMessage;
    }

    public static MqttMessage getMessage(Object o, int messageID,int qos) {
        String payload = new Gson().toJson(o);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(qos);
        mqttMessage.setRetained(false);
        mqttMessage.setId(messageID);
        mqttMessage.setPayload(payload.getBytes());
        return mqttMessage;
    }

    public static MqttMessage getMe(Object o, int messageID) {
        GsonBuilder gb = new GsonBuilder();
        gb.disableHtmlEscaping();
        String payload = gb.create().toJson(o);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(2);
        mqttMessage.setRetained(false);
        mqttMessage.setId(messageID);
        mqttMessage.setPayload(payload.getBytes());
        return mqttMessage;
    }


}
