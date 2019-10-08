package com.yun.software.kaadas.UI.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.MessageBean;
import com.yun.software.kaadas.Utils.TimeUtil;

import java.util.List;


public class OrderInfoAdapter extends BaseQuickAdapter<MessageBean,BaseViewHolder> {


    private List<MessageBean> datas ;
    public OrderInfoAdapter(List<MessageBean> datas) {
        super(R.layout.adapter_item_order_info, datas);
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

        helper.setText(R.id.time,item.getSenderDate());
        helper.setText(R.id.title,item.getTitle());
        helper.setText(R.id.content,item.getContent());
    }
}
