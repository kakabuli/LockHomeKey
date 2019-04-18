package com.kaadas.lock.mvp.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.mvp.view.IPersonalDataView;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 *
 */
public class PersonalDataPresenter<T> extends BasePresenter<IPersonalDataView> {
    //下载图片
    public void downloadPicture(String uid) {
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
  /*      XiaokaiNewServiceImp.downloadUserHead(uid, new ResourceObserver<ResponseBody>() {
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

    //上传图片
    public void uploadPicture(String uid, String path) {
        XiaokaiNewServiceImp.uploadPic(uid, path).subscribe(new BaseObserver<BaseResult>() {
            @Override
            public void onSuccess(BaseResult baseResult) {
                if (mViewRef != null) {
                    LogUtils.d("davi 图片上传成功");
                    mViewRef.get().photoUploadSuccess();
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (mViewRef.get() != null) {
                    LogUtils.d("davi 图片上传失败" + baseResult.toString());
                    mViewRef.get().photoUploadFail(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                LogUtils.d("davi 图片上传异常 " + throwable.toString());
                if (mViewRef.get() != null) {
                    mViewRef.get().photoUploadError(throwable);
                }

            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        });


    }


}
