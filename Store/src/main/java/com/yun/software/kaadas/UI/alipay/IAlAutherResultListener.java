package com.yun.software.kaadas.UI.alipay;

/**
 * Created by 傅令杰
 */

public interface IAlAutherResultListener {

    void onPaySuccess(String authercode);

    void onPaying();

    void onPayFail();

    void onPayCancel();

    void onPayConnectError();
}
