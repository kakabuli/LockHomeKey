package com.yun.software.kaadas.UI.wxchat;

import android.app.Activity;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yun.software.kaadas.Comment.ConfigKeys;
import com.yun.software.kaadas.Comment.Setting;
import com.yun.software.kaadas.UI.wxchat.callbacks.IWeChatSignInCallback;


/**
 * Created by yanliang
 * on 2019/3/29
 */
public class EcWeChat {
    public static final String APP_ID = Setting.getConfiguration(ConfigKeys.WE_CHAT_APP_ID);
    private final IWXAPI WXAPI;
    private IWeChatSignInCallback mSignInCallback = null;

    private static final class Holder {
        private static final EcWeChat INSTANCE = new EcWeChat();
    }

    public static EcWeChat getInstance() {
        return Holder.INSTANCE;
    }

    private EcWeChat() {
        final Activity activity = Setting.getConfiguration(ConfigKeys.ACTIVITY);
        WXAPI = WXAPIFactory.createWXAPI(activity, APP_ID, true);
        WXAPI.registerApp(APP_ID);
    }

    public final IWXAPI getWXAPI() {
        return WXAPI;
    }

    public EcWeChat onSignSuccess(IWeChatSignInCallback callback) {
        this.mSignInCallback = callback;
        return this;
    }

    public IWeChatSignInCallback getSignInCallback() {
        return mSignInCallback;
    }

    public final void signIn() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "random_state";
        WXAPI.sendReq(req);
    }


}
