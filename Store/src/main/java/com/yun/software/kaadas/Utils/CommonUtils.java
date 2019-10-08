package com.yun.software.kaadas.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.yun.software.kaadas.Comment.MyApplication;
import com.yun.software.kaadas.UI.bean.IndentInfo;
import com.yun.software.kaadas.UI.bean.OrderInfor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import la.xiong.androidquick.tool.LogUtils;

/**
 * 常用工具类
 */
public class CommonUtils {
    /**
     * 日志标识
     */
    private static final String TAG = "CommonUtils";

    public static final String CACHE_FILE_NAME = "star_music";
    public static final String USER_INFO = "userInfo";
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences userSharedPreferences;// 用户信息首选项

    private static long lastClickTime = 0;


    /**
     * 判断用户是否加V 1是加V(true),0是没有加(false)
     */
    public static boolean isV(String isV) {
        if ("1".equals(isV)) {
            return true;
        } else {
            return false;
        }
    }

    // 通过经纬度计算两点之间的距离
    private static double EARTH_RADIUS = 6378.137; // 地球半径 6378.137

    /**
     * 角度弧度计算公式 rad:(). <br/>
     * <p/>
     * 360度=2π π=Math.PI
     * <p/>
     * x度 = x*π/360 弧度
     *
     * @param degree 位置
     * @return 弧度
     * @since JDK 1.6
     */
    private static double getRadian(double degree) {
        return degree * Math.PI / 180.0;
    }

    /**
     * 依据经纬度计算两点之间的距离 GetDistance:(). <br/>
     *
     * @param lat1 1点的纬度
     * @param lng1 1点的经度
     * @param lat2 2点的纬度
     * @param lng2 2点的经度
     * @return 距离 单位 米
     */
    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = getRadian(lat1);
        double radLat2 = getRadian(lat2);
        double a = radLat1 - radLat2;
        double b = getRadian(lng1) - getRadian(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d; //千米
        s = s * 1000d; //米
        return s;
    }
    /**
     * 得到json文件中的内容
     * @param context
     * @param fileName
     * @return
     */
    public static String getJson(Context context,String fileName){
        StringBuilder stringBuilder = new StringBuilder();
        //获得assets资源管理器
        AssetManager assetManager = context.getAssets();
        //使用IO流读取json文件内容
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName),"utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
    /**
     * 判断网络连接状态,运营商网络返回1,wifi下返回2,没有网络,返回0;
     *
     * @param context
     * @return
     *
     */
    /**
     * 无网络时的提示语
     */
    public static final String NOT_NET_CUE = "当前网络不可用,请检查你的网络设置";

    /**
     * 没有网络的状态
     */
    public static final int TYPE_NOT_NET = 0;

    /**
     * 运营商网络
     */
    public static final int TYPE_MOBILE_NET = 1;

    /**
     * WIFI
     */
    public static final int TYPE_WIFI_NET = 2;

    public static int hasNetWorkInfo(Context context) {
        // 获取网络连接管理器
        ConnectivityManager cm = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取当前的连接
        NetworkInfo info = cm.getActiveNetworkInfo();
        // 对连接进行判断
        if (info == null) {
            return TYPE_NOT_NET;
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            return TYPE_MOBILE_NET;
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            return TYPE_WIFI_NET;
        }
        return TYPE_NOT_NET;
    }

