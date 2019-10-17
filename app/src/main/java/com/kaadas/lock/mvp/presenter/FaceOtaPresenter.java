package com.kaadas.lock.mvp.presenter;

import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IFaceOtaView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class FaceOtaPresenter<T> extends BlePresenter<IFaceOtaView> {

    private Disposable otaStateDisposable;
    private int number;
    private int otaType;
    @Override
    public void authSuccess() {

    }

    public void  init (int number,int otaType){
        this.number = number;
        this.otaType = otaType;
    }

    @Override
    public void attachView(IFaceOtaView view) {
        super.attachView(view);
        bleService.setIsOta(true);
        otaStateDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return (bleDataBean.getCmd()&0xff) == 0x86;
                    }
                })
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        byte[] decrypt = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                        LogUtils.e("收到ota状态上报   "+Rsa.bytesToHexString(decrypt));
                        if ((decrypt[0] & 0xff) == number && (decrypt[1] & 0xff) == otaType && (decrypt[3] & 0xff) == 0 ){
                            if (isSafe()){
                                mViewRef.get().otaSuccess();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(otaStateDisposable);
    }

    @Override
    public void detachView() {
        bleService.setIsOta(false);
        super.detachView();
    }
}
