package com.kaadas.lock.view.personalview;

import android.graphics.Bitmap;

import com.kaadas.lock.base.mvpbase.IBaseView;

public interface IPersonalVerifyFingerPrintView extends IBaseView {

    void downloadPhoto(Bitmap bitmap);

    void downloadPhotoError(Throwable e);
}
