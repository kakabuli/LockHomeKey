package com.kaadas.lock.activity.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.utils.cachefloder.ACache;
import com.kaadas.lock.utils.cachefloder.CacheFloder;
import com.kaadas.lock.utils.handPwdUtil.GestureContentView;
import com.kaadas.lock.utils.handPwdUtil.GestureDrawline;
import com.kaadas.lock.utils.handPwdUtil.LockIndicator;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalUpdateGesturePwdActivity extends AppCompatActivity {

    @BindView(R.id.gesture_pwd_back)
    ImageView gesturePwdBack;
    @BindView(R.id.lock_indicator)
    LockIndicator lockIndicator;
    @BindView(R.id.text_tip)
    TextView mTextTip;
    @BindView(R.id.gesture_container)
    FrameLayout gestureContainer;

    private GestureContentView mGestureContentView;
    private boolean mIsFirstInput = true;
    private String mFirstPassword = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_update_hand_pwd);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mGestureContentView = new GestureContentView(this, false, "", new GestureDrawline.GestureCallBack() {
            @Override
            public void onGestureCodeInput(String inputCode) {
                if (!isInputPassValidate(inputCode)) {
                    mTextTip.setText(getResources().getString(R.string.least_four_again_input));
                    mGestureContentView.clearDrawlineState(0L);
                    return;
                }
                if (mIsFirstInput) {
                    mFirstPassword = inputCode;
                    updateCodeList(inputCode);
                    mTextTip.setText(getResources().getString(R.string.set_again_gesture_pattern));
                    mGestureContentView.clearDrawlineState(0L);
                } else {
                    if (inputCode.equals(mFirstPassword)) {
                        mGestureContentView.clearDrawlineState(0L);
                        //缓存手势密码成功的值
                        String code = CacheFloder.readHandPassword(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "handPassword");
                        if (code != null) {
                            ACache.get(MyApplication.getInstance()).remove(MyApplication.getInstance().getUid() + "handPassword");
                        }
                        CacheFloder.writeHandPassword(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "handPassword", inputCode);
                        PersonalUpdateGesturePwdActivity.this.finish();
                    } else {
                        mTextTip.setText(getResources().getString(R.string.repaint_gesture_pattern));
                        // 左右移动动画
                        Animation shakeAnimation = AnimationUtils.loadAnimation(PersonalUpdateGesturePwdActivity.this, R.anim.shake);
                        mTextTip.startAnimation(shakeAnimation);
                        // 保持绘制的线，1.5秒后清除
                        mGestureContentView.clearDrawlineState(1300L);
                    }
                }
                mIsFirstInput = false;
            }

            @Override
            public void checkedSuccess() {

            }

            @Override
            public void checkedFail() {


            }
        });
        // 设置手势解锁显示到哪个布局里面
        mGestureContentView.setParentView(gestureContainer);
        updateCodeList("");

    }

    private boolean isInputPassValidate(String inputPassword) {
        if (TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4) {
            return false;
        }
        return true;
    }

    private void updateCodeList(String inputCode) {
        // 更新选择的图案
        lockIndicator.setPath(inputCode);
    }

    @OnClick(R.id.gesture_pwd_back)
    public void onViewClicked() {
        finish();
    }
}
