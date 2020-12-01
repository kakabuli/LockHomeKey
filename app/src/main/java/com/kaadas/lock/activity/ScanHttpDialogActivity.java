package com.kaadas.lock.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ftp.Result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScanHttpDialogActivity extends BaseAddToApplicationActivity {

    private WebView webView;
    private String url;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_http_dialog);

        url = getIntent().getStringExtra(KeyConstants.QR_URL);

        webView = findViewById(R.id.wv_webview);

        initWebView();

        //设置窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.getDecorView().setScaleX((float) 0.96);
        win.getDecorView().setScaleY((float) 0.96);

        WindowManager.LayoutParams lp = win.getAttributes();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = (int) (width * 0.9);
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;//设置对话框置顶显示
        win.setAttributes(lp);
        //设置点击外部空白处可以关闭Activity
        this.setFinishOnTouchOutside(true);

        LoadingDialog loadingDialog = LoadingDialog.getInstance(this);
        loadingDialog.show("加载中...");

    }

    private void initWebView() {
        webView = findViewById(R.id.wv_webview);
        webView.loadUrl(url);
        handler.postDelayed(runnable, 10 * 1000);
        //打开交互权限
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.addJavascriptInterface(new ScanHttpDialogActivity.InJavaScriptLocalObj(), "java_obj");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //使用WebView加载显示url
                view.loadUrl(url);
                //返回true
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:window.java_obj.getSource('<head>'+" +
                        "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                onError();
            }

            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                onError();
            }

            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                onError();
            }

        });
    }

    private void onError() {
        setResult(1);
        finish();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            onError();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private boolean isFirst = true;

    //自己定义的类
    public final class InJavaScriptLocalObj {
        //一定也要加上这个注解,否则没有用
        @JavascriptInterface
        public void getSource(String html) {
            //取出HTML中P标签的文本内容,利用正则表达式匹配.
            Pattern pattern = Pattern.compile("<p.*?>(.*?)</p>");
            Matcher matcher = pattern.matcher(html);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                sb.append(matcher.group(1));
            }
            if (isFirst && TextUtils.isEmpty(sb.toString())) {
                isFirst = false;
                return;
            }
            LogUtils.e("解析的数据是   " + sb.toString());
            Intent intent = new Intent();
            intent.putExtra(KeyConstants.URL_RESULT, sb.toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
