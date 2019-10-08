package com.yun.software.kaadas.UI.web;

import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import la.xiong.androidquick.tool.LogUtils;

/**
 * Created by cenxiaozhong on 2017/5/14.
 *  source code  https://github.com/Justson/AgentWeb
 */

public class AndroidInterFace2 {

    private Handler deliver = new Handler(Looper.getMainLooper());


    public AndroidInterFace2() {

    }

    @JavascriptInterface
    public void callAndroid(final String msg) {
        LogUtils.iTag("AgentWebFragment","回调信息"+msg);

    }

}
