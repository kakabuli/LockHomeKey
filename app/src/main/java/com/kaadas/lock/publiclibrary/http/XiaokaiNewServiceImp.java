package com.kaadas.lock.publiclibrary.http;

import com.kaadas.lock.bean.PhoneMessage;
import com.kaadas.lock.bean.PushBean;
import com.kaadas.lock.bean.PushSwitch;
import com.kaadas.lock.bean.PushSwitchBean;
import com.kaadas.lock.bean.VersionBean;
import com.kaadas.lock.publiclibrary.ble.bean.WarringRecord;
import com.kaadas.lock.publiclibrary.http.postbean.AddDeviceBean;
import com.kaadas.lock.publiclibrary.http.postbean.AddPasswordBean;
import com.kaadas.lock.publiclibrary.http.postbean.AddUserBean;
import com.kaadas.lock.publiclibrary.http.postbean.DeleteDeviceBean;
import com.kaadas.lock.publiclibrary.http.postbean.DeleteMessageBean;
import com.kaadas.lock.publiclibrary.http.postbean.DeletePasswordBean;
import com.kaadas.lock.publiclibrary.http.postbean.DeleteUserBean;
import com.kaadas.lock.publiclibrary.http.postbean.ForgetPasswordBean;
import com.kaadas.lock.publiclibrary.http.postbean.GetDevicesBean;
import com.kaadas.lock.publiclibrary.http.postbean.GetHelpLogBean;
import com.kaadas.lock.publiclibrary.http.postbean.GetLockRecordBean;
import com.kaadas.lock.publiclibrary.http.postbean.GetMessageBean;
import com.kaadas.lock.publiclibrary.http.postbean.GetOpenCountBean;
import com.kaadas.lock.publiclibrary.http.postbean.GetOperationRecordBean;
import com.kaadas.lock.publiclibrary.http.postbean.GetPasswordBean;
import com.kaadas.lock.publiclibrary.http.postbean.GetPwdBySNBean;
import com.kaadas.lock.publiclibrary.http.postbean.GetSinglePasswordBean;
import com.kaadas.lock.publiclibrary.http.postbean.GetUserNickBean;
import com.kaadas.lock.publiclibrary.http.postbean.GetUserProtocolContentBean;
import com.kaadas.lock.publiclibrary.http.postbean.GetWarringRecordBean;
import com.kaadas.lock.publiclibrary.http.postbean.LoginByEmailBean;
import com.kaadas.lock.publiclibrary.http.postbean.LoginByPhoneBean;
import com.kaadas.lock.publiclibrary.http.postbean.ModifyFunctionSetBean;
import com.kaadas.lock.publiclibrary.http.postbean.ModifyLockNickBean;
import com.kaadas.lock.publiclibrary.http.postbean.ModifyPasswordBean;
import com.kaadas.lock.publiclibrary.http.postbean.ModifyPasswordNickBean;
import com.kaadas.lock.publiclibrary.http.postbean.ModifySwitchNickBean;
import com.kaadas.lock.publiclibrary.http.postbean.ModifyUserNickBean;
import com.kaadas.lock.publiclibrary.http.postbean.OTABean;
import com.kaadas.lock.publiclibrary.http.postbean.PutMessageBean;
import com.kaadas.lock.publiclibrary.http.postbean.RegisterByPhoneBean;
import com.kaadas.lock.publiclibrary.http.postbean.ResetDeviceBean;
import com.kaadas.lock.publiclibrary.http.postbean.SearchUSerBean;
import com.kaadas.lock.publiclibrary.http.postbean.SendEmailBean;
import com.kaadas.lock.publiclibrary.http.postbean.SendMessageBean;
import com.kaadas.lock.publiclibrary.http.postbean.UpdateBleVersionBean;
import com.kaadas.lock.publiclibrary.http.postbean.UpdateSoftwareVersionBean;
import com.kaadas.lock.publiclibrary.http.postbean.UploadAppRecordBean;
import com.kaadas.lock.publiclibrary.http.postbean.UploadBinRecordBean;
import com.kaadas.lock.publiclibrary.http.postbean.UploadOperationRecordBean;
import com.kaadas.lock.publiclibrary.http.postbean.UploadOtaBean;
import com.kaadas.lock.publiclibrary.http.postbean.UploadOtaResultBean;
import com.kaadas.lock.publiclibrary.http.postbean.UploadWarringRecordBean;
import com.kaadas.lock.publiclibrary.http.postbean.WiFiLockBindBean;
import com.kaadas.lock.publiclibrary.http.postbean.WiFiLockVideoBindBean;
import com.kaadas.lock.publiclibrary.http.postbean.WiFiLockVideoUpdateBindBean;
import com.kaadas.lock.publiclibrary.http.postbean.WiFiLockViewBindFailBean;
import com.kaadas.lock.publiclibrary.http.postbean.WiFiLockWifiSNAndUid;
import com.kaadas.lock.publiclibrary.http.postbean.WiFiLockUpdateNickNameBean;
import com.kaadas.lock.publiclibrary.http.postbean.WifiLockDeleteShareBean;
import com.kaadas.lock.publiclibrary.http.postbean.WifiLockRecordBean;
import com.kaadas.lock.publiclibrary.http.postbean.WifiLockShareBean;
import com.kaadas.lock.publiclibrary.http.postbean.WifiLockUpdateInfoBean;
import com.kaadas.lock.publiclibrary.http.postbean.WifiLockUpdatePushSwitchBean;
import com.kaadas.lock.publiclibrary.http.postbean.WifiLockUpdatePwdNickBean;
import com.kaadas.lock.publiclibrary.http.postbean.WifiLockUpdateShareNickBean;
import com.kaadas.lock.publiclibrary.http.postbean.ZigBeenInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
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
import com.kaadas.lock.publiclibrary.http.temp.postbean.DeleteDeviceNormalUserBean;
import com.kaadas.lock.publiclibrary.http.temp.postbean.GetDeviceGeneralAdministratorBean;
import com.kaadas.lock.publiclibrary.http.temp.postbean.OpenLockAuth;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.publiclibrary.http.util.RetrofitServiceManager;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.BindingSingleFireSwitchBean;


