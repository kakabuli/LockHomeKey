package com.kaadas.lock.activity.addDevice.singleswitch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.WiFiLockDetailActivity;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.ButterKnife;

public class SwipLinkFailActivity extends AppCompatActivity implements View.OnClickListener{
    TextView tv_content,btn_cancel;
    Button btn_next;
    ImageView iv_back;

    private String wifiSn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swip_link_fail);
        ButterKnife.bind(this);

        tv_content = findViewById(R.id.tv_content);
        btn_next = findViewById(R.id.btn_next);
        iv_back = findViewById(R.id.iv_back);
        btn_cancel = findViewById(R.id.btn_cancel);
        tv_content.setText(getString(R.string.add_failed));

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);

        btn_next.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.btn_next:
            case R.id.iv_back:
                Intent intent=new Intent(SwipLinkFailActivity.this,SwipchLinkOne.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                startActivity(intent);
                break;
            case R.id.btn_cancel:
                intent=new Intent(SwipLinkFailActivity.this, WiFiLockDetailActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                startActivity(intent);
                break;
        }
    }
}