package com.kaadas.lock.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.inf.OnBindClickListener;
import com.kaadas.lock.bean.BluetoothLockBroadcastBean;
import com.kaadas.lock.bean.BluetoothLockBroadcastListBean;
import com.kaadas.lock.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class ClothesHangerMachineBleWiFiSearchAdapter extends BaseQuickAdapter<BluetoothDevice, BaseViewHolder> {

    private OnBindClickListener bindClickListener;
//    BluetoothLockBroadcastBean mBroadcastBean = new BluetoothLockBroadcastBean();
    List<BluetoothLockBroadcastListBean> mBroadcastList = new ArrayList<>();
    List<BluetoothLockBroadcastBean> mItemList = new ArrayList<>();

    public void setBluetoothLockBroadcast(List<BluetoothLockBroadcastListBean> broadcastList) {
        this.mBroadcastList = broadcastList;
    }
    public void setBindClickListener(OnBindClickListener bindClickListener) {
        this.bindClickListener = bindClickListener;
    }

    public ClothesHangerMachineBleWiFiSearchAdapter(@Nullable List<BluetoothDevice> data) {
        super(R.layout.clothes_hanger_machine_search_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BluetoothDevice item) {
        helper.setVisible(R.id.device_bluetooth_name,false);

        LogUtils.e("shulan broadcastBean.getDeviceName-->" + item.getName());
        helper.setVisible(R.id.device_bluetooth_name,true);
        helper.setText(R.id.device_bluetooth_name,item.getName());

        helper.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bindClickListener != null){
                    bindClickListener.onItemClickListener(v,helper.getPosition(),item);
                }
            }
        });

    }

}
