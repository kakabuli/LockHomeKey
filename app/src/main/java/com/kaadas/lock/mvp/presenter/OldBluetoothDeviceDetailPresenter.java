package com.kaadas.lock.mvp.presenter;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.view.IDeviceDetailView;
import com.kaadas.lock.mvp.view.IOldBluetoothDeviceDetailView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.OldBleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.RetryWithTime;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.greenDao.db.BleLockServiceInfoDao;
import com.kaadas.lock.utils.greenDao.db.DaoSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


/**
 * Create By lxj  on 2019/2/27
 * Describe
 */
public class OldBluetoothDeviceDetailPresenter<T> extends OldBleLockDetailPresenter<IOldBluetoothDeviceDetailView> {
    private Disposable electricDisposable;
    private Disposable oldPowerDisposable;
    private Disposable oldModeConfirmDisposable;
    private Disposable oldOpenStatusDisposable;
    private Disposable oldCloseStatusDisposable;

    @Override
    public void authSuccess() {
        if (bleService.getBleVersion() == 2 || bleService.getBleVersion()==3){
            readBattery();
        }else {
            getOldGetPower();
        }
        if (mViewRef.get()!=null){
            mViewRef.get().onBleVersionUpdate(bleService.getBleVersion());
        }
    }

    public int getBleVersion(){
        return bleService.getBleVersion();
    }


    public void getPower(){
        if (bleService.getBleVersion() == 2 || bleService.getBleVersion()==3){
            readBattery();
        }else {
            getOldGetPower();
        }
    }

