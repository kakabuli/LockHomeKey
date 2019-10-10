package com.yun.software.kaadas.UI.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.MessageBean;
import com.yun.software.kaadas.Utils.TimeUtil;

import java.util.List;


public class SystemInfoListAdapter extends BaseQuickAdapter<MessageBean,BaseViewHolder> {


    private List<MessageBean> datas ;
    public SystemInfoListAdapter(List<MessageBean> datas) {
        super(R.layout.adapter_item_system_info, datas);
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

        helper.setText(R.id.time,item.getSenderDate());
        helper.setText(R.id.title,item.getTitle());
        helper.setText(R.id.content,item.getContent());

    }
}
