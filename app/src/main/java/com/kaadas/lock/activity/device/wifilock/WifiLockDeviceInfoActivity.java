package com.kaadas.lock.activity.device.wifilock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockDeviceInfoActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.tv_device_model)
    TextView tvDeviceModel;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;
    @BindView(R.id.tv_lock_firmware_version)
    TextView tvLockFirmwareVersion;
    @BindView(R.id.tv_lock_software_version)
    TextView tvLockSoftwareVersion;
    @BindView(R.id.wifi_version)
    TextView wifiVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_device_info);
        ButterKnife.bind(this);
        String wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        WifiLockInfo wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        if (wifiLockInfo != null) {
            String productModel = wifiLockInfo.getProductModel();
            tvDeviceModel.setText(TextUtils.isEmpty(productModel) ? "" : productModel.startsWith("K13")?getString(R.string.lan_bo_ji_ni):productModel);
            tvSerialNumber.setText(TextUtils.isEmpty(wifiLockInfo.getProductSN()) ? "" : wifiLockInfo.getProductSN());
            tvLockFirmwareVersion.setText(TextUtils.isEmpty(wifiLockInfo.getLockFirmwareVersion()) ? "" : wifiLockInfo.getLockFirmwareVersion());
            tvLockSoftwareVersion.setText(TextUtils.isEmpty(wifiLockInfo.getLockSoftwareVersion()) ? "" : wifiLockInfo.getLockSoftwareVersion());
            wifiVersion.setText(TextUtils.isEmpty(wifiLockInfo.getWifiVersion()) ? "" : wifiLockInfo.getWifiVersion());
        }
    }

    @OnClick(R.id.back)
    public void onClick() {
        finish();
    }
}
