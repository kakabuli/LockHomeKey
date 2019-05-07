package com.kaadas.lock.utils.ftp;


import android.util.Log;

import com.kaadas.lock.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Create By denganzhi  on 2019/5/7
 * Describe
 */

public class FtpUtils {

    private static FtpUtils ftpUtils = null;
    private FTP mFTP;
    private  String ftpUser = "anonymous";
    private  String ftpPwd = "anonymous@example.com";
    public static FtpUtils getInstance()
    {
        if (ftpUtils == null) {
            ftpUtils = new FtpUtils();
        }
        return ftpUtils;
    }

    /*
 * 下载
 * url 图片的全路径
 * */
    public  void loadPirListFile(final String ftpCmdIp, final String ftpCmdPort, final String remotePath, final String deviceId, final String selectTime) {
        //	LogUtils.d("davi ftpCmdIp " + ftpCmdIp + " ftpCmdPort " + ftpCmdPort+" 下载开始时间 "+System.currentTimeMillis());
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFTP = new FTP(ftpCmdIp, Integer.parseInt(ftpCmdPort), ftpUser, ftpPwd);
//                mFTP = new FTP("192.168.168.1", Integer.parseInt(ftpCmdPort), ftpUser, ftpPwd);
                try {
                    mFTP.openConnection();
                    //	 /sdap0/storage/orangecat-20181102/1541140662_picture.jpg
                    mFTP.listFTPFile(remotePath,deviceId,selectTime);
                } catch (Exception e) {
                    LogUtils.d("davi e "+e);
                    Log.e(GeTui.VideoLog,"FtpUtils异常:"+e);
                    EventBus.getDefault().post(FtpException.DOWNLOADEXCEPTION);
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
