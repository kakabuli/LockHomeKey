package com.kaadas.lock.activity.device.bluetooth.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/18
 */
public class DoorCardNoConnectThreeActivity extends BaseAddToApplicationActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.btn_finish)
    Button btnFinish;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_card_no_connect_three);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        tvContent.setText(R.string.add_door_card);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_finish:
                Intent intent = new Intent(this, DoorCardManagerActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
