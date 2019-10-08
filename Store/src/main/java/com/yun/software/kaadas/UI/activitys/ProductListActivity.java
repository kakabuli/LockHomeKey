package com.yun.software.kaadas.UI.activitys;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.adapter.BaseMainFragmentAdapter;
import com.yun.software.kaadas.UI.bean.CategoryBean;
import com.yun.software.kaadas.UI.bean.HomeCategoriesBean;
import com.yun.software.kaadas.UI.fragment.ProductListFragment;
import com.yun.software.kaadas.UI.view.parger.titles.ScaleTransitionPagerTitleView;
import com.yun.software.kaadas.Utils.UserUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import la.xiong.androidquick.tool.ToastUtil;

public class ProductListActivity extends BaseActivity {
    private List<Fragment> fragments;
    @BindView(R2.id.magic_indicator_title)
    MagicIndicator magicIndicatorTitle;
    private BaseMainFragmentAdapter mBaseMainFragmentAdapter;

    @BindView(R2.id.viewPager)
    public ViewPager mViewPager;


    private List<CategoryBean> list = new ArrayList<>();

    private List<Fragment> fragmentList ;
    private List<String> listtitles=new ArrayList<>();

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_list_product_new;
    }
    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }
    @Override
    protected void initViewsAndEvents() {
        HomeCategoriesBean bean = getIntent().getParcelableExtra("bean");
        tvTitle.setText(bean.getName());
        getData(bean);


    }
    public void initFragment(){
        fragmentList = new ArrayList<>();
        for (CategoryBean bean:list){
            ProductListFragment fragment = new ProductListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id",bean.getId());
            fragment.setArguments(bundle);
            fragmentList.add(fragment);
            listtitles.add(bean.getName());
        }
        mBaseMainFragmentAdapter=new BaseMainFragmentAdapter(getSupportFragmentManager(), listtitles.size(),fragmentList);
        mBaseMainFragmentAdapter.setDestroy(true);
        mViewPager.setAdapter(mBaseMainFragmentAdapter);
        mViewPager.setOffscreenPageLimit(1);
        initMagicIndicatorTitle();
    }

    private void initMagicIndicatorTitle() {
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setScrollPivotX(0.8f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return listtitles == null ? 0 : listtitles.size();
            }
            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(listtitles.get(index));
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
                indicator.setLineWidth(UIUtil.dip2px(context, 20));
                indicator.setRoundRadius(UIUtil.dip2px(context, 3));
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

//    private class PagerAdapter extends FragmentStatePagerAdapter {
//
//        List<Fragment> listf;
//
//
//        public PagerAdapter(FragmentManager fragmentManager, List<Fragment> list) {
//            super(fragmentManager);
//            this.listf = list;
//
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return listf.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return list.size();
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return list.get(position).getName();
//        }
//    }




    private void getData(HomeCategoriesBean bean) {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("id",bean.getId());
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.SYSCATEGORY_GETCHILDLIST, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                Gson gson = new Gson();
                list = gson.fromJson(result,new TypeToken<List<CategoryBean>>(){}.getType());
                initFragment();
            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);


            }
        },false);
    }



}
