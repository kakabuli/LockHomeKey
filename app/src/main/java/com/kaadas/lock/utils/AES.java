package com.kaadas.lock.utils;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.res.Resources;

import android.net.Uri;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;

import org.apache.commons.net.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    public static final String key = "3a79fee83a79fbc3";

    // 加密
    @SuppressLint("NewApi")
    public static String Encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            LogUtils.e("shulan Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            LogUtils.e("shulan Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        LogUtils.e("shulan encrypted---->" + encrypted.toString());

        return Base64Utils.getEncoder().encodeToString(encrypted);
//        return Rsa.parseByte2HexStr(encrypted);
    }

    // 解密
    public static String Decrypt(String sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                LogUtils.e("shulan Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                LogUtils.e("shulan Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = Base64Utils.getDecoder().decode(sSrc);//先用base64解密
//            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
//            byte[] encrypted1 = Rsa.parseHexStr2Byte(sSrc);
            LogUtils.e("shulan Decrypt---->" + encrypted1.toString());
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, "utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

    public static String keyForToken(String token, String key, String timeStamp) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(key.substring(0, 4)).append(token.substring(token.length() - 4, token.length() - 2))
                .append(timeStamp.substring(timeStamp.length() - 4, timeStamp.length() - 2))
                .append(key.substring(4, 8)).append(token.substring(token.length() - 2))
                .append(timeStamp.substring(timeStamp.length() - 2));
        LogUtils.e("shulan keyForToken--->" + stringBuilder);
        return stringBuilder.toString();
    }

    public static String keyNoToken(String key, String timeStamp) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(key.substring(0, 5)).append(timeStamp.substring(timeStamp.length() - 6, timeStamp.length() - 3))
                .append(key.substring(5, 10)).append(timeStamp.substring(timeStamp.length() - 3));
        LogUtils.e("shulan keyNoToken--->" + stringBuilder);
        return stringBuilder.toString();
    }

    public static void main(String[] args) {

        System.out.println("有token" + keyForToken("11111111111111a6c4", "3a79fee83a1111111111111", "1234564806"));
        System.out.println("无token" + keyNoToken("3a79fee83a11111111111111", "1234755483"));
    }

    public static String securityPicture(String imagepath) throws IOException {
        Uri parse = Uri.parse(imagepath);
        FileInputStream fs = new FileInputStream(parse.toString());
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while (-1 != (len = fs.read(buffer))) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        fs.close();
        LogUtils.e("shulan buffer--------->" + buffer);
        LogUtils.e("shulan Base64--------->" + Base64Utils.getEncoder().encodeToString(buffer));
        return Base64Utils.getEncoder().encodeToString(buffer);
    }


    /*public static String securityKey(int length) {
        String str = "";
        try {
            str = securityPicture(getResourcesUri(R.drawable.about_us_logo));
            return str.substring(0,length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }*/


    /*private static String getResourcesUri(int id) {
        Resources resources = MyApplication.getInstance().getResources();
        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id);
        LogUtils.e("shulan uriPath--------->"  + uriPath);
        return uriPath;
    }*/


}
