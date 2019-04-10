package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeThirdActivity extends AppCompatActivity {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.add_cateye_awating)
    ImageView addCateyeAwating;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_cateye_add_third);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }

    //猫眼配置结果
    private void pariCatEyeResult(Boolean flag){
        if (flag){
            Intent successIntent=new Intent(this,AddDeviceCatEyeSuccessActivity.class);
            startActivity(successIntent);
        }else{
            Intent failIntent=new Intent(this,AddDeviceCatEyeFailActivity.class);
            startActivity(failIntent);
        }
    }
}
