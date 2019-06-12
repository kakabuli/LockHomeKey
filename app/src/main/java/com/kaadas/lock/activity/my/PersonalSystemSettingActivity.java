package com.kaadas.lock.activity.my;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.bean.UpgradeBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.SystemSettingPresenter;
import com.kaadas.lock.mvp.presenter.UpgradePresenter;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.CheckLanguageUtil;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.mvp.view.ISystemSettingView;
import com.kaadas.lock.utils.ftp.GeTui;
import com.kaadas.lock.widget.BottomMenuSelectMarketDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalSystemSettingActivity extends BaseActivity<ISystemSettingView, SystemSettingPresenter<ISystemSettingView>> implements ISystemSettingView, View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.version_num)
    TextView versionNum;
    @BindView(R.id.right)
    ImageView right;
    @BindView(R.id.version_layout)
    RelativeLayout versionLayout;
    @BindView(R.id.user_agreement_layout)
    RelativeLayout userAgreementLayout;
    @BindView(R.id.cache_data)
    TextView cacheData;
    @BindView(R.id.clear_cache_layout)
    RelativeLayout clearCacheLayout;
    @BindView(R.id.log_out)
    Button logOut;
    @BindView(R.id.rl_language_setting)
    RelativeLayout rlLanguageSetting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_system_setting);
        ButterKnife.bind(this);
        initView();
        ivBack.setOnClickListener(this);
        rlLanguageSetting.setOnClickListener(this);
        tvContent.setText(R.string.setting);
        upgradePresenter=new UpgradePresenter();
        initPackages(this);
    }

    private void initView() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(this.getPackageName(), 0);
            String version = packageInfo.versionName;
            versionNum.setText(getString(R.string.current_version) + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected SystemSettingPresenter<ISystemSettingView> createPresent() {
        return new SystemSettingPresenter<>();
    }


    @OnClick({R.id.version_layout, R.id.user_agreement_layout, R.id.clear_cache_layout, R.id.log_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            /*case R.id.language_setting_layout:
                Intent languageIntent = new Intent(this, PersonalLanguageSettingActivity.class);
                startActivity(languageIntent);
                break;*/
            case R.id.version_layout:
                checkVersion();
                break;
            case R.id.user_agreement_layout:
                Intent agreementIntent = new Intent(this, PersonalUserAgreementActivity.class);
                startActivity(agreementIntent);
                break;

            case R.id.clear_cache_layout:
                clearData();
                break;
            case R.id.log_out:
                loginOut();
                break;
        }
    }

    private void clearData() {
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint), getString(R.string.delete_cache_data), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {

            }

            @Override
            public void right() {
                //清除缓存数据，关闭会话。
                ToastUtil.getInstance().showShort(R.string.delete_cache_data_success);
            }
        });


    }

    private void loginOut() {
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint), getString(R.string.confirm_log_out), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {

            }

            @Override
            public void right() {
                mPresenter.loginOut();
                //退出mqtt
                if (MyApplication.getInstance().getMqttService()!=null){
                  MyApplication.getInstance().getMqttService().httpMqttDisconnect();
                }
                MyApplication.getInstance().tokenInvalid(false);

                //清除缓存数据，关闭会话。
//                MyApplication.getInstance().setRemoveDBData();
//                MyApplication.getInstance().getDaoSession().clear();
            }
        });
    }

    @Override
    public void onLoginOutSuccess() {

    }

    @Override
    public void onLoginOutFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.logout_fail) + HttpUtils.httpProtocolErrorCode(this, throwable));
        LogUtils.e("退出失败  " + throwable.getMessage());
    }

    @Override
    public void onLoginOutFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(getString(R.string.logout_fail) + HttpUtils.httpErrorCode(this, result.getCode()));
        LogUtils.e("退出失败  " + result.getMsg());
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckLanguageUtil.getInstance().checkLag();
    }

    UpgradePresenter upgradePresenter=null;
    public void checkVersion() {
//        Boolean updateFalg = (Boolean) SPUtils.get(SPUtils.APPUPDATE, false);
//        if (updateFalg == true) {
//            appUpdateDialog();
//        } else {
//            ToastUtil.getInstance().showShort(R.string.new_version);
//        }
        upgradePresenter.getUpgreadJson(new UpgradePresenter.IUpgradePresenter() {
            @Override
            public void ShowUpgradePresenterSuccess(String jsonPresenterResult) {
                //   Log.e(GeTui.VideoLog,jsonPresenterResult);
                if(!TextUtils.isEmpty(jsonPresenterResult)){
                    UpgradeBean upgradeBean=new Gson().fromJson(jsonPresenterResult,UpgradeBean.class);
                    try {
                        PackageManager manager =getPackageManager();
                        PackageInfo packageInfo = manager.getPackageInfo(getPackageName(), 0);
                        int cuurentversioncode= packageInfo.versionCode;
                        String versionname=packageInfo.versionName;
                        int servercode= Integer.parseInt(upgradeBean.getVersionCode());
                        boolean isPrompt = Boolean.parseBoolean(upgradeBean.getIsPrompt()); //是否提示用户升级
                        boolean isForced = Boolean.parseBoolean(upgradeBean.getIsForced()); //是否强制升级
                       // if(isPrompt){
                            if(servercode > cuurentversioncode){
                                  SelectMarket(isForced,upgradeBean);

                            }else {
                                ToastUtil.getInstance().showShort(R.string.new_version);
                            }
                      //  }
                        Log.e(GeTui.VideoLog,"currentCode:"+cuurentversioncode+" servercode:"+upgradeBean.getVersionCode());

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else {
                    Log.e(GeTui.VideoLog,"update.....获取数据为null");
                    ToastUtil.getInstance().showShort(R.string.get_version_code_fail);
                }
            }

            @Override
            public void ShowUpgradePresenterFail() {
              //  Log.e(GeTui.VideoLog,"update.....fail.......失败");
                ToastUtil.getInstance().showShort(R.string.get_version_code_fail);
            }
        });
    }

    private void toMarkApp() {
        Uri uri = Uri.parse("market://details?id=" + "com.kaidishi.aizhijia");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void appUpdateDialog() {
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint), getString(R.string.find_newAPP), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {

            }

            @Override
            public void right() {
                toMarkApp();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_language_setting:
                intent = new Intent(this, PersonalLanguageSettingActivity.class);
                startActivity(intent);
                break;
        }
    }
    public static final String[] supportMarket = new String[]{
            "com.xiaomi.market",
            "com.huawei.appmarket",
            "com.oppo.market",
            "com.tencent.android.qqdownloader",
            "com.android.vending",
            "com.bbk.appstore",
    };
    /**
     * 获取APP支持的且手机安装的应用市场列表
     *
     * @param context
     * @return
     */
    public static List<String> initPackages(Context context) {
        ArrayList<String> pkgs = new ArrayList<>();
        if (context == null)
            return pkgs;
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("market://details?id="));
        PackageManager pm = context.getPackageManager();
        // 通过queryIntentActivities获取ResolveInfo对象
        List<ResolveInfo> infos = pm.queryIntentActivities(intent,
                0);
        if (infos == null || infos.size() == 0)
            return pkgs;
        int size = infos.size();
        for (int i = 0; i < size; i++) {
            String pkgName = "";
            try {
                ActivityInfo activityInfo = infos.get(i).activityInfo;
                pkgName = activityInfo.packageName;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(pkgName))
                pkgs.add(pkgName);
        }

        packages = new ArrayList<>();

        List<String> temp = Arrays.asList(supportMarket);
        for (String s : pkgs) {
            if (temp.contains(s)) {
                packages.add(s);
            }
        }
        return packages;
    }

    //com.xiaomi.market 小米应用商店
    //com.huawei.appmarket 华为应用商店
    //com.oppo.market OPPO应用商店
    //com.tencent.android.qqdownloader 腾讯应用宝
    //com.android.vending    Google player
    //com.bbk.appstore		vivo应用商店

    private String getNameByPackage(String pkg) {
        if (pkg.equals(supportMarket[0])) {
            return getString(R.string.xiaomi_market);
        } else if (pkg.equals(supportMarket[1])) {
            return getString(R.string.huawei_market);

        } else if (pkg.equals(supportMarket[2])) {
            return getString(R.string.oppo_market);
        } else if (pkg.equals(supportMarket[3])) {
            return getString(R.string.tengxun_market);
        } else if (pkg.equals(supportMarket[4])) {
            return getString(R.string.google_market);
        } else if (pkg.equals(supportMarket[5])) {
            return getString(R.string.vivo_market);
        }
        return "";
    }

    private static List<String> packages;
    private BottomMenuSelectMarketDialog bottomMenuDialog;
    public void SelectMarket(boolean isforce,UpgradeBean upgradeBean) {
        BottomMenuSelectMarketDialog.Builder dialogBuilder = new BottomMenuSelectMarketDialog.Builder(this);
        for (final String pkg : packages) {
            String menu = getNameByPackage(pkg);
            dialogBuilder.setVersionStr(upgradeBean.getVersionName());
            dialogBuilder.addMenu(menu, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bottomMenuDialog != null) {
                        drumpMarket(pkg);
                        bottomMenuDialog.dismiss();
                    }
                }
            });
        }

        dialogBuilder.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Message message = new Message();
//                message.what = 2;
//                mHandler.sendMessage(message);
            //    if(isforce){
               //     Toast.makeText(PersonalSystemSettingActivity.this,getString(R.string.isforce),Toast.LENGTH_SHORT).show();
//                }else {
                     bottomMenuDialog.dismiss();
//                }

            }
        });

//        if (versionBean.getIsForced()) {
//            dialogBuilder.goneCancel();
//        }
        bottomMenuDialog = dialogBuilder.create();
        bottomMenuDialog.setCancelable(false);
        bottomMenuDialog.show();
    }
    private void drumpMarket(String packageName) {
        Log.e(GeTui.VideoLog,"跳入的市场是:"+packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("market://details?id=" + getPackageName());//app包名
        intent.setData(uri);
        intent.setPackage(packageName);//应用市场包名
        startActivity(intent);
    }

}
