package com.yun.software.kaadas.UI.adapter;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.software.kaadas.Comment.MyApplication;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.activitys.BigImageActivity;
import com.yun.software.kaadas.UI.activitys.CommentStartActvity;
import com.yun.software.kaadas.UI.bean.FeedPicture;
import com.yun.software.kaadas.UI.bean.SubmitComment;

import java.util.ArrayList;
import java.util.List;

import la.xiong.androidquick.tool.Glid.GlidUtils;
import la.xiong.androidquick.tool.ScreenUtils;
import la.xiong.androidquick.ui.widget.StarBarView;


public class CommentListAdapter extends BaseQuickAdapter<SubmitComment,BaseViewHolder> {


    private List<SubmitComment> datas ;
    private CommentImgsAdapter mCommentimgsAdapter;

    private CommentStartActvity actvity;


    public CommentListAdapter(List<SubmitComment> datas, CommentStartActvity actvity) {
        super(R.layout.item_comment_start, datas);
        this.datas = datas;
        this.actvity = actvity;
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
    protected void convert(BaseViewHolder helper, SubmitComment item) {
        StarBarView starBarChanpin = helper.getView(R.id.starbar_chanpin);
        StarBarView starBarShangpin = helper.getView(R.id.starbar_shangpin);
        StarBarView starBarPeisong = helper.getView(R.id.starbar_peisong);
        StarBarView starBarAnzhuang = helper.getView(R.id.starbar_anzhuang);
        ImageView imageView = helper.getView(R.id.image);

        EditText editText = helper.getView(R.id.edit_text);
        RecyclerView rcImgs = helper.getView(R.id.rc_imgs);
        CheckBox checkBox = helper.getView(R.id.checkbox);

        GlidUtils.loadImageNormal(mContext,item.getLogo(),imageView);

        starBarChanpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setScore((int) starBarChanpin.getStarRating());
            }
        });
        starBarShangpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setConformityScore((int) starBarShangpin.getStarRating());
            }
        });
        starBarPeisong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setDispatchingScore((int) starBarPeisong.getStarRating());
            }
        });
        starBarAnzhuang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setInstallSatisfiedScore((int) starBarAnzhuang.getStarRating());
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                item.setContent(s.toString());
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setAnonymous(isChecked);
            }
        });

        rcImgs.setHasFixedSize(true);
        rcImgs.setLayoutManager(new GridLayoutManager(mContext, 3));

        List<FeedPicture> pictures = item.getPictures();

        Drawable decoration = ContextCompat.getDrawable(mContext, R.drawable.album_decoration_white);
                int itemSize = (ScreenUtils.getScreenWidth() - decoration.getIntrinsicWidth() * (3 + 1)) / 3;
                  mCommentimgsAdapter = new CommentImgsAdapter(pictures, itemSize);
                  rcImgs.setAdapter(mCommentimgsAdapter);
                  mCommentimgsAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        int i = view.getId();
                        if (i == R.id.view_iamge) {
                            if (pictures.get(position).getPath().equals("temp")) {
                                actvity.initDialogChooseImage(item.getProductId(), position);
                            } else {
                                List<String> strings = new ArrayList<>();
                                strings.add(pictures.get(position).getPath());
                                BigImageActivity.startImagePagerActivity(actvity, strings, 0);
                            }

                        } else if (i == R.id.view_delete) {
                            pictures.remove(position);
                            if (pictures.size() < 4) {
                                if (pictures.get(pictures.size() - 1).getPath().equals("temp")) {
                                } else {
                                    pictures.add(new FeedPicture("temp", false, ""));
                                }
                            }
                            mCommentimgsAdapter.notifyDataSetChanged();

                        }
                    }
                });
    }
}
