package com.kaadas.lock.activity.my;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.base.mvpbase.BaseActivity;
import com.kaadas.lock.presenter.SystemSettingPresenter;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.CheckLanguageUtil;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.view.ISystemSettingView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalSystemSettingActivity extends BaseActivity<ISystemSettingView, SystemSettingPresenter<ISystemSettingView>> implements ISystemSettingView, View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.version_num)
    TextView versionNum;
    @BindView(R.id.right)
    ImageView right;
    @BindView(R.id.version_layout)
    RelativeLayout versionLayout;
    @BindView(R.id.user_agreement_layout)
    RelativeLayout userAgreementLayout;
    @BindView(R.id.cache_data)
    TextView cacheData;
    @BindView(R.id.clear_cache_layout)
    RelativeLayout clearCacheLayout;
    @BindView(R.id.log_out)
    Button logOut;
    @BindView(R.id.rl_language_setting)
    RelativeLayout rlLanguageSetting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_system_setting);
        ButterKnife.bind(this);
        initView();
        ivBack.setOnClickListener(this);
        rlLanguageSetting.setOnClickListener(this);
        tvContent.setText(R.string.setting);
    }

    private void initView() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(this.getPackageName(), 0);
            String version = packageInfo.versionName;
            versionNum.setText(getString(R.string.current_version) + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected SystemSettingPresenter<ISystemSettingView> createPresent() {
        return new SystemSettingPresenter<>();
    }


    @OnClick({R.id.version_layout, R.id.user_agreement_layout, R.id.clear_cache_layout, R.id.log_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            /*case R.id.language_setting_layout:
                Intent languageIntent = new Intent(this, PersonalLanguageSettingActivity.class);
                startActivity(languageIntent);
                break;*/
            case R.id.version_layout:
                checkVersion();
                break;
            case R.id.user_agreement_layout:
                Intent agreementIntent = new Intent(this, PersonalUserAgreementActivity.class);
                startActivity(agreementIntent);

                break;

            case R.id.clear_cache_layout:
                clearData();
                break;
            case R.id.log_out:
                loginOut();
                break;
        }
    }

    private void clearData() {
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint), getString(R.string.delete_cache_data), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {

            }

            @Override
            public void right() {
                //清除缓存数据，关闭会话。
                ToastUtil.getInstance().showShort(R.string.delete_cache_data_success);
            }
        });


    }

    private void loginOut() {
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint), getString(R.string.confirm_log_out), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {
            }

            @Override
            public void right() {
                mPresenter.loginOut();
                //清除缓存数据，关闭会话。
//                MyApplication.getInstance().setRemoveDBData();
//                MyApplication.getInstance().getDaoSession().clear();
            }
        });
    }

    @Override
    public void onLoginOutSuccess() {
        MyApplication.getInstance().tokenInvalid(false);
    }

    @Override
    public void onLoginOutFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.logout_fail) + HttpUtils.httpProtocolErrorCode(this, throwable));
        LogUtils.e("退出失败  " + throwable.getMessage());
    }

    @Override
    public void onLoginOutFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(getString(R.string.logout_fail) + HttpUtils.httpErrorCode(this, result.getCode()));
        LogUtils.e("退出失败  " + result.getMsg());
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckLanguageUtil.getInstance().checkLag();
    }


    public void checkVersion() {
        Boolean updateFalg = (Boolean) SPUtils.get(SPUtils.APPUPDATE, false);
        if (updateFalg == true) {
            appUpdateDialog();
        } else {
            ToastUtil.getInstance().showShort(R.string.new_version);
        }
    }

    private void toMarkApp() {
        Uri uri = Uri.parse("market://details?id=" + "com.kaidishi.aizhijia");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void appUpdateDialog() {
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint), getString(R.string.find_newAPP), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {

            }

            @Override
            public void right() {
                toMarkApp();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_language_setting:
                intent = new Intent(this, PersonalLanguageSettingActivity.class);
                startActivity(intent);
                break;
        }
    }
}
