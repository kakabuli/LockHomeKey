package com.kaadas.lock.activity.device.gatewaylock.stress.old;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by David
 */
public class GatewayLockStressDetailActivity extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;

    @BindView(R.id.ll_add_password)
    LinearLayout llAddPassword;
    @BindView(R.id.recycleview_password)
    RecyclerView recycleviewPassword;
    @BindView(R.id.iv_app_notification)
    ImageView ivAppNotification;
    @BindView(R.id.rl_app_notification)
    RelativeLayout rlAppNotification;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_lock_stress_password_manager);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
    }
}
