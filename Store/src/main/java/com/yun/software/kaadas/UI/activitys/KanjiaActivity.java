package com.yun.software.kaadas.UI.activitys;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.Tools.GlideImageLoader;
import com.yun.software.kaadas.UI.adapter.HomeListNormalAdapter;
import com.yun.software.kaadas.UI.adapter.KanjiaHelpAdapter;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.GoodsBean;
import com.yun.software.kaadas.UI.bean.HomeListBean;
import com.yun.software.kaadas.UI.bean.ImgUrlBean;
import com.yun.software.kaadas.UI.bean.KanjiaBean;
import com.yun.software.kaadas.UI.bean.KanjiaHelp;
import com.yun.software.kaadas.UI.view.GridItemDecoration;
import com.yun.software.kaadas.UI.view.JudgeNestedScrollView;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.iwgang.countdownview.CountdownView;
import la.xiong.androidquick.tool.Glid.GlidUtils;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.ToastUtil;

public class KanjiaActivity extends BaseActivity {


    @BindView(R2.id.rv_list)
    RecyclerView recyclerView;

    @BindView(R2.id.rv_list_help)
    RecyclerView recyclerHelpView;


    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R2.id.tv_open_view)
    TextView tvOpenView;

    @BindView(R2.id.root_view)
    JudgeNestedScrollView rootView;

    @BindView(R2.id.tv_name)
    TextView tvName;

    @BindView(R2.id.tv_price)
    TextView tvPrice;

    @BindView(R2.id.price_desc)
    TextView priceDesc;


    @BindView(R2.id.image)
    ImageView imageView;

    @BindView(R2.id.ll_help)
    LinearLayout llHelp;

    @BindView(R2.id.countdown_view)
    CountdownView countdownView;

    @BindView(R2.id.ll_countdownview)
    LinearLayout llcountdownview;

    private HomeListNormalAdapter listAdapter;
    private KanjiaHelpAdapter helpAdapter;


    private int startIndex = 1;
    private int pageSize = 10;
    private int total ;

    private List<GoodsBean> listBeans = new ArrayList<>();

    private int helpHight =0;
    private boolean isOpen = true;
    private List<KanjiaHelp> listHelp = new ArrayList<>();

    private String id;
    private String agentProductId;
    private KanjiaBean bean;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_kanjia;
    }

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected View getLoadingTargetView() {
        return rootView;
    }

    @Override
    protected void initViewsAndEvents() {
        tvTitle.setText("砍价");

        id = getIntent().getStringExtra("id");
        agentProductId = getIntent().getStringExtra("agentProductId");


        helpAdapter = new KanjiaHelpAdapter(listHelp);
        recyclerHelpView.setAdapter(helpAdapter);
        recyclerHelpView.setLayoutManager(new LinearLayoutManager(mContext));

        listAdapter=new HomeListNormalAdapter(listBeans);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
//        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //添加自定义分割线
        GridItemDecoration.Builder  decoration = new GridItemDecoration.Builder(this);
        decoration.setColor(getResources().getColor(R.color.default_bg_color));
        decoration.setHorizontalSpan(getResources().getDimension(R.dimen.dp_5));
        decoration.setVerticalSpan(getResources().getDimension(R.dimen.dp_5));
        recyclerView.addItemDecoration(decoration.build());

        recyclerView.setAdapter(listAdapter);
        //此处必须开启 防止用户刷星时候进行下拉操作 会引起IndexOutOfBoundsException
        mRefreshLayout.setDisableContentWhenRefresh(true);
        mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                startIndex = 1;
                listBeans.clear();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (startIndex * pageSize >= total){
                    ToastUtil.showShort("没有更多数据了");
                    mRefreshLayout.finishLoadMoreWithNoMoreData();
                    return;
                }
                startIndex += 1;
            }
        });
        listAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsBean bean = listBeans.get(position);
                if (bean.isFlg()){
                    Bundle bundle = new Bundle();
                    bundle.putString("id",bean.getCustomerBargainId());
                    bundle.putString("agentProductId",bean.getAgentProductId());
                    readyGo(KanjiaActivity.class,bundle);
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",ShopDetailsNewActivity.TYPE_KANJIA);
                    bundle.putString("statue",bean.getBusinessType());
                    bundle.putString("id",bean.getId());
                    bundle.putString("actid",bean.getActivityProductId());
                    readyGo(ShopDetailsNewActivity.class,bundle);
                }
            }
        });

        ViewTreeObserver viewTreeObserver = recyclerHelpView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //移除监听，只用于布局初始化
                recyclerHelpView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //传入 RecyclerView 高度，并做一些 Adapter 的初始化工作
                helpHight = recyclerHelpView.getHeight();
            }
        });


        getProductDetail();
        getKanjiaHelp();
        getTuijian();
    }

    @OnClick({R2.id.rl_shouqi,R2.id.btn_share,R2.id.btn_buy})
    public void onClick(View view){
        int i = view.getId();//收起
        if (i == R.id.rl_shouqi) {
            if (isOpen) {
                tvOpenView.setText("展开");
                openOrCloseHelpView(helpHight, 0);
            } else {
                tvOpenView.setText("收起");
                openOrCloseHelpView(0, helpHight);
            }
            isOpen = !isOpen;

        } else if (i == R.id.btn_share) {
            if (bean.getPrice().equals(bean.getSoldPrice())) {
                ToastUtil.showShort("恭喜您，您砍到最大优惠请立即购买商品哦！");
                return;
            }

        } else if (i == R.id.btn_buy) {
            bug();

        }

    }

    private void bug() {
        Bundle bundle = new Bundle();
        bundle.putString("qty","1");
        bundle.putInt("type",ShopDetailsNewActivity.TYPE_KANJIA);
        bundle.putString("id",bean.getId());
        bundle.putString("agentProductId",agentProductId);
        readyGo(CommitOrderActivity.class,bundle);
        finish();
    }


    private void openOrCloseHelpView(int start,int end){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start,end);
        valueAnimator.setDuration(500);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                LogUtils.eTag("height",height);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)animation.getAnimatedValue());
                recyclerHelpView.setLayoutParams(params);

            }
        });
    }

    private void setView() {

        GlidUtils.loadImageNormal(mContext,bean.getLogo(),imageView);
        countdownView.start(bean.getSecond());
        countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                llcountdownview.setVisibility(View.GONE);
            }
        });
        tvName.setText(bean.getProductName());
        tvPrice.setText("¥"+bean.getMaxPrice());
        priceDesc.setText("已砍"+bean.getCutPrice()+"元，还差"+bean.getSoldPrice()+"元");
    }

    /**
     *获取商品详情
     */
    private  void getProductDetail(){
        toggleShowLoading(true,"正在加载");
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,String> params = new HashMap<>();
        params.put("id",id);
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.APPBARGAIN_DETAIL, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                toggleRestore();
                Gson gson = new Gson();
                bean = gson.fromJson(result, KanjiaBean.class);
                setView();

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
        },false);
    }


    private void getKanjiaHelp(){
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,String> params = new HashMap<>();
        params.put("id",id);
        map.put("params",params);

        HttpManager.getInstance().post(mContext, ApiConstants.APPBARGAIN_GETBARGAINRECORD, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                Gson gson = new Gson();
                BaseBody<KanjiaHelp> baseBody = gson.fromJson(result,new TypeToken<BaseBody<KanjiaHelp>>(){}.getType());
                listHelp.addAll(baseBody.getRows());
                if (listHelp.size() == 0){
                    llHelp.setVisibility(View.GONE);
                }else {
                    helpAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailed(String error) {

            }
        },false);
    }


    private void getTuijian(){
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> page = new HashMap<>();
        page.put("pageNum",String.valueOf(startIndex));
        page.put("pageSize",String.valueOf(pageSize));
        map.put("page",page);
        Map<String,String> params = new HashMap<>();
        params.put("type","product_type_bargain");
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.HOME_GETFIRSTALL, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                if(startIndex == 1){
                    listBeans.clear();
                }
                Gson gson = new Gson();
                BaseBody<GoodsBean> baseBody = gson.fromJson(result,new TypeToken<BaseBody<GoodsBean>>(){}.getType());
                listBeans.addAll(baseBody.getRows());
                total = baseBody.total;
                listAdapter.notifyDataSetChanged();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore(true);

            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore(true);


            }
        },false);
    }

}
