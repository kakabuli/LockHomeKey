package com.yun.software.kaadas.UI.web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.just.agentweb.AgentWeb;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.Utils.ConstTool;
import com.yun.software.kaadas.Utils.KeyBoardListener;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import cn.sharesdk.framework.Platform;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class EcWebActivity extends BaseActivity {
    private Bundle mBundle;
    protected AgentWeb mAgentWeb;
    @BindView(R2.id.llWebView)
    LinearLayout linparent;
    private Intent serviceConnection;
    String url;
    public static final String URL = "https://www.baidu.com";
    private String tilte;
    private String type;
    private String id;
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_webview;
    }


    @Override
    protected void initViewsAndEvents() {


        initData();
    }
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    public void initData() {
        if (mBundle != null) {
            url = mBundle.getString("url");
            if (StringUtil.isEmpty(url)) {
                url = URL;
            }
            tilte = mBundle.getString("title");
            if (!StringUtil.isEmpty(tilte)) {
                tvTitle.setText(tilte);
            }
            type = mBundle.getString("type");
            id = mBundle.getString("id");


        } else {
            url = URL;
        }
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(linparent, new LinearLayout.LayoutParams(-1, -1))
                //默认指示器
                .useDefaultIndicator(getResources().getColor(R.color.colorPrimary), 3)
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .setSecurityType(AgentWeb.SecurityType.DEFAULT_CHECK)
                //              .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他应用时，弹窗咨询用户是否前往其他应用
                .interceptUnkownUrl() //拦截找不到相关页面的Scheme
                .createAgentWeb()
                .ready()
                .go(url);
        if (mAgentWeb != null) {
            mAgentWeb.getWebCreator().getWebView().getSettings().setMediaPlaybackRequiresUserGesture(false);
            //            mAgentWeb.getWebCreator().getWebView().getSettings().set
            mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(this));
        }

//        KeyBoardListener.getInstance(this).init();

    }




    @Override
    protected void getBundleExtras(Bundle extras) {
        mBundle = extras;
    }

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            LogUtils.iTag("options", "打开的web地址-----》" + request.getUrl().toString());
            String url = request.getUrl().toString();
            LogUtils.iTag("拦截地址", "id" + url);
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            LogUtils.iTag("options", "打开的web地址-----》" + url);
            return super.shouldInterceptRequest(view, url);
        }

        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.i("Info", "BaseWebActivity onPageStarted");
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.loadUrl("javascript:try{autoplay();}catch(e){}");
        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        //当前 WebView 加载网页进度
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
        }
        //接收到标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("Info", "onResult:" + requestCode + " onResult:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAgentWeb.clearWebCache();
        mAgentWeb.getWebLifeCycle().onDestroy();
    }

    @Override
    public void onEventBus(EventCenter eventCenter) {
        super.onEventBus(eventCenter);

    }

    public void readGo(Class<?> clazz){
        readyGo(clazz);
    }

    public void readGo(Class<?> clazz,Bundle bundle){
        readyGo(clazz,bundle);
    }
}
