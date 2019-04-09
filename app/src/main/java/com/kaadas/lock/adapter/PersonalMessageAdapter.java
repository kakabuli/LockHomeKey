package com.kaadas.lock.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.kaadas.lock.R;
import com.kaadas.lock.bean.PersonalMessageBean;
import com.kaadas.lock.holder.RecyclerViewHolder;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;

import java.util.List;

public class PersonalMessageAdapter extends BaseRecyclerViewAdapter<PersonalMessageBean> {

    private OnDeleteClickLister mDeleteClickListener;
    private OnItemClickLister mOnItemClickLister;
    public PersonalMessageAdapter(Context context, List<PersonalMessageBean> data, int layoutId1, int layoutId2) {
        super(context, data, layoutId1, layoutId2);
    }
    @Override
    public int getItemViewType(int position) {
        int type = KeyConstants.SYSTEM_MESSAGE;
        switch (mData.get(position).getType()) {
            case KeyConstants.SYSTEM_MESSAGE:
                type = KeyConstants.SYSTEM_MESSAGE;
                break;
            case KeyConstants.SHARE_DEVICE_AUTHORIZATION_MESSAGE:
                type = KeyConstants.SHARE_DEVICE_AUTHORIZATION_MESSAGE;
                break;
            case KeyConstants.GATEWAY_AUTHORIZATION_MESSAGE:
                type = KeyConstants.GATEWAY_AUTHORIZATION_MESSAGE;
                break;
        }
        return type;
    }

    @Override
    protected void onBindData(RecyclerViewHolder holder, PersonalMessageBean bean, int position) {
        switch (bean.getType()) {
            case KeyConstants.SYSTEM_MESSAGE:
                TextView tvTitle = (TextView) holder.getView(R.id.tv_title);
                tvTitle.setText(bean.getTitle());
                TextView tvContent = (TextView) holder.getView(R.id.tv_content);
                tvContent.setText(bean.getContent());
                TextView tvTime = (TextView) holder.getView(R.id.tv_time);
                String time= DateUtils.timestampToDateStr(bean.getTime());
                tvTime.setText(time);
                break;
            case KeyConstants.SHARE_DEVICE_AUTHORIZATION_MESSAGE:
            case KeyConstants.GATEWAY_AUTHORIZATION_MESSAGE:
                tvTitle = (TextView) holder.getView(R.id.tv_title);
                tvTitle.setText(bean.getTitle());
                tvTime = (TextView) holder.getView(R.id.tv_time);
                 time= DateUtils.timestampToDateStr(bean.getTime());
                tvTime.setText(time);
                break;
        }
        View view = holder.getView(R.id.ll_delete);
        view.setTag(position);
        if (!view.hasOnClickListeners()) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDeleteClickListener != null) {
                        mDeleteClickListener.onDeleteClick(v, (Integer) v.getTag());
                    }
                }
            });
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickLister!=null){
                    mOnItemClickLister.onItemClick(v,position);
                }
            }
        });



    }

    public void setOnDeleteClickListener(OnDeleteClickLister listener) {
        this.mDeleteClickListener = listener;
    }

    public interface OnDeleteClickLister {
        void onDeleteClick(View view, int position);
    }

    public void setOnItemClickListenerMessage(OnItemClickLister listener) {
        this.mOnItemClickLister = listener;
    }

    public interface OnItemClickLister {
        void onItemClick(View view, int position);
    }

}
