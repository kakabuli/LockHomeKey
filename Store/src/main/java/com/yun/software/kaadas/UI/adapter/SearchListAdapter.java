package com.yun.software.kaadas.UI.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.software.kaadas.Comment.MyApplication;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.GoodsBean;

import java.util.List;

import la.xiong.androidquick.tool.Glid.GlidUtils;


public class SearchListAdapter extends BaseQuickAdapter<GoodsBean,BaseViewHolder> {


    private List<GoodsBean> datas ;
    public SearchListAdapter(List<GoodsBean> datas) {
        super(R.layout.adapter_search_item, datas);
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

        ImageView imageView = helper.getView(R.id.img);
        GlidUtils.loadImageNormal(MyApplication.getInstance().getApplicationContext(),item.getLogo(),imageView);

    }
}
