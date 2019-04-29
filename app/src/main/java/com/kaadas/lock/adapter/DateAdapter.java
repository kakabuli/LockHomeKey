package com.kaadas.lock.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;

import java.util.List;

/**
 * Create By denganzhi  on 2019/4/2
 * Describe
 */

public class DateAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public DateAdapter(List data) {
        super(R.layout.ll_layout, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, String item) {

         helper.setText(R.id.date_select_item_tv,item);

//        RecyclerView.ViewHolder itemViewHolder = (RecyclerView.ViewHolder) helper;
//
//        itemViewHolder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
//        TextView date_select_tv=itemViewHolder.itemView.findViewById(R.id.date_select_item_tv);
//        date_select_tv.setTextColor(Color.parseColor("#333333"));

//         helper.setText(R.id.content1,"点击1").addOnClickListener(R.id.content1).setTag(R.id.content1,"content1");
//         helper.setText(R.id.content2,"点击2").addOnClickListener(R.id.content2).setTag(R.id.content2,"content2");

    }
}
