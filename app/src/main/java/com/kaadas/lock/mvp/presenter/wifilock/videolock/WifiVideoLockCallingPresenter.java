package com.kaadas.lock.mvp.presenter.wifilock.videolock;

import android.view.SurfaceView;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoCallingView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.WifiLockOperationBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.xm.XMP2PConnectError;
import com.kaadas.lock.publiclibrary.xm.XMP2PConnectJsonError;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.publiclibrary.xm.bean.DeviceInfo;
import com.kaadas.lock.publiclibrary.xm.bean.XMConnectErrorBean;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.MyLog;
import com.xm.sdk.struct.stream.AVStreamHeader;
import com.xmitech.sdk.AudioFrame;
import com.xmitech.sdk.H264Frame;
import com.xmitech.sdk.MP4Info;
import com.xmitech.sdk.VideoBasic;
import com.xmitech.sdk.interfaces.AVFilterListener;
import com.xmitech.sdk.interfaces.VideoPackagedListener;
import com.xmitech.sdk.utlis.CodecConstant;


import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class WifiVideoLockCallingPresenter<T> extends BasePresenter<IWifiLockVideoCallingView>  {
    private String WiFiSn;
    private Disposable startConnecrDisposable;

    private static  String did ="";//AYIOTCN-000337-FDFTF
    private static  String sn ="";//010000000020500020

    private static  String p2pPassword ="";//ut4D0mvz

    private static  String serviceString=XMP2PManager.serviceString;;

    private static int errno = 0;

    private Disposable wifiVideoLockStatusListenDisposable;

    static int	m_handleSession	= -1;
    int			mChannel		= 0;

    private static final long OVER_TIME_SECONDS = 30000;
    private static final int OVER_TIME_TIMES = 10;
    private long startTime = 0;
    private int connectTimes = 0;
    private boolean isFinish = false;

    @Override
    public void attachView(IWifiLockVideoCallingView view) {
        super.attachView(view);
        listenWifiLockStatus();
    }

    public void listenWifiLockStatus() {
        if (mqttService != null) {
            toDisposable(wifiVideoLockStatusListenDisposable);
            wifiVideoLockStatusListenDisposable = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.FUNC_WFEVENT)) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {

                            String payload = mqttData.getPayload();
                            WifiLockOperationBean wifiLockOperationBean = new Gson().fromJson(payload, WifiLockOperationBean.class);
                            if (wifiLockOperationBean != null) {
                                if(wifiLockOperationBean.getDevtype().equals(MqttConstant.WIFI_VIDEO_LOCK_XM) &&
                                        wifiLockOperationBean.getEventtype().equals(MqttConstant.WIFI_LOCK_RECORD)){
                                    if( wifiLockOperationBean.getEventparams() != null){
                                        if(wifiLockOperationBean.getEventparams().getEventType() == 1){
                                            if(isSafe()){
                                                mViewRef.get().openDoor(wifiLockOperationBean.getEventparams());
                                            }
                                        }
                                    }
                                }

                            }


                        }
                    });
            compositeDisposable.add(wifiVideoLockStatusListenDisposable);
        }
    }



    XMP2PManager.ConnectStatusListener listener = new XMP2PManager.ConnectStatusListener() {
        @Override
        public void onConnectFailed(int paramInt) {
            XMP2PManager.getInstance().stopCodec();//
            if(paramInt == XMP2PConnectError.XM_DYNAMIC_LIBRARY_NOT_INITIALIZED){
                XMP2PManager.getInstance().initAPI(MyApplication.getInstance(),serviceString);
                XMP2PManager.getInstance().init(MyApplication.getInstance());
            }
            if(errno == XMP2PConnectJsonError.XM_JSON_ERROR_ACCESS_PASSWORD_ERROR || errno == XMP2PConnectJsonError.XM_JSON_ERROR_CHANNEL_SESSION_FULL){
                if(isSafe()){
                    mViewRef.get().onConnectFailed(errno);
                    errno = 0;
                }
                return;
            }
            if(isFinish){
                if(isSafe()){
                    mViewRef.get().onConnectFailed(paramInt);
                }
                return;
            }
            if((startTime > 0 && System.currentTimeMillis() - startTime > OVER_TIME_SECONDS) || connectTimes > OVER_TIME_TIMES){
                LogUtils.e("shulan 111---------->onConnectFailed=" + paramInt);
                if(isSafe()){
                    mViewRef.get().onConnectFailed(paramInt);
                }
            }else{
                LogUtils.e("shulan 222---------->onConnectFailed=" + paramInt);
                connectTimes++;
                connectP2P();
            }

        }

        @Override
        public void onConnectSuccess() {

            if(isSafe()){
                mViewRef.get().onConnectSuccess();
            }
            XMP2PManager.getInstance().getDeviceInformation();
        }

        @Override
        public void onStartConnect(String paramString) {
            if(isSafe()){
                mViewRef.get().onStartConnect(paramString);
            }

        }

        @Override
        public void onErrorMessage(String message) {
            try {
                XMConnectErrorBean xmConnectErrorBean = new Gson().fromJson(message, XMConnectErrorBean.class);
                if(xmConnectErrorBean.getResult().equals("failed")){
                    errno = xmConnectErrorBean.getErrno();
                }
            }catch (Exception e){
            }
//            stopConnect();
            if(isSafe()){
                mViewRef.get().onErrorMessage(errno);
            }

        }

        @Override
        public void onNotifyGateWayNewVersion(String paramString) {

        }

        @Override
        public void onRebootDevice(String paramString) {

        }
    };

    @Override
    public void detachView() {
        super.detachView();
    }

    public synchronized int connectP2P(){

        DeviceInfo deviceInfo=new DeviceInfo();
        deviceInfo.setDeviceDid(did);
        deviceInfo.setP2pPassword(p2pPassword);
        deviceInfo.setDeviceSn(sn);
        deviceInfo.setServiceString(serviceString);
        XMP2PManager.getInstance().setOnConnectStatusListener(listener);
        LogUtils.e("shulan XMP2PManager.getInstance()------->" + XMP2PManager.getInstance());
        int param = XMP2PManager.getInstance().connectDevice(deviceInfo);
        return param;
    }


    public void release(){
        LogUtils.e("shulan callingActivity release");
        XMP2PManager.getInstance().stopConnect();//
        XMP2PManager.getInstance().stopCodec();
        this.startTime = 0;
        this.connectTimes = 0;
        this.isFinish = true;
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
            XMP2PManager.getInstance().setAECM(true);
            XMP2PManager.getInstance().startVideoStream();

            XMP2PManager.getInstance().setAVFilterListener(new AVFilterListener() {
                @Override
                public void onAudioRecordData(AudioFrame audioFrame) {

                    //frame为采集封装的数据,通过传输库发送给设备
                    int ret = XMP2PManager.getInstance().sendTalkBackAudioData(audioFrame);
                }
                long time=0;
                int n = 0 ;
                @Override
                public void onVideoFrameUsed(H264Frame h264Frame) {
                    // 1000 判断是不是都是16帧
//                    time = h264Frame.frameTimeStamp;
//                    h264Frame.getFrameRate();

//                    MyLog.getInstance().save("xmtest"+"FrameTimeStamp = " + h264Frame.frameTimeStamp + "--FrameRate = " + h264Frame.getFrameRate());
                    if(h264Frame.frameTimeStamp - time >= 1000){

                        LogUtils.e("xmtest"+"FrameTimeStamp = " + h264Frame.frameTimeStamp + "--FrameRate = " + n);
                        n = 0;
                        time = h264Frame.frameTimeStamp;
                    }else{
                        n++;
                    }
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

//                    LogUtils.e("xmtest--"+"i = " +  i);
                    if (i == CodecConstant.NOTIFY_RESOLUTION_CHANGE){
                        VideoBasic info=(VideoBasic)o;
                        if (info.getHeight()!=0&&info.getWidth()!=0){
//                            LogUtils.e("xmtest--"+"info.getHeight() = " + info.getHeight());
//                            LogUtils.e("xmtest--"+"info.getWidth() = " + info.getWidth());
                            XMP2PManager.getInstance().setOnAudioVideoStatusLinstener(new XMP2PManager.AudioVideoStatusListener() {
                                @Override
                                public void onVideoDataAVStreamHeader(AVStreamHeader paramAVStreamHeader) {
                                    if(isSafe()){
                                        mViewRef.get().onVideoDataAVStreamHeader(paramAVStreamHeader);
                                    }
                                }
                            });
                        }

                    }

                }

            });

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

                if(isSafe()){
                    mViewRef.get().onstartRecordMP4CallBack();
                }
            }

            @Override
            public void onStopPackaged(MP4Info mp4Info) {

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


    public void setStartTime() {
        this.startTime = System.currentTimeMillis();
        this.connectTimes = 0;
        this.isFinish = false;
    }
}
