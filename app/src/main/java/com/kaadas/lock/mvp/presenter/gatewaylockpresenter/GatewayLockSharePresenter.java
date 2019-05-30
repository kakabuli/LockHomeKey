package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockShareView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdFuncBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockPwd;
import com.kaadas.lock.utils.greenDao.db.GatewayLockPwdDao;

import org.linphone.mediastream.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayLockSharePresenter<T> extends BasePresenter<GatewayLockShareView> {
    private Disposable shareDeleteDisposable;
    //删除用户密码
    public void shareDeleteLockPwd(String gatewayId,String deviceId,String pwdNum){
        toDisposable(shareDeleteDisposable);
        if (mqttService!=null){
            shareDeleteDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), MqttCommandFactory.lockPwdFunc(gatewayId,deviceId,"clear","pin",pwdNum,""))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            LogUtils.e("删除锁消息");
                            if (MqttConstant.SET_PWD.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .timeout(20*1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            LogUtils.e("删除....."+mqttData.getFunc());
                            toDisposable(shareDeleteDisposable);
                            LockPwdFuncBean lockPwdFuncBean=new Gson().fromJson(mqttData.getPayload(),LockPwdFuncBean.class);
                            if ("200".equals(mqttData.getReturnCode())){
                                if (lockPwdFuncBean.getReturnData().getStatus()==0){
                                    //删除成功
                                    if (mViewRef.get()!=null){
                                        mViewRef.get().shareDeletePasswordSuccess(pwdNum);
                                        deleteOnePwd(gatewayId,deviceId,MyApplication.getInstance().getUid(),pwdNum);
                                    }
                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().shareDeletePasswordFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().shareDeletePasswordThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(shareDeleteDisposable);
        }



    }

    //删除某个数据
    private void deleteOnePwd(String gatewayId,String deviceId,String uid,String num) {
        GatewayLockPwdDao gatewayLockPwdDao=MyApplication.getInstance().getDaoWriteSession().getGatewayLockPwdDao();
        GatewayLockPwd gatewayLockPwd=gatewayLockPwdDao.queryBuilder().where(GatewayLockPwdDao.Properties.GatewayId.eq(gatewayId), GatewayLockPwdDao.Properties.DeviceId.eq(deviceId),GatewayLockPwdDao.Properties.Uid.eq(uid),GatewayLockPwdDao.Properties.Num.eq(num)).unique();
        if (gatewayLockPwd!=null){
            gatewayLockPwdDao.delete(gatewayLockPwd);
        }
    }

}
