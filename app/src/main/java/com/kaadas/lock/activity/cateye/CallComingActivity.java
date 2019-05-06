package com.kaadas.lock.activity.cateye;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CallComingActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.call_coming_refuse_ll)
    LinearLayout call_coming_refuse_ll;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    @BindView(R.id.call_coming_title)
    TextView callComingTitle;
    @BindView(R.id.call_coming_img)
    ImageView callComingImg;
    @BindView(R.id.call_coming_center_view)
    View callComingCenterView;
    @BindView(R.id.tv_cat_device_power)
    ImageView tvCatDevicePower;
    @BindView(R.id.iv_accept_call)
    ImageView ivAcceptCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_coming);
        ButterKnife.bind(this);
        call_coming_refuse_ll.setOnClickListener(this);
        ivAcceptCall.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 当状态栏设置为透明的时候,View渲染到状态栏
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //   getWindow().setStatusBarColor(getResources().getColor(R.color.red));
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


    }

    @Override
    public void onClick(View view) {
        Intent intent ;
        switch (view.getId()) {
            case R.id.call_coming_refuse_ll:
                intent = new Intent();
                intent.putExtra(KeyConstants.IS_ACCEPT_CALL, false);
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.iv_accept_call:
                intent = new Intent();
                intent.putExtra(KeyConstants.IS_ACCEPT_CALL, true);
                setResult(RESULT_OK,intent);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }


}
