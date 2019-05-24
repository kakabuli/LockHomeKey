package com.kaadas.lock.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David
 */
public class PhoneUtil {

 /*   public static boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        }
        if (!StringUtil.isNumeric(mobiles)) {
            return false;
        }
        return true;
    }*/

    /**
     * 判断手机格式是否正确
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^0?(13|14|15|16|17|18|19)[0-9]{9}$");
        //新加所有18段 17段
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

}
