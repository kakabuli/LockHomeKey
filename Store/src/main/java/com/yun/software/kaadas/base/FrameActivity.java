package com.yun.software.kaadas.base;

import android.os.Bundle;

import com.kaadas.lock.R;;import com.kaadas.lock.R2;;

import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.ReflectUtil;
import la.xiong.androidquick.ui.base.QuickFragment;

/**
 * @author  ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class FrameActivity extends BaseActivity {

    protected static String TAG = "FrameActivity";
    private Bundle bundle;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_frame;
    }

    @Override
    protected void initViewsAndEvents() {
        String className = getIntent().getStringExtra("fragmentName");
        LogUtils.i(TAG, "fragment 名称" + className);
        if (className != null) {
            Object object = ReflectUtil.getObject(className);
            if (object instanceof QuickFragment) {
                QuickFragment fragment = (QuickFragment) object;
                Bundle extre=getIntent().getExtras();
                if(extre!=null){
                    if(extre.containsKey("changebar")){
                        if("red".equals(extre.getString("changebar"))){
                            changeStatusBarColor(R.color.white);
                        }
                    }
                    fragment.setArguments(extre);
                }
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commitAllowingStateLoss();
                }
            } else {
                LogUtils.e(TAG, " the fragment class is not exist!!!");
            }
        }
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        bundle = extras;
    }


    @Override
    protected boolean isLoadDefaultTitleBar() {
        return  false;
    }


}
