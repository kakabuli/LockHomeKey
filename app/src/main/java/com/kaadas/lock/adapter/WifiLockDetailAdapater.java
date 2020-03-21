package com.kaadas.lock.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.bean.WifiLockFunctionBean;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.LogUtils;

import java.util.List;

public class WifiLockDetailAdapater extends RecyclerView.Adapter {
    private List<WifiLockFunctionBean> data;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public WifiLockDetailAdapater(List<WifiLockFunctionBean> data, OnItemClickListener onItemClickListener) {
        this.data = data;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_wifilock_detail, parent, false);
        view.getLayoutParams().height = parent.getHeight() / 2;
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        MyHolder holder = (MyHolder) viewHolder;

        WifiLockFunctionBean lockFunctionBean = data.get(position);
        holder.iv_icon.setImageResource(lockFunctionBean.getImage());
        holder.tvNamepublic.setText(lockFunctionBean.getName());
        holder.tvNumber.setVisibility(View.VISIBLE);
        if (lockFunctionBean.getType() == BleLockUtils.TYPE_PASSWORD
                ||  lockFunctionBean.getType() == BleLockUtils.TYPE_FINGER
//                ||  lockFunctionBean.getType() == BleLockUtils.TYPE_OFFLINE_PASSWORD
                ) {
            holder.tvNumber.setText("("+lockFunctionBean.getNumber() + context.getString(R.string.group)+")");
        } else if (lockFunctionBean.getType() == BleLockUtils.TYPE_CARD) {
            holder.tvNumber.setText("("+lockFunctionBean.getNumber() + context.getString(R.string.pice)+")");
        } else if (lockFunctionBean.getType() == BleLockUtils.TYPE_SHARE) {
            LogUtils.e("设置共享用户个数   " + lockFunctionBean.getNumber());
            holder.tvNumber.setText("("+lockFunctionBean.getNumber() + context.getString(R.string.rad)+")");
        } else    {
            holder.tvNumber.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, lockFunctionBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public ImageView iv_icon;
        public TextView tvNamepublic;
        public TextView tvNumber;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            iv_icon = itemView.findViewById(R.id.iv);
            tvNamepublic = itemView.findViewById(R.id.tv_name);
            tvNumber = itemView.findViewById(R.id.tv_number);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, WifiLockFunctionBean wifiLockFunctionBean);
    }
}
