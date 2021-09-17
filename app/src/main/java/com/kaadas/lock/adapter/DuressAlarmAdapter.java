package com.kaadas.lock.adapter;

import android.util.Log;
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
            if(bean.getPwdType() == 1){
                holder.setText(R.id.tv_password,R.string.password);
            }else if(bean.getPwdType() == 2){
                holder.setText(R.id.tv_password,R.string.fingerprint);
            }
            holder.getView(R.id.tv_password).setVisibility(View.VISIBLE);
            holder.getView(R.id.tv_password_num).setVisibility(View.GONE);
            holder.getView(R.id.rl_duress_alarm_toggle).setVisibility(View.GONE);
            holder.getView(R.id.rl_duress_alarm_password_notification).setVisibility(View.GONE);
            return;
        }else{
            holder.getView(R.id.tv_password).setVisibility(View.GONE);
            holder.getView(R.id.tv_password_num).setVisibility(View.VISIBLE);
            holder.getView(R.id.rl_duress_alarm_toggle).setVisibility(View.VISIBLE);
        }

        if(bean.getPwdDuressSwitch() == 0){
            holder.setText(R.id.tv_duress_alarm_toggle,R.string.close);
            holder.getView(R.id.rl_duress_alarm_password_notification).setVisibility(View.GONE);
        }else if(bean.getPwdDuressSwitch() == 1){
            holder.setText(R.id.tv_duress_alarm_toggle,R.string.open);
            holder.setText(R.id.tv_duress_alarm_phone,bean.getDuressAlarmAccount());
            holder.getView(R.id.rl_duress_alarm_password_notification).setVisibility(View.VISIBLE);
        }
        String sNum = bean.getNum() > 9 ? "" + bean.getNum() : "0" + bean.getNum();;
        if(bean.getNickName() != null && !bean.getNickName().isEmpty()){
            holder.setText(R.id.tv_password_num,bean.getNickName().isEmpty() ? mContext.getString(R.string.duress_number,sNum) : bean.getNickName());
        }else{
            holder.setText(R.id.tv_password_num,mContext.getString(R.string.duress_number,sNum));
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
