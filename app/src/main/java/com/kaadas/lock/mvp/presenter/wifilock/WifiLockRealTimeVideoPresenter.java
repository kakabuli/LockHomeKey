package com.kaadas.lock.mvp.presenter.wifilock;

import android.view.SurfaceView;

import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockRealTimeVideoView;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.utils.LogUtils;
import com.xmitech.sdk.AudioFrame;
import com.xmitech.sdk.H264Frame;
import com.xmitech.sdk.interfaces.AVFilterListener;

import io.reactivex.disposables.Disposable;

public class WifiLockRealTimeVideoPresenter<T> extends BasePresenter<IWifiLockRealTimeVideoView> {
    private String WiFiSn;
    private Disposable startConnecrDisposable;

    public void init(String wiFiSn){
        WiFiSn = wiFiSn;
    }



    public void startRealTimeVideo(SurfaceView surfaceView){
        if(XMP2PManager.getInstance().isConnected(-1)){
            LogUtils.e("startRealTimeVideo");
            XMP2PManager.getInstance().play();
            XMP2PManager.getInstance().setSurfaceView(surfaceView);
            XMP2PManager.getInstance().getInstance().startVideoStream();
            XMP2PManager.getInstance().setAVFilterListener(new AVFilterListener() {
                @Override
                public void onAudioRecordData(AudioFrame audioFrame) {

                }

                @Override
                public void onVideoFrameUsed(H264Frame h264Frame) {

                }

                @Override
                public void onAudioFrameUsed(AudioFrame audioFrame) {

                }

                @Override
                public void onLastFrameRgbData(int[] ints, int i, int i1, boolean b) {

                }

                @Override
                public void onCodecNotify(int i, Object o) {

                }
            });
        }
    }

    @Override
    public void attachView(IWifiLockRealTimeVideoView view) {
        super.attachView(view);



        XMP2PManager.getInstance().setOnConnectStatusListener(listener);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void release(){
        XMP2PManager.getInstance().stopCodec();
    }

    XMP2PManager.ConnectStatusListener listener = new XMP2PManager.ConnectStatusListener() {
        @Override
        public void onConnectFailed(int paramInt) {

        }

        @Override
        public void onConnectSuccess() {

        }

        @Override
        public void onStartConnect(String paramString) {

        }

        @Override
        public void onErrorMessage(String message) {

        }

        @Override
        public void onNotifyGateWayNewVersion(String paramString) {

        }

        @Override
        public void onRebootDevice(String paramString) {

        }
    };


}
