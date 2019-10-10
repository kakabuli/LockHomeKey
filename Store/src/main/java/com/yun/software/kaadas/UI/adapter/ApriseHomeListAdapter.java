package com.yun.software.kaadas.UI.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.software.kaadas.Comment.MyApplication;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.CommentsBean;

import java.util.List;

import la.xiong.androidquick.tool.Glid.GlidUtils;


public class ApriseHomeListAdapter extends BaseQuickAdapter<CommentsBean,BaseViewHolder> {


    private List<CommentsBean> datas ;
    public ApriseHomeListAdapter(List<CommentsBean> datas) {
        super(R.layout.adapter_item_aprise_home, datas);
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
    protected void convert(BaseViewHolder helper, CommentsBean item) {

        helper.setText(R.id.name,item.getUserName());
        helper.setText(R.id.comment,item.getContent());
        helper.setText(R.id.time,item.getCreateDate());
        helper.setText(R.id.goods_attr,item.getProductLabels());

        ImageView imageView = helper.getView(R.id.header);
        GlidUtils.loadCircleImageView(MyApplication.getInstance().getApplicationContext(),item.getLogo(),imageView,R.drawable.pic_head_mine);

    }
}
