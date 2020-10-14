package com.kaadas.lock.activity.device.wifilock.videolock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.WifiLockRealTimeActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockRealTimeVideoPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockRealTimeVideoView;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockRealTimeVideoActivity extends BaseActivity<IWifiLockRealTimeVideoView,
        WifiLockRealTimeVideoPresenter<IWifiLockRealTimeVideoView>> implements IWifiLockRealTimeVideoView {

    @BindView(R.id.iv_setting)
    ImageView ivSetting;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.surface_view)
    SurfaceView mSufaceView;
    @BindView(R.id.tv_temporary_password)
    TextView tvTemporaryPassword;
    @BindView(R.id.lly_temporary_password)
    LinearLayout llyTemporaryPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_real_time_video);

        ButterKnife.bind(this);
        LogUtils.e( "WifiLockRealTimeVideoActivity onCreate: ");
        mPresenter.startRealTimeVideo(mSufaceView);
    }

    @Override
    protected WifiLockRealTimeVideoPresenter<IWifiLockRealTimeVideoView> createPresent() {
        return new WifiLockRealTimeVideoPresenter<>();
    }


    @OnClick({R.id.back, R.id.iv_setting,R.id.iv_screenshot,R.id.iv_mute,R.id.iv_calling,R.id.iv_recoring})
    public void onViewClicked(View view) {
        int ret = -999;
        switch (view.getId()){
            case R.id.back:
                mPresenter.release();
                finish();
                break;
            case R.id.iv_setting:
                startActivity(new Intent(WifiLockRealTimeVideoActivity.this, WifiLockRealTimeActivity.class));
                break;
            case R.id.iv_screenshot:
                break;
            case R.id.iv_mute:
                ret= XMP2PManager.getInstance().startAudioStream();
                if(ret>=0){
                    if(!XMP2PManager.getInstance().isEnableAudio()){
                        XMP2PManager.getInstance().enableAudio(true);
                    }
                }
                break;
            case R.id.iv_calling:
                break;
            case R.id.iv_recoring:
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.attachView(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.detachView();
    }

    @Override
    protected void onDestroy() {
        mPresenter.release();
        super.onDestroy();
    }

}
