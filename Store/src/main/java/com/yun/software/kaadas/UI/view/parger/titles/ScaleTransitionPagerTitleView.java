package com.yun.software.kaadas.UI.view.parger.titles;

import android.content.Context;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

/**
 * Created by yanliang
 * on 2019/6/24
 */
public class ScaleTransitionPagerTitleView extends ColorTransitionPagerTitleView {
    private float mMinScale = 0.75f;

    public ScaleTransitionPagerTitleView(Context context) {
        super(context);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        super.onEnter(index, totalCount, enterPercent, leftToRight);    // 实现颜色渐变
//        setScaleX(mMinScale + (1.0f - mMinScale) * enterPercent);
//        setScaleY(mMinScale + (1.0f - mMinScale) * enterPercent);
        setTextSize(16);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        super.onLeave(index, totalCount, leavePercent, leftToRight);    // 实现颜色渐变
//        setScaleX(1.0f + (mMinScale - 1.0f) * leavePercent);
//        setScaleY(1.0f + (mMinScale - 1.0f) * leavePercent);
       setTextSize(16);

    }

    public float getMinScale() {
        return mMinScale;
    }

    public void setMinScale(float minScale) {
        mMinScale = minScale;
    }
}
