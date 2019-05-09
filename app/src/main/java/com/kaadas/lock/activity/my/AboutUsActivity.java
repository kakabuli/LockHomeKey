package com.kaadas.lock.activity.my;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.utils.SharedUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/9
 */
public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_customer_service_phone:
                SharedUtil.getInstance().callPhone(this,"400-11-66667");
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
