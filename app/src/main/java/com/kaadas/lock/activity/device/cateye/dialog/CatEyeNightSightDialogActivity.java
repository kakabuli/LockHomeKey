package com.kaadas.lock.activity.device.cateye.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
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
import com.kaadas.lock.mvp.presenter.NightSightPresenter;
import com.kaadas.lock.mvp.view.GatewayOTAView;
import com.kaadas.lock.mvp.view.personalview.NightSightView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.CatEyeInfoBeanPropertyResultUpdate;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GatewayOtaNotifyBean;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.ftp.GeTui;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CatEyeNightSightDialogActivity extends BaseActivity<NightSightView, NightSightPresenter<NightSightView>> implements NightSightView ,RadioGroup.OnCheckedChangeListener {



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

    ArrayList<String> valuse=null;
    private String gatewayId;
    private String deviceId;
    private LoadingDialog loadingDialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_night_sight);
        ButterKnife.bind(this);
        night_sighth_rg.setOnCheckedChangeListener(this);
        loadingDialog = LoadingDialog.getInstance(this);
        initData();
        initView();

    }

    @Override
    protected NightSightPresenter<NightSightView> createPresent() {
        return new NightSightPresenter();
    }

    private void initData() {
        Intent intent=getIntent();
   //     notifyBean= (GatewayOtaNotifyBean) intent.getSerializableExtra(KeyConstants.GATEWAY_OTA_UPGRADE);
        //20-70,50-100,70-120
        valuse= intent.getStringArrayListExtra(KeyConstants.GATEWAY_NIGHT_SIGHT);
        gatewayId= intent.getStringExtra(Constants.GATEWAYID);
        deviceId =  intent.getStringExtra(Constants.DEVICE_ID);
        Log.e(GeTui.VideoLog,valuse+"");
        if(valuse!=null){
            String[] values_str= valuse.get(0).split(",");
            String values0=values_str[0];
            String values1= values_str[1];
            String values2=values_str[2];

            if(values0.equals("0")){
            }else if(values0.equals("1")){
                // 手动切换白天
                auto_hand_ck.setChecked(true);

            }else if(values0.equals("2")){
                // 手动切换黑夜
                auto_hand_night_ck.setChecked(true);
            }else {

            }



            // 自动模式
            // 20-70,50-100,70-120
            if(values1.equals("120") && values2.equals("70")){
                night_sight_hight_ck.setChecked(true);
            }else if(values1.equals("100") && values2.equals("50")){
                night_sight_center_ck.setChecked(true);
            }else if(values1.equals("70") && values2.equals("20")){
                night_sight_low_ck.setChecked(true);
            }

        }else {

        }
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

    int autoValues=-1;
    String updateValues=null;
    List<String> valuesList=new ArrayList<>();
    @OnClick({R.id.tv_left, R.id.tv_right, R.id.auto_hand_ck,R.id.auto_hand_night_ck,R.id.night_sight_hight_ck,R.id.night_sight_center_ck,R.id.night_sight_low_ck})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
                 finish();
                break;
            case R.id.tv_right:

                if(auto_hand_ck.isChecked()){
                   updateValues="1,";
                }else if(auto_hand_night_ck.isChecked()){
                    updateValues="2,";
                }else {
                    updateValues="0,";
                }

                // 20-70,50-100,70-120
                if(night_sight_hight_ck.isChecked()){
                    updateValues=updateValues+"120,70";
                }else if(night_sight_center_ck.isChecked()){
                    updateValues=updateValues+"100,50";
                }else if(night_sight_low_ck.isChecked()){
                    updateValues=updateValues+"70,20";
                }

          //      Log.e("denganzhi1","updateValues:"+updateValues);

                valuesList.clear();
                valuesList.add(updateValues);

                if(valuse.toString().equals(valuesList.toString())){  //没有选
                     finish();
                     return;
                }

                if (!NetUtil.isNetworkAvailable()) {
                    ToastUtil.getInstance().showShort(getString(R.string.network_exception));
                    finish();
                    return;
                }

                if(!auto_hand_ck.isChecked() && !auto_hand_night_ck.isChecked()
                        && !night_sight_hight_ck.isChecked() && !night_sight_center_ck.isChecked()
                        && !night_sight_low_ck.isChecked()){
                    Toast.makeText(CatEyeNightSightDialogActivity.this, getString(R.string.update_nightsight_invalid),Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }


                loadingDialog.show(getString(R.string.update_wating));
                mPresenter.updateCatNightSightInfo(gatewayId,deviceId,MyApplication.getInstance().getUid(),valuesList);
              //  finish();

                break;
            case R.id.auto_hand_ck:   //白天 主动
                auto_hand_night_ck.setChecked(false);

                break;
            case R.id.auto_hand_night_ck: // 黑夜 主动
                auto_hand_ck.setChecked(false);

                break;
            case R.id.night_sight_hight_ck:  // 自动高
                if(!night_sight_hight_ck.isChecked()){
                    night_sight_hight_ck.setChecked(true);
                }
                night_sight_center_ck.setChecked(false);
                night_sight_low_ck.setChecked(false);

                break;
            case R.id.night_sight_center_ck:  // 自动中
                if(!night_sight_center_ck.isChecked()){
                    night_sight_center_ck.setChecked(true);
                }
                night_sight_hight_ck.setChecked(false);
                night_sight_low_ck.setChecked(false);

                break;
            case R.id.night_sight_low_ck:  // 自动低
                if(!night_sight_low_ck.isChecked()){
                    night_sight_low_ck.setChecked(true);
                }
                night_sight_hight_ck.setChecked(false);
                night_sight_center_ck.setChecked(false);

                break;
        }
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

    @Override
    public void updateNightSightSuccess(CatEyeInfoBeanPropertyResultUpdate catEyeInfoBeanPropertyResultUpdate) {
      Toast.makeText(CatEyeNightSightDialogActivity.this,getString(R.string.update_nightsight_suc),Toast.LENGTH_SHORT).show();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        SPUtils.put(deviceId+Constants.NIGHT_SIGHT,valuesList.get(0).toString());
        finish();
    }

    @Override
    public void updateNightSightFail() {
        Toast.makeText(CatEyeNightSightDialogActivity.this,getString(R.string.update_nightsight_fail),Toast.LENGTH_SHORT).show();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        finish();
    }

    @Override
    public void updateNightSighEveThrowable(Throwable throwable) {
        Toast.makeText(CatEyeNightSightDialogActivity.this,getString(R.string.update_nightsight_exc),Toast.LENGTH_SHORT).show();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
