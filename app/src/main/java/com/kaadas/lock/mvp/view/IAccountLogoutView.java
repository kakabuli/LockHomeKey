package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

/**
 * author : zhangjierui
 * time   : 2021/12/28
 * desc   :
 */
public interface IAccountLogoutView extends IBaseView {

    void onRequestCodeResult(int status, String msg);

    void onAccountLogoutSuccess();

    void onAccountLogoutError(String msg);
}
