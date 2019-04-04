package com.kaadas.lock.presenter.personalpresenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.kaadas.lock.base.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.view.personalview.IPersonalVerifyFingerPrintView;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class PersonalFingerPrintPresenter <T> extends BasePresenter<IPersonalVerifyFingerPrintView> {
    //下载图片
    public void downloadPicture(String uid) {
        // TODO: 2019/3/15    付积辉    此处订阅方法没有加入  complate   可能会导致内存泄漏  空指针异常
        XiaokaiNewServiceImp.downloadUserHead(uid).subscribe(new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(ResponseBody value) {
                LogUtils.d(" davi 下载成功:" + value);

                byte[] bys;
                try {
                    bys = value.bytes(); //注意：把byte[]转换为bitmap时，也是耗时操作，也必须在子线程
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bys, 0, bys.length);
                    LogUtils.d("davi 图片解析 bitmap " + bitmap.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //获取到图片显示
                            if (mViewRef != null) {
                                mViewRef.get().downloadPhoto(bitmap);
                            }

                        }
                    });
                } catch (Exception e) {
                    if (mViewRef != null) {
                        mViewRef.get().downloadPhotoError(e);
                    }
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.d("davi 图片路径失败 " + e);
                if (mViewRef != null) {
                    mViewRef.get().downloadPhotoError(e);
                }
            }

            @Override
            public void onComplete() {

            }
        });
     /*   XiaokaiNewServiceImp.downloadUserHead(uid, new ResourceObserver<ResponseBody>() {
            @Override
            public void onNext(ResponseBody value) {
                LogUtils.d(" davi 下载成功:" + value);

                byte[] bys;
                try {
                    bys = value.bytes(); //注意：把byte[]转换为bitmap时，也是耗时操作，也必须在子线程
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bys, 0, bys.length);
                    LogUtils.d("davi 图片解析 bitmap " + bitmap.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //获取到图片显示
                            if (mViewRef != null) {
                                mViewRef.get().downloadPhoto(bitmap);
                            }

                        }
                    });
                } catch (Exception e) {
                    if (mViewRef != null) {
                        mViewRef.get().downloadPhotoError(e);
                    }
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.d("davi 图片路径失败 " + e);
                if (mViewRef != null) {
                    mViewRef.get().downloadPhotoError(e);
                }

            }

            @Override
            public void onComplete() {

            }
        });*/
    }

}
