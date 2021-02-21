package com.kaadas.lock.mvp.presenter.personalpresenter;

import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.UserNickResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.mvp.view.personalview.IPersonalUpdateNickNameView;
import com.kaadas.lock.utils.SPUtils;


import io.reactivex.disposables.Disposable;

public class PersonalUpdateNickNamePresenter<T> extends BasePresenter<IPersonalUpdateNickNameView> {
    //修改昵称
    public void updateNickName(String uid, String nickName) {
        XiaokaiNewServiceImp.modifyUserNick(uid, nickName).subscribe(new BaseObserver<BaseResult>() {
            @Override
            public void onSuccess(BaseResult baseResult) {
                if ("200".equals(baseResult.getCode()))
                    getUserName(uid);
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (isSafe()) {
                    mViewRef.get().updateNickNameFail(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (isSafe()) {
                    mViewRef.get().updateNickNameError(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        });
    }

    //获取用户名称
    private void getUserName(String uid) {
        XiaokaiNewServiceImp.getUserNick(uid).subscribe(new BaseObserver<UserNickResult>() {
            @Override
            public void onSuccess(UserNickResult userNickResult) {
                if ("200".equals(userNickResult.getCode())) {
                    if (isSafe()) {
                        mViewRef.get().updateNickNameSuccess(userNickResult.getData().getNickName());
                    }
                }

            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {

            }

            @Override
            public void onFailed(Throwable throwable) {

            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        });
    }

}
