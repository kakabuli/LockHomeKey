package com.kaadas.lock.publiclibrary.ota;

import android.text.TextUtils;

import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.OTAResult;
import com.kaadas.lock.utils.LogUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class OtaPresenter<T> extends BasePresenter<IOtaView> {


    private Disposable otaDisposable;

    public void checkOtaInfo(String SN, String version) {
        //请求成功
        otaDisposable = XiaokaiNewServiceImp.getOtaInfo(1, SN, version)
                .subscribe(new Consumer<OTAResult>() {
                    @Override
                    public void accept(OTAResult otaResult) throws Exception {
                        LogUtils.e("服务器数据是   " + otaResult.toString());

                        if ("200".equals(otaResult.getCode()) ) {
                            //请求成功
                            String fileUrl = otaResult.getData().getFileUrl();
                            if (!TextUtils.isEmpty(fileUrl)){
                                if (!fileUrl.startsWith("http://")){
                                    otaResult.getData().setFileUrl("http://"+fileUrl);
                                }
                            }
                            if (mViewRef.get() != null) {
                                mViewRef.get().onGetOtaInfoSuccess(otaResult.getData());
                            }
                        } else {
                            if (mViewRef.get() != null) {
                                mViewRef.get().onFailedServer(otaResult);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("异常检查版本   " +throwable.getMessage() );
                        if (mViewRef.get()!=null){
                            mViewRef.get().onFailed(throwable);
                        }
                    }
                });

        compositeDisposable.add(otaDisposable);


    }
}
