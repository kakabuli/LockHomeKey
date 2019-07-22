package com.kaadas.lock.mvp.presenter;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IOperationRecordView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.publiclibrary.ble.bean.OperationLockRecord;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.postbean.UploadOperationRecordBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.OperationRecordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Create By lxj  on 2019/2/18
 * Describe
 * 第一种方式
 * 获取开锁记录的逻辑
 * 进入就从服务器获取开锁记录
 * 用户点击同步记录，即获取所有锁上的开锁记录
 * 如果用户不点击，那么不主动获取锁上开锁记录
 * 用户点击同步开锁记录时的获取开锁记录逻辑：
 * <p>
 * 方式一：
 * 获取所有的开锁记录，根据total值生成total值的开锁记录的数组
 * 超过1秒没有再收到数据则认为数据已经接受完成
 * 检查数据是否有遗漏   如果没有遗漏  直接传递给界面显示，并上传服务器
 * 如果有遗漏重新全部获取   最多重试三次
 * <p>
 * 方式二：
 * 分组查询
 * 首先查询第一组数据，获取到total值，然后根据total值分组
 * <p>
 * 每一组数据的查询逻辑
 */
public class OperationRecordPresenter<T> extends BlePresenter<IOperationRecordView> {
    private OperationLockRecord[] operationLockRecords = null;
    private Disposable disposable;
    private int currentPage;
    private int total; //开锁记录的总数
    private int startIndex;
    private int endIndex;
    private int retryTimes = 0;//重试的次数
    private int maxPage;//最大的组数
    private List<OperationLockRecord> serverRecords = new ArrayList<>();
    private List<OperationLockRecord> notNullRecord = new ArrayList<>();
    private byte[] command;
    private Disposable serverDisposable;
    private Disposable recordDisposable;

    public List<OperationLockRecord> getNotNullRecord() {
        notNullRecord.clear();
        if (operationLockRecords != null) {
            for (int i = 0; i < operationLockRecords.length; i++) {
                if (operationLockRecords[i] != null) {
                    /**
                     * 过滤报警信息
                     * */
                    if (3!=operationLockRecords[i].getEventType()){
                        notNullRecord.add(operationLockRecords[i]);
                    }

                }
            }
        }
        LogUtils.d("davi notNullRecord 数据 "+notNullRecord.toString());
        return notNullRecord;
    }

    public List<UploadOperationRecordBean.OperationListBean> getRecordToServer() {
        if (serverDisposable != null && !serverDisposable.isDisposed()) {
            serverDisposable.dispose();
        }
        List<UploadOperationRecordBean.OperationListBean> recordToServers = new ArrayList<>();
        if (operationLockRecords == null) {
            return recordToServers;
        }
        LogUtils.d("davi operationLockRecords 锁记录 "+operationLockRecords.toString());
        for (int i = 0; i < operationLockRecords.length; i++) {
            if (operationLockRecords[i] != null) {
                /**
                 * 过滤报警信息
                 * */
                if (3!=operationLockRecords[i].getEventType()){
                    UploadOperationRecordBean.OperationListBean record = new UploadOperationRecordBean.OperationListBean(
                            operationLockRecords[i].getUid(),
                            operationLockRecords[i].getEventType(),
                            operationLockRecords[i].getEventSource(),
                            operationLockRecords[i].getEventCode(),
                            operationLockRecords[i].getUserNum(),
                            operationLockRecords[i].getEventTime()
                    );
                    recordToServers.add(record);
                }

            }
        }
        return recordToServers;
    }



