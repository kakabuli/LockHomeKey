package com.kaadas.lock.activity.device.wifilock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.bluetooth.BluetoothMoreActivity;
import com.kaadas.lock.activity.device.bluetooth.BluetoothSharedDeviceManagementActivity;
import com.kaadas.lock.activity.device.bluetooth.card.DoorCardManagerActivity;
import com.kaadas.lock.activity.device.bluetooth.fingerprint.FingerprintManagerActivity;
import com.kaadas.lock.activity.device.bluetooth.password.BlePasswordManagerActivity;
import com.kaadas.lock.activity.device.oldbluetooth.OldDeviceInfoActivity;
import com.kaadas.lock.adapter.WifiLockDetailAdapater;
import com.kaadas.lock.adapter.WifiLockDetailOneLineAdapater;
import com.kaadas.lock.bean.WifiLockFunctionBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockDetailPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockDetailView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.utils.BatteryView;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.widget.MyGridItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WiFiLockDetailActivity extends BaseActivity<IWifiLockDetailView, WifiLockDetailPresenter<IWifiLockDetailView>>
        implements IWifiLockDetailView, View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_bluetooth_name)
    TextView tvBluetoothName;
    @BindView(R.id.iv_lock_icon)
    ImageView ivLockIcon;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_lock_type)
    TextView tvLockType;
    @BindView(R.id.iv_power)
    BatteryView ivPower;
    @BindView(R.id.tv_power)
    TextView tvPower;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.ll_power)
    LinearLayout llPower;
    @BindView(R.id.detail_function_recyclerView)
    RecyclerView detailFunctionRecyclerView;
    @BindView(R.id.detail_function_onLine)
    RecyclerView detailFunctionOnLine;
    private WifiLockDetailAdapater adapater;
    private WifiLockDetailOneLineAdapater oneLineAdapater;

    private static final int TO_MORE_REQUEST_CODE = 101;
    private Handler handler = new Handler();
    String lockType;
    BleLockInfo bleLockInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.e("全功能界面   1");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_lock_detail);
        LogUtils.e("全功能界面  2 ");
        ButterKnife.bind(this);
        Intent intent = getIntent();
        changeLockIcon(intent);
        ivBack.setOnClickListener(this);
        showLockType();
        initRecycleview();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.attachView(this);
        int userManageNumber = (int) SPUtils.getProtect(KeyConstants.USER_MANAGE_NUMBER + "" + bleLockInfo.getServerLockInfo().getLockName(), 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.detachView();
    }

    private void showLockType() {
        if (bleLockInfo == null) {
            tvType.setText("");
            return;
        }
        lockType = bleLockInfo.getServerLockInfo().getModel();
        if (!TextUtils.isEmpty(lockType)) {
            tvType.setText(StringUtil.getSubstringFive(lockType));
        }
    }

    private void changeLockIcon(Intent intent) {
        String model = intent.getStringExtra(KeyConstants.DEVICE_TYPE);
        LogUtils.e("获取到的设备型号是   " + model);
        ivLockIcon.setImageResource(BleLockUtils.getDetailImageByModel(model));
    }

    @Override
    protected WifiLockDetailPresenter<IWifiLockDetailView> createPresent() {
        return new WifiLockDetailPresenter<>();
    }

    @SuppressLint("SetTextI18n")
    private void showData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //每次显示界面都重新设置状态和电量
    }

    private void initRecycleview() {
//        String functionSet = bleLockInfo.getServerLockInfo().getFunctionSet(); //锁功能集
        String functionSet = ""; //锁功能集
        int func = Integer.parseInt(functionSet);
        LogUtils.e("功能集是   " + func);
        List<WifiLockFunctionBean> supportFunction = BleLockUtils.getWifiLockSupportFunction(func);
        LogUtils.e("获取到的功能集是   " + supportFunction.size());

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

        adapater = new WifiLockDetailAdapater(supportFunction, new WifiLockDetailAdapater.OnItemClickListener() {
            @Override
            public void onItemClick(int position, WifiLockFunctionBean bluetoothLockFunctionBean) {
                Intent intent;
                switch (bluetoothLockFunctionBean.getType()) {
                    case BleLockUtils.TYPE_PASSWORD:
                        intent = new Intent(WiFiLockDetailActivity.this, BlePasswordManagerActivity.class);
                        startActivity(intent);
                        break;
                    case BleLockUtils.TYPE_FINGER:
                        intent = new Intent(WiFiLockDetailActivity.this, FingerprintManagerActivity.class);
                        startActivity(intent);
                        break;
                    case BleLockUtils.TYPE_CARD:
                        intent = new Intent(WiFiLockDetailActivity.this, DoorCardManagerActivity.class);
                        startActivity(intent);
                        break;
                    case BleLockUtils.TYPE_SHARE:
                        intent = new Intent(WiFiLockDetailActivity.this, BluetoothSharedDeviceManagementActivity.class);
                        startActivity(intent);
                        break;
                    case BleLockUtils.TYPE_MORE:
                        intent = new Intent(WiFiLockDetailActivity.this, BluetoothMoreActivity.class);
                        if (lockType.startsWith("S8") || lockType.startsWith("V6") || lockType.startsWith("V7") || lockType.startsWith("S100")) {
                            intent.putExtra(KeyConstants.SOURCE, "BluetoothLockFunctionV6V7Activity");
                        }
                        startActivityForResult(intent, TO_MORE_REQUEST_CODE);
                        break;
                }
            }
        });
        oneLineAdapater = new WifiLockDetailOneLineAdapater(supportFunction, new WifiLockDetailOneLineAdapater.OnItemClickListener() {
            @Override
            public void onItemClick(int position, WifiLockFunctionBean bluetoothLockFunctionBean) {
                Intent intent;
                LogUtils.e("点击类型是    " + bluetoothLockFunctionBean.getType());
                switch (bluetoothLockFunctionBean.getType()) {
                    case BleLockUtils.TYPE_PASSWORD:
                        intent = new Intent(WiFiLockDetailActivity.this, BlePasswordManagerActivity.class);
                        startActivity(intent);
                        break;
                    case BleLockUtils.TYPE_FINGER:
                        intent = new Intent(WiFiLockDetailActivity.this, FingerprintManagerActivity.class);
                        startActivity(intent);
                        break;
                    case BleLockUtils.TYPE_CARD:
                        intent = new Intent(WiFiLockDetailActivity.this, DoorCardManagerActivity.class);
                        startActivity(intent);
                        break;
                    case BleLockUtils.TYPE_SHARE:
                        LogUtils.e("分享   ");
                        intent = new Intent(WiFiLockDetailActivity.this, BluetoothSharedDeviceManagementActivity.class);
                        startActivity(intent);
                        break;
                    case BleLockUtils.TYPE_MORE:
                        LogUtils.e("更多   ");  //这是老模块的  跳转地方
                        intent = new Intent(WiFiLockDetailActivity.this, OldDeviceInfoActivity.class);
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

        }
    }

    //震动milliseconds毫秒
    public static void vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
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
    protected void onDestroy() {
        super.onDestroy();
    }




}