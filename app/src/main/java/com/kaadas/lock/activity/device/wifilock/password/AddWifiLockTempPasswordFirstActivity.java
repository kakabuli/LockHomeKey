package com.kaadas.lock.activity.device.wifilock.password;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddWifiLockTempPasswordFirstActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.et_admin_password)
    EditText etAdminPassword;
    @BindView(R.id.tv_next)
    TextView tvNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_temp_password_first);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.tv_next})
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
                if (StringUtil.checkSimplePassword(adminPassword)) {
                    AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint), getString(R.string.password_simple_please_reset), getString(R.string.go_on), getString(R.string.reinstall), new AlertDialogUtil.ClickListener() {

                        @Override
                        public void left() {

                        }

                        @Override
                        public void right() {
                            etAdminPassword.setText("");
                            return;
                        }
                    });
                    return;
                }

                Intent intent = new Intent(AddWifiLockTempPasswordFirstActivity.this,AddWifiLockTempPasswordSecondActivity.class);
                intent.putExtra(Constants.ADMIN_PASSWORD, adminPassword);
                startActivity(intent);

                break;
        }
    }
}
