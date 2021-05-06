package com.kaadas.lock.adapter;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.bean.RouteBean;

import java.util.List;

public class RouteListAdapter extends RecyclerView.Adapter {
    private List<RouteBean> routeBeans;

    public RouteListAdapter(List<RouteBean> routeBeans) {
        this.routeBeans = routeBeans;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_route_list, viewGroup, false);
        return new MyHolderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        MyHolderView myHolderView = (MyHolderView) viewHolder;
        RouteBean routeBean = routeBeans.get(i);
        myHolderView.productName.setText(routeBean.getProduct());
        myHolderView.type.setText(routeBean.getTypeList());
    }

    @Override
    public int getItemCount() {
        return routeBeans.size();
    }

    public class MyHolderView extends RecyclerView.ViewHolder{
        public TextView productName; //路由器厂商
        public TextView type; //路由器型号

        public MyHolderView(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.tv_produce_name);
            type = itemView.findViewById(R.id.type);
        }

    }

}
