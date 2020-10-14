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
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.RelativeLayout;

import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IMyAlbumPlayerView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoFifthView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.publiclibrary.xm.bean.DeviceInfo;
import com.kaadas.lock.utils.LogUtils;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;

import org.json.JSONObject;

import java.io.File;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MyAlbumPlayerPresenter<T> extends BasePresenter<IMyAlbumPlayerView> {


    private static  String did ="";//AYIOTCN-000337-FDFTF
    private static  String sn ="";//010000000020500020

    private static  String p2pPassword ="";//ut4D0mvz

    private static  String serviceString="EBGDEIBIKEJPGDJMEBHLFFEJHPNFHGNMGBFHBPCIAOJJLGLIDEABCKOOGILMJFLJAOMLLMDIOLMGBMCGIO";


    @Override
    public void attachView(IMyAlbumPlayerView view) {
        super.attachView(view);
        new Thread(new Runnable() {
            @Override
            public void run() {
                connectP2P();
            }
        }).start();
    }

    @Override
    public void detachView() {
        super.detachView();

    }

    public int connectP2P(){

        DeviceInfo deviceInfo=new DeviceInfo();
        deviceInfo.setDeviceDid(did);
        deviceInfo.setP2pPassword(p2pPassword);
        deviceInfo.setDeviceSn(sn);
        deviceInfo.setServiceString(serviceString);
        int param = XMP2PManager.getInstance().connectDevice(deviceInfo);
        XMP2PManager.getInstance().setOnConnectStatusListener(listener);
        return param;
    }

    XMP2PManager.ConnectStatusListener listener = new XMP2PManager.ConnectStatusListener() {
        @Override
        public void onConnectFailed(int paramInt) {
            XMP2PManager.getInstance().stopCodec();//
            LogUtils.e("shulan", "onConnectFailed: paramInt=" + paramInt);

            if(isSafe()){
                mViewRef.get().onConnectFailed(paramInt);
            }

        }

        @Override
        public void onConnectSuccess() {
            LogUtils.e("shulan","onConnectSuccess");
            if(isSafe()){
                mViewRef.get().onConnectSuccess();
            }

        }

        @Override
        public void onStartConnect(String paramString) {
            LogUtils.e("shulan","onStartConnect");
            if(isSafe()){
                mViewRef.get().onStartConnect(paramString);
            }

        }

        @Override
        public void onErrorMessage(String message) {
            LogUtils.e("shulan","onErrorMessage");
//            stopConnect();
            if(isSafe()){
                mViewRef.get().onErrorMessage(message);
            }

        }

        @Override
        public void onNotifyGateWayNewVersion(String paramString) {
            LogUtils.e("shulan","onNotifyGateWayNewVersion");
        }

        @Override
        public void onRebootDevice(String paramString) {
            LogUtils.e("shulan","onRebootDevice");
        }
    };

    public void settingDevice(WifiLockInfo wifiLockInfo) {
        did = wifiLockInfo.getDevice_did();
        sn = wifiLockInfo.getDevice_sn();
        p2pPassword = wifiLockInfo.getP2p_password();

    }

    public void release(){
        XMP2PManager.getInstance().stopCodec();
        XMP2PManager.getInstance().stopConnect();//
    }

    public void stopConnect(){
        XMP2PManager.getInstance().stopCodec();

    }

    public void playDeviceRecordVideo(String fileName,String filaDate){
        XMP2PManager.getInstance().setRotate(XMP2PManager.SCREEN_ROTATE);
        XMP2PManager.getInstance().setAudioFrame();
//        XMP2PManager.getInstance().setSurfaceView(surfaceView);

        int ret = XMP2PManager.getInstance().playDeviceRecordVideo(filaDate,fileName,0,0);
        LogUtils.e("shulan playDeviceRecordVideo -- ret" + ret);
        XMP2PManager.getInstance().play();
        XMP2PManager.getInstance().enableAudio(false);
        XMP2PManager.getInstance().setOnPlayDeviceRecordVideo(new XMP2PManager.PlayDeviceRecordVideo() {
            @Override
            public void onPlayDeviceRecordVideoProcResult(JSONObject jsonObject) {
                LogUtils.e("shulan onPlayDeviceRecordVideoProcResult--jsonObject-->" + jsonObject);
            }

            @Override
            public void onPlayRecViewCtrlResult(JSONObject jsonObject) {
                LogUtils.e("shulan onPlayRecViewCtrlResult--jsonObject-->" + jsonObject);
            }

            @Override
            public void onPushCmdRet(int cmdCode, JSONObject jsonString) {

            }
        });
    }

}
