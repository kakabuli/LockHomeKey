package com.yun.software.kaadas.UI.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.GoodsBean;
import com.yun.software.kaadas.UI.bean.KanjiaMine;

import java.util.List;

import cn.iwgang.countdownview.CountdownView;
import la.xiong.androidquick.tool.Glid.GlidUtils;


public class KanjiaIngAdapter extends BaseMultiItemQuickAdapter<GoodsBean,BaseViewHolder> {


    private List<GoodsBean> datas ;
    public KanjiaIngAdapter(List<GoodsBean> datas) {
        super(datas);

        addItemType(0, R.layout.adapter_item_kanjia_top);
        addItemType(1, R.layout.adapter_kanjia_item);
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
    protected View getItemView(int layoutResId, ViewGroup parent) {
        return super.getItemView(layoutResId, parent);
    }

    @Override
    public int getItemViewType(int position) {
        if (datas.get(position).isFlg()){
            //砍价中
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, GoodsBean item) {

        switch (helper.getItemViewType()){
            case 0:
                helper.setText(R.id.tv_name,item.getName());
                helper.setText(R.id.tv_cut_price,item.getCutPrice());

                LinearLayout linearLayout = helper.getView(R.id.ll_countdownview);
                TextView tvOver = helper.getView(R.id.tv_kanjia_over);
                Button btnSuccess = helper.getView(R.id.btn_kanjia_success);
                Button btnKanjia = helper.getView(R.id.btn_kanjia);

                helper.addOnClickListener(R.id.btn_kanjia);
                helper.addOnClickListener(R.id.btn_kanjia_success);

                CountdownView countdownView = helper.getView(R.id.countdown_view);
                long des = item.getSecond();
                countdownView.start(des);
                if (des < 0){
                    linearLayout.setVisibility(View.GONE);
                    countdownView.setVisibility(View.GONE);
                    tvOver.setVisibility(View.VISIBLE);
                    btnKanjia.setVisibility(View.GONE);
                }else {
                    linearLayout.setVisibility(View.VISIBLE);
                    countdownView.setVisibility(View.VISIBLE);
                    tvOver.setVisibility(View.GONE);
                    btnKanjia.setVisibility(View.VISIBLE);
                }
                countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                    @Override
                    public void onEnd(CountdownView cv) {
                        linearLayout.setVisibility(View.GONE);
                        countdownView.setVisibility(View.GONE);
                        tvOver.setVisibility(View.VISIBLE);
                        btnKanjia.setVisibility(View.GONE);
                    }
                });

                break;
            case 1:
                helper.addOnClickListener(R.id.button);
                helper.setText(R.id.tv_num,item.getSendNumber());
                helper.setText(R.id.name,item.getName());
                break;
        }


        ImageView imageView = helper.getView(R.id.image);
        GlidUtils.loadImageNormal(mContext,item.getLogo(),imageView,R.drawable.img_default_fang);

    }
}
