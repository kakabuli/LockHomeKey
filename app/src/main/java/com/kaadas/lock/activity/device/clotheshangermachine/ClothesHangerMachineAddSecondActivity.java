package com.kaadas.lock.activity.device.clotheshangermachine;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.mvp.presenter.clotheshangermachinepresenter.ClothesHangerMachineAddSecondPresenter;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.DeviceZigBeeDetailPresenter;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineAddSecondView;
import com.kaadas.lock.mvp.view.deviceaddview.DeviceZigBeeDetailView;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.WifiUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class ClothesHangerMachineAddSecondActivity extends BaseActivity<IClothesHangerMachineAddSecondView,
        ClothesHangerMachineAddSecondPresenter<IClothesHangerMachineAddSecondView>> implements IClothesHangerMachineAddSecondView {

    @BindView(R.id.button_next)
    TextView button_next;
    @BindView(R.id.back)
    ImageView back;

    private String wifiModelType = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_hanger_machine_add_second);
        ButterKnife.bind(this);

        wifiModelType = getIntent().getStringExtra("wifiModelType") + "";
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    protected ClothesHangerMachineAddSecondPresenter<IClothesHangerMachineAddSecondView> createPresent() {
        return new ClothesHangerMachineAddSecondPresenter<>();
    }

    @OnClick({R.id.button_next,R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.button_next:
                //检查蓝牙是否打开
                if(mPresenter.isBluetoothEnable()){
                    Intent intent = new Intent(this,ClothesHangerMachineAddThirdActivity.class);
                    intent.putExtra("wifiModelType",wifiModelType);
                    startActivity(intent);
                    finish();
                }else{
                    showBluetoothEnableDialog();
                }
                break;
        }
    }



    private void showBluetoothErrorDialog() {
        AlertDialogUtil.getInstance().noEditTitleOneButtonDialog(
                ClothesHangerMachineAddSecondActivity.this,
                "设置失败",
                getString(R.string.confirm), "#1F96F7", new AlertDialogUtil.ClickListener() {
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

    private void showBluetoothEnableDialog() {
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                ClothesHangerMachineAddSecondActivity.this,
                "蓝牙未开启，是否开启蓝牙？",
                "关闭", "设置","#9A9A9A", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        mPresenter.enableBle();
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }

    @Override
    public void openBluetoothStatus(boolean flag) {
        if(!flag){
            showBluetoothErrorDialog();
        }
    }

}
