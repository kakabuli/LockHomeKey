package com.kaadas.lock.utils;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class SPUtils2 {
	
	/**
	 * 保存在手机里面的文件名
	 */
	public static final String FILE_NAME = "share_data2";
	public static final String PROTECT_FILE_NAME = "protect_share_data2";



	
	/**
	 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 *
	 * @param context
	 * @param key
	 * @param object
	 */
	public static void put(Context context, String key, Object object) {
        /*SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();*/
		if (object instanceof String) {
//            editor.putString(key, (String) object);
			MMKVUtils.setMMKV(FILE_NAME,key,(String) object);
		} else if (object instanceof Integer) {
//            editor.putInt(key, (Integer) object);
			MMKVUtils.setMMKV(FILE_NAME,key,(int) object);
		} else if (object instanceof Boolean) {
//            editor.putBoolean(key, (Boolean) object);
			MMKVUtils.setMMKV(FILE_NAME,key,(boolean) object);
		} else if (object instanceof Float) {
//            editor.putFloat(key, (Float) object);
			MMKVUtils.setMMKV(FILE_NAME,key,(float) object);
		} else if (object instanceof Long) {
//            editor.putLong(key, (Long) object);
			MMKVUtils.setMMKV(FILE_NAME,key,(long) object);
		} else {
//            editor.putString(key, object.toString());
			MMKVUtils.setMMKV(FILE_NAME,key,object.toString());
		}
//        SharedPreferencesCompat.apply(editor);
	}
	public static void putProtect(Context context, String key, Object object) {
//        SharedPreferences sp = context.getSharedPreferences(PROTECT_FILE_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
		if (object instanceof String) {
			MMKVUtils.setMMKV(PROTECT_FILE_NAME,key,(String) object);
		} else if (object instanceof Integer) {
//            editor.putInt(key, (Integer) object);
			MMKVUtils.setMMKV(PROTECT_FILE_NAME,key,(int) object);
		} else if (object instanceof Boolean) {
//            editor.putBoolean(key, (Boolean) object);
			MMKVUtils.setMMKV(PROTECT_FILE_NAME,key,(boolean) object);
		} else if (object instanceof Float) {
//            editor.putFloat(key, (Float) object);
			MMKVUtils.setMMKV(PROTECT_FILE_NAME,key,(float) object);
		} else if (object instanceof Long) {
//            editor.putLong(key, (Long) object);
			MMKVUtils.setMMKV(PROTECT_FILE_NAME,key,(long) object);
		} else {
//            editor.putString(key, object.toString());
			MMKVUtils.setMMKV(PROTECT_FILE_NAME,key,object.toString());
		}
//        SharedPreferencesCompat.apply(editor);
	}
	/**
	 * 保存byte数组
	 */
	public static void putByte(Context cnt,byte [] data,String key){
        /*SharedPreferences sharedPreferences = cnt.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String imageString = new String(Base64.encode(data, Base64.DEFAULT));
        editor.putString(key, imageString);
        editor.commit();*/
		MMKVUtils.setMMKV(FILE_NAME,key,data);
	}
	/**
	 * 取出byte数组
	 */
	public static byte[] getByte(Context cnt,String key){
        /*SharedPreferences sharedPreferences = cnt.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        String string = sharedPreferences.getString(key, "");
        byte[] b = Base64.decode(string.getBytes(), Base64.DEFAULT);
        return b;*/
		return MMKVUtils.getBytesMMKV(FILE_NAME,key);
	}
	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 *
	 * @param context
	 * @param key
	 * @param defaultObject
	 * @return
	 */
	public static Object get(Context context, String key, Object defaultObject) {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		if (defaultObject instanceof String) {
//            return sp.getString(key, (String) defaultObject);
			return MMKVUtils.getStringMMKV(FILE_NAME,key,(String) defaultObject);
		} else if (defaultObject instanceof Integer) {
//            return sp.getInt(key, (Integer) defaultObject);
			return MMKVUtils.getIntMMKV(FILE_NAME,key,(int)defaultObject);
		} else if (defaultObject instanceof Boolean) {
//            return sp.getBoolean(key, (Boolean) defaultObject);
			return MMKVUtils.getBoolMMKV(FILE_NAME,key,(boolean)defaultObject);
		} else if (defaultObject instanceof Float) {
//            return sp.getFloat(key, (Float) defaultObject);
			return MMKVUtils.getFloatMMKV(FILE_NAME,key,(float)defaultObject);
		} else if (defaultObject instanceof Long) {
//            return sp.getLong(key, (Long) defaultObject);
			return MMKVUtils.getLongMMKV(FILE_NAME,key,(long)defaultObject);
		}
		return null;
	}
	public static Object getProtect(Context context, String key, Object defaultObject) {
//        SharedPreferences sp = context.getSharedPreferences(PROTECT_FILE_NAME, Context.MODE_PRIVATE);
		if (defaultObject instanceof String) {
//            return sp.getString(key, (String) defaultObject);
			return MMKVUtils.getStringMMKV(PROTECT_FILE_NAME,key,(String) defaultObject);
		} else if (defaultObject instanceof Integer) {
//            return sp.getInt(key, (Integer) defaultObject);
			return MMKVUtils.getIntMMKV(PROTECT_FILE_NAME,key,(int)defaultObject);
		} else if (defaultObject instanceof Boolean) {
//            return sp.getBoolean(key, (Boolean) defaultObject);
			return MMKVUtils.getBoolMMKV(PROTECT_FILE_NAME,key,(boolean)defaultObject);
		} else if (defaultObject instanceof Float) {
//            return sp.getFloat(key, (Float) defaultObject);
			return MMKVUtils.getFloatMMKV(PROTECT_FILE_NAME,key,(float)defaultObject);
		} else if (defaultObject instanceof Long) {
//            return sp.getLong(key, (Long) defaultObject);
			return MMKVUtils.getLongMMKV(PROTECT_FILE_NAME,key,(long)defaultObject);
		}
		return null;
	}
	
	/**
	 * 移除某个key值已经对应的值
	 *
	 * @param context
	 * @param key
	 */
	public static void remove(Context context, String key) {
		MMKVUtils.removeKeyById(FILE_NAME,key);
		/*SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.commit();
		SharedPreferencesCompat.apply(editor);*/
	}
	
	
	/**
	 * 清除所有数据
	 *
	 * @param context
	 */
	public static void clear(Context context) {
		MMKVUtils.clearAllById(FILE_NAME);
		/*SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		if (!editor.commit()){ //如果commit失败则使用apply
			editor.commit();
			SharedPreferencesCompat.apply(editor);
		}*/
	}
	
	
	/**
	 * 查询某个key是否已经存在
	 *
	 * @param context
	 * @param key
	 * @return
	 */
	public static boolean contains(Context context, String key) {
		/*SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		return sp.contains(key);*/
		return MMKVUtils.containsKeyById(FILE_NAME,key);
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
