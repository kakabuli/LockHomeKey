package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBleView;

public interface IFaceOtaView extends IBleView {

    void otaSuccess();

    void otaFailed(int state);

    /**
     * 结束OTA成功
     */
    void onFinishOtaSuccess();

    /**
     * 结束OTA失败
     * @param throwable
     */
    void onFinishOtaFailed(Throwable throwable);
}
