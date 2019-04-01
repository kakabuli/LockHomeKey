package com.kaadas.lock.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Create By lxj  on 2019/1/9
 * Describe
 */
public class ToastUtil  {

    private Toast mToast;
    private static ToastUtil mToastUtils;
    private Context context;

    private ToastUtil(Context context) {
        this.context = context;
        mToast = Toast.makeText(context.getApplicationContext(), null, Toast.LENGTH_SHORT);
    }

    public static void init(Context context){
        if (mToastUtils==null){
            mToastUtils = new ToastUtil(context);
        }
    }


    public static synchronized ToastUtil getInstance() {
        return mToastUtils;
    }

    public void showShort(String content){
        mToast.setText(content);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }


    public void showLong(String content){
        mToast.setText(content);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }


    public void showShort(int stringId){
        mToast.setText(context.getString(stringId));
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }


    public void showLong(int stringId){
        mToast.setText(context.getString(stringId));
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

}