    // 判断Sdcard是否存在
    public static boolean isSdcardExist() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static byte[] readBytes(InputStream is) {
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readString(InputStream is) {
        return new String(readBytes(is));
    }

    /**
     * 使用SharedPreferences存储boolean类型数据.
     *
     * @param context
     * @param key     要存储的数据的key
     * @param value   要存储的数据
     */
    public static void cacheBooleanData(Context context, String key,
                                        boolean value) {
        decideSP(context);
        Editor edit = mSharedPreferences.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    /**
     * 判断首选项是否为空
     *
     * @param context
     */
    private static void decideSP(Context context) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME,
                    Context.MODE_PRIVATE);
        }

    }

    /**
     * 提供一个方法返回首选项
     *
     * @param context
     * @return
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        decideSP(context);
        return mSharedPreferences;
    }

    /**
     * 创建用户信息首选项
     *
     * @param context
     */
    private static void userSP(Context context) {
        userSharedPreferences = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
    }

    /**
     * 存储用户信息首选项
     *
     * @param context
     * @param key
     * @param value
     */
    public static void cacheUserStringData(Context context, String key,
                                           String value) {
        userSP(context);
        userSharedPreferences.edit().putString(key, value).commit();
    }

    /**
     * 存储用户登陆状态
     *
     * @param context
     * @param key
     * @param value
     */
    public static void cacheUserbooleanData(Context context, String key,
                                            boolean value) {
        userSP(context);
        userSharedPreferences.edit().putBoolean(key, value).commit();
    }

    /**
     * 取出缓存的boolean类型数据
     *
     * @param context
     * @param key      要取出的数据的key
     * @param defValue 缺省值
     * @return
     */
    public static boolean getUserBooleanData(Context context, String key,
                                             boolean defValue) {
        userSP(context);
        return userSharedPreferences.getBoolean(key, defValue);
    }

    /**
     * 获取用户信息首选项
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String getUserStringData(Context context, String key,
                                           String defValue) {
        userSP(context);
        return userSharedPreferences.getString(key, defValue);
    }

    /**
     * 清除首选项里面的数据
     */
    public static void cleanUserSharedPreferences() {
        Editor editor = userSharedPreferences.edit();
        editor.remove(USER_INFO);
        editor.clear().commit();
    }

    /**
     * 取出缓存的boolean类型数据
     *
     * @param context
     * @param key      要取出的数据的key
     * @param defValue 缺省值
     * @return
     */
    public static boolean getBooleanData(Context context, String key,
                                         boolean defValue) {
        decideSP(context);
        return mSharedPreferences.getBoolean(key, defValue);
    }

    /**
     * 缓存字符串数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void cacheStringData(Context context, String key, String value) {
        decideSP(context);
        mSharedPreferences.edit().putString(key, value).commit();
    }

    /**
     * 根据key获取缓存的数据
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String getStringData(Context context, String key,
                                       String defValue) {
        decideSP(context);
        return mSharedPreferences.getString(key, defValue);
    }

    /**
     * 缓存int型数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void cacheIntData(Context context, String key, int value) {
        decideSP(context);
        mSharedPreferences.edit().putInt(key, value).commit();
    }

    /**
     * 根据key获取缓存的int 数据
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static int getIntData(Context context, String key, int defValue) {
        decideSP(context);
        return mSharedPreferences.getInt(key, defValue);
    }

    /**
     * 获取状态栏的高度
     */
    public static int getStatusBarHeight(Context context) {
        int status_bar_height = 0;
        // 通过反射技术获取状态栏的高度
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");// 拿到字节码对象
            Object instance = clazz.newInstance();// 使用字节码对象new出一个实例
            Field field = clazz.getField("status_bar_height");// 通过字节码对象拿出属性
            int R_status_bar_height = Integer.parseInt(field.get(instance)
                    .toString());// 从实例身上中获取到属性的值
            status_bar_height = context.getResources().getDimensionPixelOffset(
                    R_status_bar_height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status_bar_height;
    }
    /**
     * 获取导航栏高度
     * @param context
     * @return
     */
    public static int getDaoHangHeight(Context context) {
        int result = 0;
        int resourceId = 0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            LogUtils.iTag("getDaoHangHeight" + "高度：" + resourceId);
            LogUtils.iTag("getDaoHangHeight" + "高度：" + context.getResources().getDimensionPixelSize(resourceId) + "");
            return context.getResources().getDimensionPixelSize(resourceId);
        } else
            return 0;
    }

        /**
         * 获取屏幕的大小0：宽度 1：高度
         */
    public static int[] getScreen(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return new int[]{dm.widthPixels, dm.heightPixels};
    }

    /**
     * 判断网络是否可用
     */
    public static boolean getNetIsAvailable(Context context) {
        try {
            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo == null) {
                return false;
            }
            return networkInfo.isAvailable();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isStrangePhone() {
        boolean strangePhone = "mx5".equalsIgnoreCase(Build.DEVICE)
                || "Redmi Note2".equalsIgnoreCase(Build.DEVICE)
                || "Z00A_1".equalsIgnoreCase(Build.DEVICE)
                || "hwH60-L02".equalsIgnoreCase(Build.DEVICE)
                || "hermes".equalsIgnoreCase(Build.DEVICE)
                || ("V4".equalsIgnoreCase(Build.DEVICE) && "Meitu".equalsIgnoreCase(Build.MANUFACTURER))
                || ("m1metal".equalsIgnoreCase(Build.DEVICE) && "Meizu".equalsIgnoreCase(Build.MANUFACTURER));

//        VcPlayerLog.e("lfj1115 ", " Build.Device = " + Build.DEVICE + " , isStrange = " + strangePhone);
        return strangePhone;
    }

    public static paramsTokenBuilder getParamsToken() {
        return new paramsTokenBuilder();
    }

    public static String getHtml(List<String> imgList) {
        if(imgList==null||imgList.size()==0){
            return null;
        }
        StringBuffer buffer=new StringBuffer();
        for (int i = 0; i < imgList.size(); i++) {
           buffer.append("<img src=\""+imgList.get(i)+"\"style=\"width:100%\";>") ;
        }
        LogUtils.iTag("htmlceshi",buffer.toString());
        return buffer.toString();
    }

    public  static String getFormatHtml(String content) {
        if (!content.contains("<img")) { //如果没有img图像标签，可以不做任何处理
            return content;
        }
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("<html>");
        strBuilder.append("<head>");
        strBuilder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
        strBuilder.append("<title>无标题文档</title>");
        strBuilder.append("<script type=\"text/javascript\">");
        strBuilder.append("function aaa() {");
        strBuilder.append("var imgTags = document.getElementsByTagName(\"img\");");
        strBuilder.append("var len = imgTags.length;");
        strBuilder.append("for(var i=0;i<len;i++) {");
        strBuilder.append("imgTags.item(i).onclick = function() {");
        strBuilder.append("window.android.callAndroid(this.src);");
        strBuilder.append("};");
        strBuilder.append("}");
        strBuilder.append("}");
        strBuilder.append("</script>");
        strBuilder.append("<style type=\"text/css\">");
        strBuilder.append("img{ width:100%}");
        strBuilder.append("div{ width:auto; height:auto;}");
        strBuilder.append("</style>");
        strBuilder.append("</head>");
        strBuilder.append("<body onload=\"aaa();\">");
        strBuilder.append("<div>");
        strBuilder.append(content.replaceAll("style=", ""));  //此处为去掉原始属性。如果想去掉指定标签的style属性，此处需要特殊处理。
        strBuilder.append("</div>");
        strBuilder.append("</body>");
        strBuilder.append("</html>");
        return strBuilder.toString();
    }
    public  static class paramsTokenBuilder{
        Map<String,Object> maps=null;
        Map<String,Object> params=null;

        public paramsTokenBuilder() {
            maps=new HashMap<>();
            params=new HashMap<>();
            maps.put("token",UserUtils.getToken());
            maps.put("params",params);
        }
        public paramsTokenBuilder putParams(String key,Object Value){
            params.put(key,Value);
            return this;
        }
        public paramsTokenBuilder putFParams(String key,Object Value){
            maps.put(key,Value);
            return this;
        }
        public Map<String,Object> build(){
            return maps;
        }
    }

    public static paramsBuilder getBundle() {
        return new paramsBuilder();
    }
    public static class paramsBuilder{
        Bundle bundle=null;
        public paramsBuilder() {
            this.bundle=new Bundle();
        }
        public paramsBuilder putParams(String key,String Value){
            bundle.putString(key,Value);
            return this;
        }
        public paramsBuilder putParams(String key,int Value){
            bundle.putInt(key,Value);
            return this;
        }

        public paramsBuilder putParams(String key,boolean Value){
            bundle.getBoolean(key,Value);
            return this;
        }
        public Bundle build(){
            return bundle;
        }
    }

    /**
     * 编码utf-8
     */
    public String EcodeUTF8(String source) throws UnsupportedEncodingException {
        return new String(source.getBytes("UTF-8"));
    }

    /**
     * 解码utf-8
     */
    public String DecodeUTF8(String source) throws UnsupportedEncodingException {
        return new String(source.getBytes(), "UTF-8");
    }

    /**
     * 提示消息
     */
    public static Toast showMessage(Toast toastMsg, Context context, String msg) {
        if (toastMsg == null) {
            toastMsg = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toastMsg.setText(msg);
        }
        toastMsg.show();
        return toastMsg;
    }

    /**
     * 根据文件名获取不带后缀名的文件名
     */
    public static String clearSuffix(String str) {
        int i = str.lastIndexOf(".");
        if (i != -1) {
            return str.substring(0, i);
        }
        return str;
    }

    /**
     * 计算百分比
     */
    public static String getPercent(int n, float total) {
        float rs = (n / total) * 100;
        // 判断是否是正整数
        if (String.valueOf(rs).indexOf(".0") != -1) {
            return String.valueOf((int) rs);
        } else {
            return String.format("%.1f", rs);
        }
    }

    /**
     * 获取文件的后缀名，返回大写
     */
    public static String getSuffix(String str) {
        int i = str.lastIndexOf('.');
        if (i != -1) {
            return str.substring(i + 1).toUpperCase();
        }
        return str;
    }

    /**
     * 修改文件名
     */
    public static String renameFileName(String str) {
        int i = str.lastIndexOf('.');
        if (i != -1) {
            File file = new File(str);
            file.renameTo(new File(str.substring(0, i)));
            return str.substring(0, i);
        }
        return str;
    }

    /**
     * 根据文件路径获取文件目录
     */
    public static String clearFileName(String str) {
        int i = str.lastIndexOf(File.separator);
        if (i != -1) {
            return str.substring(0, i + 1);
        }
        return str;
    }

    /**
     * 根据文件路径获取不带后缀名的文件名
     */
    public static String clearDirectory(String str) {
        int i = str.lastIndexOf(File.separator);
        if (i != -1) {
            return clearSuffix(str.substring(i + 1, str.length()));
        }
        return str;
    }

    /**
     * 格式化毫秒->00:00
     */
    public static String formatSecondTime(int millisecond) {
        if (millisecond == 0) {
            return "00:00";
        }
        millisecond = millisecond / 1000;
        int m = millisecond / 60 % 60;
        int s = millisecond % 60;
        return (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s);
    }

    /**
     * 格式化文件大小 Byte->MB
     */
    public static String formatByteToMB(long l) {
        float mb = l / 1024f / 1024f;
        return String.format("%.1f", mb) + "M";
    }

    /**
     * 格式化文件大小 Byte->KB
     */
    public static String formatByteToKB(int size) {
        float kb = size / 1024f;
        return String.format("%.2f", kb);
    }

    /**
     * 判断目录是否存在，不在则创建
     *
     * @param directoryName 除SD卡路径外的文件夹名称（/smart or /smart/image）
     */
    public static void isExistDirectory(Context context, String directoryName) {
        File file = new File(getCachePath(context) + directoryName);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 判断目录是否存在，不在则创建
     *
     * @param directoryName 除SD卡路径外的文件夹名称（/smart or /smart/image）
     */
    public static void isExistDirectory(Context context, String rootPath, String directoryName) {
        File file = new File(rootPath + directoryName);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 获得SD目录路径
     */
    public static String getSdCardPath() {
        if (isExistSdCard())
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        else
            return Environment.getDataDirectory().getAbsolutePath();
    }

    /**
     * 得到缓存的路径.
     *
     * @return
     */
    public static String getCachePath(Context context) {
        try {
            String path = context.getExternalFilesDir(null).getAbsolutePath();
            if (path != null && !TextUtils.isEmpty(path))
                return path;
            else
                return getSdCardPath();
        } catch (Exception e) {
            e.printStackTrace();
            return getSdCardPath();
        }
    }

    /**
     * 判断文件是否存在
     */
    public static boolean isExistFile(String file) {
        if ("".equals(file)) {
            return false;
        } else {
            return new File(file).exists();
        }

    }

    /**
     * 检查SD卡是否已装载
     */
    public static boolean isExistSdCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 删除文件并删除媒体库中数据
     */
    public static boolean deleteFile(Context context, String filePath) {
        new File(filePath).delete();
        ContentResolver cr = context.getContentResolver();
        int id = -1;
        Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID},
                MediaStore.Audio.Media.DATA + "=?", new String[]{filePath},
                null);
        if (cursor.moveToNext()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        if (id != -1) {
            Log.e(TAG, "---452---");
            return cr.delete(ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id), null,
                    null) > 0;
        }
        return false;
    }

    /**
     * 判断账号格式是否正确(邮箱)
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 判断账号密码格式是否正确(大小写英文字母加数字,6-20位) 只能是英文大小写加数字
     *
     * @return
     */
    public static boolean isPsw(String password) {
        String str = "^[A-Za-z0-9]{6,20}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    /**
     * 判断用户昵称格式是否正确(只能是中文) 只能是英文大小写加数字
     *
     * @return
     */
    public static boolean isName(String name) {
        String str = "^[\u4e00-\u9fa5]{4,8}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(name);
        return m.matches();
    }

    /**
     * 允许输入汉字,字母，数字,下划线
     *
     * @param name
     * @return
     */
    public static boolean checkName(String name) {
        String regex = "[\u4e00-\u9fa5\\w]{1,6}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);
        return m.matches();
    }

    /**
     * 是否是合法字符,数字,汉子,字母.
     *
     * @param string 字符.
     * @param n      字符长度.
     * @return
     */
    public static boolean isLegalchar(String string, int n) {
        if (n == 0) {
            n = 10;
        }
        String str = "[0-9a-zA-Z\u4e00-\u9fa5]{1," + n + "}";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(string);
        return m.matches();
    }

    /**
     * @param name
     * @return
     */
    public static boolean isPhoneNumber(String name) {
        // String str ="1[3|5|7|8|][0-9]{9} ";

        name = name.replace(" ", "");
        String str = "^((13[0-9])|(14[0-9])|(17[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(name);
        return m.matches();
    }

    /**
     * @param word
     * @return
     */
    public static boolean isIllegalWord(String word) {

        String str = ".*[\"|:\\[\\].?<>/].*";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(word);
        return m.matches();
    }

    public static void setListViewHeightBasedOnChildren(ListView view) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = view.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, view);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = totalHeight
                + (view.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight();//获取子项间分隔符占用的高度
        // params.height//最后得到整个ListView完整显示需要的高度
        view.setLayoutParams(params);
    }

    /**
     * 判读当前应用是否开启服务 "com.app.net.service.MediaPlayerService"
     *
     * @param serviceName
     * @return
     */
    public static boolean isRunService(String serviceName) {
        ActivityManager am = (ActivityManager) getContext().getSystemService(
                Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> infos = am
                .getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo info : infos) {
            if (info.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判读当前应用是否开启服务
     *
     * @param clazz
     * @return
     */
    public static boolean isRunService(Class clazz) {
        ActivityManager am = (ActivityManager) getContext().getSystemService(
                Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> infos = am
                .getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo info : infos) {
            if (info.service.getClassName().equals(clazz.getName())) {
                return true;
            }
        }
        return false;
    }





    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    private static List<String> getHomes(Context context) {
        List<String> names = new ArrayList<String>();
        Log.e(TAG, "---550---");
        PackageManager packageManager = context.getPackageManager();
        Log.e(TAG, "---553---");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        Log.e(TAG, "---555---");
        intent.addCategory(Intent.CATEGORY_HOME);
        Log.e(TAG, "---557---");
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        Log.e(TAG, "---560---");
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
            Log.e(TAG, "---564---");
        }
        return names;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param deleteThisPath
     * @param filePath
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                Log.e(TAG, "---574---");
                if (file.isDirectory()) {// 如果下面还有文件
                    File files[] = file.listFiles();
                    Log.e(TAG, "---578---");
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                        Log.e(TAG, "---582---");
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                        Log.e(TAG, "---590---");
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                            Log.e(TAG, "---597---");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "---605---");
            }
        }
    }

    /**
     * 时间格式化
     *
     * @param date
     * @return
     */
    public static String longToString(long date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Log.e(TAG, "---611---");
        // 前面的lSysTime是秒数，先乘1000得到毫秒数，再转为java.util.Date类型
        Date dt = new Date(date);
        Log.e(TAG, "---614---");
        String sDateTime = sdf.format(dt); // 得到精确到秒的表示：08/31/2006 21:08:00
        // Log.i("GroupDetailActivity", "sDateTime"+sDateTime);
        Log.e(TAG, "---617---");
        return sDateTime;
    }

    public static long string2Long(String time, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        Date date = null;
        try {
            date = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

        return date.getTime();
    }

    /**
     * @param date
     * @param format yyyy-MM-dd HH:mm
     * @return
     */
    public static String longToString(long date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        // 前面的lSysTime是秒数，先乘1000得到毫秒数，再转为java.util.Date类型
        Date dt = new Date(date);
        String sDateTime = sdf.format(dt); // 得到精确到秒的表示：08/31/2006 21:08:00
        // Log.i("GroupDetailActivity", "sDateTime"+sDateTime);
        return sDateTime;
    }

    /**
     * 通过assets资源路径获取位图
     *
     * @param path 资源地址
     * @return
     */
    public static Bitmap getBitmap(Context context, String path) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = context.getAssets().open(path);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    /**
     * 通过assets资源路径获取Drawable
     *
     * @param path 资源地址
     * @return
     */
    public static Drawable getDrawable(Context context, String path) {
        Bitmap bitmap = getBitmap(context, path);
        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);

        return drawable;
    }

    /**
     * @param bitmap_bg bg
     * @param bitmap_in src
     * @return 返回与背景相同形状的图片
     */
    public static Bitmap getRoundCornerImage(Bitmap bitmap_bg, Bitmap bitmap_in) {
        int width = (int) (getScreen(getContext())[0] * 0.7);
        int height = (int) (getScreen(getContext())[1] * 0.15);
        Bitmap roundConcerImage = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(roundConcerImage);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, width, height);
        Rect rect2 = new Rect(0, 0, bitmap_in.getWidth(), bitmap_in.getHeight());
        paint.setAntiAlias(true);
        NinePatch patch = new NinePatch(bitmap_bg, bitmap_bg.getNinePatchChunk(), null);
        patch.draw(canvas, rect);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap_in, rect2, rect, paint);
        //间隔
