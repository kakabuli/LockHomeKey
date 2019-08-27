package com.kaadas.lock.mvp.presenter.ble;


import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.mvp.view.IFamilyMemberDeatilView;


import io.reactivex.disposables.Disposable;

public class FamilyMemberDetailPresenter<T> extends BasePresenter<IFamilyMemberDeatilView> {

    /**
     * 删除用户列表
     */
    public void deleteUserList(String adminid, String dev_username, String devname) {
        XiaokaiNewServiceImp.deleteDeviceNormalUser(adminid, dev_username, devname).subscribe(new BaseObserver<BaseResult>() {
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

        XiaokaiNewServiceImp.modifyCommonUserNickname(ndId, userNickName).subscribe(new BaseObserver<BaseResult>() {
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

    /**
     * 获取关联时间
     */
/*    public void getAuthorizationTime (String ndId, String userNickName) {

        XiaokaiNewServiceImp.modifyCommonUserNickname(ndId, userNickName).subscribe(new BaseObserver<BaseResult>() {
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
    }*/


}
