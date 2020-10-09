package com.kaadas.lock.mvp.presenter.wifilock;

import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.widget.RelativeLayout;

import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IMyAlbumPlayerView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoFifthView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;

import java.io.File;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MyAlbumPlayerPresenter<T> extends BasePresenter<IMyAlbumPlayerView> {


    @Override
    public void attachView(IMyAlbumPlayerView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();

    }


}
