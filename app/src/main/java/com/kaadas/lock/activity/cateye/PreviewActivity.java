package com.kaadas.lock.activity.cateye;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreviewActivity extends AppCompatActivity {

    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.preview_img)
    PhotoView preview_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        tv_content.setText(getResources().getString(R.string.snapshot));

        // 启用图片缩放功能
        preview_img.enable();

        Glide.with(this).load(R.mipmap.pre_video_image).into(preview_img);
    }
}
