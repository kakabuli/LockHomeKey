package com.yun.software.kaadas.UI.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.EarningDetailsBean;

import java.util.List;

/**
 * 分组列表适配器
 * Create by: chenWei.li
 * Date: 2018/11/3
 * Time: 下午8:19
 * Email: lichenwei.me@foxmail.com
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<EarningDetailsBean> mList;

    public RecyclerViewAdapter(Context context, List<EarningDetailsBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_linear_content, parent, false);
            viewHolder = new ViewHolder(itemView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EarningDetailsBean bean =  mList.get(position);
        holder.tvTimeView.setText(bean.getDaytime());
        holder.tvYUEView.setText(bean.getBalance());
        holder.tvDescView.setText(bean.getCommissionTypeCN());
        holder.tvMoneyView.setText(bean.getMoney());

        if (bean.getMoney().contains("+")){
            holder.tvMoneyView.setTextColor(Color.parseColor("#F36A5F"));
        }else {
            holder.tvMoneyView.setTextColor(Color.parseColor("#41B982"));
        }
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    /**
     * 判断position对应的Item是否是组的第一项
     *
     * @param position
     * @return
     */
    public boolean isItemHeader(int position) {
        if (position == 0 || mList.size() == 1) {
            return true;
        } else {
            String lastGroupName = mList.get(position - 1).getGroupName();
            String currentGroupName = mList.get(position).getGroupName();
            //判断上一个数据的组别和下一个数据的组别是否一致，如果不一致则是不同组，也就是为第一项（头部）
            if (lastGroupName.equals(currentGroupName)) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 获取position对应的Item组名
     *
     * @param position
     * @return
     */
    public String getGroupName(int position) {
        return mList.get(position).getGroupName();
    }


    /**
     * 自定义ViewHolder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTimeView;
        TextView tvYUEView;
        TextView tvMoneyView;
        TextView tvDescView;


        public ViewHolder(View itemView) {
            super(itemView);
            tvTimeView = itemView.findViewById(R.id.tv_time);
            tvMoneyView = itemView.findViewById(R.id.tv_money);
            tvDescView = itemView.findViewById(R.id.tv_desc);
            tvYUEView = itemView.findViewById(R.id.tv_yue);
        }
    }

}
