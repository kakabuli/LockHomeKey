package com.kaadas.lock.mvp.presenter.wifilock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiSetUpActivity;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiSetUpView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.List;
import java.util.zip.CRC32;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.reactivex.disposables.Disposable;

public class WifiSetUpPresenter<T> extends BasePresenter<IWifiSetUpView> {
    private static final int PORT = 56789;
    private Thread thread;
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private InputStream inputStream;
    private OutputStream outputStream;

    public void connectSocket(String ipAddress, String adminPassword, String wifiName) {
        if (isSafe()) {
            mViewRef.get().startConnect();
        }
        thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    serverSocket = new ServerSocket(PORT);
                    serverSocket.setSoTimeout(15 * 1000);
                    socket = serverSocket.accept();
                    LogUtils.e("设备端IP是  PORT " + PORT);
                    String hostAddress = socket.getInetAddress().getHostAddress();
                    LogUtils.e("设备端IP是   " + ipAddress + "  socketIP是  " + hostAddress);
                    inputStream = socket.getInputStream();
                    //连接成功
                    byte[] data = new byte[256];
                    int size = inputStream.read(data);
                    LogUtils.e("收到的数据是   hex  " + Rsa.bytesToHexString(data));
                    LogUtils.e("收到的数据是   string  " + new String(data));
                    if (size < 45) { //
                        LogUtils.e("字节不够");
                        if (isSafe()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mViewRef.get().readFailed(-1);
                                }
                            });
                        }
                        return;
                    }
                    String temp = "";
                    for (int i = 0; i < adminPassword.length(); i++) {
                        temp += "0" + adminPassword.charAt(i);
                    }
                    byte[] t = Rsa.hex2byte(temp);
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
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
                    if (bytes[0] != crc[0] || bytes[1] != crc[1] || bytes[2] != crc[2] || bytes[3] != crc[3]) {
                        if (isSafe()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mViewRef.get().readFailed(-2);
                                }
                            });
                        }
                        return;
                    }
                    String wifiSn = new String(sn);
                    if (isSafe()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mViewRef.get().readSuccess(wifiSn, pwd);
                            }
                        });
                    }
                    outputStream = socket.getOutputStream();
                    outputStream.write("Success\r".getBytes());
                    outputStream.flush();
                    String randomCode = Rsa.bytesToHexString(pwd);
                    LogUtils.e("设备返回的随机码是  16进制 " + Rsa.bytesToHexString(pwd));
                    LogUtils.e("设备返回的随机码是   长度是 " + randomCode.length() + "  字符串  " + randomCode);
                    WifiLockInfo wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
                    if (wifiLockInfo != null && wifiLockInfo.getIsAdmin() == 1) {
                        update(wifiSn, randomCode, wifiName);
                    } else {
                        bindDevice(wifiSn, wifiSn, MyApplication.getInstance().getUid(), randomCode, wifiName);
                    }

                    LogUtils.e("读取数据成功");
                } catch (IOException e) {
                    LogUtils.e("读取数据失败   " + e.getMessage());
                    e.printStackTrace();
                    if (isSafe()) {
                        mViewRef.get().connectFailed(e);
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    if (isSafe()) {
                        mViewRef.get().readFailed(-3);
                    }
                } finally {
                    release();
                }
            }
        };
        thread.start();
    }

    private void release() {
        try {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }

            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        release();
    }

    private void bindDevice(String wifiSN, String lockNickName, String uid, String randomCode, String wifiName) {
        XiaokaiNewServiceImp.wifiLockBind(wifiSN, lockNickName, uid, randomCode, wifiName)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().onBindSuccess(wifiSN);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().onBindFailed(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()) {
                            mViewRef.get().onBindThrowable(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }


    public void update(String wifiSN, String randomCode, String wifiName) {
        XiaokaiNewServiceImp.wifiLockUpdateInfo(MyApplication.getInstance().getUid(), wifiSN, randomCode, wifiName)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().onUpdateSuccess(wifiSN);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().onUpdateFailed(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()) {
                            mViewRef.get().onUpdateThrowable(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                })
        ;

    }

}
