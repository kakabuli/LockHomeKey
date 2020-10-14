package com.kaadas.lock.mvp.presenter.wifilock;

import android.os.Environment;

import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockAlarmRecordView;
import com.kaadas.lock.mvp.view.wifilock.IWifiVideoLockAlarmRecordView;
import com.kaadas.lock.publiclibrary.bean.WifiLockAlarmRecord;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.bean.WifiVideoLockAlarmRecord;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetWifiLockAlarmRecordResult;
import com.kaadas.lock.publiclibrary.http.result.GetWifiVideoLockAlarmRecordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.publiclibrary.xm.bean.DeviceInfo;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.xmitech.sdk.MP4Info;
import com.xmitech.sdk.interfaces.VideoPackagedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class WifiVideoLockAlarmRecordPresenter<T> extends BasePresenter<IWifiVideoLockAlarmRecordView> {

    private static  String did ="";//AYIOTCN-000337-FDFTF
    private static  String sn ="";//010000000020500020

    private static  String p2pPassword ="";//ut4D0mvz

    private static  String serviceString="EBGDEIBIKEJPGDJMEBHLFFEJHPNFHGNMGBFHBPCIAOJJLGLIDEABCKOOGILMJFLJAOMLLMDIOLMGBMCGIO";


    private List<WifiVideoLockAlarmRecord> wifiVideoLockAlarmRecords = new ArrayList<>();

    public void getWifiVideoLockGetAlarmList(int page, String wifiSn) {
        if (page == 1) {
            wifiVideoLockAlarmRecords.clear();
        }
        XiaokaiNewServiceImp.wifiVideoLockGetAlarmList(wifiSn,page).subscribe(new BaseObserver<GetWifiVideoLockAlarmRecordResult>() {
            @Override
            public void onSuccess(GetWifiVideoLockAlarmRecordResult getWifiVideoLockAlarmRecordResult) {
                List<WifiVideoLockAlarmRecord> alarmRecords = getWifiVideoLockAlarmRecordResult.getData();
                if (alarmRecords != null && alarmRecords.size() > 0) {
                    if (page == 1) {
                        String object = new Gson().toJson(alarmRecords);
                        SPUtils.put(KeyConstants.WIFI_VIDEO_LOCK_ALARM_RECORD + wifiSn, object);
                    }

                    wifiVideoLockAlarmRecords.addAll(alarmRecords);
                    for (int i = 0 ; i <wifiVideoLockAlarmRecords.size() ;i++){
                        LogUtils.e("shulan wifiVideoLockAlarmRecords-"+ i +"->" + wifiVideoLockAlarmRecords.get(i).toString());
                    }

                    if (isSafe()) {
                        mViewRef.get().onLoadServerRecord(wifiVideoLockAlarmRecords, page);
                    }
                } else {
                    if (page == 1) {
                        SPUtils.put(KeyConstants.WIFI_VIDEO_LOCK_ALARM_RECORD + wifiSn, "");
                    }
                    if (isSafe()) {//服务器没有数据  提示用户

                        if (page == 1) { //第一次获取数据就没有
                            mViewRef.get().onServerNoData();
                        } else {
                            mViewRef.get().noMoreData();
                        }
                    }
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (isSafe()) {  //
                    mViewRef.get().onLoadServerRecordFailedServer(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (isSafe()) {  //
                    mViewRef.get().onLoadServerRecordFailed(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        });
    }

    public void startRecordMP4(String filePath,String filename){
        XMP2PManager.getInstance().startRecordMP4(filePath,0,0,0,270);
        XMP2PManager.getInstance().setVideoPackagedListener(new VideoPackagedListener() {
            @Override
            public void onStartedPackaged() {
                LogUtils.e("shulan 开始录制");
                if(isSafe()){
//                    mViewRef.get().onstartRecordMP4CallBack();
                }
            }

            @Override
            public void onStopPackaged(MP4Info mp4Info) {
                LogUtils.e("shulan mp4Info-->" +mp4Info.toString());
                if(isSafe()){
                    mViewRef.get().onStopRecordMP4CallBack(mp4Info,filename);
                }
            }
        });
    }

    public void stopRecordMP4(){
        XMP2PManager.getInstance().stopRecordMP4();
    }

    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DCIM" + File.separator + "Camera" ;

    public void playDeviceRecordVideo(String fileName,String filaDate,String id,String createtime){
        XMP2PManager.getInstance().setRotate(XMP2PManager.SCREEN_ROTATE);
        XMP2PManager.getInstance().setAudioFrame();
        startRecordMP4(path +  File.separator + id + ".mp4",createtime);
        int ret = XMP2PManager.getInstance().playDeviceRecordVideo(filaDate,fileName,0,0);
        LogUtils.e("shulan playDeviceRecordVideo -- ret" + ret);
        XMP2PManager.getInstance().play();
//        XMP2PManager.getInstance().enableAudio(true);
        XMP2PManager.getInstance().setOnPlayDeviceRecordVideo(new XMP2PManager.PlayDeviceRecordVideo() {
            @Override
            public void onPlayDeviceRecordVideoProcResult(JSONObject jsonObject) {
                LogUtils.e("shulan onPlayDeviceRecordVideoProcResult--jsonObject-->" + jsonObject);
                try {
                    if(jsonObject.getString("result").equals("ok")){
//                        startRecordMP4(path +  File.separator + id + ".mp4");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onPlayRecViewCtrlResult(JSONObject jsonObject) {
                LogUtils.e("shulan onPlayRecViewCtrlResult--jsonObject-->" + jsonObject);
            }

            @Override
            public void onPushCmdRet(int cmdCode, JSONObject jsonString) {
                if(cmdCode == 101){
                    stopRecordMP4();
                }
            }
        });
    }

    public void release(){
        XMP2PManager.getInstance().stopCodec();
        XMP2PManager.getInstance().stopConnect();//
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

}