//        Rect rect2 = new Rect(2, 2, 498, 498);
//        canvas.drawBitmap(bitmap_in, rect, rect2, paint);
        return roundConcerImage;
    }

    /**
     * 判断是否是网络路径
     *
     * @param urlString
     * @return
     */
    public static boolean url(String urlString) {
        if (urlString != null) {
            String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            Pattern patt = Pattern.compile(regex);
            Matcher matcher = patt.matcher(urlString);
            boolean isMatch = matcher.matches();
            return isMatch;
        } else {
            return false;
        }
    }

    /**
     * 把assets的文件放入sd卡
     *
     * @param context
     * @param strOutFileName
     * @throws IOException
     */
    public static void copyBigDataToSD(Context context, String strOutFileName)
            throws IOException {
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(strOutFileName);
        myInput = context.getAssets().open("ic_launcher.png");
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
    }

    /**
     * 获取当前版本名称
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {

        StringBuilder builder = new StringBuilder();
        String versionName = "V";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    "cn.net.loveonline.device", 0);
            builder = builder.append("V");
            builder = builder.append(packageInfo.versionName);
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static String getAppVersionCode() {
        return getAppVersionCode(getContext());
    }

    /**
     * 获取当前版本号
     *
     * @return
     */
    public static String getAppVersionCode(Context context) {

        String versionCode = "-1";

        PackageInfo info = null;
        PackageManager manager = context.getPackageManager();

        try {

            info = manager.getPackageInfo(context.getPackageName(), 0);

        } catch (NameNotFoundException e) {

            e.printStackTrace();

        }

        if (info != null) {
            versionCode = String.valueOf(info.versionCode);
        }

        return versionCode;

    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @param context （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @param context  （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param context （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param context （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * md5加密
     *
     * @param text
     * @return
     */
    public static String getMD5encode(String text) {

        MessageDigest digester;
        try {
            digester = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        // 加密后的结果
        byte[] digest = digester.digest(text.getBytes());
        // 把加密后的字符数组转化为字符串.
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            int num = b & 0xff;
            String str = Integer.toHexString(num);
            if (sb.length() == 1) {
                sb.append("0");

            }
            sb.append(str);
        }

        return sb.toString();

    }

    /**
     * 获取某个文件的MD5值
     *
     * @param path
     * @return
     */
    public static String getFileMD5(String path) {
        try {
            // 计算课程表的md5数字摘要。
            MessageDigest digest = MessageDigest.getInstance("md5");
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            byte[] result = digest.digest();
            // 把result的内容 打印出来 16进制
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                int number = b & 0xff;// - 2;//加盐
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取某年某月的天数
     *
     * @param dyear  年
     * @param dmouth 月
     * @return 天数
     */
    public static int getDayOfMonth(String dyear, String dmouth) {
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM");
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        try {
            aCalendar.setTime(simpleDate.parse(dyear + "/" + dmouth));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int day = aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return day;
    }

    /**
     * 读取联系人的信息
     *
     * @param context
     */

    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 头像ID
     **/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**
     * 联系人的ID
     **/
    private static final int PHONES_CONTACT_ID_INDEX = 3;






    /**
     * 查找sdcard卡上的所有歌曲信息
     *
     * @param context
     * @return musicPath 音乐路径 musicName 音乐名
     */
    public static List<Map<String, String>> getMultiData(Context context) {
        List<Map<String, String>> musicList = new ArrayList<Map<String, String>>();
        String musicPath = null;
        String musicName = null;
        // 加入封装音乐信息的代码
        // 查询所有歌曲
        ContentResolver musicResolver = context.getContentResolver();
        Cursor musicCursor = musicResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                null);
        int musicColumnIndex;
        if (null != musicCursor && musicCursor.getCount() > 0) {
            for (musicCursor.moveToFirst(); !musicCursor.isAfterLast(); musicCursor
                    .moveToNext()) {
                Map musicDataMap = new HashMap();
                Random random = new Random();
                int musicRating = Math.abs(random.nextInt()) % 10;
                musicDataMap.put("musicRating", musicRating);
                // 取得音乐播放路径
                musicColumnIndex = musicCursor
                        .getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
                musicPath = musicCursor.getString(musicColumnIndex);
                musicDataMap.put("musicPath", musicPath);
                // 取得音乐的名字
                musicColumnIndex = musicCursor
                        .getColumnIndex(MediaStore.Audio.AudioColumns.TITLE);
                musicName = musicCursor.getString(musicColumnIndex);
                musicDataMap.put("musicName", musicName);

                musicList.add(musicDataMap);

                // // 取得音乐的专辑名称
                // musicColumnIndex =
                // musicCursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM);
                // musicAlbum = musicCursor.getString(musicColumnIndex);
                // musicDataMap.put("musicAlbum", musicAlbum);
                // // 取得音乐的演唱者
                // musicColumnIndex =
                // musicCursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST);
                // musicArtist = musicCursor.getString(musicColumnIndex);
                // musicDataMap.put("musicArtist", musicArtist);
                // // 取得歌曲对应的专辑对应的Key
                // musicColumnIndex =
                // musicCursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_KEY);
                // musicAlbumKey = musicCursor.getString(musicColumnIndex);
                // String[] argArr = { musicAlbumKey };
                // ContentResolver albumResolver = context.getContentResolver();
                // Cursor albumCursor =
                // albumResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                // null,
                // MediaStore.Audio.AudioColumns.ALBUM_KEY + " = ?", argArr,
                // null);
                // if (null != albumCursor && albumCursor.getCount() > 0) {
                // albumCursor.moveToFirst();
                // int albumArtIndex =
                // albumCursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART);
                // musicAlbumArtPath = albumCursor.getString(albumArtIndex);
                // if (null != musicAlbumArtPath &&
                // !"".equals(musicAlbumArtPath)) {
                // musicDataMap.put("musicAlbumImage", musicAlbumArtPath);
                // } else {
                // musicDataMap.put("musicAlbumImage",
                // "file:///mnt/sdcard/alb.jpg");
                // }
                // } else {
                // // 没有专辑定义，给默认图片
                // musicDataMap.put("musicAlbumImage",
                // "file:///mnt/sdcard/alb.jpg");
                // }
                // musicList.add(musicDataMap);
            }
        }
        return musicList;
    }

    public static String getPackgeName(Context context) {

        return context.getPackageName();

    }

    public static boolean isAppRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                 * BACKGROUND=400 EMPTY=500 FOREGROUND=100 GONE=1000
				 * PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
				 */
                Log.i(context.getPackageName(), "此appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()=" + context.getClass().getName());
                if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于后台"
                            + appProcess.processName);
                    return true;
                } else {
                    Log.i(context.getPackageName(), "处于前台"
                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 判断应用是否已经启动
     *
     * @param context 一个context
     * @return boolean
     */
    public static boolean isAppAlive(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> processInfos = activityManager
                .getRunningAppProcesses();
        for (int i = 0; i < processInfos.size(); i++) {
            if (processInfos.get(i).processName
                    .equals(context.getPackageName())) {
                Log.i("NotificationLaunch", String.format(
                        "the %s is running, isAppAlive return true",
                        context.getPackageName()));
                return true;
            }
        }
        Log.i("NotificationLaunch", String.format(
                "the %s is not running, isAppAlive return false",
                context.getPackageName()));
        return false;
    }

    /**
     * 检测是否安装微信
     *
     * @param context
     * @return
     */
    public static boolean isWxInstall(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }
    /**
     * 检测是否安装微信
     *
     * @param context
     * @return
     */
    public static boolean isQQInstall(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }

        return false;
    }
    /**
     * 检测是否安装微信
     *
     * @param context
     * @return
     */
    public static boolean isWBInstall(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.sina.weibo")) {
                    return true;
                }
            }
        }

        return false;
    }


    // public static void startLoadingAnimation(ImageView view) {
    // view.setBackgroundResource(R.anim.progress_dialog);
    // AnimationDrawable animation = (AnimationDrawable) view.getBackground();
    // animation.start();
    // }

    /**
     * 获取sd卡总空间
     *
     * @return
     */
    public static long getSdAllSize() {

        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getBlockCount();
        return availableBlocks * blockSize / 1024 / 1024;

    }

    /**
     * 获取sd卡可用空间
     *
     * @return
     */
    public static long getSdAvailaleSize() {

        File path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize / 1024 / 1024;

        // (availableBlocks * blockSize)/1024 /1024 MIB单位

    }

    /**
     * 获取文件夹大小 (MB)
     *
     * @author Tom 2015-12-7
     */
    public static long getFileSize(String path) {
        long size = 0;
        File file = new File(path);
        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                size = size + getFileSize(fileList[i].getAbsolutePath());
            } else {
                size = size + fileList[i].length();
            }
        }

        size = size / 1024 / 1024;
        return size;
    }

    /**
     * 服务于下面的 getImage
     *
     * @param context
     * @param filePath
     * @return
     */
    private static Cursor getCursor(Context context, String filePath) {
        String path = null;
        Cursor c = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (c.moveToFirst()) {
            do {
                // 通过Cursor 获取路径，如果路径相同则break；
                Log.e("@chenzhen@", "////////" + filePath);
                path = c.getString(c
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                Log.e("@chenzhen@", "?????????" + path);
                // 查找到相同的路径则返回，此时cursorPosition 便是指向路径所指向的Cursor 便可以返回了
                if (path.equals(filePath)) {
                    // Log.e("audioPath = " + path);
                    // Log.e("filePath = " + filePath);
                    // cursorPosition = c.getPosition();
                    break;
                }
            } while (c.moveToNext());
        }
        // 这两个没有什么作用，调试的时候用
        // String audioPath = c.getString(c
        // .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
        //
        // Log.e("audioPath = " + audioPath);
        return c;
    }

    /**
     * 服务于下面的 getImage
     *
     * @param context
     * @param album_id
     * @return
     */
    private static String getAlbumArt(Context context, int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = context.getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),
                projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
    }

    /**
     * 获取音乐图片里面的图片
     *
     * @param context
     * @param filePath
     * @return
     */
    public static BitmapDrawable getImage(Context context, String filePath) {
        Cursor currentCursor = getCursor(context, filePath);
        int album_id = currentCursor.getInt(currentCursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        String albumArt = getAlbumArt(context, album_id);
        Bitmap bm = null;
        if (albumArt == null) {
            return null;
        } else {
            bm = BitmapFactory.decodeFile(albumArt);
            BitmapDrawable bmpDraw = new BitmapDrawable(bm);
            return bmpDraw;
        }
    }

    public static String jsonFormat(String fields) {
        if (fields.contains("\\u003d")) {
            fields = fields.replaceAll("\\u003d", "=");
        }
        return fields;
    }

    /**
     * 得到amr的时长
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static int getAmrDuration(File file) throws IOException {
        long duration = -1;
        int[] packedSize = {12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0,
                0, 0};
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            long length = file.length();// 文件的长度
            int pos = 6;// 设置初始位置
            int frameCount = 0;// 初始帧数
            int packedPos = -1;
            // ///////////////////////////////////////////////////
            byte[] datas = new byte[1];// 初始数据值
            while (pos <= length) {
                randomAccessFile.seek(pos);
                if (randomAccessFile.read(datas, 0, 1) != 1) {
                    duration = length > 0 ? ((length - 6) / 650) : 0;
                    break;
                }
                packedPos = (datas[0] >> 3) & 0x0F;
                pos += packedSize[packedPos] + 1;
                frameCount++;
            }
            // ///////////////////////////////////////////////////
            duration += frameCount * 20;// 帧数*20
        } finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
        return (int) (duration / 1000);
    }



    public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // drawable转换成bitmap
        Bitmap oldbmp = drawableToBitmap(drawable);
        // 创建操作图片用的Matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放比例
        float sx = ((float) w / width);
        float sy = ((float) h / height);
        // 设置缩放比例
        matrix.postScale(sx, sy);
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        return new BitmapDrawable(newbmp);
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                : Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * @param timeMillis
     * @return 是否是今天
     */
    public static boolean isToday(long timeMillis) {
        Date date = new Date(timeMillis);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        Calendar today = Calendar.getInstance();

        if (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && cal.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && cal.get(Calendar.DATE) == today.get(Calendar.DATE)) {
            return true;
        }

        return false;
    }



    /**
     * 计算指定时间与当前的时间差
     *
     * @param oldTime 某一指定时间
     * @param nowTime 现在时间
     * @author Tom 2015-10-26
     */
    public static String compareCurrentTime(long nowTime, long oldTime) {
        String result = "";
        int dif = (int) (nowTime - oldTime) / 1000;

        if (dif < 30) {
            result = "10秒内";
        } else if (dif < 60) {
            result = "30秒内";
        } else if (dif >= 60 && dif < 60 * 60) {
            result = dif / 60 + "分钟内";
        } else if (dif >= 60 * 60 && dif < 60 * 60 * 24) {
            result = dif / (60 * 60) + 1 + "小时内";
        } else {
            result = "1天以上";
        }

        return result;

    }



    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
                Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        // 设置颜色矩阵的饱和度，0是灰色的，1是原图(也可设置其它参数)
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static String formatDatetime(long formatTime) {
        return formatDatetime(formatTime, "yyyy-MM-dd HH:mm");
    }

    /**
     * 判断当前时间 是今天还是昨天
     *
     * @param formatTime
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String formatDatetime(long formatTime, String formatStr) {
        String time = longToString(formatTime, formatStr);
        SimpleDateFormat format = new SimpleDateFormat(formatStr);

        if (time == null || "".equals(time)) {
            return "";
        }

        Date date = null;

        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar curren = Calendar.getInstance();

        Calendar today = Calendar.getInstance();

        today.set(Calendar.YEAR, curren.get(Calendar.YEAR));
        today.set(Calendar.MONTH, curren.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, curren.get(Calendar.DAY_OF_MONTH));
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();

        yesterday.set(Calendar.YEAR, curren.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, curren.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH,
                curren.get(Calendar.DAY_OF_MONTH) - 1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        curren.setTime(date);
        if (curren.after(today)) {
            return "今天 " + time.split(" ")[1];
        } else if (curren.before(today) && curren.after(yesterday)) {
            return "昨天 " + time.split(" ")[1];
        } else {
            if (isSameDateOfYear(date, new Date())) {
                return time.substring(5, time.length());
            } else {
                return time;
            }
        }
    }

    /**
     * 判断两个日期是否是同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDate(Date date1, Date date2) {
        if (date1 == null || date2 == null)
            return false;

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }

    /**
     * 判断两个日期是否是同一年
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDateOfYear(Date date1, Date date2) {
        if (date1 == null || date2 == null)
            return false;

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
    }

    public static Date getDate(String time) {
        if (TextUtils.isEmpty(time))
            return null;

        return new Date(string2Long(time, "yyyy-MM-dd HH:mm:ss"));
    }

    public static Date getDate(String time, String format) {
        if (TextUtils.isEmpty(time) || TextUtils.isEmpty(format))
            return null;

        return new Date(string2Long(time, format));
    }

    /**
     * 返回上一天的整点信息
     *
     * @param date
     * @return XXXX-XX-XX 00:00:00
     */
    public static Date lastDayWholePointDate(Date date) {

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        if ((gc.get(gc.HOUR_OF_DAY) == 0) && (gc.get(gc.MINUTE) == 0)
                && (gc.get(gc.SECOND) == 0)) {
            return new Date(date.getTime() - (24 * 60 * 60 * 1000));
        } else {
            Date date2 = new Date(date.getTime() - gc.get(gc.HOUR_OF_DAY) * 60 * 60
                    * 1000 - gc.get(gc.MINUTE) * 60 * 1000 - gc.get(gc.SECOND)
                    * 1000 - 24 * 60 * 60 * 1000);
            return date2;
        }

    }

    /**
     * 判断当前时间 是今天还是昨天
     *
     * @param formatTime
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String formatDatetime(String formatTime) {
        String time = formatTime;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        if (time == null || "".equals(time)) {
            return "";
        }

        Date date = null;

        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar curren = Calendar.getInstance();

        Calendar today = Calendar.getInstance();

        today.set(Calendar.YEAR, curren.get(Calendar.YEAR));
        today.set(Calendar.MONTH, curren.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, curren.get(Calendar.DAY_OF_MONTH));
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();

        yesterday.set(Calendar.YEAR, curren.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, curren.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH,
                curren.get(Calendar.DAY_OF_MONTH) - 1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        curren.setTime(date);
        if (curren.after(today)) {
            return "今天 " + time.split(" ")[1];
        } else if (curren.before(today) && curren.after(yesterday)) {
            return "昨天 " + time.split(" ")[1];
        } else {
            return time;
        }
    }

    /**
     * 获取上下文
     *
     * @return
     */
    public static Context getContext() {
        return MyApplication.getInstance();
    }


    public static Bitmap decodeUriAsBitmap(@NonNull Uri uri) {

        Bitmap bitmap = null;
        try {

            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 4;
            //这里一定要将其设置回false，因为之前我们将其设置成了true
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(inputStream, null, opts);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * GPS开关
     *
     * @param context 状态判断 secureClass =
     *                cl.loadClass("android.provider.Settings$Secure"); isMethod =
     *                secureClass.getMethod("isLocationProviderEnabled",
     *                ContentResolver.class, String.class); Boolean ret = (Boolean)
     *                isMethod.invoke(secureClass, this.getContentResolver(),
     *                "gps");
     */
    public static void toggleGPS(Context context) {
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        gpsIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, gpsIntent, 0).send();
        } catch (CanceledException e) {
            e.printStackTrace();
        }
    }

    /**
     * 避免双击, 双击返回true 间隔时间 1000毫秒
     *
     * @author Tom 2015-12-15
     */
    public static boolean isDoubleOnClick() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime < 500) {
            lastClickTime = currentTime;
            return true;
        } else {
            lastClickTime = currentTime;
            return false;
        }
