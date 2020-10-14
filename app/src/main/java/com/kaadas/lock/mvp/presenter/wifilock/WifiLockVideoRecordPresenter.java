package com.kaadas.lock.mvp.presenter.wifilock;

import android.view.SurfaceView;

import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoCallingView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoRecordView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.publiclibrary.xm.bean.DeviceInfo;
import com.kaadas.lock.utils.LogUtils;
import com.xm.sdk.struct.stream.AVStreamHeader;
import com.xmitech.sdk.AudioFrame;
import com.xmitech.sdk.H264Frame;
import com.xmitech.sdk.MP4Info;
import com.xmitech.sdk.interfaces.AVFilterListener;
import com.xmitech.sdk.interfaces.VideoPackagedListener;

import io.reactivex.disposables.Disposable;

public class WifiLockVideoRecordPresenter<T> extends BasePresenter<IWifiLockVideoRecordView>  {
    private String WiFiSn;
    private Disposable startConnecrDisposable;

    private static  String did ="";//AYIOTCN-000337-FDFTF
    private static  String sn ="";//010000000020500020

    private static  String p2pPassword ="";//ut4D0mvz

    private static  String serviceString="EBGDEIBIKEJPGDJMEBHLFFEJHPNFHGNMGBFHBPCIAOJJLGLIDEABCKOOGILMJFLJAOMLLMDIOLMGBMCGIO";

    static int	m_handleSession	= -1;
    int			mChannel		= 0;

    @Override
    public void attachView(IWifiLockVideoRecordView view) {
        super.attachView(view);



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
            XMP2PManager.getInstance().setAudioFrame();
            XMP2PManager.getInstance().setSurfaceView(surfaceView);
            XMP2PManager.getInstance().play();
            XMP2PManager.getInstance().startVideoStream();


            XMP2PManager.getInstance().setOnAudioVideoStatusLinstener(new XMP2PManager.AudioVideoStatusListener() {
                @Override
                public void onVideoDataAVStreamHeader(AVStreamHeader paramAVStreamHeader) {
                    if(isSafe()){
//                        mViewRef.get().onVideoDataAVStreamHeader();
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
//                        mViewRef.get().onLastFrameRgbData(ints,height,width,b);
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




}
