package com.kaadas.lock.activity.device.wifilock.videolock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.mvp.presenter.wifilock.MyAlbumPlayerPresenter;
import com.kaadas.lock.mvp.view.wifilock.IMyAlbumPlayerView;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.RotateTransformation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockVideoPreViewActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.preview_img)
    PhotoView preview_img;
    @BindView(R.id.back)
    ImageView iv_back;
    @BindView(R.id.tv_name)
    TextView tvName;


    /*@Override
    protected MyAlbumPlayerPresenter<IMyAlbumPlayerView> createPresent() {
        return new MyAlbumPlayerPresenter<>();
    }*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_preview);
        ButterKnife.bind(this);

        String stringExtra = getIntent().getStringExtra(KeyConstants.VIDEO_PIC_PATH);
        String name = getIntent().getStringExtra("NAME");

        tvName.setText(name);
        // 启用图片缩放功能
        preview_img.enable();

        //.apply(new RequestOptions().transform(new RotateTransformation(90)))
        Glide.with(this).load(stringExtra).into(preview_img);
    }

    @OnClick({R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }
}
