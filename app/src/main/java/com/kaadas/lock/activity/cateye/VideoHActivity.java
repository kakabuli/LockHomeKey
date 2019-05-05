package com.kaadas.lock.activity.cateye;

import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoHActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.video_h_back)
    ImageView video_h_back;   // 返回
    @BindView(R.id.video_h_connecting_tv)
    TextView video_h_connecting_tv; // 连接中

    @BindView(R.id.video_h_play_time)
    TextView video_h_play_time;    // 接通时间

    @BindView(R.id.video_h_footer_action) //底部footer
    LinearLayout video_h_footer_action;
    @BindView(R.id.video_h_play_img) //呼叫按钮
    ImageView video_h_play_img;

    @BindView(R.id.video_h_screenshots)
    LinearLayout video_h_screenshots;
    @BindView(R.id.video_h_novoices)
    LinearLayout video_h_novoices;
    @BindView(R.id.video_h_hf)
    LinearLayout video_h_hf;
    @BindView(R.id.video_h_recording)
    LinearLayout video_h_recording;
    @BindView(R.id.playvideo_surface)
    View playvideo_surface;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_h);
        ButterKnife.bind(this);

        video_h_back.setOnClickListener(this);
        video_h_play_img.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.video_h_back:
                 finish();
                 break;
             case R.id.video_h_play_img:
                 video_h_connecting_tv.setVisibility(View.VISIBLE);
                 video_h_footer_action.setVisibility(View.VISIBLE);
                 video_h_footer_action.setBackgroundColor(getResources().getColor(R.color.transparent));
                 video_h_screenshots.setVisibility(View.GONE);
                 video_h_novoices.setVisibility(View.GONE);
                 video_h_hf.setVisibility(View.GONE);
                 video_h_recording.setVisibility(View.GONE);
                 video_h_play_img.setVisibility(View.GONE);
                 handler.postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         video_h_connecting_tv.setVisibility(View.GONE);
                         video_h_footer_action.setVisibility(View.VISIBLE);
                         video_h_footer_action.setBackgroundColor(Color.parseColor("#99000000"));
                         video_h_screenshots.setVisibility(View.VISIBLE);
                         video_h_novoices.setVisibility(View.VISIBLE);
                         video_h_hf.setVisibility(View.VISIBLE);
                         video_h_recording.setVisibility(View.VISIBLE);
                         playvideo_surface.setVisibility(View.VISIBLE);
                         video_h_play_time.setVisibility(View.VISIBLE);

                     }
                 },3000);
                 break;
         }
    }
}
