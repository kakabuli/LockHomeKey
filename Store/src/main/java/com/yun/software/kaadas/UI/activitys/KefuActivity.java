package com.yun.software.kaadas.UI.activitys;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.Utils.ImageUtils;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import la.xiong.androidquick.tool.DialogUtil;
import la.xiong.androidquick.tool.Glid.GlidUtils;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.base.QuickActivity;
import la.xiong.androidquick.ui.dialog.CommonDialog;
import la.xiong.androidquick.ui.permission.EasyPermissions;

public class KefuActivity extends BaseActivity {

    @BindView(R2.id.tv_qq)
    TextView tvQQ;

    @BindView(R2.id.tv_email)
    TextView tvEmail;

    @BindView(R2.id.ll_img)
    LinearLayout llimg;

    @BindView(R2.id.image)
    ImageView imageView;

    @BindView(R2.id.iv_icon)
    ImageView ivIocn;

    private boolean isShow = true;
    private String phoneNum;
    private String qq;
    private String email;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_kefu;
    }

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected void initViewsAndEvents() {
        tvTitle.setText("客户服务");
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ImageUtils.saveImageGallary(mContext,imageView);
                return false;
            }
        });
        getData();
    }


    @OnClick({R2.id.call_tel,R2.id.ll_weichat,R2.id.copy_qq,R2.id.copy_email})
    public void onClickView(View view){
        int i = view.getId();
        if (i == R.id.call_tel) {
            DialogUtil.getDialogBuilder(mContext)
                    .setTitle("拨打电话")
                    .setMessage("您确定拨打" + phoneNum + "?")
                    .setPositiveButton("确定")
                    .setNegativeButton("取消")
                    .setBtnClickCallBack(new CommonDialog.DialogBtnCallBack() {
                        @Override
                        public void onDialogButClick(boolean isConfirm) {
                            if (isConfirm) {
                                permissionsCheck();
                            }
                        }
                    }).show();


        } else if (i == R.id.ll_weichat) {
            if (isShow) {
                isShow = false;
                llimg.setVisibility(View.INVISIBLE);
                ivIocn.setImageResource(R.drawable.icon_downarrow_csh);
            } else {
                isShow = true;
                llimg.setVisibility(View.VISIBLE);
                ivIocn.setImageResource(R.drawable.icon_uparrow_csh);
            }

        } else if (i == R.id.copy_qq) {
            StringUtil.copyBord(qq, mContext);
            ToastUtil.showShort("复制成功");

        } else if (i == R.id.copy_email) {
            StringUtil.copyBord(email, mContext);
            ToastUtil.showShort("复制成功");

        }
    }



    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);

    }

    private void getData() {

        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());

        HttpManager.getInstance().post(mContext, ApiConstants.SERVERCONFIG_GETLIST, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                phoneNum = StringUtil.getJsonKeyStr(result,"phone");
                qq = StringUtil.getJsonKeyStr(result,"qq");
                email = StringUtil.getJsonKeyStr(result,"email");
                String wxUrl = StringUtil.getJsonKeyStr(result,"wxUrl");

                tvQQ.setText(qq);
                tvEmail.setText(email);
                GlidUtils.loadImageNormal(mContext,wxUrl,imageView);

            }

            @Override
            public void onFailed(String error) {

            }
        },false);


    }


    private void permissionsCheck() {
        String[] perms = {Manifest.permission.CALL_PHONE};
        performCodeWithPermission(1, Constans.RC_PERMISSION_PERMISSION_ACTIVITY, perms, new PermissionCallback() {
            @Override
            public void hasPermission(List<String> allPerms) {
                callPhone(phoneNum);
            }
            @Override
            public void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied) {
                //                LogUtil.d(TAG, "deniedPerms:" + deniedPerms.toString());
                if (hasPermanentlyDenied) {
                    StringBuilder denied = new StringBuilder();
                    if(deniedPerms.contains(Manifest.permission.CALL_PHONE)){
                        denied.append("电话权限");
                    }

                    EasyPermissions.goSettingsPermissions(KefuActivity.this, 2, Constans.RC_PERMISSION_PERMISSION_ACTIVITY, Constans.RC_PERMISSION_BASE);
                }
            }
            @Override
            public void showDialog(int dialogType, final EasyPermissions.DialogCallback callback) {
                switch (dialogType) {
                    case 1:
                        DialogUtil.getDialogBuilder(mContext).
                                setTitle(getString(R.string.app_name)).
                                setMessage("为了使APP能正常运行，请允许APP获得电话权限").
                                setPositiveButton("OK").
                                setBtnClickCallBack(new CommonDialog.DialogBtnCallBack() {
                                    @Override
                                    public void onDialogButClick(boolean isConfirm) {
                                        if (isConfirm){
                                            callback.onGranted();
                                        }else{

                                        }

                                    }
                                }).show().setCancelable(false);
                        break;
                    case 2:
                        DialogUtil.getDialogBuilder(mContext).
                                setTitle(getString(R.string.app_name)).
                                setMessage("APP缺少正常运行时所必须的权限，请在系统设置内允许APP获得电话权限").
                                setPositiveButton(getString(R.string.setting)).
                                setBtnClickCallBack(new CommonDialog.DialogBtnCallBack() {
                                    @Override
                                    public void onDialogButClick(boolean isConfirm) {
                                        if (isConfirm)
                                            callback.onGranted();
                                    }
                                }).show().setCancelable(false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

}
