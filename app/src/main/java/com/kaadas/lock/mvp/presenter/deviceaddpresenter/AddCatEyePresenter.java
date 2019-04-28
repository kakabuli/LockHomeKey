package com.kaadas.lock.mvp.presenter.deviceaddpresenter;

import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.hisilicon.hisilink.MessageSend;
import com.hisilicon.hisilink.OnlineReciever;
import com.hisilicon.hisilink.WiFiAdmin;
import com.hisilicon.hisilinkapi.HisiLibApi;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IAddCatEyeView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.MqttReturnCodeError;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class AddCatEyePresenter<T> extends BasePresenter<IAddCatEyeView> {
    private long timeoutTime = 120 * 1000;
    private String TAG = "配置猫眼";

    private Disposable listenerCatEyeOnlineDisposable;
    private String wifiName;
    private String pwd;


    //handler 消息
    private static final int MSG_ONLINE_RECEIVED = 0;
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
    private OnlineReciever onlineReciever = null;
    private String deviceSN;
    private String cateEyeMac;

    /**
     * 网关Id   猫眼的mac地址
     * @param gwId      网关Id
     */

    public void startJoin(String deviceMac, String deviceSn, String gwId, String ssid, String pwd) {


        LogUtils.e("开始加入网络   ");
        wifiName = ssid;
        this.pwd = pwd;
        deviceSN = deviceSn;
        cateEyeMac = deviceMac;

        listenerCatEyeOnline(deviceMac, deviceSn, gwId);

        handler.removeCallbacks(timeOutRunnable);
        handler.postDelayed(timeOutRunnable, timeoutTime);
        //上线消息
        mWiFiAdmin = new WiFiAdmin(MyApplication.getInstance());
        bindCateye();
    }


    //绑定猫眼
    private void bindCateye() {
        //todo 正在连接
        buttonPressTime = System.currentTimeMillis();

        String temp = cateEyeMac.replace(":", "");
        String lastFour = temp.substring(temp.length() - 4, temp.length()).toUpperCase();

        //TBD:返回值异常时的处理
        HisiLibApi.setNetworkInfo(mWiFiAdmin.getSecurity(), ONLINE_PORT_BY_TCP, ONLINE_MSG_BY_TCP,
                mWiFiAdmin.getWifiIPAdress(), wifiName, pwd, ("kaadasrgch5050" + lastFour));

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
                            toDisposable(listenerCatEyeOnlineDisposable);
                            DeviceOnLineBean deviceOnLineBean = new Gson().fromJson(mqttData.getPayload(), DeviceOnLineBean.class);
                            LogUtils.e("本地信息为   " + "   " + deviceMac + "   " + deviceSn + "    " + gwId);
                            if ( deviceMac.equalsIgnoreCase(deviceOnLineBean.getEventparams().getMacaddr())
                                    &&  "online".equals(deviceOnLineBean.getEventparams().getEvent_str())
                                    ) {
                                //设备信息匹配成功  且是上线上报
                                LogUtils.e("添加猫眼成功");
                                if (mViewRef.get()!=null){
                                    mViewRef.get().cateEyeJoinSuccess(deviceOnLineBean);
                                }
                                MyApplication.getInstance().getAllDevicesByMqtt(true);
                                toDisposable(compositeDisposable);
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().catEysJoinFailed(throwable);
                            }
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
        if (onlineReciever != null) {
            onlineReciever.stop();
        }
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
            }
        });
        onlineReciever.start();
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
                case MSG_CONNECTED_APMODE:
                    //组网模式
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



}
