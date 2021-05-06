package com.kaadas.lock.activity.device.bluetooth.fingerprint;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/17
 */
public class FingerprintNoConnectBluetoothCollectionActivity extends BaseAddToApplicationActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.btn_finish)
    Button btnFinish;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_no_connect_bluetooth_collection);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        tvContent.setText(R.string.add_fingerprint);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_finish:
                Intent intent = new Intent(this, FingerprintManagerActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
