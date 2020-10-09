package com.kaadas.lock.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

public class QrCodeUtils {

    private QrCodeUtils(){
        throw new AssertionError();
    }

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo,int denominator) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //logo大小为二维码整体大小的1/denominator
        float scaleFactor = srcWidth * 1.0f / denominator / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 生成二维码
     * @param content
     * @param heightPix
     * @param logo
     * @param hints
     * @return
     */
    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo, Map<EncodeHintType,?> hints,int denominator) {
        try {

            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, heightPix, heightPix, hints);
            int[] pixels = new int[heightPix * heightPix];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < heightPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * heightPix + x] = 0xff000000;
                    } else {
                        pixels[y * heightPix + x] = 0xffffffff;
                    }
                }
            }

            // 生成二维码图片的格式
            Bitmap bitmap = Bitmap.createBitmap(heightPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, heightPix, 0, 0, heightPix, heightPix);

            if (logo != null) {
                if(denominator > 0){
                    bitmap = addLogo(bitmap, logo,denominator);
                }else{
                    bitmap = addLogo(bitmap, logo,3);
                }

            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 生成二维码
     * @param content
     * @param heightPix
     * @return
     */
    public static Bitmap createQRCode(String content, int heightPix) {
        return createQRCode(content,heightPix,null);
    }

    /**
     * 生成二维码
     * @param content
     * @param heightPix
     * @param logo
     * @return
     */
    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo,int denominator) {
        //配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put( EncodeHintType.CHARACTER_SET, "utf-8");
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置空白边距的宽度
        hints.put(EncodeHintType.MARGIN, 1); //default is 4
        return createQRCode(content,heightPix,logo,hints,denominator);
    }

    /**
     * 生成二维码
     * @param content
     * @param heightPix
     * @param logo
     * @return
     */

    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo) {
        //配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put( EncodeHintType.CHARACTER_SET, "utf-8");
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置空白边距的宽度
        hints.put(EncodeHintType.MARGIN, 1); //default is 4
        return createQRCode(content,heightPix,logo,hints,-1);
    }
}
