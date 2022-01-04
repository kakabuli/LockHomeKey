package com.kaadas.lock.activity.addDevice.zigbeelocknew;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAdd2Activity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.PermissionUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceZigbeeLockNewFailActivity extends BaseAddToApplicationActivity {


    @BindView(R.id.button_again)
    Button buttonAgain;
    @BindView(R.id.hand_bind)
    Button handBind;
    @BindView(R.id.back)
    ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_add_zigbeenewlock_fail);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button_again, R.id.hand_bind,R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                startActivity(new Intent(this, DeviceAdd2Activity.class));
                break;
            case R.id.button_again:
                checkPermissions();
                //再来一次
                break;
            case R.id.hand_bind:
                //退出
                Intent outIntent = new Intent(this, DeviceAdd2Activity.class);
                startActivity(outIntent);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            startActivity(new Intent(this, DeviceAdd2Activity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case KeyConstants.SCANGATEWAYNEW_REQUEST_CODE:
                    String result = data.getStringExtra(Constants.SCAN_QR_CODE_RESULT);
                    LogUtils.e("扫描结果是   " + result);
                    if (result.contains("SN-GW") && result.contains("MAC-") && result.contains(" ")) {
                        String[] strs = result.split(" ");
                        String deviceSN = strs[0].replace("SN-", "");
                        Intent scanSuccessIntent = new Intent(AddDeviceZigbeeLockNewFailActivity.this, AddDeviceZigbeeLockNewZeroActivity.class);
                        scanSuccessIntent.putExtra("deviceSN", deviceSN);
                        LogUtils.e("设备SN是   " + deviceSN);
                        startActivity(scanSuccessIntent);
                        finish();
                    } else {
                        Intent scanSuccessIntent = new Intent(AddDeviceZigbeeLockNewFailActivity.this, AddDeviceZigbeeLockNewScanFailActivity.class);
                        startActivity(scanSuccessIntent);
                        finish();
                    }
                    break;
            }

        }
    }

    private void checkPermissions() {
        try {
            XXPermissions.with(this)
                    .permission(Permission.CAMERA)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all) {
                                Intent intent = new Intent(AddDeviceZigbeeLockNewFailActivity.this, QrCodeScanActivity.class);
                                startActivityForResult(intent, KeyConstants.SCANGATEWAYNEW_REQUEST_CODE);
                            }
                        }

                        @Override
                        public void onDenied(List<String> permissions, boolean never) {

                        }
                    });


        }catch (Exception e){
            Log.d("", "checkPermissions: "  + e.getMessage());
        }
    }


}
