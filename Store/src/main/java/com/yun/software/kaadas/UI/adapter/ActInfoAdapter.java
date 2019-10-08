package com.yun.software.kaadas.UI.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.MessageBean;
import com.yun.software.kaadas.Utils.TimeUtil;

import java.util.List;

import la.xiong.androidquick.tool.Glid.GlidUtils;


public class ActInfoAdapter extends BaseQuickAdapter<MessageBean,BaseViewHolder> {


    private List<MessageBean> datas ;
    public ActInfoAdapter(List<MessageBean> datas) {
        super(R.layout.adapter_item_act_info, datas);
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
    protected void convert(BaseViewHolder helper, MessageBean item) {

//        String time = TimeUtil.getDataByTimeStamp(TimeUtil.dateFormatYMD,item.getSenderDate());
        ImageView imageView = helper.getView(R.id.image);
        helper.setText(R.id.time,item.getSenderDate());
        helper.setText(R.id.title,item.getTitle());
        helper.setText(R.id.content,item.getContent());

        GlidUtils.loadImageNormal(mContext,item.getLogo(),imageView);
    }
}
