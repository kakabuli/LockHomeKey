package com.kaadas.lock.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import la.xiong.androidquick.tool.Utils;

/**
 * Create By David
 * Describe
 */
public class ToastUtil {

    private static Toast mToast;
    private static ToastUtil mToastUtils;
    private Context context;
    private static final Handler HANDLER       = new Handler(Looper.getMainLooper());

    private ToastUtil(Context context) {
        this.context = context;
        mToast = Toast.makeText(context, null, Toast.LENGTH_LONG);
    }

    public static void init(Context context) {
        if (mToastUtils == null) {
            mToastUtils = new ToastUtil(context);
        }
    }

    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    public static synchronized ToastUtil getInstance() {
        return mToastUtils;
    }

    public void showShort(String content) {
        HANDLER.post(new Runnable() {
            @SuppressLint("ShowToast")
            @Override
            public void run() {
                cancel();
                mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                mToast.setText(content);
                mToast.setDuration(Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }


    public void showLong(String content) {
        HANDLER.post(new Runnable() {
            @SuppressLint("ShowToast")
            @Override
            public void run() {
                cancel();
                mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                mToast.setText(content);
                mToast.setDuration(Toast.LENGTH_LONG);
                mToast.show();
            }
        });
    }


    public void showShort(int stringId) {
        HANDLER.post(new Runnable() {
            @SuppressLint("ShowToast")
            @Override
            public void run() {
                cancel();
                mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                mToast.setText(context.getString(stringId));
                mToast.setDuration(Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }


    public void showLong(int stringId) {
        HANDLER.post(new Runnable() {
            @SuppressLint("ShowToast")
            @Override
            public void run() {
                cancel();
                mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                mToast.setText(context.getString(stringId));
                mToast.setDuration(Toast.LENGTH_LONG);
                mToast.show();
            }
        });
    }

}
