package com.kaadas.lock.activity.device.clotheshangermachine;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.zxing.Result;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAdd2Activity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.utils.clothesHangerMachineUtil.ClothesHangerMachineUtil;
import com.kaadas.lock.utils.dialog.MessageDialog;
import com.king.zxing.CameraScan;
import com.king.zxing.DefaultCameraScan;

import androidx.camera.view.PreviewView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ClothesHangerMachineQrCodeScanActivity extends BaseAddToApplicationActivity implements CameraScan.OnScanResultCallback {
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

    private MessageDialog messageDialog;

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
        PreviewView previewView = findViewById(R.id.previewView);
        mCameraScan = new DefaultCameraScan(this, previewView);
        //动态设置状态栏高度
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(titleBar.getLayoutParams());
        lp.setMargins(0, getStatusBarHeight(), 0, 0);
        titleBar.setLayoutParams(lp);
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
                    ToastUtils.showShort(getString(R.string.ban_camera_permission));
                    finish();
                    return;
                } else {
                    //询问该权限
                    ToastUtils.showShort(getString(R.string.inquire_camera_permission));
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

    private void showErrorDialog() {
        //信息
        messageDialog = new MessageDialog.Builder(this)
                .setMessage(R.string.input_right_clothes_machine_or_scan)
                .create();
        messageDialog.show();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(messageDialog != null){
                    messageDialog.dismiss();
                    finish();
                }
            }
        }, 3000); //延迟3秒消失
    }

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

    private void showClothesMachineDialog(String content) {
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(this, content, "否", "是",
                "#9A9A9A", "#1F96F7", new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {
                Intent deviceAdd = new Intent(ClothesHangerMachineQrCodeScanActivity.this, DeviceAdd2Activity.class);
                startActivity(deviceAdd);
            }

            @Override
            public void right() {
                //首页过来的
                Intent intent = new Intent();
                intent.putExtra(Constants.SCAN_QR_CODE_RESULT, result);
                setResult(RESULT_OK, intent);
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

    @Override
    public boolean onScanResultCallback(Result result) {
        this.result = result.getText();
        LogUtils.d("shulan -->" + result);
        String[] str = result.getText().split("_");
        if(str.length > 0){
            if(str.length >= 4){
                if(ClothesHangerMachineUtil.pairMode(str[1]).equals(str[2])){
                    showClothesMachineDialog(getString(R.string.activity_clothes_hanger_machine_qrcode_scan,str[1]));
                    return true;
                }
            }
        }
        showErrorDialog();
        return true;
    }
}
