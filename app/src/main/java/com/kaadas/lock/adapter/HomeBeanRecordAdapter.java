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
import com.kaadas.lock.bean.BluetoothItemRecordBean;
import com.kaadas.lock.bean.BluetoothRecordBean;

import java.util.List;

/**
 * Created by David on 2019/2/16
 */


public class HomeBeanRecordAdapter extends BaseQuickAdapter<BluetoothRecordBean, BaseViewHolder> {


    public HomeBeanRecordAdapter(@Nullable List<BluetoothRecordBean> data) {
        super(R.layout.item_bluetooth_operation_record, data);
    }

    public interface onDataMoreListener{
        void onClickMore();
    }

    private onDataMoreListener mOnDataMoreListener = null;

    public void setOnDataMoreListener(onDataMoreListener listener){
        this.mOnDataMoreListener = listener;
    }

    @Override
    protected void convert(BaseViewHolder helper, BluetoothRecordBean bean) {
       TextView tvTitle= helper.getView(R.id.tv_title);
        TextView textView = helper.getView(R.id.tv_more);
        String time = bean.getTime();
        if (!TextUtils.isEmpty(time)){
            tvTitle.setText(time);
            tvTitle.setVisibility(View.VISIBLE);
            if(helper.getLayoutPosition() == 0)
                textView.setVisibility(View.VISIBLE);
        }else {
            tvTitle.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }
        RecyclerView recyclerView = helper.getView(R.id.item_recycleview);
        List<BluetoothItemRecordBean> data = bean.getList();
       // Log.e(GeTui.VideoLog,"数据是.....:"+data);
//        data.clear();
        if(data != null && data.size() > 0){

            BluetoothItemRecordAdapter bluetoothItemRecordAdapter = new BluetoothItemRecordAdapter(R.layout.item_item_bluetooth_record,data);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.setAdapter(bluetoothItemRecordAdapter);
        }
    /*    if (bluetoothItemRecordAdapter!=null){
            List<BluetoothItemRecordBean> dataList = bean.getList();
            data.addAll(dataList);
            bluetoothItemRecordAdapter.notifyDataSetChanged();
        }*/
        helper.getView(R.id.view_line).setVisibility(bean.isLastData() == true ? View.INVISIBLE : View.VISIBLE);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnDataMoreListener != null){
                    mOnDataMoreListener.onClickMore();
                }
            }
        });
    }


}
