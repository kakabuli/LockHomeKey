package com.kaadas.lock.utils.networkListenerutil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.NotifyRefreshActivity;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class NetWorkChangReceiver extends BroadcastReceiver {

    /**
     * 获取连接类型
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
            if (!NetUtil.isNetworkAvailable()){
                //把所有网关的状态设置为离线
                List<GatewayInfo> gatewayInfos= MyApplication.getInstance().getAllGateway();
                if (gatewayInfos!=null&&gatewayInfos.size()>0){
                    for (GatewayInfo gatewayInfo:gatewayInfos){
                        gatewayInfo.setEvent_str("offline");
                    }
                }
                networkChangeObversable.onNext(true);
            }

    }
}