//		return false;
    }

    /**
     * kb 转 mb
     *
     * @param kb
     * @return
     */
    public static String kbToMb(long kb) {
        double total = kb / ((double) 1024 * 1024);
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(total);
    }

    /**
     * 根据质量压缩图片
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static BitmapDrawable resIdToBitmap(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        options.inPreferredConfig = Config.RGB_565;
        options.inSampleSize = computeInitialSampleSize(options, 600, (int) (1 * 1024 * 1024));
        options.inJustDecodeBounds = false;

        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;

        // 获取资源图片

        InputStream is = context.getResources().openRawResource(resId);

        Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);

        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new BitmapDrawable(context.getResources(), bitmap);
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }


    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    /**
     * 手机号码格式化
     *
     * @param tel
     * @return
     */
    public static String fomartTelephone(String tel) {
        if (tel.contains(" ")) {
            tel = tel.replaceAll(" ", "");
        }
        if (tel.contains("+86")) {
            tel = tel.replaceAll("\\+86", "");
        }
        if (tel.contains("-")) {
            tel = tel.replaceAll("-", "");
        }
        return tel;

    }

    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                        if (resultData == null) {
                            resultData = String.valueOf(applicationInfo.metaData.getInt(key));
                        }
                    }
                }
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }


        return resultData;
    }

    /**
     * 判断字符串中是否包含Emoji表情
     *
     * @param str
     * @return
     */
    public static boolean containsEmoji(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (isEmojiCharacter(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前字符是否是Emoji表情
     *
     * @param codePoint
     * @return
     */
    public static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }


    /**
     * Emoji表情过滤器
     */
    public static InputFilter emojiFilter = new InputFilter() {
        Pattern emoji = Pattern.compile(
                "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);


        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                   int dstart, int dend) {


            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                Toast.makeText(getContext(), "不支持Emoji", Toast.LENGTH_SHORT).show();
                return "";
            }
            return null;
        }
    };


    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用
     *
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    public static void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = getContext().getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 去除最后面的 .0
     * 2016-12-16 16:02:25.0
     *
     * @param time
     * @return
     */
    public static String clearTimeLast(String time) {

        if (time.length() > 2) {
            return time.substring(0, time.length() - 2);
        } else {
            return "";
        }
    }


    /*打开自启动管理页    start*/
    public static void openAutoStartSetting(Context context) {
        String system = Build.BRAND;

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName comp = null;
        if (system.equals(SYS_EMUI)) {//华为
            comp = ComponentName
                    .unflattenFromString("com.huawei.systemmanager/.optimize.process.ProtectActivity");
        } else if (system.equals(SYS_MIUI)) {//小米
            comp = ComponentName
                    .unflattenFromString("com.miui.securitycenter/com.miui.permcenter.autostart.AutoStartManagementActivity");
        } else if (system.contains(SYS_VIVO)) {//vivo
            comp = ComponentName
                    .unflattenFromString("com.iqoo.secure/.ui.phoneoptimize.AddWhiteListActivity");
        } else if (system.contains(SYS_OPPO)) {//oppo  未测试
            comp = ComponentName
                    .unflattenFromString("com.coloros.oppoguardelf/com.coloros.powermanager.fuelgaue.PowerUsageModelActivity");
        } else if (system.contains(SYS_FLYME)) {//魅族
            comp = ComponentName
                    .unflattenFromString("com.meizu.safe/.permission.SmartBGActivity");
        } else if (system.contains(SYS_SANXING)) {//三星 不可行
            comp = ComponentName
                    .unflattenFromString("com.samsung.android.sm/.ui.ram.RamActivity");
        } else if (system.contains(SYS_SONY)) {//
            comp = ComponentName
                    .unflattenFromString("com.android.settings/.SubSettings");
        }
        intent.setComponent(comp);

        try {
            context.startActivity(intent);
        } catch (Exception e) {//抛出异常就直接打开设置页面
            intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }
    }
    //  查看 打开的页面类名.
    //    adb shell dumpsys activity | find "mFoc"

    public static final String SYS_EMUI = "HONOR"; //华为
    public static final String SYS_MIUI = "Xiaomi"; //小米
    public static final String SYS_FLYME = "Meizu"; //魅族
    public static final String SYS_VIVO = "vivo"; //vivo
    public static final String SYS_OPPO = "oppo"; //oppo
    public static final String SYS_SANXING = "samsung"; //三星
    public static final String SYS_SONY = "Sony"; //索尼

     /*打开自启动管理页    end*/

    public static boolean isJson(String json) {
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

    /**
     * 日期偏移
     *
     * @param offset 偏移天数
     * @param format 转化格式
     * @return
     */
    public static String dateOffset(int offset, String format) {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, offset);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(date);

        return dateString;
    }


    /**
     * 读取本地文件中JSON字符串
     *
     * @param fileName
     * @return
     */
    public static String getJson(String fileName, Context mContext) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    mContext.getAssets().open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }




    public static String getContentPath(String root, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String path = uri.getEncodedPath();
            final int splitIndex = path.indexOf('/', 1);
            path = Uri.decode(path.substring(splitIndex + 1));
            return root.concat(path);
        } else {
            return uri.getPath();
        }
    }


    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static int getNavigationBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        return resources.getDimensionPixelSize(resourceId);
    }

    public static boolean navigationBarExist(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }


    /*****************************************************************************************/


    /**
     * 中文数字 转 阿拉伯数字.
     *
     * @param text
     * @return
     */
    public static String bulidTextZHToALB(String text) {
        Pattern p = Pattern.compile(numRegex);
        Matcher m = p.matcher(text);

        while (m.find()) {
            String numZH = m.group();
            if (numZH.length() != 1 || numMap.containsKey(numZH) || zhTen.equals(numZH)) {
                String numALB = NumZHToALB(numZH);
                text = text.replaceFirst(numZH, numALB);
            }
        }

        return text;
    }

    private static String NumZHToALB(String numZH) {
        int numALB = 0;
        int formIndex = 0;
        for (String unitNum : unitNumMap.keySet()) {
            int index = numZH.indexOf(unitNum);
            if (index != -1) {
                numALB += NumZHToALB(numZH.substring(formIndex, index), unitNumMap.get(unitNum));
                formIndex = index + 1;
            }
        }

        numALB += NumZHToALB(numZH.substring(formIndex), 1);
        return String.valueOf(numALB);
    }

    private static int NumZHToALB(String numZH, int unitNum) {
        int length = numZH.length();
        int numALB = 0;
        if (length != 0) {
            int fromIndex = 0;
            for (String unit : unitMap.keySet()) {
                int index = numZH.indexOf(unit, fromIndex);
                if (index != -1) {
                    fromIndex = index + unit.length();
                    String prevChar = zhOne;
                    if (index != 0 && numMap.containsKey(prevChar)) {
                        prevChar = String.valueOf(numZH.charAt(index - 1));
                    }
                    numALB += numMap.get(prevChar) * unitMap.get(unit);
                }
            }

            String lastChar = String.valueOf(numZH.charAt(length - 1));
            if (numMap.containsKey(lastChar)) {
                String pChar = zhTen;
                if (length != 1) {
                    pChar = String.valueOf(numZH.charAt(length - 2));
                    if (zhZero.equals(pChar)) {
                        pChar = zhTen;
                    }
                }
                numALB += numMap.get(lastChar) * unitMap.get(pChar) / 10;
            }
        }

        return numALB * unitNum;
    }

    private static String encodeUnicode(String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int i : utfBytes) {
            String hexB = Integer.toHexString(i);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }

    private static final String zhZero = "零";
    private static final String zhOne = "一";
    private static final String zhTen = "十";

    private static final Map<String, Integer> numMap = new HashMap<String, Integer>();

    static {
        numMap.put("零", 0);
        numMap.put("一", 1);
        numMap.put("二", 2);
        numMap.put("三", 3);
        numMap.put("四", 4);
        numMap.put("五", 5);
        numMap.put("六", 6);
        numMap.put("七", 7);
        numMap.put("八", 8);
        numMap.put("九", 9);
    }

    private static final Map<String, Integer> unitNumMap = new LinkedHashMap<String, Integer>();

    static {
        unitNumMap.put("亿", 100000000);
        unitNumMap.put("万", 10000);
    }

    private static final Map<String, Integer> unitMap = new LinkedHashMap<String, Integer>();

    static {
        unitMap.put("千", 1000);
        unitMap.put("百", 100);
        unitMap.put("十", 10);
    }

    private static String numRegex;

    static {
        numRegex = "[";
        for (String s : numMap.keySet()) {
            numRegex += encodeUnicode(s);
        }
        for (String s : unitMap.keySet()) {
            numRegex += encodeUnicode(s);
        }
        for (String s : unitNumMap.keySet()) {
            numRegex += encodeUnicode(s);
        }
        numRegex += "]+";
    }

    /*****************************************************************************************/


    /**
     * 提取固定格式字符串中的数字，整数、double、负数都可以
     *
     * @param ptCasinoMsg
     * @return
     */
    public static String[] extractAmountMsg(String ptCasinoMsg) {
        String returnAmounts[] = new String[4];
        if (!ptCasinoMsg.equals("")) {
            ptCasinoMsg = ptCasinoMsg.replace(" | ", " ");
            String[] amounts = ptCasinoMsg.split(" ");
            for (int i = 0; i < amounts.length; i++) {
                String msgAmount = amounts[i];
                String numFlag = "";
                if (msgAmount.contains("-")) {
                    numFlag = "-";
                }
                Pattern p = Pattern.compile("(\\d+\\.\\d+)");
                Matcher m = p.matcher(amounts[i]);
                if (m.find()) {
                    returnAmounts[i] = m.group(1) == null ? "" : numFlag + m.group(1);
                } else {
                    p = Pattern.compile("(\\d+)");
                    m = p.matcher(amounts[i]);
                    if (m.find()) {
                        returnAmounts[i] = m.group(1) == null ? "" : numFlag + m.group(1);
                    }
                }
            }
        } else {
            returnAmounts[0] = "0";
            returnAmounts[1] = "0d";
            returnAmounts[2] = "0d";
            returnAmounts[3] = "0d";
        }

        return returnAmounts;
    }

    /**
     * 获取第一个可选分隔符
     *
     * @param source
     * @param spliChars
     * @return
     */
    public static String getMinIndex(String source, String[] spliChars) {
        int index = source.length() - 1;
        String sc = "";
        for (String ch : spliChars) {
            int temp = source.indexOf(ch);
            if (temp > -1 && temp < index) {
                index = temp;
                sc = ch;
            }
        }
        return sc.length() > 0 ? sc.concat("_").concat(String.valueOf(index)) : String.valueOf(index);

    }

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * Compress image by pixel, this will modify image width/height.
     * Used to get thumbnail
     *
     * @param imgPath image path
     * @param pixelW  target pixel of width
     * @param pixelH  target pixel of height
     * @return
     */
    public static Bitmap compressBitmap(String imgPath, float pixelW, float pixelH, boolean isCover) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 想要缩放的目标尺寸
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        int degree = readPictureDegree(imgPath);


        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);

        if (degree > 0)
            bitmap = rotaingImageView(degree, bitmap);

        saveBitmap(imgPath, bitmap);
        // 压缩好比例大小后再进行质量压缩
