package com.yun.software.kaadas.UI.activitys;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.adapter.BaseMainFragmentAdapter;
import com.yun.software.kaadas.UI.fragment.CouponNewFragment;
import com.yun.software.kaadas.UI.fragment.CouponOldFragment;
import com.yun.software.kaadas.UI.view.ColorFlipPagerTitleView;
import com.yun.software.kaadas.base.BaseActivity;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CouponActivity extends BaseActivity {

    @BindView(R2.id.magic_indicator_title)
    MagicIndicator magicIndicatorTitle;
    private BaseMainFragmentAdapter mBaseMainFragmentAdapter;
    @BindView(R2.id.view_pager)
    ViewPager mViewPager;


    private List<Fragment> fragmentList;

    private List<String> titleList ;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_coupon;
    }

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected void initViewsAndEvents() {
        tvTitle.setText("我的优惠券");


        String nouse = getIntent().getStringExtra("nouse");
        String overtime = getIntent().getStringExtra("overtime");

        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();

        titleList.add("未使用(" + nouse + ")");
        titleList.add("已过期(" + overtime +")");

        CouponNewFragment couponNewFragment = new CouponNewFragment();
        CouponOldFragment couponOverTimeFragment = new CouponOldFragment();


        fragmentList.add(couponNewFragment);
        fragmentList.add(couponOverTimeFragment);
        mBaseMainFragmentAdapter=new BaseMainFragmentAdapter(getSupportFragmentManager(), 2,fragmentList);
        mBaseMainFragmentAdapter.setDestroy(true);
        mViewPager.setAdapter(mBaseMainFragmentAdapter);
        mViewPager.setOffscreenPageLimit(1);
        initMagicIndicatorTitle();
//        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                TextView customView = (TextView) tab.getCustomView();
//                customView.getPaint().setFakeBoldText(true);
//                customView.setTextColor(Color.parseColor("#333333"));
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                TextView customView = (TextView) tab.getCustomView();
//                customView.getPaint().setFakeBoldText(false);
//                customView.setTextColor(Color.parseColor("#666666"));
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
    }
    private void initMagicIndicatorTitle() {
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setScrollPivotX(0.65f);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titleList == null ? 0 : titleList.size();
            }
            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorFlipPagerTitleView(context);
                simplePagerTitleView.setText(titleList.get(index));
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.color_999));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.color_333));
                simplePagerTitleView.setTextSize(16);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index, false);
                    }
                });
                return simplePagerTitleView;
            }
            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(context, 2));
                indicator.setLineWidth(UIUtil.dip2px(context, 30));
                indicator.setRoundRadius(UIUtil.dip2px(context, 1));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(ContextCompat.getColor(mContext, R.color.app_red));
                return indicator;
            }
        });
        magicIndicatorTitle.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicatorTitle, mViewPager);
        mViewPager.setCurrentItem(0);

    }

}