    public void getAllPassword(BleLockInfo bleLockInfo) {
        XiaokaiNewServiceImp.getPasswords(MyApplication.getInstance().getUid(), bleLockInfo.getServerLockInfo().getLockName(), 0)
                .subscribe(new BaseObserver<GetPasswordResult>() {
                    @Override
                    public void onSuccess(GetPasswordResult getPasswordResult) {
                        // TODO: 2019/3/6   永久密码  需要做缓存 付积辉--已做
                        if (mViewRef.get() != null) {
                            mViewRef.get().onGetPasswordSuccess(getPasswordResult);
                        }
                        //更新列表
                        MyApplication.getInstance().setPasswordResults(bleLockInfo.getServerLockInfo().getLockName(), getPasswordResult, true);
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onGetPasswordFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onGetPasswordFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }



    @Override
    public void attachView(IOldBluetoothDeviceDetailView view) {
        super.attachView(view);
    }




    private void readBattery() {
        toDisposable(electricDisposable);
        electricDisposable = Observable.just(0)
                .flatMap(new Function<Integer, ObservableSource<ReadInfoBean>>() {
                    @Override
                    public ObservableSource<ReadInfoBean> apply(Integer integer) throws Exception {
                        return bleService.readBattery();
                    }
                })
                .filter(new Predicate<ReadInfoBean>() {
                    @Override
                    public boolean test(ReadInfoBean readInfoBean) throws Exception {
                        return readInfoBean.type == ReadInfoBean.TYPE_BATTERY;
                    }
                })
                .timeout(1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .retryWhen(new RetryWithTime(2, 0))  //读取三次电量   如果没有读取到电量的话
                .subscribe(new Consumer<ReadInfoBean>() {
                    @Override
                    public void accept(ReadInfoBean readInfoBean) throws Exception {
                        LogUtils.e("读取电量成功    " + (Integer) readInfoBean.data);
                        Integer battery = (Integer) readInfoBean.data;
                        if (bleLockInfo.getBattery() == -1) {   //没有获取过再重新获取   获取到电量  那么
                            bleLockInfo.setBattery(battery);
                            bleLockInfo.setReadBatteryTime(System.currentTimeMillis());
                            if (mViewRef.get() != null) {  //读取电量成功
                                mViewRef.get().onElectricUpdata(battery);
                            }
                        }
                        toDisposable(electricDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("读取电量失败   " + throwable.getMessage());
                        if (mViewRef.get() != null) {  //读取电量失败
                            mViewRef.get().onElectricUpdataFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(electricDisposable);
    }



    ////////////////////////////////////////老模块获取电量逻辑/////////////////////////////////

    public void getOldGetPower() {
        byte[] wakeUpFrame = OldBleCommandFactory.getWakeUpFrame();
        byte[] getPower1 = OldBleCommandFactory.getPowerCommand();
        byte[] getPower2 = OldBleCommandFactory.getEndFrame();

        bleService.sendCommand(wakeUpFrame);
        bleService.sendCommand(getPower1);
        bleService.sendCommand(getPower2);

        toDisposable(oldPowerDisposable);
        oldPowerDisposable = bleService.listeneDataChange()
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        byte[] originalData = bleDataBean.getOriginalData();
                        //电量的数据
                        if ((originalData[0] & 0xff) == 0x5f && (originalData[4] & 0xff) == 0xc1) {
                            int result = (originalData[5] & 0xff);
                            if (result == 0x80) { //获取电量成功
                                int power = originalData[7] & 0b01111111;
                                if (bleLockInfo.getBattery() == -1) {   //没有获取过再重新获取   获取到电量  那么
                                    bleLockInfo.setBattery(power);
                                    bleLockInfo.setReadBatteryTime(System.currentTimeMillis());
                                    LogUtils.e("读取电量成功   " + power);
                                    if (mViewRef.get() != null) {  //读取电量成功
                                        mViewRef.get().onElectricUpdata(power);
                                    }
                                }
                            } else if (result == 0x81) {  //获取电量失败
                                if (mViewRef.get() != null) {  //读取电量成功
                                    mViewRef.get().onElectricUpdataFailed(new BleProtocolFailedException(0x81));
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(oldPowerDisposable);
    }

    public void currentOpenLock(){
        if (bleService.getBleVersion() == 1){
            oldOpenLockMethod();
        } else {
            openLock();
        }
    }


    //////////////////////老模块开锁逻辑///////////////////////////////
    public void oldOpenLockMethod() {
        openLockRunnable.run();
    }

    Runnable openLockRunnable = new Runnable() {
        @Override
        public void run() {
            byte[] wakeUpFrame = OldBleCommandFactory.getWakeUpFrame();
            List<byte[]> openLockCommands = OldBleCommandFactory.getOpenLockCommands();
            //连续发送三个指令   BleService中有自己的队列   每隔100ms发送一个数据
            bleService.sendCommand(wakeUpFrame);
            bleService.sendCommand(openLockCommands.get(0));
            bleService.sendCommand(openLockCommands.get(1));
            listenerOldModeOpenLockData();
            handler.postDelayed(this, 1000);
        }
    };

    /**
     * 监听老模块的确认帧
     */

    private void listenerOldModeOpenLockData() {
        List<byte[]> openLockDataBack = new ArrayList<>();
        toDisposable(oldModeConfirmDisposable);
        oldModeConfirmDisposable = bleService.listeneDataChange()
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        openLockDataBack.add(bleDataBean.getOriginalData());
                        if (openLockDataBack.size() >= 4) {
                            parseOpenLockResponseTest(openLockDataBack);
                            parseOpenLockResponse(openLockDataBack);
                            toDisposable(oldModeConfirmDisposable);
                            handler.removeCallbacks(openLockRunnable);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        handler.removeCallbacks(openLockRunnable);
                        if (mViewRef.get() != null) {
                            mViewRef.get().openLockFailed(new TimeoutException());
                        }
                    }
                });
        compositeDisposable.add(oldModeConfirmDisposable);
    }

    /**
     * 监听老模块的应答帧
     * 此处成功，才认为开锁成功
     */

    private void parseOpenLockResponseTest(List<byte[]> datas) {
        for (byte[] data : datas) {
            LogUtils.e("接收到的数据是  " + Rsa.bytesToHexString(data));
        }
        if (!(datas.get(0).length == 20 && datas.get(1).length == 12 && datas.get(2).length == 20 && datas.get(3).length == 12)) {
            return;
        }
        byte[] temp = new byte[64];

        byte[] data1 = datas.get(2);
        byte[] data2 = datas.get(3);
        System.arraycopy(data1, 0, temp, 32, data1.length);
        System.arraycopy(data2, 0, temp, 52, data2.length);
        for (int i = 0; i < 10; i++) {
            byte[] key = OldBleCommandFactory.oldModeKey.get(i);
            LogUtils.e("解密前的数据是   " + Rsa.bytesToHexString(temp));
            byte[] decryptByte = Rsa.decrypt(temp, key);// 对返回的数据进行解密
            LogUtils.e("解密后的数据是   " + Rsa.bytesToHexString(decryptByte));
        }
    }


    // 开锁数据头
    private void parseOpenLockResponse(List<byte[]> datas) {
        if (!(datas.get(0).length == 20 && datas.get(1).length == 12 && datas.get(2).length == 20 && datas.get(3).length == 12)) {
            if (mViewRef.get() != null) {
                mViewRef.get().openLockFailed(new TimeoutException());
            }
            return;
        }
        byte[] receiveByte = new byte[64];

        byte[] data1 = datas.get(2);
        byte[] data2 = datas.get(3);
        System.arraycopy(data1, 0, receiveByte, 32, data1.length);
        System.arraycopy(data2, 0, receiveByte, 52, data2.length);

        byte[] sendbyte = new byte[]{
                (byte) 0xf5, (byte) 0x00, (byte) 0x00, (byte) 0x1c,
                (byte) 0xc2, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

        boolean bPaw = false;
        // 总共有10组密码组
        byte[] randomDecryptByte = null;
        for (int i = 0; i < 10; i++) {
            byte[] decryptByte = Rsa.decrypt(receiveByte, OldBleCommandFactory.oldModeKey.get(i));// 对返回的数据进行解密
            LogUtils.e("解析之前的数据是   " + Rsa.bytesToHexString(receiveByte) + "   解析之后的数据是  " + Rsa.bytesToHexString(decryptByte));
            if (decryptByte[32] == (byte) 0x5f) {// 判断第二个包的第一个字节是否是0x5f,如果是就解密对了
                // 提取16位密钥随机数来再次解密
                byte[] randomByte16 = new byte[16];
                for (int i1 = 0; i1 < 16; i1++) {
                    randomByte16[i1] = decryptByte[38 + i1];
                }
                // 16位密钥重新解密
                for (int i2 = 0; i2 < 10; i2++) {
                    randomDecryptByte = Rsa.decrypt(randomByte16, OldBleCommandFactory.oldModeKey.get(i2));// 解密
                    short sendByteSum = 0;
                    for (int i3 = 2; i3 < randomDecryptByte.length; i3++) {
                        sendByteSum += Integer.parseInt(Integer.toHexString((randomDecryptByte[i3] & 0xFF)), 16);
                    }
                    byte[] bSend = new byte[2];
                    bSend[0] = (byte) (sendByteSum >> 0);// 低字节
                    bSend[1] = (byte) (sendByteSum >> 8);// 高字节
                    if (randomDecryptByte[0] != bSend[0] || randomDecryptByte[1] != bSend[1]) {// 随机数错了
                        continue;
                    } else {// 随机数对了
                        //鉴权成功
                        bPaw = true;
                        break;
                    }
                }
                break;
            } else {

            }
        }
        if (!bPaw) {
            return;
        }
        if (randomDecryptByte == null) {
            return;
        }
        Random random = new Random();// 定义随机类
        int result = random.nextInt(10);// 返回[0,10)集合中的整数，注意不包括10
        byte[] randomEncryptByte = Rsa.encrypt2(randomDecryptByte, OldBleCommandFactory.oldModeKey.get(result));// 对获取的16位随机数进行加密数据
        for (int i2 = 0; i2 < 16; i2++) {
            sendbyte[i2 + 6] = randomEncryptByte[i2];
        }
        short sendByteSum = 0;
        for (int i = 4; i < sendbyte.length; i++) {
            sendByteSum += Integer.parseInt(Integer.toHexString((sendbyte[i] & 0xFF)), 16);
        }
        byte[] bSend = new byte[2];
        bSend[0] = (byte) (sendByteSum >> 0);//低字节
        bSend[1] = (byte) (sendByteSum >> 8);// 高字节
        sendbyte[1] = bSend[0];
        sendbyte[2] = bSend[1];
        byte[] sendEncryptByte = Rsa.encrypt2(sendbyte, OldBleCommandFactory.oldModeKey.get(result));// 对发送的数据进行加密数据
        final byte[] command1 = new byte[20];
        final byte[] command2 = new byte[20];
        for (int i = 0; i < 20; i++) {
            command1[i] = sendEncryptByte[i];
        }
        for (int i = 0; i < 12; i++) {
            command2[i] = sendEncryptByte[i + 20];
        }
        bleService.sendCommand(command1);
        bleService.sendCommand(command2);
        listenerOpenStatus();
    }

    /**
     * 监听开锁情况
     * f5da011cb1010767000019051414062747000000  开锁
     * f575011cb1000000000019051414064038000000   关锁
     */
    public void listenerOpenStatus() {
        toDisposable(oldOpenStatusDisposable);
        oldOpenStatusDisposable = bleService.listeneDataChange()
                  .compose(RxjavaHelper.observeOnMainThread())
                  .filter(new Predicate<BleDataBean>() {
                      @Override
                      public boolean test(BleDataBean bleDataBean) throws Exception {
                          byte[] originalData = bleDataBean.getOriginalData();
                          if ((originalData[0] & 0xff) == 0xf5 && (originalData[4] & 0xff) == 0xb1) {
                              return true;
                          }
                          return false;
                      }
                  })
                  .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                  .subscribe(new Consumer<BleDataBean>() {
                      @Override
                      public void accept(BleDataBean bleDataBean) throws Exception {
                          byte[] originalData = bleDataBean.getOriginalData();
                          if ((originalData[5] & 0xff) == 0x01) {  //老模块开门上报
                              toDisposable(oldOpenStatusDisposable);
                              listenerCloseStatus();
                              if (mViewRef.get() != null) {
                                  mViewRef.get().openLockSuccess();
                              }
                          }
                      }
                  }, new Consumer<Throwable>() {
                      @Override
                      public void accept(Throwable throwable) throws Exception {
                          if (mViewRef.get() != null) {
                              mViewRef.get().openLockFailed(throwable);
                          }
                      }
                  });
        compositeDisposable.add(oldOpenStatusDisposable);
    }


    /**
     * 监听关门状态
     */
    public void listenerCloseStatus() {
        toDisposable(oldCloseStatusDisposable);
        //老模块关门上报
        oldCloseStatusDisposable = bleService.listeneDataChange()
                  .filter(new Predicate<BleDataBean>() {
                      @Override
                      public boolean test(BleDataBean bleDataBean) throws Exception {
                          byte[] originalData = bleDataBean.getOriginalData();
                          if ((originalData[0] & 0xff) == 0xf5 && (originalData[4] & 0xff) == 0xb1) {
                              return true;
                          }
                          return false;
                      }
                  })
                  .timeout(15 * 1000, TimeUnit.MILLISECONDS)
                  .compose(RxjavaHelper.observeOnMainThread())
                  .subscribe(new Consumer<BleDataBean>() {
                      @Override
                      public void accept(BleDataBean bleDataBean) throws Exception {
                          byte[] originalData = bleDataBean.getOriginalData();
                          if ((originalData[5] & 0xff) == 0x00) {  //老模块关门上报
                              toDisposable(oldCloseStatusDisposable);
                              if (mViewRef.get() != null) {
                                  mViewRef.get().onLockLock();
                              }
                          }
                      }
                  }, new Consumer<Throwable>() {
                      @Override
                      public void accept(Throwable throwable) throws Exception {
                          if (mViewRef.get() != null) {
                              mViewRef.get().onLockLock();
                          }
                      }
                  });
        compositeDisposable.add(oldCloseStatusDisposable);
    }
    public void deleteDevice(String deviceName) {
        XiaokaiNewServiceImp.deleteDevice(MyApplication.getInstance().getUid(), deviceName)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                        if (mViewRef.get() != null) {
                            mViewRef.get().onDeleteDeviceSuccess();
                        }
                        //todo 清除数据库的数据
                        //清除消息免打扰
                        SPUtils.remove(deviceName + SPUtils.MESSAGE_STATUS);
                        //todo 清除保存的密码
                        SPUtils.remove(KeyConstants.SAVE_PWD_HEARD + bleLockInfo.getServerLockInfo().getMacLock()); //Key

                        //通知homeFragment  和  device刷新界面
                        bleService.release();
//                        MyApplication.getInstance().deleteDevice(deviceName);
                        bleService.removeBleLockInfo();

                        //删除数据库缓存数据
                        DaoSession daoSession = MyApplication.getInstance().getDaoWriteSession();
                        BleLockServiceInfoDao bleLockServiceInfoDao = daoSession.getBleLockServiceInfoDao();
                        bleLockServiceInfoDao.queryBuilder().where(BleLockServiceInfoDao.Properties.LockName.eq(bleLockInfo.getServerLockInfo().getLockName())).buildDelete().executeDeleteWithoutDetachingEntities();

                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onDeleteDeviceFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onDeleteDeviceFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });

    }
    ////////////////////////////////////////老模块获取电量逻辑/////////////////////////////////

}
