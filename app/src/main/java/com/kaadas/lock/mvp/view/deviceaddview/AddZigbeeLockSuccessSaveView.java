package com.kaadas.lock.mvp.view.deviceaddview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface AddZigbeeLockSuccessSaveView extends IBaseView {

    //网关下设备名称修改成功
    void updateDevNickNameSuccess();

    //网关下设备名称修改失败
    void updateDevNickNameFail();

    //网关下设备名称异常
    void updateDevNickNameThrowable(Throwable throwable);

}
