package com.kaadas.lock.activity.device;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.DeviceDetailPresenter;
import com.kaadas.lock.mvp.view.IDeviceDetailView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.ToastUtil;

import net.sdvn.cmapi.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by David on 2019/4/10
 */
public class BluetoothLockAuthorizationActivity extends BaseBleActivity<IDeviceDetailView, DeviceDetailPresenter<IDeviceDetailView>> implements IDeviceDetailView, View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_bluetooth_name)
    TextView tvBluetoothName;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_open_clock)
    TextView tvOpenClock;
    @BindView(R.id.iv_power)
    ImageView ivPower;
    @BindView(R.id.tv_power)
    TextView tvPower;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.ll_power)
    LinearLayout llPower;
    @BindView(R.id.rl_device_information)
    RelativeLayout rlDeviceInformation;
    private String type;
    private BleLockInfo bleLockInfo;
    private boolean isX5 = false;
    private static final int TO_MORE_REQUEST_CODE = 101;
    int lockStatus = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_lock_authorization);
        ButterKnife.bind(this);
        bleLockInfo = mPresenter.getBleLockInfo();
        ivBack.setOnClickListener(this);
        tvOpenClock.setOnClickListener(this);
        initView();
        initListener();
        mPresenter.getAllPassword(bleLockInfo);
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
    protected DeviceDetailPresenter<IDeviceDetailView> createPresent() {
        return new DeviceDetailPresenter();
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

/*    private void changeLockPage() {
        if (isX5) {
            x5Layout.setVisibility(View.VISIBLE);
            t5Layout.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(bleLockInfo.getServerLockInfo().getModel())) {
                modelX5.setText(getText(R.string.type) + bleLockInfo.getServerLockInfo().getModel());
            }
            detailDeviceImage.setImageResource(R.mipmap.detail_device_x5);
        } else {
            x5Layout.setVisibility(View.GONE);
            t5Layout.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(bleLockInfo.getServerLockInfo().getModel())) {
                tvModelT5.setText(getText(R.string.type) + bleLockInfo.getServerLockInfo().getModel());
            }
            detailDeviceImage.setImageResource(R.mipmap.detail_device_t5);
        }


    }*/

    @SuppressLint("SetTextI18n")
    private void showData() {
        //todo 等从锁中获取自动还是手动模式进行展示
//        tvLockMode.setText();
        //默认为手动模式
        if (mPresenter.isAuth(bleLockInfo, true)) {
            authResult(true);
            if (bleLockInfo.getAutoMode() == 0) {
            } else if (bleLockInfo.getAutoMode() == 1) {
            }
            if (bleLockInfo.getBattery() != -1) {
                dealWithPower(bleLockInfo.getBattery());
            }
            mPresenter.getDeviceInfo();
        }
    }


    private void initListener() {
        rlDeviceInformation.setOnClickListener(this);
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
    }

    @Override
    public void onNeedRebind(int errorCode) {
    }

    @Override
    public void authResult(boolean isSuccess) {
        if (isSuccess) {
        } else {
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
        }
    }


    @Override
    public void onElectricUpdataFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void onStateUpdate(int type) {
        if (bleLockInfo.getAutoMode() == 0) {
        } else if (bleLockInfo.getAutoMode() == 1) {
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_open_clock:
                //开锁
                lockStatus = KeyConstants.OPEN_LOCK_SUCCESS;
                changLockStatus();
                break;
            case R.id.rl_device_information:
                Intent intent = new Intent(this, BluetoothDeviceInformationActivity.class);
                startActivity(intent);
                break;
        }
    }
    public void changLockStatus() {
        switch (lockStatus) {
            case KeyConstants.OPEN_LOCK:
                //可以开锁
                tvOpenClock.setClickable(true);
                tvOpenClock.setText(R.string.click_lock);
                tvOpenClock.setTextColor(getResources().getColor(R.color.c16B8FD));
                tvOpenClock.setBackgroundResource(R.mipmap.open_lock_bj);
                break;
            case KeyConstants.DEVICE_OFFLINE:
                //设备离线
                tvOpenClock.setClickable(false);
                tvOpenClock.setText(getString(R.string.device_offline));
                tvOpenClock.setTextColor(getResources().getColor(R.color.c149EF3));
                tvOpenClock.setBackgroundResource(R.mipmap.has_been_locked_bj);
                break;
      /*      case KeyConstants.HAS_BEEN_LOCKED:
                //已反锁
                tvOpenClock.setText(getString(R.string.has_been_locked));
                tvOpenClock.setTextColor(getResources().getColor(R.color.c149EF3));
                tvOpenClock.setBackgroundResource(R.mipmap.has_been_locked_bj);
                break;
            case KeyConstants.IS_LOCKING:
                //正在开锁中
                tvOpenClock.setText(getString(R.string.is_locking));
                tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                tvOpenClock.setBackgroundResource(R.mipmap.is_locking_bj);
                break;
            case KeyConstants.OPEN_LOCK_SUCCESS:
                //开锁成功
                tvOpenClock.setText(getString(R.string.open_lock_success));
                tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                tvOpenClock.setBackgroundResource(R.mipmap.open_lock_success_bj);
                break;
            case KeyConstants.OPEN_LOCK_FAILED:
                //开锁失败
                tvOpenClock.setText(getString(R.string.open_lock_failed));
                tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                tvOpenClock.setBackgroundResource(R.mipmap.open_lock_fail_bj);
                break;*/
        }
    }

    private void dealWithPower(int power) {
        //电量：80%
        if (power > 100) {
            power = 100;
        }
        String lockPower = power + "%";
        int imgResId = -1;
        tvPower.setText(lockPower);
        if (power == 0) {
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
        }
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


}
