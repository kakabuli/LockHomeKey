package com.kaadas.lock.activity.device.gatewaylock.more;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.gatewaylock.GatewayDeviceInformationActivity;
import com.kaadas.lock.bean.DeviceDetailBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockMorePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockMoreView;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by David on 2019/4/15
 */
public class GatewayMoreActivity extends BaseActivity<GatewayLockMoreView, GatewayLockMorePresenter<GatewayLockMoreView>> implements View.OnClickListener,GatewayLockMoreView {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rl_device_name)
    RelativeLayout rlDeviceName;
    @BindView(R.id.iv_message_free)
    ImageView ivMessageFree;
    @BindView(R.id.rl_message_free)
    RelativeLayout rlMessageFree;
    @BindView(R.id.rl_door_lock_language_switch)
    RelativeLayout rlDoorLockLanguageSwitch;
    @BindView(R.id.iv_silent_mode)
    ImageView ivSilentMode;
    @BindView(R.id.rl_silent_mode)
    RelativeLayout rlSilentMode;
    @BindView(R.id.rl_device_information)
    RelativeLayout rlDeviceInformation;
    @BindView(R.id.rl_check_firmware_update)
    RelativeLayout rlCheckFirmwareUpdate;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    boolean messageFreeStatus;
    boolean amAutoLockStatus;
    boolean silentModeStatus;
    String name;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;

    private DeviceDetailBean deviceDetailBean;
    private String gatewayId;
    private String deviceId;
    private GwLockInfo gwLockInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_more);
        ButterKnife.bind(this);
        initView();
        initData();
        initClick();
    }

    private void initView() {
        tvContent.setText(getString(R.string.settting));
    }

    @Override
    protected GatewayLockMorePresenter<GatewayLockMoreView> createPresent() {
        return new GatewayLockMorePresenter<>();
    }


    private void initData() {
        Intent intent=getIntent();
        deviceDetailBean= (DeviceDetailBean) intent.getSerializableExtra(KeyConstants.DEVICE_DETAIL_BEAN);
        if (deviceDetailBean!=null){
            tvDeviceName.setText(deviceDetailBean.getDeviceName());
            gwLockInfo= (GwLockInfo) deviceDetailBean.getShowCurentBean();
            gatewayId=gwLockInfo.getGwID();
            deviceId=gwLockInfo.getServerInfo().getDeviceId();
        }


        //todo 获取到设备名字时,key都加上设备名字
        messageFreeStatus = (boolean) SPUtils.get(KeyConstants.MESSAGE_FREE_STATUS, true);
        if (messageFreeStatus) {
            ivMessageFree.setImageResource(R.mipmap.iv_open);
        } else {
            ivMessageFree.setImageResource(R.mipmap.iv_close);
        }
        silentModeStatus = (boolean) SPUtils.get(KeyConstants.SILENT_MODE_STATUS, false);
        if (silentModeStatus) {
            ivSilentMode.setImageResource(R.mipmap.iv_open);
        } else {
            ivSilentMode.setImageResource(R.mipmap.iv_close);
        }
    }

    private void initClick() {
        ivBack.setOnClickListener(this);
        rlDeviceName.setOnClickListener(this);
        rlMessageFree.setOnClickListener(this);
        rlDoorLockLanguageSwitch.setOnClickListener(this);
        rlSilentMode.setOnClickListener(this);
        rlDeviceInformation.setOnClickListener(this);
        rlCheckFirmwareUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_device_name:
                //设备名字
                View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
                TextView tvTitle = mView.findViewById(R.id.tv_title);
                EditText editText = mView.findViewById(R.id.et_name);
                TextView tv_cancel = mView.findViewById(R.id.tv_left);
                TextView tv_query = mView.findViewById(R.id.tv_right);
                AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
                tvTitle.setText(getString(R.string.input_device_name));
                //获取到设备名称设置
                String deviceNickname=tvDeviceName.getText().toString().trim();
                editText.setText(deviceNickname);
                editText.setSelection(deviceNickname.length());
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                tv_query.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        name = editText.getText().toString().trim();
                        if (!StringUtil.nicknameJudge(name)) {
                            ToastUtil.getInstance().showShort(R.string.nickname_verify_error);
                            return;
                        }
                        //todo 判断名称是否修改
                        if (deviceNickname!=null){
                            if (deviceNickname.equals(name)){
                                ToastUtil.getInstance().showShort(getString(R.string.device_nick_name_no_update));
                                alertDialog.dismiss();
                                return;
                            }
                        }
                        if (gatewayId!=null&&deviceId!=null){
                            mPresenter.updateZigbeeLockName(gatewayId,deviceId,name);
                        }
                        alertDialog.dismiss();
                    }
                });
                break;
            case R.id.rl_message_free:
                if (messageFreeStatus) {
                    //打开状态 现在关闭
                    ivMessageFree.setImageResource(R.mipmap.iv_close);
                    SPUtils.put(KeyConstants.MESSAGE_FREE_STATUS, false);
                } else {
                    //关闭状态 现在打开
                    ivMessageFree.setImageResource(R.mipmap.iv_open);
                    SPUtils.put(KeyConstants.MESSAGE_FREE_STATUS, true);
                }
                messageFreeStatus = !messageFreeStatus;
                break;
            case R.id.rl_door_lock_language_switch:
                intent = new Intent(this, GatewayLockLanguageSettingActivity.class);
                intent.putExtra(KeyConstants.DEVICE_ID,deviceId);
                intent.putExtra(KeyConstants.GATEWAY_ID,gatewayId);
                startActivity(intent);
                break;
            case R.id.rl_silent_mode:
                if (silentModeStatus) {
                    //打开状态 现在关闭
                    ivSilentMode.setImageResource(R.mipmap.iv_close);
                    SPUtils.put(KeyConstants.SILENT_MODE_STATUS, false);
                } else {
                    //关闭状态 现在打开
                    ivSilentMode.setImageResource(R.mipmap.iv_open);
                    SPUtils.put(KeyConstants.SILENT_MODE_STATUS, true);
                }
                silentModeStatus = !silentModeStatus;
                break;
            case R.id.rl_device_information:
                intent = new Intent(this, GatewayDeviceInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_check_firmware_update:
                break;
            case R.id.btn_delete:
                AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.device_delete_dialog_head), getString(R.string.device_delete_dialog_content), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {

                    }
                });
                break;
        }
    }

    @Override
    public void updateDevNickNameSuccess(String name) {
        tvDeviceName.setText(name);
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra(KeyConstants.NAME, name);
        //设置返回数据
        GatewayMoreActivity.this.setResult(RESULT_OK, intent);
        ToastUtil.getInstance().showShort(getString(R.string.update_nick_name));
    }

    @Override
    public void updateDevNickNameFail() {
        ToastUtil.getInstance().showShort(getString(R.string.update_nickname_fail));
    }

    @Override
    public void updateDevNickNameThrowable(Throwable throwable) {
        LogUtils.e("设置昵称失败"+throwable.getMessage());
    }
}
