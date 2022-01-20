package com.kaadas.lock.publiclibrary.http;

import com.kaadas.lock.bean.VersionBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.ClothesHangerMachineBindResult;
import com.kaadas.lock.publiclibrary.http.result.ClothesHangerMachineCheckBindingResult;
import com.kaadas.lock.publiclibrary.http.result.ClothesHangerMachineUnBindResult;
import com.kaadas.lock.publiclibrary.http.result.DeleteMessageResult;
import com.kaadas.lock.publiclibrary.http.result.GetDeviceResult;
import com.kaadas.lock.publiclibrary.http.result.GetHelpLogResult;
import com.kaadas.lock.publiclibrary.http.result.GetOpenCountResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.publiclibrary.http.result.GetPwdBySnResult;
import com.kaadas.lock.publiclibrary.http.result.GetWarringRecordResult;
import com.kaadas.lock.publiclibrary.http.result.GetWifiLockAlarmRecordResult;
import com.kaadas.lock.publiclibrary.http.result.GetWifiLockOperationRecordResult;
import com.kaadas.lock.publiclibrary.http.result.GetWifiVideoLockAlarmRecordResult;
import com.kaadas.lock.publiclibrary.http.result.LockRecordResult;
import com.kaadas.lock.publiclibrary.http.result.LoginResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.publiclibrary.http.result.MultiCheckOTAResult;
import com.kaadas.lock.publiclibrary.http.result.OperationRecordResult;
import com.kaadas.lock.publiclibrary.http.result.RegisterResult;
import com.kaadas.lock.publiclibrary.http.result.SinglePasswordResult;
import com.kaadas.lock.publiclibrary.http.result.SwitchStatusResult;
import com.kaadas.lock.publiclibrary.http.result.UserNickResult;
import com.kaadas.lock.publiclibrary.http.result.UserProtocolResult;
import com.kaadas.lock.publiclibrary.http.result.UserProtocolVersionResult;
import com.kaadas.lock.publiclibrary.http.result.WifiDeviceListResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockGetPasswordListResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockShareResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockVideoBindResult;
import com.kaadas.lock.publiclibrary.http.temp.resultbean.CheckBindResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;
import com.kaadas.lock.utils.KeyConstants;


import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Create By lxj  on 2019/2/27
 * Describe
 */
public interface IXiaoKaiNewService {
    //////////////////////////////////////// 注册登陆/////////////////////////////////////////////////

