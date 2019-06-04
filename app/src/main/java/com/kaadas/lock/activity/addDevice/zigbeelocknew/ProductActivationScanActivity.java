package com.kaadas.lock.activity.addDevice.zigbeelocknew;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.my.BarCodeActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.ftp.GeTui;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;



import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductActivationScanActivity extends BaseAddToApplicationActivity {
    @BindView(R.id.back)
    ImageView back;
    private CaptureFragment captureFragment;
    private Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_activation_scan_qrcode);
        context=this;
        captureFragment = new CaptureFragment();
        CodeUtils.setFragmentArgs(captureFragment,R.layout.bar_code_scan_qrcode);
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
            Log.e(GeTui.VideoLog,"result:"+result);

            if(result.contains(" ")){
                result=result.replace(" ","%20");
            }
            String bar_url="http://s.kaadas.com:8989/extFun/regWeb.asp?uiFrm=2&id=" + result+"&telnum=";
//                    +"&telnum=18988780718&mail=8618988780718&nickname=8618988780718";

            //获取手机号码
            String phone = (String) SPUtils.get(SPUtils.PHONEN, "");
            if (!TextUtils.isEmpty(phone)) {
                bar_url= bar_url+phone+"&mail=";
            }
            String userName = (String) SPUtils.get(SPUtils.USERNAME, "");
            if (!TextUtils.isEmpty(userName)) {
                bar_url=bar_url+userName+"&nickname="+userName;
            }
            Intent intent=new Intent(ProductActivationScanActivity.this, BarCodeActivity.class);
            intent.putExtra(KeyConstants.BAR_CODE,bar_url);
            startActivity(intent);
            finish();
       //     String bar_url="http://s.kaadas.com:8989/extFun/regWeb.asp?uiFrm=2&id=SN-GW01183810798%20MAC-90:F2:78:70:0F:33&telnum=18988780718&mail=8618988780718&nickname=8618988780718";
        }


        @Override
        public void onAnalyzeFailed() {
            ToastUtil.getInstance().showShort(getString(R.string.bar_code_scan_qr_failed));
        }
    };
}
