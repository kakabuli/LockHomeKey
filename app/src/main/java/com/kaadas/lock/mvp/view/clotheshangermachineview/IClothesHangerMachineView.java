package com.kaadas.lock.mvp.view.clotheshangermachineview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface IClothesHangerMachineView extends IBaseView {

    void setAirDryTimeSuccess(int action,int countdown);

    void setAirDryTimeFailed();

    void setAirDryTimeThrowable(Throwable throwable);

    void setBakingTimeSuccess(int action,int countdown);

    void setBakingTimeFailed();

    void setBakingTimeThrowable(Throwable throwable);

    void setChildLockSuccess(int action);

    void setChildLockFailed();

    void setChildLockThrowable(Throwable throwable);

    void setVoiceSuccess(int action);

    void setVoiceFailed();

    void setVoiceThrowable(Throwable throwable);

    void setUVSuccess(int action,int countdown);

    void setUVFailed();

    void setUVThrowable(Throwable throwable);

    void setLightingSuccess(int action,int countdown);

    void setLightingFailed();

    void setLightingThrowable(Throwable throwable);

    void setMotorSuccess(int action,int status);

    void setMotorFailed(int action);

    void setMotorThrowable(Throwable throwable);

    void setOverload(int overload);
}
