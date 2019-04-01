package com.kaadas.lock.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ty on 2017/2/17.
 */

public class DetectionEmailPhone {
    private  static DetectionEmailPhone check=new DetectionEmailPhone();
    private DetectionEmailPhone(){

    }

    public static DetectionEmailPhone getInstance(){
        return check;
    }
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
    public  boolean isMobile(String str) {
        Pattern pattern = Pattern.compile("1[0-9]{10}");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }

    }
}
