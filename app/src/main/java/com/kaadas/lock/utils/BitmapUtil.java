package com.kaadas.lock.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.Display;
import android.view.View;


import com.kaadas.lock.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.graphics.BitmapFactory.decodeFile;


/**
 * @Description:图片处理类 对图片的操作类，包括： 截屏，保存图片，获取指定路径的图片， 图片转换成字节数组，字节数组转换成图片，对图片的缩放
 */
public class BitmapUtil {

    /**
     * 通过字节数组加载图片
     *
     * @param data
     * @return
     */
    public static Bitmap loadBitmap(byte[] data) {
        Bitmap bitmap = null;
        if (data != null && data.length > 0) {
            try {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;

    }

    private static final long MB = 1024 * 1024;

    /**
     * 图片转换成字节数组
     *
     * @param bm 图片对象
     * @return
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 字节数组转换成图片
     *
     * @param intent Intent对象
     * @return 图片对象
     */
    public static Bitmap Bytes2Bitmap(Intent intent) {
        byte[] buff = intent.getByteArrayExtra("bitmap");
        Bitmap bm = BitmapFactory.decodeByteArray(buff, 0, buff.length);
        return bm;
    }

    /**
     * 截屏方法
     *
     * @param activity 对象
     *                 ，可以通过getActivity()方法获取
     * @return
     */
    public static Bitmap shot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Display display = activity.getWindowManager().getDefaultDisplay();
        view.layout(0, 500, display.getWidth() - 200, display.getHeight() - 250);
        Bitmap bitmap = view.getDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(bitmap);
        // return Bitmap.createBitmap(bmp, 100,100, 500, 500);
        return bmp;
    }

    /**
     * 截取指定view的视图
     *
     * @param v 要截取的view对象
     * @return Bitmap对象
     */
    public static Bitmap getViewBitmap(View v) {
        v.clearFocus(); // 清除视图焦点
        v.setPressed(false);// 将视图设为不可点击

        boolean willNotCache = v.willNotCacheDrawing(); // 返回视图是否可以保存他的画图缓存
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation //将视图在此操作时置为透明
        int color = v.getDrawingCacheBackgroundColor(); // 获得绘制缓存位图的背景颜色
        v.setDrawingCacheBackgroundColor(0); // 设置绘图背景颜色
        if (color != 0) { // 如果获得的背景不是黑色的则释放以前的绘图缓存
            v.destroyDrawingCache(); // 释放绘图资源所使用的缓存
        }
        v.buildDrawingCache(); // 重新创建绘图缓存，此时的背景色是黑色
        Bitmap cacheBitmap = v.getDrawingCache(); // 将绘图缓存得到的,注意这里得到的只是一个图像的引用
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap); // 将位图实例化
        // Restore the view //恢复视图
        v.destroyDrawingCache();// 释放位图内存
        v.setWillNotCacheDrawing(willNotCache);// 返回以前缓存设置
        v.setDrawingCacheBackgroundColor(color);// 返回以前的缓存颜色设置
        return bitmap;
    }

    /**
     * 保存图片到指定路径的方法
     *
     * @param path   图片保存的相对路径
     * @param name   图片的名字
     * @param bitmap 要保存的图片
     * @throws IOException 读写图片文件出现的异常信息
     */
    public static void save(String path, String name, Bitmap bitmap) throws IOException {
        File file = new File(path, name);
        // 若图片文件在SD卡的文件夹不存在
        if (!file.getParentFile().exists()) {
            // 创建该文件夹
            file.getParentFile().mkdirs();
        }
        // 若文件不存在，则创建
        if (!file.exists()) {
            file.createNewFile();
        }
        // 创建文件输出流
        FileOutputStream out = new FileOutputStream(file);
        // 保存图片至SD卡指定文件夹
        bitmap.compress(CompressFormat.JPEG, 100, out);
    }


