package com.kaadas.lock.mvp.view;

import android.graphics.Bitmap;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

/**
 * Create By David
 */
public interface IMyFragmentView extends IBaseView {


    /**
     * 下载的图片
     */
    void downloadPhoto(Bitmap bitmap);

    /**
     * 下载的图片失败
     */
    void downloadPhotoError(Throwable e);
}
