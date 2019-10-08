package com.yun.software.kaadas.Http.loader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;


import com.yun.software.kaadas.Comment.Setting;

import java.util.ArrayList;

import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.ui.dialog.LoadingDialog;

/**
 * Created by yanliang
 */

public class ProgressLoader {
     public static final  String TAG="ProgressLoader";
    private static final int LOADER_SIZE_SCALE = 8;
    private static final int LOADER_OFFSET_SCALE = 10;
    private static final ArrayList<Dialog> LOADERS = new ArrayList<>();
    private static final Handler HANDLER = Setting.getHandler();
    public static void showLoading(Context context, String tips) {
        if(context instanceof Activity){
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                try {
                   LoadingDialog loadingDialog = new LoadingDialog(context);
                    if (!StringUtil.isEmpty(tips)){
                        loadingDialog.setTip(tips);
                    }
                    loadingDialog.show();
                    LOADERS.add(loadingDialog);
                } catch (Throwable e) {
                    LogUtils.eTag(TAG,"加载失败");
                }
            }
        }
    }
    public static void showLoading(Context context) {
        showLoading(context,null);
    }
    public static void stopLoading() {
        HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (Dialog dialog : LOADERS) {
                    if (dialog != null) {
                        if (dialog.isShowing()) {
                            dialog.cancel();
                            dialog.dismiss();
                        }
                    }
                }
            }
        }, 500);
    }
}
