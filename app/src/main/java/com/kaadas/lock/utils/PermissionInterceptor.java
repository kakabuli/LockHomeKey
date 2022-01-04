package com.kaadas.lock.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;

import com.hjq.permissions.IPermissionInterceptor;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.List;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2021/01/04
 *    desc   : 权限申请拦截器
 */
public final class PermissionInterceptor implements IPermissionInterceptor {

//    @Override
//    public void requestPermissions(Activity activity, OnPermissionCallback callback, List<String> permissions) {
//        // 这里的 Dialog 只是示例，没有用 DialogFragment 来处理 Dialog 生命周期
//        new AlertDialog.Builder(activity)
//                .setTitle(R.string.common_permission_hint)
//                .setMessage(R.string.common_permission_message)
//                .setPositiveButton(R.string.common_permission_granted, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        PermissionFragment.beginRequest(activity, new ArrayList<>(permissions), PermissionInterceptor.this, callback);
//                    }
//                })
//                .setNegativeButton(R.string.common_permission_denied, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .show();
//    }

    @Override
    public void grantedPermissions(Activity activity, List<String> allPermissions, List<String> grantedPermissions,
                                   boolean all, OnPermissionCallback callback) {
        if (callback != null) {
            if(all){
                callback.onGranted(grantedPermissions, all);
            }
        }
    }

    @Override
    public void deniedPermissions(Activity activity, List<String> allPermissions, List<String> deniedPermissions,
                                  boolean never, OnPermissionCallback callback) {

        if (callback != null) {
            callback.onDenied(deniedPermissions, never);
        }

        deniedPermissions(activity,deniedPermissions);
    }

    private void deniedPermissions(Activity activity , List<String> deniedPermissions){

        if(deniedPermissions.size() > 0 ){
            if(Permission.CAMERA.equals(deniedPermissions.get(0))){
                permissionTipsDialog(activity,activity.getString(R.string.kaadas_common_permission_camera),activity.getString(R.string.add_device_and_up_head_portrait));
            }
            else if(Permission.ACCESS_FINE_LOCATION.equals(deniedPermissions.get(0))){
                permissionTipsDialog(activity,activity.getString(R.string.kaadas_common_permission_location),activity.getString(R.string.device_conn));
            }
            else if(Permission.READ_EXTERNAL_STORAGE.equals(deniedPermissions.get(0))){
                permissionTipsDialog(activity,activity.getString(R.string.kaadas_common_permission_storage),activity.getString(R.string.store_pictures_and_videos));
            }
            else if(Permission.WRITE_EXTERNAL_STORAGE.equals(deniedPermissions.get(0))){
                permissionTipsDialog(activity,activity.getString(R.string.kaadas_common_permission_storage),activity.getString(R.string.store_pictures_and_videos));
            }
            else if(Permission.RECORD_AUDIO.equals(deniedPermissions.get(0))){
                permissionTipsDialog(activity,activity.getString(R.string.kaadas_common_permission_microphone),activity.getString(R.string.use_intercom_function));
            }
            else if(Permission.CALL_PHONE.equals(deniedPermissions.get(0))){
                permissionTipsDialog(activity,activity.getString(R.string.kaadas_common_permission_phone),activity.getString(R.string.call_the_service_hotline));
            }
            else {
                permissionTipsDialog(activity,activity.getString(R.string.kaadas_common_permission_setting),activity.getString(R.string.device_conn));
            }
        }
    }


