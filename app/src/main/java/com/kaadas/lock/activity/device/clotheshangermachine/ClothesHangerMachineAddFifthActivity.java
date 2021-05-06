package com.kaadas.lock.activity.device.clotheshangermachine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.mvp.presenter.clotheshangermachinepresenter.ClothesHangerMachineAddFifthPresenter;
import com.kaadas.lock.mvp.presenter.clotheshangermachinepresenter.ClothesHangerMachineAddTourthPresenter;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineAddFifthView;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineAddTourthView;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.dialog.MessageDialog;
import com.kaadas.lock.widget.DropEditText;
import com.kaadas.lock.widget.WifiCircleProgress;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ClothesHangerMachineAddFifthActivity extends BaseActivity<IClothesHangerMachineAddFifthView,
        ClothesHangerMachineAddFifthPresenter<IClothesHangerMachineAddFifthView>> implements IClothesHangerMachineAddFifthView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.circle_progress_bar2)
    WifiCircleProgress circleProgressBar2;

    private String wifiModelType = "";
    private boolean passwordHide = true;
    private String sPassword = "";
    private String sSsid = "";
    private int bleVersion ;
    private String deviceSN = "";
    private String deviceMAC = "";
    private String deviceName = "";
    private int times = 0;

    private Disposable progressDisposable;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_hanger_machine_add_fifth);
        ButterKnife.bind(this);

        wifiModelType = getIntent().getStringExtra("wifiModelType") + "";
        times = getIntent().getIntExtra(KeyConstants.CLOTHES_HANGER_PASSWORD_TIMES,0);
        sSsid = getIntent().getStringExtra(KeyConstants.ClOTHES_HANGER_MACHINE_WIFI_SSID);
        sPassword = getIntent().getStringExtra(KeyConstants.ClOTHES_HANGER_MACHINE_WIFI_PASSWORD);
        bleVersion = getIntent().getIntExtra(KeyConstants.BLE_VERSION,4);
        deviceSN = getIntent().getStringExtra(KeyConstants.BLE_DEVICE_SN);
        deviceMAC = getIntent().getStringExtra(KeyConstants.DEVICE_NAME);
        deviceName = getIntent().getStringExtra(KeyConstants.BLE_DEVICE_SN);

        initView();
        initData();
    }

    private void initView() {

    }

    private void initData() {
        handler.postDelayed(timeoutRunnable, 183 * 1000);
        progressDisposable = Observable
                .interval(0, 1, TimeUnit.SECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        circleProgressBar2.setValue(aLong % 61);
                    }
                });

        mPresenter.checkSSIDAndPasswrod(sSsid,sPassword);

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
        if (progressDisposable != null) {
            progressDisposable.dispose();
        }
        handler.removeCallbacks(timeoutRunnable);
    }

    @Override
    protected ClothesHangerMachineAddFifthPresenter<IClothesHangerMachineAddFifthView> createPresent() {
        return new ClothesHangerMachineAddFifthPresenter<>();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent data = new Intent(ClothesHangerMachineAddFifthActivity.this,ClothesHangerMachineAddTourthActivity.class);
        data.putExtra(KeyConstants.CLOTHES_HANGER_PASSWORD_TIMES,times + 1);
        data.putExtra(KeyConstants.BLE_VERSION, bleVersion);
        data.putExtra(KeyConstants.BLE_DEVICE_SN, deviceSN);
        data.putExtra(KeyConstants.BLE_MAC, deviceMAC);
        data.putExtra(KeyConstants.DEVICE_NAME, deviceName);
        setResult(RESULT_OK,data);
        finish();
    }

    @OnClick({R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.back:
                Intent data = new Intent(ClothesHangerMachineAddFifthActivity.this,ClothesHangerMachineAddTourthActivity.class);
                data.putExtra(KeyConstants.CLOTHES_HANGER_PASSWORD_TIMES,times + 1);
                data.putExtra(KeyConstants.BLE_VERSION, bleVersion);
                data.putExtra(KeyConstants.BLE_DEVICE_SN, deviceSN);
                data.putExtra(KeyConstants.BLE_MAC, deviceMAC);
                data.putExtra(KeyConstants.DEVICE_NAME, deviceName);
                setResult(RESULT_OK,data);
                finish();
                break;

        }
    }

    private Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            onScanFailed();
        }
    };

    private void onScanFailed() {
        Intent intent = new Intent(ClothesHangerMachineAddFifthActivity.this,ClothesHangerMachineAddTourthFailedActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDeviceStateChange(boolean isConnected) {
        if(!ClothesHangerMachineAddFifthActivity.this.isFinishing()){
            if (!isConnected) {
                AlertDialogUtil.getInstance().noEditSingleCanNotDismissButtonDialog(
                        ClothesHangerMachineAddFifthActivity.this, "", getString(R.string.ble_disconnected_please_retry), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {
                            }

                            @Override
                            public void right() {
                                Intent intent = new Intent(ClothesHangerMachineAddFifthActivity.this, ClothesHangerMachineAddThirdFailedActivity.class);
                                intent.putExtra("wifiModelType",wifiModelType);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            @Override
                            public void afterTextChanged(String toString) {
                            }
                        });
            }
        }
    }

    @Override
    public void onMatchingSuccess() {
        mPresenter.bindDevice(deviceSN);
    }

    @Override
    public void onMatchingFailed() {
        Intent data = new Intent(ClothesHangerMachineAddFifthActivity.this,ClothesHangerMachineAddTourthActivity.class);
        data.putExtra(KeyConstants.CLOTHES_HANGER_PASSWORD_TIMES,times + 1);
        data.putExtra(KeyConstants.BLE_VERSION, bleVersion);
        data.putExtra(KeyConstants.BLE_DEVICE_SN, deviceSN);
        data.putExtra(KeyConstants.BLE_MAC, deviceMAC);
        data.putExtra(KeyConstants.DEVICE_NAME, deviceName);
        setResult(RESULT_OK,data);
        finish();
    }

    @Override
    public void onSendSuccess(int sendType) {
        handler.removeCallbacks(timeoutRunnable);
        handler.postDelayed(timeoutRunnable, 183 * 1000);
    }

    @Override
    public void onBindDeviceSuccess() {
        LogUtils.e("shulan ----------onBindDeviceSuccess");
        Intent intent = new Intent(ClothesHangerMachineAddFifthActivity.this,ClothesHangerMachineAddSuccessActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBindDeviceFailed() {
        ToastUtil.getInstance().showLong(R.string.network_exception);
    }

    @Override
    public void onBindDeviceFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.bind_failed) + HttpUtils.httpProtocolErrorCode(this, throwable));
    }
}
