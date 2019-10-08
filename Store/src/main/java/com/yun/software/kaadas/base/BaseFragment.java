package com.yun.software.kaadas.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import la.xiong.androidquick.ui.base.QuickFragment;
import la.xiong.androidquick.ui.eventbus.EventCenter;

/**
 * @author  ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public abstract class BaseFragment extends QuickFragment {


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    protected void onFirstUserVisible() {
    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    protected void onEventComing(EventCenter eventCenter) {

    }

    @Override
    protected Intent getGoIntent(Class<?> clazz) {
        if (BaseFragment.class.isAssignableFrom(clazz)) {
            Intent intent = new Intent(getActivity(), FrameActivity.class);
            intent.putExtra("fragmentName", clazz.getName());
            return intent;
        } else {
            return super.getGoIntent(clazz);
        }
    }

    protected void readyGo(Class<?> clazz, Bundle bundle) {

        Intent intent = getGoIntent(clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);


    }

}
