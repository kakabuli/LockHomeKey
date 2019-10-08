package com.yun.software.kaadas.UI.activitys;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.adapter.AAAAListAdapter;
import com.yun.software.kaadas.UI.adapter.HomeListNormalAdapter;
import com.yun.software.kaadas.UI.adapter.KanjiaMidAdapter;
import com.yun.software.kaadas.UI.adapter.KanjiaTopAdapter;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.GoodsBean;
import com.yun.software.kaadas.UI.bean.KanjiaMine;
import com.yun.software.kaadas.UI.view.GridItemDecoration;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import la.xiong.androidquick.tool.ToastUtil;

public class KanjianListActivity extends BaseActivity {

    @BindView(R2.id.rv_list_top)
    RecyclerView recyclerTopView;

//    @BindView(R2.id.rv_list_middle)
//    RecyclerView recyclerMidView;

    @BindView(R2.id.rv_list_bottom)
    RecyclerView recyclerBottomView;

    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R2.id.root_view)
    LinearLayout rootView;

    private HomeListNormalAdapter listAdapter;
    private KanjiaTopAdapter topAdapter;
//    private KanjiaMidAdapter midAdapter;


    private int startIndex = 1;
    private int pageSize = 10;
    private int total ;

    private List<GoodsBean> listBeans = new ArrayList<>();
    private List<KanjiaMine> listKanjia = new ArrayList<>();


    @Override
    protected View getLoadingTargetView() {
        return rootView;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_kanjia_list;
    }


    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected void initViewsAndEvents() {
        tvTitle.setText("砍价");



        //上
        topAdapter=new KanjiaTopAdapter(listKanjia);
        recyclerTopView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerTopView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerTopView.setAdapter(topAdapter);

        //中
//        midAdapter=new KanjiaMidAdapter(listBeans);
//        recyclerMidView.setHasFixedSize(true);
////        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
//        recyclerMidView.setLayoutManager(new LinearLayoutManager(mContext));
//        recyclerMidView.setAdapter(midAdapter);


        //下
        listAdapter=new HomeListNormalAdapter(listBeans);
        recyclerBottomView.setHasFixedSize(true);
        recyclerBottomView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerBottomView.setAdapter(listAdapter);
        //添加自定义分割线
        GridItemDecoration.Builder  decoration = new GridItemDecoration.Builder(this);
        decoration.setColor(getResources().getColor(R.color.default_bg_color));
        decoration.setHorizontalSpan(getResources().getDimension(R.dimen.dp_5));
        decoration.setVerticalSpan(getResources().getDimension(R.dimen.dp_5));
        recyclerBottomView.addItemDecoration(decoration.build());






        //此处必须开启 防止用户刷星时候进行下拉操作 会引起IndexOutOfBoundsException
        mRefreshLayout.setDisableContentWhenRefresh(true);
        mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                startIndex = 1;
                listBeans.clear();
                getTuijian();
                getTop();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (startIndex * pageSize >= total){
                    ToastUtil.showShort("没有更多数据了");
                    mRefreshLayout.finishLoadMoreWithNoMoreData();
                    return;
                }
                startIndex += 1;
                getTuijian();
            }
        });
        listAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsBean bean = listBeans.get(position);
                if (bean.isFlg()){
                    Bundle bundle = new Bundle();
                    bundle.putString("id",bean.getCustomerBargainId());
                    bundle.putString("agentProductId",bean.getId());
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
        topAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                int i = view.getId();
                if (i == R.id.btn_kanjia) {
                    bundle.putString("id", listKanjia.get(position).getBargainProductId());
                    bundle.putString("agentProductId", listKanjia.get(position).getAgentProductId());
                    readyGo(KanjiaActivity.class, bundle);

                } else if (i == R.id.btn_kanjia_success) {
                }
            }
        });

        getTuijian();
        getTop();
    }

    private void getTop(){

        toggleShowLoading(true,"正在加载");
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        HttpManager.getInstance().post(mContext, ApiConstants.APPBARGAIN_GETMYBARGAINLIST, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                toggleRestore();
                listKanjia.clear();
                Gson gson = new Gson();
                BaseBody<KanjiaMine> baseBody = gson.fromJson(result,new TypeToken<BaseBody<KanjiaMine>>(){}.getType());
                listKanjia.addAll(baseBody.getRows());
                topAdapter.notifyDataSetChanged();
                if(listKanjia.size()==0){
                    toggleShowEmptyImage(true, R.drawable.contents_missing_pages, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleRestore();
                            getTop();
                        }
                    });
                }
            }

            @Override
            public void onFailed(String error) {
                toggleShowEmptyImage(true, R.drawable.contents_missing_pages, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleRestore();
                        getTop();
                    }
                });

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
