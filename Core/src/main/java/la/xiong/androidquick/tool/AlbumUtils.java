package la.xiong.androidquick.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by yanliang
 * on 2019/1/9 14:36
 */
public class AlbumUtils {

    /**
     * Start the camera.
     *
     * @param fragment    fragment.
     * @param requestCode code.
     * @param outPath     file path.
     */
    public static void startCamera(Fragment fragment, int requestCode, File outPath) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = getUri(fragment.getContext(), outPath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        fragment.startActivityForResult(intent, requestCode);
    }
    public static void startCamera(Fragment fragment, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = getUri(fragment.getContext(), new File(randomJPGPath()));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * Start the camera.
     *
     * @param fragment    fragment.
     * @param requestCode code.
     * @param outPath     file path.
     */
    public static void startCamera(android.app.Fragment fragment, int requestCode, File outPath) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = getUri(fragment.getActivity(), outPath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * Start the camera.
     *
     * @param activity    activity.
     * @param requestCode code.
     * @param outPath     file path.
     */
    public static void startCamera(Activity activity, int requestCode, File outPath) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = getUri(activity, outPath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void  ChoiceImage(Activity activity, int requestCode){
        Intent intentFromGallery;
        //        当sdk版本低于19时使用此方法
        if (Build.VERSION.SDK_INT < 19) {
            intentFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
            intentFromGallery.setType("image/*");
        } else {
            intentFromGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        //        Intent intentFromGallery = new Intent(Intent.ACTION_PICK);
        //        intentFromGallery.setType("image/*");//相片类型
        activity.startActivityForResult(intentFromGallery, requestCode);


//        Intent albumIntent = new Intent(Intent.ACTION_PICK);
//        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        activity.startActivityForResult(albumIntent, requestCode);

    }


    /**
     * Setting {@link Locale} for {@link Context}.
     */
    public static Context applyLanguageForContext(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            resources.updateConfiguration(config, resources.getDisplayMetrics());
            return context;
        }
    }

    private static Uri getUri(Context context, File outPath) {
        Uri uri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(outPath);
        } else {
            uri = FileProvider.getUriForFile(context, "com.yun.software.newapp.fileprovider.carmer", outPath);
        }
        return uri;
    }

    /**
     * Format the current time in the specified format.
     *
     * @param format format.
     * @return time string.
     */
    public static String getNowDateTime(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
    public  static String randomJPGPath() {
        String outFileFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        int index = new Random().nextInt(1000);
        String outFilePath =getNowDateTime("yyyyMMdd_HHmmssSSS") + "_" + index + ".jpg";
        File file = new File(outFileFolder, outFilePath);
        return file.getAbsolutePath();
    }
}

