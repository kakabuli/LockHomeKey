package com.kaadas.lock.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockAlbumDetailActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockPreViewActivity;
import com.kaadas.lock.bean.FileBean;
import com.kaadas.lock.bean.FileItemBean;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.StringUtil;

import java.util.List;

import androidx.recyclerview.widget.SimpleItemAnimator;
import la.xiong.androidquick.tool.SizeUtils;


public class MyAlbumAdapter extends BaseQuickAdapter<FileBean, BaseViewHolder> {
    private Context context;

    public MyAlbumAdapter(List<FileBean> data, Context context) {
        super(R.layout.item_my_ablum_layout, data);
        this.context = context;
    }

    public onMyAlbumItemClickListener listener;

    public interface onMyAlbumItemClickListener{
        void onMyAlbumItemClick(MyAlbumItemAdapter adapter,List<FileItemBean> data,int position);
    }

    public void setOnMyAlbumItemClickListener(onMyAlbumItemClickListener listener){
        this.listener = listener;
    }
    @Override
    protected void convert(BaseViewHolder helper, FileBean item) {

         helper.setText(R.id.tv_date,item.getDate());

        RecyclerView recyclerView = helper.getView(R.id.recycleview);
        List<FileItemBean> data = item.getItem();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        if(item.isFirst()){
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
                }
            });
            item.setFirst(false);
        }
        recyclerView.setLayoutManager(gridLayoutManager);
        LogUtils.e("MyAlbumAdapter");
        MyAlbumItemAdapter adapter = new MyAlbumItemAdapter(data,context);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LogUtils.e(data.get(position).getPath());
                if(listener != null){
                    listener.onMyAlbumItemClick((MyAlbumItemAdapter)adapter,(List<FileItemBean>)adapter.getData(),position);
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

}
