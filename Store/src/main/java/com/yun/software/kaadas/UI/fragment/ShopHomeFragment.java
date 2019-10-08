package com.yun.software.kaadas.UI.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.Tools.GlideImageLoader;
import com.yun.software.kaadas.UI.activitys.GoodCarDialog;
import com.yun.software.kaadas.UI.activitys.ProductParamActivity;
import com.yun.software.kaadas.UI.activitys.ProductSeviceActivity;
import com.yun.software.kaadas.UI.adapter.ApriseHomeListAdapter;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.CommentsBean;
import com.yun.software.kaadas.UI.bean.GoodsAttrBean;
import com.yun.software.kaadas.UI.bean.GoodsBean;
import com.yun.software.kaadas.UI.bean.ImgUrlBean;
import com.yun.software.kaadas.UI.view.GridItemDecoration;
import com.yun.software.kaadas.UI.view.JudgeNestedScrollView;
import com.yun.software.kaadas.UI.view.TextFlowLayout;
import com.yun.software.kaadas.Utils.TimeUtil;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.iwgang.countdownview.CountdownView;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;

import static com.yun.software.kaadas.UI.activitys.ShopDetailsNewActivity.TYPE_MIAOSHA;
import static com.yun.software.kaadas.UI.activitys.ShopDetailsNewActivity.TYPE_NORMAL;
import static com.yun.software.kaadas.UI.activitys.ShopDetailsNewActivity.TYPE_PINTUAN;
import static com.yun.software.kaadas.UI.activitys.ShopDetailsNewActivity.TYPE_ZHONGCHOU;

public class ShopHomeFragment extends BaseFragment {
    @BindView(R2.id.rv_list)
    RecyclerView recyclerView;

    @BindView(R2.id.scroll_view)
    JudgeNestedScrollView scrollView;

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



    private List<CommentsBean> listBeans = new ArrayList<>();
    ApriseHomeListAdapter listAdapter;

    private String[] mLabels = {"选择商品","支付开团","分享好友","成功(失败退款)"};
    private String productId;
    private int type;
    private String actid;
    private GoodsBean bean;
    private List<GoodsAttrBean> listAttr;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_shop_home;
    }




    @Override
    protected void initViewsAndEvents() {
        type = getArguments().getInt("type");
        productId = getArguments().getString("id");
        actid = getArguments().getString("actid");
        bean = getArguments().getParcelable("bean");
        List<String> imgs = new ArrayList<>();
        for (ImgUrlBean bean:bean.getBanners()){
            imgs.add(bean.getImgUrl());
        }

        mBanner.setBannerStyle(BannerConfig.NOT_INDICATOR);
        mBanner.setImages(imgs)
                .setImageLoader(new GlideImageLoader())
                .start();
        tvProductAttr.setText(bean.getAttributeComboName());
        switch (type){
            case TYPE_NORMAL:
                layoutNormal.setVisibility(View.VISIBLE);
                normalOldPriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
                normalNameView.setText(bean.getName());
                normalPriceView.setText("¥" +bean.getPrice());
                normalOldPriceView.setText("¥"+bean.getOldPrice());
                normalXiaoliangView.setText("月销  " +bean.getCountNum() + "件");
                break;
            case TYPE_MIAOSHA:
                layoutMiaosha.setVisibility(View.VISIBLE);
                miaoshaOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
                miaoshaName.setText(bean.getName());
                miaoshaPrice.setText("¥" +bean.getPrice());
                miaoshaOldPrice.setText("¥" +bean.getOldPrice());
                miaoshaYuexiao.setText("月销  " + bean.getCountNum() + "件");
//                Date endDate = TimeUtil.getDateByFormat(bean.getEndTime(),TimeUtil.dateFormatYMDHMS);
                long des = bean.getSecond();
                miaoshaCountdown.start(des);
                break;
            case TYPE_PINTUAN:
                layoutPintuan.setVisibility(View.VISIBLE);
                layoutPintuanBottom.setVisibility(View.VISIBLE);
                pintuanOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
                pintuanPrice.setText(bean.getPrice());
                pintuanName.setText(bean.getName());
                pintuanZhekou.setText(bean.getDiscount());
                pintuanCount.setText(bean.getMaxQty());
                pintuanTotal.setText(bean.getSaleQty());
                break;
            case TYPE_ZHONGCHOU:
                layoutZhongzhou.setVisibility(View.VISIBLE);
                zhongchouName.setText(bean.getName());
                zhongchouPrice.setText("¥" +bean.getPrice());
                zhongchouTotal.setText("¥" +bean.getSaleMoney());
                zhongchouCount.setText(bean.getSaleQty());
                zhongchouPercent.setText(bean.getPercentage() + "%");
                zhongchouProgressbar.setProgress(Integer.parseInt(bean.getPercentage()));
                long nowTime = System.currentTimeMillis();
                long endTime = TimeUtil.getDateByFormat(bean.getEndTime(),TimeUtil.dateFormatYMDHMS).getTime();
                int time = TimeUtil.getOffectHour(endTime,nowTime);
                zhongchouTime.setText(time +"");
                break;

        }

        initLabel();
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener(){

            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (linstener != null){
                    linstener.onScroll(scrollY);
                }
            }
        });

        mBanner.setBannerStyle(BannerConfig.NOT_INDICATOR);
        mBanner.setDelayTime(5000);
        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
