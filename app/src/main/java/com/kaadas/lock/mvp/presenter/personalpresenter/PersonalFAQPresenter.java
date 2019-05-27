package com.kaadas.lock.mvp.presenter.personalpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.GetFAQResult;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.mvp.view.personalview.IPersonalFAQView;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class PersonalFAQPresenter<T> extends BasePresenter<IPersonalFAQView> {

    /**
     * 获取常见列表
     */
    public void getFAQList(int languageType) {

        /*XiaokaiNewServiceImp.getFaqList(languageType).subscribe(new BaseObserver<GetFAQResult>() {
            @Override
            public void onSuccess(GetFAQResult getFAQResult) {
                if (mViewRef.get()!=null){
                    if ("200".equals(getFAQResult.getCode())){
                        mViewRef.get().getFAQSuccessListView(getFAQResult);
                    }
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (mViewRef.get()!=null){
                    mViewRef.get().getFAQFail(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (mViewRef.get()!=null){
                    mViewRef.get().getFAQError(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        });*/
        XiaokaiNewServiceImp.getFaqList(languageType).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(String s) {
                Gson gson = new Gson();
                GetFAQResult getFAQResult = gson.fromJson(s, GetFAQResult.class);
                if ("200".equals(getFAQResult.getCode())) { //如果请求成功
                    if (mViewRef != null) {
                        mViewRef.get().getFAQSuccessListView(getFAQResult,s);
                    }
                } else {
                    if ("444".equals(getFAQResult.getCode())) { //Token过期
                        LogUtils.e("token过期   " + Thread.currentThread().getName());
                        if (mqttService!=null){
                            mqttService.httpMqttDisconnect();
                        }
                        MyApplication.getInstance().tokenInvalid(true);
                    }else {
                        if (mViewRef != null) {
                            mViewRef.get().getFAQFail(getFAQResult);
                        }
                    }
                }
            }
            @Override
            public void onError(Throwable e) {
                if (mViewRef.get()!=null){
                    mViewRef.get().getFAQError(e);
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }
}