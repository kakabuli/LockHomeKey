package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.WifiLockAlarmRecordGroup;
import com.kaadas.lock.bean.WifiLockOperationRecordGroup;
import com.kaadas.lock.publiclibrary.bean.WifiLockAlarmRecord;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;

import java.util.List;

/**
 * Created by David on 2019/2/16
 */


public class WifiLockOperationGroupRecordAdapter extends BaseQuickAdapter<WifiLockOperationRecordGroup, BaseViewHolder> {

    public WifiLockOperationGroupRecordAdapter(@Nullable List<WifiLockOperationRecordGroup> data) {
        super(R.layout.item_bluetooth_record, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WifiLockOperationRecordGroup bean) {
        TextView tvTitle = helper.getView(R.id.tv_title);
        String time = bean.getTime();
        if (!TextUtils.isEmpty(time)) {
            tvTitle.setText(time);
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
        RecyclerView recyclerView = helper.getView(R.id.item_recycleview);
        List<WifiLockOperationRecord> list = bean.getList();
        WifiLockOperationItemRecordAdapter bluetoothItemRecordAdapter = new WifiLockOperationItemRecordAdapter(R.layout.item_wifi_lock_operation_record,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(bluetoothItemRecordAdapter);
        helper.getView(R.id.view_line).setVisibility(helper.getPosition() == getData().size()-1 ? View.INVISIBLE : View.VISIBLE);
    }


}
