package com.kaadas.lock.activity.device.wifilock.password;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SharedUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockPasswordShareActivity extends AppCompatActivity {



    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.tv_notice)
    TextView tvNotice;
    @BindView(R.id.tv_password)
    TextView tvPassword;
    @BindView(R.id.tv_time)
    TextView tvTime;

    private Intent intent;
    private int timeCeLue;
    private String password;
    private int type;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_password_share);
        ButterKnife.bind(this);
        intent = getIntent();
        type = intent.getIntExtra(KeyConstants.TO_DETAIL_TYPE, 1);
        number = intent.getStringExtra(KeyConstants.TO_DETAIL_NUMBER);
        password = intent.getStringExtra(KeyConstants.TO_DETAIL_PASSWORD);
        timeCeLue = intent.getIntExtra(KeyConstants.TIME_CE_LUE, 0);
        String pwd = "";
        for (char c : password.toCharArray()) {
            pwd += " " + c;
        }
        tvPassword.setText(pwd);
        headTitle.setText(getString(R.string.password_detail));
        tvTime.setText(" " + DateUtils.getStrFromMillisecond2(System.currentTimeMillis()));
        initData();
    }

    private void initData() {
        switch (timeCeLue) {
            case KeyConstants.YONG_JIU:
                tvNotice.setText(R.string.password_yong_jiu_valid);
                break;
            case KeyConstants.ONE_DAY:
                tvNotice.setText(R.string.password_one_day_valid);
                break;
            case KeyConstants.CUSTOM:
                long startTime = intent.getLongExtra(KeyConstants.CUSTOM_START_TIME, 0);
                long endTime = intent.getLongExtra(KeyConstants.CUSTOM_END_TIME, 0);
                String strStart = DateUtils.formatDetailTime(startTime);
                String strEnd = DateUtils.formatDetailTime(endTime);
                String content = getString(R.string.password_valid_shi_xiao) + "  " + strStart + "~" + strEnd;
                tvNotice.setText(content);
                break;
            case KeyConstants.PERIOD:
                strStart = intent.getStringExtra(KeyConstants.PERIOD_START_TIME);
                strEnd = intent.getStringExtra(KeyConstants.PERIOD_END_TIME);
                String weekRules = intent.getStringExtra(KeyConstants.WEEK_REPEAT_DATA);
                String strHint = String.format(getString(R.string.week_hint), weekRules,
                        strStart, strEnd);
                tvNotice.setText(strHint);
                break;
            case KeyConstants.TEMP:
                tvNotice.setText(R.string.password_once_valid);
                break;
        }
    }


    @OnClick({R.id.iv_back, R.id.tv_content, R.id.iv_right, R.id.tv_number, R.id.tv_password, R.id.btn_delete, R.id.tv_name, R.id.iv_editor, R.id.tv_time, R.id.tv_short_message, R.id.tv_wei_xin, R.id.tv_copy})
    public void onClick(View view) {
        String message = String.format(getString(R.string.share_content), password, tvNotice.getText().toString().trim());
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_short_message:
                SharedUtil.getInstance().sendShortMessage(message, this);
                break;
            case R.id.tv_wei_xin:
                if (SharedUtil.isWeixinAvilible(this)) {
                    SharedUtil.getInstance().sendWeiXin(message);
                } else {
                    ToastUtil.getInstance().showShort(R.string.telephone_not_install_wechat);
                }
                break;
            case R.id.tv_copy:
                SharedUtil.getInstance().copyTextToSystem(this, message);
                break;
        }
    }
}
