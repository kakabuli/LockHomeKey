package com.kaadas.lock.activity.my;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kaadas.lock.BuildConfig;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.SharedUtil;
import com.weijiaxing.logviewer.LogcatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/9
 */
public class AboutUsActivity extends BaseAddToApplicationActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rl_customer_service_phone)
    RelativeLayout rlCustomerServicePhone;
    @BindView(R.id.rl_zhao_shang_phone)
    RelativeLayout rlZhaoShangPhone;
    @BindView(R.id.rl_enterprise_official_website)
    RelativeLayout rlEnterpriseOfficialWebsite;

    @BindView(R.id.scrollview)
    ScrollView scrollView;

    private long time = 0;

    private boolean isOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        rlCustomerServicePhone.setOnClickListener(this);
        rlZhaoShangPhone.setOnClickListener(this);
        rlEnterpriseOfficialWebsite.setOnClickListener(this);
        tvContent.setText(getString(R.string.about_us));

        if(Boolean.parseBoolean(BuildConfig.LOGCAT_SHOW))
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int actionMasked = event.getActionMasked();
                int pointerCount = event.getPointerCount();

                if (pointerCount == 3) {
                    switch (actionMasked) {
                        case MotionEvent.ACTION_POINTER_DOWN:
                            time = System.currentTimeMillis();
                            break;
                        default:
                            if(System.currentTimeMillis() - time > 5000 && !isOpen){
                                //Use this method
                                LogcatActivity.launch(AboutUsActivity.this);
                                isOpen = true;
                            }
                            break;
                    }
                }

                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_customer_service_phone:
                SharedUtil.getInstance().callPhone(this,"400-800-5919");
                break;
            case R.id.rl_zhao_shang_phone:
                SharedUtil.getInstance().callPhone(this,"400-800-3756");
                break;
            case R.id.rl_enterprise_official_website:
                SharedUtil.getInstance().jumpWebsite(this,"http://www.kaadas.com");
                break;
        }
    }
}
