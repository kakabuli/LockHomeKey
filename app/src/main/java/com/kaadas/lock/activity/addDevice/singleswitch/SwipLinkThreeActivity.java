package com.kaadas.lock.activity.addDevice.singleswitch;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.kaadas.lock.R;
public class SwipLinkThreeActivity extends AppCompatActivity {
    TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swip_link_three);
        tv_content = findViewById(R.id.tv_content);


        tv_content.setText(getString(R.string.swipch_link_join_network_three_title_text));

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
}
