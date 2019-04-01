package com.kaadas.lock.utils;

import android.Manifest;
import android.app.Activity;
import android.os.Build;

import com.kaadas.lock.MyApplication;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by David
 */
public class PermissionUtil {
public String[] permission=new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE};



    public static final int REQUEST_PERMISSION_REQUEST_CODE=1;
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
}
