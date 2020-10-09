package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.espressif.iot.esptouch.util.TouchNetUtil;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockInputAdminPasswordActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiLockVideoFourthActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddNewThirdActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.head)
    TextView head;
    @BindView(R.id.notice)
    TextView notice;
    @BindView(R.id.iv_anim)
    ImageView ivAnim;
    @BindView(R.id.already_modify)
    TextView alreadyModify;
    @BindView(R.id.not_modify)
    TextView notModify;
    @BindView(R.id.not_modify_1)
    TextView notModify1;

    private String wifiModelType;

    private Boolean distributionAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_third);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        wifiModelType = intent.getStringExtra("wifiModelType");
        LogUtils.e("shulan WifiLockAddNewThirdActivity--wifiModelType--" + wifiModelType);
        //通过设置android:background时，得到AnimationDrawable 用如下方法
        final AnimationDrawable animationDrawable = (AnimationDrawable) ivAnim.getBackground();
        animationDrawable.start();
        distributionAgain = intent.getBooleanExtra("distribution_again",false);
        if(wifiModelType.contains("VIDEO")){
            if(distributionAgain){
                head.setText(R.string.wifi_lock_add_new_third_notice1_2);
            }else{
                head.setText(R.string.wifi_lock_add_new_third_notice1_1);
            }
            notice.setText(R.string.dkjsfkjhad_2);
            ivAnim.setVisibility(View.GONE);
            notModify1.setVisibility(View.VISIBLE);
            notModify.setVisibility(View.GONE);
        }else {
            head.setText(R.string.wifi_lock_add_new_third_notice1);
            ivAnim.setVisibility(View.VISIBLE);
            notice.setText(R.string.dkjsfkjhad);
            notModify1.setVisibility(View.GONE);
            notModify.setVisibility(View.VISIBLE);

        }
    }


    @OnClick({R.id.back, R.id.help, R.id.already_modify, R.id.not_modify})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                startActivity(new Intent(this,WifiLockHelpActivity.class));
                break;
            case R.id.already_modify:
                if(wifiModelType.contains("VIDEO")){
                    saveWifiName();
                    Intent wifiVideoIntent = new Intent(this, WifiLockVideoFourthActivity.class);
                    wifiVideoIntent.putExtra("wifiModelType", wifiModelType);
                    wifiVideoIntent.getBooleanExtra("distribution_again",distributionAgain);
                    startActivity(wifiVideoIntent);
                }else{
                    //startActivity(new Intent(this,WifiLockAddNewfourthActivity.class));
                    Intent wifiIntent = new Intent(this, WifiLockAddNewfourthActivity.class);
                    wifiIntent.putExtra("wifiModelType", wifiModelType);
                    startActivity(wifiIntent);
                }

                break;
            case R.id.not_modify:
            case R.id.not_modify_1:
//                startActivity(new Intent(this,WifiLockAddNewThird2Activity.class));
                Intent UnModifyWifiIntent = new Intent(this, WifiLockAddNewThird2Activity.class);
                UnModifyWifiIntent.putExtra("wifiModelType", wifiModelType);
                startActivity(UnModifyWifiIntent);
                break;
        }
    }

    private void saveWifiName() {
        WifiManager wifiMgr = (WifiManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String ssid = info != null ? info.getSSID() : null;
        LogUtils.e("--Kaadas--获取到的ssid：" + ssid);
        if (TextUtils.isEmpty(ssid)) {
            SPUtils.put(KeyConstants.WIFI_LOCK_CONNECT_NAME, "");
            return;
        }
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        if (!ssid.equals("kaadas_AP") && !"<unknown ssid>".equals(ssid)) {
            SPUtils.put(KeyConstants.WIFI_LOCK_CONNECT_NAME, ssid);
            byte[] ssidOriginalData = TouchNetUtil.getOriginalSsidBytes(info);
            LogUtils.e("获取到的   byte数据是    " + Rsa.bytesToHexString(ssidOriginalData));
            SPUtils.put(KeyConstants.WIFI_LOCK_CONNECT_ORIGINAL_DATA, Rsa.bytesToHexString(ssidOriginalData));
        }
        else if(ssid.equals("kaadas_AP")){

        }
        else {
            SPUtils.put(KeyConstants.WIFI_LOCK_CONNECT_NAME, "");
        }

    }
}
