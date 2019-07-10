package com.kaadas.lock.activity.addDevice.gateway;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.ToastUtil;
import com.king.zxing.CaptureActivity;
/*import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;*/

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddGatewayScanActivity extends CaptureActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.touch_light_layout)
    LinearLayout touchLightLayout;
    private Camera.Parameters parameter;
    private Camera camera;
    private boolean falshLight=false;
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
        checkVersion();
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

    private void checkVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i = checkSelfPermission(Manifest.permission.CAMERA);
            if (i == -1) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    ToastUtil.getInstance().showShort(getString(R.string.ban_camera_permission));
                    finish();
                    return;
                }
            }
        }
        //版本为22 5.1
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!isCameraCanUse()) {
                ToastUtil.getInstance().showShort(getString(R.string.ban_camera_permission));
                finish();
                return;
            }

        }
    }
        //Android6.0以下的摄像头权限处理：
        public static boolean isCameraCanUse() {
            boolean canUse = true;
            Camera mCamera = null;
            try {
                mCamera = Camera.open();
                // setParameters 是针对魅族MX5 做的。MX5 通过Camera.open() 拿到的Camera
                Camera.Parameters mParameters = mCamera.getParameters();
                mCamera.setParameters(mParameters);
            } catch (Exception e) {
                canUse = false;
            }
            if (mCamera != null) {
                mCamera.release();
            }
            return canUse;
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);

    }

    private void initView() {
        if (!hasFlash()){
            touchLightLayout.setVisibility(View.GONE);
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


    /*
    *//**
     * 二维码解析回调函数
     *//*
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            LogUtils.e("result",result);
            if (result.contains("SN-GW")&&result.contains("MAC-")&&result.contains(" ")){
                String[] strs=result.split(" ");
                String deviceSN=strs[0].replace("SN-","");
                Intent scanSuccessIntent=new Intent(context,AddGatewayThirdActivity.class);
                scanSuccessIntent.putExtra("deviceSN",deviceSN);
                LogUtils.e("设备SN是   " + deviceSN);
                startActivity(scanSuccessIntent);
                finish();
            }else{
                Intent scanSuccessIntent=new Intent(context,AddGatewaySecondActivity.class);
                startActivity(scanSuccessIntent);
                ToastUtil.getInstance().showShort(getString(R.string.please_use_gateway_qr_code));
            }

        }


        @Override
        public void onAnalyzeFailed() {
            ToastUtil.getInstance().showShort(getString(R.string.scan_qr_failed));
        }
    };*/
}
