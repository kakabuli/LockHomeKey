package com.kaadas.lock.mvp.view;


import com.kaadas.lock.mvp.mvpbase.IBleView;

/**
 * Create By lxj  on 2019/3/9
 * Describe
 */
public interface ISafeModeView extends IBleView {

    void onSetSuccess(boolean isOpen);

    void onSetFailed(Throwable throwable);

    void onGetStateSuccess(boolean isOpen);

    void onGetStateFailed(Throwable throwable);

    void onPasswordTypeLess();

    void onSendCommand();
}
