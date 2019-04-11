package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
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

    private Animation operatingAnim;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_cateye_add_third);
        ButterKnife.bind(this);
        initAnimation();
        startAnimation();
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

    /**
     * 初始化动画
     */
    private void initAnimation(){
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.device_zigbeelock);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
    }


    /**
     * 启动搜索图片的动画
     */
    private void startAnimation() {
        if (operatingAnim != null) {
            addCateyeAwating.startAnimation(operatingAnim);
        } else {
            addCateyeAwating.setAnimation(operatingAnim);
            addCateyeAwating.startAnimation(operatingAnim);
        }
    }

    /**
     * 停止动画
     *
     * @param
     */
    private void stopAnimation() {
        addCateyeAwating.clearAnimation();
    }

}
