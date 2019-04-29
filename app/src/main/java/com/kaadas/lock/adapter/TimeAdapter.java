package com.kaadas.lock.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.MyDate;


import java.util.List;

/**
 * Create By denganzhi  on 2019/4/2
 * Describe
 */

public class TimeAdapter extends BaseQuickAdapter<MyDate, BaseViewHolder> {


    int width =0;
    int height=0;

    public TimeAdapter(List data, Context context) {
     //   super(R.layout.ll_layout, data);
        super(R.layout.time_select_item, data);
        WindowManager manager = ((Activity)context).getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        width = outMetrics.widthPixels;
        height = outMetrics.heightPixels;



      //  Log.e("denganzhi1","宽度是:"+width);
    }

    int normalcolor=-1;
    @Override
    protected void convert(BaseViewHolder helper, MyDate item) {

        if(normalcolor==-1){
            normalcolor= ((TextView)helper.getView(R.id.time_select_item_top)).getCurrentTextColor();
        }

        RecyclerView.ViewHolder itemViewHolder = (RecyclerView.ViewHolder) helper;
        ViewGroup.LayoutParams layoutParams = itemViewHolder.itemView.getLayoutParams();
    //    Log.e("denganzhi1","宽度是:"+ width/7);
        layoutParams.width= width/7;

        helper.setText(R.id.time_select_item_top,item.getWeek());
        helper.setText(R.id.time_select_item_bottom,item.getDay()+"");

        View view= itemViewHolder.itemView;

        if(item.isChecked()){
            View childView= view.findViewById(R.id.time_select_item_ll);
            childView.setBackgroundColor(Color.parseColor("#5EB7FF"));
            TextView top = view.findViewById(R.id.time_select_item_top);
            top.setTextColor(Color.parseColor("#FFFFFF"));
            TextView bottom = view.findViewById(R.id.time_select_item_bottom);
            bottom.setTextColor(Color.parseColor("#FFFFFF"));
        }else{
            View childView= view.findViewById(R.id.time_select_item_ll);
            childView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
            TextView top = view.findViewById(R.id.time_select_item_top);
            top.setTextColor(normalcolor);
            TextView bottom = view.findViewById(R.id.time_select_item_bottom);
            bottom.setTextColor(Color.parseColor("#333333"));
        }



       //  helper.setText(R.id.my_textview_context,item.getContext());

//         helper.setText(R.id.content1,"点击1").addOnClickListener(R.id.content1).setTag(R.id.content1,"content1");
//         helper.setText(R.id.content2,"点击2").addOnClickListener(R.id.content2).setTag(R.id.content2,"content2");

    }
}
