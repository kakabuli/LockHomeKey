package com.kaadas.lock.mvp.presenter.wifilock;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWiFiLockShareUserDetailView;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;

import io.reactivex.disposables.Disposable;

public class WiFiLockShareUserDetailPresenter<T> extends BasePresenter<IWiFiLockShareUserDetailView> {
    /**
     * 删除用户列表
     */
    public void deleteUserList(String shareId,String adminNickname) {
        XiaokaiNewServiceImp.wifiLockDeleteShareUser(shareId, MyApplication.getInstance().getUid(),adminNickname).
                subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        if (mViewRef != null) {
                            mViewRef.get().deleteCommonUserListSuccess(baseResult);

                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (mViewRef != null) {
                            mViewRef.get().deleteCommonUserListFail(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (mViewRef != null) {
                            mViewRef.get().deleteCommonUserListError(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });

    }

    /**
     * 修改普通用户昵称
     */
    public void modifyCommonUserNickname(String ndId, String userNickName) {
        XiaokaiNewServiceImp.wifiLockUpdateShareUserNickname(ndId, userNickName,MyApplication.getInstance().getUid())
                .subscribe(new BaseObserver<BaseResult>() {
            @Override
            public void onSuccess(BaseResult baseResult) {
                if (mViewRef != null) {
                    mViewRef.get().modifyCommonUserNicknameSuccess(baseResult);
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (mViewRef != null) {
                    mViewRef.get().modifyCommonUserNicknameFail(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (mViewRef != null) {
                    mViewRef.get().modifyCommonUserNicknameError(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        });
    }
}
