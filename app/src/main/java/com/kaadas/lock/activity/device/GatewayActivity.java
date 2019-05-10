package com.kaadas.lock.activity.device;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.GatewayAdapter;
import com.kaadas.lock.bean.DeviceDetailBean;
import com.kaadas.lock.bean.GatewayDeviceDetailBean;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaypresenter.GatewayPresenter;
import com.kaadas.lock.mvp.view.gatewayView.GatewayView;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/25
 */
public class GatewayActivity extends BaseActivity<GatewayView, GatewayPresenter<GatewayView>> implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener,GatewayView {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    GatewayAdapter gatewayAdapter;
    @BindView(R.id.tv_gateway_status)
    TextView tvGatewayStatus;
    @BindView(R.id.gateway_nick_name)
    TextView gatewayNickName;

    private List<HomeShowBean> homeShowBeans;
    private boolean gatewayOnline=false;
    private GatewayInfo gatewayInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected GatewayPresenter<GatewayView> createPresent() {
        return new GatewayPresenter<>();
    }

    private void initView() {
        tvContent.setText(R.string.my_device);
        ivBack.setOnClickListener(this);
    }

    private void changeGatewayStatus(String eventStr) {
        if ("online".equals(eventStr)){
            gatewayOnline=true;
        }else{
            gatewayOnline=false;
        }
        if (gatewayOnline) {
            tvGatewayStatus.setText(R.string.online);
            tvGatewayStatus.setTextColor(getResources().getColor(R.color.c1F96F7));
            Drawable drawableLeft = getResources().getDrawable(
                    R.mipmap.wifi_connect);
            tvGatewayStatus.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
        } else {
            tvGatewayStatus.setText(R.string.offline);
            tvGatewayStatus.setTextColor(getResources().getColor(R.color.c999999));
            Drawable drawableLeft = getResources().getDrawable(
                    R.mipmap.wifi_disconnect);
            tvGatewayStatus.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
        }
    }

    private void initData() {
        Intent intent = getIntent();
        HomeShowBean homeShowBean = (HomeShowBean) intent.getSerializableExtra(KeyConstants.GATEWAY_INFO);
        if (homeShowBean != null) {
            gatewayInfo= (GatewayInfo) homeShowBean.getObject();
            if (gatewayInfo!=null) {
                gatewayNickName.setText(gatewayInfo.getServerInfo().getDeviceNickName());
                changeGatewayStatus(gatewayInfo.getEvent_str());
                mPresenter.getPowerData(gatewayInfo.getServerInfo().getDeviceSN());
                mPresenter.getPublishNotify();
                mPresenter.listenerDeviceOnline();
                homeShowBeans = mPresenter.getGatewayBindList(gatewayInfo.getServerInfo().getDeviceSN());
                initRecyclerview(homeShowBeans);
            }
        }

    }

