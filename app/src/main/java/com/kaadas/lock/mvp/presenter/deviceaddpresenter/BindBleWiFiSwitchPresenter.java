package com.kaadas.lock.mvp.presenter.deviceaddpresenter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.activity.choosewifi.WifiBean;
import com.kaadas.lock.activity.device.wifilock.wifilist.BleWifiListDataParser;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IBindBleView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.RetryWithTime;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleStateBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.OfflinePasswordFactorManager;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Create By hushucong  on 2020/6/5
 * Describe 蓝牙wifi单火开关绑定流程
 *
 */
public class BindBleWiFiSwitchPresenter<T> extends BasePresenter<IBindBleView> implements BleWifiListDataParser.WifiListDataCallback{

    protected BleLockInfo bleLockInfo;
    private int bleVersion;
    private String mac;
    private String deviceName;
    private int functionSet = -1;
    private Disposable characterNotifyDisposable;
    private Disposable listenConnectStateDisposable;
    private Disposable featureSetDisposable;
    private Disposable wifiDataDisposable;
    private OfflinePasswordFactorManager offlinePasswordFactorManager = OfflinePasswordFactorManager.getInstance();
    private OfflinePasswordFactorManager.OfflinePasswordFactorResult wifiResult;
    private int index;//命令包序号
    private WifiScanResultListener scanWifiResultListener;
    private BleWifiListDataParser bleWifiListDataParser;
    private static final String TAG = "BindBleWiFiSwitchPresenter";

    public interface WifiScanResultListener{
        void onWifiScanResult(List<WifiBean> data);
    }

