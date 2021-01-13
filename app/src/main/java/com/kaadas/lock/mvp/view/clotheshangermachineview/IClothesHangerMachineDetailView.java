package com.kaadas.lock.mvp.view.clotheshangermachineview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.MultiCheckOTAResult;

import java.util.List;

public interface IClothesHangerMachineDetailView extends IBaseView {

    void needUpdate(List<MultiCheckOTAResult.UpgradeTask> upgradeTasks);

    void noUpdate();

    void checkUpdateFailed(BaseResult baseResult);

    void checkUpdateThrowable(Throwable throwable);

    void updateSuccess();

    void updateFailed(BaseResult baseResult);

    void updateThrowable(Throwable throwable);

    void onDeleteDeviceSuccess();

    void onDeleteDeviceFailed(BaseResult result);

    void onDeleteDeviceThrowable(Throwable throwable);
}
