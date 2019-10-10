package com.yun.software.kaadas.UI.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.adapter.AddreeItemAdpater;
import com.yun.software.kaadas.UI.bean.AddressBean;
import com.yun.software.kaadas.UI.bean.AddressItem;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.view.GridItemDecoration;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import la.xiong.androidquick.tool.DialogUtil;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.dialog.CommonDialog;
import la.xiong.androidquick.ui.eventbus.EventCenter;


/**
 * Created by yanliang
 * on 2018/9/5 10:59
 */

public class ManageAddress extends BaseActivity {
    @BindView(R2.id.rv_list)
    RecyclerView rvList;

    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    AddreeItemAdpater AddreeAdapter;
    @BindView(R2.id.iv_root)
    View ivroot;
    private List<AddressItem> mdatas;

    private int defaultPostion = 0;
    private int startIndex = 1;
    private int pageSize = 10;
    private int total ;

    private CommonDialog commonDialog;
    private int frompage=0;
    private boolean isFirstLoading=true;

    private String canBack ;

    private List<AddressBean> list = new ArrayList<>();
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_address_manager;
    }

    @Override
    protected View getLoadingTargetView() {
        return ivroot;
    }

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected void initViewsAndEvents() {

        canBack = getIntent().getStringExtra("back");

        tvTitle.setText("地址管理");
        tvRight.setText("+新建地址");
        tvRight.setVisibility(View.VISIBLE);

        tvRight.setTextColor(Color.parseColor("#333333"));
        mRefreshLayout.setEnableRefresh(true);//启用刷新
        mRefreshLayout.setEnableLoadMore(true);//启用加载
        mdatas = new ArrayList<AddressItem>();
        AddreeAdapter = new AddreeItemAdpater(list);
        AddreeAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        rvList.setAdapter(AddreeAdapter);
        loadAddressList();
        //添加自定义分割线
        GridItemDecoration.Builder  decoration = new GridItemDecoration.Builder(mContext);
        decoration.setColor(getResources().getColor(R.color.default_bg_color));
        decoration.setHorizontalSpan(getResources().getDimension(R.dimen.dk_dp_15));
        rvList.addItemDecoration(decoration.build());
        AddreeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!"1".equals(canBack)){
                    EventBus.getDefault().post(new EventCenter<>(Constans.MESSAGE_ORDER_ADDRESS,list.get(position)));
                    finish();
                }
            }
        });
        AddreeAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                defaultPostion=position;
                int i = view.getId();//编辑
//设置默认地址
//删除
                if (i == R.id.ly_edit) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("bean", list.get(defaultPostion));
                    readyGoForResult(AddOrEditAddress.class, Constans.REQUEST_CODE_ADDRESS, bundle);

                } else if (i == R.id.lin_checkicon) {
                    if (!"1".equals(list.get(position))) {
                        setDefault();
                    }

                } else if (i == R.id.ly_delete) {
                    showDeleteDialog();

                }

            }
        });
        //刷新
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                startIndex = 1;
                loadAddressList();
            }
        });
        //加载更多
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (startIndex * pageSize >= total){
                    ToastUtil.showShort("没有更多数据了");
                    mRefreshLayout.finishLoadMoreWithNoMoreData();
                    return;
                }
                startIndex += 1;
            }
        });
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readyGoForResult(AddOrEditAddress.class, Constans.REQUEST_CODE_ADDRESS);

            }
        });
    }

    public void showDeleteDialog(){
        commonDialog= DialogUtil.getDialogBuilder(mContext).
                setTitle("提示").
                setMessage("确认删除吗？").
                setCancelable(true).
                setPositiveButton("确定").
                setNegativeButton("取消").
                setBtnClickCallBack(new CommonDialog.DialogBtnCallBack() {
                    @Override
                    public void onDialogButClick(boolean isConfirm) {
                        if (isConfirm){
                            deleteAddress(list.get(defaultPostion).getId());
                            commonDialog.cancel();
                        }else{
                            commonDialog.cancel();
                        }
                        //                                    callback.onGranted();
                    }
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddressList();
    }

    public void loadAddressList(){
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> page = new HashMap<>();
        page.put("pageNum",String.valueOf(startIndex));
        page.put("pageSize",String.valueOf(pageSize));
        map.put("page",page);
        HttpManager.getInstance().post(mContext, ApiConstants.USERADDRESSAPP_PAGE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                toggleRestore();
                list.clear();
                Gson gson = new Gson();
                BaseBody<AddressBean> beanList = gson.fromJson(result,new TypeToken<BaseBody<AddressBean>>(){}.getType());
                list.addAll(beanList.getRows());
//                getDefaultAdd(list);
                total=beanList.getTotal();
                if(total==0){
                    toggleShowEmptyImage(true, R.drawable.address_missing_pages, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleRestore();
                            mRefreshLayout.autoRefresh();
                        }
                    });
                }
                AddreeAdapter.notifyDataSetChanged();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
            }

            @Override
            public void onFailed(String error) {
                toggleRestore();
                toggleShowEmptyImage(true, R.drawable.address_missing_pages, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleRestore();
                        mRefreshLayout.autoRefresh();
                    }
                });
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
            }
        },false);
    }

//    private List<AddressBean> getDefaultAdd(List<AddressBean> datalist) {
//        List<AddressBean> list = new ArrayList<>();
//        for(AddressBean addressBean : datalist){
//            if("1".equals(addressBean.getIsDefault())){
//
//            }
//        }
//        return list;
//    }


    public void deleteAddress(String id){
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        int[] ids = new int[]{Integer.parseInt(id)};
        Map<String,Object> params = new HashMap<>();
        params.put("id",ids);
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.USERADDRESSAPP_DELETE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                list.remove(defaultPostion);
                if (list.size() == 0) {
                    toggleShowEmptyImage(true, R.drawable.address_missing_pages, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleRestore();
                            mRefreshLayout.autoRefresh();
                        }
                    });
                }
                AddreeAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
            }
        },true);
    }
    private void setDefault(){
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());

        Map<String,String> params = new HashMap<>();
        params.put("id",list.get(defaultPostion).getId());

        map.put("params",params);

        HttpManager.getInstance().post(mContext, ApiConstants.USERADDRESSAPP_UPDATEADDRESS, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                for (AddressBean addressBean:list){
                    addressBean.setIsDefault("0");
                }
                list.get(defaultPostion).setIsDefault("1");
                AddreeAdapter.notifyDataSetChanged();
                ToastUtil.showShort("设置成功");
            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },true);
    }


    @Override
    protected void onEventComing(EventCenter eventCenter) {
//        super.onEventComing(eventCenter);
//        if (eventCenter.getEventCode() == Constants.ADDRESS_CHANGE) {
//            pageNum=1;
//            loadAddressList();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constans.REQUEST_CODE_ADDRESS){
            loadAddressList();
        }
    }
}
