package com.kaadas.lock.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.MyMessageMultipleItem;

import java.util.List;

/**
 * Created by David on 2019/4/2
 */
public class MyMessageMultipleItemQuickAdapter extends BaseMultiItemQuickAdapter<MyMessageMultipleItem, BaseViewHolder> {
    public MyMessageMultipleItemQuickAdapter(List<MyMessageMultipleItem> data) {
        super(data);
        addItemType(MyMessageMultipleItem.SYSTEM_MESSAGE, R.layout.item_my_message_system_message);
        addItemType(MyMessageMultipleItem.SHARE_DEVICE_AUTHORIZATION_MESSAGE, R.layout.item_my_message_share_device_authorization_message);
//        addItemType(MyMessageMultipleItem.GATEWAY_AUTHORIZATION_MESSAGE, R.layout.image_view);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyMessageMultipleItem item) {
        switch (helper.getItemViewType()) {
            case MyMessageMultipleItem.SYSTEM_MESSAGE:
                break;
            case MyMessageMultipleItem.SHARE_DEVICE_AUTHORIZATION_MESSAGE:
                break;
            case MyMessageMultipleItem.GATEWAY_AUTHORIZATION_MESSAGE:
                break;
        }
    }
}
