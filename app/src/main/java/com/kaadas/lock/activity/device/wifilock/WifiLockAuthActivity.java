package com.kaadas.lock.activity.device.wifilock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.device.bluetooth.BleDeviceInfoActivity;
import com.kaadas.lock.activity.device.oldbluetooth.OldDeviceInfoActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.ble.OldAndAuthBleDetailPresenter;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockAuthPresenter;
import com.kaadas.lock.mvp.view.IOldBleDetailView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockAuthView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
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

public class WifiLockAuthActivity extends BaseActivity<IWifiLockAuthView, WifiLockAuthPresenter<IWifiLockAuthView>>
        implements IWifiLockAuthView, View.OnClickListener {
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
    @BindView(R.id.rl_device_information)
    RelativeLayout rlDeviceInformation;
    @BindView(R.id.iv_lock_icon)
    ImageView ivLockIcon;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;
    private static final int TO_MORE_REQUEST_CODE = 101;
    private Handler handler = new Handler();
    private WifiLockInfo wifiLockInfo;
    private String wifiSn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_lock_authorization);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        changeLockIcon();
        ivBack.setOnClickListener(this);
        tvOpenClock.setVisibility(View.INVISIBLE);
        ivDelete.setOnClickListener(this);
        showLockType();
        initListener();
        LogUtils.e("授权界面");
        rlDeviceInformation.setVisibility(View.VISIBLE);
        dealWithPower(wifiLockInfo.getPower(), wifiLockInfo.getUpdateTime());
    }

    private void showLockType() {
        String lockType = wifiLockInfo.getProductModel();
        if (!TextUtils.isEmpty(lockType)) {
            tvType.setText(StringUtil.getSubstringFive(lockType));
        }
    }

    private void changeLockIcon() {
        String productModel = wifiLockInfo.getProductModel();
        ivLockIcon.setImageResource(BleLockUtils.getAuthorizationImageByModel(productModel));
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
    protected WifiLockAuthPresenter<IWifiLockAuthView> createPresent() {
        return new WifiLockAuthPresenter<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showData();
        tvBluetoothName.setText(wifiLockInfo.getLockNickname());
    }

    @SuppressLint("SetTextI18n")
    private void showData() {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_device_information:
                //先拿本地的数据    然后拿读取到的数据
                Intent intent = new Intent(this, WifiLockAuthDeviceInfoActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                startActivity(intent);
                break;
            case R.id.iv_delete:
                AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.device_delete_dialog_head), getString(R.string.device_delete_lock_dialog_content), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {
                    }

                    @Override
                    public void right() {
                        mPresenter.deleteDevice(wifiSn);
                        showLoading(getString(R.string.is_deleting));
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


    private void dealWithPower(int power, long updateTime) {
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

        //todo  读取电量时间
        long readDeviceInfoTime = updateTime * 1000;
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
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDeleteDeviceSuccess() {
        hiddenLoading();
        ToastUtil.getInstance().showLong(R.string.delete_success);
        finish();
    }

    @Override
    public void onDeleteDeviceFailed(Throwable throwable) {
        hiddenLoading();
        ToastUtil.getInstance().showLong(R.string.delete_fialed);
    }

    @Override
    public void onDeleteDeviceFailedServer(BaseResult result) {
        hiddenLoading();
        ToastUtil.getInstance().showLong(R.string.delete_fialed);
    }
}
