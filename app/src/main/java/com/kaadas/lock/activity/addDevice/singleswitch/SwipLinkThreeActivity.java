package com.kaadas.lock.activity.addDevice.singleswitch;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
public class SwipLinkThreeActivity extends AppCompatActivity implements View.OnClickListener{
    TextView tv_content;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swip_link_three);
        tv_content = findViewById(R.id.tv_content);
        tv_content.setText(getString(R.string.swipch_link_join_network_three_title_text));

        iv_back = findViewById(R.id.iv_back);
        tv_content.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 成功
                Intent intent=new Intent(SwipLinkThreeActivity.this,SwipLinkSucActivity.class);

                //失败
             //   Intent intent=new Intent(SwipLinkThreeActivity.this,SwipLinkFailActivity.class);
                startActivity(intent);

            }
        },3000);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case  R.id.btn_next:
//
//                Intent intent=new Intent(SwipLinkThreeActivity.this,SwipLinkSucActivity.class);
//
//                startActivity(intent);
//                break;


            case R.id.iv_back:
                finish();
                break;

        }
    }
}
