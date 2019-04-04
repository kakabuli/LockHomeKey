package com.kaadas.lock.view.personalview;


import com.kaadas.lock.base.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

public interface IPersonalUpdateNickNameView  extends IBaseView {

        void  updateNickNameSuccess(String nickName);

        void updateNickNameError(Throwable throwable);

        void updateNickNameFail(BaseResult baseResult);
}
