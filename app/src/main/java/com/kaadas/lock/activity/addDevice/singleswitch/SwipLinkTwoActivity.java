package com.kaadas.lock.activity.addDevice.singleswitch;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
public class SwipLinkTwoActivity extends AppCompatActivity implements View.OnClickListener{
    TextView tv_content;
    Button btn_next;
    ImageView swich_link_two_img,iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swip_link_two);

        tv_content = findViewById(R.id.tv_content);
        tv_content.setText(getString(R.string.swipch_link_join_network_two_title_text));

        btn_next = findViewById(R.id.btn_next);
        iv_back = findViewById(R.id.iv_back);

        swich_link_two_img = findViewById(R.id.swich_link_two_img);
        AnimationDrawable animationDrawable = (AnimationDrawable) swich_link_two_img.getBackground();
        animationDrawable.start();
        btn_next.setOnClickListener(this);
        iv_back.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.btn_next:

                Intent intent=new Intent(SwipLinkTwoActivity.this,SwipLinkThreeActivity.class);

                startActivity(intent);
                break;


            case R.id.iv_back:
                finish();
                break;

        }
    }
}
