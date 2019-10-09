package com.yun.software.kaadas.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.jaeger.library.StatusBarUtil;
import com.kaadas.lock.store.R;
import com.yun.software.kaadas.Http.DisPostManager;
import com.yun.software.kaadas.Utils.ActivityCollectorUtil;

import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.NetUtil;
import la.xiong.androidquick.ui.base.QuickActivity;
import la.xiong.androidquick.ui.eventbus.EventCenter;


/**
 * @Description: 第一种类型的BaseActivity
 * @Detail: 不是每个activity都要用到的方法均保留在此;
 * 每个activity都要用到的方法则在每个activity中实现;
 * @Author: ddnosh
 * @Website http://blog.csdn.net/ddnosh
 */
public abstract class BaseActivity extends QuickActivity {

    protected static String TAG = "BaseActivity";

    @Override
    protected void getBundleExtras(Bundle extras) {
        if(extras!=null){
//            LogUtils.iTag("带过来数据", JSON.toJSONString(extras));
        }
    }
    @Override
    protected View getLoadingTargetView() {
        return null;
    }
    @Override
    protected void onEventComing(EventCenter eventCenter) {
        LogUtils.iTag(TAG, eventCenter.getEventCode() + "");
    }
    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.FADE;
    }



    @Override
    protected boolean isApplySystemBarTint() {
        return false;
    }

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        }
        super.onCreate(savedInstanceState);
//        if (isLoadDefaultTitleBar()){
//            StatebarTools.setPaddingSmart(mContext,commonHeaderView);
//        }
        ActivityCollectorUtil.addActivity(this);
        StatusBarUtil.setLightMode(this);
        if (!NetUtil.isNetworkAvailable(mContext)){
            toggleShowEmptyImage(true, R.drawable.network_missing_pages, null);
        }

    }


    @Override
    protected Intent getGoIntent(Class<?> clazz) {
        if (BaseFragment.class.isAssignableFrom(clazz)) {
            Intent intent = new Intent(this, FrameActivity.class);
            intent.putExtra("fragmentName", clazz.getName());
            return intent;
        } else {
            return super.getGoIntent(clazz);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollectorUtil.removeActivity(this);
        DisPostManager.getInstance().cancleAllDispose();

    }
}
