package com.kaadas.lock.view.personalview;


import com.kaadas.lock.base.mvpbase.IBaseView;

public interface IPersonalSecuritySettingView extends IBaseView {

    //手势密码开启状态
    void openHandPwdSuccess();

    //手势密码关闭状态
    void closeHandPwdSuccess();

    //手机开启指纹识别
    void phoneFigerprintOpen();

    //手机未设置指纹识别
    void  phoneFigerprintClose();

    //手势密码开启状态
    void openFingerPrintSuccess();

    //手势密码关闭状态
    void closeFingerPrintSuccess();

}
