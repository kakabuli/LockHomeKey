package com.kaadas.lock.activity.device.wifilock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewFirstActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewThirdActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockOldUserFirstActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockWifiDetailActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.rl_replace_wifi)
    RelativeLayout rlReplaceWifi;
    @BindView(R.id.tv_wifi_name)
    TextView tvWifiName;
    @BindView(R.id.tv_wifi_strength)
    TextView tvWifiStrength;
    @BindView(R.id.tv_rssid)
    TextView tvRssid;
    @BindView(R.id.tv_bssid)
    TextView tvBssid;

    private String wifiSn;
    private WifiLockInfo wifiLockInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_wifi_detail);
        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        initData();
    }

    private void initData() {
        if(wifiLockInfo != null){
            tvWifiName.setText(wifiLockInfo.getWifiName() + "");

        }
    }

    @OnClick({R.id.back, R.id.rl_replace_wifi})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.rl_replace_wifi:
                //老的wifi锁不存在这个字段，为wifi配网1，wifi&ble为2
                LogUtils.e("--kaadas--老的wifi锁不存在这个字段，为wifi配网1，wifi&ble为2--->" + wifiLockInfo.getDistributionNetwork());
                if (TextUtils.isEmpty(String.valueOf(wifiLockInfo.getDistributionNetwork()))) {
                    Intent wifiIntent = new Intent(this, WifiLockOldUserFirstActivity.class);
                    String wifiModelType = "WiFi";
                    wifiIntent.putExtra("wifiModelType", wifiModelType);
                    startActivity(wifiIntent);
                    //startActivity(new Intent(this, WifiLockOldUserFirstActivity.class));
                } else if (wifiLockInfo.getDistributionNetwork() == 0 || wifiLockInfo.getDistributionNetwork() == 1) {
                    Intent wifiIntent = new Intent(this, WifiLockOldUserFirstActivity.class);
                    String wifiModelType = "WiFi";
                    wifiIntent.putExtra("wifiModelType", wifiModelType);
                    startActivity(wifiIntent);
                } else if (wifiLockInfo.getDistributionNetwork() == 2) {
                    Intent wifiIntent = new Intent(this, WifiLockAddNewFirstActivity.class);
                    String wifiModelType = "WiFi&BLE";
                    wifiIntent.putExtra("wifiModelType", wifiModelType);
                    startActivity(wifiIntent);
                } else if(wifiLockInfo.getDistributionNetwork() == 3){
                    showWifiDialog();
                }else {
                    LogUtils.e("--kaadas--wifiLockInfo.getDistributionNetwork()为" + wifiLockInfo.getDistributionNetwork());

                }
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showWifiDialog() {
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(this, "更换WIFI需重新进入添加门锁步骤",
                "取消", "确定", "#999999", "#1F95F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        Intent wifiIntent = new Intent(WifiLockWifiDetailActivity.this, WifiLockAddNewThirdActivity.class);
                        String wifiModelType = "WiFi&VIDEO";
                        wifiIntent.putExtra("wifiModelType", wifiModelType);
                        wifiIntent.putExtra("distribution_again", true);
                        startActivity(wifiIntent);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }
}
