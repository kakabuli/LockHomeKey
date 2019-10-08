package com.yun.software.kaadas.UI.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.FeedPicture;

import java.util.List;

import la.xiong.androidquick.tool.Glid.GlidUtils;

/**
 * des:评论适配器
 * Created by xsf
 * on 2016.07.11:11
 */
public class SaftDetailImgsAdapter extends BaseQuickAdapter<FeedPicture, BaseViewHolder> {
    public boolean ismore = false;
    private boolean isEnglishVisible = false;
    private int itemSize;

    public SaftDetailImgsAdapter(List<FeedPicture> datas, int itemSize) {
        super(R.layout.sale_image_list, datas);
        this.itemSize = itemSize;
    }

    @Override
    public int getItemCount() {
        return getData().size();
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedPicture item) {
        ImageView img=helper.getView(R.id.view_iamge);
        GlidUtils.loadImageNormal(mContext,item.getUrl(),img);
        helper.addOnClickListener(R.id.view_iamge);

    }


}
