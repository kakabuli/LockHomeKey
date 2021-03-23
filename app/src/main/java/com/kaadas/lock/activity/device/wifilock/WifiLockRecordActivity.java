package com.kaadas.lock.activity.device.wifilock;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.fragment.record.WifiLockAlarmRecordFragment;
import com.kaadas.lock.fragment.record.WifiLockOpenRecordFragment;
import com.kaadas.lock.fragment.record.WifiLockVistorRecordFragment;
import com.kaadas.lock.fragment.record.WifiVideoLockAlarmRecordFragment;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockRecordPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoRecordView;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiLockRecordActivity extends BaseActivity<IWifiLockVideoRecordView,
        WifiVideoLockRecordPresenter<IWifiLockVideoRecordView>> implements IWifiLockVideoRecordView,View.OnClickListener  {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_open_lock_record)
    TextView tvOpenLockRecord;
    @BindView(R.id.tv_warn_information)
    TextView tvWarnInformation;
    @BindView(R.id.tv_visitor_record)
    TextView tvVistorRecord;
    @BindView(R.id.vp_home)
    ViewPager viewPager;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    WifiLockOpenRecordFragment openRecordFragment;
    WifiLockAlarmRecordFragment alarmRecordFragment;
    WifiLockVistorRecordFragment vistorRecordFragment;
    WifiVideoLockAlarmRecordFragment videoLockAlarmRecordFragment;
    private String wifiSn;
    private boolean isVideoLock = false;
    private List<Fragment> fragments = new ArrayList<>();
    private FragmentPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_equipment_dynamic);
        LogUtils.e("是否支持操作记录   ");
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(getString(R.string.device_dynamic));
        tvOpenLockRecord.setOnClickListener(this);
        tvWarnInformation.setOnClickListener(this);
        tvVistorRecord.setOnClickListener(this);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
