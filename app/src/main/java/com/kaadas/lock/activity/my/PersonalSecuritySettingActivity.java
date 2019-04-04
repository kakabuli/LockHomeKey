package com.kaadas.lock.activity.my;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.base.mvpbase.BaseActivity;
import com.kaadas.lock.presenter.personalpresenter.PersonalSecuritySettingPresenter;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.cachefloder.ACache;
import com.kaadas.lock.utils.cachefloder.CacheFloder;
import com.kaadas.lock.view.personalview.IPersonalSecuritySettingView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalSecuritySettingActivity extends BaseActivity<IPersonalSecuritySettingView, PersonalSecuritySettingPresenter<IPersonalSecuritySettingView>> implements CompoundButton.OnCheckedChangeListener, IPersonalSecuritySettingView, View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.security_setting_switch_text)
    TextView securitySettingSwitchText;
    @BindView(R.id.security_setting_switch)
    SwitchCompat securitySettingSwitch;
    @BindView(R.id.update_hand_pwd_layout)
    RelativeLayout updateHandPwdLayout;
    @BindView(R.id.open_touch_id_switch)
    SwitchCompat openTouchIdSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_security_setting);
        ButterKnife.bind(this);
        initView();
        initListener();
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.security_setting);
    }


    @Override
    protected PersonalSecuritySettingPresenter<IPersonalSecuritySettingView> createPresent() {

        return new PersonalSecuritySettingPresenter<>();
    }

    private void initListener() {
        securitySettingSwitch.setOnCheckedChangeListener(this);
        openTouchIdSwitch.setOnCheckedChangeListener(this);
    }

    private void initView() {

        mPresenter.setHandPwdSwitchFlag();
        mPresenter.setFingerPrintFlag();
    }

    @OnClick({R.id.update_hand_pwd_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.update_hand_pwd_layout:
                String code = CacheFloder.readHandPassword(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "handPassword");
                if (code != null) {
                    //todo
//                    Intent personalUpdateVerifyIntent = new Intent(this, PersonalUpdateVerifyGesturePwd.class);
//                    startActivity(personalUpdateVerifyIntent);
                } else {
                    showHandPwdDilog();
                }


                break;

        }

    }

    private void showHandPwdDilog() {
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.hint), getString(R.string.please_open_hand_pwd), getString(R.string.dialog_confirm), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {
            }

            @Override
            public void right() {

            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            //手势密码
            case R.id.security_setting_switch:
                //开启
                if (isChecked) {
//                    Intent open = new Intent(this, PersonalUpdateGesturePwdActivity.class);
//                    startActivity(open);
                } else {
                    //关闭
                    //清除缓存手势密码数据
                    ACache.get(MyApplication.getInstance()).remove(MyApplication.getInstance().getUid() + "handPassword");
                    securitySettingSwitchText.setText(R.string.open_hand_pwd);
                }
                break;

            case R.id.open_touch_id_switch:
                //指纹密码
                //判断是否支持指纹识别
                if (isChecked) {
                    Boolean flag = mPresenter.isSupportFinger();
                    if (flag == false) {
                        //手机不支持指纹识别
                        openTouchIdSwitch.setChecked(false);
                        ToastUtil.getInstance().showShort(R.string.no_support_fingeprint);
                    } else {
                        mPresenter.isOpenFingerPrint();
                    }
                } else {
                    ACache.get(MyApplication.getInstance()).remove(MyApplication.getInstance().getUid() + "fingerStatus");
                }
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置关闭手势密码的开关
        mPresenter.setHandPwdSwitchFlag();
    }


    @Override
    public void openHandPwdSuccess() {
        securitySettingSwitch.setChecked(true);
        securitySettingSwitchText.setText(R.string.close_hand_pwd);
    }

    @Override
    public void closeHandPwdSuccess() {
        securitySettingSwitch.setChecked(false);
        securitySettingSwitchText.setText(R.string.open_hand_pwd);
    }

    @Override
    public void phoneFigerprintOpen() {
        //已经打开

        View mView = LayoutInflater.from(this).inflate(R.layout.activity_personal_fingerprint_security, null);
        TextView mFingerCancel = mView.findViewById(R.id.finger_cancel);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
        CacheFloder.writePhoneFingerPrint(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "fingerStatus", "true");
        mFingerCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void phoneFigerprintClose() {
        //关闭
        openTouchIdSwitch.setChecked(false);
        ToastUtil.getInstance().showLong(R.string.no_open_fingerprint);
    }

    @Override
    public void openFingerPrintSuccess() {
        openTouchIdSwitch.setChecked(true);
    }

    @Override
    public void closeFingerPrintSuccess() {
        openTouchIdSwitch.setChecked(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
