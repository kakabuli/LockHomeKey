package com.kaadas.lock.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.fragment.DeviceFragment;
import com.kaadas.lock.fragment.HomePageFragment;
import com.kaadas.lock.fragment.PersonalCenterFragment;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.MainPresenter;
import com.kaadas.lock.mvp.view.IMainView;
import com.kaadas.lock.utils.PermissionUtil;
import com.kaadas.lock.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity<IMainView, MainPresenter<IMainView>> implements RadioGroup.OnCheckedChangeListener, IMainView {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        PermissionUtil.getInstance().requestPermission(PermissionUtil.getInstance().permission, this);
        rg.setOnCheckedChangeListener(this);
        MyApplication.getInstance().getMqttService().mqttConnection();//连接mqtt

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
    }

    @Override
    protected MainPresenter<IMainView> createPresent() {
        return new MainPresenter<>();
    }


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

}
