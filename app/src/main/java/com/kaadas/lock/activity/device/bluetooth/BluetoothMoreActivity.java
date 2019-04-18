package com.kaadas.lock.activity.device.bluetooth;

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
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/15
 */
public class BluetoothMoreActivity extends AppCompatActivity implements View.OnClickListener {
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
    @BindView(R.id.rl_safe_mode)
    RelativeLayout rlSafeMode;
    @BindView(R.id.iv_am)
    ImageView ivAm;
    @BindView(R.id.rl_am)
    RelativeLayout rlAm;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_more);
        ButterKnife.bind(this);
        initClick();
        initData();

    }

    private void initData() {
        //todo 获取到设备名字时,key都加上设备名字
        messageFreeStatus = (boolean) SPUtils.get(KeyConstants.MESSAGE_FREE_STATUS, false);
        if (messageFreeStatus) {
            ivMessageFree.setImageResource(R.mipmap.iv_open);
        } else {
            ivMessageFree.setImageResource(R.mipmap.iv_close);
        }

        amAutoLockStatus = (boolean) SPUtils.get(KeyConstants.AM_AUTO_LOCK_STATUS, false);
        if (amAutoLockStatus) {
            ivAm.setImageResource(R.mipmap.iv_open);
        } else {
            ivAm.setImageResource(R.mipmap.iv_close);
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
        tvContent.setText(getString(R.string.settting));
        rlDeviceName.setOnClickListener(this);
        rlMessageFree.setOnClickListener(this);
        rlSafeMode.setOnClickListener(this);
        rlAm.setOnClickListener(this);
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
                editText.setText("");
                editText.setSelection("".length());
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
                /*        if (deviceNickname!=null){
                            if (deviceNickname.equals(name)){
                                ToastUtil.getInstance().showShort(getString(R.string.device_nick_name_no_update));
                                alertDialog.dismiss();
                                return;
                            }
                        }*/
                        tvDeviceName.setText(name);
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
            case R.id.rl_safe_mode:
                intent = new Intent(this, BluetoothSafeModeActivity.class);
                startActivity(intent);

                break;
            case R.id.rl_am:
                if (amAutoLockStatus) {
                    //打开状态 现在关闭
                    ivAm.setImageResource(R.mipmap.iv_close);
                    SPUtils.put(KeyConstants.AM_AUTO_LOCK_STATUS, false);
                } else {
                    //关闭状态 现在打开
                    ivAm.setImageResource(R.mipmap.iv_open);
                    SPUtils.put(KeyConstants.AM_AUTO_LOCK_STATUS, true);
                }
                amAutoLockStatus = !amAutoLockStatus;
                break;
            case R.id.rl_door_lock_language_switch:
                intent = new Intent(this, BluetoothLockLanguageSettingActivity.class);
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
                break;
            case R.id.rl_check_firmware_update:
                break;
            case R.id.btn_delete:
                AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.device_delete_dialog_head), getString(R.string.device_delete_dialog_content),getString(R.string.cancel),getString(R.string.query) , new AlertDialogUtil.ClickListener() {
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
}
