package com.yun.software.kaadas.UI.activitys;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.adapter.ProductCategoryAdapter;
import com.yun.software.kaadas.UI.adapter.SearchListAdapter;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.CategoryBean;
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
import butterknife.OnClick;
import la.xiong.androidquick.tool.ToastUtil;

public class ProductListActivitybackup extends BaseActivity {
    @BindView(R2.id.rv_list)
    RecyclerView recyclerView;

    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R2.id.image_down)
    ImageView imageDown;

    @BindView(R2.id.tv_category_name)
    TextView tvCategoryName;

    @BindView(R2.id.ll_show_pop)
    LinearLayout showPopView;

    SearchListAdapter listAdapter;


    private int startIndex = 1;
    private int pageSize = 10;
    private int total ;

    private List<GoodsBean> listBeans = new ArrayList<>();
    private List<CategoryBean> listCategory = new ArrayList<>();

    private View popupView;
    private PopupWindow popupWindow;
    private String categoryId;

    private HomeCategoriesBean categoriesBean;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_list_product;
    }


    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }



    @Override
    protected void initViewsAndEvents() {


        categoriesBean = getIntent().getParcelableExtra("bean");
        if (categoriesBean == null){
            tvTitle.setText("产品分类");
            getCategoryData();
        }else {
            tvTitle.setText(categoriesBean.getName());
            showPopView.setVisibility(View.GONE);
            categoryId = categoriesBean.getId();
            getData();
        }



        listAdapter=new SearchListAdapter(listBeans);
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
                startToShopDetails(ShopDetailsNewActivity.TYPE_NORMAL,listBeans.get(position));
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
    
    
    @OnClick(R2.id.ll_show_pop)
    public void onClickVIew(View view){
        showPop();
    }

    private void showPop() {
        if (listCategory.size() == 0){
            ToastUtil.showShort("请稍后");
            return;
        }

        if (popupWindow == null ){
            popupView = LayoutInflater.from(this).inflate(R.layout.layout_product_category,null);
            popupWindow = new PopupWindow(popupView, (int) DensityUtil.dp2px(130), ViewGroup.LayoutParams.WRAP_CONTENT, true);

            int[] location = new int[2];
            imageDown.getLocationInWindow(location);

            if (!popupWindow.isShowing()){
                popupWindow.showAtLocation(imageDown,Gravity.NO_GRAVITY,location[0] - DensityUtil.dp2px(130)/2 + 10,location[1] + imageDown.getHeight());

            }

            popupWindow.setOutsideTouchable(true);
            RecyclerView rcview = popupView.findViewById(R.id.rv_list);
            ProductCategoryAdapter categoryAdapter = new ProductCategoryAdapter(listCategory);
            rcview.setLayoutManager(new LinearLayoutManager(mContext));
            rcview.setAdapter(categoryAdapter);
            categoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    startIndex = 1;
                    categoryId = listCategory.get(position).getId();
                    tvCategoryName.setText(listCategory.get(position).getName());
                    getData();
                    popupWindow.dismiss();
                }
            });
        } else {
            int[] location = new int[2];
            imageDown.getLocationInWindow(location);

            if (!popupWindow.isShowing()){
                popupWindow.showAtLocation(imageDown,Gravity.NO_GRAVITY,location[0] - DensityUtil.dp2px(130)/2 + 10,location[1] + imageDown.getHeight());

            }
        }



    }


    private void getCategoryData() {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        HttpManager.getInstance().post(mContext, ApiConstants.SYSCATEGORY_TREE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {

                Gson gson = new Gson();
                List<CategoryBean> baseBody = gson.fromJson(result,new TypeToken <List<CategoryBean>>(){}.getType());
                listCategory.addAll(baseBody);
                categoryId = listCategory.get(0).getId();
                tvCategoryName.setText(listCategory.get(0).getName());
                getData();
            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
            }
        },false);
    }

    private void getData(){
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> page = new HashMap<>();
        page.put("pageNum",String.valueOf(startIndex));
        page.put("pageSize",String.valueOf(pageSize));
        map.put("page",page);
        Map<String,String> params = new HashMap<>();
        params.put("categoryId",categoryId);
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.FRONT_PRODUCT_PAGE, map, new OnIResponseListener() {
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
        },true);
    }

}
