package com.kaadas.lock.activity.device.gatewaylock.password.old;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.kaadas.lock.R;
import com.kaadas.lock.fragment.old.GatewayLockPasswordForeverFragment;
import com.kaadas.lock.fragment.old.GatewayLockPasswrodTempararyFragment;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GatewayLockPasswordAddActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.sliding_tab_layout)
    SlidingTabLayout slidingTabLayout;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    private GatewayLockPasswordAddActivity.ListFragmentPagerAdapter mPagerAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();


    private String[] tabs;
    private List<String> lockPwdList=new ArrayList<>();
    private String gatewayId;
    private String deviceId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_password_add);
        ButterKnife.bind(this);
        initView();
        initListener();
        initViewPager();

    }

    private void initListener() {
        ivBack.setOnClickListener(this);
    }

    private void initView() {
        Intent intent=getIntent();
        lockPwdList= (List<String>) intent.getSerializableExtra(KeyConstants.LOCK_PWD_LIST);
        gatewayId=intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId=intent.getStringExtra(KeyConstants.DEVICE_ID);
        if (lockPwdList!=null){
            LogUtils.e(lockPwdList.size()+"密码集合大小   ");
        }
        tvContent.setText(getString(R.string.pwd_header_add_tv));
        tabs = getResources().getStringArray(R.array.gateway_lock_pwd_top);
    }


    /**
     * 初始化ViewPager控件
     */
    private void initViewPager() {
            viewPager.setOffscreenPageLimit(2);
            GatewayLockPasswordForeverFragment foreverFragment=new GatewayLockPasswordForeverFragment();
            Bundle foreverBundle = new Bundle();
            foreverBundle.putSerializable(KeyConstants.LOCK_PWD_LIST,(Serializable) lockPwdList);
            foreverBundle.putString(KeyConstants.GATEWAY_ID,gatewayId);
            foreverBundle.putString(KeyConstants.DEVICE_ID,deviceId);
            foreverFragment.setArguments(foreverBundle);
            mFragments.add(foreverFragment);

            GatewayLockPasswrodTempararyFragment tempararyFragment= new GatewayLockPasswrodTempararyFragment();
            Bundle tempararyBundle = new Bundle();
            tempararyBundle.putSerializable(KeyConstants.LOCK_PWD_LIST,(Serializable) lockPwdList);
            tempararyBundle.putString(KeyConstants.GATEWAY_ID,gatewayId);
            tempararyBundle.putString(KeyConstants.DEVICE_ID,deviceId);
            tempararyFragment.setArguments(tempararyBundle);
            mFragments.add(tempararyFragment);

            //适配器
            mPagerAdapter = new GatewayLockPasswordAddActivity.ListFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
            viewPager.setAdapter(mPagerAdapter);
            slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    public class ListFragmentPagerAdapter extends FragmentPagerAdapter {
        private static final int TAB_COUNT = 2;

        private List<Fragment> mFragmentList;


        public ListFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.mFragmentList = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return (mFragmentList == null || mFragmentList.size() < TAB_COUNT) ? null : mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList == null ? 0 : mFragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabs == null || tabs.length == 0 ? "" : tabs[position];
        }

    }
}

