package com.kaadas.lock.activity.device.bluetooth.password;

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
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.fragment.PasswordPeriodFragment;
import com.kaadas.lock.fragment.PasswordTemporaryFragment;
import com.kaadas.lock.fragment.PasswordTimeFragment;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * Created by David
 */
public class BluetoothUserPasswordAddActivity extends BaseAddToApplicationActivity implements View.OnClickListener {


    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.sliding_tab_layout)
    SlidingTabLayout slidingTabLayout;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    private ListFragmentPagerAdapter mPagerAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();


    private String[] tabs;
    private BleLockInfo bleLockInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_user_password_add);
        ButterKnife.bind(this);
        bleLockInfo =MyApplication.getInstance().getBleService().getBleLockInfo();
        ivBack.setOnClickListener(this);
        tvContent.setText(getString(R.string.pwd_header_add_tv));
        tabs = getResources().getStringArray(R.array.home_top);
        initViewPager();
        slidingTabLayout.setViewPager(viewPager);
    }

    public BleLockInfo getLockInfo() {
        return bleLockInfo;
    }

    /**
     * 初始化ViewPager控件
     */
    private void initViewPager() {
        //关闭预加载，默认一次只加载一个Fragment
        //mViewPager.setOffscreenPageLimit(1);
        //添加Fragment
        // 这里可以从Activity中传递数据到Fragment中
        viewPager.setOffscreenPageLimit(3);
        mFragments.add(PasswordTimeFragment.newInstance());
        mFragments.add(PasswordPeriodFragment.newInstance());
        mFragments.add(PasswordTemporaryFragment.newInstance());

        //适配器
        mPagerAdapter = new ListFragmentPagerAdapter(getSupportFragmentManager(), mFragments);

        viewPager.setAdapter(mPagerAdapter);
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
