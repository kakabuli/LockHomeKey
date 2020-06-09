package com.kaadas.lock.activity.addDevice.singleswitch;


import com.kaadas.lock.R;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;


public class SwipchLinkNo extends AppCompatActivity {

    RelativeLayout btn_swipch_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipch_link_no);
        btn_swipch_ok = findViewById(R.id.btn_swipch_ok);
        btn_swipch_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
  Intent intent=new Intent(SwipchLinkNo.this,SwipchLinkOne. class);
  startActivity(intent);


            }
        });
    }
}
