package com.kaadas.lock.publiclibrary.ota.face;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.kaadas.lock.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.zip.CRC32;

public class SocketOtaUtil {
    private static final String TAG = "Ota升级";
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private static final int PACKAGE_SIZE = 1000;  //一个包的大小
    private int currentPackage = 0;  //包总数  14:37:30.202
    private IOtaListener listener;
    private FileInputStream fileInputStream;
    private int fileSize;
    private long crc32;
    private boolean firstSendFile = false;
    private long sendTime;


    public SocketOtaUtil(IOtaListener listener) {
        this.listener = listener;
    }

    private boolean initFile(String filePath) {
        Log.e(TAG, "文件地址是  " + filePath);
        File file = new File(filePath);
        try {
            fileInputStream = new FileInputStream(file);
            fileSize = fileInputStream.available();
            crc32 = getCRC32(file);
        } catch (FileNotFoundException e) {
            onError(-8, e);
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            onError(-9, e);
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public void startSendFile(String filePath) {
        if (!initFile(filePath)) {  //如果初始化文件失败，那么不再继续
            return;
        }
        try {
            int otaPort = 3456;
            ServerSocket serverSocket = new ServerSocket(otaPort);
            serverSocket.setSoTimeout(15 * 1000);
            socket = serverSocket.accept();
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            //连接成功
            if (listener != null) {
                listener.onConnected();
            }
            sendData();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e("socket  连接出错   " + e.getMessage());
            onError(-7, e);
        }
    }

    /**
     * 发送数据
     */
    private void sendData() {
        try {
            InetAddress inetAddress = socket.getInetAddress();
            Log.e(TAG, "IP地址是 " + inetAddress.toString() + "  端口是 " + String.valueOf(socket.getPort()));
            byte[] testCommand = OTA_TEST_COMMAND.getBytes();
            Thread.sleep(500);
            int testCommandResult = writeData(testCommand);
            Log.e(TAG, "写入测试指令" + "  结果" + testCommandResult);
            if (testCommandResult == 0) {  //写入测试结果成功   接着开始OTA配置
                byte[] finishCommand = OTA_Finish_COMMAND.getBytes();
                int finishCommandResult = writeData(finishCommand);
                Log.e(TAG, "开始时  写入结束指令   结果" + finishCommandResult);
                if (finishCommandResult == 0) {  //写入数据成功
                    //包总数
                    if (!firstSendFile && listener != null) {
                        listener.startSendFile();
                        firstSendFile = true;
                    }
                    int totalPackage = (int) Math.ceil((fileSize * 1.0) / (PACKAGE_SIZE * 1.0));
                    byte[] configCommand = getOtaConfigInfo(fileSize, crc32, PACKAGE_SIZE).getBytes();
                    int configCommandResult = writeData(configCommand);
                    if (listener != null) {
                        listener.onProgress(0, totalPackage);
                    }
                    Log.e(TAG, "写入配置数据" + "  结果  " + configCommandResult);
                    if (configCommandResult == 0) {  //写入配置数据成功
                        while (currentPackage < totalPackage) {
                            byte[] line = new byte[PACKAGE_SIZE];
                            int lineSize = fileInputStream.read(line);
                            if (lineSize > 0) {
                                if (lineSize < PACKAGE_SIZE) {
                                    byte[] temp = new byte[lineSize];
                                    System.arraycopy(line, 0, temp, 0, lineSize);
                                    line = temp;
                                }
                                byte[] packageCommand = getOtaPackage(currentPackage, line);
                                long currentTimeMillis = System.currentTimeMillis();
                                int packageCommandResult = writeData(packageCommand);
                                long interval =  System.currentTimeMillis() -currentTimeMillis ;
                                if (interval>1000 && listener!=null){
                                    listener.sendTimeOut(interval,currentPackage);
                                    LogUtils.e("写入数据包 超时   " + interval);
                                }
                                Log.e(TAG, "写入数据包  "+ currentPackage +"  interval " +interval+ "  结果 " + packageCommandResult);
                                if (packageCommandResult == 0) {  //写入数据成功
                                    currentPackage++;
                                    if (listener != null) {
                                        listener.onProgress(currentPackage, totalPackage);
                                    }
                                } else {
                                    //退出循环
                                    break;
                                }
                            } else {
                                onError(-12, null);
                                Log.e(TAG, "读取数据出现异常，读取到的数据个数为0，正常不会出现此情况");
                                //退出循环
                                break;
                            }
                        }
                        if (currentPackage == totalPackage) { //数据写完了
                            Log.e("数据写入完成", "数据写入完成");
                            finishCommand = OTA_Finish_COMMAND.getBytes();
                            finishCommandResult = writeData(finishCommand);
                            Log.e(TAG, "写入结束指令   结果" + finishCommandResult);
                            if (finishCommandResult == 0) {  //写入数据成功
                                Log.e(TAG, "Ota成功");
                                if (listener != null) {
                                    listener.onComplete();
                                }
                            }
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "线程中断");
            e.printStackTrace();
            onError(-10, e);
        } catch (IOException e) {
            Log.e(TAG, "文件读取出错");
            onError(-9, e);
            e.printStackTrace();
        } finally {
            onError(1, null);
        }
    }


    public void onError(int errorCode, Throwable throwable) {
        if (listener != null && errorCode < 0) {
            listener.onError(errorCode, throwable);
        }
        release();
    }

    public void release() {
        listener = null;
        try {
            //关闭文件流
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param data
     * @return 回OK      0
     */
    public int writeData(byte[] data) {
        if (socket == null) {
            onError(-6, null);
            return -6;
        }
        try {
            outputStream.write(data);
            outputStream.flush();
            Log.e(TAG, "发送数据   " + new String(data));
            sendTime = System.currentTimeMillis();
            return isSuccess(inputStream);
        } catch (IOException e) {
            if (listener != null) {
                onError(-5, e);
            }
            e.printStackTrace();
            return -5;
        }
    }

    /**
     * @param inputStream
     * @return 0  返回OK  -1  返回ERROR     -2  inputStream  为空   -3  读取数据出多   -4  返回未知数据
     * -11   读取超时
     */
    private int isSuccess(InputStream inputStream) {
        if (inputStream == null) {
            onError(-2, null);
            return -2;
        }
        byte[] b = new byte[8];
        try {
            socket.setSoTimeout(10005 * 1000);  //超时时间  10秒
            int size = inputStream.read(b);
            String result = new String(b, 0, size);
            Log.e(TAG, "收到Socket数据   " + result + "  耗时  " + (System.currentTimeMillis() - sendTime));
            if (result.contains("OK")) {
                return 0;
            } else if (result.contains("ERROR")) {
                onError(-1, null);
                return -1;
            } else {
                onError(-4, null);
                return -4;
            }
        } catch (SocketTimeoutException e) {
            Log.e("读取数据超时", e.getMessage());
            e.printStackTrace();
            onError(-11 - currentPackage*1000, e);
            return -11 - currentPackage*1000;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("读取数据出错", e.getMessage());
            onError(-3, e);
            return -3;
        }
    }


    /**
     * 获取文件CRC32码
     *
     * @return String
     */
    public long getCRC32(File file) throws IOException {
        CRC32 crc32 = new CRC32();
        // MessageDigest.get
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                crc32.update(buffer, 0, length);
            }
            return crc32.getValue();
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getWifiName(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : null;
        return wifiId;
    }


    ///////////////////////////////////// ///合成指令的地方   /////////////////////////////////////
    public static final String Config_header = "AT+OTAFWINFO";
    public static final String Send_data_header = "AT+OTAFWTR";
    public static final String OTA_TEST_COMMAND = "AT+OTATEST=?\r";
    public static final String OTA_Finish_COMMAND = "AT+OTAFWFNS\r";
    public static final String AT_OP_SET = "=";


    //“AT+OTAFWINFO=<total_len>,<crc>,<piece_len>\r”,配置固件配置命令，配置升
    public String getOtaConfigInfo(int totalByte, long crc32, int packageSize) {
        return Config_header + AT_OP_SET + totalByte + "," + crc32 + "," + packageSize + "\r";
    }


    //“AT+OTAFWTR=<index>,<crc>,<data_string>\r”
    public byte[] getOtaPackage(int index, byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        Log.e("包数据是", " index " + index + "  crc32 " + crc32.getValue() + "  payload " + bytesToHexString(data));
        return (Send_data_header + AT_OP_SET + index + "," + crc32.getValue() + "," + bytesToHexString(data) + "\r").getBytes();
    }

    public String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}



