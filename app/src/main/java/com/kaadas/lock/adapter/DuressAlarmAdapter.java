package com.kaadas.lock.adapter;

import android.view.View;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.DuressBean;

import org.jetbrains.annotations.NotNull;

public class DuressAlarmAdapter extends BaseQuickAdapter<DuressBean, BaseViewHolder> {

    private OnClickDuressNotificationListener mOnClickDuressNotificationListener;

    public DuressAlarmAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, DuressBean bean) {
        if(bean == null) return;

        if(bean.isHead()){
            if(bean.getType() == 1){
                holder.setText(R.id.tv_password,R.string.password);
            }else if(bean.getType() == 2){
                holder.setText(R.id.tv_password,R.string.fingerprint);
            }
            holder.getView(R.id.tv_password).setVisibility(View.VISIBLE);
            holder.getView(R.id.tv_password_num).setVisibility(View.GONE);
            holder.getView(R.id.rl_duress_alarm_toggle).setVisibility(View.GONE);
            holder.getView(R.id.rl_duress_alarm_password_notification).setVisibility(View.GONE);
            return;
        }else{
            holder.getView(R.id.tv_password).setVisibility(View.GONE);
        }

        if(bean.getDuressToggle() == 0){
            holder.setText(R.id.tv_duress_alarm_toggle,R.string.close);
            holder.getView(R.id.rl_duress_alarm_password_notification).setVisibility(View.GONE);
        }else if(bean.getDuressToggle() == 1){
            holder.setText(R.id.tv_duress_alarm_toggle,R.string.open);
            holder.setText(R.id.tv_duress_alarm_phone,bean.getDuressPhone());
            holder.getView(R.id.rl_duress_alarm_password_notification).setVisibility(View.VISIBLE);
        }

        if(bean.getNickName() != null && !bean.getNickName().isEmpty()){
            holder.setText(R.id.tv_password_num,bean.getNickName().isEmpty() ? bean.getNum() : bean.getNickName());
        }else{
            holder.setText(R.id.tv_password_num,bean.getNum());
        }

        holder.getView(R.id.rl_duress_alarm_toggle).setOnClickListener(v -> {
            if(mOnClickDuressNotificationListener != null){
                mOnClickDuressNotificationListener.onClick(v,holder.getLayoutPosition(),bean);
            }
        });
    }

    public void setOnClickDuressNotificationListener(OnClickDuressNotificationListener onClickDuressNotificationListener) {
        mOnClickDuressNotificationListener = onClickDuressNotificationListener;
    }

    public interface OnClickDuressNotificationListener {
        void onClick(View v, int position, @NotNull DuressBean data);
    }
}
