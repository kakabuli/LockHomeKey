package com.yun.software.kaadas.UI.activitys;

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
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.adapter.AAAAListAdapter;
import com.yun.software.kaadas.UI.adapter.ActListAdapter;
import com.yun.software.kaadas.UI.adapter.AppraiseListAdapter;
import com.yun.software.kaadas.UI.adapter.CommentLabelsAdapter;
import com.yun.software.kaadas.UI.adapter.LabelsAdapter;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.CommentsBean;
import com.yun.software.kaadas.UI.bean.GoodsBean;
import com.yun.software.kaadas.UI.bean.HotkeyBean;
import com.yun.software.kaadas.UI.view.GridItemDecoration;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;

/**
 * Created by yanliang
 * on 2019/3/15
 */
public class CommentListActivity extends BaseActivity {
    @BindView(R2.id.rv_list)
    RecyclerView recyclerView;

    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;


    @BindView(R2.id.labels)
    RecyclerView labels;

    private CommentLabelsAdapter labelsAdapter;
    private List<HotkeyBean> labelList = new ArrayList<>();

    AppraiseListAdapter listAdapter;


    private int startIndex = 1;
    private int pageSize = 10;
    private int total ;

    private GoodsBean goodsBean;

    private String searcherKey;

    private List<CommentsBean> listBeans = new ArrayList<>();
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_shop_appraise;
    }

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected View getLoadingTargetView() {
        return mRefreshLayout;
    }

    @Override
    protected void initViewsAndEvents() {
        tvTitle.setText("评论");
        getLableData();

        labelsAdapter = new CommentLabelsAdapter(labelList);
        labels.setAdapter(labelsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        labels.setLayoutManager(layoutManager);
        labelsAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                for (HotkeyBean bean:labelList){
                    bean.setCheck(false);
                }
                labelList.get(position).setCheck(true);
                labelsAdapter.notifyDataSetChanged();
                searcherKey = labelList.get(position).getKey();
                startIndex =1;
                getPinglunData();
            }
        });





        goodsBean = getIntent().getParcelableExtra("bean");
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
        params.put("id",goodsBean.getId());
        params.put("commentType",searcherKey);
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
                    toggleShowEmptyImage(true, R.drawable.discuss_missing_pages, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleRestore();
                            getPinglunData();
                        }
                    });
                }else {
                    toggleRestore();
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
                toggleShowEmptyImage(true, R.drawable.loading_missing_pages, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleRestore();
                        getPinglunData();
                    }
                });

            }
        },false);
    }


    private void getpinglunCount() {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,String> params = new HashMap<>();
        params.put("id",goodsBean.getId());
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.FRONT_PRODUCT_GETCOMMENTLISTBYPRODUCTIDTONUM, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                String goodCount = StringUtil.getJsonKeyStr(result,"comment_type_good");
                String badCount = StringUtil.getJsonKeyStr(result,"comment_type_bad");
                String middleCount = StringUtil.getJsonKeyStr(result,"comment_type_middle");
                String allCount = StringUtil.getJsonKeyStr(result,"comment_type_all");
                for (HotkeyBean bean :labelList){
                    if (bean.getKey().equals("comment_type_good")){
                        bean.setValue(bean.getValue() + "(" +goodCount+")");
                    }else if (bean.getKey().equals("comment_type_bad")){
                        bean.setValue(bean.getValue() + "(" +badCount+")");
                    }else if (bean.getKey().equals("comment_type_middle")){
                        bean.setValue(bean.getValue() + "(" +middleCount+")");
                    }else if (bean.getKey().equals("comment_type_all")){
                        bean.setValue(bean.getValue() + "(" +allCount+")");
                    }
                }

                labelsAdapter.notifyDataSetChanged();

            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },false);
    }


    private void getLableData() {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        HttpManager.getInstance().post(mContext, ApiConstants.COMMONAPP_GETALLKEYVALUE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                String keyjson = StringUtil.getJsonKeyStr(result,"comment_type");
                Gson gson = new Gson();
                List<HotkeyBean> list = gson.fromJson(keyjson,new TypeToken <List<HotkeyBean>>(){}.getType());
                labelList.clear();
                labelList.add(new HotkeyBean("全部","comment_type_all",true));
                labelList.addAll(list);
                labelList.get(0).setCheck(true);

                searcherKey = labelList.get(0).getKey();
                getpinglunCount();


            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },false);
    }

}
