package com.kaadas.lock.utils;

import android.animation.ObjectAnimator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * Created by David on 2019/3/11
 */
public class AnimatorUtil {
    private static AnimatorUtil animatorUtil = null;

    public static AnimatorUtil getInstance() {
        if (animatorUtil == null) {
            animatorUtil = new AnimatorUtil();
        }
        return animatorUtil;
    }

    public ObjectAnimator startRotationAnimation(ImageView iv) {
        ObjectAnimator mAnimator = ObjectAnimator.ofFloat(iv, "rotation", 0.0f, 360.0f);
        mAnimator.setDuration(2000);//设定转一圈的时间
        mAnimator.setRepeatCount(Animation.INFINITE);//设定无限循环
        mAnimator.setRepeatMode(ObjectAnimator.RESTART);// 循环模式
        mAnimator.setInterpolator(new LinearInterpolator());// 匀速
        mAnimator.start();
        return mAnimator;
    }
}
