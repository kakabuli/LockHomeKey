package com.yun.software.kaadas.UI.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.software.kaadas.Comment.MyApplication;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.GoodsBean;

import java.util.List;

import la.xiong.androidquick.tool.Glid.GlidUtils;


public class HomeListNormalAdapter extends BaseQuickAdapter<GoodsBean,BaseViewHolder> {


    private List<GoodsBean> datas ;
    public HomeListNormalAdapter(List<GoodsBean> datas) {
        super(R.layout.adapter_item_home_normal, datas);
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

        if (TextUtils.isEmpty(item.getMaxPrice())){
            helper.setText( R.id.price,item.getPrice());
        }else {
            helper.setText( R.id.price,item.getMaxPrice());
        }



        ImageView imageView = helper.getView(R.id.image);
        GlidUtils.loadImageNormal(MyApplication.getInstance().getApplicationContext(),item.getLogo(),imageView);

    }
}
