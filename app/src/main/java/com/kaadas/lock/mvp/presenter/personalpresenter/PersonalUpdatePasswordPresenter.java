package com.kaadas.lock.mvp.presenter.personalpresenter;



import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.mvp.view.personalview.IPersonalUpdatePasswrodView;

import io.reactivex.disposables.Disposable;

public class PersonalUpdatePasswordPresenter<T> extends BasePresenter<IPersonalUpdatePasswrodView> {

    //修改密码
    public void updatePasswrod(String uid, String newpwd, String oldpwd) {
        XiaokaiNewServiceImp.modifyPassword(uid, newpwd, oldpwd).subscribe(new BaseObserver<BaseResult>() {
            @Override
            public void onSuccess(BaseResult baseResult) {
                if (mViewRef.get() != null) {
                    if ("200".equals(baseResult.getCode())) {
                        mViewRef.get().updatePwdSuccess(newpwd);
                    }
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (mViewRef.get()!=null){
                    mViewRef.get().updatePwdFail(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (mViewRef.get()!=null){
                    mViewRef.get().updatePwdError(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        });
    }
}
