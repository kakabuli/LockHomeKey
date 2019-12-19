package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.BluetoothItemRecordBean;
import com.kaadas.lock.publiclibrary.bean.WifiLockAlarmRecord;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.TimeUtil;

import java.util.List;

/**
 * Created by David on 2019/2/16
 */


public class WifiLockAlarmItemRecordAdapter extends BaseQuickAdapter<WifiLockAlarmRecord, BaseViewHolder> {
    public WifiLockAlarmItemRecordAdapter(int layoutResId, @Nullable List<WifiLockAlarmRecord> data) {
        super(layoutResId, data);
    }

    public WifiLockAlarmItemRecordAdapter(@Nullable List<WifiLockAlarmRecord> data) {
        super(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WifiLockAlarmRecord bean) {
        int size = getData().size();
        int position = helper.getPosition();

        TextView tvTime = helper.getView(R.id.tv_time);
        long time = bean.getTime();
        String s = DateUtils.long2HourMin(time);
        tvTime.setText(TextUtils.isEmpty(s) ? "" : s);
        helper.getView(R.id.view_top).setVisibility(position == 0? View.INVISIBLE : View.VISIBLE);
        helper.getView(R.id.view_bottom).setVisibility(position == size-1 ? View.INVISIBLE : View.VISIBLE);
        ImageView iv = helper.getView(R.id.iv);
        iv.setImageResource(R.mipmap.bluetooth_warn_icon);
        TextView tvContent = helper.getView(R.id.tv_content);
        String content = BleUtil.getAlarmByType(bean.getType(),mContext);

        LogUtils.e("Adapter 显示的   " + content);
         // 机械开锁/APP开锁/自动开锁/密码开锁/门卡开锁/指纹开锁
        tvContent.setText(content);
    }






}
