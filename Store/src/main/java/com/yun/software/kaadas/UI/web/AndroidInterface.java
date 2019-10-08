package com.yun.software.kaadas.UI.web;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;


import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.StringUtil;

/**
 * Created by cenxiaozhong on 2017/5/14.
 *  source code  https://github.com/Justson/AgentWeb
 */

public class AndroidInterface {

    private Handler deliver = new Handler(Looper.getMainLooper());
    private EcWebActivity wA;

    public AndroidInterface(EcWebActivity wA) {
        this.wA = wA;
    }

    @JavascriptInterface
    public void callAndroid(final String msg) {
        LogUtils.iTag("AgentWebFragment","回调信息"+msg);



    }

}
