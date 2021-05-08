package com.kaadas.lock.activity.addDevice.zigbeelocknew;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.zxing.Result;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.king.zxing.CameraScan;
import com.king.zxing.DefaultCameraScan;

import androidx.camera.view.PreviewView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QrCodeScanActivity extends BaseAddToApplicationActivity implements CameraScan.OnScanResultCallback {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.touch_light_layout)
    LinearLayout touchLightLayout;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    private CameraScan mCameraScan;
    private boolean isOpenLight = false;
    int scan = 0;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_scan_qrcode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        MyApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);
        checkVersion();
        scan = getIntent().getIntExtra(KeyConstants.SCAN_TYPE, 0);
        //动态设置状态栏高度
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(titleBar.getLayoutParams());
        lp.setMargins(0, getStatusBarHeight(), 0, 0);
        titleBar.setLayoutParams(lp);
        PreviewView previewView = findViewById(R.id.previewView);
        mCameraScan = new DefaultCameraScan(this, previewView);

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
    protected void onDestroy() {
        if(mCameraScan != null) {
            mCameraScan.enableTorch(false);
            mCameraScan.release();
        }
        super.onDestroy();
    }

    private void checkVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i = checkSelfPermission(Manifest.permission.CAMERA);
            LogUtils.e("权限是允许还是开启还是禁止" + i);
            if (i == -1) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    //禁止该权限
                    ToastUtil.getInstance().showShort(getString(R.string.ban_camera_permission));
                    finish();
                    return;
                } else {
                    //询问该权限
                    ToastUtil.getInstance().showShort(getString(R.string.inquire_camera_permission));
                    finish();
                    return;
                }
            }
        }
    }

    @OnClick({R.id.back, R.id.touch_light_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.touch_light_layout:
//                mZBarView.openFlashlight(); // 打开闪光灯
                if (!isOpenLight){
                    isOpenLight = true;
                    if(mCameraScan != null) {
                        mCameraScan.enableTorch(true);
                    }
                }else {
                    isOpenLight = false;
                    if(mCameraScan != null) {
                        mCameraScan.enableTorch(false);
                    }

                }
                break;
        }
    }

    private String result = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String result2 = data.getStringExtra(KeyConstants.URL_RESULT);

                Intent intent = new Intent();
                intent.putExtra(Constants.SCAN_QR_CODE_RESULT, result2);
                setResult(RESULT_OK, intent);
                finish();
            }else {
                Intent intent = new Intent();
                intent.putExtra(Constants.SCAN_QR_CODE_RESULT, result);
                setResult(RESULT_OK, intent);
                finish();
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

    @Override
    public boolean onScanResultCallback(Result result) {
        this.result = result.getText();
        //首页过来的
        Intent intent;

        intent = new Intent();
        intent.putExtra(Constants.SCAN_QR_CODE_RESULT, result.getText());
        setResult(RESULT_OK, intent);
        finish();
        return true;
    }
}
