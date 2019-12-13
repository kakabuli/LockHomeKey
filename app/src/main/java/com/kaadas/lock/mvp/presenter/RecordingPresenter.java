package com.kaadas.lock.mvp.presenter;

import android.content.Context;

import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.IDeviceView;
import com.kaadas.lock.mvp.view.IRecordingView;
import com.kaadas.lock.utils.RxUtil;
import com.kaadas.lock.utils.db.MediaFileDBDao;
import com.kaadas.lock.utils.db.MediaItem;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Create By denganzhi  on 2019/5/5
 * Describe
 */

public class RecordingPresenter<T> extends BasePresenter<IRecordingView> {


    private MediaFileDBDao mMediaDBDao;

    public void fetchVideoAndImage(String deviceId, Context context) {

        mMediaDBDao = MediaFileDBDao.getInstance(context);

        Disposable disposable = mMediaDBDao.ObservableFindAllByDeviceId(deviceId)
                .compose(RxUtil.<ArrayList<MediaItem>>applySchedulersRx2())
                .subscribe(new Consumer<ArrayList<MediaItem>>() {
                    @Override
                    public void accept(final ArrayList<MediaItem> mediaItems) throws Exception {
//                        context.runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                view.bindMediaOnView(mediaItems);
//                            }
//
//                        });

                        if (isSafe()) {
                            mViewRef.get().showFetchResult(mediaItems);
                        }

                    }
                });
        compositeDisposable.add(disposable);
    }


    public void deleteVideoAndImage(String name, Context context) {
        mMediaDBDao = MediaFileDBDao.getInstance(context);
        boolean isDeleteFlag = mMediaDBDao.deleteFileByName(name);
        if (isSafe()) {
            mViewRef.get().deleteResult(isDeleteFlag);
        }
    }


}
