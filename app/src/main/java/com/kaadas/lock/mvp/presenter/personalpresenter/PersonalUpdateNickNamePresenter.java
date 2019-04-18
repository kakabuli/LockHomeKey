package com.kaadas.lock.mvp.presenter.personalpresenter;

import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.mvp.view.personalview.IPersonalUpdateNickNameView;


import io.reactivex.disposables.Disposable;

public class PersonalUpdateNickNamePresenter<T> extends BasePresenter<IPersonalUpdateNickNameView> {
    //修改昵称
    public void updateNickName(String uid,String nickName){
        XiaokaiNewServiceImp.modifyUserNick(uid,nickName).subscribe(new BaseObserver<BaseResult>() {
            @Override
            public void onSuccess(BaseResult baseResult) {
                    if (mViewRef.get()!=null){
                        if ("200".equals(baseResult.getCode()))
                        mViewRef.get().updateNickNameSuccess(nickName);
                    }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (mViewRef.get()!=null){
                    mViewRef.get().updateNickNameFail(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                 if (mViewRef.get()!=null){
                     mViewRef.get().updateNickNameError(throwable);
                 }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        });
    }

}
