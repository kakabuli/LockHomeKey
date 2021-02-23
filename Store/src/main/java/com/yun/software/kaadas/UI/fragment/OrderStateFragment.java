package com.yun.software.kaadas.UI.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import com.yun.software.kaadas.UI.activitys.ApplySaleAfterActivity;
import com.yun.software.kaadas.UI.activitys.CommentStartActvity;
import com.yun.software.kaadas.UI.activitys.MyOderActivity;
import com.yun.software.kaadas.UI.activitys.OrderDetailActivity;
import com.yun.software.kaadas.UI.activitys.PayActivity;
import com.yun.software.kaadas.UI.activitys.ShopDetailsNewActivity;
import com.yun.software.kaadas.UI.adapter.OrderShoperItemAdapter;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.OrderInfor;
import com.yun.software.kaadas.UI.view.dialog.PayDialog;
import com.yun.software.kaadas.Utils.OrderStatue;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import org.greenrobot.eventbus.EventBus;
import la.xiong.androidquick.tool.DialogUtil;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.dialog.CommonDialog;
import la.xiong.androidquick.ui.eventbus.EventCenter;
import la.xiong.androidquick.ui.widget.LoadingTip;
import la.xiong.androidquick.ui.widget.writeDialog.BaseDialog;

/**
 * Created by yanliang
 * on 2019/5/14
 */