//        return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    public static void saveBitmap(String savePath, Bitmap bitmap) {
        Log.e(TAG, "保存图片");
        File f = new File(savePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 设置屏幕亮度，只在当前界面有效
     *
     * @param context    activity
     * @param brightness 亮度
     */
    public static void setScreenLight(Activity context, int brightness) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        context.getWindow().setAttributes(lp);
    }

    /*********************************************************************************************/


    /**
     * Is the live streaming still available
     *
     * @return is the live streaming is available
     */
    public static boolean isLiveStreamingAvailable() {
        // Todo: Please ask your app server, is the live streaming still available
        return false;
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static <T> void writeObjectToFile(List<T> list, File file) {
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(list);
            objOut.flush();
            objOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> readObjectFromFile(File file) {
        FileInputStream in;
        List<T> object = null;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn = new ObjectInputStream(in);
            object = (List<T>) objIn.readObject();
            objIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (List<T>) object;
    }

    // <------------------身份证格式的正则校验----------------->
    public static boolean verForm(String num) {
        String reg = "^\\d{15}$|^\\d{17}[0-9Xx]$";
        if (!num.matches(reg)) {
            System.out.println("Format Error!");
            return false;
        }
        return true;
    }

    //<------------------身份证最后一位的校验算法----------------->
    public static boolean verify(char[] id) {
        int sum = 0;
        int w[] = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] ch = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        for (int i = 0; i < id.length - 1; i++) {
            sum += (id[i] - '0') * w[i];
        }
        int c = sum % 11;
        char code = ch[c];
        char last = id[id.length - 1];
        last = last == 'x' ? 'X' : last;
        return last == code;
    }

    // 1-17位相乘因子数组
    final static int[] FACTOR = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    //效验码
    final static char[] PARITYBIT = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    /**
     * 验证身份证号码
     *
     * @param no 身份证号码
     * @return true or false
     */
    public static boolean validateIdentityCard(String no) {
        if (TextUtils.isEmpty(no)) return false;

        if (no.length() == 15) {
            //第一代身份证正则表达式(15位)
            no = conversionIdentityCard(no);
        }

        // 对身份证号进行长度等简单判断
        if (no.length() != 18 || !no.matches("\\d{17}[0-9X]")) {
            return false;
        }

        // 18位随机码数组
        char[] random = "10X98765432".toCharArray();
        // 计算1-17位与相应因子乘积之和
        int total = 0;
        for (int i = 0; i < 17; i++) {
            total += Character.getNumericValue(no.charAt(i)) * FACTOR[i];
        }
        // 判断随机码是否相等
        return random[total % 11] == no.charAt(17);
    }

    /**
     * 将15位身份证号码转换为18位
     *
     * @param no
     * @return
     */
    private static String conversionIdentityCard(String no) {
        String id17 = no.substring(0, 6) + "19" + no.substring(6);

        int power = 0;
        char[] cs = id17.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            power += (cs[i] - '0') * FACTOR[i];
        }

        // 将前17位与第18位校验码拼接
        return id17 + String.valueOf(PARITYBIT[power % 11]);
    }

    /**
     * 设置文字到剪切板
     */
    public static void setTextToClipboard(String text ){
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", text);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }


    /**
     * 规则2：至少包含大小写字母及数字中的两种
     * 是否包含
     *  12 到18位
     * @param str
     * @return
     */
    public static boolean isPwdRule(String str) {
        if (TextUtils.isEmpty(str)){
            return false;
        }
        if (str.length() < 6 || str.length() > 18){
            return false;
        }
        return true;


//        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
//        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
//        for (int i = 0; i < str.length(); i++) {
//            if (Character.isDigit(str.charAt(i))) {   //用char包装类中的判断数字的方法判断每一个字符
//                isDigit = true;
//            } else if (Character.isLetter(str.charAt(i))) {  //用char包装类中的判断字母的方法判断每一个字符
//                isLetter = true;
//            }
//        }
//        String regex = "^[a-zA-Z0-9]+$";
//        boolean isRight = isDigit && isLetter && str.matches(regex);
//        return isRight;
    }

    /**
     * check if the network connected or not
     * @param context context
     * @return true: connected, false:not, null:unknown
     */
    public static Boolean isNetworkConnected(Context context) {
        try {
            context = context.getApplicationContext();
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 格式化数字
     * @param total
     */
    public static String  formatNum(String total) {
        //283849  28万
        //18765 1.8万
        //8757  8757
        if (TextUtils.isEmpty(total)){
            return "0";
        }
        int num = Integer.parseInt(total);
        if (num > 20000){
            return num/10000 + "万";
        }else if (num > 10000){
            double data = num/10000.0;
            DecimalFormat df = new DecimalFormat("#.0");
            String s = df.format(data) + "万";
            return s.replace(",",".");
        }else {
            return String.valueOf(num);
        }

    }

    public static String listToString(List<IndentInfo> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (IndentInfo string : stringList) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }
}
