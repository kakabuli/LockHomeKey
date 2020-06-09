package com.kaadas.lock.activity.addDevice.singleswitch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kaadas.lock.R;
public class SwipLinkFailActivity extends AppCompatActivity {
    TextView tv_content;
    Button btn_next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swip_link_fail);

        tv_content = findViewById(R.id.tv_content);
        btn_next = findViewById(R.id.btn_next);

        tv_content.setText(getString(R.string.add_failed));
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(SwipLinkFailActivity.this,SwipLinkTwoActivity.class);

                startActivity(intent);

            }
        });
    }
}
