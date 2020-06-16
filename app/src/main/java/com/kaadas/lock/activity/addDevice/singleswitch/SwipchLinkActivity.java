package com.kaadas.lock.activity.addDevice.singleswitch;

import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.singlefireswitchpresenter.SingleFireSwitchSettingPresenter;
import com.kaadas.lock.mvp.view.singlefireswitchview.SingleFireSwitchView;
import com.kaadas.lock.publiclibrary.bean.SwitchNumberBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.AddSingleFireSwitchBean;
import com.kaadas.lock.utils.DensityUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SwipchLinkActivity extends BaseActivity<SingleFireSwitchView, SingleFireSwitchSettingPresenter<SingleFireSwitchView>> implements View.OnClickListener,SingleFireSwitchView {

    ImageView swipch_link_center_img,swipch_one_img,swipch_two_img,swipch_three_img;
    LinearLayout swipch_two,swipch_one,swipch_three;
    View swich_view_item_two;
    ImageView swipch_setting_btn,swich_link_link_switch,back;
    RelativeLayout tv_double_rl,three_double_rl;

    // 两次点击按钮之间的最小点击间隔时间(单位:ms)
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    // 最后一次点击的时间
    private long lastClickTime;


    private WifiLockInfo wifiLockInfo;
    private int SwitchNumber;
    private int switchEn;
    private boolean switchIsEn;
    private String wifiSn;
    private boolean switchOneIsEn;
    private boolean switchTwoIsEn;
    private boolean switchThreeIsEn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipch_link);

        swipch_link_center_img = findViewById(R.id.swipch_link_center_img);
        swipch_one = findViewById(R.id.swipch_one);
        swipch_two = findViewById(R.id.swipch_two);
        swich_link_link_switch = findViewById(R.id.swich_link_link_switch);

        swipch_one_img = findViewById(R.id.swipch_one_img);
        swipch_two_img = findViewById(R.id.swipch_two_img);
        swipch_three_img = findViewById(R.id.swipch_three_img);

        swipch_three = findViewById(R.id.swipch_three);
        swich_view_item_two = findViewById(R.id.swich_view_item_two);
        swipch_setting_btn = findViewById(R.id.swipch_setting_btn);

        tv_double_rl = findViewById(R.id.tv_double_rl);
        three_double_rl = findViewById(R.id.three_double_rl);
        back = findViewById(R.id.back);

        swipch_one.setOnClickListener(this);
        swipch_two.setOnClickListener(this);
        swipch_three.setOnClickListener(this);
        swipch_setting_btn.setOnClickListener(this);
        tv_double_rl.setOnClickListener(this);
        three_double_rl.setOnClickListener(this);
        swipch_link_center_img.setOnClickListener(this);
        swich_link_link_switch.setOnClickListener(this);
        back.setOnClickListener(this);
        swipch_one_img.setOnClickListener(this);
        swipch_two_img.setOnClickListener(this);
        swipch_three_img.setOnClickListener(this);

        initData();
        initRecycleview();

    }

    @Override
    protected SingleFireSwitchSettingPresenter<SingleFireSwitchView> createPresent() {
        return new SingleFireSwitchSettingPresenter<>();
    }

    private void initData() {

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        //使能开关
        switchEn = wifiLockInfo.getSingleFireSwitchInfo().getSwitchEn();
        if (switchEn != 0) {
            switchIsEn = true;
            swich_link_link_switch.setBackgroundResource(R.mipmap.swipperlinkleft_blue);
        } else {
            switchIsEn = false;
            swich_link_link_switch.setBackgroundResource(R.mipmap.swipperlinkleft_no);
        }

        //键位开关
        List<SwitchNumberBean> switchNumberList = wifiLockInfo.getSingleFireSwitchInfo().getSwitchNumber();
        for (SwitchNumberBean switchNumber : switchNumberList) {

            switch (switchNumber.getType()) {
                case 1://键位1开关
                    if (switchNumber.getWeek() == 0) {
                        switchOneIsEn = false;
                        swipch_one_img.setBackgroundResource(R.mipmap.swipperlinkleft_no);
                    } else {
                        switchOneIsEn = true;
                        swipch_one_img.setBackgroundResource(R.mipmap.swipperlinkleft_yes);
                    }
                    break;
                case 2://键位2开关
                    if (switchNumber.getWeek() == 0) {
                        switchTwoIsEn = false;
                        swipch_two_img.setBackgroundResource(R.mipmap.swipperlinkleft_no);
                    } else {
                        switchTwoIsEn = true;
                        swipch_two_img.setBackgroundResource(R.mipmap.swipperlinkleft_yes);
                    }
                    break;
                case 3://键位3开关
                    if (switchNumber.getWeek() == 0) {
                        switchThreeIsEn = false;
                        swipch_three_img.setBackgroundResource(R.mipmap.swipperlinkleft_no);
                    } else {
                        switchThreeIsEn = true;
                        swipch_three_img.setBackgroundResource(R.mipmap.swipperlinkleft_yes);
                    }
                    break;
            }

        }
    }
    private void initRecycleview() {

        SwitchNumber = getIntent().getIntExtra(KeyConstants.SWITCH_NUMBER,1);

        switch (SwitchNumber){
            case 1:
                swipch_link_center_img.setBackgroundResource(R.mipmap.swipch_link_one);
                swipch_two.setVisibility(View.GONE);
                swipch_three.setVisibility(View.GONE);
                break;
            case  2:
                swipch_link_center_img.setBackgroundResource(R.mipmap.swipch_link_two);
                swipch_three.setVisibility(View.GONE);
                swich_view_item_two.setVisibility(View.GONE);
                tv_double_rl.setVisibility(View.VISIBLE);
                three_double_rl.setVisibility(View.GONE);
                break;

            case  3:
                swipch_link_center_img.setBackgroundResource(R.mipmap.swipch_link_three);

                ViewGroup.LayoutParams mylayoutParam1 =  swipch_one_img.getLayoutParams();
                RelativeLayout.LayoutParams rllayoutParam1 =( RelativeLayout.LayoutParams) (mylayoutParam1);
                rllayoutParam1.addRule(RelativeLayout.ALIGN_PARENT_TOP);

                ViewGroup.LayoutParams mylayoutParam2 =  swipch_two_img.getLayoutParams();
                RelativeLayout.LayoutParams rllayoutParam2 =( RelativeLayout.LayoutParams) (mylayoutParam2);
                rllayoutParam2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rllayoutParam2.topMargin= DensityUtil.dip2px(this,10);

                ViewGroup.LayoutParams mylayoutParam3 =  swipch_three_img.getLayoutParams();
                RelativeLayout.LayoutParams rllayoutParam3 =( RelativeLayout.LayoutParams) (mylayoutParam3);
                rllayoutParam3.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rllayoutParam3.topMargin= DensityUtil.dip2px(this,10);

                if(mylayoutParam1 instanceof  RelativeLayout.LayoutParams){
                    // Log.e("denganzhi","AAAAAAAAA");
                    swipch_one_img.setLayoutParams(rllayoutParam1);
                    swipch_two_img.setLayoutParams(mylayoutParam2);
                    swipch_three_img.setLayoutParams(mylayoutParam3);
                }else if(mylayoutParam1 instanceof  LinearLayout.LayoutParams){
                    //  Log.e("denganzhi","bbbbbbbbbb");
                }
                break;
        }
    }
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
////        SwitchNumber = getIntent().getIntExtra(KeyConstants.SWITCH_NUMBER,1);
//    }
    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {// 两次点击的时间间隔大于最小限制时间，则触发点击事件
            lastClickTime = currentTime;
            // 这里触发点击事件
            switch (v.getId()) {

                case R.id.swipch_one:
                    Intent intent = new Intent(this, SwipchLinkSettingActivity.class);
                    startActivity(intent);
                    break;
                case R.id.swipch_two:
                    intent = new Intent(this, SwipchLinkSettingActivity.class);
                    startActivity(intent);
                    break;
                case R.id.swipch_three:
                    intent = new Intent(this, SwipchLinkSettingActivity.class);
                    startActivity(intent);
                    break;
                case R.id.swipch_one_img:
                    if (switchOneIsEn) {
                        switchOneIsEn = false;
                        swipch_one_img.setBackgroundResource(R.mipmap.swipperlinkleft_no);
                    } else {
                        switchOneIsEn = true;
                        swipch_one_img.setBackgroundResource(R.mipmap.swipperlinkleft_yes);
                    }
                    break;
                case R.id.swipch_two_img:
                    if (switchTwoIsEn) {
                        switchTwoIsEn = false;
                        swipch_two_img.setBackgroundResource(R.mipmap.swipperlinkleft_no);
                    } else {
                        switchTwoIsEn = true;
                        swipch_two_img.setBackgroundResource(R.mipmap.swipperlinkleft_yes);
                    }
                    break;
                case R.id.swipch_three_img:
                    if (switchThreeIsEn) {
                        switchThreeIsEn = false;
                        swipch_three_img.setBackgroundResource(R.mipmap.swipperlinkleft_no);
                    } else {
                        switchThreeIsEn = true;
                        swipch_three_img.setBackgroundResource(R.mipmap.swipperlinkleft_yes);
                    }
                    break;
                case R.id.swipch_setting_btn:
                    intent = new Intent(this, SwipchSeetingArgus.class);
                    intent.putExtra(KeyConstants.SWITCH_NUMBER, SwitchNumber);
                    intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                    startActivity(intent);
                    break;

                case R.id.swich_link_link_switch:

                    if (switchIsEn) {
                        switchIsEn = false;
                        swich_link_link_switch.setBackgroundResource(R.mipmap.swipperlinkleft_no);
                        WifiLockInfo wifiLockInfoChange = wifiLockInfo;
                        wifiLockInfoChange.getSingleFireSwitchInfo().setSwitchEn(0);
                        mPresenter.settingDevice(wifiLockInfoChange);

                    } else {
                        switchIsEn = true;
                        swich_link_link_switch.setBackgroundResource(R.mipmap.swipperlinkleft_blue);
                        WifiLockInfo wifiLockInfoChange = wifiLockInfo;
                        wifiLockInfoChange.getSingleFireSwitchInfo().setSwitchEn(1);
                        mPresenter.settingDevice(wifiLockInfoChange);
                    }
                    break;

                case R.id.back:
                    finish();
                    break;

            }
        }
    }

    @Override
    public void settingDeviceSuccess() {
        LogUtils.e("--kaadas--设置成功");

    }

    @Override
    public void settingDeviceFail() {
        LogUtils.e("--kaadas--设置失败");

    }

    @Override
    public void settingDeviceThrowable() {
        LogUtils.e("--kaadas--设置超时");

    }

    @Override
    public void gettingDeviceSuccess() {

    }

    @Override
    public void gettingDeviceFail() {

    }

    @Override
    public void gettingDeviceThrowable() {

    }

    @Override
    public void addDeviceSuccess(AddSingleFireSwitchBean addSingleFireSwitchBean) {
        LogUtils.e("--kaadas--添加成功");
    }

    @Override
    public void addDeviceFail() {
        LogUtils.e("--kaadas--添加失败");
    }

    @Override
    public void addDeviceThrowable() {
        LogUtils.e("--kaadas--添加超时");
    }

    @Override
    public void bindingDeviceSuccess() {

    }

    @Override
    public void bindingDeviceFail() {

    }

    @Override
    public void bindingDeviceThrowable() {

    }
}