package com.kaadas.lock.activity.device.oldbluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.OldBluetoothDeviceDetailPresenter;
import com.kaadas.lock.mvp.view.IOldBluetoothDeviceDetailView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BatteryView;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import net.sdvn.cmapi.util.LogUtils;

import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/10
 */
public class OldBluetoothLockDetailActivity extends BaseBleActivity<IOldBluetoothDeviceDetailView, OldBluetoothDeviceDetailPresenter<IOldBluetoothDeviceDetailView>> implements IOldBluetoothDeviceDetailView, View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_bluetooth_name)
    TextView tvBluetoothName;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_open_clock)
    TextView tvOpenClock;
    @BindView(R.id.iv_power)
    BatteryView ivPower;
    @BindView(R.id.tv_power)
    TextView tvPower;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.ll_power)
    LinearLayout llPower;
    @BindView(R.id.rl_device_information)
    RelativeLayout rlDeviceInformation;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;
    private String type;
    private BleLockInfo bleLockInfo;
    private boolean isX5 = false;
    private static final int TO_MORE_REQUEST_CODE = 101;
    int lockStatus = -1;
    private Runnable lockRunnable;
    private boolean isOpening = false;
    private Handler handler = new Handler();
    boolean hasMoreItem = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_bluetooth_lock_detail);
        ButterKnife.bind(this);
        bleLockInfo = mPresenter.getBleLockInfo();
        ivBack.setOnClickListener(this);
        tvOpenClock.setOnClickListener(this);
        initView();
        initListener();
        hasMoreItem = true;
        showMoreItem();
        mPresenter.getAllPassword(bleLockInfo);
        lockRunnable = new Runnable() {
            @Override
            public void run() {
                isOpening = false;
//                lockStatus = KeyConstants.OPEN_LOCK;
                changLockStatus(0);
                if (bleLockInfo.getBackLock() == 0) {  //等于0时是反锁状态
//                    lockStatus = KeyConstants.HAS_BEEN_LOCKED;
                    changLockStatus(2);
                }
                if (bleLockInfo.getSafeMode() == 1) {//安全模式

                }
                if (bleLockInfo.getArmMode() == 1) {//布防模式

                }
            }
        };

        if (mPresenter.getBleVersion() != 2) {
            rlDeviceInformation.setVisibility(View.INVISIBLE);
        } else {
            rlDeviceInformation.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected OldBluetoothDeviceDetailPresenter<IOldBluetoothDeviceDetailView> createPresent() {
        return new OldBluetoothDeviceDetailPresenter<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter.getBleLockInfo() != null && mPresenter.getBleLockInfo().getServerLockInfo() != null && mPresenter.getBleLockInfo().getServerLockInfo().getLockNickName() != null) {
            LogUtils.e("设备昵称是   " + mPresenter.getBleLockInfo().getServerLockInfo().getLockNickName());
            tvBluetoothName.setText(mPresenter.getBleLockInfo().getServerLockInfo().getLockNickName());
        }
    }

    private void initView() {
        Intent intent = getIntent();
        type = intent.getStringExtra(KeyConstants.DEVICE_TYPE);
        showData();
    }


    @SuppressLint("SetTextI18n")
    private void showData() {
        //todo 等从锁中获取自动还是手动模式进行展示
        if (mPresenter.isAuth(bleLockInfo, true)) {
            authResult(true);
            if (bleLockInfo.getAutoMode() == 0) {
            } else if (bleLockInfo.getAutoMode() == 1) {
            }
            if (bleLockInfo.getBattery() != -1) {
                dealWithPower(bleLockInfo.getBattery());
            } else {
                mPresenter.getPower();
            }

        }
    }


    private void initListener() {
        rlDeviceInformation.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TO_MORE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data.getBooleanExtra(KeyConstants.IS_DELETE, false)) {
                finish();
            }
        }
    }

    @Override
    public void onDeviceStateChange(boolean isConnected) {
        if (isConnected) {
        } else {
        }
    }

    @Override
    public void onStartSearchDevice() {
    }

    @Override
    public void onSearchDeviceFailed(Throwable throwable) {
        changLockStatus(1);
    }

    @Override
    public void onNeedRebind(int errorCode) {
    }

    @Override
    public void authResult(boolean isSuccess) {
        if (isSuccess) {
            lockStatus = KeyConstants.OPEN_LOCK;
            changLockStatus(0);
        } else {
            lockStatus = KeyConstants.DEVICE_OFFLINE;
            changLockStatus(1);
        }
    }


    @Override
    public void onBleOpenStateChange(boolean isOpen) {
        if (!isOpen) {   //没有打开
        }
    }


    @Override
    public void onElectricUpdata(Integer power) {
        if (bleLockInfo.getAutoMode() == 0) {
        } else if (bleLockInfo.getAutoMode() == 1) {
        }
        if (bleLockInfo.getBattery() != -1) {
            dealWithPower(bleLockInfo.getBattery());
            //删除成功
            Intent intent = new Intent();
            //把返回数据存入Intent
            intent.putExtra(KeyConstants.BLE_INTO, bleLockInfo);
            //设置返回数据
            this.setResult(RESULT_OK, intent);
        }
    }


    @Override
    public void onElectricUpdataFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void onBleVersionUpdate(int version) {
        if (version != 1) {
            rlDeviceInformation.setVisibility(View.VISIBLE);
        } else {
            rlDeviceInformation.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDeleteDeviceSuccess() {
        ToastUtil.getInstance().showLong(R.string.delete_success);
        hiddenLoading();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDeleteDeviceFailed(Throwable throwable) {
        LogUtils.e("删除失败   " + throwable.getMessage());
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
//        ToastUtil.getInstance().showLong(R.string.delete_fialed);
        hiddenLoading();
    }

    @Override
    public void onDeleteDeviceFailedServer(BaseResult result) {
        LogUtils.e("删除失败   " + result.toString());
        String httpErrorCode = HttpUtils.httpErrorCode(this, result.getCode());
        ToastUtil.getInstance().showLong(httpErrorCode);
        hiddenLoading();
    }

    @Override
    public void notAdminMustHaveNet() {
        ToastUtil.getInstance().showLong(R.string.not_admin_must_have_net);
    }

    @Override
    public void inputPwd() {
        View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        EditText editText = mView.findViewById(R.id.et_name);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
        tvTitle.setText(getString(R.string.input_open_lock_password));
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString().trim();
                if (!StringUtil.randomJudge(name)) {
                    ToastUtil.getInstance().showShort(R.string.random_verify_error);
                    return;
                }
                mPresenter.realOpenLock(name, false);
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void authFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void authServerFailed(BaseResult baseResult) {

    }

    @Override
    public void isOpeningLock() {
        isOpening = true;
        changLockStatus(3);
    }

    @Override
    public void openLockSuccess() {
        changLockStatus(4);
        handler.removeCallbacks(lockRunnable);
        handler.postDelayed(lockRunnable, 15 * 1000);  //十秒后退出开门状态
    }

    @Override
    public void onLockLock() {
        handler.removeCallbacks(lockRunnable);
        lockRunnable.run();
    }

    @Override
    public void openLockFailed(Throwable throwable) {
        changLockStatus(5);
        if (throwable instanceof TimeoutException) {
            ToastUtil.getInstance().showShort(getString(R.string.open_lock_failed));
        } else if (throwable instanceof BleProtocolFailedException) {
            BleProtocolFailedException bleProtocolFailedException = (BleProtocolFailedException) throwable;
            ToastUtil.getInstance().showShort(getString(R.string.open_lock_failed));
        } else {
            ToastUtil.getInstance().showShort(getString(R.string.open_lock_failed));
        }
        handler.postDelayed(lockRunnable,3000);
    }

    @Override
    public void onSafeMode() {

    }

    @Override
    public void onArmMode() {

    }

    @Override
    public void onBackLock() {
        changLockStatus(2);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_open_clock:
                //开锁
                if (isOpening) {
                    LogUtils.e("长按  但是当前正在开锁状态   ");
                    return;
                }
                if (mPresenter.isAuth(bleLockInfo, true)) {
                    if (bleLockInfo.getBackLock() == 0 || bleLockInfo.getSafeMode() == 1) {  //反锁状态下或者安全模式下  长按不操作
                        if (bleLockInfo.getSafeMode() == 1) {
                            ToastUtil.getInstance().showLong(R.string.safe_mode_can_not_open);
                        } else if (bleLockInfo.getBackLock() == 0) {
                            ToastUtil.getInstance().showLong(R.string.back_lock_can_not_open);
                            changLockStatus(2);
                        }
                        return;
                    }
                    mPresenter.currentOpenLock();
                }
                vibrate(this, 150);
                break;
            case R.id.rl_device_information:
                Intent intent = new Intent(this, OldBluetoothMoreActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_delete:
                AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.device_delete_dialog_head), getString(R.string.device_delete_lock_dialog_content), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        showLoading(getString(R.string.is_deleting));
                        mPresenter.deleteDevice(bleLockInfo.getServerLockInfo().getLockName());
                    }
                });
                break;
        }
    }

    //震动milliseconds毫秒
    public static void vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    public void changLockStatus(int state) {
        switch (state) {
//            case KeyConstants.OPEN_LOCK:
            case 0:
                //可以开锁
                tvOpenClock.setEnabled(true);
                tvOpenClock.setText(R.string.click_lock);
                tvOpenClock.setTextColor(getResources().getColor(R.color.c16B8FD));
                tvOpenClock.setBackgroundResource(R.mipmap.open_lock_bj);
                break;
//            case KeyConstants.DEVICE_OFFLINE:
            case 1:
                //设备离线
                tvOpenClock.setEnabled(false);
                tvOpenClock.setText(getString(R.string.device_offline));
                tvOpenClock.setTextColor(getResources().getColor(R.color.c149EF3));
                tvOpenClock.setBackgroundResource(R.mipmap.has_been_locked_bj);
                break;
//            case KeyConstants.HAS_BEEN_LOCKED:
            case 2:
                //已反锁
                tvOpenClock.setEnabled(false);
                tvOpenClock.setText(getString(R.string.has_been_locked));
                tvOpenClock.setTextColor(getResources().getColor(R.color.c149EF3));
                tvOpenClock.setBackgroundResource(R.mipmap.has_been_locked_bj);
                break;
//            case KeyConstants.IS_LOCKING:
            case 3:
                //正在开锁中
                tvOpenClock.setEnabled(false);
                tvOpenClock.setText(getString(R.string.is_locking));
                tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                tvOpenClock.setBackgroundResource(R.mipmap.is_locking_bj);
                break;
//            case KeyConstants.OPEN_LOCK_SUCCESS:
            case 4:
                //开锁成功
                tvOpenClock.setEnabled(false);
                tvOpenClock.setText(getString(R.string.open_lock_success));
                tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                tvOpenClock.setBackgroundResource(R.mipmap.open_lock_success_bj);
                break;
//            case KeyConstants.OPEN_LOCK_FAILED:
            case 5:
                //开锁失败
                tvOpenClock.setEnabled(false);
                tvOpenClock.setText(getString(R.string.open_lock_failed));
                tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                tvOpenClock.setBackgroundResource(R.mipmap.open_lock_fail_bj);
                break;
        }
    }

    private void dealWithPower(int power) {
        //电量：80%
        if (power > 100) {
            power = 100;
        }
        if (power<0){
            power=0;
        }

        String lockPower = power + "%";
        tvPower.setText(lockPower);
        if (ivPower != null) {
            ivPower.setPower(power);
            if (power<=20){
                ivPower.setColor(R.color.cFF3B30);
                ivPower.setBorderColor(R.color.white);
            }else{
                ivPower.setColor(R.color.c25F290);
                ivPower.setBorderColor(R.color.white);
            }
        }






      /*  if (power == 0) {
            imgResId = R.mipmap.horization_power_0;
        } else if (power <= 5) {
            imgResId = R.mipmap.horization_power_1;
        } else if (power <= 20) {
            imgResId = R.mipmap.horization_power_2;
        } else if (power <= 60) {
            imgResId = R.mipmap.horization_power_3;
        } else if (power <= 80) {
            imgResId = R.mipmap.horization_power_4;
//        } else if (power <= 100) {
        } else {
            imgResId = R.mipmap.horization_power_5;
        }
        if (imgResId != -1) {
            ivPower.setImageResource(imgResId);
        }*/
        //todo  读取电量时间
        long readDeviceInfoTime = System.currentTimeMillis();
        if (readDeviceInfoTime != -1) {
            if ((System.currentTimeMillis() - readDeviceInfoTime) < 60 * 60 * 1000) {
                //小于一小时
                tvDate.setText(getString(R.string.device_detail_power_date));
            } else if ((System.currentTimeMillis() - readDeviceInfoTime) < 24 * 60 * 60 * 1000) {
                //小于一天
                tvDate.setText(getString(R.string.today) + " " + DateUtils.currentLong2HourMin(readDeviceInfoTime));
            } else if ((System.currentTimeMillis() - readDeviceInfoTime) < 2 * 24 * 60 * 60 * 1000) {
                tvDate.setText(getString(R.string.yesterday) + " " + DateUtils.currentLong2HourMin(readDeviceInfoTime));
            } else {
                tvDate.setText(DateUtils.formatYearMonthDay(readDeviceInfoTime));
            }
        }


    }

    private void showMoreItem() {
        if (hasMoreItem) {
            rlDeviceInformation.setVisibility(View.VISIBLE);
            rlDeviceInformation.setEnabled(true);
        } else {
            rlDeviceInformation.setVisibility(View.INVISIBLE);
            rlDeviceInformation.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(lockRunnable);
    }
}
