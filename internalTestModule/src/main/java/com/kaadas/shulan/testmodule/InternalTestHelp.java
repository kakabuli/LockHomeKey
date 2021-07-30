package com.kaadas.shulan.testmodule;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.didichuxing.doraemonkit.DoKit;
import com.weijiaxing.logviewer.LogcatActivity;

public class InternalTestHelp {

    private void initDoKit(Application context){
        Log.d("InternalTestHelp","initDoKit");
        new DoKit.Builder(context)
                //.productId("需要使用平台功能的话，需要到dokit.cn平台申请id")
                .build();
    }

    private void startLogcatView(Context context){
        Log.d("InternalTestHelp","startLogcatView");
        LogcatActivity.launch(context);
    }

}
