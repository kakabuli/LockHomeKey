package com.yun.software.kaadas.UI.fragment;

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
import com.yun.software.kaadas.UI.adapter.AppraiseListAdapter;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.CommentsBean;
import com.yun.software.kaadas.UI.bean.GoodsBean;
import com.yun.software.kaadas.UI.view.GridItemDecoration;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import la.xiong.androidquick.tool.ToastUtil;

public class ShopAppraiseFragment extends BaseFragment {


    @BindView(R2.id.rv_list)
    RecyclerView recyclerView;

    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    AppraiseListAdapter listAdapter;


    private int startIndex = 1;
    private int pageSize = 10;
    private int total ;

    private GoodsBean goodsBean;

    private List<CommentsBean> listBeans = new ArrayList<>();
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_shop_appraise;
    }

    @Override
    protected View getLoadingTargetView() {
        return mRefreshLayout;
    }

    @Override
    protected void initViewsAndEvents() {

        goodsBean = getArguments().getParcelable("bean");
        listAdapter=new AppraiseListAdapter(listBeans);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //添加自定义分割线
        GridItemDecoration.Builder  decoration = new GridItemDecoration.Builder(mContext);
        decoration.setColor(getResources().getColor(R.color.default_bg_color));
        decoration.setHorizontalSpan(getResources().getDimension(R.dimen.dk_dp_15));
        recyclerView.addItemDecoration(decoration.build());

        recyclerView.setAdapter(listAdapter);
        //此处必须开启 防止用户刷星时候进行下拉操作 会引起IndexOutOfBoundsException
        mRefreshLayout.setDisableContentWhenRefresh(true);
        mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                startIndex = 1;
                getPinglunData();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (startIndex * pageSize >= total){
                    ToastUtil.showShort("没有更多数据了");
                    mRefreshLayout.finishLoadMoreWithNoMoreData();
                    return;
                }
                startIndex += 1;
                getPinglunData();
            }
        });
        listAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });


    }

    @Override
    protected void onFirstUserVisible() {
        super.onFirstUserVisible();
        getPinglunData();

    }

    private void getPinglunData(){
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> page = new HashMap<>();
        page.put("pageNum",String.valueOf(startIndex));
        page.put("pageSize",String.valueOf(pageSize));
        map.put("page",page);
        Map<String,String> params = new HashMap<>();
        params.put("id",goodsBean.getProductId());
        map.put("params",params);

        HttpManager.getInstance().post(mContext, ApiConstants.FRONT_COMMENT_PAGE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                if (startIndex == 1){
                    listBeans.clear();
                }
                Gson gson = new Gson();
                BaseBody<CommentsBean> baseBody = gson.fromJson(result,new TypeToken<BaseBody<CommentsBean>>(){}.getType());
                total=baseBody.getTotal();
                if(total==0){
                    toggleShowEmpty(true, "暂无评论", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleRestore();
                            mRefreshLayout.autoRefresh();
                        }
                    });
                }
                listBeans.addAll(baseBody.getRows());
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
