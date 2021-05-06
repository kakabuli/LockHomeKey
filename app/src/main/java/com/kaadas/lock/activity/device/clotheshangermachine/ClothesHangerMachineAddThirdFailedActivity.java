package com.kaadas.lock.activity.device.clotheshangermachine;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClothesHangerMachineAddThirdFailedActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_next)
    TextView button_next;

    private String wifiModelType = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_hanger_machine_add_third_failed);
        ButterKnife.bind(this);

        wifiModelType = getIntent().getStringExtra("wifiModelType");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.back,R.id.button_next})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.button_next:
                Intent clothesMachineIntent = new Intent(this, ClothesHangerMachineAddFirstActivity.class);
                clothesMachineIntent.putExtra("wifiModelType",wifiModelType);
                startActivity(clothesMachineIntent);
                finish();
                break;

        }
    }

}
