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


import java.util.List;

/**
 * Created by yarolegovich on 08.03.2017.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private RecyclerView parentRecycler;
    private List<Forecast> data;
    Context context;


    // Item 点击事件
   public interface OnItemClickItem{
         void onItemClickItemMethod(int position);
    }

    public OnItemClickItem onItemClickItem;

    public void setOnItemClickItem(OnItemClickItem onItemClickItem) {
        this.onItemClickItem = onItemClickItem;
    }

    public ForecastAdapter(List<Forecast> data, Context context1) {
        this.data = data;
        this.context=context1;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parentRecycler = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_city_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        int iconTint = ContextCompat.getColor(holder.itemView.getContext(), R.color.grayIconTint);
        Forecast forecast = data.get(position);
        Glide.with(holder.itemView.getContext())
                .load(forecast.getCityIcon())
            //    .listener(new TintOnLoad(holder.imageView, iconTint))
                .into(holder.imageView);
        holder.textView.setText(forecast.getCityName());
        holder.textView.setTextColor(Color.parseColor("#70333333"));

        holder.itemView.findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                      if(onItemClickItem!=null){
                          onItemClickItem.onItemClickItemMethod(position);
                      }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

   public class ViewHolder extends RecyclerView.ViewHolder /* implements View.OnClickListener */ {

        private ImageView imageView;
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.city_image);
            textView = (TextView) itemView.findViewById(R.id.city_name);

          //  itemView.findViewById(R.id.container).setOnClickListener(this);
        }
        public void showText() {
//            int parentHeight = ((View) imageView.getParent()).getHeight();
//            float scale = (parentHeight - textView.getHeight()) / (float) imageView.getHeight();
//            Log.e("denganzhi1","scale...:"+scale+"  parentHeight:"+parentHeight+"  imageView.getHeight:"+imageView.getHeight());
//            imageView.setPivotX(imageView.getWidth() * 0.5f);
//            imageView.setPivotY(0);
//            imageView.animate().scaleX(scale)
//                    .withEndAction(new Runnable() {
//                        @Override
//                        public void run() {
//                            textView.setVisibility(View.VISIBLE);
//                            imageView.setColorFilter(Color.BLACK);
//                        }
//                    })
//                    .scaleY(scale).setDuration(200)
//                    .start();

           //   imageView.setBackgroundResource(R.mipmap.lock_select);
            textView.setTextColor(Color.parseColor("#333333"));
            Glide.with(context)
                    .load(R.mipmap.lock_select)
                    .into(imageView);
        }

        public void hideText() {
           // imageView.setColorFilter(ContextCompat.getColor(imageView.getContext(), R.color.grayIconTint));
            Glide.with(context)
                    .load(R.mipmap.lock_on_select)
                    .into(imageView);
          //  textView.setVisibility(View.INVISIBLE);
            imageView.animate().scaleX(1f).scaleY(1f)
                    .setDuration(200)
                    .start();

              textView.setTextColor(Color.parseColor("#70333333"));
        }

//        @Override
//        public void onClick(View v) {
//            Log.e("denganzhi2","getAdapterPosition:"+getAdapterPosition()+"currentShowPosition："+currentShowPosition);
//            parentRecycler.smoothScrollToPosition(getAdapterPosition());
//        }
    }

    private static class TintOnLoad implements RequestListener<Integer, GlideDrawable> {

        private ImageView imageView;
        private int tintColor;

        public TintOnLoad(ImageView view, int tintColor) {
            this.imageView = view;
            this.tintColor = tintColor;
        }

        @Override
        public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            imageView.setColorFilter(tintColor);
            return false;
        }
    }
}
