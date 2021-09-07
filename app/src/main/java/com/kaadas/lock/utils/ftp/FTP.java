package com.kaadas.lock.utils.ftp;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;


import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamException;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by mopangyou on 2016/11/28.
 *
 * @创建者 mopangyou
 * @创建时间 2016/11/28 13:48
 * @描述 FTP封装类
 */

public class FTP {
	private static String hostName; //服务器名
	private int hostPort;//端口号
	private String userName; //用户名
	private String password; //密码
	private FTPClient mFTPClient; //FTP连接类
	private List<FTPFile> mFTPFiles; //FTP列表
	public final static String REMOTE_PATH_VIDEO="/home/media/trigger/video_clip"; //视频列表路径    /home/media/trigger/video_clip
	public final static String REMOTE_PATH="/temp"; //
	public final static String REMOTE_PATH_PICTURE="/home/media/trigger/video_clip";  //图片列表路径
	//    public final static String REMOTE_PATH_VIDEO="/LocalUser/lgw/videos"; //视频列表路径
//    public final static String REMOTE_PATH="/LocalUser"; //
//    public final static String REMOTE_PATH_PICTURE="/LocalUser/lgw/pics";  //图片列表路径
//    public final static String REMOTE_PATH_VIDEO="/tmp"; //视频列表路径
//    public final static String REMOTE_PATH="/tmp"; //
//    public final static String REMOTE_PATH_PICTURE="/tmp";  //图片列表路径
	private String currentPath="";
	private double response; //流量统计
	private OnConnectedListener onConnectedListener;

	public FTP(String userName, String hostName, String password, FTPClient FTPClient, List<FTPFile> FTPFiles) {
		this.userName = userName;
		this.hostName = hostName;
		this.password = password;
		this.mFTPClient = FTPClient;
		this.mFTPFiles = FTPFiles;
	}

	public FTP(String hostName) {
		this.hostName = hostName;
	}

	public FTP(String hostName, int port, String userName, String password) {
		this.hostName = hostName;
		this.userName = userName;
		this.password = password;
		this.hostPort=port;
		mFTPClient = new FTPClient();
		mFTPClient.setConnectTimeout(6000);//milliseconds
		mFTPFiles = new ArrayList<>();
		mFTPClient.setBufferSize(1024*1024);
	}

	/**
	 * 打开FTP服务
	 */
	public void openConnection() throws IOException, UnConnectException {
		if (onConnectedListener != null){
			onConnectedListener.onConnectStart();
		}
//        mFTPClient.setControlEncoding("UTF-8"); //中文转码
		int reply;  //服务器响应值
		try {
			String replyString;
			if (mFTPClient == null){
				mFTPClient =  new FTPClient();
			}
			//连接至服务器
			Log.e(GeTui.VideoLog,"FTP是否连接："+mFTPClient.isConnected());
			mFTPClient.connect(hostName,hostPort);
			//获取响应值
			reply=mFTPClient.getReplyCode();
			LogUtils.i("isPositiveCompletion "+ FTPReply.isPositiveCompletion(reply)+".");
			Log.e(GeTui.VideoLog,"获取的响应值:"+reply);
			if(!FTPReply.isPositiveCompletion(reply)){
				//断开连接
				mFTPClient.disconnect();
				EventBus.getDefault().post(GeTui.FTP_CONNECTION_FAIL);
				Log.e(GeTui.VideoLog,"FTP连接异常......");
				throw new UnConnectException("connect fail:"+reply);
			}else{
				if (onConnectedListener != null){
					onConnectedListener.onConnectSuccess();
				}
			}
			//登录到服务器
			Log.e(GeTui.VideoLog,"FTP....调用登录前");
			boolean loginSuccess = mFTPClient.login(userName,password);
			Log.e(GeTui.VideoLog,"FTP....调用登录后:"+loginSuccess);
			//获取响应值
			reply=mFTPClient.getReplyCode();
			replyString = mFTPClient.getReplyString();
			LogUtils.i(reply+"."+mFTPClient.getReplyString() +".ftp是否登录" + loginSuccess+replyString);
			Log.e(GeTui.VideoLog,reply+"."+mFTPClient.getReplyString() +".ftp是否登录" + loginSuccess+" replyString:"+replyString);
			if(!FTPReply.isPositiveCompletion(reply)){ //登录异常
				Log.e(GeTui.VideoLog,"FTP登录异常");
				EventBus.getDefault().post(GeTui.FTP_LOGIN_FAIL);
				if (onConnectedListener != null){
					onConnectedListener.onLoginFail();
				}
				//断开连接
				mFTPClient.disconnect();
				//            throw new IOException("connect fail:"+ " " + replyString);
			}else{
				//获取登录信息
				// FTPClientConfig config=new FTPClientConfig(mFTPClient.getSystemType());
				// Log.i("----sysType",mFTPClient.getSystemType());
				Log.e(GeTui.VideoLog,"FTP登录成功");
				LogUtils.i(mFTPClient.getSystemType());
				mFTPClient.setControlEncoding("GB2312");
				//config.setServerLanguageCode("zh");
				//config.setServerLanguageCode("en");
				//mFTPClient.configure(config);
				//设置为二进制模式
				mFTPClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
				//设置为被动模式
				mFTPClient.enterLocalPassiveMode();
				if (onConnectedListener != null){
					onConnectedListener.onLoginSuccess();
				}
			}
		}catch (SocketTimeoutException e){
			throw new UnConnectException("Connect Timeout:"+e.getMessage());
		} catch(ConnectException e){
			throw new UnConnectException(e.getMessage()+"");
//        } catch (SocketException e){
//            if ("Socket is not connected".equals(e.getMessage())){
//                throw new UnConnectException("");
//            }else{
//                throw new SocketException(e.getMessage());
//            }
		} catch (IOException e) {
			e.printStackTrace();
			if ("Timed out waiting for initial connect reply".equals(e.getMessage())||"Connection is not open".equals(e.getMessage())) {
				throw new UnConnectException(e.getMessage()+"");
			}else{
				throw e;
			}
		}catch (NullPointerException e){
			LogUtils.d("davi e "+e);
			throw new UnConnectException(e.getMessage()+"");
		}
	}

