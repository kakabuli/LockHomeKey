package com.kaadas.lock.publiclibrary.http;

import com.kaadas.lock.bean.VersionBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.DeleteMessageResult;
import com.kaadas.lock.publiclibrary.http.result.GetDeviceResult;
import com.kaadas.lock.publiclibrary.http.result.GetHelpLogResult;
import com.kaadas.lock.publiclibrary.http.result.GetOpenCountResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.publiclibrary.http.result.GetPwdBySnResult;
import com.kaadas.lock.publiclibrary.http.result.GetWarringRecordResult;
import com.kaadas.lock.publiclibrary.http.result.GetWifiLockAlarmRecordResult;
import com.kaadas.lock.publiclibrary.http.result.GetWifiLockOperationRecordResult;
import com.kaadas.lock.publiclibrary.http.result.LockRecordResult;
import com.kaadas.lock.publiclibrary.http.result.LoginResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.publiclibrary.http.result.OperationRecordResult;
import com.kaadas.lock.publiclibrary.http.result.RegisterResult;
import com.kaadas.lock.publiclibrary.http.result.SinglePasswordResult;
import com.kaadas.lock.publiclibrary.http.result.SwitchStatusResult;
import com.kaadas.lock.publiclibrary.http.result.UserNickResult;
import com.kaadas.lock.publiclibrary.http.result.UserProtocolResult;
import com.kaadas.lock.publiclibrary.http.result.UserProtocolVersionResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockGetPasswordListResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockShareResult;


