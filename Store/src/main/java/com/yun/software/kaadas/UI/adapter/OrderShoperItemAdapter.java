package com.yun.software.kaadas.UI.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.IndentInfo;
import com.yun.software.kaadas.UI.bean.OrderInfor;
import com.yun.software.kaadas.UI.view.OrderItemListView;
import com.yun.software.kaadas.Utils.CommonUtils;
import com.yun.software.kaadas.Utils.GsonUtil;
import com.yun.software.kaadas.Utils.OrderStatue;

import java.util.ArrayList;
import java.util.List;

import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.StringUtil;


public class OrderShoperItemAdapter extends BaseQuickAdapter<OrderInfor,BaseViewHolder> {


    private List<OrderInfor> datas ;
    private String statue;
    private String type;
    public OrderShoperItemAdapter(List<OrderInfor> datas,String statue,String type) {
        super(R.layout.adapter_item_order, datas);
        this.datas = datas;
        this.type = type;
    }


    @Override
    protected void convert(BaseViewHolder helper, OrderInfor item) {
        helper.setVisible(R.id.tv_order_delete,false);
        helper.setVisible(R.id.tv_order_return,false);
        helper.setVisible(R.id.tv_order_buy,false);
        helper.setVisible(R.id.tv_order_yuyue,false);
        helper.setVisible(R.id.tv_order_pay,false);
        helper.setVisible(R.id.tv_order_comment,false);
        helper.setVisible(R.id.tv_order_cancel,false);
        helper.setVisible(R.id.tv_order_shouhou,false);
        helper.setVisible(R.id.tv_order_install,false);
        helper.setVisible(R.id.tv_order_tuikuan,false);
        helper.addOnClickListener(R.id.tv_order_yuyue);
        helper.addOnClickListener(R.id.tv_order_delete);
        helper.addOnClickListener(R.id.tv_order_return);
        helper.addOnClickListener(R.id.tv_order_buy);
        helper.addOnClickListener(R.id.tv_order_pay);
        helper.addOnClickListener(R.id.tv_order_comment);
        helper.addOnClickListener(R.id.tv_order_cancel);
        helper.addOnClickListener(R.id.tv_order_shouhou);
        helper.addOnClickListener(R.id.lin_item);
        helper.addOnClickListener(R.id.tv_order_install);
        helper.addOnClickListener(R.id.tv_order_tuikuan);


        if (type.equals(OrderStatue.INDENT_TYPE_BASE)){//普通订单
            if(item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_COMMENT)){//待评论
                helper.setVisible(R.id.tv_order_comment,true);
                for (IndentInfo indentInfo : item.getIndentInfo()){
                    indentInfo.setShowShouhou(true);
                }

                helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.app_red));
            }else if(item.getIndentStatus().equals("")){//全部
//                if(item.getIndentStatusCN().equals("待评价")){//申请售后，评价
//                    helper.setVisible(R.id.tv_order_comment,true);
//                    helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.app_red));
//                }else if(item.getIndentStatusCN().equals("待安装")){//确认安装
//                    helper.setVisible(R.id.tv_order_install,true);
//                    helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.app_red));
//                }else if(item.getIndentStatusCN().equals("待退款")){
//                    helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.app_red));
//                }else if(item.getIndentStatusCN().equals("已退款")){//删除订单
//                    helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.color_999));
//                    helper.setVisible(R.id.tv_order_delete,true);
//                }else if(item.getIndentStatusCN().equals("已取消")){//删除订单
//                    helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.color_999));
//                    helper.setVisible(R.id.tv_order_delete,true);
//                }else if(item.getIndentStatusCN().equals("待支付")){//取消订单，立即支付
//                    helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.app_red));
//                    helper.setVisible(R.id.tv_order_pay,true);
//                    helper.setVisible(R.id.tv_order_cancel,true);
//                }else if (item.getIndentStatusCN().equals("已完成")){//删除订单，再次购买
//                    helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.color_999));
//                    helper.setVisible(R.id.tv_order_delete,true);
//                }else if (item.getIndentStatusCN().equals("已评价")){
//                    helper.setVisible(R.id.tv_order_shouhou,true);
//                    helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.app_red));
//                }

            }else if(item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_PAY)){//待付款
                helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.app_red));
                helper.setVisible(R.id.tv_order_pay,true);
                helper.setVisible(R.id.tv_order_cancel,true);
            }else if(item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_INSTALL)){//待安装
                helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.app_red));
                helper.setVisible(R.id.tv_order_install,true);

            }else if(item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_REFUND)){//待退款
                helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.app_red));
            }else if(item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_REFUNDED)){//已退款
                helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.color_999));
            }else if (item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_COMPLETED)){//已评价
                for (IndentInfo indentInfo : item.getIndentInfo()){
                    indentInfo.setShowShouhou(true);
                }
                helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.color_999));
            }else if (item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_CANCELDE)){//已取消
                helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.color_999));
            }
        }

        if (type.equals(OrderStatue.INDENT_TYPE_GROUP)){ //团购订单
            if (item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_IN_GROUP)){//团购中
                //无按钮显示
            }else if (item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_INSTALL)){//待安装
                helper.setVisible(R.id.tv_order_install,true);
            }else if (item.getIndentStatus().equals(OrderStatue.GROUP_TYPE_OVER)){//拼团结束
                //无按钮显示
            }else if (item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_COMPLETED)){//已完成
                helper.setVisible(R.id.tv_order_shouhou,true);
            }else if (item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_COMMENT)){//待评价
                helper.setVisible(R.id.tv_order_comment,true);
            }
        }

        if (type.equals(OrderStatue.INDENT_TYPE_SECKILL)){//秒杀
            if (item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_PAY)){//待支付
                helper.setVisible(R.id.tv_order_pay,true);
                helper.setVisible(R.id.tv_order_cancel,true);
            }else if (item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_INSTALL)){ //待安装
                helper.setVisible(R.id.tv_order_install,true);
            }else if (item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_COMMENT)){//待评价
                helper.setVisible(R.id.tv_order_comment,true);
            }else if (item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_COMPLETED)){//已完成
            }
        }

        helper.setText(R.id.tv_order_no,item.getOrderNo());
        helper.setText(R.id.tv_order_statue,item.getIndentStatusCN());
        helper.setText(R.id.tv_total,"共"+item.getProductNum() +"件商品 需付款：");
        helper.setText(R.id.tv_money,"¥"+item.getRealPay());

        OderItemListAdapter oderListItemAdapter=new OderItemListAdapter(mContext);
        OrderItemListView orderItemListView=helper.getView(R.id.commiteItem_list);

        orderItemListView.setAdapter(oderListItemAdapter);
//        oderListItemAdapter.setDatas(item.getIndentInfo(),item.getId());
        oderListItemAdapter.setDatas(getNewData(mData,item.getIndentInfo()),item.getId());
//        LogUtils.e("我的订单全部的数据s"+GsonUtil.beanToString(mData));
        LogUtils.e("我的订单全部的数据"+GsonUtil.beanToString(getNewData(mData,item.getIndentInfo())));
        oderListItemAdapter.notifyDataSetChanged();
    }

    private List<IndentInfo> getNewData(List<OrderInfor> datas, List<IndentInfo> indentInfo) {
        List<IndentInfo> list = new ArrayList<>();
        for(OrderInfor orderInfors : datas){
            for(IndentInfo  intentInfor :orderInfors.getIndentInfo()){
                intentInfor.setIndentStatus(orderInfors.getIndentStatus());
                intentInfor.setIndentType(orderInfors.getIndentType());
            }
        }
        list.addAll(indentInfo);
        return list;
    }
}
