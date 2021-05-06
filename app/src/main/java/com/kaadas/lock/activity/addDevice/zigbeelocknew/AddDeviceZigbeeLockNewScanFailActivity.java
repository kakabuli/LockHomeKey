package com.kaadas.lock.activity.addDevice.zigbeelocknew;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAdd2Activity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.PermissionUtil;
import com.king.zxing.Intents;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceZigbeeLockNewScanFailActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.rescan)
    Button rescan;
    @BindView(R.id.scan_cancel)
    Button scanCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qcode_no_is_kaadas);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.rescan, R.id.scan_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                Intent backIntent=new Intent(this, DeviceAdd2Activity.class);
                startActivity(backIntent);
                finish();
                break;
            case R.id.rescan:
                String[] strings = PermissionUtil.getInstance().checkPermission(new String[]{  Manifest.permission.CAMERA});
                if (strings.length>0){
                    Toast.makeText(this, "请允许拍照或录像权限", Toast.LENGTH_SHORT).show();
                    PermissionUtil.getInstance().requestPermission(new String[]{  Manifest.permission.CAMERA}, this);
                }else {
                    Intent rescanIntent=new Intent(this,QrCodeScanActivity.class);
                    startActivityForResult(rescanIntent,KeyConstants.SCANGATEWAYNEW_REQUEST_CODE);
                }

                break;
            case R.id.scan_cancel:
                Intent scanCancelntent=new Intent(this, DeviceAdd2Activity.class);
                startActivity(scanCancelntent);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DeviceAdd2Activity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            switch (requestCode){
                case KeyConstants.SCANGATEWAYNEW_REQUEST_CODE:
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    LogUtils.e("扫描结果是   " + result);
                    if (result.contains("SN-GW")&&result.contains("MAC-")&&result.contains(" ")){
                        String[] strs=result.split(" ");
                        String deviceSN=strs[0].replace("SN-","");
                        Intent scanSuccessIntent=new Intent(AddDeviceZigbeeLockNewScanFailActivity.this, AddDeviceZigbeeLockNewZeroActivity.class);
                        scanSuccessIntent.putExtra("deviceSN",deviceSN);
                        LogUtils.e("设备SN是   " + deviceSN);
                        startActivity(scanSuccessIntent);
                        finish();
                    }
                    break;
            }

        }
    }
}
