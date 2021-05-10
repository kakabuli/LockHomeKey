package com.kaadas.lock.activity.addDevice.cateye;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.blankj.utilcode.util.ToastUtils;
import com.king.zxing.CameraScan;
import com.king.zxing.CaptureActivity;
import com.king.zxing.DefaultCameraScan;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeScanActivity extends CaptureActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.touch_light_layout)
    LinearLayout touchLightLayout;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    private boolean falshLight=false;
    private CameraScan mCameraScan;

    @Override
    public int getLayoutId() {
        return R.layout.device_scan_qrcode;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        ButterKnife.bind(this);
        initView();
        checkVersion();
        //动态设置状态栏高度
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(titleBar.getLayoutParams());
        lp.setMargins(0, getStatusBarHeight(), 0, 0);
        titleBar.setLayoutParams(lp);
        PreviewView previewView = findViewById(R.id.previewView);
        mCameraScan = new DefaultCameraScan(this, previewView);
    }

    private void checkVersion() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i=checkSelfPermission(Manifest.permission.CAMERA);
            if (i==-1){
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                    //禁止
                    ToastUtils.showShort(getString(R.string.ban_camera_permission));
                    finish();
                    return;
                }else{
                    //询问
                    ToastUtils.showShort(getString(R.string.inquire_camera_permission));
                    finish();
                    return;
                }
            }
        }

    }

    private void initView() {
        if (!hasFlash()){
            touchLightLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mCameraScan != null) {
            mCameraScan.setOnScanResultCallback(this)
                    .setVibrate(true)
                    .startCamera();
        }

    }

    @Override
    protected void onStop() {
        if(mCameraScan != null) {
            mCameraScan.stopCamera();
        }
        super.onStop();
    }


    @Override
    public void onDestroy() {
        if(mCameraScan != null) {
            mCameraScan.enableTorch(false);
            mCameraScan.release();
        }
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
        if(mCameraScan != null){
            if(!highlight){
                mCameraScan.enableTorch(true);
                falshLight = true;
            }else {
                mCameraScan.enableTorch(false);
                falshLight = false;
            }
        }
    }

    //获取状态栏高度
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
