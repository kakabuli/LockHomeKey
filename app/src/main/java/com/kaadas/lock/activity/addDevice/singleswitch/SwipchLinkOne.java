package com.kaadas.lock.activity.addDevice.singleswitch;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;


public class SwipchLinkOne extends BaseAddToApplicationActivity implements View.OnClickListener{

    TextView   tv_content;
    Button btn_next;
    ImageView swich_link_one_center,iv_back;

    private String wifiSn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipch_link_one);
        tv_content = findViewById(R.id.tv_content);
        tv_content.setText(getString(R.string.swipch_link_add));
        btn_next = findViewById(R.id.btn_next);
        iv_back = findViewById(R.id.iv_back);
        swich_link_one_center = findViewById(R.id.swich_link_one_center);
        AnimationDrawable animationDrawable = (AnimationDrawable) swich_link_one_center.getBackground();
        animationDrawable.start();

        btn_next.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        initData();

    }

    private void initData() {

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.btn_next:

                Intent intent = new Intent(SwipchLinkOne.this, SwipLinkTwoActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                startActivity(intent);

                break;


            case R.id.iv_back:
                finish();
                break;

        }
    }
}
