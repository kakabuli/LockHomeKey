package com.kaadas.lock.activity.device.bluetooth.fingerprint;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/17
 */
public class FingerprintLinkBluetoothActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    //    @BindView(R.id.btn_confirm_generation)
//    Button btnConfirmGeneration;
    boolean bluetoothConnected = true;//蓝牙是否连接

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_link_bluetooth);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.add_fingerprint);
//        btnConfirmGeneration.setOnClickListener(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (bluetoothConnected) {
                    intent = new Intent(FingerprintLinkBluetoothActivity.this, FingerprintCollectionActivity.class);
                } else {
                    intent = new Intent(FingerprintLinkBluetoothActivity.this, FingerprintNoConnectBluetoothOneActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 3000);
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
}
