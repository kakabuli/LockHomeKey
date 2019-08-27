package com.kaadas.lock.activity.addDevice.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.ble.BindBleSecondPresenter;
import com.kaadas.lock.mvp.view.IBindBleSecondView;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddBluetoothSecondActivity extends BaseActivity<IBindBleSecondView, BindBleSecondPresenter<IBindBleSecondView>> implements IBindBleSecondView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_next)
    Button buttonNext;
    @BindView(R.id.help)
    ImageView help;
    private boolean isBind;
    private String password1;
    private int version;
    private static final int NEXT_ACTIVITY_CODE = 333;
    private String sn;
    private String mac;
    private String deviceName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        password1 = intent.getStringExtra(KeyConstants.PASSWORD1);
        isBind = intent.getBooleanExtra(KeyConstants.IS_BIND,true);
        version = intent.getIntExtra(KeyConstants.BLE_VERSION, 0);
        sn = intent.getStringExtra(KeyConstants.BLE_DEVICE_SN );

        mac = intent.getStringExtra(KeyConstants.BLE_MAC);
        deviceName = intent.getStringExtra( KeyConstants.DEVICE_NAME);



        LogUtils.e("第二步   " + password1 +"  设备SN    " + sn);
        setContentView(R.layout.device_bluetooth_second);
        mPresenter.listenConnectState();
        ButterKnife.bind(this);
    }

    @Override
    protected BindBleSecondPresenter<IBindBleSecondView> createPresent() {
        return new BindBleSecondPresenter<>();
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
                nextIntent.putExtra(KeyConstants.BLE_DEVICE_SN, sn);
                nextIntent.putExtra(KeyConstants.BLE_MAC, mac);
                nextIntent.putExtra(KeyConstants.DEVICE_NAME, deviceName);
                startActivityForResult(nextIntent,NEXT_ACTIVITY_CODE);
                break;
            case R.id.help:
                Intent intent=new Intent(this, DeviceAddHelpActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==NEXT_ACTIVITY_CODE && resultCode == RESULT_OK ){
            if (data != null) {
                isBind = data.getBooleanExtra(KeyConstants.IS_BIND,true);
            }
        }
    }

    @Override
    public void onDeviceStateChange(boolean isConnected) {  //设备连接状态改变   连接成功时提示正在鉴权，连接失败时直接提示用户
        if (!isConnected)  {
            //Context context, String title, String content, String query, ClickListener clickListener
            AlertDialogUtil.getInstance().noEditSingleCanNotDismissButtonDialog(AddBluetoothSecondActivity.this, getString(R.string.hint), getString(R.string.ble_disconnected_please_retry), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                @Override
                public void left() {
                    finish();
                    startActivity(new Intent(AddBluetoothSecondActivity.this,AddBluetoothSearchActivity.class));
                }

                @Override
                public void right() {
                    finish();
                    startActivity(new Intent(AddBluetoothSecondActivity.this,AddBluetoothSearchActivity.class));
                }
            });

        }
    }

}