import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.POST;


/**
 * Create By lxj  on 2019/2/27
 * Describe
 */
public class XiaokaiNewServiceImp {

    //////////////////////////////////////// 注册登陆////////////////////////////////////////

    /**
     * 通过手机号注册
     * name	    是	String	手机号码
     * password	是	String	密码
     * tokens	是	String	手机验证码
     *
     * @return
     */
    public static Observable<RegisterResult> registerByPhone(String name, String password, String tokens) {
        RegisterByPhoneBean registerByPhoneBean = new RegisterByPhoneBean(name, password, tokens);
        return RetrofitServiceManager.getNoTokenInstance().create(IXiaoKaiNewService.class)
                .registerByPhone(new HttpUtils<RegisterByPhoneBean>().getBody(registerByPhoneBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 通过手机号注册
     * name	    是	String	邮箱
     * password	是	String	密码
     * tokens	是	String	邮箱验证码
     *
     * @return
     */
    public static Observable<RegisterResult> registerByEmail(String name, String password, String tokens) {
        RegisterByPhoneBean registerByPhoneBean = new RegisterByPhoneBean(name, password, tokens);
        return RetrofitServiceManager.getNoTokenInstance().create(IXiaoKaiNewService.class)
                .registerByEmail(new HttpUtils<RegisterByPhoneBean>().getBody(registerByPhoneBean))
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * 通过手机号登陆
     * 参数名	必选	类型	说明
     * tel	    是	    String	手机号码
     * password	是	    String	密码
     *
     * @return
     */
    public static Observable<LoginResult> loginByPhone(String tel, String password) {
        LoginByPhoneBean loginByPhoneBean = new LoginByPhoneBean(tel, password);
        return RetrofitServiceManager.getNoTokenInstance().create(IXiaoKaiNewService.class)
                .loginByPhone(new HttpUtils<LoginByPhoneBean>().getBody(loginByPhoneBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 通过邮箱登陆
     * 参数名	必选	类型	说明
     * Email	    是	    String	邮箱
     * password	是	    String	密码
     *
     * @return
     */
    public static Observable<LoginResult> loginByEmail(String Email, String password) {
        LoginByEmailBean loginByEmailBean = new LoginByEmailBean(Email, password);
        return RetrofitServiceManager.getNoTokenInstance().create(IXiaoKaiNewService.class)
                .loginByEmail(new HttpUtils<LoginByEmailBean>().getBody(loginByEmailBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 忘记密码
     * 参数名	必选	类型	说明
     * name	    是	    String	手机号码
     * pwd	    是	    String	密码
     * type	    是	    int	类型：1
     * tokens	是	    string	手机验证码
     *
     * @return
     */
    public static Observable<BaseResult> forgetPassword(String name, String pwd, int type, String tokens) {
        ForgetPasswordBean forgetPasswordBean = new ForgetPasswordBean(name, pwd, type, tokens);
        return RetrofitServiceManager.getNoTokenInstance().create(IXiaoKaiNewService.class)
                .forgetPassword(new HttpUtils<ForgetPasswordBean>().getBody(forgetPasswordBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 修改密码   APP内  需要token
     * 参数名	必选	类型	说明
     * uid	    是	    String	用户ID
     * newpwd	是	    String	新密码
     * oldpwd	是	    String	旧密码
     *
     * @return
     */
    public static Observable<BaseResult> modifyPassword(String uid, String newpwd, String oldpwd) {
        ModifyPasswordBean modifyPasswordBean = new ModifyPasswordBean(uid, newpwd, oldpwd);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .modifyPassword(new HttpUtils<ModifyPasswordBean>().getBody(modifyPasswordBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 退出登陆
     *
     * @return
     */
    public static Observable<BaseResult> loginOut() {
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .loginOut()
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }


    //////////////////////////////////////////////用户管理/////////////////////////////////////////

    /**
     * 获取用户昵称
     * 参数名	必选	类型	说明
     * uid	    是	    String	用户ID
     *
     * @return
     */
    public static Observable<UserNickResult> getUserNick(String uid) {
        GetUserNickBean getUserNickBean = new GetUserNickBean(uid);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getUserNick(new HttpUtils<GetUserNickBean>().getBody(getUserNickBean))
                .compose(RxjavaHelper.observeOnMainThread());
    }


    /**
     * 修改用户昵称
     *
     * @return
     */
    public static Observable<BaseResult> modifyUserNick(String uid, String nickname) {
        ModifyUserNickBean modifyUserNickBean = new ModifyUserNickBean(uid, nickname);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .modifyUserNick(new HttpUtils<ModifyUserNickBean>().getBody(modifyUserNickBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }


    /**
     * 用户留言
     * 参数名	必选	类型	说明
     * uid	    是	    String	用户ID
     * suggest	是	    String	留言内容
     *
     * @return
     */
    public static Observable<BaseResult> putMessage(String uid, String suggest) {
        PutMessageBean putMessageBean = new PutMessageBean(uid, suggest);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .putMessage(new HttpUtils<PutMessageBean>().getBody(putMessageBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 获取指定设备的普通管理员
     */
    public static Observable<String> getDeviceGeneralAdministrator(String user_id, String devname) {
        GetDeviceGeneralAdministratorBean getDeviceGeneralAdministratorBean = new GetDeviceGeneralAdministratorBean(user_id, devname);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getDeviceGeneralAdministrator(new HttpUtils<GetDeviceGeneralAdministratorBean>().getBody(getDeviceGeneralAdministratorBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 删除设备的普通用户
     */
    public static Observable<BaseResult> deleteDeviceNormalUser(String adminid, String dev_username, String devname) {
        DeleteDeviceNormalUserBean deleteDeviceNormalUserBean = new DeleteDeviceNormalUserBean(adminid, dev_username, devname);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .deleteDeviceNormalUser(new HttpUtils<DeleteDeviceNormalUserBean>().getBody(deleteDeviceNormalUserBean))
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * 获取用户协议版本
     *
     * @return
     */
    public static Observable<UserProtocolVersionResult> getUserProtocolVersion() {
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getUserProtocolVersion()
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 获取用户协议
     * 参数名	    必选	类型	说明
     * protocolId	是	    String	协议版本ID
     *
     * @return
     */
    public static Observable<UserProtocolResult> getUserProtocolContent(String protocolId) {
        GetUserProtocolContentBean getUserProtocolContentBean = new GetUserProtocolContentBean(protocolId);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getUserProtocolContent(new HttpUtils<GetUserProtocolContentBean>().getBody(getUserProtocolContentBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    ////////////////////////////////////////////////门锁管理/////////////////////////////////////////////////////////////

    /**
     * 管理员添加设备
     * 参数名	必选	类型	说明
     * devmac	是	String	设备mac
     * devname	是	String	设备唯一编号
     * user_id	是	String	用户ID
     * password1	是	String	密码1
     * password2	是	String	密码2
     *
     * @return
     */
    public static Observable<BaseResult> addDevice(String devmac, String devname, String user_id, String password1, String password2, String model, String bleVersion, String deviceSn, String functionSet) {
        //(String devmac, String devname, String user_id, String password1, String password2, String model, String bleVersion, String deviceSN, String peripheralId, String softwareVersion, String functionSet)
        AddDeviceBean addDeviceBean = new AddDeviceBean(devmac, devname, user_id, password1, password2, model, bleVersion, deviceSn, "", "", functionSet);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .addDevice(new HttpUtils<AddDeviceBean>().getBody(addDeviceBean))
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * 查询列表设备列表
     * 参数名	必选	类型	说明
     * user_id	是	    String	用户ID
     *
     * @return
     */
    public static Observable<GetDeviceResult> getDevices(String user_id) {
        GetDevicesBean getDevicesBean = new GetDevicesBean(user_id);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getDevices(new HttpUtils<GetDevicesBean>().getBody(getDevicesBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 删除设备
     * 参数名	必选	类型	说明
     * adminid	是	    String	用户ID
     * devname	是	    String	设备唯一编号
     *
     * @return
     */
    public static Observable<BaseResult> deleteDevice(String adminid, String devname) {
        DeleteDeviceBean deleteDeviceBean = new DeleteDeviceBean(adminid, devname);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .deleteDevice(new HttpUtils<DeleteDeviceBean>().getBody(deleteDeviceBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 获取操作记录
     * 参数名	    必选	类型	说明
     * device_name	是	    String	设备SN
     * pagenum      页数
     *
     * @return
     */
    public static Observable<OperationRecordResult> getOperationRecord(String device_name, int pagenum) {
        GetOperationRecordBean getOperationRecordBean = new GetOperationRecordBean(device_name, pagenum);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getOperationRecord(new HttpUtils<GetOperationRecordBean>().getBody(getOperationRecordBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 修改门锁昵称
     * 参数名	必选	类型	说明
     * devname	是	    String	设备唯一编号
     * user_id	是	    String	用户ID
     * nickName	是	    String	设备昵称
     *
     * @return
     */
    public static Observable<BaseResult> modifyLockNick(String devname, String user_id, String lockNickName) {
        ModifyLockNickBean modifyLockNickBean = new ModifyLockNickBean(devname, user_id, lockNickName);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .modifyLockNick(new HttpUtils<ModifyLockNickBean>().getBody(modifyLockNickBean))
                .compose(RxjavaHelper.observeOnMainThread());

    }


    /**
     * 根据SN获取蓝牙密码
     * 参数名	必选	类型	说明
     * SN	    是	    String	设备唯一编号
     *
     * @return
     */
    public static Observable<GetPwdBySnResult> getPwdBySn(String SN) {
        GetPwdBySNBean getPwdBySNBean = new GetPwdBySNBean(SN);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getpwdBySN(new HttpUtils<GetPwdBySNBean>().getBody(getPwdBySNBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;

    }


    /**
     * 三方重置门锁   解绑门锁
     * 参数名	必选	类型	说明
     * devname	是	    String	设备SN
     * adminid	是	    String	管理员用户ID
     *
     * @return
     */
    public static Observable<BaseResult> resetDevice(String adminid, String devname) {
        ResetDeviceBean resetDeviceBean = new ResetDeviceBean(adminid, devname);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .resetDevice(new HttpUtils<ResetDeviceBean>().getBody(resetDeviceBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;

    }

    /**
     * 门锁密钥添加
     * 参数名	    必选	类型	说明
     * uid	        是	    String	管理员用户ID
     * devName	    是	    String	设备唯一编号
     * pwdType	    是	    int 	密钥类型：1密码 2临时密码 3指纹密码 4卡片密码
     * num	        是	    String	密钥编号
     * nickName	    是	    String	密钥昵称
     * type 	    否	    int 	密钥周期类型：1永久 2时间段 3周期 4 24小时
     * startTime	否	    timestamp	时间段密钥开始时间
     * endTime	    否	    timestamp	时间段密钥结束时间
     * items	    否	    list	周期密码星期几
     *
     * @return
     */
    public static Observable<BaseResult> addPassword(
            String uid, String devName, List<AddPasswordBean.Password> pwdList) {
        AddPasswordBean addPasswordBean = new AddPasswordBean(uid, devName, pwdList);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .addPassword(new HttpUtils<AddPasswordBean>().getBody(addPasswordBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;

    }

    /**
     * 删除秘钥
     * 参数名	必选	类型	说明
     * uid	    是	    String	管理员用户ID
     * devName	是	    String	设备唯一编号
     * pwdType	是	    String	密钥类型：1永久密码 2临时密码 3指纹密码 4卡片密码
     * num	    是	    String	密钥编号
     *
     * @return
     */
    public static Observable<BaseResult> deletePassword(String uid, String devName, List<DeletePasswordBean.DeletePassword> pwdList) {
        DeletePasswordBean deletePasswordBean = new DeletePasswordBean(uid, devName, pwdList);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .deletePassword(new HttpUtils<DeletePasswordBean>().getBody(deletePasswordBean))
                .compose(RxjavaHelper.observeOnMainThread());

    }

    /**
     * 修改秘钥昵称
     * 参数名	必选	类型	说明
     * uid	    是	    String	管理员密钥昵称
     * devName	是	    String	设备密钥昵称
     * pwdType	是	    String	密钥类型：1永久密码 2临时密码 3指纹密码 4密钥昵称
     * num	    是	    String	密钥昵称
     * nickName	是	    String	密钥昵称
     *
     * @return
     */
    public static Observable<BaseResult> modifyPasswordNick(String uid, String devName, int pwdType, String num, String nickName) {
//        String uid, String devName, int pwdType, String num, String nickName
        ModifyPasswordNickBean modifyPasswordNickBean = new ModifyPasswordNickBean(uid, devName, pwdType, num, nickName);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .modifyPasswordNick(new HttpUtils<ModifyPasswordNickBean>().getBody(modifyPasswordNickBean))
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * 查询秘钥列表
     * 参数名	必选	类型	说明
     * uid	    是	    String	管理员用户ID
     * devName	是	    String	设备唯一编号
     * pwdType	是	    String	密钥类型：0所有密码 1永久密码 2临时密码 3指纹密码 4卡片密码
     *
     * @return
     */
    public static Observable<GetPasswordResult> getPasswords(String uid, String devName, int pwdType) {
        GetPasswordBean getPasswordBean = new GetPasswordBean(uid, devName, pwdType);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getPasswords(new HttpUtils<GetPasswordBean>().getBody(getPasswordBean))
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * 查询单个秘钥
     * 参数名	必选	类型	说明
     * uid	是	String	管理员用户ID
     * devName	是	String	设备唯一编号
     * pwdType	是	String	密钥类型：1永久密码 2临时密码 3指纹密码 4卡片密码
     * num	是	String	密钥编号
     * nickName	是	String	密钥昵称
     *
     * @return
     */

    public static Observable<SinglePasswordResult> getSinglePassword(String uid, String devName, int pwdType, String num) {
        GetSinglePasswordBean getSinglePasswordBean = new GetSinglePasswordBean(uid, devName, pwdType, num);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getSinglePassword(new HttpUtils<GetSinglePasswordBean>().getBody(getSinglePasswordBean))
                .compose(RxjavaHelper.observeOnMainThread());
    }

    ////////////////////////////////////////////用户管理////////////////////////////////////////////////

    /**
     * 添加普通用户
     *
     * @param admin_id        管理员用户ID
     * @param device_username 要分享的用户账号（手机号）
     * @param devicemac
     * @param devname
     * @param end_time
     * @param lockNickName    门锁昵称
     * @param open_purview    类型：3永久
     * @param start_time
     * @param items
     * @return
     */
    public static Observable<BaseResult>
    addUser(String admin_id, String device_username, String devicemac, String devname, String end_time, String lockNickName,
            String open_purview, String start_time, List<String> items) {
        AddUserBean addUserBean = new AddUserBean(admin_id, device_username, devicemac, devname, end_time, lockNickName, open_purview, start_time, items);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .addUser(new HttpUtils<AddUserBean>().getBody(addUserBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;

    }

    /**
     * 更改普通用户昵称
     *
     * @param ndId         用户-设备关联ID
     * @param userNickName 用户昵称
     * @return
     */
    public static Observable<BaseResult>
    modifyCommonUserNickname(String ndId, String userNickName) {
        ModifyCommonUserNicknameBean modifyCommonUserNicknameBean = new ModifyCommonUserNicknameBean(ndId, userNickName);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .modifyCommonUserNickname(new HttpUtils<ModifyCommonUserNicknameBean>().getBody(modifyCommonUserNicknameBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 删除普通用户
     * 参数名	        必选	类型	说明
     * admin_id	        是	    String	管理员用户ID
     * device_username	是	    String	要分享的用户账号（手机号）
     * devname	        是	    String	设备SN
     *
     * @return
     */
    public static Observable<BaseResult> deleteUser(String dev_username, String adminid, String devname) {
        DeleteUserBean deleteUserBean = new DeleteUserBean(dev_username, adminid, devname);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .deleteUser(new HttpUtils<DeleteUserBean>().getBody(deleteUserBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;

    }

    /**
     * 查询普通用户列表
     * @return
     */
    public static Observable<BaseResult> searchUser(String devname, String user_id) {
        SearchUSerBean searchUSerBean = new SearchUSerBean(devname, user_id);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .searchUser(new HttpUtils<SearchUSerBean>().getBody(searchUSerBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;

    }


    //////////////////////////////////////开门记录///////////////////////////////////////////////

    /**
     * 上传秘钥开门记录
     * 参数名	        必选	类型	说明
     * device_name	    是	    String	设备SN
     * device_nickname	是	    String	门锁昵称
     * user_id	        是	    String	上传用户ID
     * openLockList	是	    JsonArray
     * open_time	    是	    String	开门时间
     * open_type	    是	    String	开门类型
     * user_num	    是	    String	密钥编号
     * nickName	    是	    String	密钥昵称
     *
     * @return
     */
    public static Observable<BaseResult> uploadBinRecord(String device_name, String device_nickname,
                                                         String user_id, List<UploadBinRecordBean.OpenLockRecordBle> openLockList) {
        UploadBinRecordBean uploadBinRecordBean = new UploadBinRecordBean(device_name, device_nickname, user_id, openLockList);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .uploadBinRecord(new HttpUtils<UploadBinRecordBean>().getBody(uploadBinRecordBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;

    }

    /**
     * 上传App开门记录
     * 参数名	    必选	类型	说明
     * devname	    是	    String	设备唯一编号
     * is_admin	    是	    String	是否是管理员：0否 1是
     * open_type	是	    String	开门类型：200
     * user_id	    是	    String	用户ID
     * nickName	    是	    String	用户昵称
     *
     * @return
     */
    public static Observable<BaseResult> uploadAppRecord(String devname, String is_admin, String open_type, String user_id, String nickName) {
        UploadAppRecordBean uploadAppRecordBean = new UploadAppRecordBean(devname, is_admin, open_type, user_id, nickName);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .uploadAppRecord(new HttpUtils<UploadAppRecordBean>().getBody(uploadAppRecordBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;

    }

    /**
     * 获取开门记录
     * 参数名	    必选	类型	说明
     * device_name	是	    String	设备SN
     * user_id	    是	    String	用户ID
     * open_type	否	    String	开门类型：null(查询所有) "200"(APP开锁) "密码" "指纹" "卡片"
     *
     * @return
     */
    public static Observable<LockRecordResult> getLockRecord(String device_name, String user_id, String openType, String pagenum) {
        GetLockRecordBean getLockRecordBean = new GetLockRecordBean(device_name, user_id, openType, pagenum);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getLockRecord(new HttpUtils<GetLockRecordBean>().getBody(getLockRecordBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }


    /**
     * 获取预警记录
     * 参数名	必选	类型	说明
     * devName	是	    String	设备唯一编号
     * pageNum	是	    int	    第几页
     *
     * @return
     */
    public static Observable<GetWarringRecordResult> getWarringRecord(String devName, int pageNum) {
        GetWarringRecordBean getWarringRecordBean = new GetWarringRecordBean(devName, pageNum);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getWarringRecord(new HttpUtils<GetWarringRecordBean>().getBody(getWarringRecordBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;

    }

    /**
     * 发送短信验证码
     * 参数名	必选	类型	说明
     * tel	    是	    String	手机号码
     * code	    是	    String	区号：86中国
     *
     * @return
     */
    public static Observable<BaseResult> sendMessage(String tel, String code) {
        SendMessageBean sendMessageBean = new SendMessageBean(tel, code);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .sendMessage(new HttpUtils<SendMessageBean>().getBody(sendMessageBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;

    }

    /**
     * 发送邮箱验证码
     * 参数名	必选	类型	说明
     * email	    是	    String	邮箱
     *
     * @return
     */
    public static Observable<BaseResult> sendEmailYZM(String email) {
        SendEmailBean sendEmailBean = new SendEmailBean(email);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .sendEamilYZM(new HttpUtils<SendEmailBean>().getBody(sendEmailBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;

    }

    /**
     * 上传头像
     * uid  用户唯一标识
     * path 图片路径
     */

    public static Observable<BaseResult> uploadPic(String uid, String path) {
        File file = new File(path);
        RequestBody uidBody = RequestBody.create(MediaType.parse("multipart/form-data"), uid);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class).getPicturesBean(uidBody, filePart)
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }


    /**
     * 下载头像
     */
    public static Observable<ResponseBody> downloadUserHead(String uid) {
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .downloadUserHead(uid)
                .subscribeOn(Schedulers.io());
//                .compose(RxjavaHelper.observeOnMainThread())
//                .subscribe();

    }

    /**
     * 常见问题列表
     */
    public static Observable<String> getFaqList(int languageType) {
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getFAQList(languageType)
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());

    }

    /**
     * 系统消息
     */
    public static Observable<String> getMessageList(String uid, int page) {
        GetMessageBean bean = new GetMessageBean(uid, page);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getMessageList(new HttpUtils<GetMessageBean>().getBody(bean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * 删除系统消息
     */
    public static Observable<DeleteMessageResult> deleteMessage(String uid, String mid) {
        DeleteMessageBean messageBean = new DeleteMessageBean(uid, mid);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .deleteMessage(new HttpUtils<DeleteMessageBean>().getBody(messageBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * 查询帮助日志
     */
    public static Observable<GetHelpLogResult> getHelpLog(String uid, int page) {
        GetHelpLogBean helpLogBean = new GetHelpLogBean(uid, page);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getHelpLog(new HttpUtils<GetHelpLogBean>().getBody(helpLogBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * 请求App版本
     */
    public static Observable<VersionBean> getAppVersion() {
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getAppVersion()
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }


    /**
     * 上报预警记录
     * 参数名	    必选	类型	说明
     * devName	    是	    String	设备唯一编号
     * warningList	是	    JsonArray
     * warningType	是	    int	预警类型：1低电量 2钥匙开门 3验证错误 4防撬提醒 5即时性推送消息
     * warningTime	是	    timestamp（s）	预警时间
     * content	    否	    String	预警内容
     *
     * @return
     */
    public static Observable<BaseResult> uploadWarringRecord(String devName, List<WarringRecord> warningList) {
        UploadWarringRecordBean uploadWarringRecordBean = new UploadWarringRecordBean(devName, warningList);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .uploadWarringRecord(new HttpUtils<UploadWarringRecordBean>().getBody(uploadWarringRecordBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;

    }

    public static Observable<BaseResult> openLockAuth(String devname, String is_admin, String open_type, String user_id) {
        OpenLockAuth openLockAuth = new OpenLockAuth(devname, is_admin, open_type, user_id);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .openLockAuth(new HttpUtils<OpenLockAuth>().getBody(openLockAuth))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    public static Observable<BaseResult> uploadPushId(String uid, String jpushId, int type) {
        PushBean pushBean = new PushBean(uid, jpushId, type);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .uploadPushId(new HttpUtils<PushBean>().getBody(pushBean))
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * upload phone message
     *
     * @param uid
     * @param account
     * @param model        手机型号  SM-C7000
     * @param manufacturer 手机厂商 samsung
     * @param version      Android系统版本号 android8.0
     * @return
     */
    public static Observable<BaseResult> uploadPushPhoneMsg(String uid, String account, String model, String manufacturer, String version) {
        PhoneMessage phoneMessage = new PhoneMessage(uid, account, model, manufacturer, version);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .uploadPushMsg(new HttpUtils<PhoneMessage>().getBody(phoneMessage))
                .compose(RxjavaHelper.observeOnMainThread());
    }


    public static Observable<SwitchStatusResult> getPushSwitch(String uid) {
        PushSwitch pushSwitch = new PushSwitch(uid);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getPushSwitch(new HttpUtils<PushSwitch>().getBody(pushSwitch))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }


    public static Observable<SwitchStatusResult> updatePushSwitch(String uid, boolean openlockPushSwitch) {
        PushSwitchBean pushSwitchBean = new PushSwitchBean(uid, openlockPushSwitch);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .updatePushSwitch(new HttpUtils<PushSwitchBean>().getBody(pushSwitchBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * {
     * "devName":"BT01191910010",
     * "operationList":[
     * {
     * "uid":"5c6fb4d014fd214910b33e80",
     * "eventType":4,
     * "eventSource":3,
     * "eventCode":3,
     * "userNum":4,
     * "eventTime":"2019-07-04 12:00:03"
     * }
     * ]
     * }
     */
    public static Observable<BaseResult> uploadOperationRecord(String device_name, List<UploadOperationRecordBean.OperationListBean> operationListBeanList) {
        UploadOperationRecordBean uploadOperationRecordBean = new UploadOperationRecordBean(device_name, operationListBeanList);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .uploadOperationRecord(new HttpUtils<UploadOperationRecordBean>().getBody(uploadOperationRecordBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;

    }

    /**
     * 更新蓝牙的版本信息
     *
     * @param devname         设备名
     * @param user_id
     * @param softwareVersion 蓝牙软件版本号
     * @param deviceSN        蓝牙的SN
     * @return
     */
    public static Observable<BaseResult> updateSoftwareVersion(String devname, String user_id, String softwareVersion, String deviceSN) {
        UpdateSoftwareVersionBean updateSoftwareVersionBean = new UpdateSoftwareVersionBean(devname, user_id, softwareVersion, deviceSN, "");
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .updateSoftwareVersion(new HttpUtils<UpdateSoftwareVersionBean>().getBody(updateSoftwareVersionBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }


    /**
     * 更新蓝牙的版本号
     *
     * @param devname
     * @param user_id
     * @param bleVersion
     * @return
     */
    public static Observable<BaseResult> updateBleVersion(String devname, String user_id, String bleVersion) {
        UpdateBleVersionBean updateSoftwareVersionBean = new UpdateBleVersionBean(devname, user_id, bleVersion);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .updateBleVersion(new HttpUtils<UpdateBleVersionBean>().getBody(updateSoftwareVersionBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }


    /**
     * 更新功能集
     *
     * @param devname
     * @param user_id
     * @param functionSet
     * @return
     */
    public static Observable<BaseResult> modifyFunctionSet(String devname, String user_id, String functionSet) {
        ModifyFunctionSetBean updateSoftwareVersionBean = new ModifyFunctionSetBean(devname, user_id, functionSet);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .modifyFunctionSet(new HttpUtils<ModifyFunctionSetBean>().getBody(updateSoftwareVersionBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }


    /**
     * @param customer   是	int	客户：1凯迪仕 2小凯 3桔子物联 4飞利浦
     * @param deviceName 是	String	设备唯一编号
     * @param version    否	String	当前版本号
     * @param type       1主模块 2算法模块 3相机模块（空：默认1） 4协议栈
     * @return
     */
    public static Observable<CheckOTAResult> getOtaInfo(int customer, String deviceName, String version, int type) {
        OTABean helpLogBean = new OTABean(customer, deviceName, version, type);
        return RetrofitServiceManager.getNoTokenInstance().create(IXiaoKaiNewService.class)
                .getOtaInfo(new HttpUtils<OTABean>().getBody(helpLogBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }


    /**
     * @param sn
     * @param version    新版本的版本号
     * @param customer   客户：1凯迪仕 2小凯 3桔子物联 4飞利浦
     * @param resultCode 结果：0升级成功 1升级失败 （可自定义其他错误码）
     * @param devNum     模块：1主模块 2算法模块 3相机模块（空：默认1）
     * @return
     */
    public static Observable<BaseResult> uploadOtaResult(String sn, String version, int customer, String resultCode, int devNum) {
        UploadOtaResultBean uploadOtaResultBean = new UploadOtaResultBean(sn, version, customer, resultCode, devNum);
        return RetrofitServiceManager.getNoTokenInstance().create(IXiaoKaiNewService.class)
                .uploadOtaResult(new HttpUtils<UploadOtaResultBean>().getBody(uploadOtaResultBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }


    ////////////////////////////////////////////           WiFi锁api功能            ///////////////////////////////////////////////

    /**
     * 绑定WiFi锁
     *
     * @param wifiSN       是	String	wifi模块SN
     * @param lockNickName 否	String	门锁昵称
     * @param uid          是	String	用户ID
     * @return
     */
    public static Observable<BaseResult> wifiLockBind(String wifiSN, String lockNickName, String uid, String randomCode, String wifiName,int func, int distributionNetwork) {
        WiFiLockBindBean wiFiLockBindBean = new WiFiLockBindBean(wifiSN, lockNickName, uid, randomCode, wifiName,func, distributionNetwork);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiLockBind(new HttpUtils<WiFiLockBindBean>().getBody(wiFiLockBindBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * 解绑WiFi锁
     *
     * @param wifiSN
     * @param uid
     * @return
     */
    public static Observable<BaseResult> wifiLockUnbind(String wifiSN, String uid) {
        WiFiLockWifiSNAndUid wiFiLockUnbind = new WiFiLockWifiSNAndUid(wifiSN, uid);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiLockUnbind(new HttpUtils<WiFiLockWifiSNAndUid>().getBody(wiFiLockUnbind))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * 修改WiFi锁昵称
     *
     * @param wifiSN
     * @param uid
     * @param lockNickname
     * @return
     */
    public static Observable<BaseResult> wifiLockUpdateNickname(String wifiSN, String uid, String lockNickname) {
        WiFiLockUpdateNickNameBean wiFiLockUpdateNickNameBean = new WiFiLockUpdateNickNameBean(wifiSN, uid, lockNickname);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiLockUpdateNickname(new HttpUtils<WiFiLockUpdateNickNameBean>().getBody(wiFiLockUpdateNickNameBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * 更改推送开关
     *
     * @param wifiSN
     * @param uid
     * @param switchX 推送开关：1关 2开
     * @return
     */
    public static Observable<BaseResult> wifiLockUpdatePush(String wifiSN, String uid, int switchX) {
        WifiLockUpdatePushSwitchBean wifiLockUpdatePushSwitchBean = new WifiLockUpdatePushSwitchBean(wifiSN, uid, switchX);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiLockUpdatePush(new HttpUtils<WifiLockUpdatePushSwitchBean>().getBody(wifiLockUpdatePushSwitchBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * @param wifiSN
     * @param uid
     * @return
     */
    public static Observable<WifiLockGetPasswordListResult> wifiLockGetPwdList(String wifiSN, String uid) {
        WiFiLockWifiSNAndUid wiFiLockUnbind = new WiFiLockWifiSNAndUid(wifiSN, uid);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiLockGetPwdList(new HttpUtils<WiFiLockWifiSNAndUid>().getBody(wiFiLockUnbind))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * 修改wifi密码昵称
     *
     * @param uid      是	String	管理员用户ID
     * @param wifiSN   是	String	设备唯一编号
     * @param pwdType  是	String	密钥类型：1密码 2指纹密码 3卡片密码
     * @param num      是	int	密钥编号
     * @param nickName 是	String	密钥昵称
     * @return
     */
    public static Observable<BaseResult> wifiLockUpdatePwdNickName(String uid, String wifiSN, int pwdType, int num, String nickName) {
        WifiLockUpdatePwdNickBean wifiLockUpdatePwdNickBean = new WifiLockUpdatePwdNickBean(uid, wifiSN, pwdType, num, nickName);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiLockUpdatePwdNickName(new HttpUtils<WifiLockUpdatePwdNickBean>().getBody(wifiLockUpdatePwdNickBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * 修改wifi密码昵称
     *
     * @param uid      是	String	管理员用户ID
     * @param wifiSN   是	String	设备唯一编号
     * @param pwdType  是	String	密钥类型：1密码 2指纹密码 3卡片密码 4面容识别
     * @param num      是	int	密钥编号
     * @param nickName 是	String	密钥昵称
     * @return
     */
    public static Observable<BaseResult> wifiLockUpdatePwdNickName(String uid, String wifiSN, int pwdType, int num, String nickName,String userNickName) {
        WifiLockUpdatePwdNickBean wifiLockUpdatePwdNickBean = new WifiLockUpdatePwdNickBean(uid, wifiSN, pwdType, num, nickName,userNickName);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiLockUpdatePwdNickName(new HttpUtils<WifiLockUpdatePwdNickBean>().getBody(wifiLockUpdatePwdNickBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }


    /**
     * 分享设备
     *
     * @param wifiSN       是	String	wifi模块SN
     * @param uid          是	String	用户ID
     * @param uname        是	String	分享账号
     * @param userNickname 是	String	分享用户昵称
     * @return
     */
    public static Observable<BaseResult> wifiLockAddShareUser(String wifiSN, String uid, String uname, String userNickname, String adminNickname) {
        WifiLockShareBean wifiLockShareBean = new WifiLockShareBean(wifiSN, uid, uname, userNickname, adminNickname);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiLockShare(new HttpUtils<WifiLockShareBean>().getBody(wifiLockShareBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * wifi锁删除授权用户
     *
     * @param shareId 是	String	用户设备关联ID
     * @param uid     是	String	用户ID
     * @return
     */
    public static Observable<BaseResult> wifiLockDeleteShareUser(String shareId, String uid, String adminNickname) {
        WifiLockDeleteShareBean wifiLockDeleteShareBean = new WifiLockDeleteShareBean(shareId, uid, adminNickname);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiLockDeleteShareUser(new HttpUtils<WifiLockDeleteShareBean>().getBody(wifiLockDeleteShareBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * wifi锁修改授权用户昵称
     *
     * @param shareId
     * @param nickName
     * @return
     */
    public static Observable<BaseResult> wifiLockUpdateShareUserNickname(String shareId, String nickName, String uid) {
        WifiLockUpdateShareNickBean wifiLockUpdateShareNickBean = new WifiLockUpdateShareNickBean(shareId, nickName, uid);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiLockUpdateShareUserNickname(new HttpUtils<WifiLockUpdateShareNickBean>().getBody(wifiLockUpdateShareNickBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * wifi锁授权用户列表
     *
     * @param wifiSN
     * @param uid
     * @return
     */
    public static Observable<WifiLockShareResult> wifiLockGetShareUserList(String wifiSN, String uid) {
        WiFiLockWifiSNAndUid wiFiLockWifiSNAndUid = new WiFiLockWifiSNAndUid(wifiSN, uid);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiLockGetShareUserList(new HttpUtils<WiFiLockWifiSNAndUid>().getBody(wiFiLockWifiSNAndUid))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * 获取wifi锁操作记录
     *
     * @param wifiSN
     * @param page
     * @return
     */
    public static Observable<GetWifiLockOperationRecordResult> wifiLockGetOperationList(String wifiSN, int page) {
        WifiLockRecordBean wiFiLockWifiSNAndUid = new WifiLockRecordBean(wifiSN, page);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiLockGetOperationList(new HttpUtils<WifiLockRecordBean>().getBody(wiFiLockWifiSNAndUid))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }


    /**
     * 获取wifi报警记录
     */

    public static Observable<GetWifiLockAlarmRecordResult> wifiLockGetAlarmList(String wifiSN, int page) {
        WifiLockRecordBean wiFiLockWifiSNAndUid = new WifiLockRecordBean(wifiSN, page);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiLockGetAlarmList(new HttpUtils<WifiLockRecordBean>().getBody(wiFiLockWifiSNAndUid))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * 开锁次数
     */

    public static Observable<GetOpenCountResult> wifiLockGetOpenCount(String wifiSN) {
        GetOpenCountBean getOpenCountBean = new GetOpenCountBean(wifiSN);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiLockGetOpenCount(new HttpUtils<GetOpenCountBean>().getBody(getOpenCountBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * @param uid
     * @param wifiSN
     * @param randomCode
     * @return
     */
    public static Observable<BaseResult> wifiLockUpdateInfo(String uid, String wifiSN, String randomCode, String wifiName,int func) {
        WifiLockUpdateInfoBean updateInfoBean = new WifiLockUpdateInfoBean(uid, wifiSN, randomCode, wifiName,func);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiLockUpdateInfo(new HttpUtils<WifiLockUpdateInfoBean>().getBody(updateInfoBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }



    /**
     * @return
     */
    public static Observable<BaseResult> wifiLockUploadOta( CheckOTAResult.UpdateFileInfo updateFileInfo,String wifiSN) {
        UploadOtaBean uploadOtaBean = new UploadOtaBean(  wifiSN, updateFileInfo.getFileLen(), updateFileInfo .getFileUrl(),
                updateFileInfo.getFileMd5(),updateFileInfo.getDevNum(), updateFileInfo.getFileVersion());
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiLockUploadOta(new HttpUtils<UploadOtaBean>().getBody(uploadOtaBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }


    /**
     * @return
     */
    public static Observable<ResponseBody> getQrCodeContent(String url) {
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .getQrCodeContent(url)
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }


    /**
     * @return
     */
    public static Observable<String> getZIGBEENINFO(String uid,String gwSN,String zigbeeSN) {
        ZigBeenInfo uploadOtaBean = new ZigBeenInfo(  zigbeeSN, uid, gwSN);

        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .GET_ZIGBEEN_INFO_CONTEXT(new HttpUtils<ZigBeenInfo>().getBody(uploadOtaBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     * 单火开关设备联动开关昵称修改
     *
     * @return
     */
    public static Observable<BaseResult> updateSwitchNickname(ModifySwitchNickBean modifySwitchNickBean) {

        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .updateSwitchNickname(new HttpUtils<ModifySwitchNickBean>().getBody(modifySwitchNickBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }


    ////////////////////////////////////////////           WiFi视频锁api功能            ///////////////////////////////////////////////

    /**
     * 绑定视频锁
     */
    public static Observable<WifiLockVideoBindResult> wifiVideoLockBind(WiFiLockVideoBindBean wiFiLockBindBean) {
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiVideoLockBind(new HttpUtils<WiFiLockVideoBindBean>().getBody(wiFiLockBindBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     *  解绑视频锁
     */
    public static Observable<WifiLockVideoBindResult> wifiVideoLockUnbind(String wifiSN, String uid) {
        WiFiLockWifiSNAndUid wiFiLockUnbind = new WiFiLockWifiSNAndUid(wifiSN, uid);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiVideoLockUnbind(new HttpUtils<WiFiLockWifiSNAndUid>().getBody(wiFiLockUnbind))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     *  绑定视频锁失败
     */
    public static Observable<WifiLockVideoBindResult> wifiVideoLockBindFail(String wifiSN, int result) {
        WiFiLockViewBindFailBean wiFiLockUnbind = new WiFiLockViewBindFailBean(wifiSN, result);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiVideoLockBindFail(new HttpUtils<WiFiLockViewBindFailBean>().getBody(wiFiLockUnbind))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     *  更新视频锁
     */
    public static Observable<WifiLockVideoBindResult> wifiVideoLockUpdateBind(WiFiLockVideoUpdateBindBean wiFiLockBindBean) {
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiVideoLockUpdateBind(new HttpUtils<WiFiLockVideoUpdateBindBean>().getBody(wiFiLockBindBean))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     *  视频锁分页查询报警记录
     */
    public static Observable<GetWifiVideoLockAlarmRecordResult> wifiVideoLockGetAlarmList(String wifiSn, int page) {
        WifiLockRecordBean wiFiLockWifiSNAndUid = new WifiLockRecordBean(wifiSn, page);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiVideoLockGetAlarmList(new HttpUtils<WifiLockRecordBean>().getBody(wiFiLockWifiSNAndUid))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     *  视频锁分页查询门铃记录
     */
    public static Observable<GetWifiVideoLockAlarmRecordResult> wifiVideoLockGetDoorbellList(String wifiSn,int page){
        WifiLockRecordBean wiFiLockWifiSNAndUid = new WifiLockRecordBean(wifiSn, page);
        return RetrofitServiceManager.getInstance().create(IXiaoKaiNewService.class)
                .wifiVideoLockGetDoorbellList(new HttpUtils<WifiLockRecordBean>().getBody(wiFiLockWifiSNAndUid))
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread());
    }

    /**
     *  查询wifi锁设备列表
     */
    public static Observable<WifiDeviceListResult> wifiDeviceLisst(String wifiSn, int page){
        WifiLockRecordBean wiFiLockWifiSNAndUid = new WifiLockRecordBean(wifiSn, page);
        return null;
    }
}
