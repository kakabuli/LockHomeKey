package com.yun.software.kaadas.UI.activitys;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yun.software.kaadas.Http.ApiConstants;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.User;
import com.yun.software.kaadas.Utils.ImageUtils;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import la.xiong.androidquick.tool.Glid.GlidUtils;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;

public class MyCodeActivity extends BaseActivity {

    @BindView(R2.id.iv_code)
    ImageView ivcodeView;

    @BindView(R2.id.invite_code)
    TextView inviteCode;

    @BindView(R2.id.user_image)
    ImageView userImage;

    @BindView(R2.id.user_name)
    TextView userName;

    private User user;
    private boolean isFenxiang=false;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_my_code;
    }

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected void initViewsAndEvents() {

        user = getIntent().getParcelableExtra("user");
        if (user == null){
            finish();
            return;
        }
        userName.setText(user.getUserName());
        inviteCode.setText("我的邀请码:" +user.getRandomCode());
        GlidUtils.loadCircleImageView(mContext,user.getLogo(),userImage,R.drawable.pic_head_mine);

        tvTitle.setText("我的二维码");
        GlidUtils.loadImageNormal(mContext,user.getQrImgUrl(),ivcodeView);
        ivcodeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isFenxiang=false;
                ImageUtils.saveImageGallary(mContext,ivcodeView);
                return false;
            }
        });
    }

    @Override
    protected void onEventComing(EventCenter eventCenter) {
        super.onEventComing(eventCenter);
        if(eventCenter.getEventCode()== ApiConstants.SAVE_IMG_SUCCESS){

        }
    }

    @OnClick({R2.id.copy,R2.id.tv_share_wx})
    public void onClick(View view){
        int i = view.getId();//复制
        if (i == R.id.copy) {
            StringUtil.copyBord(user.getRandomCode(), mContext);
            ToastUtil.showShort("已经复制到剪切板");

        } else if (i == R.id.tv_share_wx) {
            isFenxiang = true;
            ImageUtils.saveImageGallary(mContext, ivcodeView);

        }

    }

}
