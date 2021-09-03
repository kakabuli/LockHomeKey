package com.kaadas.lock.activity.cateye;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordingPreviewActivity extends BaseAddToApplicationActivity implements  View.OnClickListener{

    @BindView(R.id.recording_preview_img)
    PhotoView recording_preview_img;
    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.tv_content)
    TextView tv_content;

    String path=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_preview);
        ButterKnife.bind(this);
        tv_content.setText(getResources().getString(R.string.screen_img));
        path =getIntent().getStringExtra("path");
        // 启用图片缩放功能
        recording_preview_img.enable();

        iv_back.setOnClickListener(this);

        File imgPathFile=new File(path);

        if(imgPathFile.exists()){
            Glide.with(this).load(path).into(recording_preview_img);
        }

    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
