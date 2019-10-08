package com.yun.software.kaadas.UI.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.Tools.GlideImageLoader;
import com.yun.software.kaadas.UI.activitys.CollectionListActivity;
import com.yun.software.kaadas.UI.activitys.CouponActivity;
import com.yun.software.kaadas.UI.activitys.FeedBackActivity;
import com.yun.software.kaadas.UI.activitys.FootprintListActivity;
import com.yun.software.kaadas.UI.activitys.JifenListActivity;
import com.yun.software.kaadas.UI.activitys.KanjiaActivity;
import com.yun.software.kaadas.UI.activitys.KefuActivity;
import com.yun.software.kaadas.UI.activitys.ListKanjiaActivity;
import com.yun.software.kaadas.UI.activitys.ListMiaoshaActivity;
import com.yun.software.kaadas.UI.activitys.ListNormalActivity;
import com.yun.software.kaadas.UI.activitys.ListPintuanActivity;
import com.yun.software.kaadas.UI.activitys.ListZhongchouActivity;
import com.yun.software.kaadas.UI.activitys.MyOderActivity;
import com.yun.software.kaadas.UI.activitys.NewMsgListActivity;
import com.yun.software.kaadas.UI.activitys.ProductListActivity;
import com.yun.software.kaadas.UI.activitys.SearchActivity;
import com.yun.software.kaadas.UI.activitys.ShopDetailsNewActivity;
import com.yun.software.kaadas.UI.activitys.UserInfoActivity;
import com.yun.software.kaadas.UI.activitys.WebViewActivity;
import com.yun.software.kaadas.UI.activitys.WxLoginActivity;
import com.yun.software.kaadas.UI.adapter.HomeListMiaoshaAdapter;
import com.yun.software.kaadas.UI.adapter.HomeListNormalAdapter;
import com.yun.software.kaadas.UI.adapter.HomeListPintuanAdapter;
import com.yun.software.kaadas.UI.adapter.HomeListZhongchouAdapter;
import com.yun.software.kaadas.UI.bean.GoodsBean;
import com.yun.software.kaadas.UI.bean.HomeBannerBean;
import com.yun.software.kaadas.UI.bean.HomeCategoriesBean;
import com.yun.software.kaadas.UI.bean.HomeListBean;
import com.yun.software.kaadas.UI.bean.User;
import com.yun.software.kaadas.UI.view.CustomLinearLayout;
import com.yun.software.kaadas.UI.view.GridItemDecoration;
import com.yun.software.kaadas.UI.view.JudgeNestedScrollView;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.iwgang.countdownview.CountdownView;
import de.greenrobot.event.EventBus;
import la.xiong.androidquick.tool.Glid.GlidUtils;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.SizeUtils;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;

import static la.xiong.androidquick.http.HeaderInterceptor.MESSAGE_LOGINOUT;

