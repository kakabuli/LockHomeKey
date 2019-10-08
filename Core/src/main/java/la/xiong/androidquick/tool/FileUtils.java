package la.xiong.androidquick.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.concurrent.Executors;

/**
 * Created by jiangkang on 2017/9/20.
 */

public class FileUtils {

    public static String getAssetsPath(String filename) {
        return "file:///android_asset/" + filename;
    }

    public static InputStream getInputStreamFromAssets(String filename) throws IOException {
        AssetManager manager =Utils.getApp().getApplicationContext().getAssets();
        return manager.open(filename);
    }

    public static String[] listFilesFromPath(String path) throws IOException {
        AssetManager manager = Utils.getApp().getApplicationContext().getAssets();
        return manager.list(path);
    }


    public static AssetFileDescriptor getAssetFileDescription(String filename) throws IOException {
        AssetManager manager = Utils.getApp().getAssets();
        return manager.openFd(filename);
    }
    /**
     * 获取图片裁剪缓存目录
     *
     * @return 创建失败, 返回""
     */
    public static String getImageCropCachePath(Context context) {
        String path = createDir(createRootPath(context) + File.separator + "imgCrop" + File.separator);
        return path;
    }
    //判断sdcar 可用
    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
    public static String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {
                //                MyLogUtils.i("----- 创建文件夹", file.getAbsolutePath());
                file.mkdir();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                //                MyLogUtils.i("----- 创建文件夹" ,file.getAbsolutePath());
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }
    /**
     * 创建根缓存目录
     *
     * @return
     */
    public static String createRootPath(Context context) {
        String cacheRootPath = "";
        if (isSdCardAvailable()) {
            // /sdcard/Android/data/<application package>/cache
            cacheRootPath = context.getExternalCacheDir().getPath();
        } else {
            // /data/data/<application package>/cache
            cacheRootPath = context.getCacheDir().getPath();
        }
        return cacheRootPath;
    }

    public static void writeStringToFile(String string, File file, boolean isAppending) {
        FileWriter writer = null;
        BufferedWriter bufferedWriter = null;
        try {
            writer = new FileWriter(file);
            bufferedWriter = new BufferedWriter(writer);
            if (isAppending) {
                bufferedWriter.append(string);
            } else {
                bufferedWriter.write(string);
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void writeStringToFile(String string, String filePath, boolean isAppending) {
        writeStringToFile(string, new File(filePath), isAppending);
    }


    public static Bitmap getBitmapFromAssets(String filename) throws IOException {
        AssetManager manager =Utils.getApp().getApplicationContext().getAssets();
        InputStream inputStream = manager.open(filename);
        return BitmapFactory.decodeStream(inputStream);
    }


    public static void copyAssetsToFile(final String assetFilename, final String dstName) {
        Executors.newCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    File dstFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ktools", dstName);
                    fos = new FileOutputStream(dstFile);
                    InputStream fileInputStream = getInputStreamFromAssets(assetFilename);
                    byte[] buffer = new byte[1024 * 2];
                    int byteCount;
                    while ((byteCount = fileInputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteCount);
                    }
                    fos.flush();
                } catch (IOException e) {
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }


    /**
     * @param filename  filename you will create
     * @param directory directory where the file exists
     * @return true if the file created successfully, or return false
     */
    public static boolean createFile(String filename, String directory) {
        boolean isSuccess = false;
        File file = new File(directory, filename);
        if (!file.exists()) {
            try {
                isSuccess = file.createNewFile();
            } catch (IOException e) {
            }
        } else {
            file.delete();
            try {
                isSuccess = file.createNewFile();
            } catch (IOException e) {
            }
        }
        return isSuccess;
    }


    public static boolean hideFile(String directory, String filename) {
        boolean isSuccess;
        File file = new File(directory, filename);
        isSuccess = file.renameTo(new File(directory, ".".concat(filename)));
        if (isSuccess) {
            file.delete();
        }
        return isSuccess;
    }
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static long getFolderSize(final String folderPath) {
        long size = 0;
        File directory = new File(folderPath);
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    size += getFolderSize(file.getAbsolutePath());
                } else {
                    size += file.length();
                }
            }
        }
        return size;
    }
    public static String getFileSizeFromate(final String folderPath){
        long size=new File(folderPath).length();
        return getFormatSize(size);
    }
    public static String getFormatSize(double size) {

        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            //            return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }


    public static String readFromFile(String filename) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(getAssetFileDescription(filename).getFileDescriptor()));
            StringBuilder stringBuilder = new StringBuilder();
            String content;
            while ((content = bufferedReader.readLine()) != null) {
                stringBuilder.append(content);
            }
            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public  static  String  getSmailImage(String filePath){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bm = BitmapFactory.decodeFile(filePath, options);
        if(bm == null){
            return  null;
        }
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filePath);
            bm.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        }catch (Exception e){
            throw new NullPointerException("输出地址未找到");
        }finally{
            try {
                if(outputStream != null)
                    outputStream.close() ;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath ;
    }
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }

        return inSampleSize;
    }


    /**
     * 保存bitmap到SD卡
     * @param bitName 保存的名字
     * @param mBitmap 图片对像
     * return 生成压缩图片后的图片路径
     */
    @SuppressLint("SdCardPath")
    public static String saveMyBitmap(String bitName,Bitmap mBitmap) {
        File f = new File("/sdcard/" + bitName + ".png");

        try {
            f.createNewFile();
        } catch (IOException e) {
            System.out.println("在保存图片时出错：" + e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        } catch (Exception e) {
            return "create_bitmap_error";
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "/sdcard/" + bitName + ".png";
    }


    /**
     * 保存bitmap到SD卡
     * @param bitmap
     * @param imagename
     */
    public static String saveBitmapToSDCard(Bitmap bitmap, String imagename) {
        String path = "/sdcard/" + "img-" + imagename + ".jpg";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
            }

            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        saveFileImageToGallery(context,bmp,"kaadas" +  ".jpg");
    }


    public static void saveFileImageToGallery(Context context, Bitmap bmp,String fineName) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "KAADAS");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = fineName;
        //把之前的文件改名备份一下,避免被覆盖
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //        // 其次把文件插入到系统图库
        //        try {
        //            MediaStore.Images.Media.insertImage(context.getContentResolver(),
        //                    file.getAbsolutePath(), fileName, null);
        //        } catch (FileNotFoundException e) {
        //            e.printStackTrace();
        //        }
        //        // 最后通知图库更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);



    }
    public static String getDownLoadPath(){
            String appCachePath = null;
            if (checkSDCard()) {
                appCachePath = Environment.getExternalStorageDirectory() + "/KAADAS/" ;
            } else {
                appCachePath = Environment.getDataDirectory().getPath() + "/KAADAS/" ;
            }
            File file = new File(appCachePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            return appCachePath;
    }
    public static boolean checkSDCard() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

        return sdCardExist;

    }

    /**
     * 图片复制.
     */
    public static boolean copyImageAndSaveToGallery(String srcFile, String destFile,Context context) {
        try {
            FileInputStream in = new FileInputStream(srcFile);
            FileOutputStream out = new FileOutputStream(destFile);
            byte[] bytes = new byte[1024];
            int c;
            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
            }
            in.close();
            out.flush();
            out.close();

            File appDir = new File(Environment.getExternalStorageDirectory(), "QIZHI");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            File file = new File(appDir, destFile);

            // 其次把文件插入到系统图库
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        file.getAbsolutePath(), destFile, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);


        } catch (Exception e) {
            return false;
        }
        return true;
    }


}
