package com.kaadas.lock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaadas.lock.holder.RecyclerViewHolder;
import com.kaadas.lock.utils.KeyConstants;

import java.util.List;

/**
 * RecyclerView adapter基类
 * <p>
 * 封装了数据集合以及ItemView的点击事件回调,同时暴露 {@link #onBindData(RecyclerViewHolder, Object, int)}
 * 用于数据与view绑定
 *
 * @param <T> A data bean class that will be used by the adapter.
 *            <p>
 *            Created by DavidChen on 2018/5/30.
 */

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> implements View.OnClickListener {

    public Context mContext;
    public List<T> mData;
    public int mLayoutId1;
    public int layoutId2;

    private OnItemClickListener mListener;

    public BaseRecyclerViewAdapter(Context context, List<T> data, int layoutId1, int layoutId2) {
        this.mContext = context;
        this.mData = data;
        this.mLayoutId1 = layoutId1;
        this.layoutId2 = layoutId2;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = new View(mContext);
        if (KeyConstants.SYSTEM_MESSAGE == viewType) {
            view = LayoutInflater.from(mContext).inflate(mLayoutId1, parent, false);
            view.setOnClickListener(this);
        } else if (KeyConstants.SHARE_DEVICE_AUTHORIZATION_MESSAGE == viewType) {
            view = LayoutInflater.from(mContext).inflate(layoutId2, parent, false);
            view.setOnClickListener(this);
        } else if (KeyConstants.GATEWAY_AUTHORIZATION_MESSAGE == viewType) {
            view = LayoutInflater.from(mContext).inflate(layoutId2, parent, false);
            view.setOnClickListener(this);
        }

        return new RecyclerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.itemView.setTag(position);
        T bean = mData.get(position);
        onBindData(holder, bean, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onItemClick(this, v, (Integer) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
    }

    /**
     * 数据绑定，由实现类实现
     *
     * @param holder   The reference of the all view within the item.
     * @param bean     The data bean related to the position.
     * @param position The position to bind data.
     */
    protected abstract void onBindData(RecyclerViewHolder holder, T bean, int position);

    /**
     * item点击监听器
     */
    public interface OnItemClickListener {
        /**
         * item点击回调
         *
         * @param adapter  The Adapter where the click happened.
         * @param v        The view that was clicked.
         * @param position The position of the view in the adapter.
         */
        void onItemClick(RecyclerView.Adapter adapter, View v, int position);
    }
}