public class ShopFragment extends BaseFragment {
    View inflateView_tuila,inflateView_lingdong,inflateView_zhongchou,inflateView_pintuan,inflateView_miaosha,inflateView_kanjia;
    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R2.id.banner2)
    Banner mBanner2;

    private PopupWindow popupWindow;

    //灵动
    @BindView(R2.id.recycler_lingdong)
    RecyclerView recyclerLingdongView;

    //推拉
    @BindView(R2.id.recycler_tuila)
    RecyclerView recyclerTuilaView;

    //众筹
    @BindView(R2.id.recycler_zhongchou)
    RecyclerView recyclerZhongchouView;

    //拼团
    @BindView(R2.id.recycler_pintuan)
    RecyclerView recyclerPintuanView;

    //秒杀
    @BindView(R2.id.recycler_miaosha)
    RecyclerView recyclerMiaoshaView;

    //砍价
    @BindView(R2.id.recycler_kanjia)
    RecyclerView recyclerKanjiaView;


    @BindView(R2.id.scrollView)
    JudgeNestedScrollView scrollView;

    @BindView(R2.id.top_view)
    ConstraintLayout topView;

    @BindView(R2.id.ll_search)
    LinearLayout llSearchView;

    @BindView(R2.id.iv_has_news)
    ImageView hasNewsView;

    @BindView(R2.id.tv_cate_name_1)
    TextView tvCateName1View;

    @BindView(R2.id.tv_cate_name_2)
    TextView tvCateName2View;

    @BindView(R2.id.tv_cate_desc_1)
    TextView tvCateDesc1View;

    @BindView(R2.id.tv_cate_desc_2)
    TextView tvCateDesc2View;

    @BindView(R2.id.iv_cate_image_1)
    ImageView ivCateImage1View;

    @BindView(R2.id.iv_cate_image_2)
    ImageView ivCateImage2View;


    @BindView(R2.id.lingdong_top)
    ConstraintLayout lingdongTop;

    @BindView(R2.id.tuila_top)
    ConstraintLayout tuilaTop;

    @BindView(R2.id.zhongchou_top)
    ConstraintLayout zhongchouTop;

    @BindView(R2.id.pintuan_top)
    ConstraintLayout pintuanTop;

    @BindView(R2.id.miaosha_top)
    ConstraintLayout miaoshaTop;

    @BindView(R2.id.kanjia_top)
    ConstraintLayout kanjiaTop;

    @BindView(R2.id.tuila_image)
    ImageView tuilaImage;

    @BindView(R2.id.tuila_name)
    TextView tuilaName;

    @BindView(R2.id.tuila_price)
    TextView tuilaPrice;

    @BindView(R2.id.lingdong_image)
    ImageView lingdongImage;

    @BindView(R2.id.lingdong_name)
    TextView lingdongName;

    @BindView(R2.id.lingdong_price)
    TextView lingdongPrice;

    @BindView(R2.id.zhongchou_image)
    ImageView zhongchouImage;

    @BindView(R2.id.zhongchou_name)
    TextView zhongchouName;

    @BindView(R2.id.zhongchou_price)
    TextView zhongchouPrice;

    @BindView(R2.id.tv_zhichi_count)
    TextView zhongchouCount;

    @BindView(R2.id.progress)
    ProgressBar progressBar;

    @BindView(R2.id.zhongchou_percent)
    TextView zhongchouPercent;

    @BindView(R2.id.pintuan_image)
    ImageView pintuanImage;

    @BindView(R2.id.pintuan_name)
    TextView pintuanName;

    @BindView(R2.id.pintuan_price)
    TextView pintuanPrice;

    @BindView(R2.id.miaosha_image)
    ImageView miaoshaImage;

    @BindView(R2.id.miaosha_name)
    TextView miaoshaName;

    @BindView(R2.id.miaosha_price)
    TextView miaoshaPrice;

    @BindView(R2.id.kanjia_image)
    ImageView kanjiaImage;

    @BindView(R2.id.kanjia_name)
    TextView kanjiaName;

    @BindView(R2.id.kanjia_price)
    TextView kanjiaPrice;

    @BindView(R2.id.countdown_view)
    CountdownView countdownView;

    @BindView(R2.id.tv_categroy_name_1)
    TextView categoryName1;

    @BindView(R2.id.tv_categroy_name_2)
    TextView categoryName2;

    @BindView(R2.id.tv_categroy_name_4)
    TextView categoryName4;

    @BindView(R2.id.tv_categroy_name_5)
    TextView categoryName5;
    @BindView(R2.id.tv_categroy_name_6)
    TextView categoryName6;
    @BindView(R2.id.tv_categroy_name_7)
    TextView categoryName7;
    @BindView(R2.id.layout_lingdong)
    LinearLayout layoutLingdong;

    @BindView(R2.id.layout_tuila)
    LinearLayout layoutTuila;

    @BindView(R2.id.layout_zhongchou)
    LinearLayout layoutZhongchou;

    @BindView(R2.id.layout_pintuan)
    LinearLayout layoutPintuan;

    @BindView(R2.id.layout_miaosha)
    LinearLayout layoutMiaosha;

    @BindView(R2.id.layout_kanjia)
    LinearLayout layoutKanjia;

    @BindView(R2.id.lin_child)
    CustomLinearLayout linChild;

    @BindView(R2.id.miaosha_old_price)
    TextView miaoshaOldPrice;

    @BindView(R2.id.tv_over)
    TextView tvOverTimeView;

    @BindView(R2.id.tv_xianshi)
    TextView tvXianshiView;

    @BindView(R2.id.rl_new_msg)
    RelativeLayout rlNewMsgView;

    @BindView(R2.id.rl_shopcart)
    RelativeLayout rlShopCartView;

    @BindView(R2.id.shop_cart_count)
    TextView shopCartCount;



    @BindView(R2.id.ll_pintuan_xianshi)
    LinearLayout pintuanXianshi;
    @BindView(R2.id.tv_pintuan_over)
    TextView pintuanOver;
    @BindView(R2.id.pintuan_countdown_view)
    CountdownView pintuanCountdown;
    @BindView(R2.id.pintuan_end)
    ImageView pintuanEnd;
    @BindView(R2.id.miaosha_end)
    ImageView miaoshaEnd;
    @BindView(R2.id.re_root)
    RelativeLayout reRoot;

    @BindView(R2.id.iv_shopcart)
    ImageView ivShopCartView;



    private HomeListNormalAdapter lingdongAdapter;
    private HomeListNormalAdapter tuilaAdapter;
    private HomeListZhongchouAdapter zhongchouAdapter;
    private HomeListPintuanAdapter pintuanAdapter;
    private HomeListNormalAdapter kanjiaAdapter;
    private HomeListMiaoshaAdapter miaoshaAdapter;

    private List<GoodsBean> lingdongList = new ArrayList<>();
    private List<GoodsBean> tuilaList = new ArrayList<>();
    private List<GoodsBean> zhongchouList = new ArrayList<>();
    private List<GoodsBean> pintuanList = new ArrayList<>();
    private List<GoodsBean> kanjiaList = new ArrayList<>();
    private List<GoodsBean> miaoshaList = new ArrayList<>();


    public static List<String> images = new ArrayList<>();
    public static List<String> imageLinks = new ArrayList<>();
    public static List<String> bannerId = new ArrayList<>();
    private List<HomeListBean> listBeans = new ArrayList<>();
    private List<HomeCategoriesBean> categoriesList;
    private List<String> indexkey;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.store_fragment_home;
    }


    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e("9999");
        getTopData();
        getListData();
        getNumber();

    }

    @Override
    protected void initViewsAndEvents() {
        indexkey=new ArrayList<>();
        getTopData();
        getListData();
        getNumber();
        int h = DensityUtil.dp2px(200);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int color;
                if (scrollY <= 10) {
                    //顶部图处于最顶部，标题栏透明
                    color = Color.argb(0, 255, 255, 255);
                    llSearchView.setBackgroundColor(Color.parseColor("#99ffffff"));
                    rlNewMsgView.setAlpha(0.9f);
                    rlShopCartView.setAlpha(0.9f);
                    hasNewsView.setImageResource(R.drawable.home_more);
                    ivShopCartView.setImageResource(R.drawable.home_shopcart_white);
                } else if (scrollY > 0 && scrollY < h) {
                    //滑动过程中，渐变
                    float scale = (float) scrollY / h * 255;//算出滑动距离比例
                    color = Color.argb((int) scale, 255, 255, 255);
                    float alpha = (float) scrollY / h;
                    if (alpha < 0.2f) {
                        return;
                    }
                    rlNewMsgView.setAlpha((float) scrollY / h);
                    rlShopCartView.setAlpha((float) scrollY / h);
                } else {
                    //过顶部图区域，标题栏定色
                    color = Color.argb(255, 255, 255, 255);
                    llSearchView.setBackgroundColor(Color.parseColor("#99e6e6e6"));
                    rlNewMsgView.setAlpha(1.0f);
                    rlShopCartView.setAlpha(1.0f);
                    hasNewsView.setImageResource(R.drawable.home_more_black);
                    ivShopCartView.setImageResource(R.drawable.home_shopcart);


                }

                topView.setBackgroundColor(color);
            }
        });


        mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getTopData();
                getListData();
                getNumber();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

            }
        });


        //添加自定义分割线
        GridItemDecoration.Builder decoration = new GridItemDecoration.Builder(getActivity());
        decoration.setColor(getResources().getColor(R.color.default_bg_color));
        decoration.setVerticalSpan(getResources().getDimension(R.dimen.dp_5));
        decoration.setHorizontalSpan(getResources().getDimension(R.dimen.dp_5));


        //灵动系列
        GridLayoutManager layoutManager1 = new GridLayoutManager(getActivity(), 2);
        recyclerLingdongView.setLayoutManager(layoutManager1);
        lingdongAdapter = new HomeListNormalAdapter(lingdongList);
        recyclerLingdongView.setAdapter(lingdongAdapter);
        recyclerLingdongView.addItemDecoration(decoration.build());
        lingdongAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startToShopDetails(ShopDetailsNewActivity.TYPE_NORMAL, lingdongList.get(position));
            }
        });


        //推拉
        GridLayoutManager layoutManager2 = new GridLayoutManager(getActivity(), 2);
        recyclerTuilaView.setLayoutManager(layoutManager2);
        tuilaAdapter = new HomeListNormalAdapter(tuilaList);
        recyclerTuilaView.setAdapter(tuilaAdapter);
        recyclerTuilaView.addItemDecoration(decoration.build());
        tuilaAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startToShopDetails(ShopDetailsNewActivity.TYPE_NORMAL, tuilaList.get(position));
            }
        });

        //今日众筹
        GridLayoutManager layoutManager3 = new GridLayoutManager(getActivity(), 2);
        recyclerZhongchouView.setLayoutManager(layoutManager3);
        zhongchouAdapter = new HomeListZhongchouAdapter(zhongchouList);
        recyclerZhongchouView.setAdapter(zhongchouAdapter);
        recyclerZhongchouView.addItemDecoration(decoration.build());
        zhongchouAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startToShopDetails(ShopDetailsNewActivity.TYPE_ZHONGCHOU, zhongchouList.get(position));
            }
        });

        //今日拼团
        GridLayoutManager layoutManager4 = new GridLayoutManager(getActivity(), 2);
        recyclerPintuanView.setLayoutManager(layoutManager4);
        pintuanAdapter = new HomeListPintuanAdapter(pintuanList);
        recyclerPintuanView.setAdapter(pintuanAdapter);
        recyclerPintuanView.addItemDecoration(decoration.build());
        pintuanAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                startToShopDetails(ShopDetailsNewActivity.TYPE_PINTUAN, pintuanList.get(position));
            }
        });


        //秒杀
        GridLayoutManager layoutManager5 = new GridLayoutManager(getActivity(), 2);
        recyclerMiaoshaView.setLayoutManager(layoutManager5);
        miaoshaAdapter = new HomeListMiaoshaAdapter(miaoshaList);
        recyclerMiaoshaView.setAdapter(miaoshaAdapter);
        recyclerMiaoshaView.addItemDecoration(decoration.build());
        miaoshaAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                startToShopDetails(ShopDetailsNewActivity.TYPE_MIAOSHA, miaoshaList.get(position));
            }
        });


        //砍价
        GridLayoutManager layoutManager6 = new GridLayoutManager(getActivity(), 2);
        recyclerKanjiaView.setLayoutManager(layoutManager6);
        kanjiaAdapter = new HomeListNormalAdapter(kanjiaList);
        recyclerKanjiaView.setAdapter(kanjiaAdapter);
        recyclerKanjiaView.addItemDecoration(decoration.build());
        kanjiaAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsBean bean5 = kanjiaList.get(position);
                if (bean5.isFlg()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", bean5.getCustomerBargainId());
                    bundle.putString("agentProductId", bean5.getId());
                    readyGo(KanjiaActivity.class, bundle);
                } else {
                    startToShopDetails(ShopDetailsNewActivity.TYPE_KANJIA, bean5);
                }
            }
        });

        mRefreshLayout.setEnableLoadMore(false);

    }

    private void getNumber() {

        Map<String, Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        map.put("device","android");
        HttpManager.getInstance().post(mContext, ApiConstants.SHOPCAR_GETNUMBER, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                LogUtils.e("获取购物车数量"+result);
                String sysMessageNumber = StringUtil.getJsonKeyStr(result, "sysMessageNumber");
                String shopNumber = StringUtil.getJsonKeyStr(result, "shopNumber");

                shopCartCount.setText(shopNumber);


            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        }, false);
    }


    private void startToShopDetails(int type, GoodsBean bean) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putString("statue", bean.getBusinessType());
        bundle.putString("id", bean.getId());
        bundle.putString("actid", bean.getActivityProductId());
        readyGo(ShopDetailsNewActivity.class, bundle);
    }


    @OnClick({R2.id.ll_search, R2.id.rl_new_msg, R2.id.cl_more_lingdong,
            R2.id.cl_more_tuila, R2.id.cl_more_zhongchou, R2.id.cl_more_pintuan, R2.id.cl_more_miaosha,
            R2.id.cl_more_kanjia, R2.id.item_1, R2.id.item_2, R2.id.rl_shopcart,
            R2.id.ll_jifen,R2.id.ll_coupon,R2.id.ll_collection,R2.id.ll_footprint,
            R2.id.iv_kefu,
    })
    public void Onclick(View view) {
        int i = view.getId();//搜索
//新消息
//拼团
//秒杀
//砍价
//积分
//优惠券
//收藏
//足迹
        if (i == R.id.ll_search) {
            readyGo(SearchActivity.class);
//                linChild.reSetlayoutView();

        } else if (i == R.id.rl_new_msg) {
            if (TextUtils.isEmpty(UserUtils.getToken())) {
//                readyGo(WxLoginActivity.class);
                EventBus.getDefault().post(new EventCenter(MESSAGE_LOGINOUT,"relogin"));
                return;
            }
//                readyGo(NewMsgListActivity.class);
            showPop();

        } else if (i == R.id.cl_more_lingdong) {//灵动系列
            for (HomeListBean bean : listBeans) {
                if (bean.getModuleKey().equals("product_type_s")) {
                    HomeCategoriesBean bean3 = new HomeCategoriesBean("product_type_s", bean.getModuleName());
                    Bundle bundle3 = new Bundle();
                    bundle3.putParcelable("bean", bean3);
                    readyGo(ListNormalActivity.class, bundle3);
                }
            }

        } else if (i == R.id.cl_more_tuila) {//推拉系列
            for (HomeListBean bean : listBeans) {
                if (bean.getModuleKey().equals("product_type_k")) {
                    HomeCategoriesBean bean3 = new HomeCategoriesBean("product_type_k", bean.getModuleName());
                    Bundle bundle3 = new Bundle();
                    bundle3.putParcelable("bean", bean3);
                    readyGo(ListNormalActivity.class, bundle3);
                }
            }

        } else if (i == R.id.cl_more_zhongchou) {//众筹
            readyGo(ListZhongchouActivity.class);

        } else if (i == R.id.cl_more_pintuan) {
            readyGo(ListPintuanActivity.class);

        } else if (i == R.id.cl_more_miaosha) {
            readyGo(ListMiaoshaActivity.class);

        } else if (i == R.id.cl_more_kanjia) {
            readyGo(ListKanjiaActivity.class);

        } else if (i == R.id.item_1) {
            HomeCategoriesBean bean = categoriesList.get(0);
            Bundle bundle = new Bundle();
            bundle.putParcelable("bean", bean);
            readyGo(ProductListActivity.class, bundle);

        } else if (i == R.id.item_2) {
            HomeCategoriesBean bean1 = categoriesList.get(1);
            Bundle bundle1 = new Bundle();
            bundle1.putParcelable("bean", bean1);
            readyGo(ProductListActivity.class, bundle1);

        } else if (i == R.id.rl_shopcart) {
            if (TextUtils.isEmpty(UserUtils.getToken())) {
                readyGo(WxLoginActivity.class);
                return;
            }
            Bundle bundle2 = new Bundle();
            bundle2.putString("frompage", "activity");
            readyGo(ShopCartFragment.class, bundle2);

        } else if (i == R.id.ll_jifen) {
            readyGo(JifenListActivity.class);

        } else if (i == R.id.ll_coupon) {
            getUserData();


        } else if (i == R.id.ll_collection) {
            readyGo(CollectionListActivity.class);

        } else if (i == R.id.ll_footprint) {
            readyGo(FootprintListActivity.class);

        } else if (i == R.id.iv_kefu) {
            readyGo(KefuActivity.class);

        } else {
        }
    }

    private void showPop() {
        View view = getLayoutInflater().inflate(R.layout.poppup_home, null);


        if (popupWindow == null) {
            popupWindow = new PopupWindow(view, 300, SizeUtils.dp2px(51) * 4, true);
            popupWindow.setOutsideTouchable(true);
            LinearLayout llmsg = view.findViewById(R.id.ll_msg);
            LinearLayout llorder = view.findViewById(R.id.ll_order);
            LinearLayout llfeedback = view.findViewById(R.id.ll_feedback);
            LinearLayout llme = view.findViewById(R.id.ll_me);

            llmsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    readyGo(NewMsgListActivity.class);
                }
            });
            llorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    readyGo(MyOderActivity.class);
                }
            });

            llfeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    readyGo(FeedBackActivity.class);
                }
            });

            llme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    readyGo(UserInfoActivity.class);
                }
            });
        }
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            popupWindow.showAsDropDown(rlNewMsgView, 0, 0, 0);
        }


    }

    private void getUserData() {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        HttpManager.getInstance().post(mContext, ApiConstants.CUSTOMERAPP_MYINFO, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                Gson gson = new Gson();
                User user = gson.fromJson(result, User.class);
                UserUtils.setUserTel(user.getTel());
                Bundle bundle3 = new Bundle();
                bundle3.putString("nouse",user.getUserCouponUseNum());
                bundle3.putString("overtime",user.getUserCouponOverdueNum());
                readyGo(CouponActivity.class,bundle3);


            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },false);
    }


    public void getTopData() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        map.put("device","android");
        HttpManager.getInstance().post(mContext, ApiConstants.HOME_GETFIRSTPART, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {

                mRefreshLayout.finishRefresh();

                String bannerJson = StringUtil.getJsonKeyStr(result, "banner");
                String videoJson = StringUtil.getJsonKeyStr(result, "vedio");
                String categoriesJson = StringUtil.getJsonKeyStr(result, "categories");

                LogUtils.e("轮播图的数据"+result);
                images.clear();
                if(null != imageLinks){
                    imageLinks.clear();
                }
                if(null != bannerId){
                    bannerId.clear();
                }

                //轮播图
                Gson gson = new Gson();
                List<HomeBannerBean> bannerList = gson.fromJson(bannerJson, new TypeToken<List<HomeBannerBean>>() {
                }.getType());
                for (HomeBannerBean bean : bannerList) {
                    images.add(bean.getImgUrl());
                    imageLinks.add(bean.getBannerType());
                    bannerId.add(bean.getBannerUrl());
                }

                mBanner2.setBannerStyle(BannerConfig.NOT_INDICATOR)
                        .setImages(images)
                        .setDelayTime(5000)
                        .setImageLoader(new GlideImageLoader())
                        .setOnBannerListener(new OnBannerListener() {
                            @Override
                            public void OnBannerClick(int position) {
                                if("banner_type_h5".equals(imageLinks.get(position))){
                                    Intent intent_h5 = new Intent(getActivity(), WebViewActivity.class);
                                    intent_h5.putExtra("web_url", bannerId.get(position));
                                    startActivity(intent_h5);
                                }else if("banner_type_information".equals(imageLinks.get(position))){
                                    Intent intent_information = new Intent(getActivity(), WebViewActivity.class);
                                    intent_information.putExtra("web_url", ApiConstants.webUrl+"?id="+bannerId.get(position)+"&bannerType="+"banner_type_information");
                                    startActivity(intent_information);
                                }else if("banner_type_activity".equals(imageLinks.get(position))){
                                    Intent intent_activity = new Intent(getActivity(), WebViewActivity.class);
                                    intent_activity.putExtra("web_url", "https://www.kaishuzhijia.com/h5/index.html#/activity"+"?id="+bannerId.get(position)+"&bannerType="+"banner_type_activity");
                                    LogUtils.e("请求的url"+"https://www.kaishuzhijia.com/h5/index.html#/activity"+"?id="+bannerId.get(position)+"&bannerType="+"banner_type_activity");
                                    startActivity(intent_activity);
                                }else if ("banner_type_product".equals(imageLinks.get(position))){
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("type", ShopDetailsNewActivity.TYPE_NORMAL);
                                    bundle.putString("statue", "product_type_s");
                                    bundle.putString("id", bannerId.get(position));
                                    bundle.putString("actid", "");
                                    readyGo(ShopDetailsNewActivity.class, bundle);
                                }
                            }
                        })
                        .start();



            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        }, false);
    }


    private void getListData() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        map.put("device","android");
        HttpManager.getInstance().post(mContext, ApiConstants.HOME_GETSECDPART, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                Gson gson = new Gson();
                listBeans = gson.fromJson(result, new TypeToken<List<HomeListBean>>() {
                }.getType());
                LogUtils.e("卡迪克首页数据" + result);
                //根据服务器数据加载布局
                //初始化页面
//                layoutTuila.setVisibility(View.GONE);
                tuilaList.clear();
//                layoutLingdong.setVisibility(View.GONE);
                lingdongList.clear();
//                layoutZhongchou.setVisibility(View.GONE);
                zhongchouList.clear();
//                layoutPintuan.setVisibility(View.GONE);
                pintuanList.clear();
//                layoutMiaosha.setVisibility(View.GONE);
                miaoshaList.clear();
                indexkey.clear();
                for (int i = 0; i < listBeans.size(); i++) {
                    HomeListBean bean=listBeans.get(i);
                    indexkey.add(bean.getModuleKey());
                }

                for (HomeListBean bean : listBeans) {
                    switch (bean.getModuleKey()) {
                        case "product_type_k": //执手
                            layoutTuila.setVisibility(View.VISIBLE);
                            tuilaList.addAll(bean.getList());
                            if (tuilaList.size() % 2 == 1) {
                                GoodsBean bean1 = tuilaList.get(0);
                                tuilaName.setText(bean1.getName());
                                tuilaPrice.setText(bean1.getPrice());
                                GlidUtils.loadImageNormal(mContext, bean1.getLogo(), tuilaImage);
                                tuilaList.remove(bean1);
                                tuilaTop.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startToShopDetails(ShopDetailsNewActivity.TYPE_NORMAL, bean1);
                                    }
                                });

                            } else {
                                tuilaTop.setVisibility(View.GONE);
                            }
                            categoryName2.setText(bean.getModuleName());

                            break;

                        case "product_type_s": //维拉
                            lingdongList.clear();
                            layoutLingdong.setVisibility(View.VISIBLE);
                            lingdongList.addAll(bean.getList());
                            if (lingdongList.size() % 2 == 1) {
                                GoodsBean bean2 = lingdongList.get(0);
                                lingdongName.setText(bean2.getName());
                                lingdongPrice.setText(bean2.getPrice());
                                GlidUtils.loadImageNormal(mContext, bean2.getLogo(), lingdongImage);
                                lingdongList.remove(bean2);
                                lingdongTop.setOnClickListener(v -> {

                                    startToShopDetails(ShopDetailsNewActivity.TYPE_NORMAL, bean2);
                                });

                            } else {
                                lingdongTop.setVisibility(View.GONE);
                            }
                            categoryName1.setText(bean.getModuleName());

                            break;
                        case "product_type_crowd"://今日众筹
                            zhongchouList.clear();
                            layoutZhongchou.setVisibility(View.VISIBLE);
                            zhongchouList.addAll(bean.getList());
                            if (zhongchouList.size() % 2 == 1) {
                                GoodsBean bean3 = zhongchouList.get(0);
                                zhongchouName.setText(bean3.getName());
                                zhongchouPrice.setText(bean3.getPrice());
                                zhongchouPercent.setText(bean3.getPercentage() + "%");
                                String per = bean3.getPercentage();
                                progressBar.setProgress(Integer.parseInt(per));
                                zhongchouCount.setText(bean3.getSaleQty());
                                GlidUtils.loadImageNormal(mContext, bean3.getLogo(), zhongchouImage);
                                zhongchouList.remove(bean3);
                                zhongchouTop.setOnClickListener(v -> {
                                    startToShopDetails(ShopDetailsNewActivity.TYPE_ZHONGCHOU, bean3);
                                });

                            } else {
                                zhongchouTop.setVisibility(View.GONE);
                            }
                            categoryName4.setText(bean.getModuleName());

                            break;
                        case "product_type_group"://今日团购
                            pintuanList.clear();
                            layoutPintuan.setVisibility(View.VISIBLE);
                            pintuanList.addAll(bean.getList());
                            if (pintuanList.size() % 2 == 1) {
                                GoodsBean bean4 = pintuanList.get(0);
                                pintuanName.setText(bean4.getName());
                                pintuanPrice.setText(bean4.getPrice());
                                GlidUtils.loadImageNormal(mContext, bean4.getLogo(), pintuanImage);
                                if (bean4.getSecond() < 0) {
                                    pintuanXianshi.setVisibility(View.GONE);
                                    pintuanCountdown.setVisibility(View.GONE);
                                    pintuanOver.setVisibility(View.VISIBLE);
                                    pintuanEnd.setVisibility(View.VISIBLE);
                                } else {
                                    pintuanXianshi.setVisibility(View.VISIBLE);
                                    pintuanCountdown.setVisibility(View.VISIBLE);
                                    pintuanOver.setVisibility(View.GONE);
                                    pintuanEnd.setVisibility(View.GONE);
                                }

                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        pintuanCountdown.start(bean4.getSecond());
                                    }
                                });
                                pintuanCountdown.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                                    @Override
                                    public void onEnd(CountdownView cv) {
                                        pintuanXianshi.setVisibility(View.GONE);
                                        pintuanCountdown.setVisibility(View.GONE);
                                        pintuanOver.setVisibility(View.VISIBLE);
                                        pintuanEnd.setVisibility(View.VISIBLE);
                                    }
                                });
                                if ("activity_product_end".equals(bean4.getStatus())) {
                                    pintuanXianshi.setVisibility(View.GONE);
                                    pintuanCountdown.setVisibility(View.GONE);
                                    pintuanOver.setVisibility(View.VISIBLE);
                                    pintuanEnd.setVisibility(View.VISIBLE);
                                } else {
                                    pintuanXianshi.setVisibility(View.VISIBLE);
                                    pintuanCountdown.setVisibility(View.VISIBLE);
                                    pintuanOver.setVisibility(View.GONE);
                                    pintuanEnd.setVisibility(View.GONE);
                                }
                                pintuanList.remove(bean4);
                                pintuanTop.setOnClickListener(v -> {
                                    startToShopDetails(ShopDetailsNewActivity.TYPE_PINTUAN, bean4);
                                });
                            } else {
                                pintuanTop.setVisibility(View.GONE);
                            }
                            categoryName5.setText(bean.getModuleName());

                            break;
                        case "product_type_seckill"://今日秒杀
                            miaoshaList.clear();
                            layoutMiaosha.setVisibility(View.VISIBLE);
                            miaoshaList.addAll(bean.getList());
                            if (miaoshaList.size() % 2 == 1) {
                                GoodsBean bean5 = miaoshaList.get(0);
                                miaoshaName.setText(bean5.getName());
                                miaoshaPrice.setText(bean5.getPrice());
                                miaoshaOldPrice.setText("¥" + bean5.getOldPrice());
                                miaoshaOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); //中划线
                                GlidUtils.loadImageNormal(mContext, bean5.getLogo(), miaoshaImage);

