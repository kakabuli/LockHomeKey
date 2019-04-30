package com.kaadas.lock.activity.cateye;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MediaPlayerActivity extends AppCompatActivity {
    @BindView(R.id.btn_play)
    ImageButton playBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        ButterKnife.bind(this);
    }
}
