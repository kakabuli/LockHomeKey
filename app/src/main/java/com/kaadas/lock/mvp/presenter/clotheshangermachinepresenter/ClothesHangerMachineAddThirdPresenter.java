package com.kaadas.lock.mvp.presenter.clotheshangermachinepresenter;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.BluetoothLockBroadcastBean;
import com.kaadas.lock.bean.BluetoothLockBroadcastListBean;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.SearchBleWiFiDevicePresenter;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineAddSecondView;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineAddThirdView;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleStateBean;
import com.kaadas.lock.publiclibrary.http.IXiaoKaiNewService;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.ClothesHangerMachineCheckBindingResult;
import com.kaadas.lock.publiclibrary.http.temp.ILockService;
import com.kaadas.lock.publiclibrary.http.temp.postbean.CheckBind;
import com.kaadas.lock.publiclibrary.http.temp.resultbean.CheckBindResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.OtherException;
import com.kaadas.lock.publiclibrary.http.util.RetrofitServiceManager;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.clothesHangerMachineUtil.ClothesHangerMachineUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import okhttp3.RequestBody;
import retrofit2.Response;

public class ClothesHangerMachineAddThirdPresenter<T> extends BasePresenter<IClothesHangerMachineAddThirdView> {
    private List<BluetoothDevice> devices = new ArrayList<>();
    List<BluetoothLockBroadcastListBean> broadcastList = new ArrayList<>();
    List<BluetoothLockBroadcastBean> broadcastItemList = new ArrayList<>();
    private Disposable searchBluetoothDisposable;

    private int connectTimes = 0;
    private boolean isBind;
    private BluetoothDevice device;
    private Disposable bindDisposable;

    @Override
    public void attachView(IClothesHangerMachineAddThirdView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
        //界面退出的时候
        if (bleService != null) {
            bleService.scanBleDevice(false);
        }
        handler.removeCallbacks(releaseRunnable);
    }


    public void searchDevices() {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }

