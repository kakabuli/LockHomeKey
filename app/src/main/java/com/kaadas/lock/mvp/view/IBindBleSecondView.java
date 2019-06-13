package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface IBindBleSecondView extends IBaseView {
    void onDeviceStateChange(boolean isConnected);
}
