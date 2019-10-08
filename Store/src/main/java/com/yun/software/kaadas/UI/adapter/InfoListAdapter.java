package com.yun.software.kaadas.UI.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.software.kaadas.Comment.MyApplication;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.InfoDetails;

import java.util.List;

import la.xiong.androidquick.tool.Glid.GlidUtils;


public class InfoListAdapter extends BaseQuickAdapter<InfoDetails,BaseViewHolder> {


    private List<InfoDetails> datas ;
    public InfoListAdapter(List<InfoDetails> datas) {
        super(R.layout.adapter_item_info, datas);
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
    protected void convert(BaseViewHolder helper, InfoDetails item) {

        helper.setText(R.id.title,item.getArticleTitle());
        helper.setText(R.id.time,item.getTimeStr() + "前");
        helper.setText(R.id.liulan,item.getViewNum());
        helper.setText(R.id.pinglun,item.getCommentNum());
        helper.setText(R.id.fenxiang,item.getShareNum());

        ImageView imageView = helper.getView(R.id.image);
        GlidUtils.loadImageNormal(MyApplication.getInstance().getApplicationContext(),item.getArticleCoverUrl(),imageView);

    }
}
