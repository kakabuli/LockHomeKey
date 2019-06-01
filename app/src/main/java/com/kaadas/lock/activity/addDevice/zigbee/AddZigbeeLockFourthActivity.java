package com.kaadas.lock.activity.addDevice.zigbee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceBindGatewayListActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.AddZigbeeLockPresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IAddZigbeeLockView;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.utils.KeyConstants;
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
    private String gatewayId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_zigbeelock_add_scan);
        ButterKnife.bind(this);
        initAnimation();
        startAnimation();


    }

    private void initData() {
       gatewayId= (String) SPUtils.getProtect(Constants.GATEWAYID,"");
        if (!TextUtils.isEmpty(gatewayId)){
            LogUtils.e("允许设备入网的网关id",gatewayId);
            mPresenter.openJoinAllow(gatewayId);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();

    }

    @Override
    protected AddZigbeeLockPresenter<IAddZigbeeLockView> createPresent() {
        return new AddZigbeeLockPresenter<>();
    }


    @OnClick(R.id.back)
    public void onViewClicked() {
        Intent backIntent=new Intent(this, DeviceBindGatewayListActivity.class);
        startActivity(backIntent);
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
        mPresenter.deviceZigbeeIsOnLine(gatewayId);

    }

    @Override
    public void netInFail() {
        Intent failIntent=new Intent(this,AddZigbeeLockFailActivity.class);
        startActivity(failIntent);
        finish();
        LogUtils.e("设备入网失败");
    }

    @Override
    public void netInThrowable() {
        Intent failIntent=new Intent(this,AddZigbeeLockFailActivity.class);
        startActivity(failIntent);
        finish();
        LogUtils.e("设备入网异常");
    }

    @Override
    public void addZigbeeSuccess(DeviceOnLineBean deviceOnLineBean) {
        stopAnimation();
        String deviceId=deviceOnLineBean.getDeviceId();
        String gwId=deviceOnLineBean.getGwId();
        Intent successIntent=new Intent(this,AddZigbeeLockSuccessActivity.class);
        successIntent.putExtra(KeyConstants.GATEWAY_ID,gwId);
        successIntent.putExtra(KeyConstants.DEVICE_ID,deviceId);
        //清除锁上的密码

        startActivity(successIntent);
        finish();
        LogUtils.e("设备添加成功");
    }

    @Override
    public void addZigbeeFail() {
        Intent failIntent=new Intent(this,AddZigbeeLockFailActivity.class);
        startActivity(failIntent);
        finish();
        LogUtils.e("设备添加异常");

    }

    @Override
    public void addZigbeeThrowable() {
        Intent failIntent=new Intent(this,AddZigbeeLockFailActivity.class);
        startActivity(failIntent);
        finish();
        LogUtils.e("设备添加异常");
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAnimation();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return isCosumenBackKey();
        }
        return false;
    }

    private boolean isCosumenBackKey() {
        Intent backIntent=new Intent(this, DeviceBindGatewayListActivity.class);
        startActivity(backIntent);
        finish();
        return true;
    }

}
