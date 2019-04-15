package com.kaadas.lock.activity.addDevice.gateway;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddGatewayScanActivity extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView back;
    private CaptureFragment captureFragment;
    private Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_scan_qrcode);
        context=this;
        captureFragment = new CaptureFragment();
        CodeUtils.setFragmentArgs(captureFragment,R.layout.my_scan_qrcode);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.scan_layout, captureFragment).commit();
        ButterKnife.bind(this);
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            LogUtils.e("result",result);
            if (result.contains("SN-")&&result.contains("MAC-")&&result.contains(" ")){
                String[] strs=result.split(" ");
                String deviceSN=strs[0].replace("SN-","");
                Intent scanSuccessIntent=new Intent(context,AddGatewayThirdActivity.class);
                scanSuccessIntent.putExtra("deviceSN",deviceSN);
                startActivity(scanSuccessIntent);
                finish();
            }else{
                Intent scanSuccessIntent=new Intent(context,AddGatewaySecondActivity.class);
                startActivity(scanSuccessIntent);
                ToastUtil.getInstance().showShort(getString(R.string.please_use_gateway_qr_code));
            }

        }


        @Override
        public void onAnalyzeFailed() {
            ToastUtil.getInstance().showShort(getString(R.string.scan_qr_failed));
        }
    };
}