    private void initRecyclerview(List<HomeShowBean> homeShowBeans) {
        gatewayAdapter = new GatewayAdapter(homeShowBeans);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(gatewayAdapter);
        gatewayAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {




    }

    @Override
    public void getPowerDataSuccess(String deviceId, int power) {
        //获取到电量
        LogUtils.e("设备名称   "+deviceId+"   电量   "+power);
        if (homeShowBeans!=null&&homeShowBeans.size()>0){
            for (HomeShowBean device:homeShowBeans){
                //猫眼
                if (HomeShowBean.TYPE_CAT_EYE==device.getDeviceType()){
                    if (device.getDeviceId().equals(deviceId)) {
                        CateEyeInfo cateEyeInfo = (CateEyeInfo) device.getObject();
                        cateEyeInfo.setPower(power);
                        LogUtils.e("设置猫眼电量成功" + power);
                        if (gatewayAdapter != null) {
                            gatewayAdapter.notifyDataSetChanged();
                        }
                    }
                }
                //网关锁
                else if (HomeShowBean.TYPE_GATEWAY_LOCK==device.getDeviceType()){
                    if (device.getDeviceId().equals(device)){
                        GwLockInfo gwLockInfo= (GwLockInfo) device.getObject();
                        gwLockInfo.setPower(power);
                        LogUtils.e("设置zigbee电量成功"+power);
                        if (gatewayAdapter!=null){
                            gatewayAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }


        }



    }

    @Override
    public void getPowerDataFail(String gatewayId,String deviceId) {
        //获取电量失败
        if (homeShowBeans!=null&&homeShowBeans.size()>0) {
            for (HomeShowBean device : homeShowBeans) {
                //猫眼电量
                if (HomeShowBean.TYPE_CAT_EYE==device.getDeviceType()){
                    if (device.getDeviceId().equals(deviceId)){
                        CateEyeInfo cateEyeInfo= (CateEyeInfo) device.getObject();
                        if ("online".equals(cateEyeInfo.getServerInfo().getEvent_str())){
                            cateEyeInfo.getServerInfo().setEvent_str("offline");
                            if (gatewayAdapter!=null) {
                                gatewayAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }else if (HomeShowBean.TYPE_GATEWAY_LOCK==device.getDeviceType()){
                    if (device.getDeviceId().equals(deviceId)){
                        GwLockInfo gwLockInfo= (GwLockInfo) device.getObject();
                        if ("online".equals(gwLockInfo.getServerInfo().getEvent_str())){
                            gwLockInfo.getServerInfo().setEvent_str("offline");
                            if (gatewayAdapter!=null) {
                                gatewayAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        }


    }

    @Override
    public void getPowerThrowable() {

    }

    @Override
    public void gatewayStatusChange(String gatewayId, String eventStr) {
        //网关状态发生改变
        LogUtils.e("GatewayActivity网关状态发生改变");
        //当前网关
        LogUtils.e("改变网关id是  "+gatewayId+"当前的网关id是  "+gatewayInfo.getServerInfo().getDeviceSN());
        if (gatewayInfo!=null){
            if (gatewayInfo.getServerInfo().getDeviceSN().equals(gatewayId)){
                LogUtils.e("监听网关Device的状态      " + gatewayId);
                gatewayInfo.setEvent_str(eventStr);
                changeGatewayStatus(eventStr);
                //获取网关下绑定的设备,把网关下的设备设置为离线.网关离线设备也离线
                if ("offline".equals(eventStr)) {
                    if (homeShowBeans!=null&&homeShowBeans.size()>0) {
                        for (HomeShowBean gatewayBind : homeShowBeans) {
                            switch (gatewayBind.getDeviceType()) {
                                //猫眼
                                case HomeShowBean.TYPE_CAT_EYE:
                                    CateEyeInfo cateEyeInfo = (CateEyeInfo) gatewayBind.getObject();
                                    cateEyeInfo.getServerInfo().setEvent_str("offline");
                                    break;
                                //网关锁
                                case HomeShowBean.TYPE_GATEWAY_LOCK:
                                    GwLockInfo gwLockInfo = (GwLockInfo) gatewayBind.getObject();
                                    gwLockInfo.getServerInfo().setEvent_str("offline");
                                    break;
                            }
                        }
                    }
                }
                if (gatewayAdapter!=null){
                    gatewayAdapter.notifyDataSetChanged();
                }
            }
        }

        }


    @Override
    public void deviceStatusChange(String gatewayId, String deviceId, String eventStr) {
//网关状态发生改变
        LogUtils.e("Gateway设备状态发生改变");
        if (homeShowBeans!=null&&homeShowBeans.size()>0) {
            for (HomeShowBean homeShowBean : homeShowBeans) {
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
                                if (gatewayAdapter!=null) {
                                    gatewayAdapter.notifyDataSetChanged();
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
                                if (gatewayAdapter!=null) {
                                    gatewayAdapter.notifyDataSetChanged();
                                }
                                LogUtils.e("网关锁上线下线了   "+eventStr+"网关的设备id  "+deviceId);
                            }
                            break;
                    }
                }
            }
        }



    }
}
