package com.kaadas.lock.utils.ftp;

import java.math.BigDecimal;

/**
 * Created by mopangyou on 2016/11/28.
 *
 * @创建者 mopangyou
 * @创建时间 2016/11/28 15:02
 * @描述 FTP工具类
 */

public class FTPUtil {
    private static final int TIMEMSUNIT=1000; //时间毫秒单位
    private static final int STOREUNIT=1024; //存储单位
    private static final int TIMEUNIT=60;  //时间单位

    /**
     * 转化时间单位
     */
    public static String getFormatTime(long time){
        double second=(double)time/TIMEMSUNIT;
        if(second<1){
            return time+"MS";
        }
        double minute=second/TIMEUNIT;
        if(minute<1){
            BigDecimal result=new BigDecimal(Double.toString(second));
            return result.setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString()+"SEC";
        }
        double hour=minute/TIMEUNIT;
        if(hour<1){
            BigDecimal result=new BigDecimal(Double.toString(minute));
            return result.setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString()+"MIN";
        }
        BigDecimal result=new BigDecimal(Double.toString(hour));
        return result.setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString()+"H";

    }
    /**
     * 转化文件单位
     */
    public static String getFormatSize(double size){
        double kiloByte=size/STOREUNIT;
        if(kiloByte<1){
            return size+"Byte";
        }
        double megaByte=kiloByte/STOREUNIT;
        if(megaByte<1){
            BigDecimal result=new BigDecimal(Double.toString(kiloByte));
            return result.setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString()+"KB";
        }
        double gigaByte=megaByte/STOREUNIT;
        if(gigaByte<1){
            BigDecimal result=new BigDecimal(Double.toString(megaByte));
            return result.setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString()+"MB";
        }
        double teraByte=gigaByte/STOREUNIT;
        if(teraByte<1){
            BigDecimal result=new BigDecimal(Double.toString(gigaByte));
            return result.setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString()+"GB";
        }
        BigDecimal result=new BigDecimal(teraByte);
        return result.setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString()+"TB";
    }
}
