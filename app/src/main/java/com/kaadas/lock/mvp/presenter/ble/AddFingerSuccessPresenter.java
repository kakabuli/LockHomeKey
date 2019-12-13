package com.kaadas.lock.mvp.presenter.ble;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.IAddFingerSuccessView;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.postbean.AddPasswordBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Create By lxj  on 2019/3/8
 * Describe
 */
public class AddFingerSuccessPresenter<T> extends BasePresenter<IAddFingerSuccessView> {

    public void uploadPasswordNickToServer(int type, String deviceName, String nickName, String userNum) {
        LogUtils.e("上传密码昵称到服务器   " + deviceName + "     " + nickName);
        List<AddPasswordBean.Password> passwords = new ArrayList<>();
        passwords.add(new AddPasswordBean.Password(type, userNum, nickName, 1));
        XiaokaiNewServiceImp.addPassword(MyApplication.getInstance().getUid()
                , deviceName, passwords)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("davi 上传秘钥昵称到服务器成功");
                        // TODO: 2019/3/8   通知更新秘钥列表   从服务器拿
                        if (isSafe()) {
                            mViewRef.get().onUploadSuccess();
                            MyApplication.getInstance().passwordChangeListener().onNext(true);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().onUploadFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("上传秘钥昵称到服务器失败");
                        if (isSafe()) {
                            mViewRef.get().onUploadFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }


}
