package com.kaadas.lock.utils.cachefloder;

/**
* 缓存到本地文件夹
 * 格式要求全部以read和write开头
*@author FJH
*@company kaadas
*created at 2019/3/5 11:05
*/
public class CacheFloder {

    //手势密码保存:key:uid+"handPassword"
    public static void writeHandPassword(ACache cache,String key,String value){
        cache.put(key, value);
    }

    //手势密码读取
    public static String readHandPassword(ACache cache,String key){
        return  cache.getAsString(key);
    }

    //是否开启手机指纹密码的状态
    public static void writePhoneFingerPrint(ACache cache,String key,String value){
        cache.put(key,value);
    }
    //是否开启手机指纹密码的状态
    public static String readPhoneFingerPrint(ACache cache,String key){
        return  cache.getAsString(key);
    }

    //缓存当前系统语言
    public static void writeLanguage(ACache cache,String key, String value){
        cache.put(key,value);
    }
    //读取当前系统的语言
    public static String readLanguage(ACache cache,String key){
        return cache.getAsString(key);
    }

    //保存常见问题
    public static void  writeFAQ(ACache cache, String key, String  value){
        cache.put(key,value);
    }

    // 读取常见问题
    public static String readFAQ(ACache cache, String key){
        return cache.getAsString(key);
    }

}
