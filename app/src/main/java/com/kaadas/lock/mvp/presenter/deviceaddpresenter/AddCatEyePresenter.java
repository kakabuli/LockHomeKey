package com.kaadas.lock.mvp.presenter.deviceaddpresenter;

import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.Animation;

import com.google.gson.Gson;
import com.hisilicon.hisilink.MessageSend;
import com.hisilicon.hisilink.OnlineReciever;
import com.hisilicon.hisilink.WiFiAdmin;
import com.hisilicon.hisilinkapi.HisiLibApi;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.activity.addDevice.cateye.AddDeviceCatEyeThirdActivity;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IAddCatEyeView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.MqttReturnCodeError;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class AddCatEyePresenter<T> extends BasePresenter<IAddCatEyeView> {
    private long timeoutTime = 300 * 1000;
    private String TAG = "配置猫眼";

    private Disposable allowCateyeJoinDisposable;
    private Disposable listenerCatEyeOnlineDisposable;
    private String SSID;
    private String pwd;


    //handler 消息
    private static final int MSG_ONLINE_RECEIVED = 0;
    private static final int MSG_AP_RECEIVED_ACK = 2;
    private static final int MSG_CONNECTED_APMODE = 3;
    //上线消息接收方式
    private static final int ONLINE_MSG_BY_TCP = 1;
    private static final int ONLINE_PORT_BY_UDP = 1131;
    private static final int ONLINE_PORT_BY_TCP = 0x3516;

    private int counterTime = 0;
    private long buttonPressTime = -1;
    private long APModeStartTime = -1;
    private WiFiAdmin mWiFiAdmin = null;
    private MessageSend mMessageSend = null;
    private int udpPort = 0;
    private String strName = null;
    private String onlineMessage = null;
    private int homeWifiID = -1;

    private Socket TCPSocket = null;
    private OutputStream outputStream = null;

    private boolean isBroadcastListening = true;
    private Timer APModeTimer = null;
    private OnlineReciever onlineReciever = null;

    /**
     * 网关Id   猫眼的mac地址
     * @param gwId      网关Id
     * @param catEyeMac
     */
    public void allowCateyeJoin(String gwId, String catEyeMac, String catEyeSn) {
        if (mqttService != null) {
            MqttMessage mqttMessage = MqttCommandFactory.allowCateyeJoin(MyApplication.getInstance().getUid(), gwId, catEyeSn, catEyeMac);
            allowCateyeJoinDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), mqttMessage)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {

                            LogUtils.e("允许入网 1  "+mqttData.isThisRequest(mqttMessage.getId(), MqttConstant.ALLOW_GATEWAY_JOIN));
                            return mqttData.isThisRequest(mqttMessage.getId(), MqttConstant.ALLOW_GATEWAY_JOIN);
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(allowCateyeJoinDisposable);
                            LogUtils.e("允许入网 22  "+"200".equals(mqttData.getReturnCode()));
                            if ("200".equals(mqttData.getReturnCode())) {
                                if (mViewRef.get() != null) {
                                    mViewRef.get().allowCatEyeJoinSuccess();
                                }
                            } else {
                                if (mViewRef.get() != null) {
                                    mViewRef.get().allowCatEyeJoinFailed(new MqttReturnCodeError(mqttData.getReturnCode()));
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LogUtils.e("入网异常    " + throwable.getMessage());
                            if (mViewRef.get() != null) {
                                mViewRef.get().allowCatEyeJoinFailed(throwable);
                            }
                        }
                    });
            compositeDisposable.add(allowCateyeJoinDisposable);

        }
    }

    public void startJoin(String deviceMac, String deviceSn, String gwId,String ssid,String pwd) {
        LogUtils.e("开始加入网络   ");
        SSID = ssid;
        this.pwd = pwd;
        handler.postDelayed(timeOutRunnable, timeoutTime);
        listenerCatEyeOnline(deviceMac, deviceSn, gwId);

        //上线消息
        constructOnlineMessage();
        mWiFiAdmin = new WiFiAdmin(MyApplication.getInstance());
        bindCateye();
    }



    //绑定猫眼
    private void bindCateye() {
        //todo 正在连接
        buttonPressTime = System.currentTimeMillis();

        //TBD:返回值异常时的处理
        HisiLibApi.setNetworkInfo(mWiFiAdmin.getSecurity(), ONLINE_PORT_BY_TCP, ONLINE_MSG_BY_TCP,
                mWiFiAdmin.getWifiIPAdress(), SSID, pwd,
                this.strName);

        //创建线程侦听上线消息
        recieveOnlineMessage();
        //发送报文
        sendMessage();
    }
    /**
     * 监听猫眼上线
     */
    public void listenerCatEyeOnline(String deviceMac, String deviceSn, String gwId) {
        if (mqttService != null) {
            toDisposable(listenerCatEyeOnlineDisposable);
            listenerCatEyeOnlineDisposable = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            return mqttData.getFunc().equals(MqttConstant.GW_EVENT);
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            DeviceOnLineBean deviceOnLineBean = new Gson().fromJson(mqttData.getPayload(), DeviceOnLineBean.class);
                            LogUtils.e("本地信息为   " + "   " + deviceMac + "   " + deviceSn + "    " + gwId);
                            if (gwId.equals(deviceOnLineBean.getGwId()) && deviceMac.equals(deviceOnLineBean.getEventparams().getMacaddr())
                                    && deviceSn.equals(deviceOnLineBean.getDeviceId()) && "online".equals(deviceOnLineBean.getEventparams().getEvent_str())
                                    ) {
                                //设备信息匹配成功  且是上线上报
                                LogUtils.e("添加猫眼成功");
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
            compositeDisposable.add(listenerCatEyeOnlineDisposable);
        }

    }


    private Runnable timeOutRunnable = new Runnable() {
        @Override
        public void run() {
            if (mViewRef.get() != null) {
                mViewRef.get().joinTimeout();  //入网超时
            }
        }
    };

    @Override
    public void detachView() {
        super.detachView();
        handler.removeCallbacks(timeOutRunnable);
        if (onlineReciever!=null){
            onlineReciever.stop();
        }
    }

    public void endCateyeJoin(String gwId, String catEyeMac, String catEyeSn) {
        if (mqttService != null) {
            MqttMessage mqttMessage = MqttCommandFactory.allowCateyeJoin(MyApplication.getInstance().getUid(), gwId, catEyeSn, catEyeMac);
            allowCateyeJoinDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), mqttMessage)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            return mqttData.isThisRequest(mqttMessage.getId(), MqttConstant.ALLOW_GATEWAY_JOIN);
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if ("200".equals(mqttData.getReturnCode())) {
                                if (mViewRef.get() != null) {
                                    mViewRef.get().allowCatEyeJoinSuccess();
                                }
                            } else {
                                if (mViewRef.get() != null) {
                                    mViewRef.get().allowCatEyeJoinFailed(new MqttReturnCodeError(mqttData.getReturnCode()));
                                }
                            }
                            toDisposable(allowCateyeJoinDisposable);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {
                                mViewRef.get().allowCatEyeJoinFailed(throwable);
                            }
                        }
                    });
            compositeDisposable.add(allowCateyeJoinDisposable);
        }
    }



    public void constructOnlineMessage() {
        byte[] onlineMessageArray = new byte[13];
        onlineMessageArray[0] = 'o';
        onlineMessageArray[1] = 'n';
        onlineMessageArray[2] = 'l';
        onlineMessageArray[3] = 'i';
        onlineMessageArray[4] = 'n';
        onlineMessageArray[5] = 'e';
        onlineMessageArray[6] = ':';
        int[] macArray = new int[6];
        char[] ssidArray = SSID.toCharArray();
        for (int i = 0; i < 6; ++i) {
            macArray[i] = charToInt(ssidArray[12 + 2 * i]) * 16 + charToInt(ssidArray[12 + 2 * i + 1]);
        }
        for (int i = 0; i < 6; ++i) {
            onlineMessageArray[7 + i] = (byte) macArray[i];
        }
        onlineMessage = new String(onlineMessageArray, 0, 13);
    }

    public int sendMessage() {
        //启动线程发送组播消息
        mMessageSend = new MessageSend(MyApplication.getInstance());
        mMessageSend.multiCastThread();
        return 0;
    }

    public void recieveOnlineMessage() {
        onlineReciever = new OnlineReciever(new OnlineReciever.onOnlineRecievedListener() {
            @Override
            public void onOnlineReceived(String message) {
                LogUtils.d("添加猫眼上线消息 " + onlineMessage + " message " + message);
                if (onlineMessage.equals(message)) {
                    Message msg = handler.obtainMessage();
                    msg.what = MSG_ONLINE_RECEIVED;
                    handler.sendMessage(msg);
                }
            }
        });
        onlineReciever.start();
    }

    public int charToInt(char input) {
        int ret;
        if ('A' <= input) {
            ret = input - 'A' + 10;
        } else {
            ret = input - '0';
        }
        return ret;
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_ONLINE_RECEIVED:
                    //绑定模式
//                    cateyeBindSuccess();
                    //todo 猫眼绑定成功
                    long onlineRecieveTime = System.currentTimeMillis();
                    long castTime = onlineRecieveTime - buttonPressTime;
                    //停止接收上线消息
                    onlineReciever.stop();
                    //停止广播
                    mMessageSend.stopMultiCast();
                    LogUtils.e("猫眼绑定成功");
                    break;
                //AP收到单播消息
                case MSG_AP_RECEIVED_ACK:
                    Log.d(TAG, "MSG_AP_RECEIVED_ACK recieved");
                    //重新关联路由器AP
                    connectHomeAP();
                    udpPort = msg.arg1;
                    //创建UDP连接
                     BroadcastListenThread broadcastListenThread = new BroadcastListenThread();
                    broadcastListenThread.start();
                    break;
                case MSG_CONNECTED_APMODE:
                    //组网模式
                    APModeTimer.cancel();
                    long connectedTime = System.currentTimeMillis();
                    long castTime2 = connectedTime - APModeStartTime;
//                    cateyeBindSuccess();
                    //todo 猫眼绑定成功
                    LogUtils.e("猫眼绑定成功");
                    break;
                default:
                    break;
            }
        }
    };

    public void connectHomeAP() {

        //断开设备AP，关联路由器AP
        Log.d(TAG, "connect home wifi");
        mWiFiAdmin.enableNetWork(homeWifiID);
        //确认wifi已经关联上
        while (!mWiFiAdmin.isWifiConnected()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    class BroadcastListenThread extends Thread {
        public void run() {
            while (isBroadcastListening) {
                try {
                    // 创建接收方的套接字,并制定端口号
                    DatagramSocket getSocket = new DatagramSocket(udpPort);
                    // 确定数据报接受的数据的数组大小
                    byte[] buf = new byte[1024];
                    // 创建接受类型的数据报，数据将存储在buf中
                    DatagramPacket getPacket = new DatagramPacket(buf, buf.length);
                    // 通过套接字接收数据
                    getSocket.receive(getPacket);
                    // 解析发送方传递的消息
                    String getMes = new String(buf, 0, getPacket.getLength());
                    LogUtils.d("davi getMes " + getMes);
                    if (getMes.equals(onlineMessage)) {
                        Log.d(TAG, "onlineMessage recieved ");
                        isBroadcastListening = false;
                        Message msg = handler.obtainMessage();
                        msg.what = MSG_CONNECTED_APMODE;
                        handler.sendMessage(msg);
                    }
                    if (!getSocket.isClosed())
                        getSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    class TCPConnectThread extends Thread {

        public TCPConnectThread() {
        }

        public void run() {
            byte[] buffer = HisiLibApi.getMessageToSend();
            //确认WiFi已经关联上
            while (!mWiFiAdmin.isWifiConnected()) {
                try {
                    counterTime++;
                    if (counterTime >= 20) {
                        mWiFiAdmin.reconnect();
                        counterTime = 0;
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "Wifi connected");

            if (0 == connect()) {
                sendMessage(buffer);
            }
        }
    }

    public void sendMessage(byte[] buffer) {
        try {
            if (TCPSocket == null || !TCPSocket.isConnected()) {
                Log.d(TAG, "SOCKET ERROR");
                return;
            }
            outputStream.write(buffer);
            outputStream.flush();
            outputStream.close();
            outputStream = null;
            if (TCPSocket != null && !TCPSocket.isClosed()) {
                TCPSocket.close();
                TCPSocket = null;
            }
            //确认wifi已经去关联
            while (mWiFiAdmin.isWifiConnected()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mWiFiAdmin.forgetWifi(SSID);
            Log.d(TAG, "Wifi disconnect");

            Message msg = handler.obtainMessage();
            msg.what = MSG_AP_RECEIVED_ACK;
            msg.arg1 = ONLINE_PORT_BY_UDP;//udp port
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int connect() {
        try {
            //如果已经连接上了，就不再执行连接程序
            if (null == TCPSocket) {
                //用InetAddress方法获取ip地址
                String wifiGWAdress = mWiFiAdmin.getWifiGWAdress();
                InetAddress ipAddress = InetAddress.getByName(wifiGWAdress);
                TCPSocket = new Socket(ipAddress, 5000);
                outputStream = TCPSocket.getOutputStream();
                TCPSocket.getInputStream();
            }
            if (null == TCPSocket) {
                Log.e(TAG, "connet fail. socket == null");
                return -1;
            }
            return 0;
        } catch (UnknownHostException e1) {
            Log.e(TAG, "UnknownHostException error");
            e1.printStackTrace();
            return -1;
        } catch (IOException e1) {
            Log.e(TAG, "IOException error");
            e1.printStackTrace();
            return -1;
        }
    }

    public WifiConfiguration constructWifiConfig(String ssid) {
        String passwordString;
        passwordString = HisiLibApi.getPassword(ssid);
        WifiConfiguration mWifiConfig;
        mWifiConfig = mWiFiAdmin.createWifiInfo(ssid, passwordString, 3);
        return mWifiConfig;
    }



}
