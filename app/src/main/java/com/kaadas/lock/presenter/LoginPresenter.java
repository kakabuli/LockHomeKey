package com.kaadas.lock.presenter;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.base.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.LoginResult;
import com.kaadas.lock.publiclibrary.http.result.UserNickResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.view.ILoginView;


import io.reactivex.disposables.Disposable;

/**
 * Create By lxj  on 2019/1/9
 * Describe
 */
public class LoginPresenter<T> extends BasePresenter<ILoginView> {


    public void loginByPhone(String phone, String pwd,String noCounturyPhone) {
        XiaokaiNewServiceImp.loginByPhone(phone, pwd)
                .subscribe(new BaseObserver<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        loginSuccess(loginResult,noCounturyPhone,pwd);
                    }

                    @Override
                    public void onAckErrorCode(BaseResult result) {
                        mViewRef.get().onLoginFailedServer(result);
                        LogUtils.e("登陆失败   " + result.toString());
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        mViewRef.get().onLoginFailed(throwable);
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }

    //获取用户名称
    private void   getUserName(String uid){
        XiaokaiNewServiceImp.getUserNick(uid).subscribe(new BaseObserver<UserNickResult>() {
            @Override
            public void onSuccess(UserNickResult userNickResult) {
                if ("200".equals(userNickResult.getCode())){
                   setUserName(userNickResult.getData().getNickName());
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

    private void setUserName(String userName) {
        SPUtils.put(SPUtils.USERNAME,userName);
    }

    private void loginSuccess(LoginResult loginResult,String phone,String pwd) {
        //请求用户名称，由于服务器返回过来的用户名称为空，因此需要重新获取
        if (mViewRef != null) {
            mViewRef.get().onLoginSuccess();
        }
        LogUtils.e("登陆成功  数据是  " + loginResult.toString());
        //保存数据到本地  以及 内存
        SPUtils.put(SPUtils.TOKEN, loginResult.getData().getToken());
        SPUtils.put(SPUtils.UID, loginResult.getData().getUid());
        SPUtils.put(SPUtils.PHONEN,phone);
        MyApplication.getInstance().setToken(loginResult.getData().getToken());
        MyApplication.getInstance().setUid(loginResult.getData().getUid());
        getUserName(loginResult.getData().getUid());
    }
}
