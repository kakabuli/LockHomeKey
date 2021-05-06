package com.kaadas.lock.activity.device.gatewaylock.more;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockLangPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockLangView;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GatewayLockLanguageSettingActivity extends BaseActivity<GatewayLockLangView,GatewayLockLangPresenter<GatewayLockLangView>> implements View.OnClickListener,GatewayLockLangView {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.zh_img)
    CheckBox zhImg;
    @BindView(R.id.zh_layout)
    RelativeLayout zhLayout;
    @BindView(R.id.en_img)
    CheckBox enImg;
    @BindView(R.id.en_layout)
    RelativeLayout enLayout;
    @BindView(R.id.btn_save)
    Button btnSave;
    private String languageCurrent = "";

    private String  deviceId;
    private String  gatewayId;
    private LoadingDialog loadingDialog;

    private String lockCurrentLang="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_lock_language_setting);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();


    }

    @Override
    protected GatewayLockLangPresenter<GatewayLockLangView> createPresent() {
        return new GatewayLockLangPresenter<>();
    }

    private void initListener() {
        ivBack.setOnClickListener(this);
    }

    private void initView() {
        tvContent.setText(getString(R.string.lock_language));
        loadingDialog=LoadingDialog.getInstance(this);
    }
    private void initData() {
        Intent intent=getIntent();
        deviceId=intent.getStringExtra(KeyConstants.DEVICE_ID);
        gatewayId=intent.getStringExtra(KeyConstants.GATEWAY_ID);
        if (deviceId!=null&&gatewayId!=null){
            loadingDialog.show(getString(R.string.get_lock_lang));
            mPresenter.getLang(gatewayId,deviceId);

        }
    }

    @OnClick({R.id.zh_layout, R.id.en_layout, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.zh_layout:
                zhImg.setChecked(true);
                enImg.setChecked(false);
                languageCurrent = "zh";
                break;
            case R.id.en_layout:
                zhImg.setChecked(false);
                enImg.setChecked(true);
                languageCurrent = "en";
                break;
            case R.id.btn_save:
                if (TextUtils.isEmpty(lockCurrentLang)){
                    ToastUtil.getInstance().showShort(getString(R.string.no_get_current_lang));
                    return;
                }
                if (lockCurrentLang.equals(languageCurrent)){
                    ToastUtil.getInstance().showShort(getString(R.string.lock_lang_no_change));
                    return;
                }
                if (gatewayId!=null&&deviceId!= null){
                    ToastUtil.getInstance().showShort(R.string.is_setting_lock_lang);
                    mPresenter.setLang(gatewayId,deviceId,languageCurrent);
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void getLockLangSuccess(String lang) {
        LogUtils.e("获取到的语言是"+lang);
        loadingDialog.dismiss();
        if ("zh".equals(lang)){
            zhImg.setChecked(true);
            enImg.setChecked(false);
            lockCurrentLang="zh";
            languageCurrent="zh";
        }else{
            zhImg.setChecked(false);
            enImg.setChecked(true);
            lockCurrentLang="en";
            languageCurrent="en";
        }

    }

    @Override
    public void getLockLangFail() {
        loadingDialog.dismiss();
        ToastUtil.getInstance().showShort(R.string.get_lock_lang_fail);
    }

    @Override
    public void getLockLangThrowable(Throwable throwable) {
        loadingDialog.dismiss();
        LogUtils.e("获取锁的语言异常      "+throwable.getMessage());
    }

    @Override
    public void setLockLangSuccess(String lang) {
        languageCurrent=lang;
        lockCurrentLang=lang;
        ToastUtil.getInstance().showShort(getString(R.string.set_success));
    }


    @Override
    public void setLockLangFail() {
        ToastUtil.getInstance().showShort(getString(R.string.set_failed));
    }

    @Override
    public void setLockLangThrowable(Throwable throwable) {
        LogUtils.e("设置锁的语言异常    "+throwable.getMessage());
    }
}
