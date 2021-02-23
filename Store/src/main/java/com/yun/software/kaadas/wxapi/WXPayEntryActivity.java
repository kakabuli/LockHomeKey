package com.yun.software.kaadas.wxapi;


import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.UI.wxchat.BaseWXPayEntryActivity;

import org.greenrobot.eventbus.EventBus;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;

public class WXPayEntryActivity extends BaseWXPayEntryActivity {


    @Override
    protected void onPaySuccess() {
        LogUtils.iTag("WXPayEntryActivity","onpaysuccess");
        EventBus.getDefault().post(new EventCenter(Constans.MESSAGE_WX_PAYSUCCESS));
        finish();
        overridePendingTransition(0, 0);

    }

    @Override
    protected void onPayFail() {
        ToastUtil.showShort("支付失败");
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onPayCancel() {
        ToastUtil.showShort("取消支付");
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

}
