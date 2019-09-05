package com.kaadas.lock.publiclibrary.ota.gatewayota;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.GatewayOTAPresenter;
import com.kaadas.lock.mvp.view.GatewayOTAView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GatewayOtaNotifyBean;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GatewayOTADialogActivity extends BaseActivity<GatewayOTAView, GatewayOTAPresenter<GatewayOTAView>> implements GatewayOTAView {

    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.tv_right)
    TextView tvRight;
    private  GatewayOtaNotifyBean notifyBean;
    @Override
    protected GatewayOTAPresenter<GatewayOTAView> createPresent() {
        return new GatewayOTAPresenter<>();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.e("网关ota升级通知 GatewayOTADialogActivity ");
        setContentView(R.layout.activity_otadialog);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        Intent intent=getIntent();
        notifyBean= (GatewayOtaNotifyBean) intent.getSerializableExtra(KeyConstants.GATEWAY_OTA_UPGRADE);
        if(notifyBean!=null){
            // 网关
            String swInfo= notifyBean.getParams().getSW();
            String deviceSn= notifyBean.getParams().getDeviceList().get(0).toString();
            String swInfoStr= String.format(getString(R.string.have_gateway_version),swInfo);
            if(deviceSn.startsWith("GW")){
                if(swInfo.startsWith("orangeiot")){

                    tvContent.setText(getString(R.string.gateway) + ":" + notifyBean.getDeviceId() + "\n" + swInfoStr);
                }
            }

            //zigbeen
            else if(deviceSn.startsWith("ZG")){
                tvContent.setText(getString(R.string.zigbeen_have_update) + ":" + notifyBean.getDeviceId() + "\n" + swInfoStr);
            }

            // znp
           else  if(deviceSn.startsWith("GW")){
                if(swInfo.startsWith("znpv")){

                    tvContent.setText(getString(R.string.gateway_zigbeen_have_update) + ":" + notifyBean.getDeviceId() + "\n" + swInfoStr);
                }
            }


            // 猫眼
           else  if(deviceSn.startsWith("CH")){
                tvContent.setText(getString(R.string.cateye) + ":" + notifyBean.getDeviceId() + "\n" + swInfoStr);
            }

            else{

               finish();
            }
        }else{
            finish();
        }

     //   if(notifyBean!=null){
            // tvContent.setText(getString(R.string.gateway) + ":" + notifyBean.getDeviceId() + "\n" + getString(R.string.have_new_gateway_version));
     //    }

    }

    private void initView() {
        if (tvHint!=null){
            tvHint.setVisibility(View.GONE);
          //  tvHint.setText(getString(R.string.gateway_ota_upgrade));
        }
        //设置窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.getDecorView().setScaleX((float) 0.96);
        win.getDecorView().setScaleY((float) 0.96);

        WindowManager.LayoutParams lp = win.getAttributes();
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = (int) (width*0.9);
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;//设置对话框置顶显示
        win.setAttributes(lp);
        //设置点击外部空白处可以关闭Activity
        this.setFinishOnTouchOutside(true);
    }

    @OnClick({R.id.tv_left, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
                 finish();
                break;
            case R.id.tv_right:
                if (notifyBean!=null){
                    mPresenter.confirmGatewayOtaUpgrade(notifyBean, MyApplication.getInstance().getUid());
                }

                break;
        }
    }

    @Override
    public void gatewayUpgradeingNow(String deviceId) {
        ToastUtil.getInstance().showShort(deviceId+getString(R.string.gateway_upgrade_now));
        finish();
    }

    @Override
    public void gatewayUpgradeFail(String deviceId) {
        ToastUtil.getInstance().showShort(deviceId+getString(R.string.gateway_upgade_fail));
        finish();
    }
}
