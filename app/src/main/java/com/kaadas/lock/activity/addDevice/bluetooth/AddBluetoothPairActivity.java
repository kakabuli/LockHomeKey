package com.kaadas.lock.activity.addDevice.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddBluetoothPairActivity extends AppCompatActivity {
    @BindView(R.id.cancel)
    ImageView cancel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_bluetooth_pair);
        ButterKnife.bind(this);

    }

    //配对结果,成功直接跳转到相应的页面，失败也跳转到不同的页面
    private void pairResult(Boolean flag) {
        if (flag) {
            //成功
            Intent succeessIntent = new Intent(this, AddBluetoothSuccessActivity.class);
            startActivity(succeessIntent);
        } else {
            Intent failIntent = new Intent(this, AddBluetoothPairFailActivity.class);
            startActivity(failIntent);
        }

    }


    @OnClick({R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                break;
        }
    }
}
