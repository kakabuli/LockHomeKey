package com.kaadas.lock.fragment.device;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAdd2Activity;
import com.kaadas.lock.activity.addDevice.DeviceAddActivity;
import com.kaadas.lock.activity.device.BleDetailActivity;
import com.kaadas.lock.activity.device.BleAuthActivity;
import com.kaadas.lock.activity.device.cateye.more.CateyeAuthorizationFunctionActivity;
import com.kaadas.lock.activity.device.cateye.more.CateyeFunctionActivity;
import com.kaadas.lock.activity.device.gateway.GatewayActivity;
import com.kaadas.lock.activity.device.gatewaylock.GatewayLockAuthorizeFunctionActivity;
import com.kaadas.lock.activity.device.gatewaylock.GatewayLockFunctionActivity;
import com.kaadas.lock.activity.device.oldbluetooth.OldBleDetailActivity;
import com.kaadas.lock.activity.device.wifilock.WiFiLockDetailActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockAuthActivity;
import com.kaadas.lock.adapter.DeviceDetailAdapter;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.DevicePresenter;
import com.kaadas.lock.mvp.view.IDeviceView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.bean.ServerGatewayInfo;
import com.kaadas.lock.publiclibrary.bean.ServerGwDevice;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.ServerBleDevice;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttService;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.Rom;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.SPUtils2;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.greenDao.bean.BleLockServiceInfo;
import com.kaadas.lock.utils.greenDao.bean.CatEyeServiceInfo;
import com.kaadas.lock.utils.greenDao.bean.DevicePower;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockServiceInfo;
import com.kaadas.lock.utils.greenDao.bean.GatewayServiceInfo;
import com.kaadas.lock.utils.greenDao.db.BleLockServiceInfoDao;
import com.kaadas.lock.utils.greenDao.db.CatEyeServiceInfoDao;
import com.kaadas.lock.utils.greenDao.db.DaoSession;
import com.kaadas.lock.utils.greenDao.db.DevicePowerDao;
import com.kaadas.lock.utils.greenDao.db.GatewayLockServiceInfoDao;
import com.kaadas.lock.utils.greenDao.db.GatewayServiceInfoDao;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


import com.kaadas.lock.utils.AlertDialogUtil.ClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by asqw1 on 2018/3/14.
 */

public class DeviceFragment extends BaseFragment<IDeviceView, DevicePresenter<IDeviceView>> implements BaseQuickAdapter.OnItemClickListener, IDeviceView {
    @BindView(R.id.no_device_image)
    ImageView noDeviceImage;

    @BindView(R.id.device_recycler)
    RecyclerView deviceRecycler;

    @BindView(R.id.refresh)
    SmartRefreshLayout refresh;
    @BindView(R.id.buy)
    Button buy;
    @BindView(R.id.no_device_layout)
    RelativeLayout noDeviceLayout;
    @BindView(R.id.device_add)
    ImageView deviceAdd;

    private View mView;

    private Unbinder unbinder;


    private DeviceDetailAdapter deviceDetailAdapter;

