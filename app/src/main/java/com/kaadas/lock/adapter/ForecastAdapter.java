package com.kaadas.lock.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.Forecast;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;


import java.util.List;

/**
 * Created by yarolegovich on 08.03.2017.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private List<GwLockInfo>  data;
    Context context;


    // Item 点击事件
    public interface OnItemClickItem {
        void onItemClickItemMethod(int position);
    }

    public OnItemClickItem onItemClickItem;

    public void setOnItemClickItem(OnItemClickItem onItemClickItem) {
        this.onItemClickItem = onItemClickItem;
    }

    public ForecastAdapter(List<GwLockInfo>  data, Context context1) {
        this.data = data;
        this.context = context1;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_city_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        GwLockInfo gwLockInfo = data.get(position);
        Glide.with(holder.itemView.getContext())
                .load(R.mipmap.lock_on_select)
                .into(holder.imageView);
        holder.textView.setText(gwLockInfo.getServerInfo().getNickName());
        holder.textView.setTextColor(Color.parseColor("#70333333"));

        holder.itemView.findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickItem != null) {
                    onItemClickItem.onItemClickItemMethod(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.city_image);
            textView = (TextView) itemView.findViewById(R.id.city_name);

        }

        public void showText() {
            textView.setTextColor(Color.parseColor("#333333"));
            Glide.with(context)
                    .load(R.mipmap.lock_select)
                    .into(imageView);
        }

        public void hideText() {
            Glide.with(context)
                    .load(R.mipmap.lock_on_select)
                    .into(imageView);
            imageView.animate().scaleX(1f).scaleY(1f)
                    .setDuration(200)
                    .start();

            textView.setTextColor(Color.parseColor("#70333333"));
        }
    }
}
