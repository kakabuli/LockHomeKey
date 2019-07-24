package com.kaadas.lock.activity.device;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.bluetooth.BluetoothMoreActivity;
import com.kaadas.lock.activity.device.bluetooth.BluetoothSharedDeviceManagementActivity;
import com.kaadas.lock.activity.device.bluetooth.card.DoorCardManagerActivity;
import com.kaadas.lock.activity.device.bluetooth.fingerprint.FingerprintManagerActivity;
import com.kaadas.lock.activity.device.bluetooth.password.BluetoothPasswordManagerActivity;
import com.kaadas.lock.adapter.BluetoothFunctionAdapater;
import com.kaadas.lock.adapter.BluetoothFunctionOneLineAdapater;
import com.kaadas.lock.bean.BluetoothLockFunctionBean;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.DeviceDetailPresenter;
import com.kaadas.lock.mvp.view.IDeviceDetailView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BatteryView;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.widget.MyGridItemDecoration;

import net.sdvn.cmapi.util.LogUtils;

import java.util.List;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/10
 */
public class BleDetailActivity extends BaseBleActivity<IDeviceDetailView, DeviceDetailPresenter<IDeviceDetailView>> implements IDeviceDetailView, View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_bluetooth_name)
    TextView tvBluetoothName;//蓝牙门锁
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.iv_power)
    BatteryView ivPower;//电量图片
    @BindView(R.id.tv_power)
    TextView tvPower;//电量大小
    @BindView(R.id.tv_date)
    TextView tvDate;//日期
    @BindView(R.id.ll_power)
    LinearLayout llPower;
    //    @BindView(R.id.rv)
//    RecyclerView rv;

    private BluetoothFunctionAdapater adapater;
    private BluetoothFunctionOneLineAdapater oneLineAdapater;
    @BindView(R.id.iv_one)
    ImageView ivOne;
    @BindView(R.id.tv_name_one)
    TextView tvNameOne;
    @BindView(R.id.tv_number_one)
    TextView tvNumberOne;
    @BindView(R.id.ll_one)
    LinearLayout llOne;
    @BindView(R.id.iv_two)
    ImageView ivTwo;
    @BindView(R.id.tv_name_two)
    TextView tvNameTwo;
    @BindView(R.id.tv_number_two)
    TextView tvNumberTwo;
    @BindView(R.id.ll_two)
    LinearLayout llTwo;
    @BindView(R.id.iv_three)
    ImageView ivThree;
    @BindView(R.id.tv_name_three)
    TextView tvNameThree;
    @BindView(R.id.tv_number_three)
    TextView tvNumberThree;
    @BindView(R.id.ll_three)
    LinearLayout llThree;
    @BindView(R.id.iv_four)
    ImageView ivFour;
    @BindView(R.id.tv_name_four)
    TextView tvNameFour;
    @BindView(R.id.tv_number_four)
    TextView tvNumberFour;
    @BindView(R.id.ll_four)
    LinearLayout llFour;
    @BindView(R.id.iv_five)
    ImageView ivFive;
    @BindView(R.id.tv_name_five)
    TextView tvNameFive;
    @BindView(R.id.tv_number_five)
    TextView tvNumberFive;
    @BindView(R.id.ll_five)
    LinearLayout llFive;
    @BindView(R.id.iv_six)
    ImageView ivSix;
    @BindView(R.id.tv_name_six)
    TextView tvNameSix;
    @BindView(R.id.tv_number_six)
    TextView tvNumberSix;
    @BindView(R.id.ll_six)
    LinearLayout llSix;
    @BindView(R.id.tv_open_clock)
    TextView tvOpenClock;
    int lockStatus = -1;
    @BindView(R.id.iv_lock_icon)
    ImageView ivLockIcon;
    @BindView(R.id.detail_function_recyclerView)
    RecyclerView detailFunctionRecyclerView;
    @BindView(R.id.detail_function_onLine)
    RecyclerView detailFunctionOnLine;
    private BleLockInfo bleLockInfo;
    private static final int TO_MORE_REQUEST_CODE = 101;
    private boolean isOpening = false;
    private Runnable lockRunnable;
    private boolean isConnectingDevice;
    private Handler handler = new Handler();
    String lockType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_lock_function);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        changeLockIcon(intent);
        bleLockInfo = mPresenter.getBleLockInfo();
        ivBack.setOnClickListener(this);
        showLockType();
