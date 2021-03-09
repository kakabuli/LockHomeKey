package com.kaadas.lock.activity.device.clotheshangermachine;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddBLEFailedActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewBLEWIFICSwitchCheckWifiActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockChangeAdminPasswordActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockSixthActivity;
import com.kaadas.lock.adapter.ClothesHangerMachineBleWiFiSearchAdapter;
import com.kaadas.lock.adapter.inf.OnBindClickListener;
import com.kaadas.lock.bean.BluetoothLockBroadcastBean;
import com.kaadas.lock.bean.BluetoothLockBroadcastListBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.mvp.presenter.clotheshangermachinepresenter.ClothesHangerMachineAddThirdPresenter;
import com.kaadas.lock.mvp.presenter.clotheshangermachinepresenter.ClothesHangerMachineAddTourthPresenter;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineAddThirdView;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineAddTourthView;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.dialog.MessageDialog;
import com.kaadas.lock.widget.DropEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClothesHangerMachineAddTourthActivity extends BaseActivity<IClothesHangerMachineAddTourthView,
        ClothesHangerMachineAddTourthPresenter<IClothesHangerMachineAddTourthView>> implements IClothesHangerMachineAddTourthView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.ap_ssid_text)
    DropEditText apSsidText;
    @BindView(R.id.ap_password_edit)
    EditText apPasswordEdit;
    @BindView(R.id.iv_eye)
    ImageView ivEye;
    @BindView(R.id.confirm_btn)
    TextView confirmBtn;

    private MessageDialog messageDialog;

    private static final int TO_CHECK_WIFI_PASSWORD = 10105;
    private int times = 0;

    private String wifiModelType = "";
    private boolean passwordHide = true;
    public String adminPassword;
    public String sSsid;

    private int bleVersion = 0;
    private String deviceSN = "";
    private String deviceMAC = "";
    private String deviceName = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_hanger_machine_add_tourth);
        ButterKnife.bind(this);

        wifiModelType = getIntent().getStringExtra("wifiModelType") + "";
        bleVersion = getIntent().getIntExtra(KeyConstants.BLE_VERSION,4);
        deviceSN = getIntent().getStringExtra(KeyConstants.BLE_DEVICE_SN);
        deviceMAC = getIntent().getStringExtra(KeyConstants.BLE_MAC);
        deviceName = getIntent().getStringExtra(KeyConstants.DEVICE_NAME);

        initView();
        initData();
    }

    private void initView() {
        String wifiName = (String) SPUtils.get(KeyConstants.WIFI_LOCK_CONNECT_NAME, "");

        apSsidText.setText(wifiName.trim());
    }

    private void initData() {

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
    protected ClothesHangerMachineAddTourthPresenter<IClothesHangerMachineAddTourthView> createPresent() {
        return new ClothesHangerMachineAddTourthPresenter<>();
    }


    @OnClick({R.id.back,R.id.confirm_btn,R.id.iv_eye})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.iv_eye:
                passwordHide = !passwordHide;
                if (passwordHide) {
                    apPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    apPasswordEdit.setSelection(apPasswordEdit.getText().toString().length());//将光标移至文字末尾
                    ivEye.setImageResource(R.mipmap.eye_close_has_color);
                } else {
                    apPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    apPasswordEdit.setSelection(apPasswordEdit.getText().toString().length());//将光标移至文字末尾
                    ivEye.setImageResource(R.mipmap.eye_open_has_color);
                }
                break;
            case R.id.confirm_btn:
                sSsid = apSsidText.getText().toString();
                String sPassword = apPasswordEdit.getText().toString();
                if (TextUtils.isEmpty(sSsid)) { //WiFi名为空
                    Toast.makeText(this, R.string.wifi_name_disable_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (sPassword.length() < 8){
                    Toast.makeText(this, getString(R.string.activity_wifi_video_fourth_password), Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(ClothesHangerMachineAddTourthActivity.this,ClothesHangerMachineAddFifthActivity.class);
                intent.putExtra("wifiModelType",wifiModelType);
                intent.putExtra(KeyConstants.CLOTHES_HANGER_PASSWORD_TIMES,times);
                intent.putExtra(KeyConstants.ClOTHES_HANGER_MACHINE_WIFI_SSID,sSsid);
                intent.putExtra(KeyConstants.ClOTHES_HANGER_MACHINE_WIFI_PASSWORD,sPassword);
                intent.putExtra(KeyConstants.BLE_VERSION, bleVersion);
                intent.putExtra(KeyConstants.BLE_DEVICE_SN, deviceSN);
                intent.putExtra(KeyConstants.BLE_MAC, deviceMAC);
                intent.putExtra(KeyConstants.DEVICE_NAME, deviceName);
                startActivityForResult(intent,TO_CHECK_WIFI_PASSWORD);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == TO_CHECK_WIFI_PASSWORD){
                this.times = data.getIntExtra(KeyConstants.CLOTHES_HANGER_PASSWORD_TIMES,1);
                this.bleVersion = data.getIntExtra(KeyConstants.BLE_VERSION,4);
                this.deviceSN = data.getStringExtra(KeyConstants.BLE_DEVICE_SN);
                this.deviceMAC = data.getStringExtra(KeyConstants.BLE_MAC);
                this.deviceName = data.getStringExtra(KeyConstants.DEVICE_NAME);
                LogUtils.e("shulan times-->" + times);
                onAdminPasswordError();
            }
        }
    }



    private void onAdminPasswordError() {
        if (times < 5) {
            showDialog("请确认输入正确的账号密码， \n超过5次，将退出配网");
        } else { //都五次输入错误提示   退出
            AlertDialogUtil.getInstance().noEditSingleCanNotDismissButtonDialog(
                    ClothesHangerMachineAddTourthActivity.this, "",
                    "账号密码已输错5次， \n请重新配网", getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {

                        }

                        @Override
                        public void right() {
                            Intent intent = new Intent(ClothesHangerMachineAddTourthActivity.this,ClothesHangerMachineAddTourthFailedActivity.class);
                            startActivity(intent);
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(String toString) {

                        }
                    });
        }
    }


    private void showDialog(String content){
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                ClothesHangerMachineAddTourthActivity.this
                , content,
                getString(R.string.cancel), getString(R.string.re_input), "#9A9A9A", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        apPasswordEdit.setText("");
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
    public void onDeviceStateChange(boolean isConnected) {
        if(!ClothesHangerMachineAddTourthActivity.this.isFinishing()){
            if (!isConnected) {
                AlertDialogUtil.getInstance().noEditSingleCanNotDismissButtonDialog(
                        ClothesHangerMachineAddTourthActivity.this, "", getString(R.string.ble_disconnected_please_retry), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {
                            }

                            @Override
                            public void right() {
                                Intent intent = new Intent(ClothesHangerMachineAddTourthActivity.this, ClothesHangerMachineAddThirdFailedActivity.class);
                                intent.putExtra("wifiModelType",wifiModelType);
                                finish();
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            @Override
                            public void afterTextChanged(String toString) {
                            }
                        });
            }
        }
    }


}
