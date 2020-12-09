package com.kaadas.lock.shulan.pro_sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;


public class PreferenceUtil {
    public static final String METHOD_CONTAIN_KEY = "shulan_method_contain_key";
    public static final String AUTHORITY = "com.kaadas.lock.shulan.preference";
    public static final Uri URI = Uri.parse("content://" + AUTHORITY);
    public static final String METHOD_QUERY_VALUE = "shulan_method_query_value";
    public static final String METHOD_EIDIT_VALUE = "shulan_method_edit";
    public static final String METHOD_QUERY_PID = "shulan_method_query_pid";
    public static final String KEY_VALUES = "key_shulan_result";


    public static final Uri sContentCreate = Uri.withAppendedPath(URI, "create");

    public static final Uri sContentChanged = Uri.withAppendedPath(URI, "changed");

    public static SharedPreferences getSharedPreference(@NonNull Context ctx, String preferName) {
        return SharedPreferenceProxy.getSharedPreferences(ctx, preferName);
    }
}