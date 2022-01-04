package com.kaadas.lock.activity.addDevice.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddHelpActivity;
import com.kaadas.lock.fragment.home.BleLockFragment;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.LogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddBluetoothFirstActivity extends BaseAddToApplicationActivity {
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
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.help, R.id.button_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                Intent intent=new Intent(this, DeviceAddHelpActivity.class);
                startActivity(intent);
                break;
            case R.id.button_next:

                //������������Ƿ���
                if (MyApplication.getInstance().getBleService().isBleIsEnable()){
                    //���Ȩ��
                    checkPermissions();
                }else if (!GpsUtil.isOPen(MyApplication.getInstance())){
                    permissionTipsDialog(this,getString(R.string.kaadas_common_service_location));
                }
                else {
                    permissionTipsDialog(this,getString(R.string.kaadas_common_permission_ble));
                    //mPresenter.enableBle();
                }
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
                                LogUtils.e("��ȡȨ�޳ɹ�");
                                startActivity();

                            } else {
                                LogUtils.e("��ȡ����Ȩ�޳ɹ���������Ȩ��δ��������");
                            }
                        }
                        @Override
                        public void onDenied(List<String> permissions, boolean never) {

                            if (never) {
                                // ����Ǳ����þܾ�����ת��Ӧ��Ȩ��ϵͳ����ҳ��
                                LogUtils.e("�����þܾ���Ȩ�����ֶ�����Ȩ��");
                            } else {
                                LogUtils.e("��ȡȨ��ʧ��");
                            }
                        }
                    });

        }catch (Exception e){
            LogUtils.e(e.getMessage());
        }
    }
    private void startActivity(){

        Intent searchIntent=new Intent(this,AddBluetoothSearchActivity.class);
        startActivity(searchIntent);
    }
    public void permissionTipsDialog(Activity activity, String str){
        AlertDialogUtil.getInstance().permissionTipsDialog(activity, (activity.getString(R.string.kaadas_permission_title,str))
                , (activity.getString(R.string.kaadas_permission_content,str,activity.getString(R.string.device_conn))),
                (activity.getString(R.string.kaadas_permission_content_tisp,str)), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }
}
