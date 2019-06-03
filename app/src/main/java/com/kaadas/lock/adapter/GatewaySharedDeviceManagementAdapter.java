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


public class GatewaySharedDeviceManagementAdapter extends BaseQuickAdapter<BluetoothSharedDeviceBean.DataBean, BaseViewHolder> {


    public GatewaySharedDeviceManagementAdapter(@Nullable List<BluetoothSharedDeviceBean.DataBean> data, int layoutId) {
        super(layoutId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BluetoothSharedDeviceBean.DataBean bean) {
        int itemCount = getItemCount();
        int pos=helper.getPosition();
        if (pos==itemCount-1){
            View view= helper.getView(R.id.my_view);
            view.setVisibility(View.GONE);
        }
        helper.setText(R.id.tv_serial_number, bean.getOpen_purview());
        helper.setText(R.id.tv_num, bean.getUnickname());
    }


}
