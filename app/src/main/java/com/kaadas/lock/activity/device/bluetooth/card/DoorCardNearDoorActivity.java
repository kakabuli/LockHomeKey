package com.kaadas.lock.activity.device.bluetooth.card;

import android.os.Bundle;
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
public class DoorCardNearDoorActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.btn)
    Button btn;
    int bluetoothConnectStatus=-1;
    int bluetoothConnectSuccess=1;
    int bluetoothConnectFail=2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_card_near_door);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.add_door_card);
    }
    public void changeBluetoothStatus(){
        if (bluetoothConnectStatus==bluetoothConnectSuccess){
            btn.setClickable(true);
            btn.setOnClickListener(this);
            btn.setBackgroundResource(R.drawable.retangle_1f96f7_22);
            btn.setText(R.string.connect_success);
            btn.setVisibility(View.VISIBLE);
        }else if (bluetoothConnectStatus==bluetoothConnectFail){
            btn.setClickable(true);
            btn.setOnClickListener(this);
            btn.setBackgroundResource(R.drawable.retangle_ff3b30_22);
            btn.setText(R.string.connect_fail);
            btn.setVisibility(View.GONE);
        }else {
            btn.setClickable(false);
            btn.setVisibility(View.GONE);
        }
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
