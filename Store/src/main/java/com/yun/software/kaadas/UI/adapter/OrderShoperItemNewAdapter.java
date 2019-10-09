package com.yun.software.kaadas.UI.adapter;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.activitys.ApplyDrawBackActivity;
import com.yun.software.kaadas.UI.activitys.ApplySaleAfterActivity;
import com.yun.software.kaadas.UI.bean.IndentInfo;
import com.yun.software.kaadas.UI.bean.OrderInfor;
import com.yun.software.kaadas.UI.view.OrderItemListView;
import com.yun.software.kaadas.Utils.OrderStatue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 47804 on 2019/8/9.
 */

public class OrderShoperItemNewAdapter extends BaseQuickAdapter<IndentInfo,BaseViewHolder> {
    private List<IndentInfo> datas ;
    private String statue;
    private String type;

    public OrderShoperItemNewAdapter(List<IndentInfo> datas,String statue,String type) {
        super(R.layout.adapter_item_order, datas);
        this.datas = datas;
        this.type = type;
    }

    @Override
    protected void convert(BaseViewHolder helper, IndentInfo item) {
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
        helper.setVisible(R.id.tv_order_statue,false)
        .setVisible(R.id.clbom,false);

        TextView tvStutas = helper.getView(R.id.tv_status);

        if ("indent_status_wait_install".equals(item.getIndentStatus())){//待安装
            tvStutas.setVisibility(View.VISIBLE);
            tvStutas.setText("申请退款");
            tvStutas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ApplyDrawBackActivity.class);
                    intent.putExtra("bean",item);
                    intent.putExtra("id",item.getId());
                    mContext.startActivity(intent);
                }
            });
        }else if("indent_info_status_refund_of".equals(item.getStatus())){//退款中
            tvStutas.setVisibility(View.VISIBLE);
            tvStutas.setText("退款中");
            tvStutas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }else if("indent_info_status_refund_to_complete".equals(item.getStatus())){//退款完成
            tvStutas.setVisibility(View.VISIBLE);
            tvStutas.setText("退款完成");
            tvStutas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }else if("indent_status_wait_comment".equals(item.getIndentStatus())){//待评价
            tvStutas.setVisibility(View.VISIBLE);
            tvStutas.setText("申请售后");
            tvStutas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ApplySaleAfterActivity.class);
                    intent.putExtra("bean",item);
                    intent.putExtra("id",item.getId());
                    mContext.startActivity(intent);
                }
            });
        }else if("indent_info_status_after_underway".equals(item.getStatus())){//售后处理中
            tvStutas.setVisibility(View.VISIBLE);
            tvStutas.setText("售后处理中");
            tvStutas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }else if("indent_info_status_after_complete".equals(item.getStatus())){//售后完成
            tvStutas.setVisibility(View.VISIBLE);
            tvStutas.setText("售后完成");
            tvStutas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }else {
            tvStutas.setVisibility(View.GONE);
        }



        if (type.equals(OrderStatue.INDENT_TYPE_BASE)){//普通订单
            if(item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_COMMENT)){//待评论
                helper.setVisible(R.id.tv_order_comment,true);
                for (IndentInfo indentInfo : mData){
                    indentInfo.setShowShouhou(true);
                }

//                helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.app_red));
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
//                helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.app_red));
                helper.setVisible(R.id.tv_order_pay,true);
                helper.setVisible(R.id.tv_order_cancel,true);
            }else if(item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_INSTALL)){//待安装
//                helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.app_red));
                helper.setVisible(R.id.tv_order_install,true);

            }else if(item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_REFUND)){//待退款
//                helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.app_red));
            }else if(item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_REFUNDED)){//已退款
//                helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.color_999));
            }else if (item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_COMPLETED)){//已评价
                for (IndentInfo indentInfo : mData){
                    indentInfo.setShowShouhou(true);
                }
                helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.app_red));
            }else if (item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_CANCELDE)){//已取消
                helper.setTextColor(R.id.tv_order_statue,mContext.getResources().getColor(R.color.color_999));
            }
        }


        if (type.equals(OrderStatue.INDENT_TYPE_GROUP)){ //团购订单
            if (item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_IN_GROUP)){//团购中
                //无按钮显示
            }else if (item.getIndentStatus().equals(OrderStatue.GROUP_TYPE_COMPLETED)){//拼团成功
                helper.setVisible(R.id.tv_order_install,true);
            }else if (item.getIndentStatus().equals(OrderStatue.GROUP_TYPE_OVER)){//拼团结束
                //无按钮显示
            }else if (item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_COMPLETED)){//已完成
                helper.setVisible(R.id.tv_order_shouhou,true);
            }else if (item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_WAIT_COMMENT)){//待评价
                helper.setVisible(R.id.tv_order_shouhou,true);
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
                helper.setVisible(R.id.tv_order_shouhou,true);
            }else if (item.getIndentStatus().equals(OrderStatue.INDENT_STATUS_COMPLETED)){//已完成
                helper.setVisible(R.id.tv_order_shouhou,true);
            }
        }

        helper.setText(R.id.tv_order_no,item.getOrderNo());
//        helper.setText(R.id.tv_order_statue,item.getIndentStatusCN());
        helper.setText(R.id.tv_total,"共"+item.getQty() +"件商品 需付款：");
        helper.setText(R.id.tv_money,"¥"+item.getPrice());

        OderItemListAdapter oderListItemAdapter=new OderItemListAdapter(mContext);
        OrderItemListView orderItemListView=helper.getView(R.id.commiteItem_list);

        orderItemListView.setAdapter(oderListItemAdapter);
        oderListItemAdapter.setDatas(item,item.getId()+"");
        oderListItemAdapter.notifyDataSetChanged();
    }
}
