package com.kaadas.lock.mvp.view.clotheshangermachineview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.MultiCheckOTAResult;

import java.util.List;

public interface IClothesHangerMachineNicknameView extends IBaseView {

    void onSettingNicknameSuccess();

    void onSettingNicknameFailed(BaseResult result);

    void onSettingNicknameThrowable(Throwable throwable);
}
