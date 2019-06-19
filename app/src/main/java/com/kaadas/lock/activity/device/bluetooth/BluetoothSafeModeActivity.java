package com.kaadas.lock.activity.device.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.BluetoothLockFunctionActivity;
import com.kaadas.lock.activity.device.BluetoothLockFunctionV6V7Activity;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.SafeModePresenter;
import com.kaadas.lock.mvp.view.ISafeModeView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/15
 */
public class BluetoothSafeModeActivity extends BaseBleActivity<ISafeModeView, SafeModePresenter<ISafeModeView>>
        implements ISafeModeView, View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.iv_safe_mode)
    ImageView ivSafeMode;
    @BindView(R.id.rl_safe_mode)
    RelativeLayout rlSafeMode;
    boolean safeModeStatus;
    private BleLockInfo bleLockInfo;
    private String name;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_safe_mode);
        ButterKnife.bind(this);
        bleLockInfo = mPresenter.getBleLockInfo();
        initData();
        if (mPresenter.isAuth(bleLockInfo, false)) {
            mPresenter.getDeviceInfo();
        } else {
            ToastUtil.getInstance().showLong(getString(R.string.please_connect_lock));
        }
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.safe_mode);
        rlSafeMode.setOnClickListener(this);
    }
    private void initData() {
        if (bleLockInfo != null) {
            name = bleLockInfo.getServerLockInfo().getModel();
        }

    }
    @Override
    protected SafeModePresenter<ISafeModeView> createPresent() {
        return new SafeModePresenter<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_safe_mode:
                /**
                 * 开关监听事件
                 */
                if (mPresenter.isAuth(bleLockInfo, false)){
                    //打开时
                    if (safeModeStatus){
                        mPresenter.openSafeMode(false);
                    }else{
                        mPresenter.openSafeMode(true);
                    }
                }
                showLoading(getString(R.string.is_setting));

                break;
        }
    }

    @Override
    public void onSetSuccess(boolean isOpen) {
        LogUtils.e("设置安全模式成功   " + isOpen);
        if (isOpen) {
            ivSafeMode.setImageResource(R.mipmap.iv_open);
            safeModeStatus=true;
        } else {
            ivSafeMode.setImageResource(R.mipmap.iv_close);
            safeModeStatus=false;
        }
        hiddenLoading();
    }

    @Override
    public void onSetFailed(Throwable throwable) {
        ToastUtil.getInstance().showLong(getString(R.string.set_failed));
        hiddenLoading();
    }

    @Override
    public void onGetStateSuccess(boolean isOpen) {
        if (isOpen) {
            safeModeStatus=true;
            ivSafeMode.setImageResource(R.mipmap.iv_open);
        } else {
            safeModeStatus=false;
            ivSafeMode.setImageResource(R.mipmap.iv_close);
        }
    }

    @Override
    public void onGetStateFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.get_lock_state_fail) );
        LogUtils.e("获取门锁状态失败   " + throwable.getMessage());
    }

    @Override
    public void onPasswordTypeLess() {
        hiddenLoading();
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint), getString(R.string.safe_mode_dialog),getString(R.string.cancel),getString(R.string.query) , new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {

            }

            @Override
            public void right() {
                String lockType = bleLockInfo.getServerLockInfo().getModel();
                if (!TextUtils.isEmpty(lockType)){
                    if (lockType.startsWith("V6")||lockType.startsWith("V7")||lockType.startsWith("S100")){
                        Intent intent = new Intent();
                        intent.setClass(BluetoothSafeModeActivity.this, BluetoothLockFunctionV6V7Activity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent();
                        intent.setClass(BluetoothSafeModeActivity.this, BluetoothLockFunctionActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }


            }
        });
    }

    @Override
    public void onSendCommand() {
        showLoading(getString(R.string.is_setting));
    }
}
