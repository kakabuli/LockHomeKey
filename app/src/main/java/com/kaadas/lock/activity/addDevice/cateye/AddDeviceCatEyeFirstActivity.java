package com.kaadas.lock.activity.addDevice.cateye;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAdd2Activity;
import com.kaadas.lock.activity.addDevice.DeviceAddCateyeHelpActivity;
import com.kaadas.lock.activity.addDevice.DeviceBindGatewayListActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.QrCodeScanActivity;
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

public class AddDeviceCatEyeFirstActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.scan_catEye)
    LinearLayout scanCatEye;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.device_cateye_add_txt)
    TextView device_cateye_add_txt;

    private String pwd;
    private String ssid;
    private String gwId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_cateye_add_first);
        ssid = getIntent().getStringExtra(KeyConstants.GW_WIFI_SSID);
        pwd = getIntent().getStringExtra(KeyConstants.GW_WIFI_PWD);
        gwId = getIntent().getStringExtra(KeyConstants.GW_SN);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.back, R.id.scan_catEye,R.id.phone_add_txt, R.id.help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
               Intent intent=new Intent(this, DeviceBindGatewayListActivity.class);
               startActivity(intent);
                break;
            case R.id.scan_catEye:
                checkPermissions();
                break;
            case  R.id.phone_add_txt:
                Intent phoneIntent = new Intent(this, CatEyeAddPhoneActivity.class);
                if(TextUtils.isEmpty(pwd) || TextUtils.isEmpty(ssid)){
                    Toast.makeText(this, getString(R.string.mimi_no_account), Toast.LENGTH_SHORT).show();
                    return;
                }
                phoneIntent.putExtra(KeyConstants.GW_WIFI_SSID,ssid);
                phoneIntent.putExtra(KeyConstants.GW_WIFI_PWD,pwd);
                phoneIntent.putExtra(KeyConstants.GW_SN, gwId);
                startActivity(phoneIntent);
                break;
            case R.id.help:
                Intent helpIntent=new Intent(this, DeviceAddCateyeHelpActivity.class);
                startActivity(helpIntent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            switch (requestCode){
                case KeyConstants.SCANCATEYE_REQUEST_CODE:
                    String result = data.getStringExtra(Constants.SCAN_QR_CODE_RESULT);
                    LogUtils.e("扫描结果是   " + result);
                    if (result.contains("SN-CH")&&result.contains("MAC-")&&result.contains(" ")){
                        String[] strs=result.split(" ");
                        String deviceSN=strs[0].replace("SN-","");
                        String deviceMac=strs[1].replace("MAC-","");
                        Intent successIntent=new Intent(AddDeviceCatEyeFirstActivity.this,AddDeviceCatEyeSecondActivity.class);
                        successIntent.putExtra(KeyConstants.GW_WIFI_SSID, ssid);
                        successIntent.putExtra(KeyConstants.GW_WIFI_PWD, pwd);
                        successIntent.putExtra(KeyConstants.DEVICE_SN,deviceSN);
                        successIntent.putExtra(KeyConstants.DEVICE_MAC,deviceMac);
                        successIntent.putExtra(KeyConstants.GW_SN,gwId);
                        startActivity(successIntent);
                      //  finish();

                    }else{
                        Intent scanSuccessIntent=new Intent(AddDeviceCatEyeFirstActivity.this,AddDeviceCatEyeScanFailActivity.class);
                        startActivity(scanSuccessIntent);
                        //ToastUtil.getInstance().showShort(R.string.please_scan_cateye_qrcode);
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
                                Intent scanIntent = new Intent(AddDeviceCatEyeFirstActivity.this, QrCodeScanActivity.class);
                                scanIntent.putExtra(KeyConstants.SCAN_TYPE, 1);
                                startActivityForResult(scanIntent, KeyConstants.SCANCATEYE_REQUEST_CODE);
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
