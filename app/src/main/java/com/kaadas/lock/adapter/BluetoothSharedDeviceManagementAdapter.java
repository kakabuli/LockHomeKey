package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.http.result.BluetoothSharedDeviceBean;
import com.kaadas.lock.utils.LogUtils;


import java.util.List;

/**
 * Created by David on 2019/2/16
 */


public class BluetoothSharedDeviceManagementAdapter extends BaseQuickAdapter<BluetoothSharedDeviceBean.DataBean, BaseViewHolder> {


    public BluetoothSharedDeviceManagementAdapter(@Nullable List<BluetoothSharedDeviceBean.DataBean> data, int layoutId) {
        super(layoutId, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, BluetoothSharedDeviceBean.DataBean bean) {
        int position = helper.getPosition();
        String number="";
        if (position<10){
            number="0"+position;
        }
        helper.setText(R.id.tv_serial_number, number);
        helper.setText(R.id.tv_num, bean.getUnickname());
        LogUtils.d("davi getData().size() "+getData().size()+"  position  "+position);
        if(getData()!=null && getData().size()==position+1){
            helper.getView(R.id.my_view).setVisibility(View.GONE);
        }else {
            helper.getView(R.id.my_view).setVisibility(View.VISIBLE);
        }


    }


}
