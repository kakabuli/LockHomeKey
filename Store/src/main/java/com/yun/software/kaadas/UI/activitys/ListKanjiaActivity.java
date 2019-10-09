package com.yun.software.kaadas.UI.activitys;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.adapter.KanjiaIngAdapter;
import com.yun.software.kaadas.UI.adapter.SearchListAdapter;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.GoodsBean;
import com.yun.software.kaadas.UI.bean.HomeCategoriesBean;
import com.yun.software.kaadas.UI.view.GridItemDecoration;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import la.xiong.androidquick.tool.ToastUtil;

public class ListKanjiaActivity extends BaseActivity {
    @BindView(R2.id.rv_list)
    RecyclerView recyclerView;

    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;



    private KanjiaIngAdapter listAdapter;


    private int startIndex = 1;
    private int pageSize = 10;
    private int total ;

    private List<GoodsBean> listBeans = new ArrayList<>();

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_list_zhongchou;
    }
    @Override
    protected View getLoadingTargetView() {
        return mRefreshLayout;
    }


    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }



    @Override
    protected void initViewsAndEvents() {
        tvTitle.setText("砍价");
        getData();



        listAdapter=new KanjiaIngAdapter(listBeans);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //添加自定义分割线
        GridItemDecoration.Builder  decoration = new GridItemDecoration.Builder(this);
        decoration.setColor(getResources().getColor(R.color.default_bg_color));
        decoration.setHorizontalSpan(getResources().getDimension(R.dimen.dp_2));
        recyclerView.addItemDecoration(decoration.build());

        recyclerView.setAdapter(listAdapter);
        //此处必须开启 防止用户刷星时候进行下拉操作 会引起IndexOutOfBoundsException
        mRefreshLayout.setDisableContentWhenRefresh(true);
        mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                startIndex = 1;
                getData();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (startIndex * pageSize >= total){
                    ToastUtil.showShort("没有更多数据了");
                    mRefreshLayout.finishLoadMoreWithNoMoreData();
                    return;
                }
                startIndex += 1;
                getData();
            }
        });
        listAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsBean bean = listBeans.get(position);
                if (bean.isFlg()){
                    //砍价中
                    Bundle bundle = new Bundle();
                    bundle.putString("id",bean.getCustomerBargainId());
                    bundle.putString("agentProductId",bean.getId());
                    readyGo(KanjiaActivity.class,bundle);
                }else {
                    startToShopDetails(ShopDetailsNewActivity.TYPE_KANJIA,bean);
                }
            }
        });



    }

    private void startToShopDetails(int type,GoodsBean bean ){
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        bundle.putString("statue",bean.getBusinessType());
        bundle.putString("id",bean.getId());
        bundle.putString("actid",bean.getActivityProductId());
        readyGo(ShopDetailsNewActivity.class,bundle);
    }
    

    private void getData() {
        toggleShowLoading(true,"正在加载");

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
                toggleRestore();

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
                if(listBeans.size()==0){
                    toggleShowEmptyImage(true, R.drawable.contents_missing_pages, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleRestore();
                            getData();
                        }
                    });
                }

            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore(true);
                toggleShowEmptyImage(true, R.drawable.loading_missing_pages, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleRestore();
                        getData();
                    }
                });

            }
        },false);
    }


}
