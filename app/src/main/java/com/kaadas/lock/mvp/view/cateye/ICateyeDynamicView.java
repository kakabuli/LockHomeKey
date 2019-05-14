package com.kaadas.lock.mvp.view.cateye;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.utils.greenDao.bean.CatEyeEvent;

import java.util.List;

public interface ICateyeDynamicView extends IBaseView {

    //获取猫眼动信息成功
    void getCateyeDynamicSuccess(List<CatEyeEvent> catEyeInfo);

    //获取猫眼动态信息失败
    void getCateyeDynamicFail();

}
