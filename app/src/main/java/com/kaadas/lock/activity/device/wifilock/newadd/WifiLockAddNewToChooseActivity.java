package com.kaadas.lock.activity.device.wifilock.newadd;


import android.content.Intent;

import android.os.Bundle;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAdd2Activity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.AddDeviceZigbeeLockNewScanFailActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.AddDeviceZigbeeLockNewZeroActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.QrCodeScanActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;

import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.WiFiLockChooseToAddPresenter;
import com.kaadas.lock.mvp.view.deviceaddview.WiFiLockChooseToAddView;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.dialog.InfoDialog;
import com.kaadas.lock.utils.dialog.MessageDialog;
import com.king.zxing.Intents;

import java.util.logging.LogRecord;

import butterknife.ButterKnife;

/**
 * Created by hushucong
 * on 2020/6/28
 */
public class WifiLockAddNewToChooseActivity extends BaseActivity<WiFiLockChooseToAddView, WiFiLockChooseToAddPresenter<WiFiLockChooseToAddView>>
        implements View.OnClickListener,WiFiLockChooseToAddView {

    LinearLayout wifi_lock_choose_to_scan;
    EditText wifi_lock_choose_to_input;
    ImageView back,help;
    TextView add;

    private MessageDialog messageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_choose_and_scan);
        ButterKnife.bind(this);

        wifi_lock_choose_to_scan = findViewById(R.id.wifi_lock_choose_to_scan);
        wifi_lock_choose_to_input = findViewById(R.id.wifi_lock_choose_to_input);

        add = findViewById(R.id.add);
        help = findViewById(R.id.help);
        back = findViewById(R.id.back);

        back.setOnClickListener(this);
        wifi_lock_choose_to_scan.setOnClickListener(this);
        wifi_lock_choose_to_input.setOnClickListener(this);
        help.setOnClickListener(this);
        add.setOnClickListener(this);

        initData();
        initView();

    }
    @Override
    protected WiFiLockChooseToAddPresenter<WiFiLockChooseToAddView> createPresent() {
        return new WiFiLockChooseToAddPresenter<>();
    }

    private void initData() {

    }

    private void initView() {
        
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                Intent helpIntent = new Intent(this, WifiLockHelpActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.wifi_lock_choose_to_scan:
                Intent scanIntent = new Intent(this, QrCodeScanActivity.class);
                scanIntent.putExtra(KeyConstants.SCAN_TYPE, 1);
                startActivityForResult(scanIntent, KeyConstants.SCANGATEWAYNEW_REQUEST_CODE);
                break;
            case R.id.wifi_lock_choose_to_input:
                break;
            case R.id.add:
                String name = wifi_lock_choose_to_input.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    ToastUtil.getInstance().showShort(R.string.not_empty);
                    return;
                }
                if (!StringUtil.nicknameJudge(name)) {
                    ToastUtil.getInstance().showShort(R.string.nickname_verify_error);
                    return;
                }
                mPresenter.searchLockProduct(name);
                break;

        }
    }

    @Override
    public void searchLockProductSuccessForWiFi(String pairMode) {
        Intent wifiIntent = new Intent(this, WifiLockAddNewFirstActivity.class);
        String wifiModelType = pairMode;
        wifiIntent.putExtra("wifiModelType", wifiModelType);
        startActivity(wifiIntent);
    }

    @Override
    public void searchLockProductSuccessForWiFiAndBLE(String pairMode) {
        Intent wifiIntent = new Intent(this, WifiLockAddNewFirstActivity.class);
        String wifiModelType = pairMode;
        wifiIntent.putExtra("wifiModelType", wifiModelType);
        startActivity(wifiIntent);
    }

    @Override
    public void searchLockProductThrowable() {

        //信息
        messageDialog = new MessageDialog.Builder(this)
                .setMessage(R.string.input_right_lock_product_or_scan)
                .create();
        messageDialog.show();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(messageDialog != null){
                    messageDialog.dismiss();

                }
            }
        }, 3000); //延迟2秒消失
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case KeyConstants.SCANGATEWAYNEW_REQUEST_CODE:
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    LogUtils.e("扫描结果是   " + result);

                    if ( (result.contains("_WiFi_"))){  //4-30新的配网流程
                        Intent wifiIntent = new Intent(this, WifiLockAddNewFirstActivity.class);
                        String wifiModelType = "WiFi";
                        wifiIntent.putExtra("wifiModelType", wifiModelType);
                        startActivity(wifiIntent);
                    } else if ( (result.contains("http://qr01.cn/EYopdB"))){  //已生产的错误的X1二维码
                        Intent wifiIntent = new Intent(this, WifiLockAddNewFirstActivity.class);
                        String wifiModelType = "WiFi";
                        wifiIntent.putExtra("wifiModelType", wifiModelType);
                        startActivity(wifiIntent);
                    }else if ( (result.contains("_WiFi&BLE_"))){  //5-11WiFi&BLE，蓝牙Wi-Fi模组配网
                        Intent wifiIntent = new Intent(this, WifiLockAddNewFirstActivity.class);
                        String wifiModelType = "WiFi&BLE";
                        wifiIntent.putExtra("wifiModelType", wifiModelType);
                        startActivity(wifiIntent);
                    }
                    else {
                        Intent scanSuccessIntent = new Intent(WifiLockAddNewToChooseActivity.this, AddDeviceZigbeeLockNewScanFailActivity.class);
                        startActivity(scanSuccessIntent);
                        finish();
                    }
                    break;
            }

        }

    }
}
