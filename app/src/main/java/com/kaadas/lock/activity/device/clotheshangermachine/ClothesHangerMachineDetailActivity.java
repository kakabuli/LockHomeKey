package com.kaadas.lock.activity.device.clotheshangermachine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.clotheshangermachinepresenter.ClothesHangerMachineAddPresenter;
import com.kaadas.lock.mvp.presenter.clotheshangermachinepresenter.ClothesHangerMachineDetailPresenter;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineAddView;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineDetailView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.MultiCheckOTAResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.greenDao.bean.ClothesHangerMachineAllBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import la.xiong.androidquick.tool.ToastUtil;

public class ClothesHangerMachineDetailActivity extends BaseActivity<IClothesHangerMachineDetailView, ClothesHangerMachineDetailPresenter<IClothesHangerMachineDetailView>>
        implements IClothesHangerMachineDetailView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.rl_device_name)
    RelativeLayout rlDeviceName;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;
    @BindView(R.id.tv_module_number)
    TextView tvModuleNumber;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_module_version)
    TextView tvModuleVersion;
    @BindView(R.id.rl_check_version)
    RelativeLayout rlCheckVersion;

    private String wifiSN = "";
    private ClothesHangerMachineAllBean hangerInfo;

    private final int SETTING_NICKNAME_REQUSE = 1201;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_hanger_machine_detail);
        ButterKnife.bind(this);
        wifiSN = getIntent().getStringExtra(KeyConstants.WIFI_SN);

        hangerInfo = MyApplication.getInstance().getClothesHangerMachineBySn(wifiSN);
        if(hangerInfo != null){
            if(hangerInfo.getHangerNickName().isEmpty()){
                tvDeviceName.setText(hangerInfo.getWifiSN() + "");
            }else{
                tvDeviceName.setText(hangerInfo.getHangerNickName() + "");
            }
            if(hangerInfo.getHangerSN() != null)
                tvSerialNumber.setText(hangerInfo.getHangerSN() + "");
            if(hangerInfo.getModuleSN() != null)
                tvModuleNumber.setText(hangerInfo.getModuleSN() + "");
            if(hangerInfo.getHangerVersion() != null)
                tvVersion.setText(hangerInfo.getHangerVersion() + "");
            if(hangerInfo.getModuleVersion() != null)
                tvModuleVersion.setText(hangerInfo.getModuleVersion() + "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected ClothesHangerMachineDetailPresenter<IClothesHangerMachineDetailView> createPresent() {
        return new ClothesHangerMachineDetailPresenter<>();
    }

    @OnClick({R.id.back,R.id.rl_check_version,R.id.rl_device_name,R.id.btn_delete})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.rl_check_version:
                mPresenter.checkOTAInfo(wifiSN,tvVersion.getText().toString().trim() + "",
                        tvModuleVersion.getText().toString().trim() + "");
                break;
            case R.id.rl_device_name:
                Intent settingNameIntent = new Intent(ClothesHangerMachineDetailActivity.this,ClothesHangerMachineSettingNameActivity.class);
                settingNameIntent.putExtra(KeyConstants.WIFI_SN,wifiSN);
                startActivityForResult(settingNameIntent,SETTING_NICKNAME_REQUSE);
                break;
            case R.id.btn_delete:
                showDeleteDevice();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            if(requestCode == SETTING_NICKNAME_REQUSE){
                LogUtils.e("shulan hanger_nick_name-----> " +data.getStringExtra("hanger_nick_name"));
                tvDeviceName.setText(data.getStringExtra("hanger_nick_name") + "");
            }
        }
    }

    private void showCheckVersion(List<MultiCheckOTAResult.UpgradeTask> upgradeTasks) {
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                ClothesHangerMachineDetailActivity.this,
                "发现新版本，是否更新？",
                "否", "是","#9A9A9A", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        mPresenter.updateOTA(wifiSN,upgradeTasks);
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }

    private void showDeleteDevice() {
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                ClothesHangerMachineDetailActivity.this,
                "删除后，需要重新连接 \n是否删除？",
                "否", "是","#9A9A9A", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        mPresenter.deleteDevice(hangerInfo.getWifiSN());
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }

    private void showShort(String ss) {
        ToastUtil.setGravity(Gravity.CENTER,0,0);
        ToastUtil.showShort(ss);
    }

    @Override
    public void needUpdate(List<MultiCheckOTAResult.UpgradeTask> upgradeTasks) {
        LogUtils.e("shulan needUpdate");
        showCheckVersion(upgradeTasks);
    }

    @Override
    public void noUpdate() {
        showShort(getString(R.string.new_version) + "");
    }

    @Override
    public void checkUpdateFailed(BaseResult baseResult) {

    }

    @Override
    public void checkUpdateThrowable(Throwable throwable) {

    }

    @Override
    public void updateSuccess() {

    }

    @Override
    public void updateFailed(BaseResult baseResult) {

    }

    @Override
    public void updateThrowable(Throwable throwable) {

    }

    @Override
    public void onDeleteDeviceSuccess() {
        showShort(getString(R.string.delete_success));
        hiddenLoading();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDeleteDeviceFailed(BaseResult result) {
        LogUtils.e("删除失败   " + result.toString());
        String httpErrorCode = HttpUtils.httpErrorCode(this, result.getCode());
        showShort(httpErrorCode);
        hiddenLoading();
    }

    @Override
    public void onDeleteDeviceThrowable(Throwable throwable) {
        LogUtils.e("删除失败   " + throwable.getMessage());
       showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
//        ToastUtil.getInstance().showLong(R.string.delete_fialed);
        hiddenLoading();
    }
}
