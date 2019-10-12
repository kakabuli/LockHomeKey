package com.kaadas.lock.widget.image;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kaadas.lock.R;
import com.lzy.imagepicker.loader.ImageLoader;

import java.io.File;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class GlideImageLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {

     RequestOptions options = new RequestOptions()
             .placeholder(R.drawable.ic_default_image)       //加载成功之前占位图
             .error(R.drawable.ic_default_image);           //加载错误之后的错误图
        Glide.with(activity).load(Uri.fromFile(new File(path))).apply(options).into(imageView);

//        Glide.with(activity)                                     //配置上下文
//                .load(Uri.fromFile(new File(path)))             //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
//                .error(R.drawable.ic_default_image)           //设置错误图片
//                .placeholder(R.drawable.ic_default_image)     //设置占位图片
//                .into(imageView);
    }

    @Override
    public void displayImagePreview(Activity activity, String path, ImageView imageView, int width, int height) {
        Glide.with(activity)                             //配置上下文
                .load(Uri.fromFile(new File(path)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .into(imageView);
    }

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView) {

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_default_image)       //加载成功之前占位图
                .error(R.drawable.ic_default_image)          //加载错误之后的错误图
                .transform(new CircleTransform(activity));

        Glide.with(activity).load(Uri.fromFile(new File(path))).apply(options).into(imageView);

//        Glide.with(activity)                                     //配置上下文
//                .load(Uri.fromFile(new File(path)))             //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
//                .error(R.drawable.ic_default_image)           //设置错误图片
//                .placeholder(R.drawable.ic_default_image)     //设置占位图片
//                .transform(new CircleTransform(activity))
//                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {

    }


}
