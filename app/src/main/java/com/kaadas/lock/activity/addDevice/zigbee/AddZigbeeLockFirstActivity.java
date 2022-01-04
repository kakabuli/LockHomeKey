package com.kaadas.lock.activity.addDevice.zigbee;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddGatewayHelpActivity;
import com.kaadas.lock.activity.addDevice.DeviceAddHelpActivity;
import com.kaadas.lock.activity.addDevice.bluetooth.AddBluetoothSearchActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.StatusBarUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddZigbeeLockFirstActivity extends BaseAddToApplicationActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.button_next)
    Button buttonNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_bluetooth_first);
        //StatusBarUtils.setWindowStatusBarColor(this, R.color.current_time_bg);
        ButterKnife.bind(this);


    }

    @OnClick({R.id.back, R.id.help, R.id.button_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                Intent intent=new Intent(this, DeviceAddGatewayHelpActivity.class);
                startActivity(intent);
                break;
            case R.id.button_next:
                checkPermissions();
                break;
        }
    }
    private void checkPermissions() {
        try {
            XXPermissions.with(this)
                    .permission(Permission.ACCESS_FINE_LOCATION)
                    .permission(Permission.ACCESS_COARSE_LOCATION)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all) {
                                LogUtils.e("获取权限成功");
                                startActivity();

                            } else {
                                LogUtils.e("获取部分权限成功，但部分权限未正常授予");
                            }
                        }
                        @Override
                        public void onDenied(List<String> permissions, boolean never) {

                            if (never) {
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                LogUtils.e("被永久拒绝授权，请手动授予权限");
                            } else {
                                LogUtils.e("获取权限失败");
                            }
                        }
                    });

        }catch (Exception e){
            LogUtils.e(e.getMessage());
        }
    }
    private void startActivity(){

        Intent searchIntent=new Intent(this, AddZigbeeLockSecondActivity.class);
        startActivity(searchIntent);

    }
}
