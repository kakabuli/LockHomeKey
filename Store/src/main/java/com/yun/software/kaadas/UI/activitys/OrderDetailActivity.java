package com.yun.software.kaadas.UI.activitys;

import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.adapter.OderItemListAdapter;
import com.yun.software.kaadas.UI.bean.IndentInfo;
import com.yun.software.kaadas.UI.bean.OrderInfor;
import com.yun.software.kaadas.UI.view.OrderItemListView;
import com.yun.software.kaadas.Utils.OrderStatue;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import la.xiong.androidquick.tool.DialogUtil;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.dialog.CommonDialog;
import la.xiong.androidquick.ui.eventbus.EventCenter;

/**
 * Created by yanliang
 * on 2019/5/16
 */

public class OrderDetailActivity extends BaseActivity {
    @BindView(R2.id.commiteItem_list)
    OrderItemListView commiteItemListView;
    @BindView(R2.id.topConstraintLayout)
    ConstraintLayout mtopConstrainLayout;

    @BindView(R2.id.tv_name)
    TextView tvUserName;

    @BindView(R2.id.tv_phone)
    TextView tvPhone;

    @BindView(R2.id.tv_address)
    TextView tvAddress;

    @BindView(R2.id.tv_total_price)
    TextView totalPrice;

    @BindView(R2.id.tv_coupon)
    TextView tvcoupon;

    @BindView(R2.id.tv_real_price)
    TextView tvrealPrice;

    @BindView(R2.id.tv_order_no)
    TextView orderNO;

    @BindView(R2.id.tv_create_time)
    TextView tvCreateTime;

    @BindView(R2.id.ll_zhifu_type)
    LinearLayout llzhifuType;

    @BindView(R2.id.ll_zhifu_time)
    LinearLayout llzhifuTime;

    @BindView(R2.id.tv_anzhuang_time)
    TextView anzhuangTime;

    @BindView(R2.id.tv_order_statue)
    TextView orderStatueView;

    @BindView(R2.id.tv_item_01)
    TextView tvItem01View;

    @BindView(R2.id.tv_item_02)
    TextView tvItem02View;

    @BindView(R2.id.rl_coupon)
    RelativeLayout rlCouponView;
    @BindView(R2.id.lin_bottom)
    LinearLayout linBottom;

    @BindView(R2.id.view_50)
    View view50;

    @BindView(R2.id.tv_fukuan_time)
    TextView tvFukuanTime;
    @BindView(R2.id.tv_paytype)
    TextView tvPaytype;


    private OrderInfor orderInfor;
    List<OrderInfor> orderInforList = new ArrayList<>();

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected void initViewsAndEvents() {
        tvTitle.setText("订单详情");

        orderInfor = getIntent().getParcelableExtra("bean");

        if (null != orderInforList) {
            orderInforList.add(orderInfor);
        }

        getData();


    }

