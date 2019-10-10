package com.yun.software.kaadas.UI.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.activitys.CommitOrderActivity;
import com.yun.software.kaadas.UI.activitys.ShopDetailsNewActivity;
import com.yun.software.kaadas.UI.adapter.GoodCarListAdapter;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.CarItem;
import com.yun.software.kaadas.UI.bean.ShoppingCarBiz;
import com.yun.software.kaadas.UI.view.GridItemDecoration;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;

public class ShopCartFragment extends BaseFragment {

    @BindView(R2.id.tv_manager)
    TextView mTvManager;
    @BindView(R2.id.rv_car_list)
    RecyclerView mRvCarList;
    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    GoodCarListAdapter goodCarListAdapter;
    @BindView(R2.id.root_view)
    LinearLayout rootView;

    @BindView(R2.id.ivSelectAll)
    ImageView mIvSelectAll;
    @BindView(R2.id.lin_choice_all)
    LinearLayout mLinChoiceAll;
    @BindView(R2.id.tvCountMoney)
    TextView mTvCountMoney;
    @BindView(R2.id.btnSubmit)
    TextView mBtnSubmit;
    @BindView(R2.id.lin_submit)
    LinearLayout mLinSubmit;
    @BindView(R2.id.btndelete)
    TextView mBtndelete;
    @BindView(R2.id.rlBottomBar)
    RelativeLayout mRlBottomBar;
    @BindView(R2.id.lin_root)
    LinearLayout LinRoot;
    @BindView(R2.id.iv_back)
    ImageView ivBack;
    @BindView(R2.id.lin_back)
    LinearLayout linBack;
    private int startIndex = 1;
    private int pageSize =10;
    private int total;
    private boolean isCheck=false;
    private boolean isEdite=false;
    boolean isfrompageActivity=false;

    private List<CarItem> listBeans = new ArrayList<>();

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_shop_cart;
    }


    @Override
    protected void onUserVisible() {
        super.onUserVisible();
        getHoldData();
    }

    @Override
    protected View getLoadingTargetView() {
        return mRefreshLayout;
    }

    @Override
    protected void initViewsAndEvents() {
        Bundle bundle=getArguments();
        if(bundle!=null&&bundle.containsKey("frompage")){
            isfrompageActivity=true;
            ivBack.setVisibility(View.VISIBLE);
        }

        //添加自定义分割线
        GridItemDecoration.Builder  decoration = new GridItemDecoration.Builder(getActivity());
        decoration.setColor(getResources().getColor(R.color.default_bg_color));
        decoration.setHorizontalSpan(getResources().getDimension(R.dimen.dk_dp_15));

//        StatebarTools.setPaddingSmart(mContext,LinRoot);
        goodCarListAdapter = new GoodCarListAdapter(listBeans);
        mRvCarList.setHasFixedSize(true);
        mRvCarList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvCarList.setAdapter(goodCarListAdapter);
        mRvCarList.addItemDecoration(decoration.build());
        //此处必须开启 防止用户刷星时候进行下拉操作 会引起IndexOutOfBoundsException
        mRvCarList.setFocusableInTouchMode(false);
        mRefreshLayout.setDisableContentWhenRefresh(true);
        mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getHoldData();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

            }


        });
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> items = new ArrayList<>();

                for (CarItem item:listBeans){
                    if (item.isIscheck()){
                        items.add(item.getId());
                    }
                }
                if (items.size() == 0){
                    ToastUtil.showShort("请选择商品");
                    return;
                }



                Bundle bundle = new Bundle();
                bundle.putInt("type",ShopDetailsNewActivity.TYPE_NORMAL);
                bundle.putStringArrayList("ids",items);
                readyGo(CommitOrderActivity.class,bundle);
            }
        });
        goodCarListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                CarItem carItem = listBeans.get(position);
                int i = view.getId();
                if (i == R.id.v_add) {
                    carItem.addOrReduceGoodsNum(true);
                    changeQty(carItem);

                } else if (i == R.id.v_plus) {
                    carItem.addOrReduceGoodsNum(false);
                    changeQty(carItem);

                } else if (i == R.id.re_state) {
                    carItem.changeState();
                    isCheck = true;
                    for (CarItem bean : listBeans) {
                        if (!bean.isIscheck()) {
                            isCheck = false;
                            break;
                        }
                    }
                    if (isCheck) {
                        mIvSelectAll.setImageResource(R.drawable.selectbox_sel);
                    } else {
                        mIvSelectAll.setImageResource(R.drawable.selectbox_nor);
                    }

                }

                mTvCountMoney.setText(ShoppingCarBiz.getTotalMoney(listBeans));
                mBtnSubmit.setText("去结算(" + ShoppingCarBiz.getSelectCount(listBeans) + ")" );
                goodCarListAdapter.notifyDataSetChanged();

            }
        });

        mLinChoiceAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheck=!isCheck;
                if(isCheck){
                    mIvSelectAll.setImageResource(R.drawable.selectbox_sel);
                    mBtnSubmit.setText("去结算(" + listBeans.size() + ")" );
                }else{
                    mIvSelectAll.setImageResource(R.drawable.selectbox_nor);
                    mBtnSubmit.setText("去结算(0)" );
                }
                ShoppingCarBiz.setIsItemCheckAll(listBeans,isCheck);
                mTvCountMoney.setText(ShoppingCarBiz.getTotalMoney(listBeans));

                goodCarListAdapter.notifyDataSetChanged();
            }
        });
        goodCarListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (isEdite){

                }else {
                    CarItem item = listBeans.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", ShopDetailsNewActivity.TYPE_NORMAL);
                    bundle.putString("id",item.getAgentProductId());
                    readyGo(ShopDetailsNewActivity.class,bundle);
                }

            }
        });
        mTvManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdite=!isEdite;
                if(isEdite){
                    mBtndelete.setVisibility(View.VISIBLE);
                    mLinSubmit.setVisibility(View.GONE);
                    mBtnSubmit.setVisibility(View.GONE);
                    mTvManager.setText("完成");
                }else{
                    mBtndelete.setVisibility(View.GONE);
                    mLinSubmit.setVisibility(View.VISIBLE);
                    mBtnSubmit.setVisibility(View.VISIBLE);
                    mTvManager.setText("管理");


                }

            }
        });
        linBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isfrompageActivity){
                    getActivity().finish();
                }
            }
        });
        mBtndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGoods();
            }
        });
        mRefreshLayout.setEnableLoadMore(false);
