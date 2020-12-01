package com.kaadas.lock.activity.device.bluetooth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.LanguageSetPresenter;
import com.kaadas.lock.mvp.view.ILanguageSetView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothLockLanguageSettingActivity extends BaseBleActivity<ILanguageSetView, LanguageSetPresenter<ILanguageSetView>>
        implements ILanguageSetView, View.OnClickListener {
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
    private Context context;
    private String languageCurrent = "";
    private BleLockInfo bleLockInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_lock_language_setting);
        ButterKnife.bind(this);
        bleLockInfo = mPresenter.getBleLockInfo();
        if (mPresenter.isAuth(bleLockInfo, false)) {
            mPresenter.getDeviceInfo();
        }else{
            ToastUtil.getInstance().showLong(R.string.please_connect_lock);
        }
        context = MyApplication.getInstance();
        initData();
        tvContent.setText(getString(R.string.lock_language));
        ivBack.setOnClickListener(this);

    }

    @Override
    protected LanguageSetPresenter<ILanguageSetView> createPresent() {
        return new LanguageSetPresenter<>();
    }

    private void initData() {
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
                if (mPresenter.isAuth(bleLockInfo, true)){
                    if ("zh".equals(languageCurrent)){
                        mPresenter.setLanguage("zh");
                    }else if ("en".equals(languageCurrent)){
                        mPresenter.setLanguage("en");
                    }
                }
                showLoading(getString(R.string.is_setting));
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
    public void onGetLanguageSuccess(String lang) {
        if ("zh".equals(lang)) {  //中文
            zhImg.setChecked(true);
            enImg.setChecked(false);
            languageCurrent = "zh";
        } else {  //英文
            zhImg.setChecked(false);
            enImg.setChecked(true);
            languageCurrent = "en";
        }
    }

    @Override
    public void onGetLanguageFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.read_device_language_fail));
    }

    @Override
    public void onSetLangSuccess(String language) {
        if ("zh".equals(language)) {  //中文
            languageCurrent = "zh";
        } else {  //英文
            languageCurrent = "en";
        }
        ToastUtil.getInstance().showShort(R.string.set_success);
        hiddenLoading();
    }

    @Override
    public void onSetLangFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.set_failed);
        hiddenLoading();
    }
}
