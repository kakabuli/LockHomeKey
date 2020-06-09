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


public class SwipchLinkOne extends AppCompatActivity {

    TextView   tv_content;
    Button btn_next;
    ImageView swich_link_one_center;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipch_link_one);
        tv_content = findViewById(R.id.tv_content);
        btn_next = findViewById(R.id.btn_next);
        swich_link_one_center = findViewById(R.id.swich_link_one_center);
        AnimationDrawable animationDrawable= (AnimationDrawable) swich_link_one_center.getBackground();
        animationDrawable.start();

        tv_content.setText(getString(R.string.swipch_link_add));
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(SwipchLinkOne.this,SwipLinkTwoActivity.class);

                startActivity(intent);

            }
        });
    }
}