    private void setView() {
        tvUserName.setText(orderInfor.getTransportName());
        tvPhone.setText(orderInfor.getTransportPhone());
        tvAddress.setText(orderInfor.getTransportAddress());
        totalPrice.setText("¥" + orderInfor.getTotalPrice());
        tvcoupon.setText("¥" + orderInfor.getCouponPrice());
        tvrealPrice.setText("¥" + orderInfor.getRealPay());
        orderNO.setText(orderInfor.getOrderNo());
        tvPaytype.setText(orderInfor.getPayType());
        tvCreateTime.setText(orderInfor.getCreateDate());
        anzhuangTime.setText(orderInfor.getInstallationDate());
        orderStatueView.setText(orderInfor.getIndentStatusCN());
        tvFukuanTime.setText(orderInfor.getPayDate());
        if (orderInfor.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_PAY)) {
            //待支付
            llzhifuType.setVisibility(View.GONE);
            llzhifuTime.setVisibility(View.GONE);
            tvItem01View.setText("取消订单");
            tvItem02View.setText("去支付");
        } else if (orderInfor.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_INSTALL)) {
            //待安装
            tvItem01View.setVisibility(View.GONE);
            tvItem02View.setText("确认安装");
            tvItem02View.setBackground(getDrawable(R.drawable.light_blue_btn_bg));
        } else if (orderInfor.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_COMMENT)) {
            //待评价

            for (IndentInfo indentInfo : orderInfor.getIndentInfo()) {
                indentInfo.setShowShouhou(true);
            }
            tvItem01View.setVisibility(View.GONE);
            tvItem02View.setText("评价");
            tvItem02View.setBackground(getDrawable(R.drawable.light_blue_btn_bg));
        } else if (orderInfor.getIndentStatus().equals(OrderStatue.INDENT_STATUS_COMPLETED)) {
            //已评价

            for (IndentInfo indentInfo : orderInfor.getIndentInfo()) {
                indentInfo.setShowShouhou(true);
            }
            tvItem01View.setVisibility(View.GONE);
            tvItem02View.setVisibility(View.GONE);
        } else if (orderInfor.getIndentStatusCN().equals("待退款")) {
            linBottom.setVisibility(View.GONE);
            view50.setVisibility(View.GONE);
        } else if (orderInfor.getIndentStatusCN().equals("已退款")) {
            linBottom.setVisibility(View.GONE);
            view50.setVisibility(View.GONE);
        } else if (orderInfor.getIndentStatusCN().equals("已取消")) {
            orderStatueView.setBackgroundColor(Color.parseColor("#999999"));
            tvItem01View.setVisibility(View.GONE);
            tvItem02View.setVisibility(View.GONE);
            llzhifuType.setVisibility(View.GONE);
            llzhifuTime.setVisibility(View.GONE);
            linBottom.setVisibility(View.GONE);
            view50.setVisibility(View.GONE);
        } else if(orderInfor.getIndentStatusCN().equals("售后已处理")){
            linBottom.setVisibility(View.GONE);
            view50.setVisibility(View.GONE);
        }else if (orderInfor.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_REFUND)) {
            tvItem01View.setVisibility(View.GONE);
            tvItem02View.setVisibility(View.GONE);
            linBottom.setVisibility(View.GONE);
            view50.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(orderInfor.getCouponPrice())) {
            rlCouponView.setVisibility(View.GONE);
        }

        if (orderInfor.getIndentType().equals(OrderStatue.INDENT_TYPE_SECKILL)) {//秒杀
            if (orderInfor.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_INSTALL)) {//待安装
                tvItem01View.setVisibility(View.GONE);
                tvItem02View.setText("确认安装");
                tvItem02View.setBackground(getDrawable(R.drawable.light_blue_btn_bg));
            } else if (orderInfor.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_COMMENT)) {//待评价
                tvItem01View.setText("申请售后");
                tvItem02View.setText("评价");
            } else if (orderInfor.getIndentStatus().equals(OrderStatue.INDENT_STATUS_COMPLETED)) {//已评价
                tvItem01View.setVisibility(View.GONE);
                tvItem02View.setText("申请售后");
                tvItem02View.setBackground(getDrawable(R.drawable.light_blue_btn_bg));
            }

        } else if (orderInfor.getIndentType().equals(OrderStatue.INDENT_TYPE_GROUP)) {//团购
            if (orderInfor.getIndentStatus().equals(OrderStatue.INDENT_STATUS_IN_GROUP)) {//团购中
                linBottom.setVisibility(View.GONE);
                view50.setVisibility(View.GONE);
                //无按钮显示
            } else if (orderInfor.getIndentStatus().equals(OrderStatue.GROUP_TYPE_COMPLETED)) {//拼团成功
                tvItem01View.setVisibility(View.GONE);
                tvItem02View.setText("确认安装");
                tvItem02View.setBackground(getDrawable(R.drawable.light_blue_btn_bg));
            } else if (orderInfor.getIndentStatus().equals(OrderStatue.GROUP_TYPE_OVER)) {//拼团结束
                //无按钮显示
                linBottom.setVisibility(View.GONE);
                view50.setVisibility(View.GONE);
            }
        }

        OderItemListAdapter oderListItemAdapter = new OderItemListAdapter(mContext);
        commiteItemListView.setAdapter(oderListItemAdapter);
        oderListItemAdapter.setDatas(getNewData(orderInforList, orderInfor.getIndentInfo()), orderInfor.getId());
        oderListItemAdapter.notifyDataSetChanged();
    }

    private List<IndentInfo> getNewData(List<OrderInfor> datas, List<IndentInfo> indentInfo) {
        List<IndentInfo> list = new ArrayList<>();
        for (OrderInfor orderInfors : datas) {
            for (IndentInfo indentInfos : indentInfo) {
                indentInfos.setIndentStatus(orderInfors.getIndentStatus());
                indentInfos.setIndentType(orderInfors.getIndentType());
            }
        }
        list.addAll(indentInfo);
        return list;
    }

    @OnClick({R2.id.tv_item_01, R2.id.tv_item_02})
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        int i = view.getId();
        if (i == R.id.tv_item_01) {
            if (orderInfor.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_PAY)) {
                //待支付
                DialogUtil.getDialogBuilder(mContext)
                        .setTitle("提示")
                        .setMessage("你确定取消订单?")
                        .setPositiveButton("确定")
                        .setNegativeButton("取消")
                        .setBtnClickCallBack(new CommonDialog.DialogBtnCallBack() {
                            @Override
                            public void onDialogButClick(boolean isConfirm) {
                                if (isConfirm) {
                                    cancelOrder();
                                }
                            }
                        }).show();

//                    ToastUtil.showShort("取消订单");
            } else if (orderInfor.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_INSTALL)) {
                //待安装
                // ToastUtil.showShort("申请售后");
                bundle.putParcelable("bean", orderInfor);
                readyGo(ApplySaleAfterActivity.class, bundle);
                finish();
            } else if (orderInfor.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_COMMENT)) {
                //待评价
                bundle.putParcelable("bean", orderInfor);
                readyGo(ApplySaleAfterActivity.class, bundle);
                finish();
            } else if (orderInfor.getIndentStatus().equals(OrderStatue.INDENT_STATUS_COMPLETED)) {
                //已评价
                bundle.putParcelable("bean", orderInfor);
                readyGo(ApplySaleAfterActivity.class, bundle);
                finish();
            }

        } else if (i == R.id.tv_item_02) {
            if (orderInfor.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_PAY)) {
                //待支付
                String realPay = orderInfor.getRealPay();
                String id = orderInfor.getId();
                String orderNo = orderInfor.getOrderNo();
                bundle.putInt("type", ShopDetailsNewActivity.TYPE_NORMAL);
                bundle.putString("id", id);
                bundle.putString("orderNo", orderNo);
                bundle.putString("price", realPay);
                readyGo(PayActivity.class, bundle);
//                    PayDialog.getInstance().payDialog(getSupportFragmentManager(),mContext,orderInfor.getId(),orderInfor.getRealPay());
            } else if (orderInfor.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_INSTALL)) {
                //待安装
                DialogUtil.getDialogBuilder(mContext)
                        .setTitle("提示")
                        .setMessage("您已经确认安装成功？")
                        .setPositiveButton("确定")
                        .setNegativeButton("取消")
                        .setBtnClickCallBack(new CommonDialog.DialogBtnCallBack() {
                            @Override
                            public void onDialogButClick(boolean isConfirm) {
                                if (isConfirm) {
                                    installOrder(orderInfor);
                                }
                            }
                        }).show();
            } else if (orderInfor.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_COMMENT)) {
                //待评价
                bundle.putParcelable("bean", orderInfor);
                readyGo(CommentStartActvity.class, bundle);
                finish();
            } else if (orderInfor.getIndentStatusCN().equals("已取消")) {
//                    DialogUtil.getDialogBuilder(mContext)
//                            .setTitle("提示")
//                            .setMessage("你确定删除订单?")
//                            .setPositiveButton("确定")
//                            .setNegativeButton("取消")
//                            .setBtnClickCallBack(new CommonDialog.DialogBtnCallBack() {
//                                @Override
//                                public void onDialogButClick(boolean isConfirm) {
//                                    if(isConfirm){
//                                        deleteOrder(orderInfor);
//                                    }
//                                }
//                            }).show() ;
                //
            } else if (orderInfor.getIndentStatus().equals(OrderStatue.GROUP_TYPE_COMPLETED)) {
                //待安装
                DialogUtil.getDialogBuilder(mContext)
                        .setTitle("提示")
                        .setMessage("你确定安装?")
                        .setPositiveButton("确定")
                        .setNegativeButton("取消")
                        .setBtnClickCallBack(new CommonDialog.DialogBtnCallBack() {
                            @Override
                            public void onDialogButClick(boolean isConfirm) {
                                if (isConfirm) {
                                    installOrder(orderInfor);
                                }
                            }
                        }).show();

            }

        }
    }

    private void installOrder(OrderInfor infor) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String, Object> params = new HashMap<>();
        params.put("id", infor.getId());
        map.put("params", params);

        HttpManager.getInstance().post(mContext, ApiConstants.INDENT_AFFIRM, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                finish();
                //
            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
            }
        }, true);
    }


