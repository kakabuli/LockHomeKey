package com.kaadas.lock.activity.addDevice.cateye;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceBindGatewayListActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.QrCodeScanActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.PermissionUtil;
import com.kaadas.lock.utils.ftp.GeTui;
import com.king.zxing.Intents;

import org.linphone.mediastream.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeFirstActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.scan_catEye)
    LinearLayout scanCatEye;

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

    @OnClick({R.id.back, R.id.scan_catEye,R.id.phone_add_txt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
               Intent intent=new Intent(this, DeviceBindGatewayListActivity.class);
               startActivity(intent);
                break;
            case R.id.scan_catEye:
//                Intent scanIntent = new Intent(this, AddDeviceCatEyeScanActivity.class);
//                startActivityForResult(scanIntent,KeyConstants.SCANCATEYE_REQUEST_CODE);
                String[] strings = PermissionUtil.getInstance().checkPermission(new String[]{  Manifest.permission.CAMERA});
                if (strings.length>0){
                    Toast.makeText(this, "请允许拍照或录像权限", Toast.LENGTH_SHORT).show();
                    PermissionUtil.getInstance().requestPermission(new String[]{  Manifest.permission.CAMERA}, this);
                }else {
                    Intent scanIntent = new Intent(this, QrCodeScanActivity.class);
                    scanIntent.putExtra(KeyConstants.SCAN_TYPE, 1);
                    startActivityForResult(scanIntent, KeyConstants.SCANCATEYE_REQUEST_CODE);
                }

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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            switch (requestCode){
                case KeyConstants.SCANCATEYE_REQUEST_CODE:
                    String result = data.getStringExtra(Intents.Scan.RESULT);
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
}
