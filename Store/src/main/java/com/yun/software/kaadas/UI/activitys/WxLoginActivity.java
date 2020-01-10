package com.yun.software.kaadas.UI.activitys;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Comment.PreferencesConstans;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.User;
import com.yun.software.kaadas.UI.wxchat.MyPlatforActionListener;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import la.xiong.androidquick.task.TaskScheduler;
import la.xiong.androidquick.tool.DialogUtil;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.SPUtils;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.dialog.CommonDialog;
import la.xiong.androidquick.ui.permission.EasyPermissions;

/**
 * Created by yanliang
 * on 2019/6/18
 */
public class WxLoginActivity extends BaseActivity {
    @BindView(R2.id.re_wx_login)
    LinearLayout wxLogin;


    @BindView(R2.id.btn_tel)
    TextView btnTel;
    @BindView(R2.id.rl_wxlogin)
    RelativeLayout rlWxlogin;

    private String deniedPermsString;

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_wx_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViewsAndEvents() {
        tvTitle.setText("微信登录");
        getData();
//        String token= SPUtils.getInstance().getString(PreferencesConstans.TOKEN);
//        if (!TextUtils.isEmpty(token)){
//            MyApplication.getInstance().setToken(token);
//            readyGo(MainActivity.class);
//            finish();
//            return;
//        }
        rlWxlogin.setVisibility(View.GONE);
        permissionsCheck();
        wxLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWXAuther();
            }
        });

    }

    private void getData() {

        Map<String, Object> map = new HashMap<>();
//        map.put("token", UserUtils.getToken());
        Map<String, Object> params = new HashMap<>();
        params.put("categorys", new String[]{"login_type"});
        map.put("params", params);


        HttpManager.getInstance().post(mContext, ApiConstants.DICT_GETLISTVALUE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                String loginType = StringUtil.getJsonKeyStr(result, "login_type");
                String istellogin = StringUtil.getJsonKeyStr(loginType, "istellogin1");
                if (istellogin.equals("true")) {
                    rlWxlogin.setVisibility(View.GONE);
                    finish();
//                    btnTel.setVisibility(View.VISIBLE);
//                    wxLogin.setVisibility(View.GONE);
//                    tvTitle.setText("手机号登录");
                } else {
                    rlWxlogin.setVisibility(View.VISIBLE);
                    btnTel.setVisibility(View.GONE);
                    wxLogin.setVisibility(View.VISIBLE);
                    tvTitle.setText("微信登录");
                }
//                btnTel.setVisibility(View.GONE);
//                wxLogin.setVisibility(View.VISIBLE);
//                tvTitle.setText("微信登录");
            }

            @Override
            public void onFailed(String error) {

            }
        }, true);
    }

    private void startWXAuther() {
        DialogUtil.showLoadingDialog(mContext, "正在登录");
        ShareSDK.setEnableAuthTag(true);
        Platform platform = ShareSDK.getPlatform(Wechat.NAME);
        if (platform != null) {
            platform.setPlatformActionListener(new MyPlatforActionListener() {
                @Override
                public void onDownLoad() {
                    LogUtils.iTag("ceshi");
                    DialogUtil.dismissLoadingDialog(mContext);
                }

                @Override
                public void onCopy() {
                    LogUtils.iTag("ceshi", "onCopy");
                    DialogUtil.dismissLoadingDialog(mContext);
                }

                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    LogUtils.iTag("ceshi", "个人信息" + hashMap.toString());
                    String openid = StringUtil.toString(hashMap.get("openid"));
                    String unionid = StringUtil.toString(hashMap.get("unionid"));
                    String headimgurl = StringUtil.toString(hashMap.get("headimgurl"));
                    String nickname = StringUtil.toString(hashMap.get("nickname"));

                    TaskScheduler.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {

                            loginWx(unionid, openid, headimgurl, nickname);
                        }
                    });

                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                    LogUtils.iTag("ceshi", "onError" + throwable.getMessage());
                    DialogUtil.dismissLoadingDialog(mContext);
                }

                @Override
                public void onCancel(Platform platform, int i) {
                    LogUtils.iTag("ceshi", "onCancel");
                    DialogUtil.dismissLoadingDialog(mContext);
                }
            });
            if (platform.isAuthValid()) {
                LogUtils.iTag("ceshi", "----->已经授权");
                platform.removeAccount(true);
            }
            if (platform != null) {
                platform.showUser(null);
            }
        }
    }

    private void loginWx(String uid, String oid, String headImg, String nikename) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("unionId", uid);
        params.put("openId", oid);
        params.put("headImg", headImg);
        params.put("nickName", nikename);
        map.put("params", params);
        HttpManager.getInstance().post(mContext, ApiConstants.CUSTOMER_WX_LOGIN, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                LogUtils.iTag("wximgerwe", result);
                DialogUtil.dismissLoadingDialog(mContext);
                Gson gson = new Gson();
                String token = StringUtil.getJsonKeyStr(result, "token");
                String customer = StringUtil.getJsonKeyStr(result, "customer");
                String qrImgUrl = StringUtil.getJsonKeyStr(result, "qrImgUrl");
                LogUtils.iTag("wximgerwe", "qrImgUrl====>" + qrImgUrl);
                User user = gson.fromJson(customer, User.class);
                UserUtils.setToken(token);
                UserUtils.setUserID(user);
                UserUtils.setUserTel(user.getTel());
                UserUtils.setYanbaoka(user.isGetTimeDelay());

                UserUtils.setTelState(true);
                Bundle bundle = new Bundle();
                bundle.putString("from", "1");
                readyGo(MainActivity.class, bundle);
                finish();
            }

            @Override
            public void onFailed(String error) {
                DialogUtil.dismissLoadingDialog(mContext);
                ToastUtil.showShort(error);
            }
        }, false);
    }


    private void permissionsCheck() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
        performCodeWithPermission(1, Constans.RC_PERMISSION_PERMISSION_ACTIVITY, perms, new PermissionCallback() {
            @Override
            public void hasPermission(List<String> allPerms) {
                //                UserUtils.setToken("");
                if (!StringUtil.isBlank(UserUtils.getToken())) {
                    Constans.token = SPUtils.getInstance().getString(PreferencesConstans.TOKEN);
                    Observable.just(1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer aLong) throws Exception {
                            readyGo(MainActivity.class);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied) {
                //                LogUtil.d(TAG, "deniedPerms:" + deniedPerms.toString());
                if (hasPermanentlyDenied) {
                    StringBuilder denied = new StringBuilder();
                    if (deniedPerms.contains(Manifest.permission.CAMERA)) {
                        denied.append("相机权限");
                    }
                    if (deniedPerms.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        denied.append("存储权限");
                    }
                    if (deniedPerms.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        denied.append("位置权限");
                    }
                    deniedPermsString = denied.toString();
                    EasyPermissions.goSettingsPermissions(WxLoginActivity.this, 2, Constans.RC_PERMISSION_PERMISSION_ACTIVITY, Constans.RC_PERMISSION_BASE);
                } else {
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
                                        if (isConfirm) {
                                            callback.onGranted();
                                        } else {
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
}

