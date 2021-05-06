package com.kaadas.lock.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


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
        Log.e("denganzhi1","presenter:"+presenter.isConnectedEye+" gwLockInfo:"+gwLockInfo.getServerInfo().getEvent_str());
         if(!isScroll){ // 第一次进来
             //除了第二个显示正常，其他的都显示透明
             if(position==1){
                 //已经滑动
                 if(!presenter.isConnectedEye){
                     // 未呼通, 设备不在线
                     holder.imageView.setBackgroundResource(R.mipmap.video_no_online);

                     holder.textView.setTextColor(Color.parseColor("#ABABAB"));
                 } else if(gwLockInfo.getServerInfo().getEvent_str().equals("offline")){
                     holder.imageView.setBackgroundResource(R.mipmap.video_no_online);

                     holder.textView.setTextColor(Color.parseColor("#ABABAB"));
                 } else {
                     // 已呼通
                     holder.imageView.setBackgroundResource(R.mipmap.lock_on_select);

                     holder.textView.setTextColor(Color.parseColor("#70333333"));
                 }
             }else {
                 if(!presenter.isConnectedEye){  //未呼通
                     holder.imageView.setBackgroundResource(R.mipmap.video_no_online_small);

                     holder.textView.setTextColor(Color.parseColor("#8CABABAB"));
                 }else if(gwLockInfo.getServerInfo().getEvent_str().equals("offline")){
                     holder.imageView.setBackgroundResource(R.mipmap.video_no_online);

                     holder.textView.setTextColor(Color.parseColor("#ABABAB"));
                 }else {
                     holder.imageView.setBackgroundResource(R.mipmap.lock_on_select);

                     holder.textView.setTextColor(Color.parseColor("#70333333"));
                 }
             }

         }else {
              // 已经滑动了刷新
             if(!presenter.isConnectedEye ){
                 holder.imageView.setBackgroundResource(R.mipmap.video_no_online);
                 // 未呼通
                 holder.textView.setTextColor(Color.parseColor("#ABABAB"));
             } else if(gwLockInfo.getServerInfo().getEvent_str().equals("offline")){
                 holder.imageView.setBackgroundResource(R.mipmap.video_no_online);

                 holder.textView.setTextColor(Color.parseColor("#ABABAB"));
             } else {
                 // 已呼通
                 holder.imageView.setBackgroundResource(R.mipmap.lock_on_select);

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

        public void showText(int position) {
            GwLockInfo gwLockInfo = data.get(position);
            isScroll=true;
            if(!presenter.isConnectedEye || gwLockInfo.getServerInfo().getEvent_str().equals("offline")){  //未呼通
                imageView.setBackgroundResource(R.mipmap.video_no_online);
                textView.setTextColor(Color.parseColor("#ABABAB"));
            }else {
                imageView.setBackgroundResource(R.mipmap.lock_select);
                textView.setTextColor(Color.parseColor("#333333"));
            }

        }

        public void hideText(int position) {
            GwLockInfo gwLockInfo = data.get(position);
            isScroll=true;
            if(!presenter.isConnectedEye || gwLockInfo.getServerInfo().getEvent_str().equals("offline")){  //未呼通
                imageView.setBackgroundResource(R.mipmap.video_no_online_small);
                textView.setTextColor(Color.parseColor("#8CABABAB"));
            }else {
                imageView.setBackgroundResource(R.mipmap.video_no_online_small);
                textView.setTextColor(Color.parseColor("#70333333"));
            }

            imageView.animate().scaleX(1f).scaleY(1f)
                    .setDuration(200)
                    .start();
        }
    }
}