    /**
     * 获得指定路径的图片
     *
     * @param path 图片的本地路径
     * @param name 图片的名字
     * @return 图片对象
     * @throws IOException
     */
    public static Bitmap getBitmap(String path, String name) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        File file = new File(path, name);
        if (file.exists() && (file.length() / MB) > 1) {
            options.inSampleSize = 2;
        }
        Bitmap imageBitmap = decodeFile(file.getAbsolutePath(), options);
        return imageBitmap;
    }

    public static Bitmap getBitmap(String path) {
        Bitmap imageBitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            File file = new File(path);
            if (file.exists() && (file.length() / MB) > 1) {
                options.inSampleSize = 2;
            }
            imageBitmap = decodeFile(path, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageBitmap;
    }

    /***
     * 图片的缩放方法（图片按照给定宽高缩放）
     *
     * @param bm        ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return 可用的图片 bitmap对象
     */
    public static Bitmap zoomImage(Bitmap bm, double newWidth, double newHeight) {
        if (bm != null) {

            // 获取这个图片的宽和高
            float width = bm.getWidth();
            float height = bm.getHeight();
            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            // 计算宽高缩放率
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 缩放图片动作
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, (int) width, (int) height, matrix, true);
            if (bm != null && !bm.isRecycled()) {
                bm.recycle();
                bm = null;
            }
            return bitmap;
        }
        return null;
    }

    public static String getSDPath() {
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            return MyApplication.getInstance().getExternalFilesDir("").getAbsolutePath();
        } else
            return Environment.getDownloadCacheDirectory().toString();
    }

    public static File getSDPathFile() {
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            return MyApplication.getInstance().getExternalFilesDir("");
        } else
            return Environment.getDownloadCacheDirectory();
    }

    /**
     * 高斯模糊
     *
     * @param context
     * @param bitmap
     * @param radius  模糊度 （0，,25]
     * @return
     */
    public static Bitmap blurBitmap(Context context, Bitmap bitmap, float radius) {

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(context.getApplicationContext());

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur
        blurScript.setRadius(radius);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap
//        bitmap.recycle();

        //After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;
    }

    /**
     * 压缩图片至指定大小
     *
     * @param bitmap
     * @param toFile
     * @param width
     * @param height
     * @param quality
     */
    public static void transImage(Bitmap bitmap, String toFile, int width, int height, int quality) {
        try {
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            // 缩放图片的尺寸
            float scaleWidth = (float) width / bitmapWidth;
            float scaleHeight = (float) height / bitmapHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 产生缩放后的Bitmap对象
            Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
            // save file
            File myCaptureFile = new File(toFile);
            FileOutputStream out = new FileOutputStream(myCaptureFile);
            if (resizeBitmap.compress(CompressFormat.JPEG, quality, out)) {
                out.flush();
                out.close();
            }
            if (!bitmap.isRecycled()) {
                bitmap.recycle();//记得释放资源，否则会内存溢出
            }
            if (!resizeBitmap.isRecycled()) {
                resizeBitmap.recycle();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 压缩图片到100k
     *
     * @param image
     * @return
     */

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }


    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        if (filePath.contains(".mp4")) {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                retriever.setDataSource(filePath);
                bitmap = retriever.getFrameAtTime();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } finally {
                try {
                    retriever.release();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        } else {// 压缩待优化
            try {

//                options.inJustDecodeBounds = true;
//                Logger.i("----path","in:"+filePath);
                File file = new File(filePath);
//                Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(),options);

//                int sampleSize = 1;

                long currentSize = file.length() / 1024;
                long currentSample = 1;
                //                    int currentSample = 1;
                while (file.exists() && currentSize > 150) {
//                    options.inSampleSize *= 2;
                    currentSample *= 2;
                    currentSize /= 2;
                }
//                Logger.i("----path","in:"+filePath+":"+file.length()/1024/1024+",currentSize:"+currentSize+",currentSample:"+currentSample);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = (int) currentSample;
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
//                Logger.i("----bm","in"+bm);
//                if (bm != null) {
//                    int currentSize = bm.getByteCount() / 1024 ;
//                    int currentSample = 1;
//                    while (currentSize > 100) {
//                        currentSample *= 2;
//                        currentSize /= 2;
//                    }
//                    options.inSampleSize = currentSample;
//                    Logger.i("---inSampleSize","inSampleSize:"+options.inSampleSize);
//                    options.inJustDecodeBounds = false;
//                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return decodeFile(filePath, options);
    }

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap getimage(String srcPath) {

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) {

            be = 1;
        }
        newOpts.inSampleSize = be;//设置缩放比例
        return BitmapFactory.decodeFile(srcPath, newOpts);
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2:" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * Compress image by pixel, this will modify image width/height. Used to get
     * thumbnail
     *
     * @param imgPath image path
     * @param pixelW  target pixel of width
     * @param pixelH  target pixel of height
     * @return
     */
    public static Bitmap ratio(String imgPath, float pixelW, float pixelH) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 想要缩放的目标尺寸
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        // 压缩好比例大小后再进行质量压缩
        // return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }
}

