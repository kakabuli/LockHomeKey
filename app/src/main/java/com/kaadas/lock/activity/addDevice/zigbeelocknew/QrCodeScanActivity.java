package com.kaadas.lock.activity.addDevice.zigbeelocknew;

import android.Manifest;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.ScanHttpDialogActivity;
import com.kaadas.lock.activity.addDevice.DeviceAdd2Activity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.king.zxing.Intents;
/*
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
*/

import org.apache.commons.net.bsd.RLoginClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;

public class QrCodeScanActivity extends BaseAddToApplicationActivity implements QRCodeView.Delegate {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.touch_light_layout)
    LinearLayout touchLightLayout;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    private ZBarView mZBarView;
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
        mZBarView = findViewById(R.id.zbarview);
        mZBarView.setDelegate(this);
        //动态设置状态栏高度
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(titleBar.getLayoutParams());
        lp.setMargins(0, getStatusBarHeight(), 0, 0);
        titleBar.setLayoutParams(lp);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZBarView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        mZBarView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    protected void onStop() {
        mZBarView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZBarView.onDestroy(); // 销毁二维码扫描控件
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
                    mZBarView.openFlashlight(); // 打开闪光灯
                }else {
                    isOpenLight = false;
                    mZBarView.closeFlashlight(); // 打开闪光灯
                }
                break;
        }
    }

    private String result = "";
    @Override
    public void onScanQRCodeSuccess(String result) {
        this.result = result;
        //首页过来的
        Intent intent;
//        if (scan == 1 && TextUtils.isEmpty(result)) {
//
//            Intent scanSuccessIntent = new Intent(this, AddDeviceZigbeeLockNewScanFailActivity.class);
//            startActivity(scanSuccessIntent);
//            finish();
//
//        }

        intent = new Intent();
        intent.putExtra(Intents.Scan.RESULT, result);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
//        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
//        String tipText = mZBarView.getScanBoxView().getTipText();
//        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
//        if (isDark) {
//            if (!tipText.contains(ambientBrightnessTip)) {
//                mZBarView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
//            }
//        } else {
//            if (tipText.contains(ambientBrightnessTip)) {
//                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
//                mZBarView.getScanBoxView().setTipText(tipText);
//            }
//        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        LogUtils.e("打开相机出错");
        finish();
        Toast.makeText(this, getString(R.string.open_camera_failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String result2 = data.getStringExtra(KeyConstants.URL_RESULT);

                Intent intent = new Intent();
                intent.putExtra(Intents.Scan.RESULT, result2);
                setResult(RESULT_OK, intent);
                finish();
            }else {
                Intent intent = new Intent();
                intent.putExtra(Intents.Scan.RESULT, result);
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
}
