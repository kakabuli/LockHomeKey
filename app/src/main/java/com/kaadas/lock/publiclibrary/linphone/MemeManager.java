package com.kaadas.lock.publiclibrary.linphone;

import android.content.Context;
import android.util.Log;

import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;

import net.sdvn.cmapi.BaseInfo;
import net.sdvn.cmapi.CMAPI;
import net.sdvn.cmapi.Device;
import net.sdvn.cmapi.RealtimeInfo;
import net.sdvn.cmapi.global.Constants;
import net.sdvn.cmapi.protocal.ConnectStatusListener;
import net.sdvn.cmapi.protocal.ConnectStatusListenerPlus;
import net.sdvn.cmapi.protocal.EventObserver;
import net.sdvn.cmapi.protocal.ResultListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class MemeManager {

    public static MemeManager instance;
    private static String TAG = "米米网 ";
    private static final int BUILD_VPN_REQUEST = 101;
    private String currentAccount;
    private String currentPassword;
    /**
     * false则认为登陆失败，true  则认为已经获取到设备列表，可以接听了
     */
    private PublishSubject<Boolean> connectStatusChange = PublishSubject.create();
    /**
     * 设备列表变化的监听
     */
    private PublishSubject<List<Device>> gwDeviceChange = PublishSubject.create();


    public static MemeManager getInstance() {
        if (instance == null) {
            synchronized (MemeManager.class) {
                instance = new MemeManager();
            }
        }
        return instance;
    }

    private MemeManager() {
    }

    public void init() {
        CMAPI.getInstance().addConnectionStatusListener(statusListener);
        CMAPI.getInstance().subscribe(observer);
    }

    private EventObserver observer = new EventObserver() {
        @Override
        public void onNetworkChanged() {
            super.onNetworkChanged();
            LogUtils.e(TAG, "onNetworkChanged   //网络发生变更" + "  获取网络信息  " + CMAPI.getInstance().getBaseInfo().getVip());
        }

        @Override
        public void onDeviceChanged() {
            super.onDeviceChanged();
            LogUtils.e(TAG, "onDeviceChanged   设备发生变更  " + "  获取网络信息  " + CMAPI.getInstance().getBaseInfo().getVip());
        }

        @Override
        public void onRealTimeInfoChanged(RealtimeInfo realtimeInfo) {
            super.onRealTimeInfoChanged(realtimeInfo);
//            LogUtils.e(TAG, "onRealTimeInfoChanged  实时信息变更");
        }

        @Override
        public void onDeviceStatusChange(Device device) {
            super.onDeviceStatusChange(device);
            LogUtils.e(TAG, "onDeviceStatusChange   设备状态变化，某个设备上线下线及 DLT隧道建立与销毁  ");
            List<Device> deviceList = CMAPI.getInstance().getDevices();
            Log.e(TAG, "  设备状态变化     当前设备    设备类型  " + device.getDevClass() + "  设备状态  " + device.getStatus() + "  设备Ip " + device.getVip());
            for (Device device1 : deviceList) {
                LogUtils.e(TAG, "  设备状态变化     设备类型  " + device1.getDevClass() + "  设备状态  " + device1.getStatus() + "  设备Ip " + device1.getVip());
            }
            getGwDevices();
        }

        @Override
        public void onTunnelRevoke(boolean b) {
            super.onTunnelRevoke(b);
            LogUtils.e(TAG, "onTunnelRevoke  当检测出VPN隧道被占用时回调");
        }

        @Override
        public void onEMUINetChange() {
            super.onEMUINetChange();
            LogUtils.e(TAG, "onEMUINetChange   机上适配系统WIFI网络切换监听");
        }
    };
    private ConnectStatusListener statusListener = new ConnectStatusListenerPlus() {
        @Override
        public void onAuthenticated() {

        }

        @Override
        public void onConnected() {
            Log.e(TAG, "咪咪网登陆成功.....   " + "  获取网络信息  " + CMAPI.getInstance().getBaseInfo().getVip());
        }

        @Override
        public void onConnecting() {
            LogUtils.d(TAG, "onConecting (-等待认证)");
        }

        @Override
        public void onDisconnecting() {
            LogUtils.d(TAG, "onDisconnecting (-正在断开连接)");
        }

        @Override
        public void onEstablished() {
            Log.e(TAG, "(启动VPN通道完成-) onEstablished (-已连接)");
            connectStatusChange.onNext(true);
            getGwDevices();
        }


        @Override
        public void onDisconnected(int reason) {
            //连接断开时回调
            Log.e(TAG, "onDisconnected (-断开连接)   " + reason);
        }
    };


    public Observable<Boolean> LoginMeme(String meAccount, String mePassword, Context context) {
        currentAccount = meAccount;
        currentPassword = mePassword;
        LogUtils.e(TAG, "登录米米网   账号  " + currentAccount + "  密码  " + currentPassword);
        CMAPI.getInstance().login(context, meAccount, mePassword, BUILD_VPN_REQUEST, new ResultListener() {
            @Override
            public void onError(int errorCode) {
                LogUtils.e(TAG, "登录错误   " + errorCode);
                connectStatusChange.onNext(false);
            }
        });
        return connectStatusChange;
    }

    public void videoActivityDisconnectMeme() {
        CMAPI.getInstance().disconnect();
    }


    public boolean isConnected() {
        if (CMAPI.getInstance() == null) {
            return false;
        }
        int status = CMAPI.getInstance().getRealtimeInfo().getCurrentStatus();
        if (status == Constants.CS_CONNECTED || status == Constants.CS_ESTABLISHED) {
            LogUtils.e(TAG, "咪咪网 已经连接");
            return true;
        } else {
            LogUtils.e(TAG, "咪咪网  尚未连接");
            return false;
        }
    }

    public String getCurrentAccount() {
        return currentAccount;
    }


    public String getCurrentPassword() {
        return currentPassword;
    }


    public Observable<List<Device>> listDevicesChange() {
        return gwDeviceChange;
    }

    public List<Device> getGwDevices() {
        List<Device> devices = new ArrayList<>();
        List<Device> deviceList = CMAPI.getInstance().getDevices();
        for (Device device : deviceList) {
            if (device.getDevClass() == 6756929) {
                devices.add(device);
            }
        }
        if (devices.size() > 0) {
            LogUtils.e(TAG, "网关设备在线   " + devices.size());
            gwDeviceChange.onNext(devices);
        }
        return devices;
    }

    public String getDeviceIp() {
        BaseInfo baseInfo = CMAPI.getInstance().getBaseInfo();
        if (baseInfo != null) {
            return baseInfo.getVip();
        }
        return "";
    }
}
