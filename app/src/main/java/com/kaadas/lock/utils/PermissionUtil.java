package com.kaadas.lock.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kaadas.lock.MyApplication;

import java.util.ArrayList;
import java.util.List;



import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by David
 */
public class PermissionUtil {
public String[] permission=new String[] {
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO};



    public static final int REQUEST_PERMISSION_REQUEST_CODE=1;
    public static final int REQUEST_AUDIO_PERMISSION_REQUEST_CODE=8;
    private static PermissionUtil permissionUtil = null;

    public static PermissionUtil getInstance()
    {
        if (permissionUtil == null) {
            permissionUtil = new PermissionUtil();
        }
        return permissionUtil;
    }
    //请求权限
    public void requestPermission(String[] permissions, Activity activity, PermissionListener permissionListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  //版本号大鱼23
            //判断是否已经赋予权限   没有权限  赋值权限
            permissions = checkPermission(permissions);
            if (permissions.length == 0) {
                permissionListener.allPermissionAgree();
            }else {
                //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(activity, checkPermission(permissions), REQUEST_PERMISSION_REQUEST_CODE);
            }
        }
    }
    //请求权限
    public void requestPermission(String[] permissions, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  //版本号大鱼23
            //判断是否已经赋予权限   没有权限  赋值权限
            permissions = checkPermission(permissions);
            if (permissions.length == 0) {
            }else {
                //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(activity, checkPermission(permissions), REQUEST_PERMISSION_REQUEST_CODE);

            }
        }
    }
    //检查权限
    public String[] checkPermission(String[] permissions) {
        List<String> noGrantedPermission = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(MyApplication.getInstance(), permission) != PERMISSION_GRANTED) {
                noGrantedPermission.add(permission);
            }
        }
        String[] permission = new String[noGrantedPermission.size()];

        return noGrantedPermission.toArray(permission);
    }



    public interface PermissionListener {
        void allPermissionAgree();
    }

    /**
     * 跳转到权限设置
     *
     * @param activity
     */
    public static void toPermissionSetting(Context activity) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            toSystemConfig(activity);
        } else {
            try {
                toApplicationInfo(activity);
            } catch (Exception e) {
                e.printStackTrace();
                toSystemConfig(activity);
            }
        }
    }

    /**
     * 应用信息界面
     *
     * @param activity
     */
    public static void toApplicationInfo(Context activity) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(localIntent);
    }

    /**
     * 系统设置界面
     *
     * @param activity
     */
    public static void toSystemConfig(Context activity) {
        try {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
