package com.yun.software.kaadas.UI.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.software.kaadas.Comment.MyApplication;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.GoodsBean;
import com.yun.software.kaadas.UI.bean.KanjiaHelp;

import java.util.List;

import la.xiong.androidquick.tool.Glid.GlidUtils;


public class KanjiaHelpAdapter extends BaseQuickAdapter<KanjiaHelp,BaseViewHolder> {


    private List<KanjiaHelp> datas ;
    public KanjiaHelpAdapter(List<KanjiaHelp> datas) {
        super(R.layout.adapter_item_kanjia_help, datas);
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
    protected void convert(BaseViewHolder helper, KanjiaHelp item) {

        helper.setText(R.id.tv_name,item.getName());
        helper.setText(R.id.tv_cut_price,"砍掉"+item.getCutPrice()+"元");

        ImageView imageView = helper.getView(R.id.image);
        GlidUtils.loadImageNormal(MyApplication.getInstance().getApplicationContext(),item.getLogo(),imageView,R.drawable.pic_head_mine);

    }
}
