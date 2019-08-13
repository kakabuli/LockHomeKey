package com.kaadas.lock.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.cachefloder.ACache;
import com.kaadas.lock.utils.cachefloder.CacheFloder;
import com.kaadas.lock.utils.handPwdUtil.GestureContentView;
import com.kaadas.lock.utils.handPwdUtil.GestureDrawline;
import com.kaadas.lock.widget.BottomMenuDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalVerifyGesturePasswordActivity extends BaseAddToApplicationActivity {


    @BindView(R.id.text_tip)
    TextView mTextTip;

    @BindView(R.id.gesture_container)
    FrameLayout gestureContainer;
    @BindView(R.id.hand_more)
    TextView handMore;

    private GestureContentView mGestureContentView;
    //输入错误剩余次数
    private int residueCount = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_verify_hand_pwd);
        ButterKnife.bind(this);
        initView();
        Intent intent = getIntent();
        String source = intent.getStringExtra(KeyConstants.SOURCE);
        if ("WelcomeActivity".equals(source)) {
            mTextTip.setText(R.string.draw_gesture_password);
            handMore.setVisibility(View.VISIBLE);
        }else {
            handMore.setVisibility(View.GONE);
        }
    }

    private void initView() {
        String code = CacheFloder.readHandPassword(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "handPassword");
        if (code != null) {
            mGestureContentView = new GestureContentView(this, true, code, new GestureDrawline.GestureCallBack() {
                @Override
                public void onGestureCodeInput(String inputCode) {

                }

                @Override
                public void checkedSuccess() {
                    residueCount = 5;
                    finish();
                    Intent intent = new Intent(PersonalVerifyGesturePasswordActivity.this, MainActivity.class);
                    startActivity(intent);

                }

                @Override
                public void checkedFail() {
                    residueCount--;
                    if (residueCount > 0) {
                        mGestureContentView.clearDrawlineState(1300L);
                        String text = getResources().getString(R.string.resume_load) + residueCount + getResources().getString(R.string.second);
                        mTextTip.setText(Html
                                .fromHtml("<font color='#DB392B'>" + text + "</font>"));
                        // 左右移动动画
                        Animation shakeAnimation = AnimationUtils.loadAnimation(PersonalVerifyGesturePasswordActivity.this, R.anim.shake);
                        mTextTip.startAnimation(shakeAnimation);
                    } else {
                        //重新登录
                        //todo 清除数据，未做！
                        //1清除手势密码缓存的数据
                        if (MyApplication.getInstance().getMqttService() != null) {
                            MyApplication.getInstance().getMqttService().httpMqttDisconnect();
                        }
                        MyApplication.getInstance().tokenInvalid(false);
                        Intent intent = new Intent(PersonalVerifyGesturePasswordActivity.this, LoginActivity.class);
                        startActivity(intent);

                    }
                }
            });
        }
        if (mGestureContentView != null && gestureContainer != null) {
            mGestureContentView.setParentView(gestureContainer);
        }
    }

    private long lastClickBackTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastClickBackTime > 2000) {
            lastClickBackTime = System.currentTimeMillis();
            ToastUtil.getInstance().showLong(R.string.exit);
        } else {
            System.exit(0);
        }
    }


    private BottomMenuDialog.Builder dialogBuilder;
    private BottomMenuDialog bottomMenuDialog;

    //展示头像对话框
    private void showMoreDialog() {
        String fingerPwd = CacheFloder.readPhoneFingerPrint(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "fingerStatus");
        dialogBuilder = new BottomMenuDialog.Builder(this);
        //指纹解锁
        if (!TextUtils.isEmpty(fingerPwd)) {
            dialogBuilder.addMenu(R.string.finger_open, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    if (bottomMenuDialog != null) {
                        bottomMenuDialog.dismiss();
                    }
                    Intent loginIntent = new Intent(PersonalVerifyGesturePasswordActivity.this, PersonalVerifyFingerPrintActivity.class);
                    startActivity(loginIntent);
                }
            });
        }
        //密码登录
        dialogBuilder.addMenu(R.string.pwd_select, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (bottomMenuDialog != null) {
                    bottomMenuDialog.dismiss();
                }
                Intent loginIntent = new Intent(PersonalVerifyGesturePasswordActivity.this, LoginActivity.class);
                startActivity(loginIntent);

            }
        });
        //切换注册页面
        dialogBuilder.addMenu(R.string.select_register, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (bottomMenuDialog != null) {
                    bottomMenuDialog.dismiss();
                }
                Intent registerIntent = new Intent(PersonalVerifyGesturePasswordActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
        bottomMenuDialog = dialogBuilder.create();
        bottomMenuDialog.show();
    }

    @OnClick(R.id.hand_more)
    public void onClick() {
        showMoreDialog();
    }
}
