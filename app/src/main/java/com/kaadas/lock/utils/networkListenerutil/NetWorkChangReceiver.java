package com.kaadas.lock.utils.networkListenerutil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.activity.device.GatewayActivity;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NotifyRefreshActivity;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class NetWorkChangReceiver extends BroadcastReceiver {


    /**
     * 获取连接类型
     *
     * @param type
     * @return
     */
    private String getConnectionType(int type) {
        String connType = "";
        if (type == ConnectivityManager.TYPE_MOBILE) {
            connType = "3G网络数据";
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            connType = "WIFI网络";
        }
        return connType;
    }

    public static NotifyRefreshActivity notifityActivity;

    public static void setNotifyRefreshActivity(NotifyRefreshActivity notifyRefreshActivity) {
        notifityActivity = notifyRefreshActivity;
    }

    private static PublishSubject<Boolean> networkChangeObversable = PublishSubject.create();

    public static Observable<Boolean> notifyNetworkChange(){
        return networkChangeObversable;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 监听wifi的打开与关闭，与wifi的连接无关
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            LogUtils.e("TAG", "wifiState:" + wifiState);
            switch (wifiState) {
                //网络
                case WifiManager.WIFI_STATE_DISABLED:
                    //把所有网关的状态设置为离线
                    List<GatewayInfo> gatewayInfos= MyApplication.getInstance().getAllGateway();
                    if (gatewayInfos!=null&&gatewayInfos.size()>0){
                        for (GatewayInfo gatewayInfo:gatewayInfos){
                            gatewayInfo.setEvent_str("offline");
                        }
                        if (notifityActivity!=null) {
                            notifityActivity.notifityActivity(true);
                        }
                        networkChangeObversable.onNext(true);
                        LogUtils.e("通知刷新页面");
                    }
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
            }
        }
        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            //获取联网状态的NetworkInfo对象
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        LogUtils.e("TAG", getConnectionType(info.getType()) + "连上");
                       /* if(MyApplication.getInstance().getMqttService()!=null){
                            if (MyApplication.getInstance().getMqttService().getMqttClient()==null||!MyApplication.getInstance().getMqttService().getMqttClient().isConnected()){
                                MyApplication.getInstance().getMqttService().mqttConnection();
                            }
                        }*/
                    }
                }else {
                    LogUtils.e("TAG", getConnectionType(info.getType()) + "断开");
                }
                }
            }
    }
}


