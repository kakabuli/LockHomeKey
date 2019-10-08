package com.yun.software.kaadas.Utils;

import android.animation.ValueAnimator;
import android.support.v4.widget.NestedScrollView;

public class AnimatorUtils {

    public static void setScrollViewValueAnimator(NestedScrollView scrollView){

        if (scrollView == null ){
            return;
        }
        int scrollY = scrollView.getScrollY();


        // 步骤1：设置动画属性的初始值 & 结束值
        ValueAnimator anim = ValueAnimator.ofInt(scrollY, 0);
        // ofInt（）作用有两个
        // 1. 创建动画实例
        // 2. 将传入的多个Int参数进行平滑过渡:此处传入0和1,表示将值从0平滑过渡到1
        // 如果传入了3个Int参数 a,b,c ,则是先从a平滑过渡到b,再从b平滑过渡到C，以此类推
        // ValueAnimator.ofInt()内置了整型估值器,直接采用默认的.不需要设置，即默认设置了如何从初始值 过渡到 结束值
        // 关于自定义插值器我将在下节进行讲解
        // 下面看看ofInt()的源码分析 ->>关注1

// 步骤2：设置动画的播放各种属性
        anim.setDuration(500);
        // 设置动画运行的时长

        anim.setStartDelay(200);
        // 设置动画延迟播放时间

        anim.setRepeatCount(0);
        // 设置动画重复播放次数 = 重放次数+1
        // 动画播放次数 = infinite时,动画无限重复

        anim.setRepeatMode(ValueAnimator.RESTART);
        // 设置重复播放动画模式
        // ValueAnimator.RESTART(默认):正序重放
        // ValueAnimator.REVERSE:倒序回放

// 步骤3：将改变的值手动赋值给对象的属性值：通过动画的更新监听器
        // 设置 值的更新监听器
        // 即：值每次改变、变化一次,该方法就会被调用一次
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                int currentValue = (Integer) animation.getAnimatedValue();
                // 获得改变后的值

                // 步骤4：将改变后的值赋给对象的属性值，下面会详细说明
                scrollView.scrollTo(0,currentValue);

                // 步骤5：刷新视图，即重新绘制，从而实现动画效果
                scrollView.requestLayout();


            }
        });

        anim.start();
        // 启动动画
    }



}
