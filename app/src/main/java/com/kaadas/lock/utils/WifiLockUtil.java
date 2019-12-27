package com.kaadas.lock.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WifiLockUtil {

    public static String getOffLinePassword(String WiFiSN, String randomCode) {
        String offLinePassword = "";
        //五分钟的时间戳
        long time = System.currentTimeMillis() / 5 * 60 * 1000;
        //
        String content = randomCode + WiFiSN + time;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("sha-1");
            md.update(content.getBytes());
            byte[] digest = md.digest();
            System.out.println("hash的值是   " + bytesToHexString(digest));
            byte[] head = new byte[4];
            System.arraycopy(digest, 0, head, 0, head.length);
            long l = Rsa.bytes2Int(head);
            offLinePassword= (l % 1000000) + "";
            for (int i = 0; i < (6 - offLinePassword.length()); i++) {
                offLinePassword += "0";
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return offLinePassword;
    }


    /**
     * A hashing method that changes a string (like a URL) into a hash suitable for using as a
     * disk filename.
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    public static void main(String[] args) {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("sha-1");
            md.update("1111".getBytes());
            byte[] digest = md.digest();
            System.out.println("hash的值是   " + bytesToHexString(digest));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


}
