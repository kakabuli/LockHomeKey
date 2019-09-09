package com.kaadas.lock.activity.device.cateye.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class CatEyeNightSightDialogActivity extends BaseActivity<GatewayOTAView, GatewayOTAPresenter<GatewayOTAView>> implements GatewayOTAView ,RadioGroup.OnCheckedChangeListener {



    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.tv_right)
    TextView tvRight;
    private  GatewayOtaNotifyBean notifyBean;



    @BindView(R.id.night_sighth_rg)
    RadioGroup night_sighth_rg;         // 切换

    @BindView(R.id.auto_ll)
    LinearLayout auto_ll;  // 自动，高总低

    @BindView(R.id.hand_ll)
    LinearLayout hand_ll;  // 手动  白天黑夜

    @BindView(R.id.auto_hand_ck)
    CheckBox auto_hand_ck;

    @BindView(R.id.auto_hand_night_ck)
    CheckBox auto_hand_night_ck;

    @BindView(R.id.night_sight_hight_ck)
    CheckBox night_sight_hight_ck;

    @BindView(R.id.night_sight_center_ck)
    CheckBox night_sight_center_ck;


    @BindView(R.id.night_sight_low_ck)
    CheckBox night_sight_low_ck;





    @Override
    protected GatewayOTAPresenter<GatewayOTAView> createPresent() {
        return new GatewayOTAPresenter<>();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_night_sight);
        ButterKnife.bind(this);
        night_sighth_rg.setOnCheckedChangeListener(this);
        initData();
        initView();
    }

    private void initData() {
        Intent intent=getIntent();
        notifyBean= (GatewayOtaNotifyBean) intent.getSerializableExtra(KeyConstants.GATEWAY_OTA_UPGRADE);
    }

    private void initView() {

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

    @OnClick({R.id.tv_left, R.id.tv_right, R.id.auto_hand_ck,R.id.auto_hand_night_ck,R.id.night_sight_hight_ck,R.id.night_sight_center_ck,R.id.night_sight_low_ck})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
                 finish();
                break;
            case R.id.tv_right:
                 finish();

                break;
            case R.id.auto_hand_ck:   //白天 主动
                auto_hand_night_ck.setChecked(false);

                break;
            case R.id.auto_hand_night_ck: // 黑夜 主动
                auto_hand_ck.setChecked(false);

                break;
            case R.id.night_sight_hight_ck:  // 自动高

                night_sight_center_ck.setChecked(false);
                night_sight_low_ck.setChecked(false);

                break;
            case R.id.night_sight_center_ck:  // 自动中
                night_sight_hight_ck.setChecked(false);
                night_sight_low_ck.setChecked(false);

                break;
            case R.id.night_sight_low_ck:  // 自动低
                night_sight_hight_ck.setChecked(false);
                night_sight_center_ck.setChecked(false);

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

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkid) {
         switch (checkid){
             case  R.id.night_sighth_hand:  //手动，白天，黑夜，第一个
                 hand_ll.setVisibility(View.VISIBLE);
                 auto_ll.setVisibility(View.GONE);
                 break;

             case  R.id.night_sighth_auto:  // 自动
                 hand_ll.setVisibility(View.GONE);
                 auto_ll.setVisibility(View.VISIBLE);
                 break;
         }
    }
}
