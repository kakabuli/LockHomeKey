package com.kaadas.lock.activity.device.oldbluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.device.bluetooth.BluetoothSharedDeviceManagementActivity;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.ble.OldAndAuthBleDetailPresenter;
import com.kaadas.lock.mvp.view.IOldBleDetailView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BatteryView;
import com.kaadas.lock.utils.BleLockUtils;
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
public class OldBleDetailActivity extends BaseBleActivity<IOldBleDetailView, OldAndAuthBleDetailPresenter<IOldBleDetailView>> implements IOldBleDetailView, View.OnClickListener {

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
    @BindView(R.id.rl_device_share)
    RelativeLayout rlDeviceShare;
    @BindView(R.id.ll_bluetooth_18)
    LinearLayout llBluetooth18;
    @BindView(R.id.rl_bluetooth_17)
    RelativeLayout rlBluetooth17;
    @BindView(R.id.device_image)
    ImageView deviceImage;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;

    private BleLockInfo bleLockInfo;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        ButterKnife.bind(this);
        bleLockInfo = mPresenter.getBleLockInfo();
        ivBack.setOnClickListener(this);
        tvOpenClock.setOnClickListener(this);
        initListener();
        hasMoreItem = true;
        showMoreItem();
        lockRunnable = new Runnable() {
            @Override
            public void run() {
                isOpening = false;
                if (bleLockInfo.isAuth()) {
                    changLockStatus(0);
                }
            }
        };

        if (mPresenter.getBleVersion() == 2 || mPresenter.getBleVersion() == 3 ||
                (bleLockInfo != null && bleLockInfo.getServerLockInfo() != null && !TextUtils.isEmpty(bleLockInfo.getServerLockInfo().getBleVersion()) &&
                        "2".equals(bleLockInfo.getServerLockInfo().getBleVersion()))
                ||
                (bleLockInfo != null && bleLockInfo.getServerLockInfo() != null && !TextUtils.isEmpty(bleLockInfo.getServerLockInfo().getBleVersion()) &&
                        "3".equals(bleLockInfo.getServerLockInfo().getBleVersion()))
                ) {
            changeBluetoothFunction(18);
        } else {
            changeBluetoothFunction(17);
        }
        if(bleLockInfo.getServerLockInfo() != null){
            deviceImage.setImageResource( BleLockUtils.getDetailImageByModel(bleLockInfo.getServerLockInfo().getModel()));
        }else{
            deviceImage.setImageResource(R.mipmap.bluetooth_lock_default);
        }
        //动态设置状态栏高度
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(titleBar.getLayoutParams());
        lp.setMargins(0, getStatusBarHeight(), 0, 0);
        titleBar.setLayoutParams(lp);
    }


    private void changeBluetoothFunction(int bluetoothType) {
        if (17 == bluetoothType) {
            llBluetooth18.setVisibility(View.GONE);
            rlBluetooth17.setVisibility(View.VISIBLE);
        } else if (18 == bluetoothType) {
            llBluetooth18.setVisibility(View.VISIBLE);
            rlBluetooth17.setVisibility(View.GONE);
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
    protected OldAndAuthBleDetailPresenter<IOldBleDetailView> createPresent() {
        return new OldAndAuthBleDetailPresenter<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showData();
        if (mPresenter.getBleLockInfo() != null && mPresenter.getBleLockInfo().getServerLockInfo() != null && mPresenter.getBleLockInfo().getServerLockInfo().getLockNickName() != null) {
            LogUtils.e("设备昵称是   " + mPresenter.getBleLockInfo().getServerLockInfo().getLockNickName());
            tvBluetoothName.setText(mPresenter.getBleLockInfo().getServerLockInfo().getLockNickName());
        }
    }

    @SuppressLint("SetTextI18n")
    private void showData() {
        //todo 等从锁中获取自动还是手动模式进行展示
        if (mPresenter.isAuth(bleLockInfo, true)) {
            authResult(true);
            if (bleLockInfo.getBattery() != -1) {
                dealWithPower(bleLockInfo.getBattery());
            } else {
                mPresenter.getPower();
            }
        }
    }


    private void initListener() {
        rlDeviceInformation.setOnClickListener(this);
        rlBluetooth17.setOnClickListener(this);
        rlDeviceShare.setOnClickListener(this);
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
            changLockStatus(1);  //设备断开连接   连接上不处理
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
            changeBluetoothFunction(18);
        } else {
            changeBluetoothFunction(17);
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
    public void onDeviceInfoLoaded() {
        //读取到设备信息    老蓝牙模块界面不处理
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
        handler.postDelayed(lockRunnable, 3000);
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
                    ToastUtil.getInstance().showLong(R.string.is_openning);
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
                    mPresenter.openLock();
                }
                vibrate(this, 150);
                break;
            case R.id.rl_device_information:
                LogUtils.e("点击设备详情   ");
                Intent intent = new Intent(this, OldDeviceInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_device_share:
            case R.id.rl_bluetooth_17:
                LogUtils.e("点击分享   ");
                intent = new Intent(this, BluetoothSharedDeviceManagementActivity.class);
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
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                    @Override
                    public void afterTextChanged(String toString) {
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
        if (isFinishing()) {
            return;
        }
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
            case 3:
                //正在开锁中
                tvOpenClock.setEnabled(false);
                tvOpenClock.setText(getString(R.string.is_locking));
                tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                tvOpenClock.setBackgroundResource(R.mipmap.is_locking_bj);
                break;
            case 4:
                //开锁成功
                tvOpenClock.setEnabled(false);
                tvOpenClock.setText(getString(R.string.open_lock_success));
                tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                tvOpenClock.setBackgroundResource(R.mipmap.open_lock_success_bj);
                break;
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
        if (power < 0) {
            power = 0;
        }

        String lockPower = power + "%";
        tvPower.setText(lockPower);
        if (ivPower != null) {
            ivPower.setPower(power);
            if (power <= 20) {
                ivPower.setColor(R.color.cFF3B30);
                ivPower.setBorderColor(R.color.white);
            } else {
                ivPower.setColor(R.color.c25F290);
                ivPower.setBorderColor(R.color.white);
            }
        }

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
            changeBluetoothFunction(18);
        } else {
            changeBluetoothFunction(17);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    //获取状态栏高度
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
