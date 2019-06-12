package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.http.result.BluetoothSharedDeviceBean;


import java.util.List;

/**
 * Created by David on 2019/2/16
 */


public class BluetoothSharedDeviceManagementAdapter extends BaseQuickAdapter<BluetoothSharedDeviceBean.DataBean, BaseViewHolder> {


    public BluetoothSharedDeviceManagementAdapter(@Nullable List<BluetoothSharedDeviceBean.DataBean> data, int layoutId) {
        super(layoutId, data);
    }
    int i=0;
    @Override
    protected void convert(BaseViewHolder helper, BluetoothSharedDeviceBean.DataBean bean) {
        helper.setText(R.id.tv_serial_number, bean.getOpen_purview());
        helper.setText(R.id.tv_num, bean.getUnickname());
        i++;
        if(getData()!=null && getData().size()==i){
            helper.getView(R.id.my_view).setVisibility(View.GONE);
            i=0;
        }else {
            helper.getView(R.id.my_view).setVisibility(View.VISIBLE);
        }


    }


}
