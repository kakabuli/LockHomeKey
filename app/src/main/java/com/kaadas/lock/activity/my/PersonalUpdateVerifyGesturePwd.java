package com.kaadas.lock.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalUpdateVerifyGesturePwd extends AppCompatActivity {
    @BindView(R.id.text_tip)
    TextView mTextTip;

    @BindView(R.id.gesture_container)
    FrameLayout gestureContainer;

    @BindView(R.id.gesture_pwd_back)
    ImageView gesturePwdBack;

    private GestureContentView mGestureContentView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_verify_hand_pwd);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        String code = CacheFloder.readHandPassword(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "handPassword");
        if (code != null) {
            mTextTip.setText(getResources().getString(R.string.hand_pwd_drraw_the_old_ailure));
            mGestureContentView = new GestureContentView(this, true, code, new GestureDrawline.GestureCallBack() {
                @Override
                public void onGestureCodeInput(String inputCode) {

                }

                @Override
                public void checkedSuccess() {
                    finish();
                    Intent intent = new Intent(PersonalUpdateVerifyGesturePwd.this, PersonalUpdateGesturePwdActivity.class);
                    startActivity(intent);
                }

                @Override
                public void checkedFail() {
                    mGestureContentView.clearDrawlineState(1300L);
                    String text = getResources().getString(R.string.hand_pwd_drraw_the_ailure);
                    mTextTip.setText(Html
                            .fromHtml("<font color='#DB392B'>" + text + "</font>"));
                    // 左右移动动画
                    Animation shakeAnimation = AnimationUtils.loadAnimation(PersonalUpdateVerifyGesturePwd.this, R.anim.shake);
                    mTextTip.startAnimation(shakeAnimation);
                }
            });
        }
        mGestureContentView.setParentView(gestureContainer);

    }

    @OnClick(R.id.gesture_pwd_back)
    public void onViewClicked() {
        finish();
    }
}
