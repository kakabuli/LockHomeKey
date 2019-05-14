package com.kaadas.lock.activity.device.cateye.more;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SmartEyeActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.iv_smart_monitor)
    ImageView iv_smart_monitor;
    @BindView(R.id.rl_smart_linger)
    RelativeLayout rl_smart_linger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_eye);
        ButterKnife.bind(this);
        iv_smart_monitor.setOnClickListener(this);
        rl_smart_linger.setOnClickListener(this);
        tv_content.setText(getString(R.string.smart_monitor));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_smart_monitor:
                iv_smart_monitor.setBackgroundResource(R.mipmap.iv_open);
                break;
            case R.id.rl_smart_linger:
                Intent intent=new Intent(SmartEyeActivity.this,CateDefaultActivity.class);
                startActivity(intent);
                break;
        }
    }
}
