package com.kaadas.lock.activity.addDevice.cateye;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.king.zxing.CaptureActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeScanActivity extends CaptureActivity {
    @BindView(R.id.back)
    ImageView back;

    @Override
    public int getLayoutId() {
        return R.layout.device_scan_qrcode;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);
    }



    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
    }

    /*  *//**
     * 二维码解析回调函数
     *//*
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            LogUtils.e("扫描结果是   " + result);
            if (result.contains("SN-CH")&&result.contains("MAC-")&&result.contains(" ")){
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
                Intent scanSuccessIntent=new Intent(AddDeviceCatEyeScanActivity.this,AddDeviceCatEyeScanFailActivity.class);
                startActivity(scanSuccessIntent);
                //ToastUtil.getInstance().showShort(R.string.please_scan_cateye_qrcode);
            }


        }


        @Override
        public void onAnalyzeFailed() {
            ToastUtil.getInstance().showShort(getString(R.string.scan_qr_failed));
        }
    };
*/





}
