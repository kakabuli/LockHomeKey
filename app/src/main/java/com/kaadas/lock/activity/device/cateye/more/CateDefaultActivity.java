package com.kaadas.lock.activity.device.cateye.more;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CateDefaultActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.iv_default_monitor)
    ImageView iv_default_monitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cate_default);
        ButterKnife.bind(this);
        tv_content.setText(getString(R.string.pir_default_tile));
        iv_default_monitor.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_default_monitor:
                iv_default_monitor.setBackgroundResource(R.mipmap.iv_open);
                break;
        }

    }
}
