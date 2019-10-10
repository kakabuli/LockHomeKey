package com.yun.software.kaadas.UI.activitys;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Comment.MyApplication;
import com.yun.software.kaadas.Comment.PreferencesConstans;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.Utils.CommonUtils;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import la.xiong.androidquick.tool.DialogUtil;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.SPUtils;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.ui.base.QuickActivity;
import la.xiong.androidquick.ui.dialog.CommonDialog;
import la.xiong.androidquick.ui.permission.AfterPermissionGranted;
import la.xiong.androidquick.ui.permission.EasyPermissions;

/**
 * Created by yanliang
 * on 2019/1/9 09:46
 */
public class WelcomeActivity extends BaseActivity {

    private String deniedPermsString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_welcome;
    }


    @Override
    protected boolean isApplySystemBarTint() {
        return false;
    }

    @Override
    protected void initViewsAndEvents() {


        permissionsCheck();


    }



    @AfterPermissionGranted(Constans.RC_PERMISSION_PERMISSION_ACTIVITY)
    protected void ongrate(){
     LogUtils.iTag("quanxian","success");

    }
    private void permissionsCheck() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.ACCESS_COARSE_LOCATION};
        performCodeWithPermission(1, Constans.RC_PERMISSION_PERMISSION_ACTIVITY, perms, new PermissionCallback() {
            @Override
            public void hasPermission(List<String> allPerms) {
                readyGo(MainActivity.class);
                finish();
                return;
            }
            @Override
            public void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied) {
                //                LogUtil.d(TAG, "deniedPerms:" + deniedPerms.toString());
                if (hasPermanentlyDenied) {
                    StringBuilder denied = new StringBuilder();
                    if(deniedPerms.contains(Manifest.permission.CAMERA)){
                        denied.append("相机权限");
                    }
                    if (deniedPerms.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        denied.append("存储权限");
                    }
                    if (deniedPerms.contains(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        denied.append("位置权限");
                    }
                    deniedPermsString = denied.toString();
                    EasyPermissions.goSettingsPermissions(WelcomeActivity.this, 2, Constans.RC_PERMISSION_PERMISSION_ACTIVITY, Constans.RC_PERMISSION_BASE);
                }else{
                    finish();
//                     permissionsCheck();
                }
            }
            @Override
            public void showDialog(int dialogType, final EasyPermissions.DialogCallback callback) {
                switch (dialogType) {
                    case 1:
                        DialogUtil.getDialogBuilder(mContext).
                                setTitle(getString(R.string.app_name)).
                                setMessage(getString(R.string.dialog_storage_permission)).
                                setPositiveButton("OK").
                                setBtnClickCallBack(new CommonDialog.DialogBtnCallBack() {
                                    @Override
                                    public void onDialogButClick(boolean isConfirm) {
                                        if (isConfirm){
                                            callback.onGranted();
                                        }else{
                                            finish();
                                        }

                                    }
                                }).show().setCancelable(false);
                        break;
                    case 2:
                        DialogUtil.getDialogBuilder(mContext).
                                setTitle(getString(R.string.app_name)).
                                setMessage(getString(R.string.dialog_rationale_ask_again, deniedPermsString)).
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constans.RC_PERMISSION_BASE) {
            permissionsCheck();
        }
    }

}
