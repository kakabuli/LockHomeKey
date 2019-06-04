package com.kaadas.lock.activity.addDevice.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddBluetoothSecondActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_next)
    Button buttonNext;
    @BindView(R.id.help)
    ImageView help;
    private boolean isBind;
    private String password1;
    private int version;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        password1 = intent.getStringExtra(KeyConstants.PASSWORD1);
        LogUtils.e("第二步   " + password1);
        isBind = intent.getBooleanExtra(KeyConstants.IS_BIND,true);
        version = intent.getIntExtra(KeyConstants.BLE_VERSION, 0);


        setContentView(R.layout.device_bluetooth_second);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.button_next,R.id.help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.button_next:
                Intent nextIntent = new Intent(this, AddBluetoothThirdActivity.class);
                nextIntent.putExtra(KeyConstants.PASSWORD1, password1);
                nextIntent.putExtra(KeyConstants.IS_BIND, isBind);
                nextIntent.putExtra(KeyConstants.BLE_VERSION, version);
                startActivity(nextIntent);
                break;
            case R.id.help:
                Intent intent=new Intent(this, DeviceAddHelpActivity.class);
                startActivity(intent);

                break;
        }
    }


}
