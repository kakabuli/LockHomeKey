package com.yun.software.kaadas.UI.alipay;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.alipay.sdk.app.AuthTask;

import java.util.Map;

import la.xiong.androidquick.tool.LogUtils;

/**
 * Created by yanliang
 * 9000	请求处理成功
 * 4000	系统异常
 * 6001	用户中途取消
 * 6002	网络连接出错
 * <p>
 * result_code
 * 200	业务处理成功，会返回authCode
 * 1005	账户已冻结，如有疑问，请联系支付宝技术支持
 * 202	系统异常，请稍后再试或联系支付宝技术支持
 * <p>
 * <p>
 * {
 * resultStatus=9000
 * memo="处理成功"
 * result="success=true&auth_code=d9d1b5acc26e461dbfcb6974c8ff5E64&result_code=200 &user_id=2088003646494707"
 * }
 */

public class AutherAsyncTask extends AsyncTask<String, Void, Map<String, String>> {
    public static final  String TAG="AutherAsyncTask";

    private final Activity ACTIVITY;
    private final IAlAutherResultListener LISTENER;

    //请求处理成功
    private static final String AL_AUTHER_STATUS_SUCCESS = "9000";
    //系统异常
    private static final String AL_AUTHER_STATUS_FAIL = "4000";
    //用户中途取消
    private static final String AL_AUTHER_STATUS_CANCEL = "6001";
    //网络连接出错
    private static final String AL_AUTHER_STATUS_CONNECT_ERROR = "6002";

    public AutherAsyncTask(Activity activity, IAlAutherResultListener listener) {
        this.ACTIVITY = activity;
        this.LISTENER = listener;
    }

    @Override
    protected Map<String, String> doInBackground(String... params) {
        final String alPaySign = params[0];
        final AuthTask payTask = new AuthTask(ACTIVITY);
        return payTask.authV2(alPaySign, true);
    }

    @Override
    protected void onPostExecute(Map<String, String> result) {
        super.onPostExecute(result);
        LogUtils.iTag(TAG,result.toString());
        AuthResult authResult = new AuthResult(result, true);
        String resultStatus = authResult.getResultStatus();

        if (TextUtils.equals(resultStatus, AL_AUTHER_STATUS_SUCCESS) && TextUtils.equals(authResult.getResultCode(), "200")) {
            if (LISTENER != null) {
                LISTENER.onPaySuccess(authResult.getAuthCode());
            }
        } else if (TextUtils.equals(resultStatus, AL_AUTHER_STATUS_FAIL)) {
            if (LISTENER != null) {
                LISTENER.onPayCancel();
            }
        } else if (TextUtils.equals(resultStatus, AL_AUTHER_STATUS_CANCEL)) {
            if (LISTENER != null) {
                LISTENER.onPayFail();
            }
        } else if (TextUtils.equals(resultStatus, AL_AUTHER_STATUS_CONNECT_ERROR)) {
            if (LISTENER != null) {
                LISTENER.onPayConnectError();
            }
        } else {
            if (TextUtils.isEmpty(authResult.getAuthCode())) {
                // ToastUtil.warn(this, "授权取消");
                if (LISTENER != null) {
                    LISTENER.onPayCancel();
                }
            } else {
                // ToastUtil.error(this, String.format("授权失败_authCode:%s", authResult.getAuthCode()));
                if (LISTENER != null) {
                    LISTENER.onPayFail();
                }
            }


        }
    }
}
