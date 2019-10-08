package com.yun.software.kaadas.UI.wxchat;

import com.alibaba.fastjson.JSON;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import la.xiong.androidquick.tool.LogUtils;

/**
 * Created by yanliang
 * on 2019/3/29
 */

public abstract class BaseWXPayEntryActivity extends BaseWXActivity {

    private static final int WX_PAY_SUCCESS = 0;
    private static final int WX_PAY_FAIL = -1;
    private static final int WX_PAY_CANCEL = -2;

    protected abstract void onPaySuccess();

    protected abstract void onPayFail();

    protected abstract void onPayCancel();

    @Override
    public void onResp(BaseResp baseResp) {
        LogUtils.iTag("WXPayEntryActivity","有返回值");
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            LogUtils.iTag("payparams","微信返回值"+JSON.toJSONString(baseResp));
            switch (baseResp.errCode) {
                case WX_PAY_SUCCESS:
                    onPaySuccess();
                    break;
                case WX_PAY_FAIL:
                    onPayFail();
                    break;
                case WX_PAY_CANCEL:
                    onPayCancel();
                    break;
                default:
                    break;
            }
        }
    }
}
