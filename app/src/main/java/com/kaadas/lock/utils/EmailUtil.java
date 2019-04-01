package com.kaadas.lock.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David
 */

public class EmailUtil {
    private static EmailUtil emailUtil = null;

    public static EmailUtil getInstance()
    {
        if (emailUtil == null) {
            emailUtil = new EmailUtil();
        }
        return emailUtil;
    }
    /**
     * 验证邮箱输入是否合法
     *
     * @param
     * @return
     */
    public  boolean isEmail(String email) {
        
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


}