    public void permissionTipsDialog(Activity activity,String str,String str1){
        AlertDialogUtil.getInstance().permissionTipsDialog(activity, (activity.getString(R.string.kaadas_permission_title,str))
                , (activity.getString(R.string.kaadas_permission_content,str,str1)),
                (activity.getString(R.string.kaadas_permission_content_tisp,str)), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }

    /**
     * 显示授权对话框
     */
    protected void showPermissionDialog(Activity activity, List<String> permissions) {

        AlertDialogUtil.getInstance().titleTwoButtonKaadasDialog(activity, MyApplication.getInstance().getString(R.string.kaadas_common_permission_alert),
                getPermissionHint(activity, permissions),
                MyApplication.getInstance().getString(R.string.cancel), MyApplication.getInstance().getString(R.string.confirm),
                "#0066A1","#FFFFFF",new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        XXPermissions.startPermissionActivity(activity, permissions);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }

    /**
     * 根据权限获取提示
     */
    protected String getPermissionHint(Context context, List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return context.getString(R.string.kaadas_common_permission_fail_2);
        }

        List<String> hints = new ArrayList<>();
        for (String permission : permissions) {
            switch (permission) {
                case Permission.READ_EXTERNAL_STORAGE:
                case Permission.WRITE_EXTERNAL_STORAGE:
                case Permission.MANAGE_EXTERNAL_STORAGE: {
                    String hint = context.getString(R.string.kaadas_common_permission_storage);
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                }
                case Permission.CAMERA: {
                    String hint = context.getString(R.string.kaadas_common_permission_camera);
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                }
                case Permission.RECORD_AUDIO: {
                    String hint = context.getString(R.string.kaadas_common_permission_microphone);
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                }
                case Permission.ACCESS_FINE_LOCATION:
                case Permission.ACCESS_COARSE_LOCATION:
                case Permission.ACCESS_BACKGROUND_LOCATION: {
                    String hint;
                    if (!permissions.contains(Permission.ACCESS_FINE_LOCATION) &&
                            !permissions.contains(Permission.ACCESS_COARSE_LOCATION)) {
                        hint = context.getString(R.string.kaadas_common_permission_location_background);
                    } else {
                        hint = context.getString(R.string.kaadas_common_permission_location);
                    }
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                }
                case Permission.BLUETOOTH_SCAN:
                case Permission.BLUETOOTH_CONNECT:
                case Permission.BLUETOOTH_ADVERTISE: {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        String hint = context.getString(R.string.kaadas_common_permission_bluetooth);
                        if (!hints.contains(hint)) {
                            hints.add(hint);
                        }
                    }
                    break;
                }
                case Permission.READ_PHONE_STATE:
                case Permission.CALL_PHONE:
                case Permission.ADD_VOICEMAIL:
                case Permission.USE_SIP:
                case Permission.READ_PHONE_NUMBERS:
                case Permission.ANSWER_PHONE_CALLS: {
                    String hint = context.getString(R.string.kaadas_common_permission_phone);
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                }
                case Permission.GET_ACCOUNTS:
                case Permission.READ_CONTACTS:
                case Permission.WRITE_CONTACTS: {
                    String hint = context.getString(R.string.kaadas_common_permission_contacts);
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                }
                case Permission.READ_CALENDAR:
                case Permission.WRITE_CALENDAR: {
                    String hint = context.getString(R.string.kaadas_common_permission_calendar);
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                }
                case Permission.READ_CALL_LOG:
                case Permission.WRITE_CALL_LOG:
                case Permission.PROCESS_OUTGOING_CALLS: {
                    String hint = context.getString(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ?
                            R.string.kaadas_common_permission_call_log : R.string.kaadas_common_permission_phone);
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                }
                case Permission.BODY_SENSORS: {
                    String hint = context.getString(R.string.kaadas_common_permission_sensors);
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                }
                case Permission.ACTIVITY_RECOGNITION: {
                    String hint = context.getString(R.string.kaadas_common_permission_activity_recognition);
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                }
                case Permission.SEND_SMS:
                case Permission.RECEIVE_SMS:
                case Permission.READ_SMS:
                case Permission.RECEIVE_WAP_PUSH:
                case Permission.RECEIVE_MMS: {
                    String hint = context.getString(R.string.kaadas_common_permission_sms);
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                }
                case Permission.REQUEST_INSTALL_PACKAGES: {
                    String hint = context.getString(R.string.kaadas_common_permission_install);
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                }
                case Permission.NOTIFICATION_SERVICE: {
                    String hint = context.getString(R.string.kaadas_common_permission_notification);
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                }
                case Permission.SYSTEM_ALERT_WINDOW: {
                    String hint = context.getString(R.string.kaadas_common_permission_window);
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                }
                case Permission.WRITE_SETTINGS: {
                    String hint = context.getString(R.string.kaadas_common_permission_setting);
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                }
                case Permission.PACKAGE_USAGE_STATS: {
                    String hint = context.getString(R.string.kaadas_common_permission_task);
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                }
                default:
                    break;
            }
        }

        if (!hints.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (String text : hints) {
                if (builder.length() == 0) {
                    builder.append(text);
                } else {
                    builder.append("、")
                            .append(text);
                }
            }
            builder.append(" ");
            return context.getString(R.string.kaadas_common_permission_fail_3, builder.toString());
        }

        return context.getString(R.string.kaadas_common_permission_fail_2);
    }
}