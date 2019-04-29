package com.kaadas.lock.activity.device.gatewaylock.fingerprint;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/17
 */
public class GatewayFingerprintLinkActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_fingerprint_link);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.add_fingerprint);
/*        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (bluetoothConnected) {
                    intent = new Intent(GatewayFingerprintLinkActivity.this, FingerprintCollectionActivity.class);
                } else {
                    intent = new Intent(GatewayFingerprintLinkActivity.this, FingerprintNoConnectBluetoothOneActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 3000);*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;

        }
    }
}