//        initFragment();

    }

    private void initData() {
        if(!wifiSn.isEmpty()){
            Bundle bundle = new Bundle();
            bundle.putString(KeyConstants.WIFI_SN,wifiSn);
            if(isVideoLock){
                openRecordFragment = new WifiLockOpenRecordFragment();
                openRecordFragment.setArguments(bundle);
                fragments.add(openRecordFragment);
                vistorRecordFragment = new WifiLockVistorRecordFragment();
                vistorRecordFragment.setArguments(bundle);
                fragments.add(vistorRecordFragment);
                videoLockAlarmRecordFragment = new WifiVideoLockAlarmRecordFragment();
                videoLockAlarmRecordFragment.setArguments(bundle);
                fragments.add(videoLockAlarmRecordFragment);
            }else {
                openRecordFragment = new WifiLockOpenRecordFragment();
                openRecordFragment.setArguments(bundle);
                fragments.add(openRecordFragment);
                alarmRecordFragment = new WifiLockAlarmRecordFragment();
                alarmRecordFragment.setArguments(bundle);
                fragments.add(alarmRecordFragment);
            }
        }

        if (adapter == null) {
            adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int i) {
                    return fragments.get(i);
                }

                @Override
                public long getItemId(int position) {
                    int hashCode = fragments.get(position).hashCode();
                    return hashCode;
                }

                @Override
                public int getItemPosition(@NonNull Object object) {
                    return POSITION_NONE;
                }

                @Override
                public int getCount() {
                    return fragments.size();
                }
            };
            viewPager.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(isVideoLock){
                    if(position == 0){
                        tvOpenLockRecord.setBackgroundResource(R.drawable.retangle_1f96f7_22);
                        tvOpenLockRecord.setTextColor(getResources().getColor(R.color.white));
                        tvVistorRecord.setBackgroundResource(0);
                        tvVistorRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                        tvWarnInformation.setTextColor(getResources().getColor(R.color.c1F96F7));
                        tvWarnInformation.setBackgroundResource(0);
                        tvWarnInformation.setTextColor(getResources().getColor(R.color.c1F96F7));
                    }else if(position == 1){
                        tvOpenLockRecord.setBackgroundResource(0);
                        tvOpenLockRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                        tvWarnInformation.setBackgroundResource(0);
                        tvWarnInformation.setTextColor(getResources().getColor(R.color.c1F96F7));
                        tvVistorRecord.setBackgroundResource(R.drawable.retangle_1f96f7_22);
                        tvVistorRecord.setTextColor(getResources().getColor(R.color.white));
                    }else{
                        tvOpenLockRecord.setBackgroundResource(0);
                        tvOpenLockRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                        tvVistorRecord.setBackgroundResource(0);
                        tvVistorRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                        tvWarnInformation.setBackgroundResource(R.drawable.retangle_1f96f7_22);
                        tvWarnInformation.setTextColor(getResources().getColor(R.color.white));
                    }
                }else{
                    if(position == 0){
                        tvOpenLockRecord.setBackgroundResource(R.drawable.retangle_1f96f7_22);
                        tvOpenLockRecord.setTextColor(getResources().getColor(R.color.white));
                        tvVistorRecord.setBackgroundResource(0);
                        tvVistorRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                        tvWarnInformation.setTextColor(getResources().getColor(R.color.c1F96F7));
                        tvWarnInformation.setBackgroundResource(0);
                        tvWarnInformation.setTextColor(getResources().getColor(R.color.c1F96F7));
                    }else{
                        tvOpenLockRecord.setBackgroundResource(0);
                        tvOpenLockRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                        tvVistorRecord.setBackgroundResource(0);
                        tvVistorRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                        tvWarnInformation.setBackgroundResource(R.drawable.retangle_1f96f7_22);
                        tvWarnInformation.setTextColor(getResources().getColor(R.color.white));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        if(MyApplication.getInstance().getWifiVideoLockTypeBySn(wifiSn) == HomeShowBean.TYPE_WIFI_VIDEO_LOCK){
            tvVistorRecord.setVisibility(View.VISIBLE);
            isVideoLock = true;
        }else{
            tvVistorRecord.setVisibility(View.GONE);
            isVideoLock = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.attachView(this);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        if(MyApplication.getInstance().getWifiVideoLockTypeBySn(wifiSn) == HomeShowBean.TYPE_WIFI_VIDEO_LOCK){
            tvVistorRecord.setVisibility(View.VISIBLE);
            isVideoLock = true;
        }else{
            tvVistorRecord.setVisibility(View.GONE);
            isVideoLock = false;
        }
        initView();
        initData();
        if(getIntent().getIntExtra(KeyConstants.WIFI_VIDEO_LOCK_WANDERING_ALARM_PIR_FLAG,0) == 1){
            if(isVideoLock){
                viewPager.setCurrentItem(2);
                tvOpenLockRecord.setBackgroundResource(0);
                tvOpenLockRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                tvVistorRecord.setBackgroundResource(0);
                tvVistorRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                tvWarnInformation.setBackgroundResource(R.drawable.retangle_1f96f7_22);
                tvWarnInformation.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.detachView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected WifiVideoLockRecordPresenter<IWifiLockVideoRecordView> createPresent() {
        return new WifiVideoLockRecordPresenter<>();
    }

    private void initFragment() {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        openRecordFragment = new WifiLockOpenRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KeyConstants.WIFI_SN,wifiSn);
        openRecordFragment.setArguments(bundle);
        transaction.add(R.id.content, openRecordFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction ;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_open_lock_record:
                //开锁记录
                tvOpenLockRecord.setBackgroundResource(R.drawable.retangle_1f96f7_22);
                tvOpenLockRecord.setTextColor(getResources().getColor(R.color.white));
                tvVistorRecord.setBackgroundResource(0);
                tvVistorRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                tvWarnInformation.setTextColor(getResources().getColor(R.color.c1F96F7));
                tvWarnInformation.setBackgroundResource(0);
                tvWarnInformation.setTextColor(getResources().getColor(R.color.c1F96F7));
                /*fragmentTransaction = manager.beginTransaction();
                hideAll(fragmentTransaction);

                if (openRecordFragment != null) {
                    fragmentTransaction.show(openRecordFragment);
                } else {
                    openRecordFragment = new WifiLockOpenRecordFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(KeyConstants.WIFI_SN,wifiSn);
                    openRecordFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.content, openRecordFragment);
                }
                fragmentTransaction.commit();*/
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_warn_information:
                //警告信息
                tvOpenLockRecord.setBackgroundResource(0);
                tvOpenLockRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                tvVistorRecord.setBackgroundResource(0);
                tvVistorRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                tvWarnInformation.setBackgroundResource(R.drawable.retangle_1f96f7_22);
                tvWarnInformation.setTextColor(getResources().getColor(R.color.white));
                /*fragmentTransaction = manager.beginTransaction();
                hideAll(fragmentTransaction);
                if(isVideoLock){
                    if (videoLockAlarmRecordFragment != null) {
                        fragmentTransaction.show(videoLockAlarmRecordFragment);
                    } else {
                        videoLockAlarmRecordFragment = new WifiVideoLockAlarmRecordFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(KeyConstants.WIFI_SN,wifiSn);
                        videoLockAlarmRecordFragment.setArguments(bundle);
                        fragmentTransaction.add(R.id.content, videoLockAlarmRecordFragment);
                    }
                }else{
                    if (alarmRecordFragment != null) {
                        fragmentTransaction.show(alarmRecordFragment);
                    } else {
                        alarmRecordFragment = new WifiLockAlarmRecordFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(KeyConstants.WIFI_SN,wifiSn);
                        alarmRecordFragment.setArguments(bundle);
                        fragmentTransaction.add(R.id.content, alarmRecordFragment);
                    }
                }
                fragmentTransaction.commit();*/
                if(isVideoLock){
                    viewPager.setCurrentItem(2);
                }else{
                    viewPager.setCurrentItem(1);
                }
                break;

            case R.id.tv_visitor_record:
                //访客记录
                tvOpenLockRecord.setBackgroundResource(0);
                tvOpenLockRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                tvWarnInformation.setBackgroundResource(0);
                tvWarnInformation.setTextColor(getResources().getColor(R.color.c1F96F7));
                tvVistorRecord.setBackgroundResource(R.drawable.retangle_1f96f7_22);
                tvVistorRecord.setTextColor(getResources().getColor(R.color.white));
               /* fragmentTransaction = manager.beginTransaction();
                hideAll(fragmentTransaction);
                if (vistorRecordFragment != null) {
                    fragmentTransaction.show(vistorRecordFragment);
                } else {
                    vistorRecordFragment = new WifiLockVistorRecordFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(KeyConstants.WIFI_SN,wifiSn);
                    vistorRecordFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.content, vistorRecordFragment);
                }
                fragmentTransaction.commit();*/
                viewPager.setCurrentItem(1);
                break;
        }
    }

    private void hideAll(FragmentTransaction ft) {

        if (ft == null) {
            return;
        }
        if (openRecordFragment != null) {
            ft.hide(openRecordFragment);
        }
        if (alarmRecordFragment != null) {
            ft.hide(alarmRecordFragment);
        }
        if(vistorRecordFragment != null){
            ft.hide(vistorRecordFragment);
        }
        if(videoLockAlarmRecordFragment != null){
            ft.hide(videoLockAlarmRecordFragment);
        }

    }

    @Override
    public void finish() {
        super.finish();
    }

}
