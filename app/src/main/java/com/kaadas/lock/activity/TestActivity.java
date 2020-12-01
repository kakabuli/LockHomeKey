package com.kaadas.lock.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.utils.LogUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

public class TestActivity extends BaseAddToApplicationActivity {

    private TextView tv_content;
    private WebView webView;
    private int time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        tv_content = findViewById(R.id.tv_content);

        webView = findViewById(R.id.wv_test);
        webView.loadUrl("https://active.clewm.net/EYopdB");
        //打开交互权限
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //使用WebView加载显示url
                view.loadUrl(url);
                //返回true
                time++;
                LogUtils.d(time+"  次请求22  " +  url);
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
                LogUtils.d(time+"  次请求33 " +  view.getResources());
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }

            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            }

            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {

            }

        });
    }
    //自己定义的类
    public final class InJavaScriptLocalObj {
        //一定也要加上这个注解,否则没有用
        @JavascriptInterface
        public void getSource(String html) {
            //取出HTML中P标签的文本内容,利用正则表达式匹配.

            Pattern pattern=Pattern.compile("<p.*?>(.*?)</p>");
            Matcher matcher = pattern.matcher(html);
            StringBuffer sb=new StringBuffer();
            while (matcher.find())
            {
                sb.append(matcher.group(1));
            }
        }
    }
}