    public void listenerCharacterNotify() {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        toDisposable(characterNotifyDisposable);

        LogUtils.i(TAG,"--kaadas--subscribe listenerCharacterNotify");
        characterNotifyDisposable = bleService.listeneDataChange()

                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        //收到入网数据
                        byte[] originalData = bleDataBean.getOriginalData();
//                        LogUtils.e("--kaadas--收到锁的配网数据" + Rsa.bytesToHexString(originalData));
                        if ((originalData[3] & 0xff) == 0x95) {//接到剩余校验次数
                            LogUtils.i(TAG,"verify num " + (originalData[4] & 0xFF));
                            if(isSafe()) mViewRef.get().onlistenerLastNum(originalData[4] & 0xFF);
                        }
                        if ((originalData[3] & 0xff) == 0x92) {//离线密码因子
                            //1.0x92有可能在订阅前就已经发出，导致接收到不完整数据包
                            //2.客户端解密因子验证失败0x94，设备会重新发一遍0x92密码因子
                            handlePassWordFactor(bleDataBean);
                        }
                        if ((originalData[3] & 0xff) == 0x94) {//收到解密结果
                            checkAdminPassWordResult();
                        }
                        if ((originalData[3] & 0xff) == 0x98) {//收到wifi列表结果
                            //handleWifiListData(bleDataBean);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        LogUtils.e(TAG,"--kaadas-- accept notification exception: " + throwable.getMessage());
                    }
                });
        compositeDisposable.add(characterNotifyDisposable);
    }


    public void listenerWifiListNotify() {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        toDisposable(wifiDataDisposable);

        LogUtils.i(TAG,"--kaadas--subscribe listenerCharacterNotify");
        wifiDataDisposable = bleService.listenerWifiData()

                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        //收到入网数据
                        byte[] originalData = bleDataBean.getOriginalData();
                        if ((originalData[3] & 0xff) == 0x98) {//收到wifi列表结果
                            handleWifiListData(bleDataBean);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        LogUtils.e(TAG,"--kaadas-- accept notification exception: " + throwable.getMessage());
                    }
                });
        compositeDisposable.add(wifiDataDisposable);
    }

    private synchronized void handleWifiListData(BleDataBean bleDataBean){
        if(bleWifiListDataParser == null){
            bleWifiListDataParser = new BleWifiListDataParser();
            bleWifiListDataParser.setWifiListDataCallback(this);
        }

        try {
            bleWifiListDataParser.parseWifiListFromBle(bleDataBean.getPayload());
        }catch (Exception e){
            LogUtils.e(TAG,"--kaadas-- parseWifiList exception: " + e.toString());
            if(bleWifiListDataParser != null){
                bleWifiListDataParser.resetData();
            }
            if(scanWifiResultListener != null){
                scanWifiResultListener.onWifiScanResult(Collections.emptyList());
            }
        }
    }

    @Override
    public void onWifiData(ArrayList<WifiBean> data) {

        LogUtils.d(TAG,"--kaadas-- onWifiData, listener " + (scanWifiResultListener != null));
        if(scanWifiResultListener != null){
            scanWifiResultListener.onWifiScanResult(data);
        }
    }

    private void handlePassWordFactor(BleDataBean bleDataBean){
        byte[] originalData = bleDataBean.getOriginalData();
        int pswLen = originalData[4];
        index = originalData[5];
        //新数组
        byte[] passwordFactor = new byte[originalData.length - 6];
        //从原始数组4位置开始截取后面所有
        System.arraycopy(originalData, 6, passwordFactor, 0, originalData.length - 6);
//                            LogUtils.e("--kaadas--密码因子分包数据==    " + Rsa.bytesToHexString(passwordFactor));
        if(isSafe()) mViewRef.get().onlistenerPasswordFactor(passwordFactor, pswLen, index);
    }

    public void parsePasswordFactorData(String adminPassword, byte[] data) {
        wifiResult = OfflinePasswordFactorManager.parseOfflinePasswordFactorData(adminPassword, data);
        LogUtils.e(TAG,"--Kaadas--wifiResult："+wifiResult.result);

        //发送0x94下发密码因子校验结果 通知锁端
        if (wifiResult.result == 0){
            //校验成功
            if(isSafe()) mViewRef.get().onDecodeResult(2,wifiResult);

            String wifiSN = getWifiSN(wifiResult);
            if(BleLockUtils.isFuncSetB9(wifiResult.func) && !TextUtils.isEmpty(wifiSN)){
                //k30系列 0xB9 配网流程变更 解绑后再发送0x94
                LogUtils.i(TAG,"--Kaadas--, do preBind");
                unbindLock(wifiSN);
                return;
            }

            replayPasswordFactorCmd(0);
        }
        else {
            //校验失败
            if(isSafe()) mViewRef.get().onDecodeResult(-1,wifiResult);
            replayPasswordFactorCmd(1);
        }
    }

    private void replayPasswordFactorCmd(int result){
        //result 0成功，1失败
        if(result != 0){
            wifiResult = null;
        }
        byte[] authKey = null;//不加密
        byte[] command = BleCommandFactory.onDecodeResult(authKey,Rsa.int2BytesArray(result)[0]);
        bleService.sendCommand(command);
        LogUtils.i(TAG,"--Kaadas--replay factor, decodeResult=" + result);
    }

    public void readFeatureSet(){
        readFeatureSet(0);
    }

    public void readFeatureSet(long delay){
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        toDisposable(featureSetDisposable);
        featureSetDisposable = bleService.readFunctionSet(delay)
                .filter(new Predicate<ReadInfoBean>() {
                    @Override
                    public boolean test(ReadInfoBean readInfoBean) throws Exception {
                        return readInfoBean.type == ReadInfoBean.TYPE_LOCK_FUNCTION_SET;
                    }
                })
                .timeout(2 * 1000, TimeUnit.MILLISECONDS)
                .retryWhen(new RetryWithTime(2, 0))
                .subscribe(new Consumer<ReadInfoBean>() {
                    @Override
                    public void accept(ReadInfoBean readInfoBean) throws Exception {
                        toDisposable(featureSetDisposable);

                        functionSet = (int) readInfoBean.data;
                        LogUtils.e("--kaadas--BLE&wifi锁功能集==" + functionSet);
                        if (isSafe()) {
                            mViewRef.get().readFunctionSetSuccess(functionSet);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(featureSetDisposable);
    }

    public void checkAdminPassWordResult() {

            if (wifiResult != null){
                if(isSafe()) mViewRef.get().onDecodeResult(3, wifiResult);
            }

    }

    public void disconnectBLE() {

        if (bleService != null) { //停止扫描设备
            bleService.scanBleDevice(false);  //1
            bleService.release();
        }
    }
    public void listenConnectState() {
        toDisposable(listenConnectStateDisposable);
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        listenConnectStateDisposable = bleService.subscribeDeviceConnectState() //1
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleStateBean>() {
                    @Override
                    public void accept(BleStateBean bleStateBean) throws Exception {
                        if (isSafe()) {
                            mViewRef.get().onDeviceStateChange(bleStateBean.isConnected());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(listenConnectStateDisposable);
    }

    private String getWifiSN(OfflinePasswordFactorManager.OfflinePasswordFactorResult wifiResult){
        if(wifiResult.wifiSn == null || wifiResult.wifiSn.length == 0){
            return "";
        }
        return new String(wifiResult.wifiSn);
    }

    private void unbindLock(String wifiSn){
        //调用预绑定设备接口（解绑设备）
        XiaokaiNewServiceImp.preBind(wifiSn, MyApplication.getInstance().getUid())
                .retry(3)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        LogUtils.i("--Kaadas--preBind device success");
                        //解绑成功回复0x94
                        replayPasswordFactorCmd(0);

                        //需要解绑后清除本地数据？
                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                        SPUtils.remove(KeyConstants.WIFI_LOCK_ALARM_RECORD + wifiSn);
                        SPUtils.remove(KeyConstants.WIFI_LOCK_OPERATION_RECORD + wifiSn);
                        SPUtils.remove(KeyConstants.WIFI_LOCK_OPEN_COUNT + wifiSn);
                        SPUtils.remove(KeyConstants.WIFI_LOCK_SHARE_USER_LIST + wifiSn);
                        SPUtils.remove(KeyConstants.WIFI_LOCK_PASSWORD_LIST + wifiSn);

                        int lockType = MyApplication.getInstance().getWifiVideoLockTypeBySn(wifiSn);
                        if(lockType == HomeShowBean.TYPE_WIFI_VIDEO_LOCK){
                            SPUtils.remove(KeyConstants.WIFI_VIDEO_LOCK_VISITOR_RECORD + wifiSn);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        LogUtils.e("--Kaadas-- preBind device error："+ baseResult.getCode());
                        if (isSafe()) {
                            //解绑设备失败返回状态-2
                            mViewRef.get().onDecodeResult(-2,wifiResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("--Kaadas-- preBind device error："+ throwable.getMessage());
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }

    /**
     * 从设备获取wifi列表
     */
    public void getWifiListFromDevice(){
        byte[] authKey = null;//不加密
        byte[] command = BleCommandFactory.getDeviceWifiList(authKey);
        bleService.sendCommand(command);
        if(bleWifiListDataParser != null){
            bleWifiListDataParser.resetData();
        }
    }

    /**
     * 从手机扫描获取wifi列表
     */
    public void getWifiListFromApp(){
        WifiManager wifiManager = (WifiManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        List<ScanResult> scanResults = wifiManager.getScanResults();
        ArrayList<WifiBean> wifiBeanList = new ArrayList<>();
        if(scanResults != null && scanResults.size() > 0){
            for (ScanResult result : scanResults) {
                int frequency = result.frequency;
                if(!(frequency >= 2400 && frequency <= 2500)){
                    //排除掉非2.4g的
                    continue;
                }
                String ssid = result.SSID;
                if(TextUtils.isEmpty(ssid)){
                    //过滤ssid为空的
                    continue;
                }
                int level = WifiManager.calculateSignalLevel(result.level, 5);
                wifiBeanList.add(new WifiBean(level, ssid));
            }
        }
        onWifiData(wifiBeanList);
    }

    public void setWifiScanResultListener(WifiScanResultListener listener){
        scanWifiResultListener = listener;
    }
}
