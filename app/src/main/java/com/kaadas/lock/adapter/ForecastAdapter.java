package com.kaadas.lock.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.kaadas.lock.mvp.presenter.cateye.VideoPresenter;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;




import java.util.List;

/**
 * Created by yarolegovich on 08.03.2017.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private List<GwLockInfo>  data;
    Context context;
    VideoPresenter presenter;
    boolean isScroll=false;

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

    public ForecastAdapter(VideoPresenter presenter,List<GwLockInfo>  data, Context context1) {
        this.data = data;
        this.context = context1;
        this.presenter=presenter;
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
        Log.e("denganzhi1","positon:"+position+"isscroll:"+isScroll);

         if(!isScroll){ // 第一次进来
             //除了第二个显示正常，其他的都显示透明
             if(position==1){
                 //已经滑动
                 if(!presenter.isConnectedEye){
                     // 未呼通
                     Glide.with(holder.itemView.getContext())
                             .load(R.mipmap.video_no_online)
                             .into(holder.imageView);
                     holder.textView.setTextColor(Color.parseColor("#ABABAB"));
                 }  else {
                     // 已呼通
                     Glide.with(holder.itemView.getContext())
                             .load(R.mipmap.lock_on_select)
                             .into(holder.imageView);
                     holder.textView.setTextColor(Color.parseColor("#70333333"));
                 }
             }else {
                 if(!presenter.isConnectedEye){  //未呼通
                     Glide.with(context)
                             .load(R.mipmap.video_no_online_small)
                             .into(holder.imageView);
                     holder.textView.setTextColor(Color.parseColor("#8CABABAB"));
                 }else {
                     Glide.with(context)
                             .load(R.mipmap.lock_on_select)
                             .into(holder.imageView);
                     holder.textView.setTextColor(Color.parseColor("#70333333"));
                 }
             }

         }else {
              // 已经滑动了刷新
             if(!presenter.isConnectedEye){
                 // 未呼通
                 Glide.with(holder.itemView.getContext())
                         .load(R.mipmap.video_no_online)
                         .into(holder.imageView);
                 holder.textView.setTextColor(Color.parseColor("#ABABAB"));
             }  else {
                 // 已呼通
                 Glide.with(holder.itemView.getContext())
                         .load(R.mipmap.lock_on_select)
                         .into(holder.imageView);
                 holder.textView.setTextColor(Color.parseColor("#70333333"));
             }
         }



        holder.textView.setText(gwLockInfo.getServerInfo().getNickName());


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
            isScroll=true;
            if(!presenter.isConnectedEye){  //未呼通
                Glide.with(context)
                        .load(R.mipmap.video_no_online)
                        .into(imageView);
                textView.setTextColor(Color.parseColor("#ABABAB"));
            }else {
                Glide.with(context)
                        .load(R.mipmap.lock_select)
                        .into(imageView);
                textView.setTextColor(Color.parseColor("#333333"));
            }

        }

        public void hideText() {
            isScroll=true;
            if(!presenter.isConnectedEye){  //未呼通
                Glide.with(context)
                        .load(R.mipmap.video_no_online_small)
                        .into(imageView);
                textView.setTextColor(Color.parseColor("#8CABABAB"));
            }else {
                Glide.with(context)
                        .load(R.mipmap.lock_on_select)
                        .into(imageView);
                textView.setTextColor(Color.parseColor("#70333333"));
            }

            imageView.animate().scaleX(1f).scaleY(1f)
                    .setDuration(200)
                    .start();


        }
    }
}
