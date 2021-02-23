package com.yun.software.kaadas.UI.activitys;

import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaadas.lock.store.R2;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.Tools.GlideImageLoader;
import com.yun.software.kaadas.UI.adapter.ApriseHomeListAdapter;
import com.yun.software.kaadas.UI.adapter.ShopImageListAdapter;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.CommentsBean;
import com.yun.software.kaadas.UI.bean.GoodsAttrBean;
import com.yun.software.kaadas.UI.bean.GoodsBean;
import com.yun.software.kaadas.UI.bean.ImgUrlBean;
import com.yun.software.kaadas.UI.bean.ProductParams;
import com.yun.software.kaadas.UI.fragment.ShopCartFragment;
import com.yun.software.kaadas.UI.view.GridItemDecoration;
import com.yun.software.kaadas.UI.view.IdeaScrollView;
import com.yun.software.kaadas.UI.view.TextFlowLayout;
import com.yun.software.kaadas.Utils.TimeUtil;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.iwgang.countdownview.CountdownView;
import cn.sharesdk.framework.Platform;
import org.greenrobot.eventbus.EventBus;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;

import static la.xiong.androidquick.http.HeaderInterceptor.MESSAGE_LOGINOUT;


public class ShopDetailsNewActivity extends BaseActivity {

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_ZHONGCHOU = 2;
    public static final int TYPE_PINTUAN = 3;
    public static final int TYPE_MIAOSHA = 4;
    public static final int TYPE_KANJIA = 5;

    @BindView(R2.id.ll_shop_top_show)
    LinearLayout llshopTopShowView;

    @BindView(R2.id.iv_back)
    ImageView ivBackView;

    @BindView(R2.id.layout_tab)
    TabLayout mTabLayout;

    @BindView(R2.id.rl_back_hide)
    RelativeLayout ivBackHideView;

    @BindView(R2.id.ll_normal)
    LinearLayout normalView;

    @BindView(R2.id.tv_kanjia)
    TextView kanjiaView;

    @BindView(R2.id.tv_zhongchou)
    TextView zhongchouView;

    @BindView(R2.id.rl_pintuan)
    RelativeLayout pintuanView;

    @BindView(R2.id.ll_miaosha)
    LinearLayout miaoshaView;


    @BindView(R2.id.tv_count)
    TextView tvCount;

    @BindView(R2.id.tv_price)
    TextView tvPrice;

    @BindView(R2.id.iv_collect)
    ImageView ivCollect;

    @BindView(R2.id.banner)
    Banner mBanner;

    @BindView(R2.id.text_flow_layout)
    TextFlowLayout textFlowLayout;


    @BindView(R2.id.layout_normal)
    LinearLayout layoutNormal;

    @BindView(R2.id.layout_pintuan)
    LinearLayout layoutPintuan;

    @BindView(R2.id.layout_pintuan_bottom)
    LinearLayout layoutPintuanBottom;

    @BindView(R2.id.layout_miaosha)
    LinearLayout layoutMiaosha;

    @BindView(R2.id.layout_coupon)
    LinearLayout layoutCoupon;

    @BindView(R2.id.layout_zhongchou)
    LinearLayout layoutZhongzhou;

    //正常
    @BindView(R2.id.normal_name)
    TextView normalNameView;

    @BindView(R2.id.normal_price)
    TextView normalPriceView;

    @BindView(R2.id.normal_old_price)
    TextView normalOldPriceView;

    @BindView(R2.id.normal_xiaoliang)
    TextView normalXiaoliangView;

    //众筹
    @BindView(R2.id.zhongchou_name)
    TextView zhongchouName;

    @BindView(R2.id.zhongchou_price)
    TextView zhongchouPrice;

    @BindView(R2.id.zhongchou_total)
    TextView zhongchouTotal;

    @BindView(R2.id.zhongchou_count)
    TextView zhongchouCount;

    @BindView(R2.id.zhongchou_percent)
    TextView zhongchouPercent;

    @BindView(R2.id.zhongchou_time)
    TextView zhongchouTime;