//    private void deleteOrder(OrderInfor infor) {
//        Map<String,Object> map = new HashMap<>();
//        map.put("token", UserUtils.getToken());
//        Map<String,Object> params = new HashMap<>();
//        params.put("id",infor.getId());
//        map.put("params",params);
//
//        HttpManager.getInstance().post(mContext, ApiConstants.INDENT_DELETE, map, new OnIResponseListener() {
//            @Override
//            public void onSucceed(String result) {
//                ToastUtil.showShort("已删除");
//                finish();
//
//            }
//            @Override
//            public void onFailed(String error) {
//                ToastUtil.showShort(error);
//            }
//        },true);
//    }

    /**
     * 买家查看订单详情
     */
    private void getData() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String, Object> params = new HashMap<>();
        params.put("id", orderInfor.getId());
        map.put("params", params);

        HttpManager.getInstance().post(mContext, ApiConstants.INDENT_DETAILS, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                Gson gson = new Gson();
                orderInfor = gson.fromJson(result, OrderInfor.class);
                setView();
//
            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
            }
        }, false);
    }

    private void cancelOrder() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String, Object> params = new HashMap<>();
        params.put("id", orderInfor.getId());
        map.put("params", params);

        HttpManager.getInstance().post(mContext, ApiConstants.INDENT_CANCEL, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                finish();
                ToastUtil.showShort("取消订单成功");
//
            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
            }
        }, true);
    }

    @Override
    protected void onEventComing(EventCenter eventCenter) {
        super.onEventComing(eventCenter);
        if (eventCenter.getEventCode() == Constans.MESSAGE_PAY_SUCCESS) {
            finish();
        }
    }
}