import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
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
    Observable<RegisterResult> registerByPhone(@Body RequestBody info);

    /**
     * 通过邮箱注册
     *
     * @return
     */
    @POST(HttpUrlConstants.EMAIL_REGISTER)
    Observable<RegisterResult> registerByEmail(@Body RequestBody info);

    /**
     * 通过手机号登陆
     *
     * @return
     */
    @POST(HttpUrlConstants.LOGIN_BY_PHONE)
    Observable<LoginResult> loginByPhone(@Body RequestBody info);

    /**
     * 通过邮箱登陆
     *
     * @return
     */
    @POST(HttpUrlConstants.EMAIL_LOGIN)
    Observable<LoginResult> loginByEmail(@Body RequestBody info);

    /**
     * 忘记密码
     *
     * @return
     */
    @POST(HttpUrlConstants.FORGET_PASSWORD)
    Observable<BaseResult> forgetPassword(@Body RequestBody info);

    /**
     * 修改密码   APP内  需要token
     *
     * @return
     */
    @POST(HttpUrlConstants.MODIFY_PASSWORD)
    Observable<BaseResult> modifyPassword(@Body RequestBody info);

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
    Observable<UserNickResult> getUserNick(@Body RequestBody info);

    /**
     * 修改用户昵称
     *
     * @return
     */
    @POST(HttpUrlConstants.MODIFY_USER_NICK_NAME)
    Observable<BaseResult> modifyUserNick(@Body RequestBody info);

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
    Observable<BaseResult> putMessage(@Body RequestBody info);

    /**
     * 获取指定设备的普通管理员
     */
    @POST(HttpUrlConstants.GET_NORMALS_DEVLIST)
    Observable<String> getDeviceGeneralAdministrator(@Body RequestBody info);

    /**
     * 删除设备的普通用户
     */
    @POST(HttpUrlConstants.DELETE_NORMALDEV)
    Observable<BaseResult> deleteDeviceNormalUser(@Body RequestBody info);

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
    Observable<UserProtocolResult> getUserProtocolContent(@Body RequestBody info);


    ////////////////////////////////////////////////门锁管理/////////////////////////////////////////////////////////////

    /**
     * 管理员添加设备
     *
     * @return
     */
    @POST(HttpUrlConstants.ADD_DEVICE)
    Observable<BaseResult> addDevice(@Body RequestBody info);

    /**
     * 查询列表设备列表
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_DEVICES)
    Observable<GetDeviceResult> getDevices(@Body RequestBody info);

    /**
     * 删除设备
     *
     * @return
     */
    @POST(HttpUrlConstants.DELETE_DEVICE)
    Observable<BaseResult> deleteDevice(@Body RequestBody info);

    /**
     * 修改门锁昵称
     *
     * @return
     */
    @POST(HttpUrlConstants.MODIFY_LOCK_NICK_NAME)
    Observable<BaseResult> modifyLockNick(@Body RequestBody info);

    /**
     * 根据SN获取pwd
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_PWD_BY_SN)
    Observable<GetPwdBySnResult> getpwdBySN(@Body RequestBody info);

    /**
     * 三方重置门锁   解绑门锁
     *
     * @return
     */
    @POST(HttpUrlConstants.RESET_DEVICE)
    Observable<BaseResult> resetDevice(@Body RequestBody info);

    /**
     * 门锁密钥添加
     *
     * @return
     */
    @POST(HttpUrlConstants.ADD_PASSWORD)
    Observable<BaseResult> addPassword(@Body RequestBody info);

    /**
     * 删除秘钥
     *
     * @return
     */
    @POST(HttpUrlConstants.DELETE_PASSWORD)
    Observable<BaseResult> deletePassword(@Body RequestBody info);

    /**
     * 修改秘钥昵称
     *
     * @return
     */
    @POST(HttpUrlConstants.MODIFY_PASSWORD_NICK)
    Observable<BaseResult> modifyPasswordNick(@Body RequestBody info);

    /**
     * 查询秘钥列表
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_PASSWORDS)
    Observable<GetPasswordResult> getPasswords(@Body RequestBody info);

    /**
     * 查询单个秘钥
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_SINGLE_PASSWORD)
    Observable<SinglePasswordResult> getSinglePassword(@Body RequestBody info);

    ////////////////////////////////////////////用户管理////////////////////////////////////////////////

    /**
     * 添加普通用户
     *
     * @return
     */
    @POST(HttpUrlConstants.ADD_USER)
    Observable<BaseResult> addUser(@Body RequestBody info);

    /**
     * 修改普通用户昵称
     *
     * @return
     */
    @POST(HttpUrlConstants.MODIFY_COMMON_USER_NICKNAME)
    Observable<BaseResult> modifyCommonUserNickname(@Body RequestBody info);

    /**
     * 删除普通用户
     *
     * @return
     */
    @POST(HttpUrlConstants.DELETE_USER)
    Observable<BaseResult> deleteUser(@Body RequestBody info);

    /**
     * 查询普通用户列表
     *
     * @return
     */
    @POST(HttpUrlConstants.SEARCH_USER)
    Observable<BaseResult> searchUser(@Body RequestBody info);


    //////////////////////////////////////开门记录///////////////////////////////////////////////

    /**
     * 上传App开门记录
     *
     * @return
     */
    @POST(HttpUrlConstants.UPLOAD_APP_RECORD)
    Observable<BaseResult> uploadAppRecord(@Body RequestBody info);

    /**
     * 上传秘钥开门记录
     *
     * @return
     */
    @POST(HttpUrlConstants.UPLOAD_BIN_RECORD)
    Observable<BaseResult> uploadBinRecord(@Body RequestBody info);

    /**
     * 获取开门记录
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_LOCK_RECORD)
    Observable<LockRecordResult> getLockRecord(@Body RequestBody info);

    /**
     * 获取操作记录
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_OPERATION_RECORD)
    Observable<OperationRecordResult> getOperationRecord(@Body RequestBody info);

    ////////////////////////////////////////////预警记录////////////////////////////////////////////

    /**
     * 上报预警记录
     *
     * @return
     */
    @POST(HttpUrlConstants.UPLOAD_WARRING_RECORD)
    Observable<BaseResult> uploadWarringRecord(@Body RequestBody info);


    /**
     * 上传操作记录
     *
     * @return
     */
    @POST(HttpUrlConstants.UPLOAD_OPERATION_RECORD)
    Observable<BaseResult> uploadOperationRecord(@Body RequestBody info);

    /**
     * 获取预警记录
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_WARRING_RECORD)
    Observable<GetWarringRecordResult> getWarringRecord(@Body RequestBody info);

    /**
     * 发送短信验证码
     *
     * @return
     */
    @POST(HttpUrlConstants.SEND_MSG)
    Observable<BaseResult> sendMessage(@Body RequestBody info);

    /**
     * 发送邮箱验证码
     *
     * @return
     */
    @POST(HttpUrlConstants.GET_EMAIL_YZM)
    Observable<BaseResult> sendEamilYZM(@Body RequestBody info);

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
    Observable<String> getMessageList(@Body RequestBody info);


    /**
     * 获取常见问题列表
     */
    @GET(HttpUrlConstants.FAQ_LIST + "{languageType}")
    Observable<String> getFAQList(@Path("languageType") int languageType);

    /**
     * 删除消息列表
     */
    @POST(HttpUrlConstants.SYSTEM_MESSAGE_DELETE)
    Observable<DeleteMessageResult> deleteMessage(@Body RequestBody info);

    /**
     * 查询帮助日志列表
     */
    @POST(HttpUrlConstants.SYSTEM_HELP_LOG)
    Observable<GetHelpLogResult> getHelpLog(@Body RequestBody info);

    /**
     * 获取APP的版本信息
     */
    @GET(HttpUrlConstants.GET_APP_VERSION)
    Observable<VersionBean> getAppVersion();


    /**
     * 开锁鉴权
     */
    @POST(HttpUrlConstants.USER_OPEN_LOCK_AUTHORITY)
    Observable<BaseResult> openLockAuth(@Body RequestBody info);

    /**
     * 上传PushId
     *
     * @param info
     * @return
     */
    @POST(HttpUrlConstants.UPLOAD_PUSH_ID)
    Observable<BaseResult> uploadPushId(@Body RequestBody info);


    /**
     * 上传Msg
     *
     * @param info
     * @return
     */
    @POST(HttpUrlConstants.UPLOAD_PHONE_MSG)
    Observable<BaseResult> uploadPushMsg(@Body RequestBody info);


    /**
     * 获取控制个推开关
     *
     * @param info
     * @return
     */
    @POST(HttpUrlConstants.GET_PUSH_SWITch)
    Observable<SwitchStatusResult> getPushSwitch(@Body RequestBody info);


    /**
     * 修改个推开关状态
     *
     * @param info
     * @return
     */
    @POST(HttpUrlConstants.UPDATE_PUSH_SWITch)
    Observable<SwitchStatusResult> updatePushSwitch(@Body RequestBody info);


    /**
     * 上传版本号和SN到服务器
     */
    @POST(HttpUrlConstants.UPDATE_SOFTWARE_VERSION)
    Observable<BaseResult> updateSoftwareVersion(@Body RequestBody info);


    /**
     * 上传版本号和SN到服务器
     */
    @POST(HttpUrlConstants.UPDATE_BLE_VERSION)
    Observable<BaseResult> updateBleVersion(@Body RequestBody info);


    /**
     * 上传版本号和SN到服务器
     */
    @POST(HttpUrlConstants.MODIFY_FUNCTION_SET)
    Observable<BaseResult> modifyFunctionSet(@Body RequestBody info);


    /**
     * OTA升级查询ApI
     */
    @POST(HttpUrlConstants.OTA_INFO_URL)
    Observable<CheckOTAResult> getOtaInfo(@Body RequestBody info);

    /**
     * OTA升级结果上报
     */
    @POST(HttpUrlConstants.OTA_RESULT_UPLOAD_URL)
    Observable<BaseResult> uploadOtaResult(@Body RequestBody info);


    ////////////////////////////////////////////           WiFi锁api功能            ///////////////////////////////////////////////

    /**
     * 绑定WiFi锁
     */
    @POST(HttpUrlConstants.WIFI_LOCK_BIND)
    Observable<BaseResult> wifiLockBind(@Body RequestBody info);


    /**
     * 解绑WiFi锁
     */
    @POST(HttpUrlConstants.WIFI_LOCK_UNBIND)
    Observable<BaseResult> wifiLockUnbind(@Body RequestBody info);


    /**
     * 修改WiFi锁昵称
     */
    @POST(HttpUrlConstants.WIFI_LOCK_UPDATE_NICK_NAME)
    Observable<BaseResult> wifiLockUpdateNickname(@Body RequestBody info);


    /**
     * 更改推送开关
     */
    @POST(HttpUrlConstants.WIFI_LOCK_UPDATE_PUSH)
    Observable<BaseResult> wifiLockUpdatePush(@Body RequestBody info);


    /**
     * 获取wifi锁密码列表
     */
    @POST(HttpUrlConstants.WIFI_LOCK_GET_PWD_LIST)
    Observable<WifiLockGetPasswordListResult> wifiLockGetPwdList(@Body RequestBody info);

    /**
     * 修改wifi密码昵称
     */
    @POST(HttpUrlConstants.WIFI_LOCK_UPDATE_PWD_NICKNAME)
    Observable<BaseResult> wifiLockUpdatePwdNickName(@Body RequestBody info);

    /**
     * wifi锁授权用户
     */
    @POST(HttpUrlConstants.WIFI_LOCK_SHARE)
    Observable<BaseResult> wifiLockShare(@Body RequestBody info);

    /**
     * wifi锁删除授权用户
     */
    @POST(HttpUrlConstants.WIFI_LOCK_DELETE_SHARE)
    Observable<BaseResult> wifiLockDeleteShareUser(@Body RequestBody info);

    /**
     * wifi锁修改授权用户昵称
     */
    @POST(HttpUrlConstants.WIFI_LOCK_UPDATE_SHARE_NICKNAME)
    Observable<BaseResult> wifiLockUpdateShareUserNickname(@Body RequestBody info);

    /**
     * wifi锁授权用户列表
     */
    @POST(HttpUrlConstants.WIFI_LOCK_GET_SHARE_LIST)
    Observable<WifiLockShareResult> wifiLockGetShareUserList(@Body RequestBody info);

    /**
     * 获取wifi锁操作记录
     */
    @POST(HttpUrlConstants.WIFI_LOCK_OPERATION_LIST)
    Observable<GetWifiLockOperationRecordResult> wifiLockGetOperationList(@Body RequestBody info);

    /**
     * 获取wifi报警记录
     */
    @POST(HttpUrlConstants.WIFI_LOCK_ALARM_LIST)
    Observable<GetWifiLockAlarmRecordResult> wifiLockGetAlarmList(@Body RequestBody info);
    /**
     * 开锁次数
     */
    @POST(HttpUrlConstants.WIFI_LOCK_OPEN_COUNT)
    Observable<GetOpenCountResult> wifiLockGetOpenCount(@Body RequestBody info);


    /**
     * 更新wifi信息
     */
    @POST(HttpUrlConstants.WIFI_LOCK_UPDATE_INFO)
    Observable<BaseResult> wifiLockUpdateInfo(@Body RequestBody info);



    /**
     * 更新wifi信息
     */
    @POST(HttpUrlConstants.WIFI_LOCK_UPLOAD_OTA)
    Observable<BaseResult> wifiLockUploadOta(@Body RequestBody info);


    /**
     * 获取二维码连接数据
     */
    @GET
    Observable<ResponseBody> getQrCodeContent(@Url String url);



    @POST(HttpUrlConstants.GET_ZIGBEEN_INFO)
    Observable<String> GET_ZIGBEEN_INFO_CONTEXT(@Body RequestBody info);

    /**
     * 单火开关设备昵称及开关修改
     *
     * @return
     */
    @POST(HttpUrlConstants.BINDING_AND_MODIFY_DEVICE_NICK)
    Observable<BaseResult> bindingAndModifyDeviceNick(@Body RequestBody info);
}
