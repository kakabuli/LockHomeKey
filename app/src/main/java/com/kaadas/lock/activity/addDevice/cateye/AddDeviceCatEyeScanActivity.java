package com.kaadas.lock.activity.addDevice.cateye;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.utils.LogUtils;
import com.king.zxing.CaptureActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeScanActivity extends CaptureActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.touch_light_layout)
    LinearLayout touchLightLayout;
    private boolean falshLight=false;
    private Camera.Parameters parameter;
    private Camera camera;
    @Override
    public int getLayoutId() {
        return R.layout.device_scan_qrcode;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        if (!hasFlash()){
            touchLightLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
    }

    @OnClick({R.id.back, R.id.touch_light_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.touch_light_layout:
                openFlashLight(falshLight);
                break;
        }
    }
    // 判断是否有闪光灯功能
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    //打开手电筒
    private void openFlashLight(boolean highlight){
        camera=  getCameraManager().getOpenCamera().getCamera();
        parameter = camera.getParameters();
        if (!highlight) {
            parameter.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameter);
            falshLight = true;
        } else {  // 关灯
            parameter.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameter);
            falshLight = false;
        }


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
