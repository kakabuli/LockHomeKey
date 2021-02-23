package com.yun.software.kaadas.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.yun.software.kaadas.Http.ApiConstants;

import org.greenrobot.eventbus.EventBus;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import la.xiong.androidquick.tool.ExceptionUtil;
import la.xiong.androidquick.tool.FileUtils;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;

/**
 * Created by yanliang
 * on 2019/3/25
 */
public class ImageUtils {
    /**
     * View to bitmap.
     *
     * @param view The view.
     * @return bitmap
     */
    public static Bitmap view2Bitmap(final View view) {
        if (view == null)
            return null;
        Bitmap ret = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(ret);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return ret;
    }

    @SuppressLint("CheckResult")
    public static void saveImageGallary(final Context context, final View view) {
        Flowable.create(new FlowableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(FlowableEmitter<Bitmap> e) throws Exception {
                e.onNext(view2Bitmap(view));
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.schedulers.Schedulers.newThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) {
                        try {
                            FileUtils.saveImageToGallery(context,bitmap);
                            ToastUtil.showShort("已保存至目录KADDAS文件夹下");
                            EventBus.getDefault().post(new EventCenter<String>(ApiConstants.SAVE_IMG_SUCCESS));
                        } catch (Exception e) {
                            ExceptionUtil.handle(e);
                            ToastUtil.showShort("保存图片失败，请重试！");
                        }
                    }
                });

    }
    @SuppressLint("CheckResult")
    public static void saveImageGallary(final Context context, final View view,String name) {
        Flowable.create(new FlowableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(FlowableEmitter<Bitmap> e) throws Exception {
                e.onNext(view2Bitmap(view));
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.schedulers.Schedulers.newThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) {
                        try {
                            FileUtils.saveFileImageToGallery(context,bitmap,name);
                            ToastUtil.showShort("已保存至目录KADDAS文件夹下");
                            EventBus.getDefault().post(new EventCenter<String>(ApiConstants.SAVE_IMG_SUCCESS));
                        } catch (Exception e) {
                            ExceptionUtil.handle(e);
                            ToastUtil.showShort("保存图片失败，请重试！");
                        }
                    }
                });

    }

}