    private List<HomeShowBean> mDeviceList = new ArrayList<>();
    private List<HomeShowBean> homeShowBeanList;
    private String uid;
    private DaoSession daoSession;
    private MqttService mqttService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_device, container, false);
        }
        unbinder = ButterKnife.bind(this, mView);
        deviceRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        homeShowBeanList = MyApplication.getInstance().getAllDevices();
        mqttService = MyApplication.getInstance().getMqttService();
        initData(homeShowBeanList);
        initRefresh();
        return mView;
    }


    @Override
    protected DevicePresenter<IDeviceView> createPresent() {
        return new DevicePresenter<>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initAdapter() {
        if (mDeviceList != null) {
            deviceDetailAdapter = new DeviceDetailAdapter(mDeviceList);
            deviceRecycler.setAdapter(deviceDetailAdapter);
            deviceDetailAdapter.setOnItemClickListener(this);
            for (int i = 0; i < mDeviceList.size(); i++) {
                if (HomeShowBean.TYPE_GATEWAY == mDeviceList.get(i).getDeviceType()) {
                    Object obj = mDeviceList.get(i).getObject();
                    String gwid = mDeviceList.get(i).getDeviceId();
                    if (obj instanceof GatewayInfo) {
                        GatewayInfo gatewayInfo = (GatewayInfo) obj;
                        String meUsername = gatewayInfo.getServerInfo().getMeUsername();
                        String mePwd = gatewayInfo.getServerInfo().getMePwd();
                        SPUtils2.put(getActivity(), gwid, meUsername + "&" + mePwd);
                    }
                }
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    private void initData(List<HomeShowBean> homeShowBeanList) {
        mDeviceList.clear();
        if (homeShowBeanList != null) {
            daoSession = MyApplication.getInstance().getDaoWriteSession();
            uid = MyApplication.getInstance().getUid();
            //清除数据库,可能存在用户在其他手机删除了设备，但是服务器已经没有该设备，所以会造成本地数据库误差
            daoSession.getGatewayServiceInfoDao().queryBuilder().where(GatewayServiceInfoDao.Properties.Uid.eq(uid)).buildDelete().executeDeleteWithoutDetachingEntities();
            daoSession.getGatewayLockServiceInfoDao().queryBuilder().where(GatewayLockServiceInfoDao.Properties.Uid.eq(uid)).buildDelete().executeDeleteWithoutDetachingEntities();
            daoSession.getCatEyeServiceInfoDao().queryBuilder().where(CatEyeServiceInfoDao.Properties.Uid.eq(uid)).buildDelete().executeDeleteWithoutDetachingEntities();
            daoSession.getBleLockServiceInfoDao().queryBuilder().where(BleLockServiceInfoDao.Properties.Uid.eq(uid)).buildDelete().executeDeleteWithoutDetachingEntities();
            if (homeShowBeanList.size() > 0) {
                noDeviceLayout.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
                mPresenter.getPublishNotify();
                mPresenter.listenerDeviceOnline();
                mPresenter.listenerNetworkChange();
                for (HomeShowBean homeShowBean : homeShowBeanList) {
                    LogUtils.e(homeShowBeanList.size() + "获取到大小     " + "获取到昵称  " + homeShowBean.getDeviceNickName());
                    //请求电量
                    switch (homeShowBean.getDeviceType()) {
                        case HomeShowBean.TYPE_GATEWAY_LOCK:
                            //网关锁
                            GwLockInfo gwLockInfo = (GwLockInfo) homeShowBean.getObject();
                            if (gwLockInfo != null) {
                                GatewayInfo gate = MyApplication.getInstance().getGatewayById(gwLockInfo.getGwID());
                                if (gate != null && gate.getEvent_str() != null && gate.getEvent_str().equals("offline")) {
                                    gwLockInfo.getServerInfo().setEvent_str("offline");
                                } else if (gate != null && gate.getEvent_str() == null) {
                                    gwLockInfo.getServerInfo().setEvent_str("offline");
                                }
                            }
                            DevicePower devicePower = daoSession.getDevicePowerDao().queryBuilder().where(DevicePowerDao.Properties.DeviceIdUid.eq(gwLockInfo.getServerInfo().getDeviceId() + uid)).unique();
                            if (devicePower != null) {
                                gwLockInfo.setPower(devicePower.getPower());
                            }

                            if (!gwLockInfo.getServerInfo().getEvent_str().equals("offline")) {
                                mPresenter.getPower(gwLockInfo.getGwID(), gwLockInfo.getServerInfo().getDeviceId(), MyApplication.getInstance().getUid());
                            }

                            //插入数据库
                            ServerGwDevice gwLock = gwLockInfo.getServerInfo();
                            String deviceId = gwLock.getDeviceId();
                            GatewayLockServiceInfo gatewayLockServiceInfo = new GatewayLockServiceInfo(deviceId + uid, gwLock.getDeviceId(),
                                    gwLock.getSW(), gwLock.getDevice_type(), gwLock.getEvent_str(), gwLock.getIpaddr(), gwLock.getMacaddr(),
                                    gwLock.getNickName(), gwLock.getTime(), gwLockInfo.getGwID(), uid,
                                    gwLock.getDelectTime(), gwLock.getLockversion(), gwLock.getModuletype(), gwLock.getNwaddr(), gwLock.getOfflineTime(),
                                    gwLock.getOnlineTime(), gwLock.getShareFlag(), gwLock.getPushSwitch());
                            daoSession.insertOrReplace(gatewayLockServiceInfo);
                            mDeviceList.add(homeShowBean);
                            break;
                        case HomeShowBean.TYPE_CAT_EYE:
                            //猫眼
                            CateEyeInfo cateEyeInfo = (CateEyeInfo) homeShowBean.getObject();
                            if (cateEyeInfo != null) {
                                if (cateEyeInfo.getServerInfo() != null) {
                                    SPUtils.put(cateEyeInfo.getServerInfo().getDeviceId(), cateEyeInfo.getGwID());
                                }
                                GatewayInfo gat = MyApplication.getInstance().getGatewayById(cateEyeInfo.getGwID());
                                if (gat != null && gat.getEvent_str() != null && gat.getEvent_str().equals("offline")) {
                                    cateEyeInfo.getServerInfo().setEvent_str("offline");
                                } else if (gat != null && gat.getEvent_str() == null) {
                                    cateEyeInfo.getServerInfo().setEvent_str("offline");
                                }
                            }
                            DevicePower catPower = daoSession.getDevicePowerDao().queryBuilder().where(DevicePowerDao.Properties.DeviceIdUid.eq(cateEyeInfo.getServerInfo().getDeviceId() + uid)).unique();
                            if (catPower != null) {
                                cateEyeInfo.setPower(catPower.getPower());
                            }
                            //请求电量
                            if (!cateEyeInfo.getServerInfo().getEvent_str().equals("offline")) {
                                mPresenter.getPower(cateEyeInfo.getGwID(), cateEyeInfo.getServerInfo().getDeviceId(), MyApplication.getInstance().getUid());
                            }

                            //插入数据库
                            ServerGwDevice gwDevice = cateEyeInfo.getServerInfo();
                            String catDeviceId = gwDevice.getDeviceId();
                            CatEyeServiceInfo catEyeServiceInfo = new CatEyeServiceInfo(catDeviceId + uid, gwDevice.getDeviceId(),
                                    gwDevice.getSW(), gwDevice.getDevice_type(), gwDevice.getEvent_str(), gwDevice.getIpaddr(), gwDevice.getMacaddr(),
                                    gwDevice.getNickName(), gwDevice.getTime(), cateEyeInfo.getGwID(), uid,
                                    gwDevice.getDelectTime(), gwDevice.getLockversion(), gwDevice.getModuletype(), gwDevice.getNwaddr(), gwDevice.getOfflineTime(),
                                    gwDevice.getOnlineTime(), gwDevice.getShareFlag(), gwDevice.getPushSwitch());
                            daoSession.insertOrReplace(catEyeServiceInfo);
                            mDeviceList.add(homeShowBean);
                            break;
                        case HomeShowBean.TYPE_GATEWAY:
                            //网关
                            GatewayInfo gatewayInfo = (GatewayInfo) homeShowBean.getObject();
                            LogUtils.e("网关信息进入");
                            ServerGatewayInfo serverGatewayInfo = gatewayInfo.getServerInfo();
                            String deviceSn = serverGatewayInfo.getDeviceSN();
                            //插入数据库
                            GatewayServiceInfo gatewayServiceInfo =
                                    new GatewayServiceInfo(
                                            deviceSn + uid, serverGatewayInfo.getDeviceSN(), serverGatewayInfo.getDeviceNickName(), serverGatewayInfo.getAdminuid(), serverGatewayInfo.getAdminName(), serverGatewayInfo.getAdminNickname()
                                            , serverGatewayInfo.getIsAdmin(), serverGatewayInfo.getMeUsername(),
                                            serverGatewayInfo.getMePwd(), serverGatewayInfo.getMeBindState(), uid,
                                            serverGatewayInfo.getModel());
                            daoSession.getGatewayServiceInfoDao().insertOrReplace(gatewayServiceInfo);
                            //咪咪网未绑定
                            if (gatewayInfo.getServerInfo().getMeBindState() != 1 && gatewayServiceInfo.getModel() != null &&
                                    gatewayServiceInfo.getModel().equals(KeyConstants.BIG_GW)) {
                                //需要绑定咪咪网
                                String deviceSN = gatewayInfo.getServerInfo().getDeviceSN();
                                mPresenter.bindMimi(deviceSN, deviceSN);
                            } else {
                                try {
                                    String gwid = gatewayInfo.getServerInfo().getDeviceSN();
                                    String meName = gatewayInfo.getServerInfo().getMeUsername();
                                    String mePwd = gatewayInfo.getServerInfo().getMePwd();
                                    String me = meName + "&" + mePwd;
                                    if (!TextUtils.isEmpty(gwid) && !TextUtils.isEmpty(meName) && !TextUtils.isEmpty(mePwd)) {
                                        SPUtils.put(gwid, me);
                                    }
                                } catch (Exception e) {
                                    LogUtils.e(e.getMessage());
                                }

                            }
                            mDeviceList.add(homeShowBean);
                            break;
                        case HomeShowBean.TYPE_BLE_LOCK:
                            //蓝牙锁
                            BleLockInfo bleLockInfo = (BleLockInfo) homeShowBean.getObject();
                            ServerBleDevice serverBleDevice = bleLockInfo.getServerLockInfo();
                            if (serverBleDevice != null) {
                                BleLockServiceInfo bleLockServiceInfo = new BleLockServiceInfo();
                                bleLockServiceInfo.setLockName(serverBleDevice.getLockName());
                                bleLockServiceInfo.setLockNickName(serverBleDevice.getLockNickName());
                                bleLockServiceInfo.setMacLock(serverBleDevice.getMacLock());  //缓存
                                bleLockServiceInfo.setOpen_purview(serverBleDevice.getOpen_purview());
                                bleLockServiceInfo.setIs_admin(serverBleDevice.getIs_admin());
                                bleLockServiceInfo.setCenter_latitude(serverBleDevice.getCenter_latitude());
                                bleLockServiceInfo.setCenter_longitude(serverBleDevice.getCenter_longitude());
                                bleLockServiceInfo.setCircle_radius(serverBleDevice.getCircle_radius());
                                bleLockServiceInfo.setAuto_lock(serverBleDevice.getAuto_lock());
                                bleLockServiceInfo.setPassword1(serverBleDevice.getPassword1());
                                bleLockServiceInfo.setPassword2(serverBleDevice.getPassword2());
                                bleLockServiceInfo.setModel(serverBleDevice.getModel());

                                bleLockServiceInfo.setDeviceSN(serverBleDevice.getDeviceSN());
                                bleLockServiceInfo.setCreateTime(serverBleDevice.getCreateTime());
                                bleLockServiceInfo.setSoftwareVersion(serverBleDevice.getSoftwareVersion());
                                bleLockServiceInfo.setBleVersion(serverBleDevice.getBleVersion());
                                bleLockServiceInfo.setUid(uid);
                                bleLockServiceInfo.setFunctionSet(serverBleDevice.getFunctionSet());
                                daoSession.insertOrReplace(bleLockServiceInfo);

                                //请求电量
                                DevicePower blePower = daoSession.getDevicePowerDao().queryBuilder().where(DevicePowerDao.Properties.DeviceSN.eq(serverBleDevice.getDeviceSN() + uid)).unique();
                                if (blePower != null) {
                                    bleLockInfo.setBattery(blePower.getPower());
                                }

                            }
                            mDeviceList.add(homeShowBean);
                            break;
                        case HomeShowBean.TYPE_WIFI_LOCK:
                            WifiLockInfo wifiLockInfo = (WifiLockInfo) homeShowBean.getObject();
                            mDeviceList.add(homeShowBean);
                            break;
                    }
                }
                if (deviceDetailAdapter != null) {
                    deviceDetailAdapter.notifyDataSetChanged();
                } else {
                    initAdapter();
                }
            } else {
                noDeviceLayout.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.GONE);
            }
        } else {
            noDeviceLayout.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.GONE);
        }
        if (mDeviceList != null && mDeviceList.size() > 0) {
            for (int i = 0; i < mDeviceList.size(); i++) {
                HomeShowBean homeShowBean = mDeviceList.get(i);
                if (HomeShowBean.TYPE_CAT_EYE == homeShowBean.getDeviceType()) { //有网关
                    boolean isFlag = NotificationManagerCompat.from(getActivity()).areNotificationsEnabled();
                    if ((!isFlag && Rom.isOppo()) || (!isFlag && Rom.isVivo())) {
                        AlertDialogUtil.getInstance().noEditTwoButtonDialogWidthDialog_color_padding(
                                getActivity(),
                                getString(R.string.mainactivity_permission_alert_title),
                                getString(R.string.mainactivity_permission_alert_msg),
                                getString(R.string.cancel),
                                getString(R.string.confirm),
                                new ClickListener() {

                                    @Override
                                    public void left() {

                                    }

                                    @Override
                                    public void right() {
                                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            Intent intent = new Intent();
                                            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                            intent.setData(Uri.fromParts("package", "com.kaidishi.lock", null));
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                }
            }
        }else {
            noDeviceLayout.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.GONE);
        }
    }


    private void initRefresh() {
        refresh.setEnableLoadMore(false);
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //刷新页面
                if (NetUtil.isNetworkAvailable()) {
                    if (mqttService != null && mqttService.getMqttClient() != null && !mqttService.getMqttClient().isConnected()) {
                        MyApplication.getInstance().getMqttService().mqttConnection(); //重新连接mqtt
                        LogUtils.e("重新连接mqtt");

                    }
                    mPresenter.refreshData();
                    refreshLayout.finishRefresh(8 * 1000);
                } else {
                    ToastUtil.getInstance().showShort(getString(R.string.network_exception));
                    refreshLayout.finishRefresh();
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.device_add, R.id.buy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.device_add:
                Intent deviceAdd = new Intent(getActivity(), DeviceAdd2Activity.class);
                startActivity(deviceAdd);
                break;
            case R.id.buy:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://www.kaadas.com/");//此处填链接
                intent.setData(content_url);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (mDeviceList != null) {
            if (mDeviceList.size() > position) {
                LogUtils.e("设备总和  " + mDeviceList.size() + "位置  " + position);
                HomeShowBean deviceDetailBean = mDeviceList.get(position);
                switch (deviceDetailBean.getDeviceType()) {
                    case HomeShowBean.TYPE_CAT_EYE:
                        //猫眼
                        CateEyeInfo cateEyeInfo = (CateEyeInfo) deviceDetailBean.getObject();
                        GatewayInfo cateGw = MyApplication.getInstance().getGatewayById(cateEyeInfo.getGwID());
                        if (cateGw != null && cateGw.getServerInfo().getIsAdmin() == 1) {
                            //管理员
                            Intent cateEyeInfoIntent = new Intent(getActivity(), CateyeFunctionActivity.class);
                            cateEyeInfoIntent.putExtra(KeyConstants.CATE_INFO, deviceDetailBean);
                            startActivity(cateEyeInfoIntent);
                        } else {
                            //授权
                            Intent cateEyeAuthorizationInfoIntent = new Intent(getActivity(), CateyeAuthorizationFunctionActivity.class);
                            cateEyeAuthorizationInfoIntent.putExtra(KeyConstants.CATE_INFO, deviceDetailBean);
                            startActivity(cateEyeAuthorizationInfoIntent);
                        }
                        break;
                    case HomeShowBean.TYPE_GATEWAY_LOCK:
                        GwLockInfo lockInfo = (GwLockInfo) deviceDetailBean.getObject();
                        GatewayInfo gw = MyApplication.getInstance().getGatewayById(lockInfo.getGwID());
                        String gatewayModel = gw.getServerInfo().getModel();   // 6032 网关
                        if (gw.getServerInfo().getIsAdmin() == 1) {
                            //网关锁
                            Intent gatewayLockintent = new Intent(getActivity(), GatewayLockFunctionActivity.class);
                            gatewayLockintent.putExtra(KeyConstants.GATEWAY_LOCK_INFO, deviceDetailBean);
                            gatewayLockintent.putExtra(KeyConstants.GATEWAY_MODEL,gatewayModel);
                            startActivity(gatewayLockintent);
                        } else {
                            //授权锁
                            //网关锁
                            Intent gatewayLockintent = new Intent(getActivity(), GatewayLockAuthorizeFunctionActivity.class);
                            gatewayLockintent.putExtra(KeyConstants.GATEWAY_LOCK_INFO, deviceDetailBean);
                            gatewayLockintent.putExtra(KeyConstants.GATEWAY_MODEL,gatewayModel);
                            startActivity(gatewayLockintent);
                        }

                        break;
                    case HomeShowBean.TYPE_GATEWAY:
                        //网关
                        Intent gatwayInfo = new Intent(getActivity(), GatewayActivity.class);
                        gatwayInfo.putExtra(KeyConstants.GATEWAY_INFO, deviceDetailBean);
                        startActivity(gatwayInfo);
                        break;
                    case HomeShowBean.TYPE_BLE_LOCK:
                        //蓝牙
                        BleLockInfo bleLockInfo = (BleLockInfo) deviceDetailBean.getObject();
                        mPresenter.setBleLockInfo(bleLockInfo);
                        if (bleLockInfo.getServerLockInfo().getIs_admin() != null && bleLockInfo.getServerLockInfo().getIs_admin().equals("1")) {
                            if ("3".equals(bleLockInfo.getServerLockInfo().getBleVersion())) {
                                String functionSet = bleLockInfo.getServerLockInfo().getFunctionSet();
                                LogUtils.e("蓝牙的功能集是   " + functionSet);
                                if (!TextUtils.isEmpty(functionSet) && Integer.parseInt(functionSet) == 0) {
                                    LogUtils.e("跳转   老蓝牙");
                                    Intent detailIntent = new Intent(getActivity(), OldBleDetailActivity.class);
                                    String model = bleLockInfo.getServerLockInfo().getModel();
                                    detailIntent.putExtra(KeyConstants.DEVICE_TYPE, model);
                                    startActivityForResult(detailIntent, KeyConstants.GET_BLE_POWER);
                                } else {
                                    LogUtils.e("跳转   全功能  蓝牙");
                                    Intent detailIntent = new Intent(getActivity(), BleDetailActivity.class);
                                    String model = bleLockInfo.getServerLockInfo().getModel();
                                    detailIntent.putExtra(KeyConstants.DEVICE_TYPE, model);
                                    startActivityForResult(detailIntent, KeyConstants.GET_BLE_POWER);
                                }
                            } else {
                                LogUtils.e("跳转   老蓝牙");
                                Intent detailIntent = new Intent(getActivity(), OldBleDetailActivity.class);
                                String model = bleLockInfo.getServerLockInfo().getModel();
                                detailIntent.putExtra(KeyConstants.DEVICE_TYPE, model);
                                startActivityForResult(detailIntent, KeyConstants.GET_BLE_POWER);
                            }
                        } else {
                            LogUtils.e("跳转 授权  蓝牙");
                            Intent impowerIntent = new Intent(getActivity(), BleAuthActivity.class);
                            String model = bleLockInfo.getServerLockInfo().getModel();
                            impowerIntent.putExtra(KeyConstants.DEVICE_TYPE, model);
                            startActivityForResult(impowerIntent, KeyConstants.GET_BLE_POWER);
                        }
                        break;
                    case HomeShowBean.TYPE_WIFI_LOCK:
                        WifiLockInfo wifiLockInfo = (WifiLockInfo) deviceDetailBean.getObject();
                        if (!TextUtils.isEmpty(wifiLockInfo.getFunctionSet())) {
                            if (wifiLockInfo.getIsAdmin() == 1) { //主用户
                                Intent intent = new Intent(getActivity(), WiFiLockDetailActivity.class);
                                intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                                startActivity(intent);
                            } else { //分享用户
                                LogUtils.e("分享 wifi锁  用户  " + wifiLockInfo.toString());
                                Intent intent = new Intent(getActivity(), WifiLockAuthActivity.class);
                                intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                                startActivity(intent);
                            }
                        } else {
                            ToastUtil.getInstance().showLong(R.string.lock_info_not_push);
                        }
                        break;
                }
            } else {
                ToastUtil.getInstance().showShort(R.string.please_refresh_page_get_newdata);
            }
        }
    }

    @Override
    public void onDeviceRefresh(AllBindDevices allBindDevices) {
        //数据更新了
        if (refresh != null) {
            refresh.finishRefresh();
        }
        if (allBindDevices != null) {
            homeShowBeanList = MyApplication.getInstance().getAllDevices();
            initData(homeShowBeanList);
        } else {
            initData(null);
        }

    }


    @Override
    public void deviceDataRefreshFail() {
        LogUtils.e("刷新页面失败");
        refresh.finishRefresh();
        ToastUtil.getInstance().showShort(R.string.refresh_data_fail);
    }

    @Override
    public void deviceDataRefreshThrowable(Throwable throwable) {
        //刷新页面异常
        refresh.finishRefresh();
        ToastUtil.getInstance().showShort(R.string.refresh_data_fail);
        LogUtils.e("刷新页面异常");
    }

    @Override
    public void getDevicePowerSuccess(String gatewayId, String devciceId, int power, String timestamp) {
        LogUtils.e("设备SN" + devciceId + "设备电量" + power);
        if (mDeviceList != null && mDeviceList.size() > 0) {
            for (HomeShowBean device : mDeviceList) {
                //猫眼电量
                if (HomeShowBean.TYPE_CAT_EYE == device.getDeviceType()) {
                    if (device.getDeviceId().equals(devciceId)) {
                        CateEyeInfo cateEyeInfo = (CateEyeInfo) device.getObject();
                        cateEyeInfo.setPower(power);
                        cateEyeInfo.setPowerTimeStamp(timestamp);
                        DevicePower devicePower = new DevicePower(devciceId + uid, devciceId, power);
                        daoSession.insertOrReplace(devicePower);
                        /*if (cateEyeInfo.getServerInfo().getEvent_str().equals("offline")) {
                            cateEyeInfo.getServerInfo().setEvent_str("online");
                        }*/
                        if (deviceDetailAdapter != null) {
                            deviceDetailAdapter.notifyDataSetChanged();
                        }
                    }
                } else if (HomeShowBean.TYPE_GATEWAY_LOCK == device.getDeviceType()) {
                    if (device.getDeviceId().equals(devciceId)) {
                        GwLockInfo gwLockInfo = (GwLockInfo) device.getObject();
                       /* if (gwLockInfo.getServerInfo().getEvent_str().equals("offline")) {
                            gwLockInfo.getServerInfo().setEvent_str("online");
                        }*/
                        gwLockInfo.setPower(power);
                        gwLockInfo.setPowerTimeStamp(timestamp);
                        //缓存电量
                        DevicePower devicePower = new DevicePower(devciceId + uid, devciceId, power);
                        daoSession.insertOrReplace(devicePower);
                        if (deviceDetailAdapter != null) {
                            deviceDetailAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }


    }

    @Override
    public void getDevicePowerFail(String gatewayId, String deviceId) {


    }

    @Override
    public void getDevicePowerThrowable(String gatewayId, String deviceId) {

    }

    @Override
    public void gatewayStatusChange(String gatewayId, String evnetStr) {
        //网关状态发生改变
        LogUtils.e("DeviceFragment网关状态发生改变");
        if (mDeviceList != null && mDeviceList.size() > 0) {
            for (HomeShowBean device : mDeviceList) {
                //网关
                if (device.getDeviceType() == HomeShowBean.TYPE_GATEWAY) {
                    GatewayInfo gatewayInfo = (GatewayInfo) device.getObject();
                    if (gatewayInfo.getServerInfo().getDeviceSN().equals(gatewayId)) {
                        LogUtils.e("监听网关Device的状态      " + gatewayId + "连接状态" + evnetStr);
                        gatewayInfo.setEvent_str(evnetStr);
                        //获取网关下绑定的设备,把网关下的设备设置为离线.网关离线设备也离线
                        if ("offline".equals(evnetStr)) {
                            switch (device.getDeviceType()) {
                                //猫眼
                                case HomeShowBean.TYPE_CAT_EYE:
                                    CateEyeInfo cateEyeInfo = (CateEyeInfo) device.getObject();
                                    if (cateEyeInfo.getGwID().equals(gatewayId)) {
                                        cateEyeInfo.getServerInfo().setEvent_str("offline");
                                    }
                                    break;
                                //网关锁
                                case HomeShowBean.TYPE_GATEWAY_LOCK:
                                    GwLockInfo gwLockInfo = (GwLockInfo) device.getObject();
                                    if (gwLockInfo.getGwID().equals(gatewayId)) {
                                        gwLockInfo.getServerInfo().setEvent_str("offline");
                                    }
                                    break;
                            }

                        }
                        if (deviceDetailAdapter != null) {
                            deviceDetailAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

    }

    @Override
    public void deviceStatusChange(String gatewayId, String deviceId, String eventStr) {
        //设备状态发生改变
        LogUtils.e("DeviceFragment设备状态发生改变");
        if (mDeviceList != null && mDeviceList.size() > 0) {
            for (HomeShowBean homeShowBean : mDeviceList) {
                if (deviceId.equals(homeShowBean.getDeviceId())) {
                    switch (homeShowBean.getDeviceType()) {
                        //猫眼上线
                        case HomeShowBean.TYPE_CAT_EYE:
                            CateEyeInfo cateEyeInfo = (CateEyeInfo) homeShowBean.getObject();
                            if (cateEyeInfo.getGwID().equals(gatewayId) && cateEyeInfo.getServerInfo().getDeviceId().equals(deviceId)) {
                                if ("online".equals(eventStr)) {
                                    cateEyeInfo.getServerInfo().setEvent_str("online");
                                } else {
                                    cateEyeInfo.getServerInfo().setEvent_str("offline");
                                }
                                if (deviceDetailAdapter != null) {
                                    deviceDetailAdapter.notifyDataSetChanged();
                                }
                                LogUtils.e("猫眼上线下线了   " + eventStr + "猫眼的设备id  " + deviceId);
                            }
                            break;
                        //网关锁上线
                        case HomeShowBean.TYPE_GATEWAY_LOCK:
                            GwLockInfo gwLockInfo = (GwLockInfo) homeShowBean.getObject();
                            if (gwLockInfo.getGwID().equals(gatewayId) && gwLockInfo.getServerInfo().getDeviceId().equals(deviceId)) {
                                if ("online".equals(eventStr)) {
                                    gwLockInfo.getServerInfo().setEvent_str("online");
                                } else if ("offline".equals(eventStr)) {
                                    gwLockInfo.getServerInfo().setEvent_str("offline");
                                }
                                if (deviceDetailAdapter != null) {
                                    deviceDetailAdapter.notifyDataSetChanged();
                                }
                                LogUtils.e("网关锁上线下线了   " + eventStr + "网关的设备id  " + deviceId);
                            }
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void networkChangeSuccess() {
        //网络断开
        if (deviceDetailAdapter != null) {
            deviceDetailAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void bindMimiSuccess(String deviceSN) {
        //绑定咪咪网成功
        LogUtils.e("咪咪绑定注册成功");
    }

    @Override
    public void bindMimiFail(String code, String msg) {
        LogUtils.e("咪咪绑定注册失败" + code + "咪咪绑定失败原因" + msg);
    }

    @Override
    public void bindMimiThrowable(Throwable throwable) {
        LogUtils.e("咪咪绑定异常");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser == true) {
            //切换左右切换Fragment时，刷新页面
            if (mDeviceList != null && mDeviceList.size() > 0) {
                if (mqttService != null && mqttService.getMqttClient() != null && !mqttService.getMqttClient().isConnected()) {
                    LogUtils.e("重连次数" + mqttService.reconnectionNum);
                    if (mqttService.reconnectionNum == 0) {
                        ToastUtil.getInstance().showShort(getString(R.string.mqtt_already_disconnect_refresh));
                    }
                }
                if (deviceDetailAdapter != null) {
                    deviceDetailAdapter.notifyDataSetChanged();
                }
            }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KeyConstants.GET_BLE_POWER) {
            if (resultCode == Activity.RESULT_OK) {
                BleLockInfo getBle = (BleLockInfo) data.getSerializableExtra(KeyConstants.BLE_INTO);
                if (getBle != null && mDeviceList != null && mDeviceList.size() > 0) {
                    for (HomeShowBean device : mDeviceList) {
                        //蓝牙电量
                        if (HomeShowBean.TYPE_BLE_LOCK == device.getDeviceType()) {
                            if (device.getDeviceId().equals(getBle.getServerLockInfo().getLockName())) {
                                BleLockInfo bleLockInfo = (BleLockInfo) device.getObject();
                                bleLockInfo.setBattery(getBle.getBattery());
                                String deviceSN = bleLockInfo.getServerLockInfo().getDeviceSN();
                                DevicePower devicePower = new DevicePower(deviceSN + uid, deviceSN, getBle.getBattery());
                                daoSession.insertOrReplace(devicePower);
                                if (getBle.isConnected()) {
                                    bleLockInfo.setConnected(true);
                                } else {
                                    bleLockInfo.setConnected(false);
                                }
                                if (deviceDetailAdapter != null) {
                                    deviceDetailAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}
