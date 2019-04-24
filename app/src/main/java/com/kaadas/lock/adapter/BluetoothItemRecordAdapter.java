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
import com.kaadas.lock.bean.BluetoothRecordBean;
import com.kaadas.lock.utils.KeyConstants;

import java.util.List;

/**
 * Created by David on 2019/2/16
 */


public class BluetoothItemRecordAdapter extends BaseQuickAdapter<BluetoothItemRecordBean, BaseViewHolder> {


    public BluetoothItemRecordAdapter(@Nullable List<BluetoothItemRecordBean> data) {
        super(R.layout.item_item_bluetooth_record, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, BluetoothItemRecordBean bean) {
        TextView tvTime = helper.getView(R.id.tv_time);
        tvTime.setText(bean.getOpen_time());
        helper.getView(R.id.view_top).setVisibility(bean.isFirstData() == true ? View.INVISIBLE : View.VISIBLE);
        helper.getView(R.id.view_bottom).setVisibility(bean.isLastData() == true ? View.INVISIBLE : View.VISIBLE);
        ImageView iv = helper.getView(R.id.iv);
        String imgIcon = bean.getIconImg();
        if (KeyConstants.BLUETOOTH_RECORD_COMMON.equals(imgIcon)) {
            iv.setImageResource(R.mipmap.bluetooth_common_icon);
        } else if (KeyConstants.BLUETOOTH_RECORD_WARN.equals(imgIcon)) {
            iv.setImageResource(R.mipmap.bluetooth_warn_icon);
        }
        TextView tvContent = helper.getView(R.id.tv_content);
        tvContent.setText(bean.getContent());
        TextView tvRight = helper.getView(R.id.tv_right);
        String strRight = bean.getStrRight();
//        机械开锁/APP开锁/自动开锁/密码开锁/门卡开锁/指纹开锁
        if (!TextUtils.isEmpty(strRight)){
           tvRight.setText(strRight);
        }

    }


}
