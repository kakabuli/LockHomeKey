package com.kaadas.lock.activity.device.wifilock.add;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockInputAdminPasswordActivity extends BaseAddToApplicationActivity {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.et_admin_password)
    EditText etAdminPassword;
    @BindView(R.id.tv_next)
    TextView tvNext;
    private String wifiBssid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_ap_input_admin_password);
        ButterKnife.bind(this);
        wifiBssid = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_WIFI_SSID);
        etAdminPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!TextUtils.isEmpty(text) && text.length() >= 6) {
                    tvNext.setBackgroundResource(R.drawable.go_and_buy);
                    tvNext.setClickable(true);
                } else {
                    tvNext.setBackgroundResource(R.drawable.button_bg_bfbfbf_);
                    tvNext.setClickable(false);
                }
            }
        });
    }


    @OnClick({R.id.back, R.id.help, R.id.et_admin_password, R.id.tv_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_next:
                String adminPassword = etAdminPassword.getText().toString().trim();
                if (!StringUtil.randomJudge(adminPassword)) {
                    ToastUtil.getInstance().showShort(R.string.random_verify_error);
                    return;
                }
                Intent intent = new Intent(WifiLockInputAdminPasswordActivity.this, WifiLockCheckAdminPasswordActivity.class);
                intent.putExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD, adminPassword);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_SSID, wifiBssid);
                startActivity(intent);
                break;
        }
    }

    @OnClick(R.id.help)
    public void onClick() {
        startActivity(new Intent(this,WifiLockHelpActivity.class));
    }
}