    @BindView(R2.id.zhongchou_progressbar)
    ProgressBar zhongchouProgressbar;

    //拼团
    @BindView(R2.id.pintuan_name)
    TextView pintuanName;

    @BindView(R2.id.pintuan_old_price)
    TextView pintuanOldPrice;

    @BindView(R2.id.pintuan_price)
    TextView pintuanPrice;

    @BindView(R2.id.pintuan_zhekou)
    TextView pintuanZhekou; //优惠折扣

    @BindView(R2.id.pintuan_count)
    TextView pintuanCount;//成团人数

    @BindView(R2.id.pintuan_total)
    TextView pintuanTotal;//已团件数

    //秒杀
    @BindView(R2.id.miaosha_name)
    TextView miaoshaName;

    @BindView(R2.id.miaosha_price)
    TextView miaoshaPrice;

    @BindView(R2.id.miaosha_old_price)
    TextView miaoshaOldPrice;

    @BindView(R2.id.miaosha_countdown)
    CountdownView miaoshaCountdown;

    @BindView(R2.id.miaosha_yuexiao)
    TextView miaoshaYuexiao;

    @BindView(R2.id.comment_count)
    TextView commtentCount;

    @BindView(R2.id.tv_product_attr)
    TextView tvProductAttr;

    @BindView(R2.id.rv_list)
    RecyclerView recyclerView;

    @BindView(R2.id.detail_list)
    RecyclerView detailListView;

    @BindView(R2.id.root_view)
    RelativeLayout rootView;

    @BindView(R2.id.empty_pinglun)
    LinearLayout emptyPinglun;

    @BindView(R2.id.tv_xianshi)
    TextView tvXianshiView;

    @BindView(R2.id.tv_over)
    TextView tvOverTimeView;

    @BindView(R2.id.ll_shoucang)
    LinearLayout shoucangView;

    @BindView(R2.id.tv_params)
    TextView tvParamsView;


    private IdeaScrollView ideaScrollView;
    private RelativeLayout headerParent;

    private ShopImageListAdapter detailAdapter;

    private boolean isNeedScrollTo = true;

    private List<String> titleList;

    private float alpha = 0;
    private int type;
    private String productId;
    private String statue;
    private String actid;

    private int selectAttr = 0;
    private GoodsBean bean;

    private List<GoodsAttrBean> listAttr;
    private List<CommentsBean> listBeans = new ArrayList<>();
    ApriseHomeListAdapter listAdapter;

    private String[] mLabels = {"选择商品", "支付开团", "分享好友", "成功(失败退款)"};
    private boolean miaoshaTimeOver = false;//秒杀时间是否到期

    @Override
    protected View getLoadingTargetView() {
        return rootView;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_shop_details_backup;
    }

    @Override
    protected boolean isApplySystemBarTint() {
        return false;
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        type = extras.getInt("type");
        productId = extras.getString("id");
        statue = extras.getString("statue");
        actid = extras.getString("actid");
        switch (type) {
            case TYPE_NORMAL:
                normalView.setVisibility(View.VISIBLE);
                shoucangView.setVisibility(View.VISIBLE);
                break;
            case TYPE_MIAOSHA:
                miaoshaView.setVisibility(View.VISIBLE);
                break;
            case TYPE_PINTUAN:
                pintuanView.setVisibility(View.VISIBLE);
                break;
            case TYPE_ZHONGCHOU:
                zhongchouView.setVisibility(View.VISIBLE);
                break;
            case TYPE_KANJIA:
                kanjiaView.setVisibility(View.VISIBLE);
                break;

        }

    }


