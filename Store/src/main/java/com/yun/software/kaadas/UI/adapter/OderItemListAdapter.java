package com.yun.software.kaadas.UI.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.activitys.ApplyDrawBackActivity;
import com.yun.software.kaadas.UI.activitys.ApplySaleAfterActivity;
import com.yun.software.kaadas.UI.bean.IndentInfo;
import com.yun.software.kaadas.UI.view.OrderItemListView;
import com.yun.software.kaadas.Utils.TransformationUtils;

import java.util.ArrayList;
import java.util.List;

import la.xiong.androidquick.tool.Glid.GlidUtils;
import la.xiong.androidquick.tool.LogUtils;


/**
 * des:评论适配器
 * Created by xsf
 * on 2016.07.11:11
 */
public class OderItemListAdapter {

    private Context mContext;
    private OrderItemListView mListview;
    private List<IndentInfo> mDatas;
    private String id;

    public OderItemListAdapter(Context context) {
        mContext = context;
        mDatas = new ArrayList<IndentInfo>();
    }

    //
    public void bindListView(OrderItemListView listView) {
        if (listView == null) {
            throw new IllegalArgumentException("CommentListView is null....");
        }
        mListview = listView;
    }

    //
    public void setDatas(List<IndentInfo> datas,String id) {
        if (datas == null) {
            datas = new ArrayList<IndentInfo>();
        }

        mDatas = datas;
        this.id = id;
    }

    //
    public void setDatas(IndentInfo info,String id) {
        List<IndentInfo> datas  = new ArrayList<IndentInfo>();
        datas.add(info);
        mDatas = datas;
        this.id = id;
    }

    public List<IndentInfo> getDatas() {
        return mDatas;
    }

    public int getCount() {        if (mDatas == null) {

            return 0;
        }
        return mDatas.size();
    }

