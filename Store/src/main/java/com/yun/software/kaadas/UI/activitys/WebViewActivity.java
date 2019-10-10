package com.yun.software.kaadas.UI.activitys;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import la.xiong.androidquick.tool.LogUtils;

public class WebViewActivity extends BaseActivity {

    @BindView(R2.id.wv_mmd)
    WebView wvMmd;
    @BindView(R2.id.game_pro)
    ProgressBar gamePro;

    String web_title, web_url;
    @BindView(R2.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R2.id.tv_titlt)
    TextView tvTitlt;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void initViewsAndEvents() {
        if (null != getIntent()) {
            web_title = getIntent().getStringExtra("web_title");
            web_url = getIntent().getStringExtra("web_url");
        }

        initData();
    }

    private void initData() {
        //WebView属性设置！！！
        WebSettings settings = wvMmd.getSettings();
        settings.setTextZoom(100);
        settings.setJavaScriptEnabled(true);
//        wvMmd.addJavascriptInterface(new Htlm5WithAndroid(this), "html5WithAndroid");
        settings.setAllowFileAccess(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        wvMmd.setWebViewClient(new WebViewClient() {
            //是否在webview内加载页面
            @SuppressLint("NewApi")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtils.e("网页返回的数据" + url);
                wvMmd.evaluateJavascript("", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        LogUtils.e("网页返回的数据" + value);
                    }
                });
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                gamePro.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                String title = view.getTitle();
//                if (!TextUtils.isEmpty(title)) {
//                    setTvTitle(title);
////                    titleView.setCenterText(title);
//                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });

        wvMmd.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                gamePro.setProgress(newProgress);
                if (newProgress == 100) {
                    gamePro.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                getWebTitle();
            }
        });
        if (null != web_url) {
            wvMmd.loadUrl(web_url);
        }
    }

    private void getWebTitle() {
        WebBackForwardList forwardList = wvMmd.copyBackForwardList();
        WebHistoryItem item = forwardList.getCurrentItem();
        if (null != web_title) {
            tvTitlt.setText(web_title);
        } else {
            if (item != null) {
//            setActionBarTitle(item.getTitle());
                tvTitlt.setText(item.getTitle());
            }
        }

    }

    private void onWebViewGoBack() {
        wvMmd.goBack();
        getWebTitle();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (wvMmd.canGoBack()) {
            onWebViewGoBack();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        wvMmd.reload();
    }

    @Override
    protected void onDestroy() {
        if (wvMmd != null) {
            wvMmd.removeView(gamePro);
            // 清理缓存
            wvMmd.stopLoading();
            wvMmd.onPause();
            wvMmd.clearHistory();
            wvMmd.clearCache(true);
            wvMmd.clearFormData();
            wvMmd.clearSslPreferences();
            WebStorage.getInstance().deleteAllData();
            wvMmd.destroyDrawingCache();
            wvMmd.removeAllViews();
            wvMmd.destroy();
            wvMmd = null;
        }
        super.onDestroy();
    }

    @OnClick({R2.id.rl_back})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.rl_back) {
            finish();

        }
    }
}