    @Override
    protected void initViewsAndEvents() {

        titleList = new ArrayList<>();

        titleList.add("商品");
        titleList.add("评价");
        titleList.add("详情");

        mTabLayout.addTab(mTabLayout.newTab().setText(titleList.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titleList.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titleList.get(2)));
        mTabLayout.setSelectedTabIndicatorColor(mContext.getResources().getColor(R.color.light_blue));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (isNeedScrollTo) {
                    ideaScrollView.setPosition(position);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        listAdapter = new ApriseHomeListAdapter(listBeans);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //添加自定义分割线
        GridItemDecoration.Builder decoration = new GridItemDecoration.Builder(mContext);
        decoration.setColor(getResources().getColor(R.color.default_bg_color));
        decoration.setHorizontalSpan(getResources().getDimension(R.dimen.dp_2));
        recyclerView.addItemDecoration(decoration.build());
        recyclerView.setFocusableInTouchMode(false);
        recyclerView.setAdapter(listAdapter);

        initLabel();
        getProductDetail();

    }


    private void setShowAlpha(float alpha) {
        mTabLayout.setAlpha(alpha);
        llshopTopShowView.setAlpha(alpha);
        ivBackView.setAlpha(alpha);
    }

    private void setHideAlpha(float alpha) {
        ivBackHideView.setAlpha(alpha);
    }

    public int getMeasureHeight(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        return view.getMeasuredHeight();
    }

    @OnClick({R2.id.rl_back_show, R2.id.rl_back_hide, R2.id.tv_add_card, R2.id.tv_buy_now,
            R2.id.tv_zhongchou, R2.id.rl_pintuan, R2.id.ll_miaosha, R2.id.ll_shoucang, R2.id.ll_shopcart, R2.id.ll_kefu, R2.id.tv_kanjia})
    public void onViewClick(View view) {
        int i = view.getId();//返回
//收藏
//去购物车
        if (i == R.id.rl_back_hide || i == R.id.rl_back_show) {
            finish();

        } else if (i == R.id.tv_add_card || i == R.id.tv_buy_now || i == R.id.tv_zhongchou || i == R.id.rl_pintuan || i == R.id.ll_miaosha || i == R.id.tv_kanjia) {
            getProductAttr();

        } else if (i == R.id.ll_shoucang) {
            if (TextUtils.isEmpty(UserUtils.getToken())) {
//                readyGo(WxLoginActivity.class);
                EventBus.getDefault().post(new EventCenter(MESSAGE_LOGINOUT, "relogin"));
                return;
            }
            changeCollectStatue();

        } else if (i == R.id.ll_shopcart) {
            if (TextUtils.isEmpty(UserUtils.getToken())) {
//                readyGo(WxLoginActivity.class);
                EventBus.getDefault().post(new EventCenter(MESSAGE_LOGINOUT, "relogin"));
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("frompage", "activity");
            readyGo(ShopCartFragment.class, bundle);

        } else if (i == R.id.ll_kefu) {
            readyGo(KefuActivity.class);

        }
    }

    private void shareNetWork() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String, Object> params = new HashMap<>();
        params.put("type", Constans.SHARE_TYPE_PRODUCT);
        params.put("businessId", bean.getAgentProductId());
        map.put("params", params);

        HttpManager.getInstance().post(mContext, ApiConstants.SHARE_SAVE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {

            }

            @Override
            public void onFailed(String error) {

            }
        }, false);
    }

