package com.kaadas.lock.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.kaadas.lock.MyApplication;

import java.io.File;

/**
 * Created by ${howard} on 2018/4/17.
 */

public class FileUtils {
    private String rootSDString = MyApplication.getInstance().getExternalFilesDir("").getAbsolutePath();
    private String rootDirString = "kaadas";
    private String picturesDirString = "pictures";
    private String videosDirString = "videos";


    private static FileUtils instance;

    public static synchronized FileUtils getInstance() {
        if (instance == null) {
            instance = new FileUtils();
        }
        return instance;
    }

    public String getRootDir() {
        return rootSDString + "/" + rootDirString;
    }

    public String getPictureDir() {
        return getRootDir() + "/" + picturesDirString;
    }

    public String getVideoDir() {
        return getRootDir() + "/" + videosDirString;
    }

    public boolean createPictureDir() {
        File captureDir = new File(getPictureDir());
        if (!captureDir.exists()) {
            return captureDir.mkdirs();
        }
        return true;
    }

    public boolean createVideoDir() {
        File captureDir = new File(getVideoDir());
        if (!captureDir.exists()) {
            return captureDir.mkdirs();
        }
        return true;
    }

    public static boolean isFileExist(String fileStr) {
        File file1 = new File(fileStr);
        return file1.exists();
    }

    public static boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            return file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @return sdcard是否存在
     */
    public static boolean isSDCardExist() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    public static String getPhoneUUID(Context context) {

        String androidId = (String) SPUtils.get( "androidId", null);

        if (TextUtils.isEmpty(androidId)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }else {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                androidId = tm.getDeviceId();
            }

            if (TextUtils.isEmpty(androidId)) {
                androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            }
            SPUtils.put( "androidId",androidId);
        }
        return androidId;
    }
    public static void createFolder(String folder){
    	File file=new File(folder);
    	if (!file.exists()){
    		file.mkdirs();
		}
	}
}
