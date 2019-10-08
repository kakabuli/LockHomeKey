package com.yun.software.kaadas.UI.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.adapter.AAAAListAdapter;
import com.yun.software.kaadas.UI.adapter.ShopImageListAdapter;
import com.yun.software.kaadas.UI.bean.GoodsBean;
import com.yun.software.kaadas.UI.view.GridItemDecoration;
import com.yun.software.kaadas.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import la.xiong.androidquick.tool.ToastUtil;

public class ShopDetailsFragment extends BaseFragment {

    @BindView(R2.id.rv_list)
    RecyclerView recyclerView;

    ShopImageListAdapter listAdapter;


    private List<Object> listBeans = new ArrayList<>();

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_shop_details;
    }

    @Override
    protected void initViewsAndEvents() {
        GoodsBean bean = getArguments().getParcelable("bean");

        listAdapter=new ShopImageListAdapter(bean.getDetailImgs());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(listAdapter);



    }
}
