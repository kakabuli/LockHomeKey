package com.kaadas.lock.activity.cateye;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.preview_img)
    PhotoView preview_img;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.btn_play_video)
    Button btn_play_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        tv_content.setText(getResources().getString(R.string.snapshot));
        btn_play_video.setOnClickListener(this);

        // 启用图片缩放功能
        preview_img.enable();

        iv_back.setOnClickListener(this);

        Glide.with(this).load(R.mipmap.pre_video_image).into(preview_img);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_play_video:
                Intent video_play_intent=new Intent(PreviewActivity.this,MediaPlayerActivity.class);
                startActivity(video_play_intent);
                break;
        }
    }
}
