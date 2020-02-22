package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.BluetoothLockFunctionBean;
import com.kaadas.lock.bean.SingleSwitchTimerShowBean;
import com.kaadas.lock.utils.LogUtils;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by David on 2019/2/16
 */


public class SingleSwitchTimerAdapter extends BaseQuickAdapter<SingleSwitchTimerShowBean, BaseViewHolder> {


    public SingleSwitchTimerAdapter(@Nullable List<SingleSwitchTimerShowBean> data) {
        super(R.layout.item_single_switch_timer_layout, data);
        LogUtils.e("数据是  " + data.toString());
    }

    @Override
    protected void convert(BaseViewHolder helper, SingleSwitchTimerShowBean bean) {
        ImageView waitIcon =   helper.getView(R.id.iv_wait);
        ImageView switchIcon =   helper.getView(R.id.iv_switch);
        TextView time =   helper.getView(R.id.tv_time);
        TextView repeat =   helper.getView(R.id.tv_repeat);
        TextView action =   helper.getView(R.id.tv_action);
        time.setText(bean.getTime());
        action.setText(bean.getAction());
        repeat.setText(bean.getRepeat());
        if (bean.isOpen()){
            waitIcon.setImageResource(R.mipmap.single_switch_timer_wait_blue );
            switchIcon.setImageResource(R.mipmap.single_switch_timer_switch_blue);
        }else {
            waitIcon.setImageResource(R.mipmap.single_switch_timer_wait_gray );
            switchIcon.setImageResource(R.mipmap.single_switch_timer_switch_gray);
        }
    }
}
