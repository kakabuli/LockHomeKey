package com.kaadas.lock.activity.device.bluetooth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/15
 */
public class BluetoothSafeModeActivity extends AppCompatActivity implements View.OnClickListener {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_safe_mode);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.safe_mode);
        rlSafeMode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_safe_mode:
                if (safeModeStatus) {
                    //打开状态 现在关闭
                    ivSafeMode.setImageResource(R.mipmap.iv_close);
                    SPUtils.put(KeyConstants.SAFE_MODE_STATUS, false);
                } else {
                    //关闭状态 现在打开
                    ivSafeMode.setImageResource(R.mipmap.iv_open);
                    SPUtils.put(KeyConstants.SAFE_MODE_STATUS, true);
                }
                safeModeStatus = !safeModeStatus;
                break;
        }
    }
}
