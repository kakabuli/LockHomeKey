package com.yun.software.kaadas.UI.wxchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import la.xiong.androidquick.tool.LogUtils;

/**
 * Created by yanliang
 * on 2019/3/29
 */
public abstract class BaseWXActivity extends AppCompatActivity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.iTag("WXPayEntryActivity","onCreate");
        //这个必须写在onCreate中
        EcWeChat.getInstance().getWXAPI().handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LogUtils.iTag("WXPayEntryActivity","onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
        EcWeChat.getInstance().getWXAPI().handleIntent(getIntent(), this);
    }
}
