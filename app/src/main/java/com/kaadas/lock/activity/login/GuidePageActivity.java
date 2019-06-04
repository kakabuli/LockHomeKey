package com.kaadas.lock.activity.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.kaadas.lock.R;
import com.kaadas.lock.fragment.GuidePageOneFragment;
import com.kaadas.lock.fragment.GuidePageThreeFragment;
import com.kaadas.lock.fragment.GuidePageTwoFragment;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/15
 */
public class GuidePageActivity extends BaseAddToApplicationActivity implements ViewPager.OnPageChangeListener {
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_page);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        fragments = new ArrayList<Fragment>();
        fragments.add(new GuidePageOneFragment());
        fragments.add(new GuidePageTwoFragment());
        fragments.add(new GuidePageThreeFragment());
//        pager.setOffscreenPageLimit(3);

        viewPager.setAdapter(new FragmentPagerAdapter(this.getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return fragments.get(arg0);
            }
        });

        // 添加页面切换事件的监听器
        viewPager.setOnPageChangeListener(this);

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
