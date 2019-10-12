package com.yun.software.kaadas.Http;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.body.UIProgressResponseCallBack;
import com.zhouyou.http.cache.model.CacheMode;
import com.zhouyou.http.callback.CallBackProxy;
import com.zhouyou.http.callback.ProgressDialogCallBack;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.model.HttpHeaders;
import com.zhouyou.http.subsciber.IProgressDialog;

import java.io.File;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import la.xiong.androidquick.tool.DialogUtil;
import la.xiong.androidquick.tool.ExceptionUtil;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.StringUtil;

public class HttpManager {
    private boolean mIsLoading;
    private String tipMessage;
    private HttpManager() {

    }

    private static class SingletonInstance {
        private static final HttpManager INSTANCE = new HttpManager();
    }

    public static HttpManager getInstance() {

        return SingletonInstance.INSTANCE;
    }

    public void post(String method, Map<String,Object> map, final OnIResponseListener responseListener) {
        post(null, method, map, responseListener, false, null);
    }

    public void post(Context context, String method, Map<String,Object> map, final OnIResponseListener responseListener, boolean isLoading) {
       post(context, method, map, responseListener, isLoading, null);

    }

    public void post(Context context, String method, Map<String,Object> map, final OnIResponseListener responseListener, boolean isLoading, String tipMessage) {
        mIsLoading = isLoading;
        Gson gson = new Gson();
        map.put("device","android");
        String json = gson.toJson(map);
        HttpHeaders headers = new HttpHeaders();
        headers.put("Content-Type", "application/json");
        Disposable disposable = EasyHttp.post(method)
                .baseUrl(ApiConstants.BASE_URL)
                .upJson(json)
                .cacheMode(CacheMode.CACHEANDREMOTE)
                .headers(headers)
                .cacheKey(method + System.currentTimeMillis())
                .execute(new CallBackProxy<MyApiResult<String>, String>(new SimpleCallBack<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        if (mIsLoading) {
                            if (StringUtil.isEmpty(tipMessage)) {
//                                ProgressLoader.showLoading(context);
                                DialogUtil.showLoadingDialog(context);
                            } else {
                                DialogUtil.showLoadingDialog(context,tipMessage);
//                                ProgressLoader.showLoading(context, tipMessage);
                            }
                        }
                    }
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }
                    @Override
                    public void onError(ApiException e) {
                        Log.e("出错了", "出错原因是   " + e.getMessage());
                        if(mIsLoading){
                          DialogUtil.dismissLoadingDialog(context);
                        }
//                        if(e.getMessage().contains("无效的")){
//                            Log.e("无效的", "无效的");
//                            return;
//                        }else if(e.getMessage().contains("无法解析该域名")){
//
//                        }
                        responseListener.onFailed(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {
                        try {
                            if(mIsLoading){
                                DialogUtil.dismissLoadingDialog(context);
                            }
                            responseListener.onSucceed(response);
                        }catch (Exception e) {
                            ExceptionUtil.handle(e);
                        }finally {
                            if(mIsLoading){
                                DialogUtil.dismissLoadingDialog(context);
                            }
                        }
                    }
                }) {
                });
        DisPostManager.getInstance().addDisoPost(disposable);
    }


    public void uploadFile(File file, IProgressDialog mProgressDialog,final OnIResponseListener responseListener){
        //fileRewriteFullyQualifiedPath 有用的
        HttpHeaders headers = new HttpHeaders();
        headers.put("Content-type","multipart/form-data");
        Disposable disposable =  EasyHttp.post(ApiConstants.COMMONAPP_IMGUPLOAD)
                .params("file", file, new UIProgressResponseCallBack() {
                    @Override
                    public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
                        LogUtils.eTag("TAG",contentLength);
                    }
                })
                .headers(headers)
                .accessToken(true)
                .timeStamp(true)
                .execute(new ProgressDialogCallBack<String>(mProgressDialog, true, true) {
                    @Override
                    public void onError(ApiException e) {
                        super.onError(e);
                        responseListener.onFailed(e.getMessage());
                    }
                    @Override
                    public void onSuccess(String response) {
                        responseListener.onSucceed(response);
                        LogUtils.eTag("success",response);
                    }
                } );

        DisPostManager.getInstance().addDisoPost(disposable);
    }




}