        if (devices != null || broadcastItemList != null) {  //每次重新搜索都清空搜索到的设备，然后传递给界面让界面刷新
            devices.clear();
            broadcastItemList.clear();//清空数据
            if (isSafe()) {
                LogUtils.e("--kaadas--每次重新搜索都清空搜索到的设备");
//                mViewRef.get().loadDevices(devices);
                mViewRef.get().loadBLEWiFiModelDevices(devices, broadcastItemList);

            }
        }
        LogUtils.e("--kaadas--搜索设备    断开连接");
        bleService.release();  //搜索设备    断开连接
        handler.removeCallbacks(stopScanLe);
        handler.postDelayed(stopScanLe, 180 * 1000);
        toDisposable(searchBluetoothDisposable);
        searchBluetoothDisposable = bleService.scanBleDevice(true)  //1
                .filter(new Predicate<BluetoothLockBroadcastBean>() {
                    @Override
                    public boolean test(BluetoothLockBroadcastBean broadcastBean) throws Exception {
                        BluetoothDevice device = broadcastBean.getDevice();

                        boolean contains;
                        synchronized (this) {
                            for (BluetoothDevice bluetoothDevice : devices) {
                                if (bluetoothDevice.getName().equals(device.getName())) {
//                                    LogUtils.e("--kaadas--相同device   " + device.getName());
                                    return false;
                                }
                            }

                            contains = devices.contains(device);
                        }

                        return !contains; //看是否包含有此设备
                    }
                })
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BluetoothLockBroadcastBean>() {
                    @Override
                    public void accept(BluetoothLockBroadcastBean broadcastBean) throws Exception {
                        BluetoothDevice device = broadcastBean.getDevice();
                        LogUtils.e("shulan broadcastBean.getDeviceSN()-->" + broadcastBean.getDeviceSN());
                        LogUtils.e("shulan --kaadas--搜索到设备   " + device.getName());
                        LogUtils.e("shulan --kaadas--搜索到设备   " + device.getAddress());
                        //过滤KLH / M08开头设备信息
                        if (!device.getName().startsWith("KLH") && !device.getName().startsWith("M08")) {
                            devices.add(device);
                            broadcastItemList.add(broadcastBean);
                            broadcastList.add(new BluetoothLockBroadcastListBean(broadcastItemList, devices));
                            //搜索到设备
                            if (mViewRef != null) {
                                mViewRef.get().loadBLEWiFiModelDevices(devices, broadcastItemList);

                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("--kaadas--throwable==" + throwable);
                        if (mViewRef != null) {
                            mViewRef.get().onScanDevicesFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(searchBluetoothDisposable);
    }

    Runnable stopScanLe = new Runnable() {
        @Override
        public void run() {
            if (bleService != null) { //停止扫描设备
                bleService.scanBleDevice(false);
                if (mViewRef != null) {
                    if (isAttach) {
                        LogUtils.e("--kaadas--设备停止扫描");
                        LogUtils.e("--kaadas--mViewRef==" + mViewRef);
                        mViewRef.get().onStopScan();
                    }
                }
            }
        }
    };

    public void checkBind(String wifiModelType, BluetoothLockBroadcastBean device) {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }

        handler.removeCallbacks(stopScanLe);
        if (bleService != null) { //停止扫描设备
            bleService.scanBleDevice(false);  //1
            LogUtils.e("点击绑定设备   断开连接");
            bleService.release();  //点击绑定设备   断开连接
            if (mViewRef != null) {
                mViewRef.get().onStopScan();
            }
        }

        String name = "";

        List<BluetoothDevice> mItemList;
        int[] list = new int[2];
        for (int i = 0; i < broadcastList.size(); i++) {
            mItemList = broadcastList.get(i).getDevices();
            for (int j = 0; j < mItemList.size(); j++) {
                if (device.getDeviceName().equals(mItemList.get(j).getName())) {
                    if (device.getDeviceModel() != null && device.getDeviceSN() != null) {
                        list[0] = i;
                        list[1] = j;
                        name = device.getDeviceSN();
                    }
                }
            }
        }

        XiaokaiNewServiceImp.clothesHangerMachineCheckBinding(name).subscribe(new BaseObserver<ClothesHangerMachineCheckBindingResult>() {
            @Override
            public void onSuccess(ClothesHangerMachineCheckBindingResult clothesHangerMachineCheckBindingResult) {
                if (clothesHangerMachineCheckBindingResult.getCode().equals("201")) {
                    if (isSafe()) {
                        mViewRef.get().onNoBind(broadcastList.get(list[0]).getDevices().get(list[1]));
                    }
                } else if (clothesHangerMachineCheckBindingResult.getCode().equals("202")) {
                    if (isSafe()) {
                        mViewRef.get().onAlreadyBind(broadcastList.get(list[0]).getDevices().get(list[1]), clothesHangerMachineCheckBindingResult.getData().getUname() + "");
                    }
                } else if ("444".equals(clothesHangerMachineCheckBindingResult.getCode())) {
                    if (mqttService != null) {
                        mqttService.httpMqttDisconnect();
                    }
                    MyApplication.getInstance().tokenInvalid(true);
                    return;
                } else {
                    if (isSafe()) {
                        mViewRef.get().checkBindFailed();
                    }
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {

            }

            @Override
            public void onFailed(Throwable throwable) {
                if (isSafe()) {
                    mViewRef.get().onCheckBindFailed(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {

            }
        });
    }

    private Runnable releaseRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtils.e("延时断开连接  ");
            //如果此时没有连接上设备，那么结束连接   释放连接资源
            if (isSafe()) {
                mViewRef.get().onConnectFailed();
            }
            if (bleService == null) { //判断
                if (MyApplication.getInstance().getBleService() == null) {
                    return;
                } else {
                    bleService = MyApplication.getInstance().getBleService();
                }
            }
            LogUtils.e("搜索设备  连接蓝牙时的延时   断开连接");
            bleService.release();  //搜索设备  连接蓝牙时的延时   断开连接  1
        }
    };

    public void bindDevice(BluetoothDevice device, boolean isBind) {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        LogUtils.e("开始绑定");
        this.isBind = isBind;
        this.device = device;
        if (connectTimes > 5) {
            if (isSafe()) {
                mViewRef.get().onConnectFailed();
            }
            return;
        }
        // 连接
        bleService.removeBleLockInfo(); //1
        handler.removeCallbacks(releaseRunnable);
        bleService.connectDeviceByDevice(device); //1
        handler.postDelayed(releaseRunnable, 15 * 1000);

        try {
            bindDisposable = bleService.subscribeDeviceConnectState() //1
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<BleStateBean>() {
                        @Override
                        public void accept(BleStateBean bleStateBean) throws Exception {
                            handler.removeCallbacks(releaseRunnable);
                            toDisposable(bindDisposable);
                            if (bleStateBean.isConnected()) {
                                LogUtils.e(ClothesHangerMachineAddThirdPresenter.class.getName() + "--kaadas--连接成功");
                                LogUtils.e("shulan ------getBleVersion----->" + bleStateBean.getBleVersion());
                                if (bleStateBean.getBleVersion() == 4) {

                                    if (isSafe()) {
                                        for (BluetoothLockBroadcastBean broadcastBean : broadcastItemList) {
                                            if (broadcastBean.getDevice().equals(device)) {
                                                mViewRef.get().onConnectBLEWIFISuccess(broadcastBean, bleStateBean.getBleVersion());
                                            }
                                        }
                                    }
                                }

                            } else {
                                connectTimes++;
                                LogUtils.e(ClothesHangerMachineAddThirdPresenter.class.getName() + "--kaadas--绑定界面连接失败");
                                bindDevice(device, isBind);
                            }
                        }
                    });
            compositeDisposable.add(bindDisposable);
        } catch (Exception e) {

        }
    }

    public void bindDeviceInit(BluetoothDevice device, boolean isBind) {
        connectTimes = 0;
        bindDevice(device, isBind);

    }
}
