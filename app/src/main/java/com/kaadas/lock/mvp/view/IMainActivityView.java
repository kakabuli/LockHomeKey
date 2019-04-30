package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;


/**
 * Create By lxj  on 2019/3/18
 * Describe
 */
public interface IMainActivityView extends IBleView {

    void onWarringUp(String warringContent);

    void onDeviceInBoot(BleLockInfo bleLockInfo);

}
