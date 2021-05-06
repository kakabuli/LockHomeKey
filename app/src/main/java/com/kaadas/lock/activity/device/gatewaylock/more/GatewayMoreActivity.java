package com.kaadas.lock.activity.device.gatewaylock.more;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.device.gatewaylock.GatewayDeviceInformationActivity;
import com.kaadas.lock.adapter.ForecastAdapter;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockMorePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockMoreView;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.http.result.SwitchStatusResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.EditTextWatcher;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.ftp.GeTui;
import com.kaadas.lock.utils.greenDao.db.DaoSession;
import com.kaadas.lock.utils.greenDao.db.GatewayLockServiceInfoDao;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by David on 2019/4/15
 */
public class GatewayMoreActivity extends BaseActivity<GatewayLockMoreView, GatewayLockMorePresenter<GatewayLockMoreView>>
        implements View.OnClickListener, GatewayLockMoreView {
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
    @BindView(R.id.btn_delete)
    Button btnDelete;
    boolean getAutoLockSuccess = false;
    boolean silentModeStatus;
    String name;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.rl_am)
    RelativeLayout rlAm;
    @BindView(R.id.iv_am)
    ImageView ivAm;

    private HomeShowBean showBean;
    private String gatewayId;
    private String deviceId;
    private GwLockInfo gwLockInfo;

    private LoadingDialog loadingDialog;

    //获取音量时避免用户去点开音量开关
    private boolean flagSoundVolume = false;
    //获取AM时避免用户去点开
    private boolean flagAM = false;

    private int autoRelock = 0;
    private AlertDialog deleteDialog;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_more);
        ButterKnife.bind(this);
        context = this;
        initView();
        initData();
        initClick();
    }

    private void initView() {
        tvContent.setText(getString(R.string.settting));
        loadingDialog = LoadingDialog.getInstance(this);
    }

    @Override
    protected GatewayLockMorePresenter<GatewayLockMoreView> createPresent() {
        return new GatewayLockMorePresenter<>();
    }


    private void initData() {
        Intent intent = getIntent();
        showBean = (HomeShowBean) intent.getSerializableExtra(KeyConstants.GATEWAY_LOCK_INFO);
        if (showBean != null) {
            gwLockInfo = (GwLockInfo) showBean.getObject();
            if (!TextUtils.isEmpty(gwLockInfo.getServerInfo().getNickName())) {
                tvDeviceName.setText(gwLockInfo.getServerInfo().getNickName());
            } else {
                tvDeviceName.setText(gwLockInfo.getServerInfo().getDeviceId());
            }
            gatewayId = gwLockInfo.getGwID();
            deviceId = gwLockInfo.getServerInfo().getDeviceId();
            if (gwLockInfo.getServerInfo().getPushSwitch() == 2) {
                ivMessageFree.setImageResource(R.mipmap.iv_open);
            } else {
                ivMessageFree.setImageResource(R.mipmap.iv_close);
            }
        }

        if (gatewayId != null && deviceId != null) {
            if (loadingDialog != null) {
                loadingDialog.show(getString(R.string.be_beging_syc_lockinfo));
                //先获取音量，在获取AM
                mPresenter.getSoundVolume(gatewayId, deviceId);
            }
        }


    }

    private void initClick() {
        ivBack.setOnClickListener(this);
        rlDeviceName.setOnClickListener(this);
        rlMessageFree.setOnClickListener(this);
        rlDoorLockLanguageSwitch.setOnClickListener(this);
        rlSilentMode.setOnClickListener(this);
        rlDeviceInformation.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        rlAm.setOnClickListener(this);

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
                String deviceNickname = tvDeviceName.getText().toString().trim();
                editText.setText(deviceNickname);
                editText.setSelection(deviceNickname.length());
                editText.addTextChangedListener(new EditTextWatcher(this, null, editText, 50));
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
                        //todo 判断名称是否修改
                        if (TextUtils.isEmpty(name)) {
                            ToastUtil.getInstance().showShort(getString(R.string.device_name_cannot_be_empty));
                            return;
                        }

                        if (deviceNickname != null) {
                            if (deviceNickname.equals(name)) {
                                ToastUtil.getInstance().showShort(getString(R.string.device_nick_name_no_update));
                                alertDialog.dismiss();
                                return;
                            }
                        }
                        if (gatewayId != null && deviceId != null) {
                            mPresenter.updateZigbeeLockName(gatewayId, deviceId, name);
                        }
                        alertDialog.dismiss();
                    }
                });
                break;
            case R.id.rl_message_free:
                showLoading(getString(R.string.is_modifing));
                mPresenter.updatePushSwitch(gatewayId, deviceId, gwLockInfo.getServerInfo().getPushSwitch() == 2 ? 1 : 2);
                break;
            case R.id.rl_door_lock_language_switch:
                intent = new Intent(this, GatewayLockLanguageSettingActivity.class);
                intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                startActivity(intent);
                break;
            case R.id.rl_silent_mode:
                if (flagSoundVolume) {
                    if (silentModeStatus) {
                        //打开状态 现在关闭
                        loadingDialog.show(getString(R.string.close_silence));
                        if (gatewayId != null && deviceId != null) {
                            mPresenter.setSoundVolume(gatewayId, deviceId, 1);
                        }
                    } else {
                        //关闭状态 现在打开
                        loadingDialog.show(getString(R.string.open_slience));
                        if (gatewayId != null && deviceId != null) {
                            mPresenter.setSoundVolume(gatewayId, deviceId, 0);
                        }
                    }
                    rlSilentMode.setEnabled(false);
                } else {
                    ToastUtil.getInstance().showShort(R.string.get_sound_volme_no_fun);
                }
                break;
            case R.id.rl_device_information:
                intent = new Intent(this, GatewayDeviceInformationActivity.class);
                intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                intent.putExtra(KeyConstants.IS_GATEAWAY, true);
                startActivity(intent);
                break;

            case R.id.rl_am:
                //设置A-M
                LogUtils.e("点击了AM");
                if (flagAM) {
                    if (getAutoLockSuccess) {
                        if (autoRelock == 10) {
                            autoRelock = 0;
                        } else {
                            autoRelock = 10;
                        }
                        if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                            loadingDialog.show(getString(R.string.is_setting_am_wait));
                            mPresenter.setAM(MyApplication.getInstance().getUid(), gatewayId, deviceId, autoRelock);
                        }

                    } else {
                        ToastUtil.getInstance().showShort(getString(R.string.get_am_fail));
                    }
                } else {
                    ToastUtil.getInstance().showShort(R.string.get_am_status);
                }

                break;
            case R.id.btn_delete:
                AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.device_delete_dialog_head), getString(R.string.device_delete_lock_dialog_content), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        if (gatewayId != null && deviceId != null) {
                            mPresenter.deleteLock(gatewayId, deviceId, "zigbee");
                            deleteDialog = AlertDialogUtil.getInstance().noButtonDialog(context, getString(R.string.delete_be_being));
                            deleteDialog.setCancelable(false);
                        }

                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

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
        LogUtils.e("设置昵称失败" + throwable.getMessage());
    }

    @Override
    public void getSoundVolumeSuccess(int volume) {
        //获取到音量
        if (volume != 0) {
            ivSilentMode.setImageResource(R.mipmap.iv_close);
            silentModeStatus = false;
            flagSoundVolume = true;
        } else {
            ivSilentMode.setImageResource(R.mipmap.iv_open);
            silentModeStatus = true;
            flagSoundVolume = true;
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
       /* if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
            mPresenter.getAm(MyApplication.getInstance().getUid(), gatewayId, deviceId);
        }*/

    }

    @Override
    public void getSoundVolumeFail() {
      /*  if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
            mPresenter.getAm(MyApplication.getInstance().getUid(), gatewayId, deviceId);
        }*/
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort("获取失败");

    }

    @Override
    public void getSoundVolumeThrowable(Throwable throwable) {
      /* if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
            mPresenter.getAm(MyApplication.getInstance().getUid(), gatewayId, deviceId);
        }*/
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort("获取失败");
        LogUtils.e("获取音量异常   " + throwable.getMessage());
    }

    @Override
    public void setSoundVolumeSuccess(int volume) {
        loadingDialog.dismiss();
        LogUtils.e("设置的音量是  " + volume);
        //设置成功
        if (volume != 0) {
            ivSilentMode.setImageResource(R.mipmap.iv_close);
            silentModeStatus = false;
        } else {
            ivSilentMode.setImageResource(R.mipmap.iv_open);
            silentModeStatus = true;
        }
        rlSilentMode.setEnabled(true);
    }

    @Override
    public void setSoundVolumeFail() {
        //设置失败
        loadingDialog.dismiss();
        rlSilentMode.setEnabled(true);
        ToastUtil.getInstance().showShort(getString(R.string.set_failed));
    }

    @Override
    public void setSoundVolumeThrowable(Throwable throwable) {
        loadingDialog.dismiss();
        rlSilentMode.setEnabled(true);
        LogUtils.e("设置音量异常    " + throwable.getMessage());
    }

    @Override
    public void deleteDeviceSuccess() {
        //删除成功
        DaoSession daoSession = MyApplication.getInstance().getDaoWriteSession();
        String uid = MyApplication.getInstance().getUid();
        daoSession.getGatewayLockServiceInfoDao().queryBuilder().where(GatewayLockServiceInfoDao.Properties.Uid.eq(uid)).buildDelete().executeDeleteWithoutDetachingEntities();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        if (deleteDialog != null) {
            deleteDialog.dismiss();
        }
    }

    @Override
    public void deleteDeviceFail() {
        if (deleteDialog != null) {
            deleteDialog.dismiss();
        }
        //删除失败
        ToastUtil.getInstance().showShort(getString(R.string.delete_fialed));
    }

    @Override
    public void deleteDeviceThrowable(Throwable throwable) {
        if (deleteDialog != null) {
            deleteDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(getString(R.string.delete_fialed));
        LogUtils.e("删除异常   " + throwable.getMessage());
    }


    @Override
    public void setAMSuccess(int autoRelockTime) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (autoRelockTime == 10) {
            ivAm.setImageResource(R.mipmap.iv_open);
        } else {
            ivAm.setImageResource(R.mipmap.iv_close);
        }
    }

    @Override
    public void setAMFail(String code) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if ("405".equals(code)) {
            ToastUtil.getInstance().showShort(R.string.the_lock_no_support_func);
        } else {
            ToastUtil.getInstance().showShort(R.string.set_failed);
        }
    }

    @Override
    public void setAMThrowable(Throwable throwable) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(R.string.set_failed);
    }

    @Override
    public void getAMSuccess(int autoRelockTime) {
        loadingDialog.dismiss();
        getAutoLockSuccess = true;
        flagAM = true;
        if (autoRelockTime == 10) {
            ivAm.setImageResource(R.mipmap.iv_open);
            autoRelock = 10;
        } else {
            ivAm.setImageResource(R.mipmap.iv_close);
            autoRelock = 0;
        }
    }

    @Override
    public void getAMFail(String code) {
        loadingDialog.dismiss();
        flagAM = true;
        if ("405".equals(code)) {
            getAutoLockSuccess = true;
        } else {
            getAutoLockSuccess = false;
            ToastUtil.getInstance().showShort("获取失败");
        }

    }

    @Override
    public void getAMThrowable(Throwable throwable) {
        loadingDialog.dismiss();
        getAutoLockSuccess = false;
        flagAM = true;
        ToastUtil.getInstance().showShort("获取失败");
    }

    @Override
    public void onUpdatePushSwitchSuccess(int status) {
        Toast.makeText(this, getString(R.string.set_success), Toast.LENGTH_SHORT).show();
        hiddenLoading();
        gwLockInfo.getServerInfo().setPushSwitch(status);
        if (status == 2) {
            ivMessageFree.setImageResource(R.mipmap.iv_open);
        } else {
            ivMessageFree.setImageResource(R.mipmap.iv_close);
        }
    }

    @Override
    public void onUpdatePushSwitchThrowable(Throwable throwable) {
        hiddenLoading();
        Toast.makeText(this, getString(R.string.set_failed), Toast.LENGTH_SHORT).show();
    }
}
