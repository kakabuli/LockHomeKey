package com.kaadas.lock.utils;

public class LoginUtil {
    private static LoginUtil loginUtil = null;

    public static LoginUtil getInstance()
    {
        if (loginUtil == null) {
            loginUtil = new LoginUtil();
        }
        return loginUtil;
    }

}
