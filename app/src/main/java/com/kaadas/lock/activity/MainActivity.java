package com.kaadas.lock.activity;


import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.fragment.DeviceFragment;
import com.kaadas.lock.fragment.HomePageFragment;
import com.kaadas.lock.fragment.MyFragment;
import com.kaadas.lock.fragment.PersonalCenterFragment;
import com.kaadas.lock.mvp.presenter.MainPresenter;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.PermissionUtil;
import com.kaadas.lock.mvp.view.IMainView;

public class MainActivity extends BaseActivity<IMainView, MainPresenter<IMainView>> implements RadioGroup.OnCheckedChangeListener,IBaseView {
    @BindView(R.id.rb_one)
    RadioButton rbOne;
    @BindView(R.id.rb_two)
    RadioButton rbTwo;
    @BindView(R.id.rb_three)
    RadioButton rbThree;
    @BindView(R.id.content)
    FrameLayout flContent;
    @BindView(R.id.rg)
    RadioGroup rg;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private HomePageFragment homePageFragment;
    private DeviceFragment deviceFragment;
    PersonalCenterFragment personalCenterFragment;
    MyFragment myFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        PermissionUtil.getInstance().requestPermission(PermissionUtil.getInstance().permission,this);
        rg.setOnCheckedChangeListener(this);
        initFragment();
        LogUtils.e("attachView  " + ( MyApplication.getInstance().getMqttService() == null));
        MyApplication.getInstance().getMqttService().mqttConnection();//连接mqtt
        mPresenter.getPublishNotify();

    }

    @Override
    protected MainPresenter<IMainView> createPresent() {
        return new MainPresenter<>();
    }

    private void initFragment() {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        homePageFragment = new HomePageFragment();
        transaction.add(R.id.content, homePageFragment);
        transaction.commit();

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        switch (checkedId) {
            case R.id.rb_one:
                hideAll(fragmentTransaction);
                if (homePageFragment != null) {
                    fragmentTransaction.show(homePageFragment);
                } else {
                    homePageFragment = new HomePageFragment();
                    fragmentTransaction.add(R.id.content, homePageFragment);
                }
                fragmentTransaction.commit();
                break;
            case R.id.rb_two:
                hideAll(fragmentTransaction);
                if (deviceFragment != null) {
                    fragmentTransaction.show(deviceFragment);
                } else {
                    deviceFragment = new DeviceFragment();
                    fragmentTransaction.add(R.id.content, deviceFragment);
                }
                fragmentTransaction.commit();
                break;
            case R.id.rb_three:
                hideAll(fragmentTransaction);
                if (personalCenterFragment!=null){
                    fragmentTransaction.show(personalCenterFragment);
                }else {
                    personalCenterFragment = new PersonalCenterFragment();
                    fragmentTransaction.add(R.id.content, personalCenterFragment);
                }
                fragmentTransaction.commit();
                break;
//            case R.id.rb_three:
//                hideAll(fragmentTransaction);
//                if (myFragment != null) {
//                    fragmentTransaction.show(myFragment);
//                } else {
//                    myFragment = new MyFragment();
//                    fragmentTransaction.add(R.id.content, myFragment);
//                }
//                fragmentTransaction.commit();
//                break;
        }
    }

    private void hideAll(FragmentTransaction ft) {

        if (ft == null) {
            return;
        }
        if (homePageFragment != null) {
            ft.hide(homePageFragment);
        }
        if (deviceFragment != null) {
            ft.hide(deviceFragment);
        }
        if (personalCenterFragment != null) {
            ft.hide(personalCenterFragment);
        }

        if (myFragment != null) {
            ft.hide(myFragment);
        }

    }
}
