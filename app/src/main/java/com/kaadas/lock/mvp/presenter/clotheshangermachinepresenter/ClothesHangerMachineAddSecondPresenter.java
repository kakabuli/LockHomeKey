package com.kaadas.lock.mvp.presenter.clotheshangermachinepresenter;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineAddSecondView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ClothesHangerMachineAddSecondPresenter<T> extends BasePresenter<IClothesHangerMachineAddSecondView> {

    private Disposable openBluetoothStatusDisposable;

    @Override
    public void attachView(IClothesHangerMachineAddSecondView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public boolean isBluetoothEnable(){
        if(bleService != null){
            return bleService.isBleIsEnable();
        }else{
            return MyApplication.getInstance().getBleService().isBleIsEnable();
        }
    }

    public boolean enableBle(){
        if(bleService != null){
            bleService.enableBle();
            listenerBleOpenState();
        }
        return false;
    }

    public void listenerBleOpenState(){
        if(bleService != null){
            toDisposable(openBluetoothStatusDisposable);
            openBluetoothStatusDisposable = bleService.listenerBleOpenState()
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    if(isSafe()){
                        mViewRef.get().openBluetoothStatus(aBoolean);
                    }
                }
            });
            compositeDisposable.add(openBluetoothStatusDisposable);
        }
    }
}
