package com.kaadas.lock.activity.device.wifilock;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.internal.$Gson$Preconditions;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockSafeModePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockSafeModeView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.utils.LogUtils;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiLockSafeModelActivity extends BaseActivity<IWifiLockSafeModeView,WifiLockSafeModePresenter<IWifiLockSafeModeView>> implements
        View.OnClickListener,IWifiLockSafeModeView {


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
    @BindView(R.id.notice1)
    RelativeLayout notice1;
    @BindView(R.id.all)
    LinearLayout all;
    @BindView(R.id.iv_1)
    ImageView iv1;
    @BindView(R.id.tv_1)
    TextView tv1;
    @BindView(R.id.iv_2)
    ImageView iv2;
    @BindView(R.id.tv_2)
    TextView tv2;
    @BindView(R.id.no_card)
    LinearLayout noCard;
    @BindView(R.id.rl_notice)
    RelativeLayout rlNotice;
    @BindView(R.id.lly_face1)
    LinearLayout llyFace1;
    @BindView(R.id.lly_face)
    LinearLayout llyFace;
    private String wifiSn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_safe_mode);
        ButterKnife.bind(this);

        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.safe_mode);
        rlSafeMode.setOnClickListener(this);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        mPresenter.init(wifiSn);
        WifiLockInfo wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        String functionSet = wifiLockInfo.getFunctionSet();
        int safeMode = wifiLockInfo.getSafeMode();
        ivSafeMode.setImageResource(safeMode == 1 ? R.mipmap.iv_open : R.mipmap.iv_close);
        int func;
        try {
            func = Integer.parseInt(functionSet);
        } catch (Exception e) {
            func = 0x04;
        }
        boolean supportCard = BleLockUtils.isSupportCard(func + "");
        boolean supportFinger = BleLockUtils.isSupportFinger(func + "");
        boolean supportPassword = BleLockUtils.isSupportPassword(func + "");
        if (supportCard && supportFinger && supportPassword) {
            all.setVisibility(View.VISIBLE);
            noCard.setVisibility(View.GONE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(rlNotice.getLayoutParams());
            lp.setMargins(0, 0, 0, DensityUtil.dp2px( 60));
            rlNotice.setLayoutParams(lp);

            llyFace.setVisibility(View.GONE);
            if(BleLockUtils.isSupportFace(func + "")){
                llyFace1.setVisibility(View.VISIBLE);
            }else{
                llyFace1.setVisibility(View.GONE);
            }
        } else {
            all.setVisibility(View.GONE);
            noCard.setVisibility(View.VISIBLE);
            if(BleLockUtils.isSupportFace(func + "")){
                llyFace.setVisibility(View.VISIBLE);
            }else{
                llyFace.setVisibility(View.GONE);
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(rlNotice.getLayoutParams());
            lp.setMargins(0, 0, 0, DensityUtil.dp2px( 100));
            rlNotice.setLayoutParams(lp);
            if (supportFinger && supportCard) {
                iv1.setImageResource(R.mipmap.safe_finger);
                iv1.setImageResource(R.mipmap.safe_card);
                tv1.setText(R.string.finger);
                tv2.setText(R.string.card);
            } else if (supportPassword && supportFinger) {
                iv1.setImageResource(R.mipmap.safe_password);
                iv1.setImageResource(R.mipmap.safe_finger);
                tv1.setText(R.string.password);
                tv2.setText(R.string.finger);
            } else if (supportPassword && supportCard) {
                iv1.setImageResource(R.mipmap.safe_password);
                iv1.setImageResource(R.mipmap.safe_card);
                tv1.setText(R.string.password);
                tv2.setText(R.string.card);
            }
        }
    }

    @Override
    protected WifiLockSafeModePresenter<IWifiLockSafeModeView> createPresent() {
        return new WifiLockSafeModePresenter<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_safe_mode:
                ToastUtils.showLong(R.string.please_operation_in_lock);
                break;
        }
    }


    @Override
    public void onWifiLockActionUpdate() {
        WifiLockInfo wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        if (ivSafeMode!=null && wifiLockInfo!=null){
            int safeMode = wifiLockInfo.getSafeMode();
            ivSafeMode.setImageResource(safeMode == 1 ? R.mipmap.iv_open : R.mipmap.iv_close);
        }
    }
}
