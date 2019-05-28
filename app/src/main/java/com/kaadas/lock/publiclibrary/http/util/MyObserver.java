package com.kaadas.lock.publiclibrary.http.util;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.publiclibrary.http.temp.BaseResponse;
import com.kaadas.lock.utils.LogUtils;


import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.disposables.ListCompositeDisposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.util.EndConsumerHelper;
import retrofit2.HttpException;

/**
 * Create By lxj  on 2019/2/19
 * Describe
 */
@Deprecated
public abstract class MyObserver<T> implements Observer<BaseResponse<String>>, Disposable {
    private Class<T> mClass;

    /**
     * The active subscription.
     */
    private final AtomicReference<Disposable> upstream = new AtomicReference<Disposable>();

    /**
     * The resource composite, can never be null.
     */
    private final ListCompositeDisposable resources = new ListCompositeDisposable();


    public MyObserver(Class<T> mClass) {
        this.mClass = mClass;
    }

    /**
     * Adds a resource to this ResourceObserver.
     *
     * @param resource the resource to add
     * @throws NullPointerException if resource is null
     */
    public final void add(@NonNull Disposable resource) {
        ObjectHelper.requireNonNull(resource, "resource is null");
        resources.add(resource);
    }

    @Override
    public final void onSubscribe(Disposable d) {
        if (EndConsumerHelper.setOnce(this.upstream, d, getClass())) {
            onStart();
        }
        onSubscribe1(d);
    }

    @Override
    public void onNext(BaseResponse<String> response) {
        LogUtils.e("请求的数据是  code " + response.getCode()  + MyApplication.getInstance().getToken() );
        if (response.isSuccess()) { //如果请求成功
            LogUtils.e("请求到的数据体是  " + response.getData());
            if (response.getData() == null) {
                onSuccess(new BaseResponse<T>(response.getCode(), response.getMsg(), null));
                return;
            } else if (mClass.equals(String.class)) {
                onSuccess(new BaseResponse<T>(response.getCode(), response.getMsg(), (T) response.getData()));
                return;
            } else if (response.getData().startsWith("[")) { //是数组
                response.setData(" { \"data\":" + response.getData() + " }");
            }
            LogUtils.e("data数据是  " + response.getData());
            onSuccess(new BaseResponse<T>(response.getCode(), response.getMsg(), new Gson().fromJson(response.getData(), mClass)));
        } else {
            OtherException exception = new OtherException(response);
            onError(exception);
        }
    }


    @Override
    public void onError(Throwable e) {
        simpleParseThrowable(e);
        onFailed(e);
    }


    Handler handler = new Handler(Looper.getMainLooper());
    private void simpleParseThrowable(Throwable e){
        String errorMsg;
        if (e instanceof IOException) {
            /** 没有网络 */
            errorMsg = "网络异常";
        }  else if (e instanceof OtherException) {
            /** 网络正常，http 请求成功，服务器返回逻辑错误  接口返回的别的状态码处理*/
            OtherException otherException = (OtherException) e;
            if (otherException.getResponse().getCode() == 444 ){ //Token过期
                LogUtils.e("token过期   "+ Thread.currentThread().getName());
                if (MyApplication.getInstance().getMqttService()!=null){
                    MyApplication.getInstance().getMqttService().httpMqttDisconnect();
                }
                MyApplication.getInstance().tokenInvalid(true);
            }
            errorMsg = "服务器返回异常"+((OtherException) e).getResponse().toString();
        } else if (e instanceof SocketTimeoutException) { //连接超时
            errorMsg = "连接超时";
        } else if (e instanceof ConnectException) { //连接异常
            errorMsg = "连接异常";
        } else if (e instanceof UnknownHostException) { //无法识别主机  网络异常
            errorMsg = "无法识别主机";
        } else if (e instanceof HttpException) {
            /** 网络异常，http 请求失败，即 http 状态码不在 [200, 300) 之间, such as: "server internal error". 协议异常处理*/
            errorMsg = ((HttpException) e).response().message();
            HttpException httpException = (HttpException) e;
            errorMsg = "http异常  "+httpException.code()+ "  异常消息  " + httpException.message();
        }else {
            /** 其他未知错误 */
            errorMsg = !TextUtils.isEmpty(e.getMessage()) ? e.getMessage() : "unknown error";
        }
        LogUtils.e("  网络请求   " + errorMsg);
    }



    @Override
    public void onComplete() {

    }

    public abstract void onSuccess(BaseResponse<T> stringBaseResponse);

    public abstract void onFailed(Throwable throwable);

    public abstract void onSubscribe1(Disposable d);


    /**
     * Called once the upstream sets a Subscription on this ResourceObserver.
     *
     * <p>You can perform initialization at this moment. The default
     * implementation does nothing.
     */
    protected void onStart() {


    }

    /**
     * Cancels the main disposable (if any) and disposes the resources associated with
     * this ResourceObserver (if any).
     *
     * <p>This method can be called before the upstream calls onSubscribe at which
     * case the main Disposable will be immediately disposed.
     */
    @Override
    public final void dispose() {
        if (DisposableHelper.dispose(upstream)) {
            resources.dispose();
        }
    }

    /**
     * Returns true if this ResourceObserver has been disposed/cancelled.
     *
     * @return true if this ResourceObserver has been disposed/cancelled
     */
    @Override
    public final boolean isDisposed() {
        return DisposableHelper.isDisposed(upstream.get());
    }






}


