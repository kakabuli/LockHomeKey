package com.yun.software.kaadas.UI.adapter;

import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.software.kaadas.Comment.MyApplication;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.GoodsBean;
import com.yun.software.kaadas.Utils.TimeUtil;

import java.util.Date;
import java.util.List;

import cn.iwgang.countdownview.CountdownView;
import la.xiong.androidquick.tool.Glid.GlidUtils;


public class ListMiaoshaAdapter extends BaseQuickAdapter<GoodsBean,BaseViewHolder> {


    private List<GoodsBean> datas ;
    public ListMiaoshaAdapter(List<GoodsBean> datas) {
        super(R.layout.adapter_item_list_miaosha, datas);
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
    protected void convert(BaseViewHolder helper, GoodsBean item) {

        TextView tvOverTimeView = helper.getView(R.id.tv_over);
        TextView tvXianshiView = helper.getView(R.id.tv_xianshi);

        TextView olpPriceView = helper.getView(R.id.tv_old_price);
        olpPriceView.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG); //中划线
        olpPriceView.getPaint().setAntiAlias(true);//抗锯齿
        olpPriceView.setText("¥"+item.getOldPrice());
        helper.setText(R.id.name,item.getName());
        helper.setText(R.id.price,item.getPrice());

        CountdownView countdownView = helper.getView(R.id.countdown_view);
//        Date endDate = TimeUtil.getDateByFormat(item.getEndTime(),TimeUtil.dateFormatYMDHMS);
        long des = item.getSecond();
        countdownView.start(des);

        if (des < 0){
            tvXianshiView.setVisibility(View.GONE);
            countdownView.setVisibility(View.GONE);
            tvOverTimeView.setVisibility(View.VISIBLE);
        }else {
            tvXianshiView.setVisibility(View.VISIBLE);
            countdownView.setVisibility(View.VISIBLE);
            tvOverTimeView.setVisibility(View.GONE);
        }
        countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                tvXianshiView.setVisibility(View.GONE);
                countdownView.setVisibility(View.GONE);
                tvOverTimeView.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
            }
        });

        ImageView imageView = helper.getView(R.id.image);
        GlidUtils.loadImageNormal(MyApplication.getInstance().getApplicationContext(),item.getLogo(),imageView);

    }
}
