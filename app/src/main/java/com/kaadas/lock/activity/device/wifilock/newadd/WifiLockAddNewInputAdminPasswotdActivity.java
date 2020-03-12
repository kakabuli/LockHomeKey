package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockApCheckAdminPasswordActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockApInputAdminPasswordActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SocketManager;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddNewInputAdminPasswotdActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.head)
    TextView head;
    @BindView(R.id.ap_password_edit)
    EditText apPasswordEdit;
    @BindView(R.id.button_next)
    TextView buttonNext;
    int times = 1;
    byte[] data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_input_admin_passwotd);
        ButterKnife.bind(this);

        data =  getIntent().getByteArrayExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_DATA);
        times =  getIntent().getIntExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_TIMES,1);
    }

    @OnClick({R.id.back, R.id.help, R.id.button_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                showWarring();
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;
            case R.id.button_next: //输入管理员密码  下一步
                String adminPassword = apPasswordEdit.getText().toString().trim();
                if (!StringUtil.randomJudge(adminPassword)) {
                    ToastUtil.getInstance().showShort(R.string.random_verify_error);
                    return;
                }


                LogUtils.e(getLocalClassName()+"次数是   " + times + "  data 是否为空 " + (data == null));
                Intent intent = new Intent(this, WifiLockAddNewCheckAdminPasswordActivity.class);
                intent.putExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD, adminPassword);
                intent.putExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_TIMES, times);
                intent.putExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_DATA, data);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        showWarring();
    }

    private void showWarring(){
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                 this
                , "确定重新开始配网吗？",
                "取消", "确定", "#A4A4A4", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        //退出当前界面
                        Intent intent = new Intent(WifiLockAddNewInputAdminPasswotdActivity.this, WifiLockAddNewFirstActivity.class);
                        intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, times);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}
