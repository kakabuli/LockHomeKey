package com.kaadas.lock.activity.cateye;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallComingActivity extends AppCompatActivity implements  View.OnClickListener {

    @BindView(R.id.call_coming_refuse_ll)
    LinearLayout call_coming_refuse_ll;
    @BindView(R.id.call_coming_answer_ll)
    LinearLayout call_coming_answer_ll;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_coming);
        ButterKnife.bind(this);
        call_coming_refuse_ll.setOnClickListener(this);
        call_coming_answer_ll.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 当状态栏设置为透明的时候,View渲染到状态栏
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //   getWindow().setStatusBarColor(getResources().getColor(R.color.red));
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


    }

    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.call_coming_refuse_ll:
                 finish();
                  break;
             case R.id.call_coming_answer_ll:
             Intent intent=new Intent(CallComingActivity.this,VideoVActivity.class);
             startActivity(intent);
                 break;
         }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
