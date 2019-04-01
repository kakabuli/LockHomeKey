package com.kaadas.lock.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class SPUtils {
    private static Context context;

    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "share_data";
    public static final String PROTECT_FILE_NAME = "protect_share_data";
    //保存token  uid等信息
    public static final String LOGIN_RESULT = "LoginResult";
    //保存优先级高的设备名称
    public static final String HIGH_PRIORITY_DEVICE_NAME = "HighPriorityDeviceName";
    //保存开锁密码的前缀
    public static final String LOCK_PWD = "lock_pwd";
    //token
    public static final String TOKEN = "token";
    //uid
    public static final String UID = "uid";
    //phone
    public static final String PHONEN="phone";
    //username
    public static final String USERNAME="username";
    //pwd
    public static final String PASSWORD="password";
    //update
    public static final String APPUPDATE="appUpdate";

    //消息免打扰状态
    public static final String MESSAGE_STATUS="messageStatus";

    //上次获取锁的开锁记录的时间的  key   蓝牙模块mac地址+此key
    public static final String LAST_OPEN_LOCK_RECORD_TIME = "last_open_lock_record_time";

    public static void init(Context context2) {
        context = context2;
    }
    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * @param key
     * @param object
     */
    public static void put(String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    public static void putProtect(String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(PROTECT_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 保存byte数组
     */
    public static void putByte(Context cnt, byte[] data, String key) {
        SharedPreferences sharedPreferences = cnt.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String imageString = new String(Base64.encode(data, Base64.DEFAULT));
        editor.putString(key, imageString);
        editor.commit();
    }

    /**
     * 取出byte数组
     */
    public static byte[] getByte(Context cnt, String key) {
        SharedPreferences sharedPreferences = cnt.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        String string = sharedPreferences.getString(key, "");
        byte[] b = Base64.decode(string.getBytes(), Base64.DEFAULT);
        return b;
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(String key, Object defaultObject) {

        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    public static Object getProtect(String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(PROTECT_FILE_NAME, Context.MODE_PRIVATE);
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public static void remove(String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }


    /**
     * 清除所有数据
     */
    public static void clear() {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        if (!editor.commit()) { //如果commit失败则使用apply
            SharedPreferencesCompat.apply(editor);
        }
    }


    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    public static boolean contains(String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.contains(key);
    }


    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getAll();
    }


    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {

        private static final Method sApplyMethod = findApplyMethod();


        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }
            return null;
        }


        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }
}
