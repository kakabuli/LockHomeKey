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
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.adapter.OrderShoperItemAdapter;
import com.yun.software.kaadas.UI.adapter.OrderShoperItemNewAdapter;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.IndentInfo;
import com.yun.software.kaadas.UI.bean.OrderInfor;
import com.yun.software.kaadas.Utils.OrderStatue;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.dialog.CommonDialog;
import la.xiong.androidquick.ui.widget.LoadingTip;
import la.xiong.androidquick.ui.widget.writeDialog.BaseDialog;

/**
 * Created by yanliang
 * on 2019/3/15
 */
public class SaleAfterListActivity extends BaseActivity {
    @BindView(R2.id.rv_list)
    RecyclerView rvList;
    List<OrderInfor> mdatas;
    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R2.id.loadingtip)
    LoadingTip loadingTip;
    private boolean isMore=false;
    private int pageNum=1;

    OrderInfor temOrderInfor;
    private BaseDialog dialog;
    private CommonDialog commonDialog;

    private int startIndex = 1;
    private int pageSize = 10;
    private int total ;

    OrderShoperItemNewAdapter oderShoperItemAdapter;
    private List<IndentInfo> listBeans = new ArrayList<>();


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_oder_sale_after;
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
        tvTitle.setText("申请售后");
//        mSaleAfterAdapter=new SaleAfterItemAdapter(TestDateUtils.getcarItems());
        oderShoperItemAdapter=new OrderShoperItemNewAdapter(listBeans,"","");
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        rvList.setAdapter(oderShoperItemAdapter);
        oderShoperItemAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int i = view.getId();//                    case R.id.tv_applay_sale_after:
////                        readyGo(ApplySaleAfterActivity.class, Bundle);
//                        break;
                if (i == R.id.lin_item) {
                    String id = listBeans.get(position).getIndentDetailId() + "";
                    String statue = listBeans.get(position).getIndentInfoStatus();

                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    bundle.putString("statue", statue);
                    readyGo(AfterSaleDetailActivity.class, bundle);

                }
            }
        });

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                startIndex = 1;
                getData();

            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
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

        getData();
    }


    private void getData() {
        toggleShowLoading(true,"正在加载");
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> page = new HashMap<>();
        page.put("pageNum",String.valueOf(startIndex));
        page.put("pageSize",String.valueOf(pageSize));
        map.put("page",page);
//        Map<String,String> params = new HashMap<>();
//        params.put("indentType","");//订单类型
//        params.put("indentStatus","indent_status_wait_refund");//订单状态
//        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.INDENT_MYINDENTSEARCH_NEW, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                LogUtils.e("售后列表数据"+result);
                toggleRestore();
                if(startIndex == 1){
                    listBeans.clear();
                }
                Gson gson = new Gson();
                BaseBody<IndentInfo> baseBody = gson.fromJson(result,new TypeToken<BaseBody<IndentInfo>>(){}.getType());
                listBeans.addAll(baseBody.getRows());
                total = baseBody.total;
                if(total==0){
                    toggleShowEmptyImage(true, R.drawable.contents_missing_pages, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleRestore();
                            getData();
                        }
                    });
                }
                oderShoperItemAdapter.notifyDataSetChanged();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore(true);
            }
            @Override
            public void onFailed(String error) {
                toggleShowEmptyImage(true, R.drawable.contents_missing_pages, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleRestore();
                        getData();
                    }
                });
                ToastUtil.showShort(error);
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore(true);

            }
        },false);
    }


}
