package com.kaadas.lock.activity.addDevice.gateway;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.cateye.AddDeviceCatEyeFirstActivity;
import com.kaadas.lock.activity.addDevice.cateye.AddDeviceCatEyeScanFailActivity;
import com.kaadas.lock.activity.addDevice.cateye.AddDeviceCatEyeSecondActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.king.zxing.Intents;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddGatewaySecondActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.scan_gateway)
    LinearLayout scanGateway;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_gateway_add_second);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.scan_gateway})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.scan_gateway:
                Intent scanIntent=new Intent(this,AddGatewayScanActivity.class);
                startActivityForResult(scanIntent, KeyConstants.SCANGATEWAY_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            switch (requestCode){
                case KeyConstants.SCANGATEWAY_REQUEST_CODE:
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    LogUtils.e("扫描结果是   " + result);
                    if (result.contains("SN-GW")&&result.contains("MAC-")&&result.contains(" ")){
                        String[] strs=result.split(" ");
                        String deviceSN=strs[0].replace("SN-","");
                        Intent scanSuccessIntent=new Intent(AddGatewaySecondActivity.this,AddGatewayThirdActivity.class);
                        scanSuccessIntent.putExtra("deviceSN",deviceSN);
                        LogUtils.e("设备SN是   " + deviceSN);
                        startActivity(scanSuccessIntent);
                        finish();
                    }else{
                        ToastUtil.getInstance().showShort(getString(R.string.please_use_gateway_qr_code));
                    }
                    break;
            }

        }

    }
}
