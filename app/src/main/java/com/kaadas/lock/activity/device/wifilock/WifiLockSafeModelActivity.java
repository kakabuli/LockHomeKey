package com.kaadas.lock.activity.device.wifilock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.ToastUtil;
import com.lzy.imagepicker.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiLockSafeModelActivity extends BaseAddToApplicationActivity
        implements View.OnClickListener {


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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_safe_mode);
        ButterKnife.bind(this);

        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.safe_mode);
        rlSafeMode.setOnClickListener(this);

        String functionSet = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_FUNCTION);
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
            lp.setMargins(0, 0, 0, Utils.dp2px(this, 60));
            rlNotice.setLayoutParams(lp);
        } else {
            all.setVisibility(View.GONE);
            noCard.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(rlNotice.getLayoutParams());
            lp.setMargins(0, 0, 0, Utils.dp2px(this, 100));
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_safe_mode:
                ToastUtil.getInstance().showLong(R.string.please_operation_in_lock);
                break;
        }
    }

}
