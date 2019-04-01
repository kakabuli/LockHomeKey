package com.kaadas.lock.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create By lxj  on 2019/1/9
 * Describe
 */
public class MatcherUtil {

    /**
     * 验证邮箱输入是否合法
     *
     * @param
     * @return
     */
    public static boolean isEmail(String email) {

        boolean flag = false;
        if (TextUtils.isEmpty(email)){
            flag = false;
        }
        try {
            String check = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证是否是手机号码
     *
     * @param str
     * @return
     */
    public static boolean isMobile(String str) {
        Pattern pattern = Pattern.compile("1[0-9]{10}");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isBleMac(String mac){

        Pattern pattern = Pattern.compile("[0-9a-zA-Z]{2}:[0-9a-zA-Z]{2}:[0-9a-zA-Z]{2}:[0-9a-zA-Z]{2}:[0-9a-zA-Z]{2}:[0-9a-zA-Z]{2}");
        Matcher matcher = pattern.matcher(mac);


        Pattern pattern2 = Pattern.compile("[0-9A-Z]{2}:[0-9A-Z]{2}:[0-9A-Z]{2}:[0-9A-Z]{2}:[0-9A-Z]{2}:[0-9A-Z]{2}");
        Matcher matcher2 = pattern2.matcher(mac);
        if (matcher.matches() || matcher2.matches()) {
            return true;
        } else {
            return false;
        }
    }


}