    /**
     * 获取商品属性
     */
    private void getParams() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String, String> params = new HashMap<>();
        params.put("id", bean.getProductId());
        map.put("params", params);
        HttpManager.getInstance().post(mContext, ApiConstants.PARAMETERAPP_GETPARAMETERAPP, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                Gson gson = new Gson();
                List<ProductParams> listAttr = gson.fromJson(result, new TypeToken<List<ProductParams>>() {
                }.getType());
                String text = "";
                for (ProductParams p : listAttr) {
                    text = text + p.getName() + " ";
                }
                tvParamsView.setText(text);

            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        }, false);
    }

    @Override
    protected void onEventComing(EventCenter eventCenter) {
        super.onEventComing(eventCenter);
        if (eventCenter.getEventCode() == Constans.MESSAGE_GOODS_ATTR) {
            selectAttr = (int) eventCenter.getData();
            tvProductAttr.setText(listAttr.get(selectAttr).getAttributeComboName());
        } else if (eventCenter.getEventCode() == Constans.MESSAGE_FINISH_SHOP_DETAILS) {
            finish();
        }
    }


    @OnClick({R2.id.ll_look_all, R2.id.ll_product_param, R2.id.ll_servers, R2.id.ll_product_attr})
    public void onViewCLick(View view) {
        Bundle bundle = new Bundle();
        int i = view.getId();//查看全部
//参数
//服务
//选择属性
        if (i == R.id.ll_look_all) {
            bundle.putParcelable("bean", bean);
            readyGo(CommentListActivity.class, bundle);

        } else if (i == R.id.ll_product_param) {
            bundle.putString("id", bean.getProductId());
            readyGo(ProductParamActivity.class, bundle);
            overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);


        } else if (i == R.id.ll_servers) {
            overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);
            readyGo(ProductSeviceActivity.class);


        } else if (i == R.id.ll_product_attr) {
            getProductAttr();


        }
    }


    // 初始化标签
    private void initLabel() {
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 30, 5, 10);// 设置边距
        for (int i = 0; i < mLabels.length; i++) {

            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.text_flow_layout_shop, null);
            TextView tvXuhaoView = linearLayout.findViewById(R.id.tv_xuhao);
            TextView tvStringView = linearLayout.findViewById(R.id.tv_string);
            View view = linearLayout.findViewById(R.id.view_);
            tvXuhaoView.setText(String.valueOf(i + 1));

            if (i == mLabels.length - 1) {
                view.setVisibility(View.GONE);
            }
            tvStringView.setText(mLabels[i]);
            textFlowLayout.addView(linearLayout, layoutParams);

        }
    }

    private void setView() {

        getPinglunData();
        List<String> imgs = new ArrayList<>();
        for (ImgUrlBean bean : bean.getBanners()) {
            imgs.add(bean.getImgUrl());
        }

        mBanner.setBannerStyle(BannerConfig.NOT_INDICATOR);
        mBanner.setDelayTime(5000);
        mBanner.setImages(imgs)
                .setImageLoader(new GlideImageLoader())
                .start();

        tvProductAttr.setText(bean.getAttributeComboName());

        detailAdapter = new ShopImageListAdapter(bean.getDetailImgs());
        detailListView.setHasFixedSize(true);
        detailListView.setLayoutManager(new LinearLayoutManager(mContext));
        detailListView.setAdapter(detailAdapter);

        switch (type) {
            case TYPE_KANJIA://砍价
            case TYPE_NORMAL://普通
                layoutNormal.setVisibility(View.VISIBLE);
                normalOldPriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
                normalNameView.setText(bean.getName());
                normalPriceView.setText(bean.getPrice());
                normalOldPriceView.setText("¥" + bean.getOldPrice());
                normalXiaoliangView.setText("月销  " + bean.getCountNum() + "件");
                break;
            case TYPE_MIAOSHA://秒杀
                layoutMiaosha.setVisibility(View.VISIBLE);
                miaoshaOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
                miaoshaName.setText(bean.getName());
                miaoshaPrice.setText(bean.getPrice());
                miaoshaOldPrice.setText("¥" + bean.getOldPrice());
                miaoshaYuexiao.setText("月销  " + bean.getCountNum() + "件");
//                Date endDate = TimeUtil.getDateByFormat(bean.getEndTime(),TimeUtil.dateFormatYMDHMS);
                long des = bean.getSecond();
                miaoshaCountdown.start(des);
                if (des < 0) {
                    tvXianshiView.setVisibility(View.GONE);
                    miaoshaCountdown.setVisibility(View.GONE);
                    tvOverTimeView.setVisibility(View.VISIBLE);
                    miaoshaTimeOver = true;
                }
                miaoshaCountdown.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                    @Override
                    public void onEnd(CountdownView cv) {
                        tvXianshiView.setVisibility(View.GONE);
                        miaoshaCountdown.setVisibility(View.GONE);
                        tvOverTimeView.setVisibility(View.VISIBLE);
                        miaoshaTimeOver = true;
                    }
                });

                break;
            case TYPE_PINTUAN://拼团
                layoutPintuan.setVisibility(View.VISIBLE);
