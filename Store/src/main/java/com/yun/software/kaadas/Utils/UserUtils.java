package com.yun.software.kaadas.Utils;


import com.yun.software.kaadas.Comment.PreferencesConstans;
import com.yun.software.kaadas.UI.bean.User;



import la.xiong.androidquick.tool.SPUtils;

public class UserUtils {



    public static String getuserID() {
        return SPUtils.getInstance().getString(PreferencesConstans.ID, "0");
    }

    public static void setUserID(User user){
        SPUtils.getInstance().put(PreferencesConstans.ID, user.getId() +"");
    }


    public static void setUserTel(String tel) {
        SPUtils.getInstance().put(PreferencesConstans.USER_TEL, tel);
    }


    public static String getToken() {
        return SPUtils.getInstance().getString(PreferencesConstans.TOKEN, "");
//        return "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMSIsImlzcyI6Imh0dHBzOi8vd3d3Lmthbmdhcm9vYmFieWNhci5jb20iLCJzdWIiOiIxMzY3MDA5MTI3OSIsImlhdCI6MTU2OTQ2MjcwOX0.23FbT8QA8XDsXqlam_CnqTZxJoqwD2qixIu0SvO42TM";
    }

    public static void setToken(String token) {
        SPUtils.getInstance().put(PreferencesConstans.TOKEN, token);
    }
    public static boolean hasTal(){
        return SPUtils.getInstance().getBoolean(PreferencesConstans.HAS_TEL, false);
    }
    public static void setTelState(boolean hatel){
         SPUtils.getInstance().put(PreferencesConstans.HAS_TEL, hatel);
    }



    public static void setYanbaoka(boolean isGetTimeDelay) {
        SPUtils.getInstance().put(PreferencesConstans.YANBAOKA_CARD, isGetTimeDelay);
    }


    public static void clear(){
        SPUtils.getInstance().put(PreferencesConstans.YANBAOKA_CARD, false);
        SPUtils.getInstance().put(PreferencesConstans.HAS_TEL, false);
        SPUtils.getInstance().put(PreferencesConstans.TOKEN, "");
        SPUtils.getInstance().put(PreferencesConstans.USER_TEL, "");
        SPUtils.getInstance().put(PreferencesConstans.ID,  "");
    }


}
