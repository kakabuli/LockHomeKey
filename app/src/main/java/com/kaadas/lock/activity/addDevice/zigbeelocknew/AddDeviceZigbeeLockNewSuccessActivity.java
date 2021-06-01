package com.kaadas.lock.activity.addDevice.zigbeelocknew;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceZigbeeLockNewSuccessActivity extends BaseAddToApplicationActivity {
    @BindView(R.id.button_next)
    Button buttonNext;
    @BindView(R.id.back)
    ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_add_zigbeenewlock_success);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button_next,R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
            case R.id.button_next:
                Intent backIntent = new Intent(this, MainActivity.class);
                startActivity(backIntent);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent finishIntent = new Intent(this, MainActivity.class);
            startActivity(finishIntent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
