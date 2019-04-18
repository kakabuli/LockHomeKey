package com.kaadas.lock.mvp.view.personalview;

import android.graphics.Bitmap;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface IPersonalVerifyFingerPrintView extends IBaseView {

    void downloadPhoto(Bitmap bitmap);

    void downloadPhotoError(Throwable e);
}
