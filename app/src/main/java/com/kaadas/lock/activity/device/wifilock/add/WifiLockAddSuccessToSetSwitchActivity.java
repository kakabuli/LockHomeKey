package com.kaadas.lock.activity.device.wifilock.add;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;

import com.kaadas.lock.activity.addDevice.singleswitch.SwipchLinkActivity;
import com.kaadas.lock.activity.addDevice.singleswitch.SwipchLinkNo;

import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockAddSuccessPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockAddSuccessView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddSuccessToSetSwitchActivity extends BaseActivity<IWifiLockAddSuccessView
        , WifiLockAddSuccessPresenter<IWifiLockAddSuccessView>> implements IWifiLockAddSuccessView, BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.tv_right_now_set)
    TextView right_now_set;
    @BindView(R.id.close)
    ImageView close;


    private String wifiSn;
    private WifiLockInfo wifiLockInfo;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_set_single_switch);
        ButterKnife.bind(this);

        initData();
        initView();

    }

    @Override
    protected WifiLockAddSuccessPresenter<IWifiLockAddSuccessView> createPresent() {
        return new WifiLockAddSuccessPresenter<>();
    }

    private void initData() {
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);

    }
    public void initView(){
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @OnClick({R.id.close,R.id.tv_right_now_set})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.close:

                finish();
                Intent intent = new Intent(this, WifiLockAddSuccessActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                startActivity(intent);
                overridePendingTransition(R.anim.page_centerzoom_enter_quick, R.anim.page_centerzoom_exit);
                break;
            case R.id.tv_right_now_set:
                MyApplication.getInstance().getAllDevicesByMqtt(true);

                handler.postDelayed(runnable, 1000);

                break;
        }
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setSwitch();

            handler.removeCallbacks(runnable);

        }
    };
    public void setSwitch(){
        Intent intent;

        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        finish();
        LogUtils.e("--kaadas--wifiLockInfo=="+wifiLockInfo);

        if (wifiLockInfo != null){
            if (wifiLockInfo.getSingleFireSwitchInfo() != null) {

                int SwitchNumber = wifiLockInfo.getSingleFireSwitchInfo().getSwitchNumber().size();

                if (SwitchNumber > 0) {
                    intent = new Intent(WifiLockAddSuccessToSetSwitchActivity.this, SwipchLinkActivity.class);
                    intent.putExtra(KeyConstants.SWITCH_NUMBER, SwitchNumber);
                    intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                    startActivity(intent);
                } else {
                    intent = new Intent(WifiLockAddSuccessToSetSwitchActivity.this, SwipchLinkNo.class);
                    intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                    startActivity(intent);
                }
            }else {
                intent = new Intent(WifiLockAddSuccessToSetSwitchActivity.this, SwipchLinkNo.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                startActivity(intent);
            }
        }else {
            intent = new Intent(WifiLockAddSuccessToSetSwitchActivity.this, SwipchLinkNo.class);
            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
            startActivity(intent);
        }
    }
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onSetNameSuccess() {

    }

    @Override
    public void onSetNameFailedNet(Throwable throwable) {

    }

    @Override
    public void onSetNameFailedServer(BaseResult baseResult) {

    }
}
