package com.kaadas.lock.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddActivity;
import com.kaadas.lock.activity.device.BluetoothLockAuthorizationActivity;
import com.kaadas.lock.activity.device.BluetoothLockFunctionActivity;
import com.kaadas.lock.activity.device.gateway.GatewayActivity;
import com.kaadas.lock.activity.device.cateye.more.CateyeFunctionActivity;
import com.kaadas.lock.activity.device.gatewaylock.GatewayLockFunctionActivity;
import com.kaadas.lock.activity.device.oldbluetooth.OldBluetoothLockDetailActivity;
import com.kaadas.lock.adapter.DeviceDetailAdapter;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.DevicePresenter;
import com.kaadas.lock.mvp.view.IDeviceView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rom;
import com.kaadas.lock.utils.SPUtils2;
import com.kaadas.lock.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by asqw1 on 2018/3/14.
 */

public class DeviceFragment extends BaseFragment<IDeviceView, DevicePresenter<IDeviceView>> implements BaseQuickAdapter.OnItemClickListener,IDeviceView {
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

    private Boolean flag = false;

    private DeviceDetailAdapter deviceDetailAdapter;

    private List<HomeShowBean> mDeviceList=new ArrayList<>();
    private  List<HomeShowBean> homeShowBeanList;

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
            for (int i=0;i<mDeviceList.size();i++){
               if(HomeShowBean.TYPE_GATEWAY ==mDeviceList.get(i).getDeviceType()) {
                   Object obj=mDeviceList.get(i).getObject();
                   String gwid= mDeviceList.get(i).getDeviceId();
                   if(obj instanceof GatewayInfo){
                       GatewayInfo gatewayInfo=(GatewayInfo)obj;
                       String meUsername= gatewayInfo.getServerInfo().getMeUsername();
                       String mePwd= gatewayInfo.getServerInfo().getMePwd();
                       SPUtils2.put(getActivity(),gwid,meUsername+"&"+mePwd);
                   }
               }
            }
        }

    }

    private void initData(List<HomeShowBean> homeShowBeanList) {
        mDeviceList.clear();
        if (homeShowBeanList!=null){
            if (homeShowBeanList.size()>0){
                noDeviceLayout.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
                mPresenter.getPublishNotify();
                mPresenter.listenerDeviceOnline();
                mPresenter.listenerNetworkChange();
                for (HomeShowBean homeShowBean:homeShowBeanList){
                    LogUtils.e(homeShowBeanList.size()+"获取到大小     "+"获取到昵称  "+homeShowBean.getDeviceNickName());
                    //请求电量
                    switch (homeShowBean.getDeviceType()){
                        case HomeShowBean.TYPE_GATEWAY_LOCK:
                            //网关锁
                            GwLockInfo gwLockInfo= (GwLockInfo) homeShowBean.getObject();
                            mPresenter.getPower(gwLockInfo.getGwID(),gwLockInfo.getServerInfo().getDeviceId(),MyApplication.getInstance().getUid());
                            break;
                        case HomeShowBean.TYPE_CAT_EYE:
                            //猫眼
                            CateEyeInfo cateEyeInfo= (CateEyeInfo) homeShowBean.getObject();
                            mPresenter.getPower(cateEyeInfo.getGwID(),cateEyeInfo.getServerInfo().getDeviceId(),MyApplication.getInstance().getUid());
                            break;
                        case HomeShowBean.TYPE_GATEWAY:
                            //网关
                            GatewayInfo gatewayInfo= (GatewayInfo) homeShowBean.getObject();
                            //咪咪网未绑定
                            if (gatewayInfo.getServerInfo().getMeBindState()!=1){
                                //需要绑定咪咪网
                                String deviceSN=gatewayInfo.getServerInfo().getDeviceSN();
                                mPresenter.bindMimi(deviceSN,deviceSN);
                            }

                            break;
                    }

                    mDeviceList.add(homeShowBean);
                }
                if (deviceDetailAdapter!=null){
                    deviceDetailAdapter.notifyDataSetChanged();
                }else{
                    initAdapter();
                }
            }else{
                noDeviceLayout.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.GONE);
            }
        }else{
            noDeviceLayout.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.GONE);
        }
        if(mDeviceList!=null){
            for (int i=0;i<mDeviceList.size();i++){
                HomeShowBean homeShowBean=mDeviceList.get(i);
                if(HomeShowBean.TYPE_CAT_EYE==homeShowBean.getDeviceType()){
                    boolean isFlag= NotificationManagerCompat.from(getActivity()).areNotificationsEnabled();
                    if(  !isFlag && Rom.isOppo()){
                        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(getActivity());
                        //	normalDialog.setIcon(R.drawable.icon_dialog);
                        normalDialog.setTitle(getString(R.string.mainactivity_permission_alert_title));
                        normalDialog.setMessage(getString(R.string.mainactivity_permission_alert_msg));
                        normalDialog.setPositiveButton(getString(R.string.confirm),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            Intent intent  = new Intent();
                                            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                            intent.putExtra(Settings.EXTRA_APP_PACKAGE,"com.kaidishi.lock");
                                            getActivity().startActivity(intent);
                                        }
                                    }
                                });
                        normalDialog.setNegativeButton(getActivity().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                }
                                });
                        // 显示
                        AlertDialog dialog= normalDialog.create();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }
                    break;
                }
            }
        }
    }


    private void initRefresh() {
        refresh.setEnableLoadMore(false);
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //刷新页面
                mPresenter.refreshData();
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
                Intent deviceAdd = new Intent(getActivity(), DeviceAddActivity.class);
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
        HomeShowBean deviceDetailBean = mDeviceList.get(position);
        switch (deviceDetailBean.getDeviceType()) {
            case HomeShowBean.TYPE_CAT_EYE:
                //猫眼
                Intent  cateEyeInfoIntent=new Intent(getActivity(),CateyeFunctionActivity.class);
                cateEyeInfoIntent.putExtra(KeyConstants.CATE_INFO, deviceDetailBean);
                startActivity(cateEyeInfoIntent);
                break;
            case HomeShowBean.TYPE_GATEWAY_LOCK:
                //网关锁
                Intent gatewayLockintent=new Intent(getActivity(),GatewayLockFunctionActivity.class);
                gatewayLockintent.putExtra(KeyConstants.GATEWAY_LOCK_INFO,deviceDetailBean);
                startActivity(gatewayLockintent);
                break;
            case HomeShowBean.TYPE_GATEWAY:
                //网关
                Intent gatwayInfo=new Intent(getActivity(), GatewayActivity.class);
                gatwayInfo.putExtra(KeyConstants.GATEWAY_INFO,deviceDetailBean);
                startActivity(gatwayInfo);
                break;
            case HomeShowBean.TYPE_BLE_LOCK:
                //蓝牙
                BleLockInfo bleLockInfo = (BleLockInfo) deviceDetailBean.getObject();
                mPresenter.setBleLockInfo(bleLockInfo);
                if (bleLockInfo.getServerLockInfo().getIs_admin() != null && bleLockInfo.getServerLockInfo().getIs_admin().equals("1")) {
                    if ("3".equals(bleLockInfo.getServerLockInfo().getBleVersion())){
                        Intent detailIntent = new Intent(getActivity(), BluetoothLockFunctionActivity.class);
                        String model = bleLockInfo.getServerLockInfo().getModel();
                        detailIntent.putExtra(KeyConstants.DEVICE_TYPE, model);
                        detailIntent.putExtra(KeyConstants.BLE_DEVICE_INFO, bleLockInfo);
                        startActivityForResult(detailIntent,KeyConstants.GET_BLE_POWER);
                    }else {
                        Intent detailIntent = new Intent(getActivity(), OldBluetoothLockDetailActivity.class);
                        String model = bleLockInfo.getServerLockInfo().getModel();
                        detailIntent.putExtra(KeyConstants.DEVICE_TYPE, model);
                        detailIntent.putExtra(KeyConstants.BLE_DEVICE_INFO, bleLockInfo);
                        startActivityForResult(detailIntent,KeyConstants.GET_BLE_POWER);
                    }
                } else {
                    Intent impowerIntent = new Intent(getActivity(), BluetoothLockAuthorizationActivity.class);
                    String model = bleLockInfo.getServerLockInfo().getModel();
                    impowerIntent.putExtra(KeyConstants.DEVICE_TYPE, model);
                    impowerIntent.putExtra(KeyConstants.BLE_DEVICE_INFO, bleLockInfo);
                    startActivityForResult(impowerIntent,KeyConstants.GET_BLE_POWER);
                }
                break;
        }
    }

    @Override
    public void onDeviceRefresh(AllBindDevices allBindDevices) {
        //数据更新了
        if (refresh!=null){
            refresh.finishRefresh();
        }
        if (allBindDevices !=null){
            homeShowBeanList = MyApplication.getInstance().getAllDevices();
            initData(homeShowBeanList);
        }else {
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
        LogUtils.e("刷新页面异常");
    }

    @Override
    public void getDevicePowerSuccess(String gatewayId,String devciceId,int power,String timestamp) {
        LogUtils.e("设备SN"+devciceId+"设备电量"+power);
        if (mDeviceList!=null&&mDeviceList.size()>0) {
            for (HomeShowBean device : mDeviceList) {
                //猫眼电量
                if (HomeShowBean.TYPE_CAT_EYE==device.getDeviceType()){
                    if (device.getDeviceId().equals(devciceId)){
                        CateEyeInfo cateEyeInfo= (CateEyeInfo) device.getObject();
                        cateEyeInfo.setPower(power);
                        cateEyeInfo.setPowerTimeStamp(timestamp);
                        if (cateEyeInfo.getServerInfo().getEvent_str().equals("offline")){
                            cateEyeInfo.getServerInfo().setEvent_str("online");
                        }
                        if (deviceDetailAdapter!=null) {
                            deviceDetailAdapter.notifyDataSetChanged();
                        }
                    }
                 }else if (HomeShowBean.TYPE_GATEWAY_LOCK==device.getDeviceType()){
                    if (device.getDeviceId().equals(devciceId)){
                        GwLockInfo gwLockInfo= (GwLockInfo) device.getObject();
                        if (gwLockInfo.getServerInfo().getEvent_str().equals("offline")){
                            gwLockInfo.getServerInfo().setEvent_str("online");
                        }
                        gwLockInfo.setPower(power);
                        gwLockInfo.setPowerTimeStamp(timestamp);
                        if (deviceDetailAdapter!=null) {
                            deviceDetailAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }



    }

    @Override
    public void getDevicePowerFail(String gatewayId,String deviceId) {
        //获取电量失败
        if (mDeviceList!=null&&mDeviceList.size()>0) {
            for (HomeShowBean device : mDeviceList) {
                //猫眼电量
                if (HomeShowBean.TYPE_CAT_EYE==device.getDeviceType()){
                    if (device.getDeviceId().equals(deviceId)){
                        CateEyeInfo cateEyeInfo= (CateEyeInfo) device.getObject();
                        cateEyeInfo.getServerInfo().setEvent_str("offline");
                        if (deviceDetailAdapter!=null) {
                            deviceDetailAdapter.notifyDataSetChanged();
                        }
                    }
                }else if (HomeShowBean.TYPE_GATEWAY_LOCK==device.getDeviceType()){
                    if (device.getDeviceId().equals(deviceId)){
                        GwLockInfo gwLockInfo= (GwLockInfo) device.getObject();
                        gwLockInfo.getServerInfo().setEvent_str("offline");
                        if (deviceDetailAdapter!=null) {
                            deviceDetailAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

    }

    @Override
    public void getDevicePowerThrowable(Throwable throwable) {

    }

    @Override
    public void gatewayStatusChange(String gatewayId,String evnetStr) {
        //网关状态发生改变
        LogUtils.e("DeviceFragment网关状态发生改变");
        if (mDeviceList!=null&&mDeviceList.size()>0) {
            for (HomeShowBean device : mDeviceList) {
                //网关
                if (device.getDeviceType() == HomeShowBean.TYPE_GATEWAY) {
                    GatewayInfo gatewayInfo = (GatewayInfo) device.getObject();
                    if (gatewayInfo.getServerInfo().getDeviceSN().equals(gatewayId)) {
                        LogUtils.e("监听网关Device的状态      " + gatewayId+"连接状态"+evnetStr);
                        gatewayInfo.setEvent_str(evnetStr);
                        //获取网关下绑定的设备,把网关下的设备设置为离线.网关离线设备也离线
                        if ("offline".equals(evnetStr)) {
                            List<HomeShowBean> gatewayBindList = MyApplication.getInstance().getGatewayBindList(gatewayId);
                            for (HomeShowBean gatewayBind : gatewayBindList) {
                                switch (gatewayBind.getDeviceType()) {
                                    //猫眼
                                    case HomeShowBean.TYPE_CAT_EYE:
                                        CateEyeInfo cateEyeInfo = (CateEyeInfo) gatewayBind.getObject();
                                        cateEyeInfo.getServerInfo().setEvent_str("offline");
                                        break;
                                    //网关锁
                                    case HomeShowBean.TYPE_GATEWAY_LOCK:
                                        GwLockInfo gwLockInfo= (GwLockInfo) gatewayBind.getObject();
                                        gwLockInfo.getServerInfo().setEvent_str("offline");
                                        break;
                                }
                            }
                        }
                        if (deviceDetailAdapter!=null) {
                            deviceDetailAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

    }

    @Override
    public void deviceStatusChange(String gatewayId,String deviceId,String eventStr) {
        //网关状态发生改变
        LogUtils.e("DeviceFragment设备状态发生改变");
        if (mDeviceList!=null&&mDeviceList.size()>0) {
            for (HomeShowBean homeShowBean : mDeviceList) {
                if (deviceId.equals(homeShowBean.getDeviceId())) {
                    switch (homeShowBean.getDeviceType()) {
                        //猫眼上线
                        case HomeShowBean.TYPE_CAT_EYE:
                            CateEyeInfo cateEyeInfo= (CateEyeInfo) homeShowBean.getObject();
                            if (cateEyeInfo.getGwID().equals(gatewayId)) {
                                if ("online".equals(eventStr)) {
                                    cateEyeInfo.getServerInfo().setEvent_str("online");
                                } else {
                                    cateEyeInfo.getServerInfo().setEvent_str("offline");
                                }
                                if (deviceDetailAdapter!=null) {
                                    deviceDetailAdapter.notifyDataSetChanged();
                                }
                                LogUtils.e("猫眼上线下线了   "+eventStr+"猫眼的设备id  "+deviceId);
                            }
                            break;
                        //网关锁上线
                        case HomeShowBean.TYPE_GATEWAY_LOCK:
                            GwLockInfo gwLockInfo= (GwLockInfo) homeShowBean.getObject();
                            if (gwLockInfo.getGwID().equals(gatewayId)) {
                                if ("online".equals(eventStr)) {
                                    gwLockInfo.getServerInfo().setEvent_str("online");
                                }else if ("offline".equals(eventStr)){
                                    gwLockInfo.getServerInfo().setEvent_str("offline");
                                }
                                if (deviceDetailAdapter!=null) {
                                    deviceDetailAdapter.notifyDataSetChanged();
                                }
                                LogUtils.e("网关锁上线下线了   "+eventStr+"网关的设备id  "+deviceId);
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
        if (deviceDetailAdapter!=null) {
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
        LogUtils.e("咪咪绑定注册失败"+code+"咪咪绑定失败原因"+msg);
    }

    @Override
    public void bindMimiThrowable(Throwable throwable) {
        LogUtils.e("咪咪绑定异常");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser==true){
            //切换左右切换Fragment时，刷新页面
            if (mDeviceList!=null&&mDeviceList.size()>0){
                if (deviceDetailAdapter!=null){
                    deviceDetailAdapter.notifyDataSetChanged();
                }
            }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==KeyConstants.GET_BLE_POWER){
            if (resultCode== Activity.RESULT_OK){
                BleLockInfo getBle= (BleLockInfo) data.getSerializableExtra(KeyConstants.BLE_INTO);
                if (getBle!=null&&mDeviceList!=null&&mDeviceList.size()>0){
                    for (HomeShowBean device : mDeviceList) {
                        //蓝牙电量
                        if (HomeShowBean.TYPE_BLE_LOCK==device.getDeviceType()){
                            if (device.getDeviceId().equals(getBle.getServerLockInfo().getLockName())){
                                BleLockInfo bleLockInfo= (BleLockInfo) device.getObject();
                                bleLockInfo.setBattery(getBle.getBattery());
                                if (getBle.isConnected()){
                                    bleLockInfo.setConnected(true);
                                }else{
                                    bleLockInfo.setConnected(false);
                                }
                                if (deviceDetailAdapter!=null) {
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
