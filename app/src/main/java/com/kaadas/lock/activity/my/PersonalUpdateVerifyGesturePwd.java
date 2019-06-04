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
import com.kaadas.lock.activity.login.LoginActivity;
import com.kaadas.lock.activity.login.PersonalVerifyGesturePasswordActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.cachefloder.ACache;
import com.kaadas.lock.utils.cachefloder.CacheFloder;
import com.kaadas.lock.utils.handPwdUtil.GestureContentView;
import com.kaadas.lock.utils.handPwdUtil.GestureDrawline;
import com.lzy.imagepicker.ImagePicker;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalUpdateVerifyGesturePwd extends BaseAddToApplicationActivity {
    @BindView(R.id.text_tip)
    TextView mTextTip;

    @BindView(R.id.gesture_container)
    FrameLayout gestureContainer;

    @BindView(R.id.gesture_pwd_back)
    ImageView gesturePwdBack;

    private GestureContentView mGestureContentView;
    //输入错误剩余次数
    private int residueCount = 5;
    String source;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_verify_hand_pwd);
        ButterKnife.bind(this);
        initView();
        Intent intent = getIntent();
         source = intent.getStringExtra(KeyConstants.SOURCE);

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
                    if ("PersonalSecuritySettingActivity".equals(source)){
                        Intent intent = new Intent();
                        //把返回数据存入Intent
                        //设置返回数据
                        setResult(RESULT_OK, intent);
                        finish();
                    }else {
                        finish();
                        Intent intent = new Intent(PersonalUpdateVerifyGesturePwd.this, PersonalUpdateGesturePwdActivity.class);
                        startActivity(intent);
                    }

                }

                @Override
                public void checkedFail() {
     /*               mGestureContentView.clearDrawlineState(1300L);
                    String text = getResources().getString(R.string.hand_pwd_drraw_the_ailure);
                    mTextTip.setText(Html
                            .fromHtml("<font color='#DB392B'>" + text + "</font>"));
                    // 左右移动动画
                    Animation shakeAnimation = AnimationUtils.loadAnimation(PersonalUpdateVerifyGesturePwd.this, R.anim.shake);
                    mTextTip.startAnimation(shakeAnimation);*/

                    residueCount--;
                    if (residueCount > 0) {
                        mGestureContentView.clearDrawlineState(1300L);
                        String text = getResources().getString(R.string.resume_load) + residueCount + getResources().getString(R.string.second);
                        mTextTip.setText(Html
                                .fromHtml("<font color='#DB392B'>" + text + "</font>"));
                        // 左右移动动画
                        Animation shakeAnimation = AnimationUtils.loadAnimation(PersonalUpdateVerifyGesturePwd.this, R.anim.shake);
                        mTextTip.startAnimation(shakeAnimation);
                    } else {
                        //重新登录
                        //todo 清除数据，未做！
                        //1清除手势密码缓存的数据
                        if (MyApplication.getInstance().getMqttService()!=null){
                            MyApplication.getInstance().getMqttService().httpMqttDisconnect();
                        }
                        MyApplication.getInstance().tokenInvalid(false);
                        Intent intent = new Intent(PersonalUpdateVerifyGesturePwd.this, LoginActivity.class);
                        startActivity(intent);

                    }
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