    /**
     * 通过手机号注册
     *
     * @return
     */
    @POST(HttpUrlConstants.REGISTER_BY_PHONE)
    @Headers({KeyConstants.VERSION})
    Observable<RegisterResult> registerByPhone(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 通过邮箱注册
     *
     * @return
     */
    @POST(HttpUrlConstants.EMAIL_REGISTER)
    @Headers({KeyConstants.VERSION})
    Observable<RegisterResult> registerByEmail(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 通过手机号登陆
     *
     * @return
     */
    @POST(HttpUrlConstants.LOGIN_BY_PHONE)
    @Headers({KeyConstants.VERSION})
    Observable<LoginResult> loginByPhone(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 通过邮箱登陆
     *
     * @return
     */
    @POST(HttpUrlConstants.EMAIL_LOGIN)
    @Headers({KeyConstants.VERSION})
    Observable<LoginResult> loginByEmail(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 忘记密码
     *
     * @return
     */
    @POST(HttpUrlConstants.FORGET_PASSWORD)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> forgetPassword(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 修改密码   APP内  需要token
     *
     * @return
     */
    @POST(HttpUrlConstants.MODIFY_PASSWORD)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> modifyPassword(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 退出登陆
     *
     * @return
     */
    @POST(HttpUrlConstants.LOGIN_OUT)
    Observable<BaseResult> loginOut();


    //////////////////////////////////////////////用户管理/////////////////////////////////////////

    /**
     * 获取用户昵称
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_USER_NICK_NAME)
    @Headers({KeyConstants.VERSION})
    Observable<UserNickResult> getUserNick(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 修改用户昵称
     *
     * @return
     */
    @POST(HttpUrlConstants.MODIFY_USER_NICK_NAME)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> modifyUserNick(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 上传用户头像
     *
     * @return
     */
    @POST(HttpUrlConstants.UPLOAD_USER_HEARD)
    Observable<BaseResult> uploadUserHead(@Body RequestBody info);

    /**
     * 获取用户头像
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_USER_HEARD)
    Observable<BaseResult> getUserHeard(@Body RequestBody info);

    /**
     * 用户留言
     *
     * @return
     */
    @POST(HttpUrlConstants.PUT_MESSAGE)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> putMessage(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 获取指定设备的普通管理员
     */
    @POST(HttpUrlConstants.GET_NORMALS_DEVLIST)
    @Headers({KeyConstants.VERSION})
    Observable<String> getDeviceGeneralAdministrator(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 删除设备的普通用户
     */
    @POST(HttpUrlConstants.DELETE_NORMALDEV)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> deleteDeviceNormalUser(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 获取用户协议版本
     *
     * @return
     */
    @GET(HttpUrlConstants.GET_USER_PROTOCOL_VERSION)
    Observable<UserProtocolVersionResult> getUserProtocolVersion();

    /**
     * 获取用户协议
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_USER_PROTOCOL_CONTENT)
    @Headers({KeyConstants.VERSION})
    Observable<UserProtocolResult> getUserProtocolContent(@Header("timestamp") String timestamp,@Body RequestBody info);


    ////////////////////////////////////////////////门锁管理/////////////////////////////////////////////////////////////

    /**
     * 管理员添加设备
     *
     * @return
     */
    @POST(HttpUrlConstants.ADD_DEVICE)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> addDevice(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 查询列表设备列表
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_DEVICES)
    @Headers({KeyConstants.VERSION})
    Observable<GetDeviceResult> getDevices(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 删除设备
     *
     * @return
     */
    @POST(HttpUrlConstants.DELETE_DEVICE)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> deleteDevice(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 修改门锁昵称
     *
     * @return
     */
    @POST(HttpUrlConstants.MODIFY_LOCK_NICK_NAME)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> modifyLockNick(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 根据SN获取pwd
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_PWD_BY_SN)
    @Headers({KeyConstants.VERSION})
    Observable<GetPwdBySnResult> getpwdBySN(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 三方重置门锁   解绑门锁
     *
     * @return
     */
    @POST(HttpUrlConstants.RESET_DEVICE)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> resetDevice(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 门锁密钥添加
     *
     * @return
     */
    @POST(HttpUrlConstants.ADD_PASSWORD)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> addPassword(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 删除秘钥
     *
     * @return
     */
    @POST(HttpUrlConstants.DELETE_PASSWORD)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> deletePassword(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 修改秘钥昵称
     *
     * @return
     */
    @POST(HttpUrlConstants.MODIFY_PASSWORD_NICK)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> modifyPasswordNick(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 查询秘钥列表
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_PASSWORDS)
    @Headers({KeyConstants.VERSION})
    Observable<GetPasswordResult> getPasswords(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 查询单个秘钥
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_SINGLE_PASSWORD)
    @Headers({KeyConstants.VERSION})
    Observable<SinglePasswordResult> getSinglePassword(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 检查所是否绑定
     * @return
     */
    @POST(HttpUrlConstants.CHECK_LOCK_BIND)
    @Headers({KeyConstants.VERSION})
    Observable<CheckBindResult> checkLockBind(@Header("timestamp") String timestamp,@Body RequestBody info);

    ////////////////////////////////////////////用户管理////////////////////////////////////////////////

    /**
     * 添加普通用户
     *
     * @return
     */
    @POST(HttpUrlConstants.ADD_USER)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> addUser(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 修改普通用户昵称
     *
     * @return
     */
    @POST(HttpUrlConstants.MODIFY_COMMON_USER_NICKNAME)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> modifyCommonUserNickname(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 删除普通用户
     *
     * @return
     */
    @POST(HttpUrlConstants.DELETE_USER)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> deleteUser(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 查询普通用户列表
     *
     * @return
     */
    @POST(HttpUrlConstants.SEARCH_USER)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> searchUser(@Header("timestamp") String timestamp,@Body RequestBody info);


    //////////////////////////////////////开门记录///////////////////////////////////////////////

    /**
     * 上传App开门记录
     *
     * @return
     */
    @POST(HttpUrlConstants.UPLOAD_APP_RECORD)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> uploadAppRecord(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 上传秘钥开门记录
     *
     * @return
     */
    @POST(HttpUrlConstants.UPLOAD_BIN_RECORD)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> uploadBinRecord(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 获取开门记录
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_LOCK_RECORD)
    @Headers({KeyConstants.VERSION})
    Observable<LockRecordResult> getLockRecord(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 获取操作记录
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_OPERATION_RECORD)
    @Headers({KeyConstants.VERSION})
    Observable<OperationRecordResult> getOperationRecord(@Header("timestamp") String timestamp,@Body RequestBody info);

    ////////////////////////////////////////////预警记录////////////////////////////////////////////

    /**
     * 上报预警记录
     *
     * @return
     */
    @POST(HttpUrlConstants.UPLOAD_WARRING_RECORD)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> uploadWarringRecord(@Header("timestamp") String timestamp,@Body RequestBody info);


    /**
     * 上传操作记录
     *
     * @return
     */
    @POST(HttpUrlConstants.UPLOAD_OPERATION_RECORD)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> uploadOperationRecord(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 获取预警记录
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_WARRING_RECORD)
    @Headers({KeyConstants.VERSION})
    Observable<GetWarringRecordResult> getWarringRecord(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 发送短信验证码
     *
     * @return
     */
    @POST(HttpUrlConstants.SEND_MSG)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> sendMessage(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 发送邮箱验证码
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_EMAIL_YZM)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> sendEamilYZM(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 上传头像
     *
     * @return
     */
    @Multipart
    @POST(HttpUrlConstants.UP_LOAD_USERHEAD)
    Observable<BaseResult> getPicturesBean(@Part("uid") RequestBody uid, @Part MultipartBody.Part file);

    /**
     * 下载头像
     *
     * @return
     */
    @GET(HttpUrlConstants.DOWN_LOAD_USERHEAD + "{uid}")
    @Streaming
    Observable<ResponseBody> downloadUserHead(@Path("uid") String uid);

    /**
     * 获取消息列表
     */
    @POST(HttpUrlConstants.SYSTEM_MESSAGE)
    @Headers({KeyConstants.VERSION})
    Observable<String> getMessageList(@Header("timestamp") String timestamp,@Body RequestBody info);


    /**
     * 获取常见问题列表
     */
    @GET(HttpUrlConstants.FAQ_LIST + "{languageType}")
    Observable<String> getFAQList(@Path("languageType") int languageType);

    /**
     * 删除消息列表
     */
    @POST(HttpUrlConstants.SYSTEM_MESSAGE_DELETE)
    @Headers({KeyConstants.VERSION})
    Observable<DeleteMessageResult> deleteMessage(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 查询帮助日志列表
     */
    @POST(HttpUrlConstants.SYSTEM_HELP_LOG)
    @Headers({KeyConstants.VERSION})
    Observable<GetHelpLogResult> getHelpLog(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 获取APP的版本信息
     */
    @Deprecated
    @GET(HttpUrlConstants.GET_APP_VERSION)
    Observable<VersionBean> getAppVersion();


    /**
     * 开锁鉴权
     */
    @POST(HttpUrlConstants.USER_OPEN_LOCK_AUTHORITY)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> openLockAuth(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 上传PushId
     *
     * @param info
     * @return
     */
    @POST(HttpUrlConstants.UPLOAD_PUSH_ID)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> uploadPushId(@Header("timestamp") String timestamp,@Body RequestBody info);


    /**
     * 上传Msg
     *
     * @param info
     * @return
     */
    @POST(HttpUrlConstants.UPLOAD_PHONE_MSG)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> uploadPushMsg(@Header("timestamp") String timestamp,@Body RequestBody info);


    /**
     * 获取控制个推开关
     *
     * @param info
     * @return
     */
    @POST(HttpUrlConstants.GET_PUSH_SWITch)
    @Headers({KeyConstants.VERSION})
    Observable<SwitchStatusResult> getPushSwitch(@Header("timestamp") String timestamp,@Body RequestBody info);


    /**
     * 修改个推开关状态
     *
     * @param info
     * @return
     */
    @POST(HttpUrlConstants.UPDATE_PUSH_SWITch)
    @Headers({KeyConstants.VERSION})
    Observable<SwitchStatusResult> updatePushSwitch(@Header("timestamp") String timestamp,@Body RequestBody info);


    /**
     * 上传版本号和SN到服务器
     */
    @POST(HttpUrlConstants.UPDATE_SOFTWARE_VERSION)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> updateSoftwareVersion(@Header("timestamp") String timestamp,@Body RequestBody info);


    /**
     * 上传版本号和SN到服务器
     */
    @POST(HttpUrlConstants.UPDATE_BLE_VERSION)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> updateBleVersion(@Header("timestamp") String timestamp,@Body RequestBody info);


    /**
     * 上传版本号和SN到服务器
     */
    @POST(HttpUrlConstants.MODIFY_FUNCTION_SET)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> modifyFunctionSet(@Header("timestamp") String timestamp,@Body RequestBody info);


    /**
     * OTA升级查询ApI
     */
    @POST(HttpUrlConstants.OTA_INFO_URL)
    @Headers({KeyConstants.VERSION})
    Observable<CheckOTAResult> getOtaInfo(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * OTA多固件升级查询ApI
     */
    @POST(HttpUrlConstants.OTA_MULTI_CHECK)
    @Headers({KeyConstants.VERSION})
    Observable<MultiCheckOTAResult> getOtaMultiCheck(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * OTA升级结果上报
     */
    @POST(HttpUrlConstants.OTA_RESULT_UPLOAD_URL)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> uploadOtaResult(@Header("timestamp") String timestamp,@Body RequestBody info);


    ////////////////////////////////////////////           WiFi锁api功能            ///////////////////////////////////////////////

    /**
     * 绑定WiFi锁
     */
    @POST(HttpUrlConstants.WIFI_LOCK_BIND)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> wifiLockBind(@Header("timestamp") String timestamp,@Body RequestBody info);


    /**
     * 解绑WiFi锁
     */
    @POST(HttpUrlConstants.WIFI_LOCK_UNBIND)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> wifiLockUnbind(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 预绑定设备
     */
    @POST(HttpUrlConstants.WIFI_LOCK_PRE_BINDING)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> wifiLockPreBind(@Header("timestamp") String timestamp,@Body RequestBody info);


    /**
     * 修改WiFi锁昵称
     */
    @POST(HttpUrlConstants.WIFI_LOCK_UPDATE_NICK_NAME)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> wifiLockUpdateNickname(@Header("timestamp") String timestamp,@Body RequestBody info);


    /**
     * 更改推送开关
     */
    @POST(HttpUrlConstants.WIFI_LOCK_UPDATE_PUSH)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> wifiLockUpdatePush(@Header("timestamp") String timestamp,@Body RequestBody info);


    /**
     * 获取wifi锁密码列表
     */
    @POST(HttpUrlConstants.WIFI_LOCK_GET_PWD_LIST)
    @Headers({KeyConstants.VERSION})
    Observable<WifiLockGetPasswordListResult> wifiLockGetPwdList(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 修改wifi密码昵称
     */
    @POST(HttpUrlConstants.WIFI_LOCK_UPDATE_PWD_NICKNAME)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> wifiLockUpdatePwdNickName(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * wifi锁授权用户
     */
    @POST(HttpUrlConstants.WIFI_LOCK_SHARE)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> wifiLockShare(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * wifi锁删除授权用户
     */
    @POST(HttpUrlConstants.WIFI_LOCK_DELETE_SHARE)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> wifiLockDeleteShareUser(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * wifi锁修改授权用户昵称
     */
    @POST(HttpUrlConstants.WIFI_LOCK_UPDATE_SHARE_NICKNAME)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> wifiLockUpdateShareUserNickname(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * wifi锁授权用户列表
     */
    @POST(HttpUrlConstants.WIFI_LOCK_GET_SHARE_LIST)
    @Headers({KeyConstants.VERSION})
    Observable<WifiLockShareResult> wifiLockGetShareUserList(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 获取wifi锁操作记录
     */
    @POST(HttpUrlConstants.WIFI_LOCK_OPERATION_LIST)
    @Headers({KeyConstants.VERSION})
    Observable<GetWifiLockOperationRecordResult> wifiLockGetOperationList(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 获取wifi报警记录
     */
    @POST(HttpUrlConstants.WIFI_LOCK_ALARM_LIST)
    @Headers({KeyConstants.VERSION})
    Observable<GetWifiLockAlarmRecordResult> wifiLockGetAlarmList(@Header("timestamp") String timestamp,@Body RequestBody info);
    /**
     * 开锁次数
     */
    @POST(HttpUrlConstants.WIFI_LOCK_OPEN_COUNT)
    @Headers({KeyConstants.VERSION})
    Observable<GetOpenCountResult> wifiLockGetOpenCount(@Header("timestamp") String timestamp,@Body RequestBody info);


    /**
     * 更新wifi信息
     */
    @POST(HttpUrlConstants.WIFI_LOCK_UPDATE_INFO)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> wifiLockUpdateInfo(@Header("timestamp") String timestamp,@Body RequestBody info);



    /**
     * 更新wifi信息
     */
    @POST(HttpUrlConstants.WIFI_LOCK_UPLOAD_OTA)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> wifiLockUploadOta(@Header("timestamp") String timestamp,@Body RequestBody info);


    /**
     * 获取二维码连接数据
     */
    @GET
    Observable<ResponseBody> getQrCodeContent(@Url String url);



    @POST(HttpUrlConstants.GET_ZIGBEEN_INFO)
    @Headers({KeyConstants.VERSION})

    Observable<String> GET_ZIGBEEN_INFO_CONTEXT(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 单火开关设备联动开关昵称修改
     *
     * @return
     */
    @POST(HttpUrlConstants.UPDATE_SWITCH_NICK_NAME)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> updateSwitchNickname(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     *  设置胁迫报警开关
     */
    @POST(HttpUrlConstants.WIFI_DURESS_ALARM_SWITCH)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> wifiDuressAlarmSwitch(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     *  设置密钥胁迫报警开关
     */
    @POST(HttpUrlConstants.WIFI_PWD_DURESS_ALARM)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> wifiPwdDuressAlarm(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     *  设置密钥胁迫报警接收账户
     */
    @POST(HttpUrlConstants.WIFI_PWD_DURESS_ACCOUNT)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> wifiPwdDuressAccount(@Header("timestamp") String timestamp,@Body RequestBody info);

    ////////////////////////////////////////////           WiFi视频锁api功能            ///////////////////////////////////////////////

    /**
     * 绑定视频锁
     */
    @POST(HttpUrlConstants.WIFI_VIDEO_LOCK_BIND)
    @Headers({KeyConstants.VERSION})
    Observable<WifiLockVideoBindResult> wifiVideoLockBind(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 解绑视频锁
     */
    @POST(HttpUrlConstants.WIFI_VIDEO_LOCK_UNBIND)
    @Headers({KeyConstants.VERSION})
    Observable<WifiLockVideoBindResult> wifiVideoLockUnbind(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 解绑视频锁失败
     */
    @POST(HttpUrlConstants.WIFI_VIDEO_LOCK_BIND_FAIL)
    @Headers({KeyConstants.VERSION})
    Observable<WifiLockVideoBindResult> wifiVideoLockBindFail(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     *  更新视频锁
     */
    @POST(HttpUrlConstants.WIFI_VIDEO_LOCK_UPDATE_BIND)
    @Headers({KeyConstants.VERSION})
    Observable<WifiLockVideoBindResult> wifiVideoLockUpdateBind(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 获取视频锁报警记录
     */
    @POST(HttpUrlConstants.WIFI_VIDEO_LOCK_ALARM_LIST)
    @Headers({KeyConstants.VERSION})
    Observable<GetWifiVideoLockAlarmRecordResult> wifiVideoLockGetAlarmList(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 获取分页查询门铃记录
     */
    @POST(HttpUrlConstants.WIFI_VIDEO_LOCK_DOORBELL_LIST)
    @Headers({KeyConstants.VERSION})
    Observable<GetWifiVideoLockAlarmRecordResult> wifiVideoLockGetDoorbellList(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     *  查询wifi锁设备列表
     */
    @POST(HttpUrlConstants.WIFI_DEVICE_LIST)
    @Headers({KeyConstants.VERSION})
    Observable<WifiDeviceListResult> wifiDeviceList(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 查询设备列表
     */
    @POST(HttpUrlConstants.GET_ALL_BIND_DEVICES)
    @Headers({KeyConstants.VERSION})
    Observable<AllBindDevices> getAllBindDevices(@Header("timestamp") String timestamp, @Body RequestBody info);

    ////////////////////////////////////////////           晾衣机api功能            ///////////////////////////////////////////////

    /**
     * 检查晾衣机设备是否被绑定
     */
    @POST(HttpUrlConstants.HANGER_CHECK_BINDING)
    @Headers({KeyConstants.VERSION})
    Observable<ClothesHangerMachineCheckBindingResult> clothesHangerMachineCheckBinding(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 检查晾衣机设备是否被绑定
     */
    @POST(HttpUrlConstants.HANGER_BIND)
    @Headers({KeyConstants.VERSION})
    Observable<ClothesHangerMachineBindResult> clothesHangerMachineBind(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 检查晾衣机设备是否被绑定
     */
    @POST(HttpUrlConstants.HANGER_UNBIND)
    @Headers({KeyConstants.VERSION})
    Observable<ClothesHangerMachineUnBindResult> clothesHangerMachineUnbind(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 修改晾衣机昵称
     */
    @POST(HttpUrlConstants.MODIFY_HANGER_NICK_NAME)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> hangerUpdateNickname(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 多固件确认升级
     */
    @POST(HttpUrlConstants.WIFI_LOCK_UPLOAD_MULTI_OTA)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> wifiDeviceUploadMultiOta(@Header("timestamp") String timestamp,@Body RequestBody info);

    /**
     * 注销账号
     */
    @POST(HttpUrlConstants.ACCOUNT_LOGOUT)
    @Headers({KeyConstants.VERSION})
    Observable<BaseResult> accountLogout(@Header("timestamp") String timestamp,@Body RequestBody info);

}
