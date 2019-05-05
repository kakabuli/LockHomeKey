package com.kaadas.lock.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.fragment.DeviceFragment;
import com.kaadas.lock.fragment.HomePageFragment;
import com.kaadas.lock.fragment.PersonalCenterFragment;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.MainActivityPresenter;
import com.kaadas.lock.mvp.view.IMainActivityView;
import com.kaadas.lock.mvp.view.IMainView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.PermissionUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.widget.NoScrollViewPager;

import net.sdvn.cmapi.CMAPI;
import net.sdvn.cmapi.ConnectionService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseBleActivity<IMainActivityView, MainActivityPresenter<IMainActivityView>>
        implements ViewPager.OnPageChangeListener, IMainActivityView, RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.rb_one)
    RadioButton rbOne;
    @BindView(R.id.rb_two)
    RadioButton rbTwo;
    @BindView(R.id.rb_three)
    RadioButton rbThree;

    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.home_view_pager)
    NoScrollViewPager homeViewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private boolean isOnBackground = false;
    private static MainActivity instance;
    private static final int REQUEST_CODE_VPN_SERVICE = 11;

    public boolean isSelectHome = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        PermissionUtil.getInstance().requestPermission(PermissionUtil.getInstance().permission, this);
        rg.setOnCheckedChangeListener(this);
        if (MyApplication.getInstance().getMqttService().getMqttClient() == null || !MyApplication.getInstance().getMqttService().getMqttClient().isConnected()) {
            MyApplication.getInstance().getMqttService().mqttConnection(); //连接mqtt
        }
        fragments.add(new HomePageFragment());
        fragments.add(new DeviceFragment());
        fragments.add(new PersonalCenterFragment());

        instance = this;

        homeViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });

        //首页的fragment不重新加载，导致各种问题
        homeViewPager.setOffscreenPageLimit(fragments.size());

        checkVpnService();

        //设置Linphone的监听

        mPresenter.initLinphone();

    }

    @Override
    protected MainActivityPresenter<IMainActivityView> createPresent() {
        return new MainActivityPresenter();
    }

    public interface HomeSelectListener {
        void onHomeSelectChange(boolean isSelect);
    }

    private List<HomeSelectListener> listeners = new ArrayList<>();

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_one:
                homeViewPager.setCurrentItem(0);
                break;
            case R.id.rb_two:
                homeViewPager.setCurrentItem(1);
                break;
            case R.id.rb_three:
                homeViewPager.setCurrentItem(2);
                break;
        }
    }


    public boolean isOnBackground() {
        return isOnBackground;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isOnBackground = false;
    }

    public static final boolean isInstanciated() {
        return instance != null;
    }


    public static final MainActivity instance() {
        if (instance != null)
            return instance;
        throw new RuntimeException("LinphoneActivity not instantiated yet");
    }


    //检查vpn授权
    public void checkVpnService() {
        Intent prepare = ConnectionService.prepare(this);
        boolean resultvpn = prepare == null ? true : false;
        net.sdvn.cmapi.util.LogUtils.d(resultvpn + " 已授权 " + " 未授权 ");
        if (prepare != null) {
            startActivityForResult(prepare, REQUEST_CODE_VPN_SERVICE);
        } else {
            onActivityResult(REQUEST_CODE_VPN_SERVICE, RESULT_OK, null);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  vpn  授权

        if (requestCode == REQUEST_CODE_VPN_SERVICE) {
            CMAPI.getInstance().onVpnPrepareResult(requestCode, resultCode);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        if (i == 0) {
            isSelectHome = true;
            for (HomeSelectListener listener : listeners) {
                listener.onHomeSelectChange(true);
            }
        } else {
            isSelectHome = false;
            for (HomeSelectListener listener : listeners) {
                listener.onHomeSelectChange(false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


    @Override
    public void onWarringUp(String warringContent) {
        ToastUtil.getInstance().showLong(warringContent);
    }

    @Override
    public void onDeviceInBoot(BleLockInfo bleLockInfo) {
        //todo ota 处理
  /*      Intent intent = new Intent();
        intent.setClass(MainActivity.this, OTADialogActivity.class);
        intent.putExtra(KeyConstants.BLE_DEVICE_INFO, bleLockInfo);
        startActivity(intent);*/
    }
}
