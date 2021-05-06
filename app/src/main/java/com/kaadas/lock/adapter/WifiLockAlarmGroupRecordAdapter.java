package com.kaadas.lock.adapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.WifiLockAlarmRecordGroup;
import com.kaadas.lock.publiclibrary.bean.WifiLockAlarmRecord;
import com.kaadas.lock.utils.DateUtils;

import java.util.List;

/**
 * Created by David on 2019/2/16
 */


public class WifiLockAlarmGroupRecordAdapter extends BaseQuickAdapter<WifiLockAlarmRecordGroup, BaseViewHolder> {


    public WifiLockAlarmGroupRecordAdapter(@Nullable List<WifiLockAlarmRecordGroup> data) {
        super(R.layout.item_bluetooth_record, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, WifiLockAlarmRecordGroup bean) {

        TextView tvTitle = helper.getView(R.id.tv_title);
        String time = bean.getTime();
        if (!TextUtils.isEmpty(time)) {
            if (time.equals(DateUtils.getCurrentYMD())){
                time = mContext.getString(R.string.today);
            }
            tvTitle.setText(time);
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
        RecyclerView recyclerView = helper.getView(R.id.item_recycleview);
        List<WifiLockAlarmRecord> list = bean.getList();
        WifiLockAlarmItemRecordAdapter bluetoothItemRecordAdapter = new WifiLockAlarmItemRecordAdapter(R.layout.item_wifi_lock_alram,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(bluetoothItemRecordAdapter);
        helper.getView(R.id.view_line).setVisibility(helper.getPosition() == getData().size()-1 ? View.INVISIBLE : View.VISIBLE);
    }


}