//                                Date endDate = TimeUtil.getDateByFormat(bean5.getEndTime(),TimeUtil.dateFormatYMDHMS);
//                                long des = endDate.getTime() - System.currentTimeMillis();
                                if (bean5.getSecond() < 0) {
                                    tvXianshiView.setVisibility(View.GONE);
                                    countdownView.setVisibility(View.GONE);
                                    tvOverTimeView.setVisibility(View.VISIBLE);
                                    miaoshaEnd.setVisibility(View.VISIBLE);
                                } else {
                                    tvXianshiView.setVisibility(View.VISIBLE);
                                    countdownView.setVisibility(View.VISIBLE);
                                    tvOverTimeView.setVisibility(View.GONE);
                                    miaoshaEnd.setVisibility(View.GONE);
                                }

                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        countdownView.start(bean5.getSecond());
                                    }
                                });

                                LogUtils.e("即日秒杀的事件"+bean5.getSecond());
                                countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                                    @Override
                                    public void onEnd(CountdownView cv) {
                                        tvXianshiView.setVisibility(View.GONE);
                                        countdownView.setVisibility(View.GONE);
                                        tvOverTimeView.setVisibility(View.VISIBLE);
                                        miaoshaEnd.setVisibility(View.VISIBLE);
                                    }
                                });
                                if ("activity_product_end".equals(bean5.getStatus())) {
                                    tvXianshiView.setVisibility(View.GONE);
                                    countdownView.setVisibility(View.GONE);
                                    tvOverTimeView.setVisibility(View.VISIBLE);
                                    miaoshaEnd.setVisibility(View.VISIBLE);
                                } else {
                                    tvXianshiView.setVisibility(View.VISIBLE);
                                    countdownView.setVisibility(View.VISIBLE);
                                    tvOverTimeView.setVisibility(View.GONE);
                                    miaoshaEnd.setVisibility(View.GONE);
                                }
                                miaoshaList.remove(bean5);
                                miaoshaTop.setOnClickListener(v -> {
                                    startToShopDetails(ShopDetailsNewActivity.TYPE_MIAOSHA, bean5);
                                });
                            } else {
                                miaoshaTop.setVisibility(View.GONE);
                            }
                            categoryName6.setText(bean.getModuleName());

                            break;
                        case "product_type_bargain"://砍价
                            kanjiaList.clear();
                            layoutKanjia.setVisibility(View.VISIBLE);
                            kanjiaList.addAll(bean.getList());
                            if (kanjiaList.size() % 2 == 1) {
                                GoodsBean bean5 = kanjiaList.get(0);
                                kanjiaName.setText(bean5.getName());
                                kanjiaPrice.setText(bean5.getMaxPrice());
                                GlidUtils.loadImageNormal(mContext, bean5.getLogo(), kanjiaImage);
                                kanjiaList.remove(bean5);
                                kanjiaTop.setOnClickListener(v -> {
                                    if (bean5.isFlg()) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("id", bean5.getCustomerBargainId());
                                        bundle.putString("agentProductId", bean5.getId());
                                        readyGo(KanjiaActivity.class, bundle);
                                    } else {
                                        startToShopDetails(ShopDetailsNewActivity.TYPE_KANJIA, bean5);
                                    }

//                                    Bundle bundle = new Bundle();
//                                    bundle.putParcelable("bean",bean5);
//
                                });
                            } else {
                                kanjiaTop.setVisibility(View.GONE);
                            }
                            categoryName7.setText(bean.getModuleName());
                            break;
                    }
                    mRefreshLayout.finishRefresh();
                    mRefreshLayout.finishLoadMore(true);
                }

                tuilaAdapter.notifyDataSetChanged();
                lingdongAdapter.notifyDataSetChanged();
                zhongchouAdapter.notifyDataSetChanged();
                pintuanAdapter.notifyDataSetChanged();
                miaoshaAdapter.notifyDataSetChanged();
//                linChild.reSetlayoutView(indexkey);
            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore(true);
            }
        }, false);
    }

    @Override
    protected void onEventComing(EventCenter eventCenter) {
        super.onEventComing(eventCenter);
        if (eventCenter.getEventCode() == Constans.MESSAGE_REFRESH_CART) {
            getNumber();
        } else if (eventCenter.getEventCode() == Constans.MESSAGE_FINISH_SHOP_DETAILS) {
            getListData();
        }
    }

}
