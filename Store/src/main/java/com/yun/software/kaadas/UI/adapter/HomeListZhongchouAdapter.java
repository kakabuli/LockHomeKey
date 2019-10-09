package com.yun.software.kaadas.UI.adapter;

import android.widget.ImageView;
import android.widget.ProgressBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.software.kaadas.Comment.MyApplication;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.GoodsBean;

import java.util.List;

import la.xiong.androidquick.tool.Glid.GlidUtils;


public class HomeListZhongchouAdapter extends BaseQuickAdapter<GoodsBean,BaseViewHolder> {


    private List<GoodsBean> datas ;
    public HomeListZhongchouAdapter(List<GoodsBean> datas) {
        super(R.layout.adapter_item_home_zhongchou, datas);
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

        helper.setText(R.id.name,item.getName());
        helper.setText(R.id.price,item.getPrice());
        helper.setText(R.id.tv_zhichi_count,item.getSaleQty());
        helper.setText(R.id.progress_percent,item.getPercentage() + "%");

        String per = item.getPercentage();
        ProgressBar progressBar = helper.getView(R.id.progress);
        progressBar.setProgress(Integer.parseInt(per));

        ImageView imageView = helper.getView(R.id.image);
        GlidUtils.loadImageNormal(MyApplication.getInstance().getApplicationContext(),item.getLogo(),imageView);

    }
}
