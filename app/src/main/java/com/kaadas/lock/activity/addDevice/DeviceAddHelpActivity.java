package com.kaadas.lock.activity.addDevice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceAddHelpActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_help);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }
}
