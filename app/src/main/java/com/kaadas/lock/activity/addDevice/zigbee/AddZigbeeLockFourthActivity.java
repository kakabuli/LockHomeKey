package com.kaadas.lock.activity.addDevice.zigbee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.AddZigbeeLockPresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IAddZigbeeLockView;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.handPwdUtil.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddZigbeeLockFourthActivity extends BaseActivity<IAddZigbeeLockView,AddZigbeeLockPresenter<IAddZigbeeLockView>> implements IAddZigbeeLockView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.add_cateye_awating)
    ImageView addCateyeAwating;

    private Animation operatingAnim;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_zigbeelock_add_scan);
        ButterKnife.bind(this);
        initAnimation();
        startAnimation();
        initData();

    }

    private void initData() {
        String gatewayId= (String) SPUtils.getProtect(Constants.GATEWAYID,"");
        if (!TextUtils.isEmpty(gatewayId)){
            LogUtils.e("允许设备入网的网关id",gatewayId);
            mPresenter.openJoinAllow(gatewayId);
        }
    }


    @Override
    protected AddZigbeeLockPresenter<IAddZigbeeLockView> createPresent() {
        return new AddZigbeeLockPresenter<>();
    }


    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
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


    @Override
    public void netInSuccess() {
        //入网成功
        stopAnimation();
        Intent successIntent=new Intent(this,AddZigbeeLockSuccessActivity.class);
        startActivity(successIntent);
        LogUtils.e("设备入网成功");
    }

    @Override
    public void netInFail() {
        stopAnimation();
        Intent failIntent=new Intent(this,AddZigbeeLockFailActivity.class);
        startActivity(failIntent);
        LogUtils.e("设备入网失败");
    }

    @Override
    public void netInThrowable() {
        LogUtils.e("设备入网异常");
    }
}
