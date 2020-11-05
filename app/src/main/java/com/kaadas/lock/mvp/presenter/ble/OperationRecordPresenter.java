package com.kaadas.lock.mvp.presenter.ble;

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
    private Disposable operationDisposable;
    private int operationCurrentPage;
    private int operationTotal; //开锁记录的总数
    private int operationStartIndex;
    private int operationEndIndex;
    private int operationRetryTimes = 0;//重试的次数
    private int operationMaxPage;//最大的组数
    private List<OperationLockRecord> operationServerRecords = new ArrayList<>();
    private List<OperationLockRecord> operationNotNullRecord = new ArrayList<>();
    private byte[] operationCommand;
    private Disposable operationServerDisposable;
    private Disposable operationRecordDisposable;

    public List<OperationLockRecord> getNotNullOperationRecord() {
        operationNotNullRecord.clear();
        if (operationLockRecords != null) {
            for (int i = 0; i < operationLockRecords.length; i++) {
                if (operationLockRecords[i] != null) {
                    /**
                     * 过滤报警信息
                     * */
                    if (3 != operationLockRecords[i].getEventType()) {
                        operationNotNullRecord.add(operationLockRecords[i]);
                    }

                }
            }
        }
        LogUtils.d("davi operationNotNullRecord 数据 " + operationNotNullRecord.toString());
        return operationNotNullRecord;
    }

    public List<UploadOperationRecordBean.OperationListBean> getOperationRecordToServer() {
        if (operationServerDisposable != null && !operationServerDisposable.isDisposed()) {
            operationServerDisposable.dispose();
        }
        List<UploadOperationRecordBean.OperationListBean> recordToServers = new ArrayList<>();
        if (operationLockRecords == null) {
            return recordToServers;
        }
        LogUtils.d("davi operationLockRecords 锁记录 " + operationLockRecords.toString());
        for (int i = 0; i < operationLockRecords.length; i++) {
            if (operationLockRecords[i] != null) {
                /**
                 * 过滤报警信息
                 * */
                if (3 != operationLockRecords[i].getEventType()) {
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
     */
    public void getOperationRecordFromServer(int pagenum) {
        if (pagenum == 1) {  //如果是获取第一页的数据，那么清楚所有的开锁记录
            operationServerRecords.clear();
        }
        if(bleService.getBleLockInfo() == null){
            return;
        }
        XiaokaiNewServiceImp.getOperationRecord(bleService.getBleLockInfo().getServerLockInfo().getLockName(),
                pagenum)
                .subscribe(new BaseObserver<OperationRecordResult>() {
                    @Override
                    public void onSuccess(OperationRecordResult operationRecordResult) {
                        if (operationRecordResult.getData() != null && operationRecordResult.getData().size() == 0) {  //服务器没有数据  提示用户
                            if (isSafe()) {
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
                            if (3 != record.getEventType()) {
                                operationServerRecords.add(
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
                        if (isSafe()) {
                            LogUtils.d(" 服务器记录 1 serverRecords " + operationServerRecords.toString());
                            mViewRef.get().onLoadServerOperationRecord(operationServerRecords, pagenum);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        LogUtils.e("获取 开锁记录  失败   " + baseResult.getMsg() + "  " + baseResult.getCode());
                        if (isSafe()) {  //
                            mViewRef.get().onLoadServerRecordFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("获取 开锁记录  失败   " + throwable.getMessage());
                        if (isSafe()) {
                            mViewRef.get().onLoadServerRecordFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        operationServerDisposable = d;
                        compositeDisposable.add(operationServerDisposable);
                    }
                });
    }

    /**
     * 用户点击同步时  调用的从BLe设备获取的开锁记录
     * 每次都从第一组开始获取  此时不知道开锁记录总个数
     */
    private boolean isFirst = true;

    public void getOperationRecordFromBle() {
        isFirst = true;
        //添加
        toDisposable(operationDisposable);
        if (isSafe()) {
            mViewRef.get().startBleRecord();
        }
        operationCurrentPage = 0;
        operationRetryTimes = 0;
        operationTotal = 0;
        operationMaxPage = 0;
        operationLockRecords = null;
        getOperationRecordByPage();
    }

    public void getOperationRecordByPage() {
        //获取开锁记录的时候  取消对服务器获取记录的订阅
        if (operationServerDisposable != null && !operationServerDisposable.isDisposed()) {
            operationServerDisposable.dispose();
        }
        if (!(operationServerDisposable != null && !operationServerDisposable.isDisposed())) {
            listenerOperationLockRecord();
        }

        operationStartIndex = 0;
        operationEndIndex = 20;
        LogUtils.e("重试次数   " + operationRetryTimes + "    " + operationCurrentPage);
        if (operationRetryTimes > 2) { //已经重试了两次，即请求过三次
            //当前组数据已经查询完  不管查到的是什么结果  都显示给用户
            //看还有下一组数据没有   如果没有那么所有的数据都查询完了  不管之前查询到的是什么结果，都上传到服务器
            if (operationCurrentPage + 1 >= operationMaxPage) {  //已经查了最后一组数据
                if (operationLockRecords == null) {  //没有获取到数据  请稍后再试
                    if (isSafe() && operationLockRecords == null) {
                        mViewRef.get().onLoadBleRecordFinish(false);
                    }
                    return;
                } else {
                    if (isSafe() && operationLockRecords != null) {
                        mViewRef.get().onLoadBleRecordFinish(true);
                        LogUtils.d(" 蓝牙记录 1 ");
                        mViewRef.get().onLoadBleOperationRecord(getNotNullOperationRecord());
                    }
                }
                if (operationRecordDisposable != null && !operationRecordDisposable.isDisposed()) {
                    operationRecordDisposable.dispose();
                }
                LogUtils.d(" 上传开锁记录 1");
                upLoadOperationRecord(bleService.getBleLockInfo().getServerLockInfo().getLockName()
                        , bleService.getBleLockInfo().getServerLockInfo().getLockNickName()
                        , getOperationRecordToServer(), MyApplication.getInstance().getUid());
                return;
            }
            operationCurrentPage++;
        }


        if (operationCurrentPage != 0) {       //如果不是第一组  动态生成
            operationStartIndex = ((operationCurrentPage) * 20);
            operationEndIndex = ((operationCurrentPage + 1) * 20 - 1);
        }

        if (operationDisposable != null && !operationDisposable.isDisposed()) {
            operationDisposable.dispose();
        }

        //TODO 测试操作记录
//        operationCommand = BleCommandFactory.getLockRecordCommand((byte) startIndex, (byte) endIndex, bleService.getBleLockInfo().getAuthKey());
        operationCommand = BleCommandFactory.getOperationCommand(Rsa.int2BytesArray(operationStartIndex), Rsa.int2BytesArray(operationEndIndex), bleService.getBleLockInfo().getAuthKey());
        bleService.sendCommand(operationCommand);

        operationDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        boolean b = operationCommand[1] == bleDataBean.getTsn();
                        return b;
                    }
                })

                .timeout(5000, TimeUnit.MILLISECONDS)  //间隔一秒没有数据，那么认为数据获取完成
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(
                        new Consumer<BleDataBean>() {
                            @Override
                            public void accept(BleDataBean bleDataBean) throws Exception {
                                if (bleDataBean.isConfirm()) {
                                    if (0x8b == (bleDataBean.getPayload()[0] & 0xff) && isFirst) {  //没有数据
                                        LogUtils.e("锁上   没有开锁记录  ");
                                        if (isSafe()) {
                                            mViewRef.get().noData();
                                        }
                                        toDisposable(operationDisposable);
                                    }
                                    return;
                                }
                                //判断是否是当前指令
                                if (bleDataBean.getCmd() != operationCommand[3]) {
                                    return;
                                }
                                isFirst = false;
                                byte[] deVaule = Rsa.decrypt(bleDataBean.getPayload(), bleService.getBleLockInfo().getAuthKey());
                                LogUtils.e("获取操作记录   解码之后的数据是   " + Rsa.bytesToHexString(deVaule) + "原始数据是   " + Rsa.toHexString(bleDataBean.getOriginalData()));
//                                OpenLockRecord openLockRecord = BleUtil.parseLockRecord(deVaule);
                                OperationLockRecord operationLockRecord = BleUtil.parseOperationRecord(deVaule);
                                LogUtils.e("获取操作记录是   " + operationLockRecord.toString());
                                if (operationLockRecords == null) {
                                    byte[] totalByte = new byte[2];
                                    System.arraycopy(deVaule, 0, totalByte, 0, 2);
                                    operationTotal = Rsa.bytesToInt(totalByte);
                                    operationLockRecords = new OperationLockRecord[operationTotal];
                                    operationMaxPage = (int) Math.ceil(operationTotal * 1.0 / 20.0);
                                    LogUtils.e(" 总个数   " + operationTotal + "  最大页数  " + operationMaxPage);
                                }
                                byte[] byteIndex = new byte[2];
                                System.arraycopy(deVaule, 2, byteIndex, 0, 2);
                                int index = Rsa.bytesToInt(byteIndex);
                                LogUtils.d(" 1 index " + index + " operationTotal " + operationTotal);
                                operationLockRecords[index] = operationLockRecord;

                                // TODO: 2019/3/7  开锁记录测试
                                List<Integer> loseNumber = new ArrayList<>();
                                for (int i = 0; i < operationEndIndex && i < operationTotal; i++) {
                                    if (operationLockRecords[i] == null) { //数据不全
                                        loseNumber.add(i);
                                    }
                                }
                                if (isSafe()) {
                                    mViewRef.get().onLoseRecord(loseNumber);
                                }
                                // TODO: 2019/3/7  开锁记录测试

                                if (index == (operationEndIndex - 1) || index == (operationTotal - 1)) {  //收到一组最后一个数据  或者全部的最后一个数据
                                    for (int i = operationStartIndex; i < operationEndIndex && i < operationTotal; i++) {
                                        if (operationLockRecords[i] == null) { //如果一组  数据不全
                                            operationRetryTimes++;
                                            if (operationRetryTimes > 2) {  //如果已经尝试了三次  那么先显示数据
                                                if (isSafe()) {
                                                    mViewRef.get().onLoadBleOperationRecord(getNotNullOperationRecord());
                                                }
                                            }
                                            getOperationRecordByPage();
                                            return;
                                        }
                                    }
                                    if (isSafe()) {
                                        mViewRef.get().onLoadBleOperationRecord(getNotNullOperationRecord());
                                    }
                                    if (operationCurrentPage + 1 >= operationMaxPage) { //如果收到最后一组的最后一个数据   直接上传
                                        if (isSafe()) {
                                            mViewRef.get().onLoadBleOperationRecord(getNotNullOperationRecord());
                                            mViewRef.get().onLoadBleRecordFinish(true);
                                        }
                                        if (operationRecordDisposable != null && !operationRecordDisposable.isDisposed()) {
                                            operationRecordDisposable.dispose();
                                        }
                                        if (operationDisposable != null && !operationDisposable.isDisposed()) {
                                            operationDisposable.dispose();
                                        }
                                        upLoadOperationRecord(bleService.getBleLockInfo().getServerLockInfo().getLockName()
                                                , bleService.getBleLockInfo().getServerLockInfo().getLockNickName()
                                                , getOperationRecordToServer(), MyApplication.getInstance().getUid());
                                    } else {  //如果后面还有
                                        LogUtils.e("收到一组完整的数据");
                                        operationCurrentPage++;  //下一组数据
                                        operationRetryTimes = 0; //重试次数
                                        getOperationRecordByPage();  //获取数据
                                    }
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e("取消订阅了吗   " + operationDisposable.isDisposed() + "   " + throwable.getMessage());
//                                if (throwable instanceof TimeoutException) {  //5秒没有收到数据
                                if (operationLockRecords == null) {  //一个数据都没有收到  重试
                                    operationRetryTimes++;
                                    getOperationRecordByPage();
                                    return;
                                }
                                LogUtils.e("获取数据  超时   数据完成");

                                // TODO: 2019/3/7  开锁记录测试
                                List<Integer> loseNumber = new ArrayList<>();
                                for (int i = 0; i < operationEndIndex && i < operationTotal; i++) {
                                    if (operationLockRecords[i] == null) { //数据不全
                                        loseNumber.add(i);
                                    }
                                }
                                if (isSafe()) {
                                    mViewRef.get().onLoseRecord(loseNumber);
                                }
                                // TODO: 2019/3/7  开锁记录测试
                                for (int i = operationStartIndex; i < operationEndIndex && i < operationTotal; i++) {
                                    if (operationLockRecords[i] == null) { //数据不全
                                        LogUtils.e("数据不全  " + operationRetryTimes);
                                        operationRetryTimes++;
                                        if (operationRetryTimes > 2) {  //如果已经尝试了三次  那么先显示数据
                                            if (isSafe()) {
                                                mViewRef.get().onLoadBleOperationRecord(getNotNullOperationRecord());
                                            }
                                        }
                                        getOperationRecordByPage();
                                        return;
                                    }
                                }
                                //到此处，那么说明这一组数据完整不重新获取
                                if (operationCurrentPage + 1 >= operationMaxPage) { //如果收到最后一组的最后一个数据   直接上传
                                    // 获取数据完成
                                    if (isSafe()) {
                                        mViewRef.get().onLoadBleOperationRecord(getNotNullOperationRecord());
                                        mViewRef.get().onLoadBleRecordFinish(true);
                                    }
                                    if (operationRecordDisposable != null && !operationRecordDisposable.isDisposed()) {
                                        operationRecordDisposable.dispose();
                                    }
                                    upLoadOperationRecord(bleService.getBleLockInfo().getServerLockInfo().getLockName()
                                            , bleService.getBleLockInfo().getServerLockInfo().getLockNickName()
                                            , getOperationRecordToServer(), MyApplication.getInstance().getUid());
                                } else {  //如果后面还有
                                    operationCurrentPage++;  //下一组数据
                                    operationRetryTimes = 0; //重试次数
                                    getOperationRecordByPage();  //获取数据
                                }
                            }
                        }
                );

        compositeDisposable.add(operationDisposable);

    }


    public void upLoadOperationRecord(String device_name, String device_nickname, List<UploadOperationRecordBean.OperationListBean> openLockList, String user_id) {

        for (UploadOperationRecordBean.OperationListBean bleRecord : openLockList) {
            LogUtils.e("上传的数据是    " + bleRecord.toString());
        }
        LogUtils.e("数据获取完成   operationTotal" + operationTotal + "  获取到的个数是  " + openLockList.size());
        XiaokaiNewServiceImp.uploadOperationRecord(device_name, openLockList)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("上传操作记录成功");
                        if (isSafe()) {
                            mViewRef.get().onUploadServerRecordSuccess();
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().onUploadServerRecordFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("上传开锁记录失败");
                        if (isSafe()) {
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
    public void listenerOperationLockRecord() {
        toDisposable(operationRecordDisposable);
        operationRecordDisposable = bleService.listeneDataChange()
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
                            System.arraycopy(deVaule, 2, byteIndex, 0, 2);
                            int index = Rsa.bytesToInt(byteIndex);
                            if (operationLockRecords != null && index < operationLockRecords.length) {
                                LogUtils.d(" 操作记录 index " + index + " operationTotal " + operationTotal);
                                operationLockRecords[index] = operationLockRecord;
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(operationRecordDisposable);
    }

    @Override
    public void authSuccess() {

    }

}