public class OrderStateFragment extends BaseFragment {
     public static final  String TAG="OrderStateFragment";
    @BindView(R2.id.rv_list)
    RecyclerView rvList;
    List<OrderInfor> mdatas;
    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R2.id.loadingtip)
    LoadingTip loadingTip;

    @BindView(R2.id.toolbar)
    LinearLayout toolbar;

    @BindView(R2.id.tv_title)
    TextView tvTitle;

    private boolean isMore=false;
    private int pageNum=1;
    OrderShoperItemAdapter oderShoperItemAdapter;
    OrderInfor temOrderInfor;
    private BaseDialog dialog;
    private CommonDialog commonDialog;
    @Override
    protected View getLoadingTargetView() {
        return mRefreshLayout;
    }

    private int startIndex = 1;
    private int pageSize = 10;
    private int total ;

    private String orderStatue;
    private String orderType;
    private String title;

    private List<OrderInfor> listBeans = new ArrayList<>();
    public static OrderStateFragment getInstance(Bundle bundle) {
        OrderStateFragment orderStateFragment = new OrderStateFragment();
        if (bundle != null) {
            orderStateFragment.setArguments(bundle);
        }
        return orderStateFragment;
    }
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_oder_state;
    }
    @Override
    protected void onUserVisible() {
        super.onUserVisible();
        if (mRefreshLayout != null){
            mRefreshLayout.autoRefresh();
        }

    }

    @Override
    protected void onFirstUserVisible() {
        super.onFirstUserVisible();
        if (mRefreshLayout != null){
            mRefreshLayout.autoRefresh();
        }
    }

    @Override
    protected void initViewsAndEvents() {
        Bundle bundle=getArguments();
        if(bundle!=null){
            orderStatue =bundle.getString("statue");
            orderType = bundle.getString("type");
            title = bundle.getString("title");
            LogUtils.e("orderStatue  " + orderStatue+"  orderType "+orderType);
            LogUtils.iTag(TAG,"type"+orderStatue);
        }

        if (OrderStatue.INDENT_TYPE_BASE.equals(orderType) || TextUtils.isEmpty(orderType)){
            toolbar.setVisibility(View.GONE);
        }else {
            tvTitle.setText(title);
        }

        initAdapter();

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
//        mRefreshLayout.autoRefresh();

    }


    public void initAdapter(){
        oderShoperItemAdapter=new OrderShoperItemAdapter(listBeans,orderStatue,orderType);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        rvList.setAdapter(oderShoperItemAdapter);
        oderShoperItemAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                OrderInfor infor = listBeans.get(position);
                Bundle bundle = new Bundle();
                int i = view.getId();//删除订单

                if (i == R.id.tv_order_buy) {
                } else if (i == R.id.tv_order_delete) {//                        DialogUtil.getDialogBuilder(mContext)
//                                .setTitle("提示")
//                                .setMessage("你确定删除订单?")
//                                .setPositiveButton("确定")
//                                .setNegativeButton("取消")
//                                .setBtnClickCallBack(new CommonDialog.DialogBtnCallBack() {
//                                    @Override
//                                    public void onDialogButClick(boolean isConfirm) {
//                                        if(isConfirm){
//                                            deleteOrder(infor);
//                                        }
//                                    }
//                                }).show() ;

                } else if (i == R.id.tv_order_comment) {
                    bundle.putParcelable("bean", infor);
                    readyGo(CommentStartActvity.class, bundle);

                } else if (i == R.id.tv_order_return) {
                } else if (i == R.id.lin_item) {
                    bundle.putParcelable("bean", infor);
                    readyGo(OrderDetailActivity.class, bundle);

                } else if (i == R.id.tv_order_yuyue) {
                } else if (i == R.id.tv_order_pay) {
                    String realPay = infor.getRealPay();
                    String id = infor.getId();
                    String orderNo = infor.getOrderNo();
                    bundle.putInt("type", ShopDetailsNewActivity.TYPE_NORMAL);
                    bundle.putString("id", id);
                    bundle.putString("orderNo", orderNo);
                    bundle.putString("price", realPay);
                    readyGo(PayActivity.class, bundle);

                } else if (i == R.id.tv_order_cancel) {
                    DialogUtil.getDialogBuilder(mContext)
                            .setTitle("提示")
                            .setMessage("你是否确定取消订单?")
                            .setPositiveButton("确定")
                            .setNegativeButton("取消")
                            .setBtnClickCallBack(new CommonDialog.DialogBtnCallBack() {
                                @Override
                                public void onDialogButClick(boolean isConfirm) {
                                    if (isConfirm) {
                                        cancelOrder(infor);
                                    }
                                }
                            }).show();
//

                } else if (i == R.id.tv_order_shouhou) {
                    bundle.putParcelable("bean", infor);
                    readyGo(ApplySaleAfterActivity.class, bundle);

                } else if (i == R.id.tv_order_install) {
                    DialogUtil.getDialogBuilder(mContext)
                            .setTitle("提示")
                            .setMessage("您已经确认安装成功?")
                            .setPositiveButton("确定")
                            .setNegativeButton("取消")
                            .setBtnClickCallBack(new CommonDialog.DialogBtnCallBack() {
                                @Override
                                public void onDialogButClick(boolean isConfirm) {
                                    if (isConfirm) {
                                        installOrder(infor);
                                    }
                                }
                            }).show();

                } else if (i == R.id.tv_order_tuikuan) {
                    ToastUtil.showShort("申请退款");


                }
            }
        });
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("切换到当前界面", "界面类型是   " + orderStatue);
        if (isVisibleToUser){
            if (mRefreshLayout!=null){
                Log.e("切换到当前界面", "刷新获取数据   "   );
                mRefreshLayout.autoRefresh();
            }
        }
    }

    @OnClick(R2.id.lin_back)
    public void onClickView(View view){
        getActivity().finish();
    }



    private void cancelOrder(OrderInfor infor) {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("id",infor.getId());
        map.put("params",params);

        HttpManager.getInstance().post(mContext, ApiConstants.INDENT_CANCEL, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                ToastUtil.showShort("取消订单成功");
                EventBus.getDefault().post(new EventCenter(Constans.MESSAGE_REFRESH_ORDER));
//
            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
            }
        },true);
    }


    private Handler handler = new Handler();
    Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (mRefreshLayout != null) {
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore(true);
            }
            toggleShowEmptyImage(true, R.drawable.order_missing_pages, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleRestore();
                    getData();
                }
            });
        }
    };

    private void getData() {
        handler.postDelayed(timeoutRunnable, 5 * 1000);
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> page = new HashMap<>();
        page.put("pageNum",String.valueOf(startIndex));
        page.put("pageSize",String.valueOf(pageSize));
        map.put("page",page);
        Map<String,String> params = new HashMap<>();
        params.put("indentType",orderType);//订单类型
        params.put("indentStatus",orderStatue);//订单状态
        map.put("params",params);
        LogUtils.e("数据返回  " + orderStatue + "11");
        HttpManager.getInstance().post(mContext, ApiConstants.INDENT_MYINDENTSEARCH, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                if(startIndex == 1){
                    listBeans.clear();
                }
                LogUtils.e("数据返回  " + orderStatue);
                Gson gson = new Gson();
                BaseBody<OrderInfor> baseBody = gson.fromJson(result,new TypeToken<BaseBody<OrderInfor>>(){}.getType());
                listBeans.addAll(baseBody.getRows());
                total = baseBody.total;
                if(total==0){
                    toggleShowEmptyImage(true, R.drawable.order_missing_pages, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleRestore();
                            getData();
                        }
                    });
                }
                if (oderShoperItemAdapter == null){
                    initAdapter();
                }
                handler.removeCallbacks(timeoutRunnable);
                oderShoperItemAdapter.notifyDataSetChanged();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore(true);
            }
            @Override
            public void onFailed(String error) {
                toggleShowEmptyImage(true, R.drawable.loading_missing_pages, new View.OnClickListener() {
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

    private void installOrder(OrderInfor infor) {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("id",infor.getId());
        map.put("params",params);

        HttpManager.getInstance().post(mContext, ApiConstants.INDENT_AFFIRM, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                EventBus.getDefault().post(new EventCenter(Constans.MESSAGE_REFRESH_ORDER));

            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
            }
        },true);
    }


    @Override
    protected void onEventComing(EventCenter eventCenter) {
        super.onEventComing(eventCenter);
        if (eventCenter.getEventCode() == Constans.MESSAGE_PAY_SUCCESS){
            getData();
        }else if (eventCenter.getEventCode() == Constans.MESSAGE_REFRESH_ORDER){
            getData();
        }
//        else if (eventCenter.getEventCode() == Constans.MESSAGE_SUBMIT_ORDER){
////            getData();
//        }
    }
}
