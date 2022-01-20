package com.kaadas.lock.mvp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.LoginErrorResult;
import com.kaadas.lock.publiclibrary.http.result.LoginResult;
import com.kaadas.lock.publiclibrary.http.result.UserNickResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.LoginObserver;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.MD5Utils;
import com.kaadas.lock.utils.MMKVUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.mvp.view.ILoginView;

import io.reactivex.disposables.Disposable;

/**
 * Create By lxj  on 2019/1/9
 * Describe
 */
public class LoginPresenter<T> extends BasePresenter<ILoginView> {


    public void loginByPhone(String phone, String pwd, String noCounturyPhone) {
        LogUtils.e("shulan -------------?loginByPhone");
        XiaokaiNewServiceImp.loginByPhone(phone, pwd)
                .subscribe(new LoginObserver<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        loginSuccess(loginResult, noCounturyPhone, pwd);
                        LogUtils.e("shulan ---------->" + loginResult.toString());
                    }

                    @Override
                    public void onAckErrorCode(LoginResult result) {
                        mViewRef.get().onLoginFailedServer(result);
                        LogUtils.e("shulan ---------->登陆失败   " + result.toString());
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        mViewRef.get().onLoginFailed(throwable);
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {

                    }
                });
    }

    public void loginByEmail(String Email, String pwd) {
        XiaokaiNewServiceImp.loginByEmail(Email, pwd)
                .subscribe(new LoginObserver<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        loginSuccess(loginResult, Email, pwd);
                    }

                    @Override
                    public void onAckErrorCode(LoginResult result) {
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
    private void getUserName(String uid) {
        XiaokaiNewServiceImp.getUserNick(uid).subscribe(new BaseObserver<UserNickResult>() {
            @Override
            public void onSuccess(UserNickResult userNickResult) {
                if ("200".equals(userNickResult.getCode())) {
                    setUserName(userNickResult.getData().getNickName());
                    SPUtils.put(SPUtils.REAL_USERNAME, userNickResult.getData().getUserName());
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
        SPUtils.put(SPUtils.USERNAME, userName);
    }

    private void loginSuccess(LoginResult loginResult, String phone, String pwd) {
        //请求用户名称，由于服务器返回过来的用户名称为空，因此需要重新获取
        if (mViewRef != null) {
            mViewRef.get().onLoginSuccess();
        }
        LogUtils.e("登陆成功  数据是  " + loginResult.toString());
        //保存数据到本地  以及 内存
//        SPUtils.put(SPUtils.TOKEN, loginResult.getData().getToken());
//        SPUtils.put(SPUtils.UID, loginResult.getData().getUid());
        MMKVUtils.setMMKV(SPUtils.TOKEN,loginResult.getData().getToken());
        MMKVUtils.setMMKV(SPUtils.UID,loginResult.getData().getUid());
        SPUtils.put(SPUtils.PHONEN, phone);
        SPUtils.put(SPUtils.PASSWORD, MD5Utils.encode(pwd));

        //如果有注销 获取注销时间
        long accountLogoutTime = loginResult.getData().getAccountLogoutTime();
        if(accountLogoutTime > 0){
            MyApplication.getInstance().setAccountLogoutTime(accountLogoutTime);
        }

        MyApplication.getInstance().setToken(loginResult.getData().getToken());
        MyApplication.getInstance().setUid(loginResult.getData().getUid());
        getUserName(loginResult.getData().getUid());
    }

}