    //
    public IndentInfo getItem(int position) {
        if (mDatas == null) {
            return null;
        }
        if (position < mDatas.size()) {
            return mDatas.get(position);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    private View getView(final int position) {
        IndentInfo carItem =mDatas.get(position);
        View convertView = View.inflate(mContext, R.layout.item_order_list_item, null);
        TextView tvName = convertView.findViewById(R.id.tv_name);
        ImageView imageView = convertView.findViewById(R.id.image);
        TextView tvAttr = convertView.findViewById(R.id.tv_attr);
        TextView tvMoney = convertView.findViewById(R.id.tv_money);
        TextView tvCount = convertView.findViewById(R.id.tv_count);
        TextView tvShouhou = convertView.findViewById(R.id.tv_order_shouhou);

        tvName.setText(carItem.getProductName());
        tvAttr.setText(carItem.getProductLabels());
        tvMoney.setText("¥" + carItem.getPrice());
        tvCount.setText("x" + carItem.getQty());

        if("indent_type_base".equals(carItem.getIndentType())){
            if ("indent_status_wait_install".equals(carItem.getIndentStatus())){//待安装
                tvShouhou.setVisibility(View.VISIBLE);
                if("indent_info_status_refund_of".equals(carItem.getStatus())){//退款中
                    tvShouhou.setText("退款中");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }else if("indent_info_status_refund_to_complete".equals(carItem.getStatus())){//退款完成
                    tvShouhou.setText("退款完成");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }else if("indent_info_status_after_underway".equals(carItem.getStatus())){//售后处理中
                    tvShouhou.setText("售后处理中");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }else if("indent_info_status_after_complete".equals(carItem.getStatus())){//售后完成
                    tvShouhou.setText("售后完成");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }else {
                    tvShouhou.setText("申请退款");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ApplyDrawBackActivity.class);
                            intent.putExtra("bean",carItem);
                            intent.putExtra("id",id);
                            mContext.startActivity(intent);
                        }
                    });
                }
            }else if("indent_status_wait_comment".equals(carItem.getIndentStatus()) ||
                    "indent_status_completed".equals(carItem.getIndentStatus())){//待评价和已评价
                tvShouhou.setVisibility(View.VISIBLE);
                if("indent_info_status_after_underway".equals(carItem.getStatus())){//售后处理中
                    tvShouhou.setText("售后处理中");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }else if("indent_info_status_after_complete".equals(carItem.getStatus())){//售后完成
                    tvShouhou.setText("售后完成");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                } else if("indent_info_status_refund_of".equals(carItem.getStatus())){//退款中
                    tvShouhou.setText("退款中");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }else if("indent_info_status_refund_to_complete".equals(carItem.getStatus())){//退款完成
                    tvShouhou.setText("退款完成");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }else if("indent_info_status_after_underway".equals(carItem.getStatus())){//售后处理中
                    tvShouhou.setText("售后处理中");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }else {
                    tvShouhou.setText("申请售后");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ApplySaleAfterActivity.class);
                            intent.putExtra("bean",carItem);
                            intent.putExtra("id",id);
                            mContext.startActivity(intent);
                        }
                    });
                }
            }else if("indent_status_refunded".equals(carItem.getIndentStatus())){//售后已处理
                tvShouhou.setVisibility(View.VISIBLE);
                if("indent_info_status_refund_of".equals(carItem.getStatus())){//售后退款中
                    tvShouhou.setText("退款中");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }else if("indent_info_status_refund_to_complete".equals(carItem.getStatus())){
                    tvShouhou.setText("退款完成");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }
            }else {
                tvShouhou.setVisibility(View.GONE);
            }
        }else {
            if ("indent_status_wait_install".equals(carItem.getIndentStatus())){//待安装
                tvShouhou.setVisibility(View.GONE);
            }else if("indent_status_wait_comment".equals(carItem.getIndentStatus()) ||
                    "indent_status_completed".equals(carItem.getIndentStatus())){//待评价和已评价
                tvShouhou.setVisibility(View.VISIBLE);
                if("indent_info_status_after_underway".equals(carItem.getStatus())){//售后处理中
                    tvShouhou.setText("售后处理中");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }else if("indent_info_status_after_complete".equals(carItem.getStatus())){//售后完成
                    tvShouhou.setText("售后完成");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }else {
                    tvShouhou.setText("申请售后");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ApplySaleAfterActivity.class);
                            intent.putExtra("bean",carItem);
                            intent.putExtra("id",id);
                            mContext.startActivity(intent);
                        }
                    });
                }
            }else if("indent_status_refunded".equals(carItem.getIndentStatus())){//售后已处理
                tvShouhou.setVisibility(View.VISIBLE);
                if("indent_info_status_refund_of".equals(carItem.getStatus())){//售后退款中
                    tvShouhou.setText("退款中");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }else if("indent_info_status_refund_to_complete".equals(carItem.getStatus())){
                    tvShouhou.setText("退款完成");
                    tvShouhou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }
            }else {
                tvShouhou.setVisibility(View.GONE);
            }
        }

        GlidUtils.loadImageNormal(mContext,carItem.getLogo(),imageView);

        return convertView;
    }


    public interface OderListItemClickLisener{
        void onclickshowOder(IndentInfo listBean);
        void onclickDownFile(IndentInfo listBean);
    }
    private OderListItemClickLisener oderListItemClickLisener;
    public void setOderListItemClickLisener(OderListItemClickLisener oderListItemClickLisener){
        this.oderListItemClickLisener=oderListItemClickLisener;
    }
    public void notifyDataSetChanged() {
        if (mListview == null) {
            throw new NullPointerException("listview is null, please bindListView first...");
        }
        mListview.removeAllViews();
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < mDatas.size(); i++) {
            final int index = i;
            View view = getView(index);
            if (view == null) {
                throw new NullPointerException("listview item layout is null, please check getView()...");
            }

            mListview.addView(view, index, layoutParams);
        }

    }
    public void destory() {

    }

}
