package com.kaadas.lock.utils.ftp;


import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

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


    /*
	 * 下载
	 * url 图片的全路径
	 * */
    public  void downloadMultiFile(final String ftpCmdIp, final String ftpCmdPort, final String[] urlStr, final String deviceId, final String folderName,final String singleUrl) {
        //	LogUtils.d("davi ftpCmdIp " + ftpCmdIp + " ftpCmdPort " + ftpCmdPort+" 下载开始时间 "+System.currentTimeMillis());
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFTP = new FTP(ftpCmdIp, Integer.parseInt(ftpCmdPort), ftpUser, ftpPwd);
//                mFTP = new FTP("192.168.168.1", Integer.parseInt(ftpCmdPort), ftpUser, ftpPwd);
                try {
                    mFTP.setOnConnectedListener(new FTP.OnConnectedListener() {
                        @Override
                        public void onConnectStart() {
                            Log.e(GeTui.VideoLog,"FtpUtils...onConnectStart");

                        }

                        @Override
                        public void onConnectSuccess() {
                            Log.e(GeTui.VideoLog,"FtpUtils...onConnectSuccess");
                        }

                        @Override
                        public void onLoginSuccess() {
                            Log.e(GeTui.VideoLog,"FtpUtils...onLoginSuccess");
                        }

                        @Override
                        public void onLoginFail() {
                            Log.e(GeTui.VideoLog,"FtpUtils...onLoginFail");
                        }

                        @Override
                        public void onDownloadSuccess(String type) {
                            Log.e(GeTui.VideoLog,"FtpUtils...onDownloadSuccess:"+"下载成功:"+type+"发送eventbus");

                            EventBus.getDefault().post( type);
                        }
                    });
                    Log.e(GeTui.VideoLog,"调用了..................downloadMultiFile....................197");
                    mFTP.openConnection();

                    //  下载当个图片
                    if(urlStr!=null && urlStr.length>0 ){
                        String downloadPath=null;
                        if(urlStr!=null && urlStr.length>0){
                            String url= urlStr[0];
                            LogUtils.d("davi url " + url);
                            if (!TextUtils.isEmpty(url)) {
                                int i = url.lastIndexOf("/");
//                        String remotePath = url.substring(0, i);
                                //  1552636453_picture.jpg
                                //sdap0/storage/orangecat-20190419/1555639246_picture.jpg
                                String fileName = url.substring(i + 1);
                                //  orangecat-20190315
                                String folder=getCurrentDayFolder(url);
                                LogUtils.d("davi  "  + " fileName " + fileName);
                                //  storage/emulated/0/Android/data/com.kaidishi.lock/files/catEyeImages/AC35EE88401A/orangecat-20190315
                                //  在SdCard上创建文件夹
                                //	 boolean isCreateFlag= createDownloadPath(folderName,deviceId,folder);
                                downloadPath = createDownloadPath(folderName,deviceId,folder);
                                if( !TextUtils.isEmpty(downloadPath)){
                                    Log.e(GeTui.VideoLog,"安卓文件夹创建成功");
                                }else{
                                    Log.e(GeTui.VideoLog,"安卓文件夹创建失败");
                                }
                                LogUtils.d("davi downloadPath " + downloadPath);
//                      String  remotePath="/sdap0/storage";
                                //	 /sdap0/storage/orangecat-20181102/1541140662_picture.jpg
                                //			String remotePath="/sdap0/storage"+File.separator+folder;
//							String url=remotePath+File.separator+fileName;
//							mFTP.download1(remotePath, downloadPath,fileName);
                            }
                        }

                        //			String str="/sdap0/storage/orangecat-20190315/1552636453_picture.jpg";
                        int i= urlStr[0].lastIndexOf("/");
                        String remoteRootPath =  urlStr[0].substring(0,i+1);
                        for(int j = 0;  j<urlStr.length; j++) {
                            Log.e(GeTui.VideoLog,"下载的第"+j+"张图片:"+urlStr[j]);
                            String fileName= urlStr[j].substring(i+1,urlStr[j].length());
                            SystemClock.sleep(200);
                            mFTP.download1(remoteRootPath,downloadPath,fileName);
                        }

                    }else if(urlStr==null && !TextUtils.isEmpty(singleUrl)){

                        int i = singleUrl.lastIndexOf("/");
//                        String remotePath = url.substring(0, i);
                        //  1552636453_picture.jpg
                        String fileName = singleUrl.substring(i + 1);
                        //  orangecat-20190315
                        String folder=getCurrentDayFolder(singleUrl);
                        LogUtils.d("davi  "  + " fileName " + fileName);
//                        mFTP.download("/sdap0/storage", "1.mp4", "/storage/emulated/0/kaadas/videos");
                       /* if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
                            downloadPath = FileUtils.getInstance().getRootDir() + File.separator + folderName+ File.separator + deviceId;
                        } else {// 如果SD卡不存在，就保存到本应用的目录下
                            downloadPath = MyApplication.getInstance().getCacheDir().getAbsolutePath() + File.separator +folderName + File.separator + deviceId;
                        }
                        File file = new File(downloadPath);
                        LogUtils.d("davi downloadPath " + downloadPath);
                        //判断文件夹是否存在，如果不存在就创建，否则不创建
                        if (!file.exists()) {
                            LogUtils.d("davi 文件夹不存在");
                            //通过file的mkdirs()方法创建目录中包含却不存在的文件夹
                            file.mkdirs();
                        }*/
//						String downloadPath= getDownloadPath(folderName,deviceId);
                        //  storage/emulated/0/Android/data/com.kaidishi.lock/files/catEyeImages/AC35EE88401A/orangecat-20190315
                        String downloadPath= getDownloadPath(folderName,deviceId,folder);
                        LogUtils.d("davi downloadPath " + downloadPath);
//                      String  remotePath="/sdap0/storage";
                        //	 /sdap0/storage/orangecat-20181102/1541140662_picture.jpg
                        String remotePath="/sdap0/storage"+ File.separator+folder;
                        String url=remotePath+File.separator+fileName;
                        mFTP.download1(remotePath, downloadPath,fileName);

                    }else {
                        Log.e(GeTui.VideoLog,"没有匹配文件下载");
                    }
                } catch (Exception e) {
                    LogUtils.d("davi e "+e);
                    Log.e(GeTui.VideoLog,"FtpUtils异常:"+e);
                    EventBus.getDefault().post(FtpException.DOWNLOADEXCEPTION);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String createDownloadPath(String folder,String deviceId,String currentTimeFolder){
        String path="";
//		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
//			path = FileUtils.getInstance().getRootDir() + File.separator + folder+ File.separator + deviceId+File.separator+currentTimeFolder;
//		} else {// 如果SD卡不存在，就保存到本应用的目录下
        path = MyApplication.getInstance().getExternalFilesDir("").getAbsolutePath() + File.separator +folder + File.separator + deviceId+File.separator+currentTimeFolder;
//		}
        File file = new File(path);
        LogUtils.d("davi 包含时间戳文件夹path " + path);
        //判断文件夹是否存在，如果不存在就创建，否则不创建
        if (!file.exists()) {
            LogUtils.d("davi 文件夹不存在");
            //通过file的mkdirs()方法创建目录中包含却不存在的文件夹
            boolean isPathCreate =file.mkdirs();
            if(isPathCreate){
                return path;
            }else{
                return  "";
            }
        }else{
            return path;
        }
    }

    //获取中间时间戳的文件夹名字
    public String getCurrentDayFolder(String url){
        //	/storage/emulated/0/kaadas/catEyeImages/AC35EEA60F2F/1535012736_audio.raw
        String folder="";
        if(url.indexOf("/")>=0){
            int i = url.lastIndexOf("/");
            String substring = url.substring(0, i);
            folder=substring.substring(substring.lastIndexOf("/")+1);
            return folder;
        }
        return folder;
    };

    //获取中间时间戳的文件夹名字
    public static String getCurrentDayFolder1(String url){
        //	/storage/emulated/0/kaadas/catEyeImages/AC35EEA60F2F/1535012736_audio.raw
        String folder="";
        if(url.indexOf("/")>=0){
            int i = url.lastIndexOf("/");
            String substring = url.substring(0, i);
            folder=substring.substring(substring.lastIndexOf("/")+1);
            return folder;
        }
        return folder;
    }

    //包含时间戳文件夹的路径名
    public String getDownloadPath(String folder,String deviceId,String currentTimeFolder){
        String path="";
//		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
//			path = FileUtils.getInstance().getRootDir() + File.separator + folder+ File.separator + deviceId+File.separator+currentTimeFolder;
//		} else {// 如果SD卡不存在，就保存到本应用的目录下
        path = MyApplication.getInstance().getExternalFilesDir("").getAbsolutePath() + File.separator +folder + File.separator + deviceId+File.separator+currentTimeFolder;
//		}
        File file = new File(path);
        LogUtils.d("davi 包含时间戳文件夹path " + path);
        //判断文件夹是否存在，如果不存在就创建，否则不创建
        if (!file.exists()) {
            LogUtils.d("davi 文件夹不存在");
            //通过file的mkdirs()方法创建目录中包含却不存在的文件夹
            file.mkdirs();
        }
        return path;
    }

    //不包含时间戳文件夹的路径名
    public String getDownloadPath(String folder,String deviceId){
        String path="";
//		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
//			path = FileUtils.getInstance().getRootDir() + File.separator + folder+ File.separator + deviceId;
//		} else {// 如果SD卡不存在，就保存到本应用的目录下
        path = MyApplication.getInstance().getExternalFilesDir("").getAbsolutePath() + File.separator +folder + File.separator + deviceId;
//		}
        File file = new File(path);
        LogUtils.d("davi path " + path);
        //判断文件夹是否存在，如果不存在就创建，否则不创建
        if (!file.exists()) {
            LogUtils.d("davi 文件夹不存在");
            //通过file的mkdirs()方法创建目录中包含却不存在的文件夹
            file.mkdirs();
        }
        return path;
    }
}
