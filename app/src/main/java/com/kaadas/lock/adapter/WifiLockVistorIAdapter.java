package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.LogUtils;

import java.util.List;

public class WifiLockVistorIAdapter extends BaseQuickAdapter<WifiLockOperationRecord, BaseViewHolder> {
    List<WifiLockOperationRecord> data;
    public WifiLockVistorIAdapter(@Nullable List<WifiLockOperationRecord> data) {
        super(R.layout.item_wifi_lock_vistor_record,data);
        this.data = data;
    }

    @Override
    protected void convert(BaseViewHolder helper, WifiLockOperationRecord record) {
        boolean first = record.isFirst();
        boolean last = record.isLast();

        int size = getData().size();
        TextView tvTime = helper.getView(R.id.tv_time);
        TextView tvDayTime = helper.getView(R.id.tv_day_time);
        long time = record.getTime();
        String s = DateUtils.currentLong2HourMin(time * 1000);
        tvTime.setText(TextUtils.isEmpty(s) ? "" : s);
        tvDayTime.setVisibility( first? View.VISIBLE : View.GONE);
        //设置天时间
        String dayTime = record.getDayTime();
        if (!TextUtils.isEmpty(dayTime)) {
            if (dayTime.equals(DateUtils.getCurrentYMD())) {
                dayTime = mContext.getString(R.string.today);
            }
            tvDayTime.setText(dayTime+"");

        }

        int position = helper.getPosition();
        helper.getView(R.id.view_bottom).setVisibility((position == size -1 || last) ? View.INVISIBLE : View.VISIBLE);
        helper.getView(R.id.driver).setVisibility(last ? View.VISIBLE : View.GONE);

        ImageView tvContent = helper.getView(R.id.iv_content);
        String content = BleUtil.getAlarmByType(record.getType(), mContext);
        TextView tvRight = helper.getView(R.id.tv_right);
        tvRight.setText(record.getShareUserNickname());
    }
}
