package com.kaadas.lock.activity.addDevice.zigbeelocknew;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.gateway.AddGatewaySuccessActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.AddDeviceCatEyeSuitPresenter;
import com.kaadas.lock.mvp.view.deviceaddview.AddDeviceCatEyeSuitView;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceZigbeeLockNewFirstActivity extends BaseActivity<AddDeviceCatEyeSuitView, AddDeviceCatEyeSuitPresenter<AddDeviceCatEyeSuitView>> implements AddDeviceCatEyeSuitView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_layout)
    RelativeLayout headLayout;
    @BindView(R.id.cancel_bind)
    Button cancelBind;
    private String gatewayId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_cateye_suit_first);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        Intent intent=getIntent();
        gatewayId=intent.getStringExtra(KeyConstants.GATEWAY_ID);
        boolean isBindMeme=intent.getBooleanExtra(KeyConstants.IS_BIND_MEME,false);
        if (isBindMeme){
            Intent successIntent = new Intent(this, AddDeviceZigbeeLockNewSuccessActivity.class);
            startActivity(successIntent);
            finish();
        }else {
          if (!TextUtils.isEmpty(gatewayId)){
              mPresenter.bindMimi(gatewayId,gatewayId);
              Intent successIntent = new Intent(this, AddDeviceZigbeeLockNewSuccessActivity.class);
              startActivity(successIntent);
              finish();

          }else{
                //绑定套装失败
              Intent successIntent = new Intent(this, AddDeviceZigbeeLockNewFailActivity.class);
              startActivity(successIntent);
              finish();
          }
        }
    }

    @Override
    protected AddDeviceCatEyeSuitPresenter<AddDeviceCatEyeSuitView> createPresent() {
        return new AddDeviceCatEyeSuitPresenter<>();
    }


    @OnClick({R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }


    @Override
    public void bindMimiSuccess() {
        LogUtils.e("绑定咪咪网成功");
    }

    @Override
    public void bindMimiFail(String code, String msg) {
        LogUtils.e("绑定咪咪网失败");
    }

    @Override
    public void bindMimiThrowable(Throwable throwable) {
        LogUtils.e("绑定咪咪网异常");
    }


}
