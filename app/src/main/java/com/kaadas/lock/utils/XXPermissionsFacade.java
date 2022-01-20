package com.kaadas.lock.utils;

import android.app.Activity;
import android.content.Context;
import com.hjq.permissions.IPermissionInterceptor;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import java.util.ArrayList;
import java.util.List;

/**
 * author : zhangjierui
 * time   : 2021/12/31
 * desc   : 权限处理，XXPermissions的外观模式
 */
public class XXPermissionsFacade {

    private final List<String> mPermissions;

    public interface PermissionCallback {

        default void onGranted(List<String> permissions, boolean all){};

        default void onDenied(List<String> permissions, boolean never) {}
    }

    public interface OnGrantedCallback {
        void onGranted();
    }

    public XXPermissionsFacade(String[] permissions) {
        mPermissions = new ArrayList<>();
        and(permissions);
    }

    public static XXPermissionsFacade with(String... permissions){
        return  new XXPermissionsFacade(permissions);
    }

    public static XXPermissionsFacade get(){
        return  new XXPermissionsFacade(null);
    }

    public XXPermissionsFacade withStoragePermission(){
        addPermission(Permission.WRITE_EXTERNAL_STORAGE);
        addPermission(Permission.READ_EXTERNAL_STORAGE);
        return this;
    }

    public XXPermissionsFacade withCameraPermission(){
        addPermission(Permission.CAMERA);
        return this;
    }

    public XXPermissionsFacade withLocationPermission(){
        addPermission(Permission.ACCESS_FINE_LOCATION);
        addPermission(Permission.ACCESS_COARSE_LOCATION);
        return this;
    }

    public XXPermissionsFacade withCallPhonePermission(){
        addPermission(Permission.CALL_PHONE);
        return this;
    }

    public XXPermissionsFacade withRecordAudioPermission(){
        addPermission(Permission.RECORD_AUDIO);
        return this;
    }

    public XXPermissionsFacade and(String... permissions){
        if(permissions == null){
            return this;
        }
        for (String p: permissions) {
            addPermission(p);
        }
        return this;
    }

    private void addPermission(String perms){
        if(!mPermissions.contains(perms)){
            mPermissions.add(perms);
        }
    }

    public void request(Context context, PermissionCallback callback){
        request(context, new OnPermissionCallback() {
                    public void onGranted(List<String> permissions, boolean all) {
                        if(callback != null) callback.onGranted(permissions, all);
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if(callback != null) callback.onDenied(permissions, never);
                    }
                });
    }

    public void request(Context context, OnGrantedCallback callback){
        request(context, new OnPermissionCallback() {
                    public void onGranted(List<String> permissions, boolean all) {
                        if(callback != null && all) callback.onGranted();
                    }
                });
    }

    private void request(Context context, OnPermissionCallback callback){
        XXPermissions.with(context)
                .permission(mPermissions)
                .request(callback);
    }

    public static void setInterceptor(){
        //待实现
        XXPermissions.setInterceptor(new IPermissionInterceptor(){
            @Override
            public void requestPermissions(Activity activity, OnPermissionCallback callback, List<String> allPermissions) {

            }

            @Override
            public void grantedPermissions(Activity activity, List<String> allPermissions, List<String> grantedPermissions, boolean all, OnPermissionCallback callback) {

            }

            @Override
            public void deniedPermissions(Activity activity, List<String> allPermissions, List<String> deniedPermissions, boolean never, OnPermissionCallback callback) {

            }
        });
    }

    public static boolean isGranted(Context context, String... permissions) {
        return XXPermissions.isGranted(context, permissions);
    }

    public static void checkPermission(Context context, String[] permissions, PermissionCallback callback){
        XXPermissions.with(context)
                .permission(permissions)
                .request(new OnPermissionCallback() {
                    public void onGranted(List<String> permissions, boolean all) {
                        if(callback != null) callback.onGranted(permissions, all);
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if(callback != null) callback.onDenied(permissions, never);
                    }
                });
    }

}
