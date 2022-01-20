package com.kaadas.lock.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 注销账号
 */
public class AccountLogoutActivity extends BaseAddToApplicationActivity {

    private Disposable timerDisposable;
    private Button btnCloseAccount;
    public static final int FINISH_CODE = 10001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_logout);
        TextView textView = findViewById(R.id.tv_content);
        textView.setText(getString(R.string.account_logout_apply));
        final String continueStr = getString(R.string.account_logout_continue);
        btnCloseAccount = findViewById(R.id.btn_closeAccount);
        btnCloseAccount.setText(continueStr+"(7s)");

        timerDisposable = Observable.intervalRange(1,7, 1, 1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        long time = 7 - aLong;
                        if(time <= 0) {
                            btnCloseAccount.setText(continueStr);
                            btnCloseAccount.setTextColor(getColor(R.color.white));
                            btnCloseAccount.setEnabled(true);
                        }else{
                            btnCloseAccount.setText(continueStr+"("+time+"s)");
                        }
                    }
                });

        btnCloseAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountLogoutActivity.this, AccountLogoutGetCodeActivity.class);
                startActivityForResult(intent, FINISH_CODE);
            }
        });
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(FINISH_CODE == requestCode && resultCode == RESULT_OK){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!timerDisposable.isDisposed()){
            timerDisposable.dispose();
        }
    }
}
