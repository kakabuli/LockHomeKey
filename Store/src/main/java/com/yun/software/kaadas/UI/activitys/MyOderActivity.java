package com.yun.software.kaadas.UI.activitys;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.adapter.AAAAListAdapter;
import com.yun.software.kaadas.UI.adapter.BaseMainFragmentAdapter;
import com.yun.software.kaadas.UI.adapter.LabelsAdapter;
import com.yun.software.kaadas.UI.adapter.OrderPopAdapter;
import com.yun.software.kaadas.UI.bean.HotkeyBean;
import com.yun.software.kaadas.UI.fragment.OrderStateFragment;
import com.yun.software.kaadas.UI.fragment.SaleAfterListFragment;
import com.yun.software.kaadas.UI.view.ColorFlipPagerTitleView;
import com.yun.software.kaadas.Utils.MessageEvent;
import com.yun.software.kaadas.Utils.OrderStatue;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.SizeUtils;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;


/**
 * Created by yanliang
 * on 2018/9/6 18:07
 */

public class MyOderActivity extends BaseActivity {

    private List<Fragment> fragments;
    private BaseMainFragmentAdapter mBaseMainFragmentAdapter;
    private String[] mTitles = new String[]{"全部", "待支付","待安装","待评价","退款/售后"};
    private List<String> mDataList = Arrays.asList(mTitles);
    @BindView(R2.id.magic_indicator_title)
    MagicIndicator magicIndicatorTitle;
    @BindView(R2.id.view_pager)
    ViewPager viewPager;

    @BindView(R2.id.tv_title)
    TextView tvTitle;

    @BindView(R2.id.iv_back)
    ImageView llBack;

    private  Bundle mbundle;
    private int page=0;

    private PopupWindow popupWindow;

    private List<HotkeyBean> labelList = new ArrayList<>();
    private LabelsAdapter labelsAdapter;
    private String searcherKey;

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        page=extras.getInt("page",0);
    }

    @Override
    protected View getLoadingTargetView() {
        return viewPager;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_my_oders;
    }
    @Override
    protected boolean isLoadDefaultTitleBar() {
        return false;
    }
    @Override
    protected void initViewsAndEvents() {
        toggleShowLoading(true,"正在加载");
        tvTitle.setText("我的订单");
        getLableData();
        initFragment();
        mBaseMainFragmentAdapter=new BaseMainFragmentAdapter(getSupportFragmentManager(), 5,fragments);
        mBaseMainFragmentAdapter.setDestroy(true);
        viewPager.setAdapter(mBaseMainFragmentAdapter);
        viewPager.setOffscreenPageLimit(1);
        initMagicIndicatorTitle();
        toggleRestore();

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    public void initFragment(){
        fragments=new ArrayList<>();
        mbundle=new Bundle();
        mbundle.putString("type", OrderStatue.INDENT_TYPE_BASE);

        mbundle.putString("statue","");
        fragments.add(OrderStateFragment.getInstance(mbundle));
        mbundle=null;
        mbundle=new Bundle();
        mbundle.putString("statue", OrderStatue.INDENT_STATUS_WAIT_PAY);
        mbundle.putString("type", OrderStatue.INDENT_TYPE_BASE);
        fragments.add(OrderStateFragment.getInstance(mbundle));
        mbundle=null;
        mbundle=new Bundle();
        mbundle.putString("statue",OrderStatue.INDENT_STATUS_WAIT_INSTALL);
        mbundle.putString("type", OrderStatue.INDENT_TYPE_BASE);
        fragments.add(OrderStateFragment.getInstance(mbundle));
        mbundle=null;
        mbundle=new Bundle();
        mbundle.putString("statue",OrderStatue.INDENT_STATUS_WAIT_COMMENT);
        mbundle.putString("type", OrderStatue.INDENT_TYPE_BASE);
        fragments.add(OrderStateFragment.getInstance(mbundle));


        fragments.add(new SaleAfterListFragment());

    }
    private int  getpage(){
//        return 0;

        return page;
    }
    private void initMagicIndicatorTitle() {
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setScrollPivotX(0.65f);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorFlipPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.color_999));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.color_333));
                simplePagerTitleView.setTextSize(16);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index, false);
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
                indicator.setColors(ContextCompat.getColor(mContext, R.color.light_blue));
                return indicator;
            }
        });
        magicIndicatorTitle.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicatorTitle, viewPager);
        //设置选中哪个页面
        viewPager.setCurrentItem(getpage());

    }

    private void getLableData() {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        HttpManager.getInstance().post(mContext, ApiConstants.COMMONAPP_GETALLKEYVALUE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                String keyjson = StringUtil.getJsonKeyStr(result,"indent_type");
                Gson gson = new Gson();
                List<HotkeyBean> list = gson.fromJson(keyjson,new TypeToken<List<HotkeyBean>>(){}.getType());
                labelList.clear();
                for (int i=0; i<list.size(); i++){
                    if (list.get(i).getValue().contains("普通")){
                        list.remove(i);
                        break;
                    }
                }
                labelList.addAll(list);
                labelList.get(0).setCheck(true);
                labelsAdapter.notifyDataSetChanged();
                searcherKey = labelList.get(0).getKey();

            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },false);
    }

}