//                readyGo(PublishTieziActivity.class);
//                bannerClick(position);
            }
        });


        listAdapter=new ApriseHomeListAdapter(listBeans);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //添加自定义分割线
        GridItemDecoration.Builder  decoration = new GridItemDecoration.Builder(mContext);
        decoration.setColor(getResources().getColor(R.color.default_bg_color));
        decoration.setHorizontalSpan(getResources().getDimension(R.dimen.dp_2));
        recyclerView.addItemDecoration(decoration.build());
        recyclerView.setFocusableInTouchMode(false);
        recyclerView.setAdapter(listAdapter);


        getPinglunData();
    }


    // 初始化标签
    private void initLabel() {
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 30, 5, 10);// 设置边距
        for (int i = 0; i < mLabels.length; i++) {

            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.text_flow_layout_shop,null);
            TextView tvXuhaoView = linearLayout.findViewById(R.id.tv_xuhao);
            TextView tvStringView = linearLayout.findViewById(R.id.tv_string);
            View view= linearLayout.findViewById(R.id.view_);
            tvXuhaoView.setText(String.valueOf(i + 1));

            if (i == mLabels.length - 1){
                view.setVisibility(View.GONE);
            }
            tvStringView.setText(mLabels[i]);
            textFlowLayout.addView(linearLayout, layoutParams);

        }
    }

    @OnClick({R2.id.ll_look_all,R2.id.ll_product_param,R2.id.ll_servers,R2.id.ll_product_attr})
    public void onViewCLick(View view ){
        Bundle bundle = new Bundle();
        int i = view.getId();//查看全部
//参数
//服务
//选择属性
        if (i == R.id.ll_look_all) {
            if (tabLinstener != null) {
                tabLinstener.onTabSelect(1);
            }

        } else if (i == R.id.ll_product_param) {
            bundle.putString("id", bean.getId());
            readyGo(ProductParamActivity.class, bundle);
            getActivity().overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);


        } else if (i == R.id.ll_servers) {
            getActivity().overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);
            readyGo(ProductSeviceActivity.class);


        } else if (i == R.id.ll_product_attr) {
            getProductAttr();


        }
    }

    public interface onSelectTabLinstener{
        void onTabSelect(int tab);
    }

    private onSelectTabLinstener tabLinstener;

    public void setSelectTabLinstener(onSelectTabLinstener linstener){
         this.tabLinstener = linstener;
    }



    public interface onScrollerLinstener{
        void onScroll(int scrollY);
    }

    private onScrollerLinstener linstener ;

    public void setScrollLinstener(onScrollerLinstener linstener){
        this.linstener = linstener;
    }



    @Override
    protected void onEventComing(EventCenter eventCenter) {
        super.onEventComing(eventCenter);
        if(eventCenter.getEventCode()== Constans.MESSAGE_GOODS_ATTR){
            String attr = (String) eventCenter.getData();
            tvProductAttr.setText(attr);
        }
    }

    /**
     *获取商品属性
     */
    private  void getProductAttr(){
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,String> params = new HashMap<>();
        params.put("id",bean.getProductId());//productId
        params.put("activityProductId",bean.getActivityProductId());
        params.put("status",bean.getBusinessType());
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.FRONT_PRODUCT_GETSKU, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                Gson gson = new Gson();
                listAttr = gson.fromJson(result,new TypeToken<List<GoodsAttrBean>>(){}.getType());
//                GoodCarDialog.getInstance().buyDialog(getSupportFragmentManager(),mContext,listAttr,type,0);
            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },false);
    }




    private void getPinglunData(){
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> page = new HashMap<>();
        page.put("pageNum",String.valueOf(1));
        page.put("pageSize",String.valueOf(2));
        map.put("page",page);
        Map<String,String> params = new HashMap<>();
        params.put("id",bean.getProductId());
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.FRONT_COMMENT_PAGE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                Gson gson = new Gson();
                BaseBody<CommentsBean> baseBody = gson.fromJson(result,new TypeToken<BaseBody<CommentsBean>>(){}.getType());
                listBeans.addAll(baseBody.getRows());
                listAdapter.notifyDataSetChanged();
                commtentCount.setText("商品评价(" + baseBody.getTotal() + ")");

            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },false);
    }


}
