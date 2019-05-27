package com.kaadas.lock.mvp.presenter.deviceaddpresenter;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.ISearchDeviceView;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleStateBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPwdBySnResult;
import com.kaadas.lock.publiclibrary.http.temp.ILockService;
import com.kaadas.lock.publiclibrary.http.temp.postbean.CheckBind;
import com.kaadas.lock.publiclibrary.http.temp.resultbean.CheckBindResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.OtherException;
import com.kaadas.lock.publiclibrary.http.util.RetrofitServiceManager;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * Create By lxj  on 2019/1/7
 * Describe
 */
public class SearchDevicePresenter<T> extends BasePresenter<ISearchDeviceView> {
    private List<BluetoothDevice> devices = new ArrayList<>();
    private Disposable disposable;
    private BluetoothDevice device;
    private byte[] bPwd1;
    private String pwd1;
    private Disposable snDisposable;
    private int readSnTimes = 0;
    private Disposable pwd1Disposable;
    private byte[] password_1 = new byte[16];
    private boolean isBind;

    private Disposable bindDisposable;

    public void searchDevices() {
        //订阅时 延时取消订阅
        //看是否包含有此设备
        //搜索到设备
        //看是否包含有此设备
        //搜索到设备
        toDisposable(disposable);
        if (devices != null) {  //每次重新搜索都清空搜索到的设备，然后传递给界面让界面刷新
            devices.clear();
            if (mViewRef.get() != null) {
                mViewRef.get().loadDevices(devices);
            }
        }
        bleService.release();
        handler.removeCallbacks(stopScanLe);
        handler.postDelayed(stopScanLe, 10 * 1000);
        disposable = bleService.scanBleDevice(true)
                .filter(new Predicate<BluetoothDevice>() {
                    @Override
                    public boolean test(BluetoothDevice device) throws Exception {
                        return !devices.contains(device); //看是否包含有此设备
                    }
                })
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BluetoothDevice>() {
                    @Override
                    public void accept(BluetoothDevice device) throws Exception {
                        LogUtils.e("搜索到设备   " + device.getName());
                        devices.add(device);
                        //搜索到设备
                        if (mViewRef != null) {
                            mViewRef.get().loadDevices(devices);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef != null) {
                            mViewRef.get().onScanFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(disposable);
    }


    Runnable stopScanLe = new Runnable() {
        @Override
        public void run() {
            if (bleService != null) { //停止扫描设备
                bleService.scanBleDevice(false);
                if (mViewRef != null) {
                    if (isAttach) {
                        LogUtils.e("设备停止扫描   ");
                        mViewRef.get().onStopScan();
                    }
                }
            }
        }
    };


    public void checkBind(BluetoothDevice device) {
        handler.removeCallbacks(stopScanLe);
        if (bleService != null) { //停止扫描设备
            bleService.scanBleDevice(false);
            if (mViewRef != null) {
                mViewRef.get().onStopScan();
            }
        }

        CheckBind checkBind = new CheckBind();
        checkBind.setUser_id(MyApplication.getInstance().getUid());
        checkBind.setDevname(device.getName());

        String obj = new Gson().toJson(checkBind);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), obj);

        RetrofitServiceManager.getInstance().create(ILockService.class).checkLockBind(body)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Observer<Response<CheckBindResult>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<CheckBindResult> stringResponse) {
                        if ("202".equals(stringResponse.body().getCode())) {
                            if (mViewRef != null) {
                                mViewRef.get().onAlreadyBind(device);
                            }
                        } else if ("201".equals(stringResponse.body().getCode())) {
                            if (mViewRef != null) {
                                mViewRef.get().onNoBind(device);
                            }
                        } else if ("444".equals(stringResponse.body().getCode())) {
                            if (mqttService!=null){
                                mqttService.httpMqttDisconnect();
                            }
                            MyApplication.getInstance().tokenInvalid(true);
                            return;
                        } else {
                            if (mViewRef != null) {
                                mViewRef.get().onCheckBindFailedServer(stringResponse.body().getCode());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof OtherException) {
                            OtherException exception = (OtherException) e;
                            if (exception.getResponse().getCode() == 444) {
                                if (mqttService!=null){
                                    mqttService.httpMqttDisconnect();
                                }
                                MyApplication.getInstance().tokenInvalid(true);
                                return;
                            }
                        }
                        if (mViewRef != null) {
                            mViewRef.get().onCheckBindFailed(e);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    public void detachView() {
        super.detachView();
        //界面退出的时候
        bleService.scanBleDevice(false);
    }

    private int connectTimes = 0;


    public void bindDevice(BluetoothDevice device, boolean isBind) {
        this.isBind = isBind;
        this.device = device;
        if (connectTimes > 2) {
            if (mViewRef.get() != null) {
                mViewRef.get().onConnectFailed();
            }
            return;
        }
        // 连接
        bleService.connectDeviceByDevice(device);
        if (mViewRef.get() != null) {
            mViewRef.get().onConnecting();
        }
        bindDisposable = bleService.subscribeDeviceConnectState()
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleStateBean>() {
                    @Override
                    public void accept(BleStateBean bleStateBean) throws Exception {
                        toDisposable(bindDisposable);
                        if (bleStateBean.isConnected()) {
                            LogUtils.e(SearchDevicePresenter.class.getName() + "连接成功");
                            if (bleStateBean.getBleVersion() == 2 || bleStateBean.getBleVersion() ==3) {
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onConnectSuccess();
                                }
                                readSnTimes = 0;  //初始化读取SN的次数
                                readSn(bleStateBean.getBleVersion());
                            }else if (bleStateBean.getBleVersion() == 1){ //最老的模块，走老的流程
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onConnectedAndIsOldMode(bleStateBean.getBleVersion(),isBind);
                                }
                            }
                        } else {
                            connectTimes++;
                            LogUtils.e(SearchDevicePresenter.class.getName() + "绑定界面连接失败");
                            bindDevice(device, isBind);
                        }
                    }
                });
        compositeDisposable.add(bindDisposable);
    }


    public void readSn(int version) {
        toDisposable(snDisposable);
        LogUtils.e("第" + readSnTimes + "次读取SN");
        if (readSnTimes > 2) {
            if (mViewRef.get() != null) {
                mViewRef.get().readSNFailed();
            }
            return;
        }
        snDisposable = bleService.readSN(500)
                .filter(new Predicate<ReadInfoBean>() {
                    @Override
                    public boolean test(ReadInfoBean readInfoBean) throws Exception {
                        return readInfoBean.type == ReadInfoBean.TYPE_SN;
                    }
                })
                .timeout(2000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<ReadInfoBean>() {
                    @Override
                    public void accept(ReadInfoBean readInfoBean) throws Exception {
                        LogUtils.e("读取SN成功  " + readInfoBean.data);
                        toDisposable(snDisposable);
                        getPwd1((String) readInfoBean.data,version);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("读取SN失败  " + throwable.getMessage());
                        readSnTimes++;
                        readSn(version);
                    }
                });
        compositeDisposable.add(snDisposable);

    }


    public void getPwd1(String sn,int version) {
        if (mViewRef.get() != null) {
            mViewRef.get().getPwd1();
        }

        toDisposable(pwd1Disposable);

        XiaokaiNewServiceImp.getPwdBySn(sn)
                .subscribe(new BaseObserver<GetPwdBySnResult>() {
                    @Override
                    public void onSuccess(GetPwdBySnResult getPwdBySnResult) {
                        LogUtils.e("根据SN 获取pwd1    " + getPwdBySnResult.getData().getPassword1());
                        if ("200".equals(getPwdBySnResult.getCode())) { //获取pwd1成功
                            pwd1 = getPwdBySnResult.getData().getPassword1();
                        } else { //如果没有请求道pwd1  拿设备MAc地址解析成pwd1
                            byte[] bPwd1 = device.getAddress().replace(":", "").getBytes();
//                            System.arraycopy(bPwd1, 0, password_1, 0, bPwd1.length);
                            pwd1 = device.getAddress().replace(":", "");

                            bPwd1 = device.getAddress().replace(":", "").getBytes();
                            System.arraycopy(bPwd1, 0, password_1, 0, bPwd1.length);
                            pwd1 = Rsa.bytesToHexString(bPwd1);
                        }
                        if (mViewRef.get() != null) {
                            mViewRef.get().getPwd1Success(pwd1, isBind,version);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if ("419".equals(baseResult.getCode())) {  //此SN在服务器没有找到
                            LogUtils.e("获取pwd1失败   服务器没有该SN  将Mac地址转换成pwd");
                            bPwd1 = device.getAddress().replace(":", "").getBytes();
                            System.arraycopy(bPwd1, 0, password_1, 0, bPwd1.length);
                            pwd1 = Rsa.bytesToHexString(bPwd1);
                            if (mViewRef.get() != null) {
                                mViewRef.get().getPwd1Success(pwd1, isBind,version);
                                mViewRef.get().notice419();
                            }
                            return;
                        } else {
                            if (mViewRef.get() != null) {
                                mViewRef.get().getPwd1FailedServer(baseResult);
                            }
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("获取pwd1失败");
                        if (mViewRef.get() != null) {
                            mViewRef.get().getPwd1Failed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }


}
