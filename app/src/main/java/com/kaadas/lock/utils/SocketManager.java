package com.kaadas.lock.utils;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.CRC32;

import androidx.annotation.NonNull;

public class SocketManager {
    public static int PORT = 56789;
    private ServerSocket serverSocket;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String TAG = "SocketManager";
    private static SocketManager instance;

    public static SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
            LogUtils.e("--Kaadas--初始化SocketManager");
        }
        return instance;
    }

    public boolean isStart() {
        return socket != null;
    }

    /**
     * @return -1 打开端口失败   -2   超时   3连接异常  0 正常
     */
    public int startServer() {
        try {
                if (serverSocket == null){
                serverSocket = new ServerSocket(PORT);
                LogUtils.e("--Kaadas--打开socket端口：56789");
            }
            else {
                LogUtils.e("--Kaadas--已存在socket端口：56789");
            }

        } catch (IOException e) {
               LogUtils.e("--Kaadas--打开socket失败  " + e.getMessage());
                e.printStackTrace();
                release();
                return -1;
        }
        try {
            serverSocket.setSoTimeout(30 * 1000);
            LogUtils.e("--Kaadas--设置socket连接30s超时");

        } catch (SocketException e) {
            e.printStackTrace();
            release();
            LogUtils.e("--Kaadas--等待客户accept连接socket超时  " + e.getMessage());
            return -2;
        }
        try {
            socket = serverSocket.accept();
            LogUtils.e("--Kaadas--socket等待连接");

        } catch (IOException e) {
            e.printStackTrace();
            release();
            LogUtils.e("--Kaadas--socket.accept连接失败 ： " + e.getMessage());
            LogUtils.e("--Kaadas--socket.accept连接失败  == " + e);

            return -3;
        }
        return 0;
    }

    public boolean isConnected(){
        LogUtils.e("serverSocket  是否为空   " + (serverSocket == null ) + "socket  是否为空   " + (socket == null ));
        if (serverSocket!=null && socket!=null &&!serverSocket.isClosed() && socket.isConnected()){
            return true;
        }
        if (serverSocket!=null   ){
            LogUtils.e("serverSocket状态  isClosed " + serverSocket.isClosed());
        }
        if (socket!=null){
            LogUtils.e("socket状态  isClosed " + socket.isClosed());
        }
        return false;
    }

    /**
     * @return -1 超时  -2获取输入流失败   -3 读取数据失败  -99 socket为空  >0读取到的数据个数
     */
    public ReadResult readWifiData() {
        ReadResult readResult = new ReadResult();
        if (socket == null) {
            readResult.resultCode = -99;
            return readResult;
        }
        try {
            socket.setSoTimeout(15 * 1000);
        } catch (SocketException e) {
            e.printStackTrace();
            release();
            readResult.resultCode = -1;
            LogUtils.e("读取数据超时   -1 " + e.getMessage());
            return readResult;
        }
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            release();
            readResult.resultCode = -2;
            LogUtils.e("读取数据超时   -2 " + e.getMessage());
            return readResult;
        }
        int size = 0;
        try {
            byte[] temp = new byte[64];
            size = inputStream.read(temp);
            Log.e(TAG, "readWifiData: 读取到的数据是  " + new String(temp));
            if (size > 0) {
                readResult.data = temp;
                readResult.dataLen = size;
                return readResult;
            } else {
                readResult.resultCode = -3;
                return readResult;
            }
        } catch (IOException e) {
            e.printStackTrace();
            release();
            readResult.resultCode = -3;
            return readResult;
        }
    }



    /**
     * @return -1 超时  -2获取输入流失败   -3 读取数据失败  -99 socket为空  >0读取到的数据个数
     */
    public ReadResult readWifiDataTimeout(int timeout) {
        ReadResult readResult = new ReadResult();
        if (socket == null) {
            readResult.resultCode = -99;
            return readResult;
        }
        try {
            socket.setSoTimeout(timeout);
        } catch (SocketException e) {
            e.printStackTrace();
            release();
            readResult.resultCode = -1;
            LogUtils.e("读取数据超时   -1 " + e.getMessage());
            return readResult;
        }
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            release();
            readResult.resultCode = -2;
            LogUtils.e("读取数据超时   -2 " + e.getMessage());
            return readResult;
        }
        int size = 0;
        try {
            byte[] temp = new byte[64];
            size = inputStream.read(temp);
            Log.e(TAG, "readWifiData: 读取到的数据是  " + new String(temp));
            if (size > 0) {
                readResult.data = temp;
                readResult.dataLen = size;
                return readResult;
            } else {
                readResult.resultCode = -3;
                return readResult;
            }
        } catch (IOException e) {
            e.printStackTrace();
            release();
            readResult.resultCode = -3;
            return readResult;
        }
    }
    public class ReadResult {
        public byte[] data = new byte[10];
        public int resultCode;
        public int dataLen;

        @Override
        public String toString() {
            return "ReadResult{" +
                    "data=" + Arrays.toString(data) +
                    ", resultCode=" + resultCode +
                    ", dataLen=" + dataLen +
                    '}';
        }
    }


    /**
     * @param data
     * @return -1 写入失败 -99 socket 为空  0成功
     */
    public int writeData(byte[] data) {
        if (socket != null) {
            try {
                LogUtils.e("发送数据  " + new String(data));
                outputStream = socket.getOutputStream();
                outputStream.write(data);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.e("发送数据失败   " + e.getMessage());
                release();
                return -1;
            }
        } else {
            LogUtils.e("发送数据失败   socket 为空 "  );
            return -99;
        }
        return 0;
    }

    private void release(  ) {
        LogUtils.e("--Kaadas--释放Socket  " );
        try {
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }

            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "release:  关闭异常");
        }
    }


    public WifiResult parseWifiData(String adminPassword, byte[] data) {
        WifiResult wifiResult = new WifiResult();
        wifiResult.data = data;
        wifiResult.func = data[45] & 0xff;

        String temp = "";
        for (int i = 0; i < adminPassword.length(); i++) {
            temp += "0" + adminPassword.charAt(i);
        }
        byte[] t = Rsa.hex2byte(temp);
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            wifiResult.result = -1;
        }
        messageDigest.update(t);
        byte[] adminKey = messageDigest.digest();
        LogUtils.e("管理密码管理  Sha256之后是 " + Rsa.bytesToHexString(adminKey));

        byte[] content = new byte[32];
        byte[] sn = new byte[13];
        //Object src,  int  srcPos,    Object dest, int destPos,    int length
        System.arraycopy(data, 0, content, 0, content.length);
        System.arraycopy(data, 32, sn, 0, sn.length);

        byte[] decrypt = Rsa.decrypt(content, adminKey);
        LogUtils.e("解密之后的数据是  " + Rsa.bytesToHexString(adminKey));

        byte[] pwd = new byte[28];
        byte[] crc = new byte[4];

        System.arraycopy(decrypt, 0, pwd, 0, pwd.length);
        System.arraycopy(decrypt, 28, crc, 0, crc.length);

        LogUtils.e("随机数明文是  " + Rsa.bytesToHexString(pwd));
        LogUtils.e("RCR明文是  " + Rsa.bytesToHexString(crc));
        CRC32 crc32 = new CRC32();
        crc32.update(pwd);
        long localCrc = crc32.getValue();
        byte[] bytes = Rsa.int2BytesArray((int) localCrc);
        LogUtils.e("校验和 本地CRC  " + Rsa.bytesToHexString(bytes) + "   锁端CRC  " + Rsa.bytesToHexString(crc));
        if (bytes[0] != crc[0] || bytes[1] != crc[1] || bytes[2] != crc[2] || bytes[3] != crc[3]) { //校验失败
            wifiResult.result = -2;
            return wifiResult;
        }
        wifiResult.password = pwd;
        wifiResult.wifiSn = sn;
        return wifiResult;
    }

    public class WifiResult {
        public int result;  // -1 SHA256失败  -2校验失败  0成功
        public byte[] wifiSn;
        public byte[] password;
        public byte[] data;
        public int func;

        @Override
        public String toString() {
            return "WifiResult{" +
                    "result=" + result +
                    ", wifiSn=" + new String(wifiSn) +
                    ", password=" + new String(password) +
                    ", data=" + new  String(data) +
                    ", func=" + func +
                    '}';
        }
    }

    public void destroy() {
        release();
    }
}
