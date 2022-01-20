package com.kaadas.lock.mvp.presenter;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.IAccountLogoutView;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.LogUtils;

import io.reactivex.disposables.Disposable;

/**
 * author : zhangjierui
 * time   : 2021/12/28
 * desc   : 账号注销
 */
public class AccountLogoutPresenter<T> extends BasePresenter<IAccountLogoutView> {


    public void requestSmsCode(String phone, String areaCode) {
        XiaokaiNewServiceImp.sendMessage(phone, areaCode)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.i("get captcha success");
                        if(isSafe()){
                            mViewRef.get().onRequestCodeResult(0, "");
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if(isSafe()){
                            String msg = baseResult.getMsg();
                            //"code":"704","msg":"验证码发送次数过多"
                            LogUtils.e("get captcha error -> " + baseResult.getMsg());
                            if("704".equals(baseResult.getCode())){
                                msg = MyApplication.getInstance().getString(R.string.get_verification_code_limit);
                            }
                            mViewRef.get().onRequestCodeResult(-1, msg);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        showNetworkErrorMsg(throwable,0);
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }

    public void requestEmailCode(String email) {
        XiaokaiNewServiceImp.sendEmailYZM(email)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        if(isSafe()){
                            mViewRef.get().onRequestCodeResult(0, "");
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if(isSafe()){
                            mViewRef.get().onRequestCodeResult(-1, baseResult.getMsg());
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        showNetworkErrorMsg(throwable,0);
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }

    /**
     * 注销账号
     */
    public void accountLogout(String areaCode, String userAccount, int accountType, String code){
        String uid = MyApplication.getInstance().getUid();
        if(accountType == 1){//手机号需加区号
            userAccount = areaCode+userAccount;
        }
        XiaokaiNewServiceImp.accountLogout(userAccount,uid, accountType, code)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        if (isSafe()) {
                            mViewRef.get().onAccountLogoutSuccess();
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().onAccountLogoutError(baseResult.getMsg());
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        showNetworkErrorMsg(throwable,1);
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }

    private void showNetworkErrorMsg(Throwable throwable, int which){
        if(isSafe()){
            String errorMsg = MyApplication.getInstance().getString(R.string.network_exception_please_check);
            if(which == 0){
                mViewRef.get().onRequestCodeResult(-2, errorMsg);
            }else {
                mViewRef.get().onAccountLogoutError(errorMsg);
            }
        }
    }

}
