package com.yun.software.kaadas.UI.gallery;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.User;
import com.yun.software.kaadas.Utils.ImageUtils;
import com.yun.software.kaadas.Utils.UserUtils;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import la.xiong.androidquick.tool.Glid.GlidUtils;
import la.xiong.androidquick.tool.ToastUtil;

/**
 * Created by yanliang
 * on 2019/3/20
 */
public class ShareViewOne extends BaseShareView {
    View view;
    RelativeLayout reimg;
    private Context mContext;
    private int mPosition;
    private ImageView mShareImg;

    private ShareContent mShareContent;
    private ImageView qrCode;
    private CircleImageView iv_weixinhead;
    private TextView tv_weixinname;
    private User user;


    public ShareViewOne(Context context, ShareContent mShareContent) {
        mContext = context;
        this.mShareContent=mShareContent;
    }

    @Override
    public View getLayoutView() {
        if (view == null) {
            view = View.inflate(mContext, R.layout.share_one, null);
        }

        getUserInfo();

        mShareImg=view.findViewById(R.id.share_img);
        qrCode = view.findViewById(R.id.iv_qrcode);
        reimg=view.findViewById(R.id.re_choice_image);
        iv_weixinhead = view.findViewById(R.id.iv_weixinhead);
        tv_weixinname = view.findViewById(R.id.tv_weixinname);
        return view;
    }

    private void getUserInfo() {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        HttpManager.getInstance().post(mContext, ApiConstants.CUSTOMERAPP_MYINFO, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                Gson gson = new Gson();
                user = gson.fromJson(result,User.class);
                //微信头像
                GlidUtils.loadImageNormal(mContext, user.getLogo(),iv_weixinhead);
                tv_weixinname.setText(user.getUserName());
            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },false);
    }

    @Override
    public void initDate(int postion) {
        this.mPosition=postion;
        GlidUtils.loadImageNormal(mContext, mShareContent.getQrimgurl(),qrCode);
        GlidUtils.loadImageNormal(mContext, mShareContent.getImgpath(),mShareImg);
        mShareImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                saveImage();
                return true;
            }
        });
    }
    @Override
    public void saveImage(){
        ImageUtils.saveImageGallary(mContext,reimg,"share_kiddis_img"+mPosition+".jpg");
    }
    public void saveShareImage(){
        ImageUtils.saveImageGallary(mContext,reimg);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }



}
