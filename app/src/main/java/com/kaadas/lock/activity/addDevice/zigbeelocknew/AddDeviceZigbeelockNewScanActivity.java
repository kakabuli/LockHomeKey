package com.kaadas.lock.activity.addDevice.zigbeelocknew;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.gateway.AddGatewaySecondActivity;
import com.kaadas.lock.activity.addDevice.gateway.AddGatewayThirdActivity;
import com.kaadas.lock.activity.addDevice.zigbee.AddZigbeeLockFailActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.king.zxing.CaptureActivity;
/*
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
*/

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceZigbeelockNewScanActivity extends CaptureActivity {
    @BindView(R.id.back)
    ImageView back;

    @Override
    public int getLayoutId() {
        return R.layout.device_scan_qrcode;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
    }

    /*   *//**
     * 二维码解析回调函数
     *//*
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            LogUtils.e("result",result);
            if (result.contains("SN-GW")&&result.contains("MAC-")&&result.contains(" ")){
                String[] strs=result.split(" ");
                String deviceSN=strs[0].replace("SN-","");
                Intent scanSuccessIntent=new Intent(context,AddDeviceZigbeeLockNewZeroActivity.class);
                scanSuccessIntent.putExtra("deviceSN",deviceSN);
                LogUtils.e("设备SN是   " + deviceSN);
                startActivity(scanSuccessIntent);
                finish();
            }else{
                Intent scanSuccessIntent=new Intent(context, AddDeviceZigbeeLockNewScanFailActivity.class);
                startActivity(scanSuccessIntent);
            }

        }


        @Override
        public void onAnalyzeFailed() {
            ToastUtil.getInstance().showShort(getString(R.string.scan_qr_failed));
        }
    };*/
}
