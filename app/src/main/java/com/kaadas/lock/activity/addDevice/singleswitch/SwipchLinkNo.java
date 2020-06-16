package com.kaadas.lock.activity.addDevice.singleswitch;


import com.kaadas.lock.R;
import com.kaadas.lock.utils.KeyConstants;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SwipchLinkNo extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_swipch_ok)
    RelativeLayout btn_swipch_ok;
    @BindView(R.id.back)
    ImageView back;

    private String wifiSn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipch_link_no);
        ButterKnife.bind(this);

        btn_swipch_ok.setOnClickListener(this);
        back.setOnClickListener(this);

        initData();

    }

    private void initData() {

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);

    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_swipch_ok:
                Intent intent = new Intent(SwipchLinkNo.this, SwipchLinkOne.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                startActivity(intent);
                break;

        }

    }
}

