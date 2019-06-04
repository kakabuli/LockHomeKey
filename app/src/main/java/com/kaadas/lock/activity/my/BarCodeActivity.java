package com.kaadas.lock.activity.my;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.ftp.GeTui;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BarCodeActivity extends BaseAddToApplicationActivity implements View.OnClickListener {

    String bar_url="";
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.bar_code_webview)
    WebView bar_code_webview;
    @BindView(R.id.iv_back)
    ImageView ivBack;//返回

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code);
        bar_url=getIntent().getStringExtra(KeyConstants.BAR_CODE);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        if(TextUtils.isEmpty(bar_url)){
            ToastUtil.getInstance().showShort(getString(R.string.bar_code_scan_qr_failed));
            finish();
        }
        Log.e(GeTui.VideoLog," finall->result:"+bar_url);

        tv_content.setText(getString(R.string.production_activation));
        bar_code_webview.getSettings().setJavaScriptEnabled(true);
        bar_code_webview.requestFocus();//触摸焦点起作用mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);//取消滚动条
        bar_code_webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置允许js弹出alert对话框
        bar_code_webview.loadUrl(bar_url);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back :
                finish();
                break;
        }
    }
}
