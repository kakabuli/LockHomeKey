package com.kaadas.lock.mvp.presenter.personalpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.DeleteMessageResult;
import com.kaadas.lock.publiclibrary.http.result.GetMessageResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.mvp.view.personalview.IPersonalMessageView;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class PersonalMessagePresenter<T> extends BasePresenter<IPersonalMessageView> {

    //获取消息
    public void getMessage(String uid,int page) {
        // TODO: 2019/3/15
        XiaokaiNewServiceImp.getMessageList(uid, page).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(String s) {

                Gson gson=new Gson();
                GetMessageResult getMessageResult = gson.fromJson(s, GetMessageResult.class);
                if (getMessageResult.isSuccess()) { //如果请求成功
                    if (mViewRef != null) {
                        mViewRef.get().getMessageSuccess(getMessageResult);
                    }
                } else {
                    if ("444".equals(getMessageResult.getCode())) { //Token过期
                        LogUtils.e("token过期   " + Thread.currentThread().getName());
                        if (mqttService!=null){
                            mqttService.httpMqttDisconnect();
                        }
                        MyApplication.getInstance().tokenInvalid(true);
                    }else {
                        if (mViewRef != null) {
                            mViewRef.get().getMessageFail(getMessageResult);
                        }
                    }
                }
            /*    if ("200".equals(getMessageResult.getCode())) { //如果请求成功
                    if (mViewRef != null) {
                        mViewRef.get().getMessageSuccess(getMessageResult);
                    }
                } else {
                    if ("444".equals(getMessageResult.getCode())) { //Token过期
                        LogUtils.e("token过期   " + Thread.currentThread().getName());
                        MyApplication.getInstance().tokenInvalid(true);
                    }
                }*/
            }

            @Override
            public void onError(Throwable e) {
                if (mViewRef.get()!=null){
                    mViewRef.get().getMessageError(e);
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }
    //删除消息
    public void deleteMessage(String uid,String mid,int position){
        XiaokaiNewServiceImp.deleteMessage(uid,mid).subscribe(new BaseObserver<DeleteMessageResult>() {
            @Override
            public void onSuccess(DeleteMessageResult deleteMessageResult) {
                if ("200".equals(deleteMessageResult.getCode())){
                    if (mViewRef.get()!=null){
                        mViewRef.get().deleteSuccess(position);
                    }
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (mViewRef.get()!=null){
                    mViewRef.get().deleteFail(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (mViewRef.get()!=null){
                    mViewRef.get().deleteError(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
            compositeDisposable.add(d);
            }
        });
    }


}
