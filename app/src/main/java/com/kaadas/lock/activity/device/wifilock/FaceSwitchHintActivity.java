package com.kaadas.lock.activity.device.wifilock;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

/**
 * author : zhangjierui
 * time   : 2022/1/15
 * desc   : 人脸识别开关操作提示
 */
public class FaceSwitchHintActivity extends BaseAddToApplicationActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_face_switch_hint_layout);
        TextView titleView = findViewById(R.id.tv_content);
        titleView.setText(getString(R.string.face_recognition_switch));
        findViewById(R.id.iv_back).setOnClickListener((view)->{
            finish();
        });
    }
}
