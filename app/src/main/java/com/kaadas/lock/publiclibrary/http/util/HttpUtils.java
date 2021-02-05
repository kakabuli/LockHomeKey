package com.kaadas.lock.publiclibrary.http.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.huawei.hms.core.aidl.RequestHeader;
import com.kaadas.lock.BuildConfig;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.rxutils.TimeOutException;
import com.kaadas.lock.utils.AES;
import com.kaadas.lock.utils.LogUtils;


import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.RequestBody;
import retrofit2.HttpException;

public class HttpUtils<T> {

    public RequestBody getBodyToken(T t,String timestamp) {
        String obj = new Gson().toJson(t);
        String contentType = "application/json; charset=utf-8";
        if(Integer.parseInt(BuildConfig.HTTP_VERSION) > 0){
            LogUtils.e("shulan getBodyToken http body 加密前--->" + obj);
            LogUtils.e("shulan getBodyToken timestamp--->" + timestamp);
            try {
                if (!TextUtils.isEmpty(MyApplication.getInstance().getToken())) {
                    obj = AES.Encrypt(obj,AES.keyForToken(MyApplication.getInstance().getToken(),AES.key,timestamp));
                }else{
                    obj = AES.Encrypt(obj,AES.keyNoToken(AES.key,timestamp));
                }
                if(!TextUtils.isEmpty(obj))
                    contentType = "text/plain; charset=utf-8";
                LogUtils.e("shulan getBodyToken http body 加密后--->" + obj);
                LogUtils.e("shulan getBodyToken http body 解密后后--->" + AES.Decrypt(obj,AES.keyForToken(MyApplication.getInstance().getToken(),AES.key,timestamp)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse(contentType), obj);
        return body;
    }

    public RequestBody getBodyNoToken(T t,String timestamp){
        String obj = new Gson().toJson(t);
        String contentType = "application/json; charset=utf-8";
        if(Integer.parseInt(BuildConfig.HTTP_VERSION) > 0){
            LogUtils.e("shulan getBodyNoToken http body 加密前--->" + obj);
            LogUtils.e("shulan getBodyNoToken timestamp--->" + timestamp);
            try {
                obj = AES.Encrypt(obj,AES.keyNoToken(AES.key,timestamp));
                if(!TextUtils.isEmpty(obj))
                    contentType = "text/plain; charset=utf-8";
                LogUtils.e("shulan getBodyNoToken http body 加密后--->" + obj);
                LogUtils.e("shulan getBodyNoToken http body 解密后后--->" + AES.Decrypt(obj,AES.keyNoToken(AES.key,timestamp)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse(contentType), obj);
        return body;
    }

    public RequestBody getBody(T t) {
        String obj = new Gson().toJson(t);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), obj);
        return body;
    }


    public static String httpErrorCode(Activity activity, String errorMsg) {
        int code = Integer.parseInt(errorMsg);
        switch (code) {
            case 100:
                errorMsg = activity.getString(R.string.account_or_password_empty);
                break;
            case 101:
                errorMsg = activity.getString(R.string.account_or_password_error);
                break;
            case 806:
                errorMsg = activity.getString(R.string.illegal_data);
                break;
            case 801:
                errorMsg = activity.getString(R.string.e_yi_login);
                break;
            case 595:
                errorMsg = activity.getString(R.string.information_verification_fail);
                break;
            case 500:
                errorMsg = activity.getString(R.string.server_error);
                break;
            case 404:
                errorMsg = activity.getString(R.string.device_not_found);
                break;
            case 999:
                errorMsg = activity.getString(R.string.resource_not_find);
                break;
            case 509:
                errorMsg = activity.getString(R.string.server_process_timeout);
                break;
            case 501:
                errorMsg = activity.getString(R.string.ye_wu_process_fail);
                break;
            case 400:
                errorMsg = activity.getString(R.string.request_data_format_incorrect);
                break;
            case 401:
                errorMsg = activity.getString(R.string.data_parameter_incorrect);
                break;
            case 402:
                errorMsg = activity.getString(R.string.fu_quan_user_not_find);
                break;
      /*      case 444:
                errorMsg = activity.getString(R.string.not_login);
                break;*/
            case 413:
                errorMsg = activity.getString(R.string.image_size_too_large);
                break;
            case 406:
                errorMsg = activity.getString(R.string.not_show_verification);
                break;
            case 408:
                errorMsg = activity.getString(R.string.virification_error);
                break;
            case 607:
                errorMsg = activity.getString(R.string.upload_file_fail);
                break;
            case 601:
                errorMsg = activity.getString(R.string.get_nickname_fail);
                break;
            case 602:
                errorMsg = activity.getString(R.string.modify_push_switch_fail);
                break;
            case 603:
                errorMsg = activity.getString(R.string.modify_nickname_fail);
                break;
            case 204:
                errorMsg = activity.getString(R.string.register_account_fail);
                break;
            case 208:
                errorMsg = activity.getString(R.string.modify_password_fail);
                break;
            case 301:
                errorMsg = activity.getString(R.string.character_length_chao_guo_limit);
                break;
            case 302:
                errorMsg = activity.getString(R.string.liu_yan_fail);
                break;
            case 704:
                errorMsg = activity.getString(R.string.verification_send_too_many);
                break;
            case 435:
                errorMsg = activity.getString(R.string.password_only_number_and_letter_6_15);
                break;
            case 780:
                errorMsg = activity.getString(R.string.logout_fail);
                break;
            case 431:
                errorMsg = activity.getString(R.string.upload_pushid_fail);
                break;
            case 781:
                errorMsg = activity.getString(R.string.add_device_fail);
                break;
            case 803:
                errorMsg = activity.getString(R.string.open_lock_fail_not_permission);
                break;
            case 782:
                errorMsg = activity.getString(R.string.operation_fail);
                break;
            case 785:
                errorMsg = activity.getString(R.string.open_lock_fail);
                break;
            case 201:
                errorMsg = activity.getString(R.string.not_bind);
                break;
            case 202:
                errorMsg = activity.getString(R.string.has_bind);
                break;
            case 409:
                errorMsg = activity.getString(R.string.device_repeat_register);
                break;
            case 410:
                errorMsg = activity.getString(R.string.user_not_exist);
                break;
            case 411:
                errorMsg = activity.getString(R.string.fu_quan_user_not_find);
                break;
            case 433:
                errorMsg = activity.getString(R.string.not_manager);
                break;
            case 412:
                errorMsg = activity.getString(R.string.device_register_fail_repeat_record);
                break;
            case 445:
                errorMsg = activity.getString(R.string.invalid_random_code);
                break;
            case 405:
                errorMsg = activity.getString(R.string.user_repeat_register);
                break;
            case 991:
                errorMsg = activity.getString(R.string.production_fail);
                break;
            case 812:
                errorMsg = activity.getString(R.string.has_nodification_manager_confirm);
                break;
            case 813:
                errorMsg = activity.getString(R.string.has_bind_gateway);
                break;
            case 871:
                errorMsg = activity.getString(R.string.bind_gateway_fail);
                break;
            case 946:
                errorMsg = activity.getString(R.string.mimi_bind_gateway_fail);
                break;
            case 947:
                errorMsg = activity.getString(R.string.approval_mimi_bind_gateway_fail);
                break;
            case 816:
                errorMsg = activity.getString(R.string.approval_fail);
                break;
            case 819:
                errorMsg = activity.getString(R.string.get_list_fail);
                break;
            case 847:
                errorMsg = activity.getString(R.string.not_gateway_manager);
                break;
            case 845:
                errorMsg = activity.getString(R.string.unbind_fail);
                break;
            case 820:
                errorMsg = activity.getString(R.string.get_gateway_device_list_fail);
                break;
            case 823:
                errorMsg = activity.getString(R.string.get_open_lock_record_fail);
                break;
            case 436:
                errorMsg = activity.getString(R.string.data_not_map);
                break;
            case 419:
                errorMsg = activity.getString(R.string.not_peremeter_peremeter_not_match);
                break;
            case 567:
                errorMsg = activity.getString(R.string.pi_liang_register_user_fail);
                break;
            case 592:
                errorMsg = activity.getString(R.string.sn_password_mac_map_incorrect);
                break;
            case 539:
                errorMsg = activity.getString(R.string.api_interface_update_fail);
                break;
            case 407:
                errorMsg = activity.getString(R.string.mqtt_server_connect_fail);
                break;
            case 414:
                errorMsg = activity.getString(R.string.do_not_register_user_memenet);
                break;
            case 499:
                errorMsg = activity.getString(R.string.too_many_request);
                break;
            case 711:
                errorMsg = activity.getString(R.string.register_memenet_fail);
                break;
            case 416:
                errorMsg=activity.getString(R.string.user_exit);
                break;
        }
        return errorMsg;
    }

    public static String httpProtocolErrorCode(Activity activity, Throwable e) {
        String errorMsg = "";
        if (e instanceof IOException) {
            /** 没有网络 */
            errorMsg = activity.getString(R.string.network_exception);
        } else if (e instanceof OtherException) {
            /** 网络正常，http 请求成功，服务器返回逻辑错误  接口返回的别的状态码处理*/
            OtherException otherException = (OtherException) e;
            if (otherException.getResponse().getCode() == 444) { //Token过期
                LogUtils.e("token过期   " + Thread.currentThread().getName());
                if (MyApplication.getInstance().getMqttService()!=null){
                    MyApplication.getInstance().getMqttService().httpMqttDisconnect();
                }
                MyApplication.getInstance().tokenInvalid(true);
            } else {
                errorMsg = activity.getString(R.string.server_back_exception) + ((OtherException) e).getResponse().toString();
            }

        } else if (e instanceof SocketTimeoutException) { //连接超时
            errorMsg = activity.getString(R.string.connect_out_of_date);
        } else if (e instanceof TimeOutException) { //连接超时
            errorMsg = activity.getString(R.string.tiem_out);
        } else if (e instanceof ConnectException) { //连接异常
            errorMsg = activity.getString(R.string.connect_exception);
        } else if (e instanceof UnknownHostException) { //无法识别主机  网络异常
            errorMsg = activity.getString(R.string.unable_identify_host);
        } else if (e instanceof HttpException) {
            /** 网络异常，http 请求失败，即 http 状态码不在 [200, 300) 之间, such as: "server internal error". 协议异常处理*/
            errorMsg = ((HttpException) e).response().message();
            HttpException httpException = (HttpException) e;
            errorMsg = activity.getString(R.string.http_exception) + httpException.code() + activity.getString(R.string.exception) + httpException.message();
        } else {
            /** 其他未知错误 */
            errorMsg = !TextUtils.isEmpty(e.getMessage()) ? e.getMessage() : "unknown error";
        }
        LogUtils.e("  网络请求   " + errorMsg);
        return errorMsg;
    }
}
