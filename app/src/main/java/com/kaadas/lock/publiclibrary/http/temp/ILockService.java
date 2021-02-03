package com.kaadas.lock.publiclibrary.http.temp;


import com.kaadas.lock.publiclibrary.http.temp.resultbean.CheckBindResult;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Create By lxj  on 2019/1/10
 * Describe
 */
@Deprecated
public interface ILockService {

    /**
     * 通过Sn获取pwd1
     * @return
     */
    @POST(HttpConstants.GET_PSW_MAC)
    Observable<BaseResponse<String>> getPwd1BySN(@Body RequestBody info);


    /**
     * 检查所是否绑定
     * @return
     */
    @POST(HttpConstants.CHECK_LOCK_BIND)
    Observable<Response<CheckBindResult>> checkLockBind(@Body RequestBody info);


    /**
     * 绑定锁
     * @return
     */
    @POST(HttpConstants.CREATE_LOCK_ADMIN)
    Observable<BaseResponse<String>> bindDevice(@Body RequestBody info);


    /**
     * 解绑蓝牙设备
     * @return
     */
    @POST(HttpConstants.RESET_LOCK_ALL_ADMIN)
    Observable<BaseResponse<String>> unbindDevice(@Body RequestBody info);

    /**
     * 获取用户绑定的锁列表
     * @return
     */
    @POST(HttpConstants.GET_LOCK_LIST)
    Observable<BaseResponse<String>> getAllLocks(@Body RequestBody info);


    /**
     * 开锁鉴权
     */
    @POST(HttpConstants.USER_OPEN_LOCK_AUTHORITY)
    Observable<BaseResponse<String>> openLockAuth(@Body RequestBody info);

    /**
     * 删除锁
     */
    @POST(HttpConstants.RESET_LOCK_ADMIN)
    Observable<BaseResponse<String>> deleteLock(@Body RequestBody info);

    /**
     * 管理员开锁
     */
    @POST(HttpConstants.OPEN_LOCK)
    Observable<BaseResponse<String>> administratorOpenLock(@Body RequestBody info);
    /**
     * 修改设备昵称
     */
    @POST(HttpConstants.UPDATE_LOCK_NICKNAME)
    Observable<BaseResponse<String>> modifyDeviceNickname(@Body RequestBody info);
    /**
     * 获取设备经纬度
     */
    @POST(HttpConstants.GET_LOCTION)
    Observable<BaseResponse<String>> getDeviceLatitudeAndLogitude(@Body RequestBody info);
    /**
     * 获取指定设备的普通管理员
     */
    @POST(HttpConstants.GET_NORMALS_DEVLIST)
    Observable<BaseResponse<String>> getDeviceGeneralAdministrator(@Body RequestBody info);
    /**
     *添加设备的普通用户
     */
    @POST(HttpConstants.CREATENOR_NORMALDEV)
            Observable<BaseResponse<String>> addDeviceNormalUser(@Body RequestBody info);
    /**
     *修改设备的普通用户的权限
     */
    @POST(HttpConstants.UPDATE_NORMALDEV)
            Observable<BaseResponse<String>> modifyDeviceOrginaryUserPermission(@Body RequestBody info);
    /**
     *删除设备的普通用户
     */
    @POST(HttpConstants.DELETE_NORMALDEV)
            Observable<BaseResponse<String>> deleteDeviceNormalUser(@Body RequestBody info);
    /**
     *上传开锁记录
     */
    @POST(HttpConstants.UPDATA_OPENLOCK)
            Observable<BaseResponse<String>> uploadOpenLockRecord(@Body RequestBody info);
    /**
     *获取开锁记录
     */
    @POST(HttpConstants.GET_OPEN_RECORD)
            Observable<BaseResponse<String>> getOpenLockRecord(@Body RequestBody info);
    /**
     *更新指点设备的位置信息
     */
    @POST(HttpConstants.UPDATA_lOCATION)
            Observable<BaseResponse<String>> updatePointDeviceLocation(@Body RequestBody info);
    /**
     *是否启用自动开锁
     */
    @POST(HttpConstants.IS_AUTO_OPENLOCK)
            Observable<BaseResponse<String>> whetherEnabledAutomaticOpenLock(@Body RequestBody info);
    /**
     *修改锁编号信息
     */
    @POST(HttpConstants.MODIFY_LOCK_NUMBER_INFORMATION)
    Observable<BaseResponse<String>> modifyLockNumberInformation(@Body RequestBody info);
    /**
     *分类查询
     */
    @POST(HttpConstants.CLASSIFICATION_QUERY)
            Observable<BaseResponse<String>> classificationQuery(@Body RequestBody info);
    /**
     *批量修改锁编号信息
     */
    @POST(HttpConstants.MODIFY_LOCK_NUMBER_INFORMATION_BATCHES)
            Observable<BaseResponse<String>> batchModifyLockNumberInformation(@Body RequestBody info);
    /**
     *获取锁编号对应的昵称
     */
    @POST(HttpConstants.GET_LOCK_USER_NUMBER_NICKNAME)
            Observable<BaseResponse<String>> getLockNumberNickname(@Body RequestBody info);

}
