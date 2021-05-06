package com.kaadas.lock.activity.addDevice.gateway;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAdd2Activity;
import com.kaadas.lock.activity.addDevice.DeviceAddActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddGatewayFailActivity extends BaseAddToApplicationActivity {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_again)
    Button buttonAgain;
    @BindView(R.id.button_unbind)
    Button buttonUnbind;

    private String code;
    private String msg;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_gateway_add_fail);
        ButterKnife.bind(this);
//        initView();
    }

    private void initView() {
        Intent intent=getIntent();
        code=intent.getStringExtra("code");
        msg=intent.getStringExtra("msg");
        if (!TextUtils.isEmpty(code)){
           if ("813".equals(code)){
               ToastUtil.getInstance().showShort(R.string.gateway_already_bind);
           } else if ("812".equals(code)){
               ToastUtil.getInstance().showShort(R.string.already_notify_admin_sure);
           }else{
               ToastUtil.getInstance().showShort(msg);
           }

            LogUtils.e("网关绑定失败"+msg);
        }
    }


    @OnClick({R.id.back, R.id.button_again, R.id.button_unbind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                Intent scanIntent=new Intent(this,AddGatewaySecondActivity.class);
                startActivity(scanIntent);
                finish();
                break;
            case R.id.button_again:
                //重新连接
                Intent reConnection=new Intent(this,AddGatewayFirstActivity.class);
                startActivity(reConnection);
                break;
            case R.id.button_unbind:
                Intent addDeviceIntent=new Intent(this, DeviceAdd2Activity.class);
                startActivity(addDeviceIntent);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DeviceAdd2Activity.class));
    }
}
