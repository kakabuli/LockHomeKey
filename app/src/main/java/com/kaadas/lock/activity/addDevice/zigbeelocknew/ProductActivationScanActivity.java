package com.kaadas.lock.activity.addDevice.zigbeelocknew;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.king.zxing.CaptureActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ProductActivationScanActivity extends CaptureActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.touch_light_layout)
    LinearLayout touchLightLayout;

    private boolean falshLight = false;

    private Camera.Parameters parameter;
    private Camera camera;

    @Override
    public int getLayoutId() {
        return R.layout.product_activation_scan_qrcode;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);
        initView();
        checkVersion();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
    }

    private void checkVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i=checkSelfPermission(Manifest.permission.CAMERA);
            if (i==-1){
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                    ToastUtil.getInstance().showShort(getString(R.string.ban_camera_permission));
                    finish();
                    return;
                }
            }
        }
        //版本为22 5.1
        if (Build.VERSION.SDK_INT==Build.VERSION_CODES.LOLLIPOP_MR1){
            if (!isCameraCanUse()){
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


    private void initView() {
        if (!hasFlash()) {
            touchLightLayout.setVisibility(View.GONE);
        }
    }


    // 判断是否有闪光灯功能
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }


    //打开手电筒
    private void openFlashLight(boolean highlight) {
        LogUtils.e("开启闪光灯");
        camera = getCameraManager().getOpenCamera().getCamera();
        parameter = camera.getParameters();
        if (!highlight) {
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameter);
            falshLight = true;
        } else {  // 关灯
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameter);
            falshLight = false;
        }


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

    /*
     *//**
     * 二维码解析回调函数
     *//*
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Log.e(GeTui.VideoLog,"result:"+result);

            if(result.contains(" ")){
                result=result.replace(" ","%20");
            }
            String bar_url="http://s.kaadas.com:8989/extFun/regWeb.asp?uiFrm=2&id=" + result+"&telnum=";
//                    +"&telnum=18988780718&mail=8618988780718&nickname=8618988780718";

            //获取手机号码
            String phone = (String) SPUtils.get(SPUtils.PHONEN, "");
            if (!TextUtils.isEmpty(phone)) {
                bar_url= bar_url+phone+"&mail=";
            }
            String userName = (String) SPUtils.get(SPUtils.USERNAME, "");
            if (!TextUtils.isEmpty(userName)) {
                bar_url=bar_url+userName+"&nickname="+userName;
            }
            Intent intent=new Intent(ProductActivationScanActivity.this, BarCodeActivity.class);
            intent.putExtra(KeyConstants.BAR_CODE,bar_url);
            startActivity(intent);
            finish();
       //     String bar_url="http://s.kaadas.com:8989/extFun/regWeb.asp?uiFrm=2&id=SN-GW01183810798%20MAC-90:F2:78:70:0F:33&telnum=18988780718&mail=8618988780718&nickname=8618988780718";
        }


        @Override
        public void onAnalyzeFailed() {
            ToastUtil.getInstance().showShort(getString(R.string.bar_code_scan_qr_failed));
        }
    };*/
}
