package com.kaadas.lock.mvp.view.gatewayView;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface IGatewayDeleteShareView extends IBaseView {

    //刪除分享用戶成功
    void  deleteShareUserSuccess();

    //刪除分享用戶失敗
    void deleteShareUserFail();

    //删除分享用户异常
    void deleteShareUserThrowable();

    //修改分享用戶昵称成功
    void  updateShareUserNameSuccess(String name);

    //修改分享用戶昵称失敗
    void updateShareUserNameFail();

    //修改分享用户昵称异常
    void updaateShareUserNameThrowable();

}
