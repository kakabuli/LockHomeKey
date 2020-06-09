package com.kaadas.lock.activity.addDevice.singleswitch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
public class SwipLinkSucActivity extends AppCompatActivity {
    TextView tv_content,swipch_link_join_tv;
    Button btn_next;
    ImageView swipch_link_join_img;
    LinearLayout swipch_link_ll_1,swipch_link_ll_2,swipch_link_ll_3;
    int  link=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swip_link_suc);

        tv_content = findViewById(R.id.tv_content);
        btn_next = findViewById(R.id.btn_next);
        swipch_link_join_img = findViewById(R.id.swipch_link_join_img);
        swipch_link_join_tv = findViewById(R.id.swipch_link_join_tv);
        swipch_link_ll_1 = findViewById(R.id.swipch_link_ll_1);
        swipch_link_ll_2 = findViewById(R.id.swipch_link_ll_2);
        swipch_link_ll_3 = findViewById(R.id.swipch_link_ll_3);

        tv_content.setText(getString(R.string.add_success));
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(SwipLinkSucActivity.this,SwipLinkTwoActivity.class);

                startActivity(intent);

            }
        });
        chageLink(1);

    }


    private void chageLink(int id){
        switch (link){
            case 1:
                swipch_link_join_tv.setText(getString(R.string.swipch_link_join_network_suc1));
                swipch_link_join_img.setBackgroundResource(R.mipmap.swipch_link_join_network_suc1);
                swipch_link_ll_2.setVisibility(View.GONE);
                swipch_link_ll_3.setVisibility(View.GONE);
                break;
            case 2:
                swipch_link_join_tv.setText(getString(R.string.swipch_link_join_network_suc2));
                swipch_link_join_img.setBackgroundResource(R.mipmap.swipch_link_join_network_suc2);
                swipch_link_ll_3.setVisibility(View.GONE);
                break;
            case 3:
                swipch_link_join_tv.setText(getString(R.string.swipch_link_join_network_suc3));
                swipch_link_join_img.setBackgroundResource(R.mipmap.swipch_link_join_network_suc3);
                break;
        }
    }
}
