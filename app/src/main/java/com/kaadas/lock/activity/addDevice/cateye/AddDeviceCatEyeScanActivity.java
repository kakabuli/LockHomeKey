package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.gateway.AddGatewaySecondActivity;
import com.kaadas.lock.activity.addDevice.gateway.AddGatewayThirdActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeScanActivity extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView back;
    private CaptureFragment captureFragment;
    private String ssid;
    private String pwd;
    private String gwId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_scan_qrcode);

        ssid = getIntent().getStringExtra(KeyConstants.GW_WIFI_SSID);
        pwd = getIntent().getStringExtra(KeyConstants.GW_WIFI_PWD);
        gwId = getIntent().getStringExtra(KeyConstants.GW_SN);

        captureFragment = new CaptureFragment();
        CodeUtils.setFragmentArgs(captureFragment,R.layout.my_scan_qrcode);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.scan_layout, captureFragment).commit();



        ButterKnife.bind(this);
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            LogUtils.e("扫描结果是   " + result);
            if (result.contains("SN-")&&result.contains("MAC-")&&result.contains(" ")){
                String[] strs=result.split(" ");
                String deviceSN=strs[0].replace("SN-","");
                String deviceMac=strs[1].replace("MAC-","");
                Intent successIntent=new Intent(AddDeviceCatEyeScanActivity.this,AddDeviceCatEyeSecondActivity.class);
                successIntent.putExtra(KeyConstants.GW_WIFI_SSID, ssid);
                successIntent.putExtra(KeyConstants.GW_WIFI_PWD, pwd);
                successIntent.putExtra(KeyConstants.DEVICE_SN,deviceSN);
                successIntent.putExtra(KeyConstants.DEVICE_MAC,deviceMac);
                successIntent.putExtra(KeyConstants.GW_SN,gwId);
                startActivity(successIntent);
                finish();

            }else{
                Intent scanSuccessIntent=new Intent(AddDeviceCatEyeScanActivity.this,AddGatewaySecondActivity.class);
                startActivity(scanSuccessIntent);
                ToastUtil.getInstance().showShort(R.string.please_scan_cateye_qrcode);
            }


        }


        @Override
        public void onAnalyzeFailed() {
            ToastUtil.getInstance().showShort(getString(R.string.scan_qr_failed));
        }
    };






}
