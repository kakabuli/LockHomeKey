package com.yun.software.kaadas.UI.gallery;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * on 2017/5/23.
 * 类的描述:
 */

public class GalleryViewPager extends ViewPager {
    //默认距离
    private final static float DISTANCE = 10;
    private float downX;
    private float downY;

    public GalleryViewPager(Context context) {
        super(context);
    }

    public GalleryViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            downX = ev.getX();
            downY = ev.getY();
        }else if (ev.getAction() == MotionEvent.ACTION_UP) {

            float upX = ev.getX();
            float upY = ev.getY();
            //如果 up的位置和down 的位置 距离 > 设置的距离,则事件继续传递,不执行下面的点击切换事件
            if(Math.abs(upX - downX) > DISTANCE || Math.abs(upY - downY) > DISTANCE){
                return super.dispatchTouchEvent(ev);
            }

//            View view = viewOfClickOnScreen(ev);
//            if (view != null) {
//                int index = (Integer) view.getTag();
//                if (getCurrentItem() != index) {
//                    setCurrentItem(index);
//                }
//            }
        }
        return super.dispatchTouchEvent(ev);
    }


}
