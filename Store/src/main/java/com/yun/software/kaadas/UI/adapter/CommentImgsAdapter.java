package com.yun.software.kaadas.UI.adapter;

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
public class CommentImgsAdapter  extends BaseQuickAdapter<FeedPicture, BaseViewHolder> {
    public boolean ismore = false;
    private boolean isEnglishVisible = false;
    private int itemSize;

    public CommentImgsAdapter(List<FeedPicture> datas, int itemSize) {
        super(R.layout.feed_image_list, datas);
        this.itemSize = itemSize;
    }

    @Override
    public int getItemCount() {
        return getData().size();
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedPicture item) {
        helper.addOnClickListener(R.id.view_delete).addOnClickListener(R.id.view_iamge);
        if (item.isShowDelete()) {
            helper.setVisible(R.id.view_delete, true);
        } else {
            helper.setVisible(R.id.view_delete, false);
        }
        if (item.getPath().equals("temp")) {
            helper.setImageResource(R.id.view_iamge, R.drawable.icon_camera_add3x);
        } else {
            GlidUtils.loadImageNormal(mContext, item.getPath(), helper.getView(R.id.view_iamge));
        }

    }


}