//                layoutPintuanBottom.setVisibility(View.VISIBLE);
                pintuanOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
                pintuanOldPrice.setText("¥" + bean.getOldPrice());
                pintuanPrice.setText(bean.getPrice());
                pintuanName.setText(bean.getName());
                pintuanZhekou.setText(bean.getDiscount());
                pintuanCount.setText(bean.getMaxQty());
                pintuanTotal.setText(bean.getSaleQty());
                break;
            case TYPE_ZHONGCHOU: //众筹
                layoutZhongzhou.setVisibility(View.VISIBLE);
                zhongchouName.setText(bean.getName());
                zhongchouPrice.setText(bean.getPrice());
                zhongchouTotal.setText(bean.getSaleMoney());
                zhongchouCount.setText(bean.getSaleQty());
                zhongchouPercent.setText(bean.getPercentage() + "%");
                zhongchouProgressbar.setProgress(Integer.parseInt(bean.getPercentage()));
                long nowTime = System.currentTimeMillis();
                long endTime = TimeUtil.getDateByFormat(bean.getEndTime(), TimeUtil.dateFormatYMDHMS).getTime();
                int time = TimeUtil.getOffectHour(endTime, nowTime);
                zhongchouTime.setText(time + "");
                break;

        }


    }


    private void setScrollView() {
        headerParent = findViewById(R.id.headerParent);
        ideaScrollView = (IdeaScrollView) findViewById(R.id.ideaScrollView);

        Rect rectangle = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        ideaScrollView.setViewPager(null, getMeasureHeight(headerParent));
        setShowAlpha(0);

        View one = findViewById(R.id.one);
        View two = findViewById(R.id.two);
        ArrayList<Integer> araryDistance = new ArrayList<>();

        araryDistance.add(0);
        araryDistance.add(getMeasureHeight(one) - getMeasureHeight(headerParent));
        araryDistance.add(getMeasureHeight(one) + getMeasureHeight(two) - getMeasureHeight(headerParent));

        ideaScrollView.setArrayDistance(araryDistance);
        ideaScrollView.setOnScrollChangedColorListener(new IdeaScrollView.OnScrollChangedColorListener() {
            @Override
            public void onChanged(float percentage) {

                alpha = percentage > 0.8f ? 1.0f : percentage;
                setShowAlpha(alpha);
                setHideAlpha(1 - alpha);

            }

            @Override
            public void onChangedFirstColor(float percentage) {

            }

            @Override
            public void onChangedSecondColor(float percentage) {

            }
        });

        ideaScrollView.setOnSelectedIndicateChangedListener(new IdeaScrollView.OnSelectedIndicateChangedListener() {
            @Override
            public void onSelectedChanged(int position) {
                LogUtils.e(position);
                isNeedScrollTo = false;
                mTabLayout.getTabAt(position).select();
                isNeedScrollTo = true;
            }
        });
    }

    /**
     * 获取商品详情
     */
    private void getProductDetail() {
        toggleShowLoading(true, "正在加载");
        Map<String, Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String, String> params = new HashMap<>();
        params.put("id", productId);//productId
        if (type != TYPE_NORMAL) {
            params.put("status", statue);
            params.put("activityProductId", actid);
        }
        map.put("params", params);
        HttpManager.getInstance().post(mContext, ApiConstants.HOME_SHOPDETAIL, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                toggleRestore();
                Gson gson = new Gson();
                bean = gson.fromJson(result, GoodsBean.class);
                if (type == TYPE_PINTUAN) {
                    tvPrice.setText("¥" + bean.getPrice() + "/");
                    tvCount.setText(bean.getMaxQty() + "人团");
                }
                if ("1".equals(bean.getColloctFlag())) {
                    ivCollect.setImageResource(R.drawable.icon_collected_detail);
                } else {
                    ivCollect.setImageResource(R.drawable.icon_collect_detail);
                }
                setView();
                getParams();

            }

            @Override
            public void onFailed(String error) {
                toggleRestore();
                toggleShowEmptyImage(true, R.drawable.loading_missing_pages, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getProductDetail();
                        toggleRestore();
                    }
                });


            }
        }, false);
    }

    private void getPinglunData() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String, Object> page = new HashMap<>();
        page.put("pageNum", String.valueOf(1));
        page.put("pageSize", String.valueOf(1));
        map.put("page", page);
        Map<String, String> params = new HashMap<>();
        params.put("id", bean.getId());
        map.put("params", params);
        HttpManager.getInstance().post(mContext, ApiConstants.FRONT_COMMENT_PAGE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                Gson gson = new Gson();
                BaseBody<CommentsBean> baseBody = gson.fromJson(result, new TypeToken<BaseBody<CommentsBean>>() {
                }.getType());
                listBeans.addAll(baseBody.getRows());

                if (listBeans.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    emptyPinglun.setVisibility(View.VISIBLE);
                }

                listAdapter.notifyDataSetChanged();
                commtentCount.setText("商品评价(" + baseBody.getTotal() + ")");
                setScrollView();
            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        }, false);
    }


    /**
     * 获取商品属性
     */
    private void getProductAttr() {

        if (miaoshaTimeOver) {
            ToastUtil.showShort("秒杀商品已经过期");
            return;
        }

        if (type == TYPE_PINTUAN && "activity_product_end".equals(bean.getStatus())) {
            ToastUtil.showShort("活动已结束");
            return;

//            long nowTime = System.currentTimeMillis();
//            //2019-07-24 00:00:00
//            long endTime = TimeUtil.getDateByFormat(bean.getEndTime(),TimeUtil.dateFormatYMDHMS).getTime();
//            if (nowTime > endTime){
//                ToastUtil.showShort("活动已结束");
//                return;
//            }
        }


        Map<String, Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String, String> params = new HashMap<>();
        params.put("id", bean.getProductId());//productId
        params.put("activityProductId", bean.getActivityProductId());
        params.put("status", bean.getBusinessType());
        map.put("params", params);
        HttpManager.getInstance().post(mContext, ApiConstants.FRONT_PRODUCT_GETSKU, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                Gson gson = new Gson();
                listAttr = gson.fromJson(result, new TypeToken<List<GoodsAttrBean>>() {
                }.getType());
                GoodCarDialog dialog = GoodCarDialog.getInstance();
                dialog.setPriorityRefresh(new GoodCarDialog.Priority() {
                    @Override
                    public void refreshPriorityUI(String colorType, String price) {
                        normalPriceView.setText(price);
                        normalOldPriceView.setText(colorType);
                    }
                });
                dialog.buyDialog(getSupportFragmentManager(), mContext, listAttr, type, selectAttr, bean.getResidueNum(), getSelecttext());
            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
            }
        }, false);
    }

    private void changeCollectStatue() {
        if ("1".equals(bean.getColloctFlag())) {
            cancelCollect();
        } else {
            addCollect();
        }
    }

    private void addCollect() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String, String> params = new HashMap<>();
        params.put("agentProductId", bean.getId());
        map.put("params", params);
        HttpManager.getInstance().post(mContext, ApiConstants.COLLECTAPP_ADDCOLLECT, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                ToastUtil.showShort("添加收藏成功");
                bean.setColloctFlag("1");
                ivCollect.setImageResource(R.drawable.icon_collected_detail);
            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
            }
        }, true);
    }

    private void cancelCollect() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String, String> params = new HashMap<>();
        params.put("agentProductId", bean.getId());
        map.put("params", params);
        HttpManager.getInstance().post(mContext, ApiConstants.COLLECTAPP_CANCELCOLLECT, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                bean.setColloctFlag("0");
                ToastUtil.showShort("取消收藏成功");
                ivCollect.setImageResource(R.drawable.icon_collect_detail);
            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        }, true);

    }

    private String getSelecttext() {
        return tvProductAttr.getText().toString().trim();
    }

}