//        initData();
        initClick();
        showData();
        lockRunnable = new Runnable() {
            @Override
            public void run() {
                LogUtils.e(" 首页锁状态  反锁状态   " + bleLockInfo.getBackLock() + "    安全模式    " + bleLockInfo.getSafeMode() + "   布防模式   " + bleLockInfo.getArmMode());
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
        initRecycleview();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.attachView(this);
        mPresenter.getAllPassword(bleLockInfo);
        int userManageNumber = (int) SPUtils.getProtect(KeyConstants.USER_MANAGE_NUMBER + "" + bleLockInfo.getServerLockInfo().getLockName(), 0);
        tvNumberFour.setText(userManageNumber + getString(R.string.people));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.detachView();
    }

    private void showLockType() {
        lockType = bleLockInfo.getServerLockInfo().getModel();

        if (!TextUtils.isEmpty(lockType)) {
            tvType.setText(StringUtil.getSubstringFive(lockType));
        }
    }

    private void changeLockIcon(Intent intent) {
        String model = intent.getStringExtra(KeyConstants.DEVICE_TYPE);
        if (!TextUtils.isEmpty(model)) {
            if (model.contains("K7")) {
                ivLockIcon.setImageResource(R.mipmap.bluetooth_lock_k7);
            } else if (model.contains("S8C")) {
                ivLockIcon.setImageResource(R.mipmap.bluetooth_lock_s8);
            } else if (model.contains("V6")) {
                ivLockIcon.setImageResource(R.mipmap.bluetooth_lock_v6);
            } else if (model.contains("V7") || model.contains("S100")) {
                ivLockIcon.setImageResource(R.mipmap.bluetooth_lock_v7);
            } else if (model.contains("K8-T")) {
                ivLockIcon.setImageResource(R.mipmap.bluetooth_lock_k8_t);
            } else if (model.contains("K8")) {
                ivLockIcon.setImageResource(R.mipmap.bluetooth_lock_k8);
            } else if (model.contains("K9")) {
                ivLockIcon.setImageResource(R.mipmap.bluetooth_lock_k9);
            } else if (model.contains("KX")) {
                ivLockIcon.setImageResource(R.mipmap.bluetooth_lock_kx);
            } else if (model.contains("S8")) {
                ivLockIcon.setImageResource(R.mipmap.bluetooth_lock_s8);
            } else if (model.contains("QZ013")) {
                ivLockIcon.setImageResource(R.mipmap.bluetooth_lock_qz013);
            } else if (model.contains("QZ012")) {
                ivLockIcon.setImageResource(R.mipmap.bluetooth_lock_qz012);
            } else if (model.contains("K100")) {
                ivLockIcon.setImageResource(R.mipmap.bluetooth_lock_k100);
            } else if (model.contains("S6")) {
                ivLockIcon.setImageResource(R.mipmap.bluetooth_lock_s6);
            }
        }
    }

    @Override
    protected DeviceDetailPresenter<IDeviceDetailView> createPresent() {
        return new DeviceDetailPresenter();
    }

    @SuppressLint("SetTextI18n")
    private void showData() {
        //todo 等从锁中获取自动还是手动模式进行展示
//        tvLockMode.setText();
        //默认为手动模式
        if (mPresenter.isAuth(bleLockInfo, false)) {
            authResult(true);
            if (bleLockInfo.getBattery() != -1) {
                dealWithPower(bleLockInfo.getBattery());
            }
            mPresenter.getDeviceInfo();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter.getBleLockInfo() != null && mPresenter.getBleLockInfo().getServerLockInfo() != null && mPresenter.getBleLockInfo().getServerLockInfo().getLockNickName() != null) {
            LogUtils.e("设备昵称是   " + mPresenter.getBleLockInfo().getServerLockInfo().getLockNickName());
            tvBluetoothName.setText(mPresenter.getBleLockInfo().getServerLockInfo().getLockNickName());
        }
    }

    private void initClick() {
        llOne.setOnClickListener(this);
        llTwo.setOnClickListener(this);
        llThree.setOnClickListener(this);
        llFour.setOnClickListener(this);
        llFive.setOnClickListener(this);
        tvOpenClock.setOnClickListener(this);
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

    private void initData() {
        tvBluetoothName.setText("jfjif");
        ivOne.setImageResource(R.mipmap.bluetooth_password);
        tvNameOne.setText(R.string.password);
        tvNumberOne.setText(0 + getString(R.string.group));
        ivTwo.setImageResource(R.mipmap.bluetooth_fingerprint);
        tvNameTwo.setText(R.string.fingerprint);
        tvNumberTwo.setText(0 + getString(R.string.ge));
        ivThree.setImageResource(R.mipmap.bluetooth_card);
        tvNameThree.setText(R.string.card);
        tvNumberThree.setText(0 + getString(R.string.zhang));
        ivFour.setImageResource(R.mipmap.bluetooth_share);
        tvNameFour.setText(R.string.device_share);
        tvNumberFour.setText(0 + getString(R.string.people));
        ivFive.setImageResource(R.mipmap.bluetooth_more);
        tvNameFive.setText(R.string.more);

    }

    private void initRecycleview() {
        String functionSet = bleLockInfo.getServerLockInfo().getFunctionSet(); //锁功能集
        int func = Integer.parseInt(functionSet);
        List<BluetoothLockFunctionBean> supportFunction = BleLockUtils.getSupportFunction(func);
        MyGridItemDecoration dividerItemDecoration;
        if (supportFunction.size() <= 2) {
            detailFunctionOnLine.setLayoutManager(new GridLayoutManager(this, 2));
            dividerItemDecoration = new MyGridItemDecoration(this, 2);

            detailFunctionRecyclerView.setVisibility(View.GONE);
            detailFunctionOnLine.setVisibility(View.VISIBLE);
        } else if (supportFunction.size() <= 4) {
            dividerItemDecoration = new MyGridItemDecoration(this, 2);
            detailFunctionRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            detailFunctionRecyclerView.setVisibility(View.VISIBLE);
            detailFunctionOnLine.setVisibility(View.GONE);
        } else {
            detailFunctionRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            detailFunctionRecyclerView.setVisibility(View.VISIBLE);
            detailFunctionOnLine.setVisibility(View.GONE);
            dividerItemDecoration = new MyGridItemDecoration(this, 3);
        }

        detailFunctionOnLine.addItemDecoration(dividerItemDecoration);
        detailFunctionRecyclerView.addItemDecoration(dividerItemDecoration);

        adapater = new BluetoothFunctionAdapater(supportFunction, new BluetoothFunctionAdapater.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BluetoothLockFunctionBean bluetoothLockFunctionBean) {
                Intent intent;
                switch (bluetoothLockFunctionBean.getType()){
                    case BleLockUtils.TYPE_PASSWORD:
                        intent = new Intent(BleDetailActivity.this, BluetoothPasswordManagerActivity.class);
                        startActivity(intent);
                        break;
                    case BleLockUtils.TYPE_FINGER:
                        intent = new Intent(BleDetailActivity.this, FingerprintManagerActivity.class);
                        startActivity(intent);
                        break;
                    case BleLockUtils.TYPE_CARD:
                        intent = new Intent(BleDetailActivity.this, DoorCardManagerActivity.class);
                        startActivity(intent);
                        break;
                    case BleLockUtils.TYPE_SHARE:
                        intent = new Intent(BleDetailActivity.this, BluetoothSharedDeviceManagementActivity.class);
                        startActivity(intent);
                        break;
                    case BleLockUtils.TYPE_MORE:
                        intent = new Intent(BleDetailActivity.this, BluetoothMoreActivity.class);
                        if (lockType.startsWith("S8") || lockType.startsWith("V6") || lockType.startsWith("V7") || lockType.startsWith("S100")) {
                            intent.putExtra(KeyConstants.SOURCE, "BluetoothLockFunctionV6V7Activity");
                        }
                        startActivityForResult(intent, TO_MORE_REQUEST_CODE);
                        break;
                }
            }
        });
        oneLineAdapater = new BluetoothFunctionOneLineAdapater(supportFunction, new BluetoothFunctionOneLineAdapater.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BluetoothLockFunctionBean bluetoothLockFunctionBean) {
                Intent intent;
                switch (bluetoothLockFunctionBean.getType()){
                    case BleLockUtils.TYPE_PASSWORD:
                        intent = new Intent(BleDetailActivity.this, BluetoothPasswordManagerActivity.class);
                        startActivity(intent);
                        break;
                    case BleLockUtils.TYPE_FINGER:
                        intent = new Intent(BleDetailActivity.this, FingerprintManagerActivity.class);
                        startActivity(intent);
                        break;
                    case BleLockUtils.TYPE_CARD:
                        intent = new Intent(BleDetailActivity.this, DoorCardManagerActivity.class);
                        startActivity(intent);
                        break;
                    case BleLockUtils.TYPE_SHARE:
                        intent = new Intent(BleDetailActivity.this, BluetoothSharedDeviceManagementActivity.class);
                        startActivity(intent);
                        break;
                    case BleLockUtils.TYPE_MORE:
                        intent = new Intent(BleDetailActivity.this, BluetoothMoreActivity.class);
                        if (lockType.startsWith("S8") || lockType.startsWith("V6") || lockType.startsWith("V7") || lockType.startsWith("S100")) {
                            intent.putExtra(KeyConstants.SOURCE, "BluetoothLockFunctionV6V7Activity");
                        }
                        startActivityForResult(intent, TO_MORE_REQUEST_CODE);
                        break;
                }
            }
        });
        detailFunctionRecyclerView.setAdapter(adapater);
        detailFunctionOnLine.setAdapter(oneLineAdapater);


    }

    @Override
    public void onClick(View v) {
        Intent intent;
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
                    mPresenter.openLock();
                }
                vibrate(this, 150);
                break;
        }
    }

    //震动milliseconds毫秒
    public static void vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    @Override
    public void onSearchDeviceFailed(Throwable throwable) {
//        lockStatus = KeyConstants.DEVICE_OFFLINE;
        changLockStatus(1);
    }

    @Override
    public void authResult(boolean isSuccess) {
        if (isSuccess) {
//            lockStatus = KeyConstants.OPEN_LOCK;
            changLockStatus(0);
        } else {
//            lockStatus = KeyConstants.DEVICE_OFFLINE;
            changLockStatus(1);
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
        //电量：80%
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

        /*if (power == 0) {
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
    public void onElectricUpdata(Integer electric) {
        if (bleLockInfo != null && bleLockInfo.getBattery() != -1) {
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
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(lockRunnable);
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
    public void onGetPasswordSuccess(GetPasswordResult result) {
        super.onGetPasswordSuccess(result);
        if (result != null) {
            GetPasswordResult.DataBean dataBean = result.getData();
            if (dataBean != null) {
                List<GetPasswordResult.DataBean.Card> cardList = dataBean.getCardList();
                if (cardList != null) {
                    tvNumberThree.setText(cardList.size() + getString(R.string.zhang));
                }

                List<GetPasswordResult.DataBean.Fingerprint> fingerprintList = dataBean.getFingerprintList();
                if (fingerprintList != null) {
                    tvNumberTwo.setText(fingerprintList.size() + getString(R.string.ge));
                }
                List<ForeverPassword> pwdList = dataBean.getPwdList();
                List<GetPasswordResult.DataBean.TempPassword> tempPwdList = dataBean.getTempPwdList();
                if (tempPwdList != null) {
                    tvNumberOne.setText((pwdList.size() + tempPwdList.size()) + getString(R.string.group));
                }
                LogUtils.d("davi " + result.toString());
            }

        }

    }
}
