package com.kaadas.lock.widget.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.TypedValue;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;

public class ImageLoad {

    public static ImagePicker initDefault(Context context) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        //
        imagePicker.setImageLoader(new GlideImageLoader());
        //设置只选一张图片
        imagePicker.setMultiMode(false);
        //矩形  长宽固定200
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, context.getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, context.getResources().getDisplayMetrics());
        imagePicker.setFocusWidth(width);
        imagePicker.setFocusHeight(height);

        //是否显示摄像机
        imagePicker.setShowCamera(false);
        //矩形区域保存
        imagePicker.setSaveRectangle(true);
        //设置保存的图片大小
        imagePicker.setOutPutX(200);
        imagePicker.setOutPutY(200);
        //设置裁剪
        imagePicker.setCrop(true);

        return imagePicker;
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


}