//        mRefreshLayout.setEnableRefresh(false);
        getHoldData();
    }


    public void restore() {
        if (mBtndelete!=null && mLinSubmit != null && mTvManager!= null){
            mBtndelete.setVisibility(View.GONE);
            mLinSubmit.setVisibility(View.VISIBLE);
            mBtnSubmit.setVisibility(View.VISIBLE);
            mTvManager.setText("管理");

            isEdite = false;
        }
    }

    @Override
    protected void onEventComing(EventCenter eventCenter) {
        super.onEventComing(eventCenter);
        if(eventCenter.getEventCode()== Constans.MESSAGE_REFRESH_CART){
                getHoldData();
        }
    }



    private void deleteGoods(){
        List<String> items = new ArrayList<>();
        List<CarItem> newList = new ArrayList<>();
        for (CarItem item:listBeans){
            if (item.isIscheck()){
                items.add(item.getId());
            }else {
                newList.add(item);
            }
        }
        if (items.size() <= 0 ){
            ToastUtil.showShort("请选中商品");
            return;
        }

        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("shopcarIds",items.toArray());
        map.put("params",params);

        HttpManager.getInstance().post(mContext, ApiConstants.SHOPCAR_DELETE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                listBeans.clear();
                listBeans.addAll(newList);
                mTvCountMoney.setText(ShoppingCarBiz.getTotalMoney(listBeans));
                mBtnSubmit.setText("去结算(0)" );
                EventBus.getDefault().post(new EventCenter(Constans.MESSAGE_REFRESH_CART));
                if(newList.size()==0){
                    toggleShowEmptyImage(true, R.drawable.cart_missing_pages, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleRestore();
                            getHoldData();
//                            mRefreshLayout.autoRefresh();
                        }
                    });
                    mTvManager.setVisibility(View.GONE);

                }
                goodCarListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);


            }
        },true);
    }

    private void changeQty(CarItem item) {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,String> params = new HashMap<>();
        params.put("id",item.getId());
        params.put("qty",item.getQty());
        map.put("params",params);

        HttpManager.getInstance().post(mContext, ApiConstants.SHOPCAR_UPDATEQTY, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                goodCarListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },true);
    }


    private void getHoldData() {
        toggleShowLoading(true,"正在加载");
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> page = new HashMap<>();
        page.put("pageNum",String.valueOf(startIndex));
        page.put("pageSize",String.valueOf(pageSize));
        map.put("page",page);
        HttpManager.getInstance().post(mContext, ApiConstants.SHOPCAR_LIST, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                toggleRestore();
                listBeans.clear();
                Gson gson = new Gson();
                BaseBody<CarItem> baseBody = gson.fromJson(result, new TypeToken<BaseBody<CarItem>>() {
                }.getType());
                listBeans.addAll(baseBody.getRows());
                total = baseBody.total;
                if(total==0){
                    toggleShowEmptyImage(true, R.drawable.cart_missing_pages, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleRestore();
                            getHoldData();
//                            mRefreshLayout.autoRefresh();
                        }
                    });
                    mTvManager.setVisibility(View.GONE);
                    mRlBottomBar.setVisibility(View.GONE);
                }else {
                    mTvManager.setVisibility(View.VISIBLE);
                    mRlBottomBar.setVisibility(View.VISIBLE);
                }
                mIvSelectAll.setImageResource(R.drawable.selectbox_nor);
                goodCarListAdapter.notifyDataSetChanged();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
                restore();
                mBtnSubmit.setText("去结算(0)" );
                mTvCountMoney.setText("0");

            }
            @Override
            public void onFailed(String error) {
                toggleRestore();
                ToastUtil.showShort(error);
                toggleShowEmptyImage(true, R.drawable.cart_missing_pages, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleRestore();
                        getHoldData();
                    }
                });
                mTvManager.setVisibility(View.GONE);

            }
        },false);
    }

}
