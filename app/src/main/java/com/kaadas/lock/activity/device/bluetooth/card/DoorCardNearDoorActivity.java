package com.kaadas.lock.activity.device.bluetooth.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.ToastUtil;

import net.sdvn.cmapi.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/17
 */
public class DoorCardNearDoorActivity extends BaseActivity<IBleView, BlePresenter<IBleView>>
        implements View.OnClickListener, IBleView{
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.btn)
    Button btn;
    int bluetoothConnectStatus=-1;
    int bluetoothConnectSuccess=1;
    int bluetoothConnectFail=2;
    private BleLockInfo bleLockInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_card_near_door);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        btn.setOnClickListener(this);
        tvContent.setText(R.string.add_door_card);
        bleLockInfo = MyApplication.getInstance().getBleService().getBleLockInfo();
        mPresenter.isAuth(bleLockInfo, true);
    }

    @Override
    protected BlePresenter<IBleView> createPresent() {
        return new BlePresenter<IBleView>() {
            @Override
            public void authSuccess() {

            }
        };
    }

    public void changeBluetoothStatus(){
        if (bluetoothConnectStatus==bluetoothConnectSuccess){
            btn.setEnabled(true);
            btn.setBackgroundResource(R.drawable.retangle_1f96f7_22);
            btn.setText(R.string.connect_success);
            btn.setVisibility(View.VISIBLE);
        }else if (bluetoothConnectStatus==bluetoothConnectFail){
            btn.setEnabled(true);
            btn.setBackgroundResource(R.drawable.retangle_ff3b30_22);
            btn.setText(R.string.connect_fail);
            btn.setVisibility(View.VISIBLE);
        }else {
            btn.setEnabled(false);
            btn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn:
                if (bluetoothConnectStatus==bluetoothConnectSuccess){
                    //跳转到添加界面
                    Intent intent = new Intent(this, DoorCardIdentificationActivity.class);
                    startActivity(intent);
                    finish();
                }else if (bluetoothConnectStatus==bluetoothConnectFail){
                    Intent intent = new Intent(this, DoorCardNoConnectOneActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;

        }
    }

    @Override
    public void onBleOpenStateChange(boolean isOpen) {
        if (!isOpen) { //如果蓝牙没有打开
            ToastUtil.getInstance().showLong(R.string.please_allow_open_ble);
        }
    }

    @Override
    public void onDeviceStateChange(boolean isConnected) {

    }

    @Override
    public void onStartSearchDevice() {

    }

    @Override
    public void onSearchDeviceFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.search_device_fail));
    }

    @Override
    public void onSearchDeviceSuccess() {

    }

    @Override
    public void onNeedRebind(int errorCode) {

    }

    @Override
    public void authResult(boolean isSuccess) {
        if (isSuccess) {
            LogUtils.e("鉴权成功");
            bluetoothConnectStatus=bluetoothConnectSuccess;
            changeBluetoothStatus();

        } else {  //鉴权失败

        }
    }

    @Override
    public void onStartConnectDevice() {

    }

    @Override
    public void onEndConnectDevice(boolean isSuccess) {
        //结束连接
        if (!isSuccess) {
            //r如果没有连接，
            ToastUtil.getInstance().showLong(R.string.connect_failed_please_hand_connect);
            toHandView();
        }
    }

    @Override
    public void onGetPasswordSuccess(GetPasswordResult result) {

    }

    @Override
    public void onGetPasswordFailedServer(BaseResult result) {

    }

    @Override
    public void onGetPasswordFailed(Throwable throwable) {

    }

    @Override
    public void noPermissions() {

    }

    /**
     * 去手动绑定界面
     */
    public void toHandView() {
        bluetoothConnectStatus=bluetoothConnectFail;
        changeBluetoothStatus();
    }
}
