package com.kaadas.lock.mvp.presenter.clotheshangermachinepresenter;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineDetailView;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineView;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.postbean.HangerMultiOTABean;
import com.kaadas.lock.publiclibrary.http.postbean.HangerUpgradeMultiOTABean;
import com.kaadas.lock.publiclibrary.http.postbean.MultiOTABean;
import com.kaadas.lock.publiclibrary.http.postbean.UpgradeMultiOTABean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.ClothesHangerMachineUnBindResult;
import com.kaadas.lock.publiclibrary.http.result.MultiCheckOTAResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class ClothesHangerMachineDetailPresenter<T> extends BasePresenter<IClothesHangerMachineDetailView> {

    private Disposable deleteDeviceDisposable;

    @Override
    public void attachView(IClothesHangerMachineDetailView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void deleteDevice(String wifiSN){
        XiaokaiNewServiceImp.clothesHangerMachineUnBind(wifiSN)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new BaseObserver<ClothesHangerMachineUnBindResult>() {
            @Override
            public void onSuccess(ClothesHangerMachineUnBindResult clothesHangerMachineUnBindResult) {
                if("200".equals(clothesHangerMachineUnBindResult.getCode() + "")){
                    if(isSafe()){
                        mViewRef.get().onDeleteDeviceSuccess();
                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                    }
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if(isSafe()){
                    mViewRef.get().onDeleteDeviceFailed(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if(isSafe()){
                    mViewRef.get().onDeleteDeviceThrowable(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {

            }
        });
    }

    public void checkOTAInfo(String wifiSN,String hangerVersion,String moduleVersion) {
        List<HangerMultiOTABean.OTAParams> params = new ArrayList<>();
        params.add(new HangerMultiOTABean.OTAParams(6,hangerVersion));
        params.add(new HangerMultiOTABean.OTAParams(7,moduleVersion));

        XiaokaiNewServiceImp.getOtaMultiInfo(1,wifiSN,params,MqttConstant.CLOTHES_HANGER_MACHINE_MX_CHIP)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new BaseObserver<MultiCheckOTAResult>() {
            @Override
            public void onSuccess(MultiCheckOTAResult multiCheckOTAResult) {
                if("200".equals(multiCheckOTAResult.getCode() + "")){
                    if(isSafe()){
                        mViewRef.get().needUpdate(multiCheckOTAResult.getData().getUpgradeTask());
                    }
                }else if("210".equals(multiCheckOTAResult.getCode() + "")){
                    if(isSafe()){
                        mViewRef.get().noUpdate();
                    }
                }else{
                    if(isSafe()){
                        BaseResult bean = new BaseResult();
                        bean.setCode(multiCheckOTAResult.getCode());
                        bean.setMsg(multiCheckOTAResult.getMsg());
                        mViewRef.get().checkUpdateFailed(bean);
                    }
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if(isSafe()){
                    mViewRef.get().checkUpdateFailed(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if(isSafe()){
                    mViewRef.get().checkUpdateThrowable(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {

            }
        });
    }

    public void updateOTA(String wifiSN,List<MultiCheckOTAResult.UpgradeTask> upgradeTasks) {
        List<HangerUpgradeMultiOTABean.UpgradeTaskBean> data = new ArrayList<>();
        for(int i = 0;i < upgradeTasks.size();i++){
            data.add(new HangerUpgradeMultiOTABean.UpgradeTaskBean(upgradeTasks.get(i).getDevNum(),upgradeTasks.get(i).getFileLen(),
                    upgradeTasks.get(i).getFileUrl(),upgradeTasks.get(i).getFileMd5(),upgradeTasks.get(i).getFileVersion()));
        }
        XiaokaiNewServiceImp.hangerDeviceUploadMultiOta(wifiSN, MqttConstant.CLOTHES_HANGER_MACHINE_MX_CHIP,data)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new BaseObserver<BaseResult>() {
            @Override
            public void onSuccess(BaseResult baseResult) {

            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if(isSafe()){
                    mViewRef.get().updateFailed(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if(isSafe()){
                    mViewRef.get().updateThrowable(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {

            }
        });
    }
}

