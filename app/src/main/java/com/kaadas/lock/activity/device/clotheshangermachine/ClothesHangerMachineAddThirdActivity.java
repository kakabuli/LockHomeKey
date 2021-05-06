package com.kaadas.lock.activity.device.clotheshangermachine;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewWiFiScanBLEFailedActivity;
import com.kaadas.lock.adapter.ClothesHangerMachineBleWiFiSearchAdapter;
import com.kaadas.lock.adapter.DeviceBleWiFiSearchAdapter;
import com.kaadas.lock.adapter.inf.OnBindClickListener;
import com.kaadas.lock.bean.BluetoothLockBroadcastBean;
import com.kaadas.lock.bean.BluetoothLockBroadcastListBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.clotheshangermachinepresenter.ClothesHangerMachineAddThirdPresenter;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineAddThirdView;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.AnimationsContainer;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.dialog.MessageDialog;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ClothesHangerMachineAddThirdActivity extends BaseActivity<IClothesHangerMachineAddThirdView,
        ClothesHangerMachineAddThirdPresenter<IClothesHangerMachineAddThirdView>> implements IClothesHangerMachineAddThirdView, OnBindClickListener {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.recyclerview)
    RecyclerView searchRecycler;
    @BindView(R.id.iv_research)
    ImageView ivReSearch;

    private ClothesHangerMachineBleWiFiSearchAdapter clothesHangerMachineBleWiFiSearchAdapter;
    private MessageDialog messageDialog;

    private String wifiModelType = "";

    private List<BluetoothDevice> mDevices;

    private Handler handler = new Handler();

    private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_hanger_machine_add_third);
        ButterKnife.bind(this);

        wifiModelType = getIntent().getStringExtra("wifiModelType") + "";

        initView();
        initData();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        startSearchAnimation();
    }

    private void startSearchAnimation() {
        animationDrawable = (AnimationDrawable) ivReSearch.getBackground();
        animationDrawable.start();
    }

    private void stopSearchAnimation(){
        if(animationDrawable != null){
            animationDrawable.stop();
        }
    }

    private void initView() {
        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        handler.postDelayed(timeoutRunnable, 183 * 1000);
        //获取数据
        if (GpsUtil.isOPen(this)){
            mPresenter.searchDevices();
        }else {
            ToastUtil.getInstance().showLong(R.string.check_phone_not_open_gps_please_open);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timeoutRunnable);
    }

    @Override
    protected ClothesHangerMachineAddThirdPresenter<IClothesHangerMachineAddThirdView> createPresent() {
        return new ClothesHangerMachineAddThirdPresenter<>();
    }

    @OnClick({R.id.back,R.id.iv_research})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.iv_research:
                if (GpsUtil.isOPen(this)){
                    mPresenter.searchDevices();
                }else {
                    ToastUtil.getInstance().showLong(R.string.check_phone_not_open_gps_please_open);
                }
                break;
        }
    }

    //当没有搜索到蓝牙设备时，显示对话框。
    private void showNotScanDeviceDialog() {
        stopSearchAnimation();
        didnotdiscoverlock();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(ClothesHangerMachineAddThirdActivity.this,ClothesHangerMachineAddThirdFailedActivity.class);
                intent.putExtra("wifiModelType",wifiModelType);
                startActivity(intent);
                finish();
            }
        }, 2000); //延迟2秒跳转

    }
    public void didnotdiscoverlock(){
        //信息
        messageDialog = new MessageDialog.Builder(this)
                .setMessage(R.string.did_not_discover_hanger)
                .create();
        messageDialog.show();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(messageDialog != null){
                    messageDialog.dismiss();

                }
            }
        }, 2000); //延迟2秒消失
    }

    @Override
    public void onItemClickListener(View view, int position, BluetoothDevice device) {
        //添加设备
        if (NetUtil.isNetworkAvailable()) {
            mPresenter.checkBind(wifiModelType,device);
//            showLoading(getString(R.string.is_checking_bind));
        } else {
            ToastUtil.getInstance().showShort(R.string.noNet);
        }
    }

    @Override
    public void loadBLEWiFiModelDevices(List<BluetoothDevice> devices, List<BluetoothLockBroadcastListBean> broadcastList) {
        if (devices == null) {
            return;
        }
        if (devices.size()==0){
            return;
        }
        mDevices = devices;
        LogUtils.e("shulan -------->" + mDevices.size());
        if (clothesHangerMachineBleWiFiSearchAdapter == null) {
            clothesHangerMachineBleWiFiSearchAdapter = new ClothesHangerMachineBleWiFiSearchAdapter(mDevices);
            clothesHangerMachineBleWiFiSearchAdapter.setBindClickListener(this);
            clothesHangerMachineBleWiFiSearchAdapter.setBluetoothLockBroadcast(broadcastList);

            searchRecycler.setAdapter(clothesHangerMachineBleWiFiSearchAdapter);
        } else {
            clothesHangerMachineBleWiFiSearchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStopScan() {
        if (mDevices == null || mDevices.size()==0){
            LogUtils.e("--kaadas--mDevices=="+mDevices);
            showNotScanDeviceDialog();
            return;
        }
    }

    @Override
    public void onScanDevicesFailed(Throwable throwable) {
        stopSearchAnimation();
        ToastUtil.getInstance().showShort(getString(R.string.scan_fail) + HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void onConnectFailed() {
        hiddenLoading();
        stopSearchAnimation();
//        ToastUtil.getInstance().showLong(R.string.connect_failed_retry);
        Intent intent = new Intent(ClothesHangerMachineAddThirdActivity.this,ClothesHangerMachineAddThirdFailedActivity.class);
        intent.putExtra("wifiModelType",wifiModelType);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAlreadyBind(BluetoothDevice device, String name) {
        hiddenLoading();
        stopSearchAnimation();
        binding(device, false,  String.format("设备已被%1$s绑定",name ));
    }

    @Override
    public void onNoBind(BluetoothDevice device) {
        hiddenLoading();
        stopSearchAnimation();
        binding(device, true, getResources().getString(R.string.device_not_bind_to_bind));
    }

    private void binding(BluetoothDevice device, boolean bindFlag, String content) {
        hiddenLoading();
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint), content, getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {

            }
            @Override
            public void right() {
                mPresenter.bindDeviceInit(device, bindFlag);
                showLoading(getString(R.string.connecting_ble));
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(String toString) {

            }
        });
    }

    @Override
    public void onCheckBindFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.bind_failed) + HttpUtils.httpProtocolErrorCode(this, throwable));
        hiddenLoading();
    }

    @Override
    public void checkBindFailed() {
        hiddenLoading();
        ToastUtil.getInstance().showLong(R.string.network_exception);
    }

    @Override
    public void onConnectBLEWIFISuccess(BluetoothLockBroadcastBean broadcastBean, int bleVersion) {
        hiddenLoading();
        Intent intent = new Intent(ClothesHangerMachineAddThirdActivity.this,ClothesHangerMachineAddThirdSuccessActivity.class);
        intent.putExtra("wifiModelType",wifiModelType);
        intent.putExtra(KeyConstants.BLE_VERSION, bleVersion);
        intent.putExtra(KeyConstants.BLE_DEVICE_SN, broadcastBean.getDeviceSN());
        intent.putExtra(KeyConstants.BLE_MAC, broadcastBean.getDeviceMAC());
        intent.putExtra(KeyConstants.DEVICE_NAME, broadcastBean.getDeviceName());
        startActivity(intent);
        LogUtils.e("shulan broadcastBean.getDeviceSN()-->" + broadcastBean.getDeviceSN());
        finish();
    }

    private Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            onScanFailed();
        }
    };

    private void onScanFailed() {
        stopSearchAnimation();
        Intent intent = new Intent(ClothesHangerMachineAddThirdActivity.this,ClothesHangerMachineAddThirdFailedActivity.class);
        intent.putExtra("wifiModelType",wifiModelType);
        startActivity(intent);
        finish();
    }
}
