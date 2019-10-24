package com.yun.software.kaadas.UI.adapter;

import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.software.kaadas.Comment.MyApplication;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.ImgUrlBean;

import java.util.List;

import la.xiong.androidquick.tool.Glid.GlidUtils;
import la.xiong.androidquick.tool.ScreenUtils;


public class ShopImageListAdapter extends BaseQuickAdapter<ImgUrlBean,BaseViewHolder> {


    private List<ImgUrlBean> datas ;
    public ShopImageListAdapter(List<ImgUrlBean> datas) {
        super(R.layout.adapter_item_shop_image, datas);
        this.datas = datas;
    }

    @Override
    public int getItemCount() {
        if (datas != null ){
            return datas.size();
        }else {
            return 0;
        }

    }

    @Override
    protected void convert(BaseViewHolder helper, ImgUrlBean item) {
        ImageView imageView = helper.getView(R.id.image);
        int screenWidth = ScreenUtils.getScreenWidth(); // 获取屏幕宽度
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        lp.width = screenWidth;
        lp.height = lp.WRAP_CONTENT;
        imageView.setLayoutParams(lp);
        imageView.setMaxWidth(screenWidth);
        GlidUtils.loadImageNormal(mContext,item.getImgUrl(),imageView);

    }
}
