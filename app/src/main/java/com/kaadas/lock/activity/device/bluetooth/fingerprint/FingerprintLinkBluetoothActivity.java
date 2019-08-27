package com.kaadas.lock.activity.device.bluetooth.fingerprint;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.ble.AddFingerprintSearchPresenter;
import com.kaadas.lock.mvp.view.IAddPringerprintSearchView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.utils.ToastUtil;

import net.sdvn.cmapi.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/17
 */
public class FingerprintLinkBluetoothActivity extends BaseActivity<IAddPringerprintSearchView, AddFingerprintSearchPresenter<IAddPringerprintSearchView>>
        implements View.OnClickListener, IAddPringerprintSearchView {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    //    @BindView(R.id.btn_confirm_generation)
//    Button btnConfirmGeneration;
    boolean bluetoothConnected = true;//蓝牙是否连接
    private BleLockInfo bleLockInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_link_bluetooth);
        ButterKnife.bind(this);
        bleLockInfo = MyApplication.getInstance().getBleService().getBleLockInfo();
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.add_fingerprint);
//        btnConfirmGeneration.setOnClickListener(this);
        mPresenter.isAuth(bleLockInfo, true);

    }

    @Override
    protected AddFingerprintSearchPresenter<IAddPringerprintSearchView> createPresent() {
        return new AddFingerprintSearchPresenter<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
  /*          case R.id.btn_confirm_generation:

                break;*/
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
            //跳转到添加界面
            Intent intent = new Intent(this, FingerprintCollectionActivity.class);
            startActivity(intent);
            finish();
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

    @Override
    public void noOpenGps() {

    }

    @Override
    public void notAdminMustHaveNet() {

    }

    @Override
    public void inputPwd() {

    }

    @Override
    public void authServerFailed(BaseResult baseResult) {

    }

    @Override
    public void openLockFailed(Throwable throwable) {

    }

    @Override
    public void openLockSuccess() {

    }

    @Override
    public void onLockLock() {

    }

    @Override
    public void authFailed(Throwable throwable) {

    }

    @Override
    public void isOpeningLock() {

    }

    /**
     * 去手动绑定界面
     */
    public void toHandView() {
        Intent intent = new Intent(this, FingerprintNoConnectBluetoothOneActivity.class);
        startActivity(intent);
        finish();
    }
}
