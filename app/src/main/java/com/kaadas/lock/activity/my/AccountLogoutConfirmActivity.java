package com.kaadas.lock.activity.my;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.login.LoginActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.AccountLogoutPresenter;
import com.kaadas.lock.mvp.view.IAccountLogoutView;
import com.kaadas.lock.utils.AlertDialogUtil;
import java.util.Calendar;
import java.util.Date;

/**
 * 注销账号-确认
 */
public class AccountLogoutConfirmActivity extends BaseActivity<IAccountLogoutView, AccountLogoutPresenter<IAccountLogoutView>> implements IAccountLogoutView{

    private String account;
    private int accountType;
    private String areaCode;
    private String code;
    private int day;
    private int month;
    private int year;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_logout_confirm);

        account = getIntent().getStringExtra("account");
        accountType = getIntent().getIntExtra("accountType", 1);
        code = getIntent().getStringExtra("code");
        areaCode = getIntent().getStringExtra("areaCode");
        if(areaCode == null){
            areaCode = "";
        }

        initDate();

        TextView tvCancelLogoutTime = findViewById(R.id.tvCancelLogoutTime);
        String string = getStringWithDate(R.string.account_logout_protocol_2);
        tvCancelLogoutTime.setText(string);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountLogoutConfirmActivity.this.setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void showConfirmDialog() {
        if(TextUtils.isEmpty(account) || TextUtils.isEmpty(code)){
            Toast.makeText(MyApplication.getInstance(), "参数缺失", Toast.LENGTH_SHORT).show();
            return;
        }

        String alertStr = getStringWithDate(R.string.account_logout_confirm_dialog_centent);
        AlertDialogUtil.getInstance().showAccountLogoutDialog(this, getString(R.string.account_logout_confirm_dialog_confirm), alertStr, getString(R.string.cancel), getString(R.string.account_logout_confirm_dialog_confirm), new AlertDialogUtil.OnDialogClickListener() {
            @Override
            public void left() {

            }

            @Override
            public void right() {
                showLoading(getString(R.string.loading));
                mPresenter.accountLogout(areaCode, account, accountType, code);
            }
        });

    }

    private String getStringWithDate(int resId) {
        String string = getString(resId);
        return String.format(string, year, month, day);
    }

    private void initDate() {
        //延后7天
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 7);

        day = calendar.get(Calendar.DATE);
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
    }

    @Override
    protected AccountLogoutPresenter<IAccountLogoutView> createPresent() {
        return new AccountLogoutPresenter<>();
    }

    @Override
    public void onRequestCodeResult(int status, String msg) {

    }

    @Override
    public void onAccountLogoutSuccess() {
        hiddenLoading();
        if (MyApplication.getInstance().getMqttService()!=null){
            MyApplication.getInstance().getMqttService().httpMqttDisconnect();
        }
        MyApplication.getInstance().tokenInvalid(false);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onAccountLogoutError(String msg) {
        hiddenLoading();
        if(!TextUtils.isEmpty(msg)){
            Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
        }
    }

}
