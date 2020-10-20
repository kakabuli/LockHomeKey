package com.kaadas.lock.mvp.presenter.wifilock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.SurfaceView;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiLockVideoCallingActivity;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoCallingView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetSingleFireSwitchBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.publiclibrary.xm.bean.DeviceInfo;
import com.kaadas.lock.utils.BitmapUtil;
import com.kaadas.lock.utils.LogUtils;
import com.xm.sdk.struct.stream.AVStreamHeader;
import com.xmitech.sdk.AudioFrame;
import com.xmitech.sdk.H264Frame;
import com.xmitech.sdk.MP4Info;
import com.xmitech.sdk.interfaces.AVFilterListener;
import com.xmitech.sdk.interfaces.VideoPackagedListener;
import com.yuv.display.MyBitmapFactory;


import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class WifiLockVideoCallingPresenter<T> extends BasePresenter<IWifiLockVideoCallingView>  {
    private String WiFiSn;
    private Disposable startConnecrDisposable;

    private static  String did ="";//AYIOTCN-000337-FDFTF
    private static  String sn ="";//010000000020500020

    private static  String p2pPassword ="";//ut4D0mvz

    private static  String serviceString=XMP2PManager.serviceString;;

    static int	m_handleSession	= -1;
    int			mChannel		= 0;

    @Override
    public void attachView(IWifiLockVideoCallingView view) {
        super.attachView(view);

    }

    XMP2PManager.ConnectStatusListener listener = new XMP2PManager.ConnectStatusListener() {
        @Override
        public void onConnectFailed(int paramInt) {
            XMP2PManager.getInstance().stopCodec();//
            LogUtils.e("shulan", this + "onConnectFailed: paramInt=" + paramInt);

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
        XMP2PManager.getInstance().setOnConnectStatusListener(listener);
        int param = XMP2PManager.getInstance().connectDevice(deviceInfo);

        return param;
    }


    public void release(){
        XMP2PManager.getInstance().stopCodec();
        XMP2PManager.getInstance().stopConnect();//
    }

    public void stopConnect(){
        XMP2PManager.getInstance().stopCodec();

    }

    public void startRealTimeVideo(SurfaceView surfaceView){
        LogUtils.e("shulan isConnect--> " + XMP2PManager.getInstance().isConnected(-1));
//        if(XMP2PManager.getInstance().isConnected(-1)){
            LogUtils.e("startRealTimeVideo");
            XMP2PManager.getInstance().setRotate(XMP2PManager.SCREEN_ROTATE);
            try {
                XMP2PManager.getInstance().setAudioFrame();
            }catch (java.lang.NegativeArraySizeException e){
                if(isSafe()){
                    mViewRef.get().recordAudidFailed();
                }
            }

            XMP2PManager.getInstance().setSurfaceView(surfaceView);
            XMP2PManager.getInstance().play();
            XMP2PManager.getInstance().startVideoStream();


            XMP2PManager.getInstance().setOnAudioVideoStatusLinstener(new XMP2PManager.AudioVideoStatusListener() {
                @Override
                public void onVideoDataAVStreamHeader(AVStreamHeader paramAVStreamHeader) {
                    if(isSafe()){
                        mViewRef.get().onVideoDataAVStreamHeader(paramAVStreamHeader);
                    }
                }
            });
            XMP2PManager.getInstance().setAVFilterListener(new AVFilterListener() {
                @Override
                public void onAudioRecordData(AudioFrame audioFrame) {

                    //frame为采集封装的数据,通过传输库发送给设备
                    int ret = XMP2PManager.getInstance().sendTalkBackAudioData(audioFrame);
                    LogUtils.e("shulan onAudioRecordData length-->" +audioFrame.getAudioBuff().length);
                    LogUtils.e("shulan onAudioRecordData ret-->" +ret);
                    LogUtils.e("shulan onAudioRecordData frameType-->" +audioFrame.frameType);
                    LogUtils.e("shulan onAudioRecordData frameRate-->" +audioFrame.frameRate);
                }

                @Override
                public void onVideoFrameUsed(H264Frame h264Frame) {

                }

                @Override
                public void onAudioFrameUsed(AudioFrame audioFrame) {

                }

                @Override
                public void onLastFrameRgbData(int[] ints, int height, int width, boolean b) {
                    if(isSafe()){
                        mViewRef.get().onLastFrameRgbData(ints,height,width,b);
                    }

                }

                @Override
                public void onCodecNotify(int i, Object o) {

                }


            });
//        }
    }

    public void settingDevice(WifiLockInfo wifiLockInfo) {
        did = wifiLockInfo.getDevice_did();
        sn = wifiLockInfo.getDevice_sn();
        p2pPassword = wifiLockInfo.getP2p_password();

    }

    public void snapImage(){
        XMP2PManager.getInstance().snapImage();
    }

    public int startAudioStream(){
        return XMP2PManager.getInstance().startAudioStream();
    }

    public int stopAudioStream(){
        return XMP2PManager.getInstance().stopAudioStream();
    }

    public boolean isEnableAudio(){
       return XMP2PManager.getInstance().isEnableAudio();
    }

    public void enableAudio(boolean flag){
        try {
            XMP2PManager.getInstance().enableAudio(flag);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public boolean isTalkback(){
        try {
            return XMP2PManager.getInstance().isTalkback();
        }catch (java.lang.NegativeArraySizeException e){
            if(isSafe()){
                mViewRef.get().recordAudidFailed();
            }
            return true;
        }

    }

    public void talkback(boolean flag){
        try {
            XMP2PManager.getInstance().talkback(flag);;
        }catch (java.lang.NegativeArraySizeException e){
            if(isSafe()){
                mViewRef.get().recordAudidFailed();
            }
        }

    }

    public void startRecordMP4(String filePath){
        XMP2PManager.getInstance().startRecordMP4(filePath,0,0,0,XMP2PManager.SCREEN_ROTATE);
        XMP2PManager.getInstance().setVideoPackagedListener(new VideoPackagedListener() {
            @Override
            public void onStartedPackaged() {
                LogUtils.e("shulan 开始录制");
                if(isSafe()){
                    mViewRef.get().onstartRecordMP4CallBack();
                }
            }

            @Override
            public void onStopPackaged(MP4Info mp4Info) {
                LogUtils.e("shulan mp4Info-->" +mp4Info.toString());
                if(isSafe()){
                    mViewRef.get().onStopRecordMP4CallBack(mp4Info);
                }
            }
        });
    }

    public void stopRecordMP4(){
        XMP2PManager.getInstance().stopRecordMP4();
    }

    public int[] getVideoResolution(){
        return XMP2PManager.getInstance().getVideoResolution();
    }

    public void startTalkback(){
        try {
            XMP2PManager.getInstance().startTalkback();
        }catch (java.lang.NegativeArraySizeException e){
            if(isSafe()){
                mViewRef.get().recordAudidFailed();
            }
        }

    }

    public void stopTalkback(){
        XMP2PManager.getInstance().stopTalkback();
    }

    public void setAECM(boolean falg){
        XMP2PManager.getInstance().setAECM(falg);
    }


}