    /**
     * 获取全部的操作记录
     * */
    public void getOperationRecordFromServer(int pagenum) {
        if (pagenum == 1) {  //如果是获取第一页的数据，那么清楚所有的开锁记录
            serverRecords.clear();
        }
        XiaokaiNewServiceImp.getOperationRecord(bleService.getBleLockInfo().getServerLockInfo().getLockName(),
                pagenum )
                .subscribe(new BaseObserver<OperationRecordResult>() {
                    @Override
                    public void onSuccess(OperationRecordResult operationRecordResult) {
                        if (operationRecordResult.getData()!=null&&operationRecordResult.getData().size() == 0) {  //服务器没有数据  提示用户
                            if (mViewRef.get() != null) {
                                if (pagenum == 1) { //第一次获取数据就没有
                                    mViewRef.get().onServerNoData();
                                } else {
                                    mViewRef.get().noMoreData();
                                }
                                return;
                            }
                        }
                        ///将服务器数据封装成用来解析的数据
                        for (OperationRecordResult.OperationBean record : operationRecordResult.getData()) {
                            if (3!=record.getEventType()){
                                serverRecords.add(
                                        new OperationLockRecord(
                                                record.getEventType(),
                                                record.getEventSource(),
                                                record.getEventCode(),
                                                record.getUserNum(),
                                                record.getEventTime(),
                                                record.getUid()
                                        )
                                );
                            }

                        }
                        if (mViewRef.get() != null) {
                            LogUtils.d("davi 服务器记录 1 serverRecords "+serverRecords.toString());
                            mViewRef.get().onLoadServerRecord(serverRecords, pagenum);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        LogUtils.e("获取 开锁记录  失败   " + baseResult.getMsg() + "  " + baseResult.getCode());
                        if (mViewRef.get() != null) {  //
                            mViewRef.get().onLoadServerRecordFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("获取 开锁记录  失败   " + throwable.getMessage());
                        if (mViewRef.get() != null) {
                            mViewRef.get().onLoadServerRecordFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        serverDisposable = d;
                        compositeDisposable.add(serverDisposable);
                    }
                });
    }

    /**
     * 用户点击同步时  调用的从BLe设备获取的开锁记录
     * 每次都从第一组开始获取  此时不知道开锁记录总个数
     */
    public void getRecordFromBle() {
        //添加
        toDisposable(disposable);
        if (mViewRef.get() != null) {
            mViewRef.get().startBleRecord();
        }
        currentPage = 0;
        retryTimes = 0;
        total = 0;
        maxPage = 0;
        operationLockRecords = null;
        getRecordByPage();

    }

    public void getRecordByPage() {
        //获取开锁记录的时候  取消对服务器获取记录的订阅
        if (serverDisposable != null && !serverDisposable.isDisposed()) {
            serverDisposable.dispose();
        }
        if (!(serverDisposable != null && !serverDisposable.isDisposed())) {
            listenerLockRecord();
        }

        startIndex = 0;
        endIndex = 20;
        LogUtils.e("重试次数   " + retryTimes + "    " + currentPage);
        if (retryTimes > 2) { //已经重试了两次，即请求过三次
            //当前组数据已经查询完  不管查到的是什么结果  都显示给用户
            //看还有下一组数据没有   如果没有那么所有的数据都查询完了  不管之前查询到的是什么结果，都上传到服务器
            if (currentPage + 1 >= maxPage) {  //已经查了最后一组数据
                if (operationLockRecords == null) {  //没有获取到数据  请稍后再试
                    if (mViewRef.get() != null && operationLockRecords == null) {
                        mViewRef.get().onLoadBleRecordFinish(false);
                    }
                    return;
                } else {
                    if (mViewRef.get() != null && operationLockRecords != null) {
                        mViewRef.get().onLoadBleRecordFinish(true);
                        LogUtils.d("davi 蓝牙记录 1 ");
                        mViewRef.get().onLoadBleRecord(getNotNullRecord());
                    }
                }
                if (recordDisposable != null && !recordDisposable.isDisposed()) {
                    recordDisposable.dispose();
                }
                LogUtils.d("davi 上传开锁记录 1");
                upLoadOpenRecord(bleService.getBleLockInfo().getServerLockInfo().getLockName()
                        , bleService.getBleLockInfo().getServerLockInfo().getLockNickName()
                        , getRecordToServer(), MyApplication.getInstance().getUid());
                return;
            }
            currentPage++;
        }


        if (currentPage != 0) {       //如果不是第一组  动态生成
            startIndex = ((currentPage) * 20);
            endIndex = ((currentPage + 1) * 20-1);
        }

        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        //TODO 测试操作记录
//        command = BleCommandFactory.getLockRecordCommand((byte) startIndex, (byte) endIndex, bleService.getBleLockInfo().getAuthKey());
        command = BleCommandFactory.getOperationCommand(Rsa.int2BytesArray(startIndex), Rsa.int2BytesArray(endIndex), bleService.getBleLockInfo().getAuthKey());
        bleService.sendCommand(command);

        disposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        boolean b = command[1] == bleDataBean.getTsn();
//                        if (Rsa.bytesToHexString(bleDataBean.getOriginalData()).contains("33")){
//                            return false;
//                        }
                        return b;
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        LogUtils.e("订阅了   ");
                    }
                })
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        LogUtils.e("取消订阅了   ");
                    }
                })
                .timeout(5000, TimeUnit.MILLISECONDS)  //间隔一秒没有数据，那么认为数据获取完成
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(
                        new Consumer<BleDataBean>() {
                            @Override
                            public void accept(BleDataBean bleDataBean) throws Exception {
                                if (bleDataBean.isConfirm()) {
                                    if (0x8b == (bleDataBean.getPayload()[0]&0xff) ) {  //没有数据
                                        LogUtils.e("锁上   没有开锁记录  ");
                                        if (mViewRef.get() != null) {
                                            mViewRef.get().noData();
                                        }
                                        toDisposable(disposable);
                                    }
                                    return;
                                }

                                byte[] deVaule = Rsa.decrypt(bleDataBean.getPayload(), bleService.getBleLockInfo().getAuthKey());
                                LogUtils.e("获取开锁记录   解码之后的数据是   " + Rsa.bytesToHexString(deVaule) + "原始数据是   " + Rsa.toHexString(bleDataBean.getOriginalData()));
//                                OpenLockRecord openLockRecord = BleUtil.parseLockRecord(deVaule);
                                OperationLockRecord operationLockRecord = BleUtil.parseOperationRecord(deVaule);
                                LogUtils.e("获取开锁记录是   " + operationLockRecord.toString());
                                if (operationLockRecords == null) {
                                    byte[] totalByte = new byte[2];
                                    System.arraycopy(deVaule, 0,totalByte , 0, 2);
                                    total = Rsa.bytesToInt(totalByte);
                                    operationLockRecords = new OperationLockRecord[total];
                                    maxPage = (int) Math.ceil(total * 1.0 / 20.0);
                                    LogUtils.e(" 总个数   " + total + "  最大页数  " + maxPage);
                                }
                                byte[] byteIndex = new byte[2];
                                System.arraycopy(deVaule, 2,byteIndex , 0, 2);
                                int index = Rsa.bytesToInt(byteIndex);
                                LogUtils.d("davi 1 index "+index+" total "+total);
                                operationLockRecords[index] = operationLockRecord;

                                // TODO: 2019/3/7  开锁记录测试
                                List<Integer> loseNumber = new ArrayList<>();
                                for (int i = 0; i < endIndex && i < total; i++) {
                                    if (operationLockRecords[i] == null) { //数据不全
                                        loseNumber.add(i);
                                    }
                                }
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onLoseRecord(loseNumber);
                                }
                                // TODO: 2019/3/7  开锁记录测试

                                if (index == (endIndex - 1) || index == (total - 1)) {  //收到一组最后一个数据  或者全部的最后一个数据
                                    for (int i = startIndex; i < endIndex && i < total; i++) {
                                        if (operationLockRecords[i] == null) { //如果一组  数据不全
                                            retryTimes++;
                                            if (retryTimes > 2) {  //如果已经尝试了三次  那么先显示数据
                                                if (mViewRef.get() != null) {
                                                    LogUtils.d("davi 蓝牙记录 2 ");
                                                    mViewRef.get().onLoadBleRecord(getNotNullRecord());
                                                }
                                            }
                                            getRecordByPage();
                                            return;
                                        }
                                    }
                                    if (mViewRef.get() != null) {
                                        LogUtils.d("davi 蓝牙记录 3 ");
                                        mViewRef.get().onLoadBleRecord(getNotNullRecord());
                                    }
                                    if (currentPage + 1 >= maxPage) { //如果收到最后一组的最后一个数据   直接上传
                                        if (mViewRef.get() != null) {
                                            LogUtils.d("davi 蓝牙记录 4 ");
                                            mViewRef.get().onLoadBleRecord(getNotNullRecord());
                                            mViewRef.get().onLoadBleRecordFinish(true);
                                        }
                                        if (recordDisposable != null && !recordDisposable.isDisposed()) {
                                            recordDisposable.dispose();
                                        }
                                        if (disposable != null && !disposable.isDisposed()) {
                                            disposable.dispose();
                                        }
                                        LogUtils.d("davi 上传开锁记录 2");
                                        upLoadOpenRecord(bleService.getBleLockInfo().getServerLockInfo().getLockName()
                                                , bleService.getBleLockInfo().getServerLockInfo().getLockNickName()
                                                , getRecordToServer(), MyApplication.getInstance().getUid());
                                    } else {  //如果后面还有
                                        LogUtils.e("收到一组完整的数据");
                                        currentPage++;  //下一组数据
                                        retryTimes = 0; //重试次数
                                        getRecordByPage();  //获取数据
                                    }
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e("取消订阅了吗   " + disposable.isDisposed() + "   " + throwable.getMessage());
//                                if (throwable instanceof TimeoutException) {  //5秒没有收到数据
                                if (operationLockRecords == null) {  //一个数据都没有收到  重试
                                    retryTimes++;
                                    getRecordByPage();
                                    return;
                                }
                                LogUtils.e("获取数据  超时   数据完成");

                                // TODO: 2019/3/7  开锁记录测试
                                List<Integer> loseNumber = new ArrayList<>();
                                for (int i = 0; i < endIndex && i < total; i++) {
                                    if (operationLockRecords[i] == null) { //数据不全
                                        loseNumber.add(i);
                                    }
                                }
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onLoseRecord(loseNumber);
                                }
                                // TODO: 2019/3/7  开锁记录测试
                                for (int i = startIndex; i < endIndex && i < total; i++) {
                                    if (operationLockRecords[i] == null) { //数据不全
                                        LogUtils.e("数据不全  " + retryTimes);
                                        retryTimes++;
                                        if (retryTimes > 2) {  //如果已经尝试了三次  那么先显示数据
                                            if (mViewRef.get() != null) {
                                                LogUtils.d("davi 蓝牙记录 5 ");
                                                mViewRef.get().onLoadBleRecord(getNotNullRecord());
                                            }
                                        }
                                        getRecordByPage();
                                        return;
                                    }
                                }
                                //到此处，那么说明这一组数据完整不重新获取
                                if (currentPage + 1 >= maxPage) { //如果收到最后一组的最后一个数据   直接上传
                                    // 获取数据完成
                                    if (mViewRef.get() != null) {
                                        LogUtils.d("davi 蓝牙记录 6 ");
                                        mViewRef.get().onLoadBleRecord(getNotNullRecord());
                                        mViewRef.get().onLoadBleRecordFinish(true);
                                    }
                                    if (recordDisposable != null && !recordDisposable.isDisposed()) {
                                        recordDisposable.dispose();
                                    }
                                    LogUtils.d("davi 上传开锁记录 3");
                                    upLoadOpenRecord(bleService.getBleLockInfo().getServerLockInfo().getLockName()
                                            , bleService.getBleLockInfo().getServerLockInfo().getLockNickName()
                                            , getRecordToServer(), MyApplication.getInstance().getUid());
                                } else {  //如果后面还有
                                    currentPage++;  //下一组数据
                                    retryTimes = 0; //重试次数
                                    getRecordByPage();  //获取数据
                                }
                            }
                        }
                );

        compositeDisposable.add(disposable);

    }


    public void upLoadOpenRecord(String device_name, String device_nickname, List<UploadOperationRecordBean.OperationListBean> openLockList, String user_id) {

        for (UploadOperationRecordBean.OperationListBean bleRecord : openLockList) {
            LogUtils.e("上传的数据是    " + bleRecord.toString());
        }
        LogUtils.e("数据获取完成   total" + total + "  获取到的个数是  " + openLockList.size());
        XiaokaiNewServiceImp.uploadOperationRecord(device_name, openLockList)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("上传操作记录成功");
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUploadServerRecordSuccess();
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUploadServerRecordFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("上传开锁记录失败");
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUploadServerRecordFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }


    /**
     * 开一个独立的监听，监听开锁记录
     * 防止数据
     */
    public void listenerLockRecord() {
        toDisposable(recordDisposable);
        recordDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return bleDataBean.getCmd() == 0x18;
                    }
                }).subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (bleDataBean.getOriginalData()[0] == 1) { //开锁记录都是加密的
                            byte[] deVaule = Rsa.decrypt(bleDataBean.getPayload(), bleService.getBleLockInfo().getAuthKey());
                            //TODO 测试操作记录
//                            OpenLockRecord openLockRecord = BleUtil.parseLockRecord(deVaule);
                            OperationLockRecord operationLockRecord = BleUtil.parseOperationRecord(deVaule);
                            byte[] byteIndex = new byte[2];
                            System.arraycopy(deVaule, 2,byteIndex , 0, 2);
                            int index = Rsa.bytesToInt(byteIndex);
                            if (operationLockRecords != null && index < operationLockRecords.length) {
                                LogUtils.d("davi 2 index "+index+" total "+total);
                                operationLockRecords[index] = operationLockRecord;
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(recordDisposable);
    }

    @Override
    public void authSuccess() {

    }
}