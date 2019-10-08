package com.yun.software.kaadas.UI.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.software.kaadas.Comment.MyApplication;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.activitys.BigImageActivity;
import com.yun.software.kaadas.UI.bean.CommentsBean;
import com.yun.software.kaadas.UI.bean.ImgUrlBean;
import com.yun.software.kaadas.UI.view.MultiImageView;

import java.util.ArrayList;
import java.util.List;

import la.xiong.androidquick.tool.Glid.GlidUtils;


public class AppraiseListAdapter extends BaseQuickAdapter<CommentsBean,BaseViewHolder> {


    private List<CommentsBean> datas ;
    public AppraiseListAdapter(List<CommentsBean> datas) {
        super(R.layout.adapter_item_appraise, datas);
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
    protected void convert(BaseViewHolder helper, CommentsBean item) {

        ImageView imageView = helper.getView(R.id.image);
        GlidUtils.loadCircleImageView(MyApplication.getInstance().getApplicationContext(),item.getLogo(),imageView,R.drawable.pic_head_mine);
        helper.setText(R.id.user_name,item.getUserName());
        helper.setText(R.id.tv_create_time,item.getCreateDate());
        helper.setText(R.id.tv_attr,"饰面颜色:"+item.getProductLabels());
        helper.setText(R.id.tv_comment,item.getContent());


        MultiImageView multiImageView = (MultiImageView) helper.getView(R.id.multiImagView);
        if (multiImageView != null) {
            List<String> list= new ArrayList<>();
            for (String bean :item.getUrls()){
                list.add(bean);
            }


            if (list != null && list.size() > 0) {
                multiImageView.setVisibility(View.VISIBLE);
                multiImageView.setList(list);
                List<String> finalList = list;
                multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // 查看大图
                        BigImageActivity.startImagePagerActivity((Activity) mContext, finalList, position);
                    }
                });
            } else {
                multiImageView.setVisibility(View.GONE);
            }
        }

    }
}