	/**
	 * 关闭FTP服务
	 */
	public void closeConnect() throws Exception{
		try{
			if (mFTPClient != null && isConnected()) {
				mFTPClient.logout();
				mFTPClient.disconnect();
				mFTPClient = null;
				Log.i("FTP LOG OUT INFO", "Logout");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 列出FTP下所有文件
	 */
	public List<FTPFile> listFiles(String remotePath) throws IOException, UnConnectException {
		try {
			if (mFTPClient == null || !isConnected()) {
				openConnection();
			}
			List<FTPFile> mFTPFiles = new ArrayList<>();
			//更改FTP目录
			mFTPClient.changeWorkingDirectory(remotePath);
			//获取文件
			FTPFile[] files = mFTPClient.listFiles(remotePath);
			//遍历并添加到集合
			for (FTPFile file : files) {
				mFTPFiles.add(file);
			}
			return mFTPFiles;
		}catch (SocketTimeoutException e){
			throw new UnConnectException(e.getMessage()+"");
		}catch (SocketException e){
			throw new UnConnectException(e.getMessage()+"");
		}catch (FTPConnectionClosedException e){
			throw new UnConnectException(e.getMessage()+"");
		}catch (NullPointerException e){
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 下载
	 *
	 * /sdap0/storage/orangecat-20181102
	 * /sdap0/storage/orangecat-20181102/1541140662_picture.jpg
	 * 本地路径
	 * 1541140662_picture.jpg
	 */
	public Result download(String remotePath,String url,String localPath,String fileName) throws IOException, UnConnectException {
		Log.e(GeTui.VideoLog,"FTP...调用download方法开始下载");
		try{
			LogUtils.d("davi remotePath "+remotePath);
			LogUtils.d("davi url "+url);
			LogUtils.d("davi localPath "+localPath);
			LogUtils.d("davi fileName "+fileName);
			Log.e(GeTui.VideoLog,"FTP...调用download方法开始下载:remotePath:"+remotePath+" localPath:"+localPath+" fileName:"+fileName);

			boolean flag = false;
			if (mFTPClient == null || !isConnected()){
				LogUtils.d("davi 连接前 "+System.currentTimeMillis());
				openConnection();
				LogUtils.d("davi 连接后 "+System.currentTimeMillis());

			}
			if (mFTPClient != null) {
				Result result = null; //初始化FTP当前目录
				currentPath = remotePath;//初始化当前流量
				response = 0;
				Date startTime = null;
				Date endTime = null;
				mFTPClient.changeWorkingDirectory(currentPath);
				if (mFTPClient != null) {//切换目录
					//创建本地目录
					File file = new File(localPath + "/" + fileName);
					//下载当前时间
					startTime = new Date();
					//下载单个文件
					FTPFile ftpFile=new FTPFile();
					ftpFile.setName(fileName);
					LogUtils.d("davi ftpFile 名字 "+ftpFile.getName());
					Log.e(GeTui.VideoLog,"FTP==>file:"+file+"  ftpFile:"+ftpFile.getName());
					flag = downloadSingle(file, ftpFile);
					//下载完时间
					LogUtils.w("download"+ "success:" + flag);
					endTime = new Date();
					result = new Result(flag, FTPUtil.getFormatTime(endTime.getTime() - startTime.getTime()), FTPUtil.getFormatSize(response));
				}
				return result;
			}
		}catch (SocketTimeoutException e){
			throw new UnConnectException(e.getMessage()+"");
		}catch (SocketException e){
			throw new UnConnectException(e.getMessage()+"");
		}
		return null;
	}


	/**
	 * 下载
	 *
	 * /sdap0/storage/orangecat-20181102
	 * /sdap0/storage/orangecat-20181102/1541140662_picture.jpg
	 * 本地路径
	 * 1541140662_picture.jpg
	 * remotePath:/sdap0/storage/orangecat-20190404/
	 * localPath:/storage/emulated/0/Android/data/com.kaidishi.lock/files/catEyeImages/CH01183910009/orangecat-20190404
	 * fileName:1554355888_picture.jpg
	 */
	public  Result download1(String remotePath,String localPath,String fileName) throws IOException, UnConnectException {
		String cacheFilePath= MyApplication.getInstance().getExternalCacheDir().getAbsolutePath();
		boolean isImage= fileName.contains("jpg") || fileName.contains("png") || fileName.contains("gif");

		String finallLocalPath= localPath;
		if(isImage){  //下载的是图片
			if(!TextUtils.isEmpty(cacheFilePath)){  //mnt/sdcard/Android/data/com.tz.androidtest/cache
				localPath=cacheFilePath;
			}
		}

		//	Log.e(GeTui.VideoLog,"FTP...调用download方法开始下载");
		try{
			LogUtils.d("davi remotePath "+remotePath);
			LogUtils.d("davi localPath "+localPath);
			LogUtils.d("davi fileName "+fileName);
			Log.e(GeTui.VideoLog,"FTP...调用download方法开始下载:remotePath:"+remotePath+" localPath:"+localPath+" fileName:"+fileName);

			boolean flag = false;
			if (mFTPClient == null || !isConnected()){
				Log.e(GeTui.VideoLog,"mFTPClient == null || !isConnected()");
				LogUtils.d("davi 连接前 "+System.currentTimeMillis());
				openConnection();
				LogUtils.d("davi 连接后 "+System.currentTimeMillis());

			}
			if (mFTPClient != null) {
				Result result = null; //初始化FTP当前目录
				currentPath = remotePath;//初始化当前流量
				response = 0;
				Date startTime = null;
				Date endTime = null;
				boolean isChange=mFTPClient.changeWorkingDirectory(currentPath);
//				Log.e(GeTui.VideoLog,"=====================================================================");
//				FTPFile[] ftpFiles=mFTPClient.listFiles();
//				if(ftpFiles==null){
//					Log.e(GeTui.VideoLog,"ftp空");
//				}
//				Log.e(GeTui.VideoLog,"***********************************"+ftpFiles.length);
//				for (FTPFile ftpFile1: ftpFiles){
//					Log.e(GeTui.VideoLog,"ftp下所有的:"+ftpFile1.getName()+"........"+ftpFile1.getSize());
//				}
//				Log.e(GeTui.VideoLog,"=====================================================================");
				if (mFTPClient != null) {//切换目录
					//创建本地目录
					File file = new File(localPath + "/" + fileName);
					File finally_file=new File(finallLocalPath+"/" + fileName);
					//下载当前时间
					startTime = new Date();
					//下载单个文件
					FTPFile ftpFile=new FTPFile();
					ftpFile.setName(fileName);
					LogUtils.d("davi ftpFile 名字 "+ftpFile.getName());
					Log.e(GeTui.VideoLog,"FTP==>file:"+file+"  ftpFile:"+ftpFile.getName()+"isChange:"+isChange);
//					InputStream is = mFTPClient.retrieveFileStream(remotePath+"/"+fileName);  //判断文件是否存在
//					if (is == null || mFTPClient.getReplyCode() == FTPReply.FILE_UNAVAILABLE) {
//						 Log.e(GeTui.VideoLog,"FTP=====》文件不存在");
//					}else{
//						Log.e(GeTui.VideoLog,"FTP=====》文件存在");
//					}

					flag = downloadSingle(file, ftpFile);
					if(flag  && file.getAbsolutePath()!= finally_file.getAbsolutePath() && isImage){
						//下载成功
						boolean iscopysuccess=copyFile(file.getAbsolutePath(),finally_file.getAbsolutePath());
						Log.e(GeTui.VideoLog,"FTP===>调用拷贝方法:"+file.getAbsolutePath()+"  "+finally_file.getAbsolutePath()+" iscopysuccess:"+iscopysuccess);
						if(iscopysuccess) {
							EventBus.getDefault().post(GeTui.FTP_IMAGE_COPY);
							file.delete();
						}else {
							Log.e(GeTui.VideoLog,"FTP==>图片拷贝失败");
						}

					}
					//下载完时间
					LogUtils.w("download"+ "success:" + flag);
					endTime = new Date();
					result = new Result(flag, FTPUtil.getFormatTime(endTime.getTime() - startTime.getTime()), FTPUtil.getFormatSize(response));
				}
				return result;
			}
		}catch (SocketTimeoutException e){
			throw new UnConnectException(e.getMessage()+"");
		}catch (SocketException e){
			throw new UnConnectException(e.getMessage()+"");
		}
		return null;
	}


	public  void listFTPFile(String remotePath,String deviceId,String selectTime) throws IOException, UnConnectException, ParseException {
		try{
			//	remotePath="/sdap0/storage/orangecat-20190409";

			Log.e(GeTui.VideoLog,"FTP...调用download方法开始下载:remotePath:"+remotePath);
			if (mFTPClient == null || !isConnected()){
				LogUtils.d("davi 连接前 "+System.currentTimeMillis());
				openConnection();
				LogUtils.d("davi 连接后 "+System.currentTimeMillis());

			}
			if (mFTPClient != null) {
				currentPath = remotePath;//初始化当前流量
				MyApplication.getInstance().setPirListImg(null);
				boolean isCurrentFlag=mFTPClient.changeWorkingDirectory(currentPath);
				Log.e(GeTui.VideoLog,"=====================================================================isCurrentFlag:"+isCurrentFlag);
				if(!isCurrentFlag){
					//	EventBus.getDefault().post(GeTui.FTP_DIR_NO);
				}

				FTPFile[] ftpFiles=mFTPClient.listFiles();
				if(ftpFiles==null){
					Log.e(GeTui.VideoLog,"ftp空");
				}
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date sd2=new Date();
				String current_data =df.format(sd2);

				Date today =  df.parse(selectTime);
				long todayTimeStamp = today.getTime()/1000;
				Calendar c = Calendar.getInstance();
				c.setTime(today);
				c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天  用户选择时间
				Date tomorrow = c.getTime();
				String nextDay=df.format(tomorrow);
				long nextDayTimeStamp= c.getTimeInMillis()/1000;
                String afterDay= "/sdap0/storage/orangecat-"+nextDay;

				Calendar c2 = Calendar.getInstance();
				c2.setTime(today);
				c2.add(Calendar.DAY_OF_MONTH, -1);// 前一天
				long beforeDayTimeStamp= c2.getTimeInMillis()/1000;
				Date before = c2.getTime();
				String beforeDay=df.format(before);
				String beforeDayPath = "/sdap0/storage/orangecat-"+beforeDay;
				Log.e(GeTui.VideoLog,"前一天是:"+beforeDayPath);


				Log.e(GeTui.VideoLog,"对比结果current_data："+current_data+" deviceId:"+deviceId +" selectTime："+selectTime+" nextDayTimeStamp:"+nextDayTimeStamp);

				Log.e(GeTui.VideoLog,"***********************************"+ftpFiles.length+"afterDay:"+afterDay);
				LinkedList<String> imageList=new LinkedList<>();


				if(isCurrentFlag){
					for (FTPFile ftpFile1: ftpFiles) {

						String file_name = ftpFile1.getName();
						if (!file_name.contains(".jpg")){
							continue;
						}

						Log.e(GeTui.VideoLog,"ftp下所有的:"+ftpFile1.getName()+"........"+ftpFile1.getSize());

						long img_time=0;

                        try{
							img_time = Long.parseLong(ftpFile1.getName().split("_")[0]);
						}catch (Exception e){
                        	 e.printStackTrace();
						}

						Log.e(GeTui.VideoLog,"时间是:"+df2.format(new Date(img_time*1000)));
       // 今天内目录下，有前一天的
						// 有后一天的,过滤东区
						if(img_time >= nextDayTimeStamp ){
							// 排除不要
							Log.e(GeTui.VideoLog,"排除东区的图片是:"+df2.format(new Date(img_time*1000)) + "img_time:"+img_time +" nextDayTimeStamp:"+nextDayTimeStamp);
							continue;
						}

						//过滤西区
						if(img_time< todayTimeStamp){
							// 排除不要
							Log.e(GeTui.VideoLog,"排除西区的图片是:"+df2.format(new Date(img_time*1000)) + "img_time:"+img_time +" nextDayTimeStamp:"+nextDayTimeStamp);
							continue;
						}



						if(ftpFile1.getSize()>10){
							imageList.add(ftpFile1.getName());
						}

					}
				}
				String key= deviceId+selectTime;
				if(imageList.size()>0){
					Collections.sort(imageList);
					Collections.reverse(imageList);
					MyApplication.getInstance().setPirListImg(imageList);
					String json = new Gson().toJson(imageList);
					SPUtils.put(key,json);
				}

				Log.e(GeTui.VideoLog,"FTP===>imageList大小是:"+imageList);
				Log.e(GeTui.VideoLog,"*************************第一个阶段完成*******************************:"+beforeDayPath);
				// 东区
				String currentTimeZone= getCurrentTimeZone();
				if(currentTimeZone.contains("+")){
					boolean isBeforeFlag=mFTPClient.changeWorkingDirectory(beforeDayPath);
					Log.e(GeTui.VideoLog,"isBeforeFlag:"+isBeforeFlag+" 东区");
					// 东区
					if(isBeforeFlag){
						FTPFile[] before_ftpFiles=mFTPClient.listFiles();
						if(before_ftpFiles==null){
							Log.e(GeTui.VideoLog,"第二阶段....ftp空:"+beforeDayPath);
						}
						for (FTPFile ftpFile1: before_ftpFiles){

							String file_name = ftpFile1.getName();
							if (!file_name.contains(".jpg")){
								continue;
							}
							long beforeimg_time =-1;
							try{
								beforeimg_time=Long.parseLong(ftpFile1.getName().split("_")[0]);   // 4月9日
							}catch (Exception e){
								// 卡插入苹果电脑以后生成临时文件异常
								continue;
							}

							Log.e(GeTui.VideoLog,"第二阶段....ftp下所有的:"+ftpFile1.getName()+"........"+ftpFile1.getSize()+"时间是:"+df2.format(new Date(beforeimg_time*1000)));
							// 过滤掉的
							if(beforeimg_time < todayTimeStamp){
								Log.e(GeTui.VideoLog,"第二阶段....过滤掉的:"+ftpFile1.getName()+"........"+"时间是:"+df2.format(new Date(beforeimg_time*1000)));
								continue;
							}
							if(ftpFile1.getSize()>10){
								imageList.add(ftpFile1.getName());
							}

						}
					}
					Collections.sort(imageList);
					Collections.reverse(imageList);
					MyApplication.getInstance().setPirListImg(imageList);
					EventBus.getDefault().post(GeTui.FTP_LIST_SUC);
					if(imageList!=null && imageList.size() > 0){
						String json = new Gson().toJson(imageList);
					//	String key= deviceId+selectTime;
						SPUtils.put(key,json);

						Log.e("denganzhi5","Sp保存key:"+key+"json:"+json);
					}
				}else if(currentTimeZone.contains("-")){
					// 西区
					boolean isAfterFlag=mFTPClient.changeWorkingDirectory(afterDay);
					Log.e(GeTui.VideoLog,"isAfterFlag:"+isAfterFlag+" 西区");
					if(isAfterFlag){
						FTPFile[] after_ftpFiles=mFTPClient.listFiles();
						if(after_ftpFiles==null){
							Log.e(GeTui.VideoLog,"第二阶段....ftp空:"+beforeDayPath);
						}
						for (FTPFile ftpFile1: after_ftpFiles){

							String file_name = ftpFile1.getName();
							if (!file_name.contains(".jpg")){
								continue;
							}
							long after_time =-1;
							try{
								after_time=Long.parseLong(ftpFile1.getName().split("_")[0]);   // 4月9日
							}catch (Exception e){
								// 卡插入苹果电脑以后生成临时文件异常
								continue;
							}

							Log.e(GeTui.VideoLog,"第二阶段....ftp下所有的:"+ftpFile1.getName()+"........"+ftpFile1.getSize()+"时间是:"+df2.format(new Date(after_time*1000)));
							// 过滤掉的
							if(after_time >= nextDayTimeStamp){
								Log.e(GeTui.VideoLog,"第二阶段....过滤掉的:"+ftpFile1.getName()+"........"+"时间是:"+df2.format(new Date(after_time*1000)));
								continue;
							}
							if(ftpFile1.getSize()>10){
								imageList.add(ftpFile1.getName());
							}

						}
					}
					Collections.sort(imageList);
					Collections.reverse(imageList);
					MyApplication.getInstance().setPirListImg(imageList);
					EventBus.getDefault().post(GeTui.FTP_LIST_SUC);
					if(imageList!=null && imageList.size() > 0){
						String json = new Gson().toJson(imageList);
					//	String key= deviceId+selectTime;
						SPUtils.put(key,json);

						Log.e("denganzhi5","Sp保存key:"+key+"json:"+json);
					}
				}
				Log.e(GeTui.VideoLog,"=====================================================================");
			}
		}catch (SocketTimeoutException e){
			throw new UnConnectException(e.getMessage()+"");
		}catch (SocketException e){
			throw new UnConnectException(e.getMessage()+"");
		}
	}

	public  String getCurrentTimeZone()
	{
		TimeZone tz = TimeZone.getDefault();
		return createGmtOffsetString(true,true,tz.getRawOffset());

	}
	public  String createGmtOffsetString(boolean includeGmt,
										 boolean includeMinuteSeparator, int offsetMillis) {
		int offsetMinutes = offsetMillis / 60000;
		char sign = '+';
		if (offsetMinutes < 0) {
			sign = '-';
			offsetMinutes = -offsetMinutes;
		}
		StringBuilder builder = new StringBuilder(9);
		if (includeGmt) {
			builder.append("GMT");
		}
		builder.append(sign);
		appendNumber(builder, 2, offsetMinutes / 60);
		if (includeMinuteSeparator) {
			builder.append(':');
		}
		appendNumber(builder, 2, offsetMinutes % 60);
		return builder.toString();
	}
	private static void appendNumber(StringBuilder builder, int count, int value) {
		String string = Integer.toString(value);
		for (int i = 0; i < count - string.length(); i++) {
			builder.append('0');
		}
		builder.append(string);
	}



	private  boolean copyFile(String srcFile, String destFile){
		try{
			InputStream streamFrom = new FileInputStream(srcFile);
			OutputStream streamTo = new FileOutputStream(destFile);
			byte buffer[]=new byte[1024];
			int len;
			while ((len= streamFrom.read(buffer)) > 0){
				streamTo.write(buffer, 0, len);
			}
			streamFrom.close();
			streamTo.close();
			return true;
		} catch(Exception ex){
			Log.e(GeTui.VideoLog,"拷贝异常:"+ex.getMessage());
			return false;
		}
	}


	/**
	 * 下载多个文件
	 */
	private boolean downloadMany(File localFile) throws IOException, UnConnectException {

		boolean flag = false;
		try{
			if (mFTPClient == null || !isConnected()){
				openConnection();
			}

			if (mFTPClient != null) {
				//FTP当前目录
				if (!currentPath.equals(REMOTE_PATH)) {
					currentPath = currentPath + REMOTE_PATH + localFile.getName();
				} else {
					currentPath = currentPath + localFile.getName();
				}
				//创建本地文件夹
				localFile.mkdir();
				mFTPClient.changeWorkingDirectory(currentPath);
				//得到当前目录下的所有文件
				FTPFile[] ftpFiles = mFTPClient.listFiles();
				//循环遍历
				for (FTPFile ftpFile : ftpFiles) {
					//创建文件
					File file = new File(localFile.getPath() + "/" + ftpFile.getName());
					if (ftpFile.isDirectory()) {
						flag = downloadMany(file);
					} else {
						flag = downloadSingle(file, ftpFile);
					}
				}
			}
		}catch (SocketTimeoutException e){
			throw new UnConnectException(e.getMessage()+"");
		}catch (SocketException e){
			throw new UnConnectException(e.getMessage()+"");
		}
		return flag;
	}
	boolean isDowning = false;
	/**
	 * 下载单个文件
	 *
	 */
	private boolean downloadSingle(final File localFile, FTPFile ftpFile) throws IOException, UnConnectException {
		boolean flag = false;
		try {
			if (mFTPClient == null || !isConnected()) {
				LogUtils.d("davi 连接前1 "+System.currentTimeMillis());
				openConnection();
				LogUtils.d("davi 连接后1 "+System.currentTimeMillis());
			}
			if (mFTPClient != null) {
				OutputStream outputStream = new FileOutputStream(localFile);
				if (mFTPClient != null && mFTPClient.isConnected() && outputStream != null) {
					try {
						//统计流量
						response += ftpFile.getSize();
						LogUtils.d("davi 流量 response "+response);
						Log.e(GeTui.VideoLog,"davi 流量 response:"+response);
                      /*  new Thread(){
                            @Override
                            public void run() {
                                isDowning = true;
                                while (isDowning){
                                    LogUtils.i("davi " +new Date() + " "+ localFile.getName() + ":" + localFile.length());
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }.start();*/
						LogUtils.d("davi 下载前 "+System.currentTimeMillis()+" localFile "+localFile);
						Log.e(GeTui.VideoLog,"davi 下载前 "+System.currentTimeMillis()+" localFile "+localFile);
						flag = mFTPClient.retrieveFile(localFile.getName(), outputStream);
						LogUtils.d("davi 下载完成 "+System.currentTimeMillis()+" flag "+flag+" 文件类型 "+localFile.getName());
						Log.e(GeTui.VideoLog,"davi 下载完成 "+System.currentTimeMillis()+" flag "+flag+" 文件类型 "+localFile.getName());
						if (flag==true){
							if (onConnectedListener != null){
								if (localFile.getName().contains(Constants.IMAGE_SUFFIX)){
									//图片
									onConnectedListener.onDownloadSuccess(Constants.IMAGE);
									EventBus.getDefault().post(GeTui.FTP_IMAGE_SUC);
								}else if (localFile.getName().contains(Constants.VIDEO_SUFFIX)){
									//视频
									onConnectedListener.onDownloadSuccess(Constants.VIDEO);
								}else if (localFile.getName().contains(Constants.AUDIO_SUFFIX)){
									//音频
									onConnectedListener.onDownloadSuccess(Constants.AUDIO);
								}
							}else {
								Log.e(GeTui.VideoLog,"onConnectedListener为空.....");
							}
						}else{
							Log.e(GeTui.VideoLog,"FTP==>下载图片失败:"+localFile.getName());
							localFile.delete();
							if(localFile.getName().contains(Constants.IMAGE_SUFFIX)){
								EventBus.getDefault().post(GeTui.FTP_IMAGE_FAIL);
							}else if(localFile.getName().contains(Constants.VIDEO_SUFFIX)){
								EventBus.getDefault().post(GeTui.FTP_VIDEO_FAIL);
							}else if(localFile.getName().contains(Constants.AUDIO_SUFFIX)){
								EventBus.getDefault().post(GeTui.FTP_AUDIO_FAIL);
							}
						}
						isDowning = false;
					}catch (NullPointerException e){
						localFile.delete();
						flag = false;
						LogUtils.d("davi 下载状态 "+flag+" e "+e);
						Log.e(GeTui.VideoLog,"FTP下载异常:"+false+"  e "+e);
						if(localFile.getName().contains(Constants.IMAGE_SUFFIX)){
							EventBus.getDefault().post(GeTui.FTP_IMAGE_FAIL);
						}
					}finally {
						//关闭文件流
						Log.e(GeTui.VideoLog,"FTP 关闭流");
						outputStream.close();
					}

				}
			}
		}catch (SocketTimeoutException e){
			throw new UnConnectException(e.getMessage()+"");
		}catch (SocketException e){
			throw new UnConnectException(e.getMessage()+"");
		}catch (CopyStreamException e){
			throw new UnConnectException(e.getMessage()+"");
		}
		return flag;
	}

	/**
	 * 删除ftp上的文件
	 * @param path
	 * @return
	 */
	public boolean deleteFile(String path) throws IOException, UnConnectException {

		boolean flag = false;
		try{
			if (mFTPClient == null || !mFTPClient.isConnected()){
				openConnection();
			}
			if (mFTPClient != null) {
				try {
					flag = mFTPClient.deleteFile(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}catch (SocketTimeoutException e){
			throw new UnConnectException(e.getMessage()+"");
		}catch (SocketException e){
			throw new UnConnectException(e.getMessage()+"");
		}
		return flag;
	}

	/**
	 * ftp是否连接状态
	 * @return
	 */
	public boolean isConnected(){
		return mFTPClient == null ? false : mFTPClient.isConnected();
	}

	/**
	 * 切换操作目录
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public boolean moveWorkSpace(String path) throws IOException, UnConnectException {
		boolean isSuccess = false;
		try{
			if (mFTPClient == null || !mFTPClient.isConnected()){
				openConnection();
			}
			if (mFTPClient != null) {
				isSuccess = mFTPClient.changeWorkingDirectory(path);
			}
		}catch (SocketTimeoutException e) {
			throw new UnConnectException(e.getMessage()+"");
		}catch (SocketException e){
			throw new UnConnectException(e.getMessage()+"");
		}catch (CopyStreamException e){
			throw new UnConnectException(e.getMessage()+"");
		}
		return isSuccess;
	}

	/**
	 *
	 * ftp上传文件
	 * @param localFileName 待上传文件
	 * @param ftpDirName ftp 目录名
	 * @param ftpFileName ftp目标文件
	 * @return true||false
	 */
	public boolean uploadFile(String localFileName,String ftpDirName, String ftpFileName) {
		return uploadFile(localFileName, ftpDirName, ftpFileName,false);
	}
	/**
	 *
	 * ftp上传文件
	 * @param localFileName 待上传文件
	 * @param ftpDirName ftp 目录名
	 * @param ftpFileName ftp目标文件
	 * @param deleteLocalFile 是否删除本地文件
	 * @return true||false
	 */
	public boolean uploadFile(String localFileName,String ftpDirName,String ftpFileName,boolean deleteLocalFile) {

//		if(StringExtend.isNullOrEmpty(ftpDirName))
//			ftpDirName="/";
		File srcFile = new File(localFileName);
		if(!srcFile.exists())
			throw new RuntimeException("文件不存在："+localFileName);

		try (FileInputStream fis = new FileInputStream(srcFile)) {
			//上传文件
			boolean flag = uploadFile(fis,ftpDirName,ftpFileName);
			fis.close();
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
		}
	}
	/**
	 *
	 * ftp上传文件 (使用inputstream)
	 * @param ftpDirName ftp 目录名
	 * @param ftpFileName ftp目标文件
	 * @return true||false
	 */
	public boolean uploadFile(FileInputStream uploadInputStream,String ftpDirName,String ftpFileName) {

//		if(StringExtend.isNullOrEmpty(ftpDirName))
//			ftpDirName="/";

		try {

			mFTPClient.setBufferSize(1024);
			//解决上传中文 txt 文件乱码
			mFTPClient.setControlEncoding("GBK");
			FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
			conf.setServerLanguageCode("zh");
			// 设置文件类型（二进制）
			mFTPClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			mFTPClient.changeWorkingDirectory(ftpDirName); //得到FTP当前目录下的所有文件
			// 上传
			String fileName = new String(ftpFileName.getBytes("GBK"),"iso-8859-1");
			if(mFTPClient.storeFile(fileName, uploadInputStream)){
				uploadInputStream.close();
				LogUtils.i(new Date() + ""+ ftpDirName + ":" + ftpFileName);
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
		}
	}


	/**
	 * 无法连接异常类
	 */
	public class UnConnectException extends Exception{
		public UnConnectException() {
		}

		public UnConnectException(String detailMessage) {
			super(detailMessage);
		}

		public UnConnectException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}

		public UnConnectException(Throwable throwable) {
			super(throwable);
		}
	}
	public interface OnConnectedListener{
		void onConnectStart();
		void onConnectSuccess();
		void onLoginSuccess();
		void onLoginFail();
		void onDownloadSuccess(String type);
	}
	public void setOnConnectedListener(OnConnectedListener onConnectedListener){
		this.onConnectedListener = onConnectedListener;
	}
}