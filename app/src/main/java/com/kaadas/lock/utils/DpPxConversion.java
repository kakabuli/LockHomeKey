package com.kaadas.lock.utils;

import android.content.Context;


/**
 * Created by David
 */
public class DpPxConversion {
    private static DpPxConversion dpPxConversion = null;

    public static DpPxConversion getInstance()
    {
        if (dpPxConversion == null) {
            dpPxConversion = new DpPxConversion();
        }
        return dpPxConversion;
    }
    public  int px2dp(Context context, float pxValue) {
        final float scale =  context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * convert dp to its equivalent px
     *
     * 将dp转换为与之相等的px
     */
    public  int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
