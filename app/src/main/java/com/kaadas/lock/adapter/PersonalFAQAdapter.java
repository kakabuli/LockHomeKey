package com.kaadas.lock.adapter;

import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.FAQBean;


import java.util.List;

public class PersonalFAQAdapter extends BaseQuickAdapter<FAQBean, BaseViewHolder> {


    private OnItemClickLister onItemClickLister;

    public void setOnItemClickLister(OnItemClickLister onItemClickLister) {
        this.onItemClickLister = onItemClickLister;
    }

    public PersonalFAQAdapter(@Nullable List<FAQBean> data) {
        super(R.layout.personal_faq_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FAQBean item) {
        TextView mTitle = helper.getView(R.id.faq_title);
        mTitle.setText(item.getTitle());
        TextView mContent = helper.getView(R.id.faq_content);
        mContent.setText(item.getContent());
        LinearLayout llContent = helper.getView(R.id.ll_content);
        if (item.getFlag() == false) {
            mContent.setVisibility(View.GONE);
        } else {
            mContent.setVisibility(View.VISIBLE);
        }
        ImageView imageView = helper.getView(R.id.faq_folding);
        View view = helper.getView(R.id.faq_layout);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击之前已经是打开状态
                if (item.getFlag() == true) {
                    //关闭
                    llContent.setVisibility(View.GONE);
                    imageView.setImageResource(R.mipmap.right);
                    mContent.setVisibility(View.GONE);
                    item.setFlag(false);
                } else {
                    llContent.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.mipmap.down);
                    mContent.setVisibility(View.VISIBLE);
                    item.setFlag(true);
                }

            }
        });


    }

    public interface OnItemClickLister {
        void onItemClick(View view);
    }


}
