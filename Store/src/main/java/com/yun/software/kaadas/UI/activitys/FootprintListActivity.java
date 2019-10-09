package com.yun.software.kaadas.UI.activitys;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.yun.software.kaadas.UI.adapter.FootprintAdapter;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.EarningBaseBean;
import com.yun.software.kaadas.UI.bean.EarningDetailsBean;
import com.yun.software.kaadas.UI.bean.MyCollection;
import com.yun.software.kaadas.UI.bean.MyCollectionBaseBean;
import com.yun.software.kaadas.UI.view.FootprintHeaderDecoration;
import com.yun.software.kaadas.UI.view.GridItemDecoration;
import com.yun.software.kaadas.UI.view.StickHeaderDecoration;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;

/**
 * Created by yanliang
 * on 2019/3/15
 */
public class FootprintListActivity extends BaseActivity {
    @BindView(R2.id.rv_list)
    RecyclerView recyclerView;

    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R2.id.rl_bottom)
    RelativeLayout bottomView;

    @BindView(R2.id.root_view)
    LinearLayout rootView;

    @BindView(R2.id.image_check)
    ImageView imageCheck;

    FootprintAdapter listAdapter;

    private boolean isCheckAll = false;

    private int startIndex = 1;
    private int pageSize = 10;
    private int total ;

    private boolean editStatus = false;

    private List<MyCollectionBaseBean> listBeans = new ArrayList<>();
    private List<MyCollection> list = new ArrayList<>();

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_footprint;
    }
    @Override
    protected View getLoadingTargetView() {
        return rootView;
    }

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected void initViewsAndEvents() {
        tvTitle.setText("我的足迹");
        tvRight.setText("管理");
        tvRight.setVisibility(View.VISIBLE);

        listAdapter=new FootprintAdapter(this,list);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //添加自定义分割线
        recyclerView.addItemDecoration(new FootprintHeaderDecoration(mContext));

        recyclerView.setAdapter(listAdapter);
        //此处必须开启 防止用户刷星时候进行下拉操作 会引起IndexOutOfBoundsException
        mRefreshLayout.setDisableContentWhenRefresh(true);
        mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                startIndex = 1;
                listBeans.clear();
                list.clear();
                isCheckAll = false;
                imageCheck.setImageResource(R.drawable.selectbox_nor);
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

        tvRight.setOnClickListener(view ->{
            editStatus = !editStatus;
            listAdapter.setEditStatus(editStatus);
            if (editStatus){
                tvRight.setText("完成");
                bottomView.setVisibility(View.VISIBLE);
            }else {
                tvRight.setText("管理");
                bottomView.setVisibility(View.GONE);
            }
        });
        listAdapter.setOnClickListener(new FootprintAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                if (editStatus){
                    isCheckAll = true;
                    for (MyCollection bean:list){
                        if (!bean.isChecked()){
                            isCheckAll = false;
                            break;
                        }
                    }
                    if (isCheckAll){
                        imageCheck.setImageResource(R.drawable.selectbox_sel);
                    }else {
                        imageCheck.setImageResource(R.drawable.selectbox_nor);
                    }
                }else {
                    MyCollection collection = list.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",ShopDetailsNewActivity.TYPE_NORMAL);
                    bundle.putString("id",collection.getProductId());
                    readyGo(ShopDetailsNewActivity.class,bundle);
                }



            }
        });
        getData();


    }

    @OnClick({R2.id.image_check,R2.id.text_check,R2.id.delete})
    public void onClickView(View view){
        int i = view.getId();//全选
        if (i == R.id.image_check || i == R.id.text_check) {
            isCheckAll = !isCheckAll;
            if (isCheckAll) {
                imageCheck.setImageResource(R.drawable.selectbox_sel);
            } else {
                imageCheck.setImageResource(R.drawable.selectbox_nor);
            }
            for (MyCollection bean : list) {
                bean.setChecked(isCheckAll);
            }
            listAdapter.notifyDataSetChanged();


        } else if (i == R.id.delete) {
            deleteData();

        }
    }





    private void deleteData() {

        List<String> lists = new ArrayList<>();
        for (MyCollection bean:list){
            if (bean.isChecked()){
                lists.add(bean.getId());
            }
        }

        if (lists.size() == 0){
            ToastUtil.showShort("需要选择商品");
            return;
        }



        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("id",lists);
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.COLLECTAPP_DELETE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {

                Iterator<MyCollection> it = list.iterator();
                while(it.hasNext()){
                    MyCollection x = it.next();
                    if(x.isChecked()){
                        it.remove();
                    }
                }

                listAdapter.notifyDataSetChanged();
                if (list.size() == 0){
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

            }
        },true);

    }


    private void getData() {
        toggleShowLoading(true,"正在加载");

        Map<String,Object> map = new HashMap<>();
        map.put("openid","");
        map.put("token", UserUtils.getToken());
        map.put("device","3");//设备类型1 manage，2PC，3 andriod，4 ios，5 h5
        Map<String,Object> page = new HashMap<>();
        page.put("pageNum",String.valueOf(startIndex));
        page.put("pageSize",String.valueOf(pageSize));
        map.put("page",page);
        HttpManager.getInstance().post(mContext, ApiConstants.COLLECTAPP_VIEWPAGE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                toggleRestore();
                Gson gson = new Gson();
                BaseBody<MyCollectionBaseBean> baseBody = gson.fromJson(result,new TypeToken<BaseBody<MyCollectionBaseBean>>(){}.getType());
                listBeans.addAll(baseBody.getRows());
                total = baseBody.total;

                for (MyCollectionBaseBean baseBean : listBeans){
                    String groupName = baseBean.getDate();
                    for (MyCollection detailsBean : baseBean.getList()){
                        detailsBean.setGroupName(groupName);
                        list.add(detailsBean);
                    }
                }

                listAdapter.notifyDataSetChanged();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore(true);
                if(total==0){
                    tvRight.setVisibility(View.GONE);
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
                tvRight.setVisibility(View.GONE);
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
