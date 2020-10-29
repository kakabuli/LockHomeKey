package com.kaadas.lock.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huawei.android.hms.agent.common.UIUtils;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiLockVideoAlbumDetailActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiLockVideoPreViewActivity;
import com.kaadas.lock.bean.FileBean;
import com.kaadas.lock.bean.FileItemBean;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.StringUtil;
import com.lzy.imagepicker.util.Utils;

import java.util.List;

import la.xiong.androidquick.tool.SizeUtils;

/**
 * Create By denganzhi  on 2019/4/2
 * Describe
 */

public class MyAlbumAdapter extends BaseQuickAdapter<FileBean, BaseViewHolder> {
    private Context context;

    public MyAlbumAdapter(List<FileBean> data, Context context) {
        super(R.layout.item_my_ablum_layout, data);
        this.context = context;
    }


    @Override
    protected void convert(BaseViewHolder helper, FileBean item) {

         helper.setText(R.id.tv_date,item.getDate());
        LogUtils.e("recyclerView-->" + item.getDate());

        RecyclerView recyclerView = helper.getView(R.id.recycleview);
        LogUtils.e("recyclerView-->" + recyclerView);
        List<FileItemBean> data = item.getItem();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                if(parent.getChildAdapterPosition(view) % 3 != 0){
                    outRect.left = SizeUtils.dp2px(5);
                }
                if(parent.getChildAdapterPosition(view) >2){
                    outRect.top = SizeUtils.dp2px(5);
                }
//                super.getItemOffsets(outRect, view, parent, state);
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        LogUtils.e("MyAlbumAdapter");
        MyAlbumItemAdapter adapter = new MyAlbumItemAdapter(data,context);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LogUtils.e(data.get(position).getPath());
                if(((List<FileItemBean>)adapter.getData()).get(position).getType() == 1){
                    Intent intent = new Intent(context, WifiLockVideoPreViewActivity.class);
                    intent.putExtra(KeyConstants.VIDEO_PIC_PATH,((List<FileItemBean>)adapter.getData()).get(position).getPath());
                    String filename = ((List<FileItemBean>)adapter.getData()).get(position).getName();
                    filename = StringUtil.getFileNameNoEx(filename);
                    try {
                        filename = DateUtils.getStrFromMillisecond2(Long.parseLong(filename));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    intent.putExtra("NAME",filename);
                    context.startActivity(intent);
                }else if(((List<FileItemBean>)adapter.getData()).get(position).getType() == 2){
                    Intent intent = new Intent(context, WifiLockVideoAlbumDetailActivity.class);
                    String filename = ((List<FileItemBean>)adapter.getData()).get(position).getName();
                    filename = StringUtil.getFileNameNoEx(filename);
                    try{
                        filename = DateUtils.getStrFromMillisecond2(Long.parseLong(filename));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    intent.putExtra("NAME",filename);
                    intent.putExtra(KeyConstants.VIDEO_PIC_PATH,((List<FileItemBean>)adapter.getData()).get(position).getPath());
                    context.startActivity(intent);
                }

            }
        });
        adapter.notifyDataSetChanged();
    }

}
