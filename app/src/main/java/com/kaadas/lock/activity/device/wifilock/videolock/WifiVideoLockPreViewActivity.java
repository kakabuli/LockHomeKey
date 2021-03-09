package com.kaadas.lock.activity.device.wifilock.videolock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoLockPreViewActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.preview_img)
    PhotoView preview_img;
    @BindView(R.id.back)
    ImageView iv_back;
    @BindView(R.id.tv_name)
    TextView tvName;

    private String stringExtra;

    /*@Override
    protected MyAlbumPlayerPresenter<IMyAlbumPlayerView> createPresent() {
        return new MyAlbumPlayerPresenter<>();
    }*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_preview);
        ButterKnife.bind(this);

        stringExtra = getIntent().getStringExtra(KeyConstants.VIDEO_PIC_PATH);
        String name = getIntent().getStringExtra("NAME");

        tvName.setText(name);
        // 启用图片缩放功能
        preview_img.enable();

        //.apply(new RequestOptions().transform(new RotateTransformation(90)))
        Glide.with(this).load(stringExtra).into(preview_img);
    }

    @OnClick({R.id.back,R.id.iv_myalbum_delete})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.iv_myalbum_delete:
                if(!stringExtra.isEmpty() && new File(stringExtra).exists()){
                    showDeleteDialog(stringExtra);
                }
                break;
        }
    }

    private void showDeleteDialog(String filepath) {
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                WifiVideoLockPreViewActivity.this
                , getString(R.string.activity_wifi_video_preview_delete),
                getString(R.string.cancel), getString(R.string.confirm), "#A4A4A4", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        Intent intent = new Intent(WifiVideoLockPreViewActivity.this,WifiVideoLockAlbumActivity.class);
                        intent.putExtra(KeyConstants.VIDEO_PIC_PATH,stringExtra);
                        intent.putExtra("NAME",tvName.getText().toString());
                        setResult(RESULT_OK,intent);
                        finish();
                        if(!filepath.isEmpty()){
                            if(new File(filepath).exists()){
                                new File(filepath).delete();
                            }
                        }
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }
}
