package com.kaadas.lock.activity.device.gatewaylock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockInformationPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockInformationView;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetGatewayLockInfoBean;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GatewayDeviceInformationActivity extends BaseActivity<GatewayLockInformationView, GatewayLockInformationPresenter<GatewayLockInformationView>> implements GatewayLockInformationView{


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;
    @BindView(R.id.tv_device_model)
    TextView tvDeviceModel;
    @BindView(R.id.tv_lock_firmware_version)
    TextView tvLockFirmwareVersion;
    @BindView(R.id.tv_software_version)
    TextView tvSoftwareVersion;
    @BindView(R.id.rl_software_version)
    RelativeLayout rlSoftwareVersion;
    @BindView(R.id.tv_hardware_version)
    TextView tvHardwareVersion;
    @BindView(R.id.rl_hardware_version)
    RelativeLayout rlHardwareVersion;
    @BindView(R.id.tv_module_mark)
    TextView tvModuleMark;
    @BindView(R.id.rl_module_mark)
    RelativeLayout rlModuleMark;
    private String gatewayId;
    private String deviceId;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_device_information);
        ButterKnife.bind(this);
        initView();
        initData();
        
    }

    private void initData() {
        Intent intent=getIntent();
        gatewayId=intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId=intent.getStringExtra(KeyConstants.DEVICE_ID);
        if (gatewayId!=null&&deviceId!=null){
            if (loadingDialog!=null){
                loadingDialog.show(getString(R.string.get_lock_info_later_on));
                mPresenter.getGatewayLockInfo(gatewayId,deviceId);
            }

        }
    }

    @Override
    protected GatewayLockInformationPresenter<GatewayLockInformationView> createPresent() {
        return new GatewayLockInformationPresenter<>();
    }

    private void initView() {
        loadingDialog=LoadingDialog.getInstance(this);
        tvContent.setText(R.string.device_info);
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void getLockInfoSuccess(GetGatewayLockInfoBean.ReturnDataBean returnDataBean) {
        loadingDialog.dismiss();
        if (returnDataBean!=null){
            tvSerialNumber.setText(returnDataBean.getModel());//序列号
            tvDeviceModel.setText(returnDataBean.getLinkquality());//链路信号
            tvLockFirmwareVersion.setText(returnDataBean.getFirmware());//固件版本号
            tvSoftwareVersion.setText(returnDataBean.getSwversion());//软件版本号
            tvHardwareVersion.setText(returnDataBean.getHwversion());//硬件版本号
            tvModuleMark.setText(returnDataBean.getMacaddr());
        }
    }

    @Override
    public void getLcokInfoFail() {
        loadingDialog.dismiss();
        ToastUtil.getInstance().showShort(R.string.get_lock_info_fail);
    }

    @Override
    public void getLockInfoThrowable(Throwable throwable) {
        loadingDialog.dismiss();
        LogUtils.e("获取锁信息出现异常    "+throwable.getMessage());
    }
}
