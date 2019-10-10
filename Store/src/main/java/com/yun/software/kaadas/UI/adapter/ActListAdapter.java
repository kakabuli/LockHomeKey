package com.yun.software.kaadas.UI.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.ActDetails;
import com.yun.software.kaadas.Utils.TimeUtil;

import java.util.List;

import la.xiong.androidquick.tool.Glid.GlidUtils;


public class ActListAdapter extends BaseQuickAdapter<ActDetails,BaseViewHolder> {


    private List<ActDetails> datas ;
    public ActListAdapter(List<ActDetails> datas) {
        super(R.layout.adapter_item_act, datas);
        this.datas = datas;
    }

    @Override
    public int getItemCount() {
        if (datas != null ){
            return datas.size();
        }else {
            return 0;
        }

    }

    @Override
    protected void convert(BaseViewHolder helper, ActDetails item) {
        ImageView imageView = helper.getView(R.id.image);
        TextView tvRedView = helper.getView(R.id.tv_statue_red);
        TextView tvGrayView = helper.getView(R.id.tv_statue_gray);

        //add  报名  running 进行中   end 结束
        switch (item.getStatus()){
            case "activity_middle":
                tvRedView.setVisibility(View.VISIBLE);
                tvGrayView.setVisibility(View.GONE);
                tvRedView.setText(item.getOrderTypeCN()+"...");
                break;
            case "activity_unfinished":
                tvRedView.setVisibility(View.VISIBLE);
                tvGrayView.setVisibility(View.GONE);
                tvRedView.setText(item.getOrderTypeCN() +"...");
                break;
            case "activity_end":
                tvRedView.setVisibility(View.GONE);
                tvGrayView.setVisibility(View.VISIBLE);
                tvGrayView.setText(item.getOrderTypeCN());

                break;
        }

        ImageView iconPeople = helper.getView(R.id.icon_people);

        if (item.getStatus().equals("activity_end")){
            helper.setTextColor(R.id.tv_title, Color.parseColor("#999999"));
            helper.setTextColor(R.id.count, Color.parseColor("#999999"));
            helper.setTextColor(R.id.address, Color.parseColor("#999999"));
            helper.setTextColor(R.id.time, Color.parseColor("#999999"));
            iconPeople.setImageResource(R.drawable.icon_people_activity_gray);
        }else {
            helper.setTextColor(R.id.tv_title, Color.parseColor("#333333"));
            helper.setTextColor(R.id.count, Color.parseColor("#333333"));
            helper.setTextColor(R.id.address, Color.parseColor("#333333"));
            helper.setTextColor(R.id.time, Color.parseColor("#333333"));
            iconPeople.setImageResource(R.drawable.icon_people_activity_black);
        }

        GlidUtils.loadImageNormal(mContext,item.getLogo(),imageView);

        helper.setText(R.id.tv_title,item.getTitle());
        helper.setText(R.id.count,item.getQty());
        helper.setText(R.id.address,item.getAddress());



        //
        String startTime = item.getBeginTime().substring(5,16);
        String endTime = item.getEndTime().substring(5,16);

        String time = startTime.concat(" - ").concat(endTime);

        helper.setText(R.id.time,time);

    }
}
