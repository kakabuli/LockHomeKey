package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.utils.ToastUtil;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeScanActivity extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView back;
    private CaptureFragment captureFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_scan_qrcode);
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
            /*String snCode = "";
            String[] strs = result.split(" ");
            snCode = strs[0].replace("SN-", "");
            Intent intent = new Intent();
            intent.putExtra("qrCodeData", snCode);
            setResult(RESULT_OK, intent);*/
            Intent successIntent=new Intent(AddDeviceCatEyeScanActivity.this,AddDeviceCatEyeSecondActivity.class);
            startActivity(successIntent);
            finish();
        }


        @Override
        public void onAnalyzeFailed() {
            ToastUtil.getInstance().showShort(getString(R.string.scan_qr_failed));
        }
    };


}
