package com.kaadas.lock.activity.device.gateway;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.adapter.GatewayAdapter;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaypresenter.GatewayPresenter;
import com.kaadas.lock.mvp.view.gatewayView.GatewayView;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by David on 2019/4/25
 */
public class GatewayActivity extends BaseActivity<GatewayView, GatewayPresenter<GatewayView>> implements BaseQuickAdapter.OnItemClickListener, GatewayView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    GatewayAdapter gatewayAdapter;
    @BindView(R.id.tv_gateway_status)
    TextView tvGatewayStatus;
    @BindView(R.id.gateway_nick_name)
    TextView gatewayNickName;
    @BindView(R.id.unbindGateway)
    TextView unbindGateway;
    @BindView(R.id.testunbindGateway)
    TextView testunbindGateway;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.see_more)
    ImageView seeMore;
    @BindView(R.id.share)
    ImageView share;
    @BindView(R.id.con_device)
    TextView conDevice;
    @BindView(R.id.no_device_layout)
    LinearLayout noDeviceLayout;

    private List<HomeShowBean> homeShowBeans;
    private boolean gatewayOnline = false;
    private GatewayInfo gatewayInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    private void initListener() {

    }

    @Override
    protected GatewayPresenter<GatewayView> createPresent() {
        return new GatewayPresenter<>();
    }

    private void initView() {

    }

    private void changeGatewayStatus(String eventStr) {
        if ("online".equals(eventStr)) {
            gatewayOnline = true;
        } else {
            gatewayOnline = false;
        }
        if (gatewayOnline) {
            tvGatewayStatus.setText(R.string.online);
            tvGatewayStatus.setTextColor(getResources().getColor(R.color.c068AEC));
            Drawable drawableLeft = getResources().getDrawable(
                    R.mipmap.gateway_online);
            tvGatewayStatus.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
        } else {
            tvGatewayStatus.setText(R.string.offline);
            tvGatewayStatus.setTextColor(getResources().getColor(R.color.c7CD2FF));
            Drawable drawableLeft = getResources().getDrawable(
                    R.mipmap.gateway_offline_gateway);
            tvGatewayStatus.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
        }
    }

    private void initData() {
        Intent intent = getIntent();
        HomeShowBean homeShowBean = (HomeShowBean) intent.getSerializableExtra(KeyConstants.GATEWAY_INFO);
        if (homeShowBean != null) {
            gatewayInfo = (GatewayInfo) homeShowBean.getObject();
            if (gatewayInfo != null) {
                gatewayNickName.setText(gatewayInfo.getServerInfo().getDeviceNickName());
                changeGatewayStatus(gatewayInfo.getEvent_str());
                mPresenter.getPowerData(gatewayInfo.getServerInfo().getDeviceSN());
                mPresenter.getPublishNotify();
                mPresenter.listenerDeviceOnline();
                mPresenter.listenerNetworkChange();
                homeShowBeans = mPresenter.getGatewayBindList(gatewayInfo.getServerInfo().getDeviceSN());
                initRecyclerview(homeShowBeans);
            }
        }

    }

    private void initRecyclerview(List<HomeShowBean> homeShowBeans) {
        if (homeShowBeans!=null&&homeShowBeans.size()>0){
            changeView(true);
            gatewayAdapter = new GatewayAdapter(homeShowBeans);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(gatewayAdapter);
            gatewayAdapter.setOnItemClickListener(this);
        }else{
            changeView(false);
        }


    }

    //改变视图
    public void changeView(boolean haveData) {
        if (haveData) {
            recyclerView.setVisibility(View.VISIBLE);
            conDevice.setVisibility(View.VISIBLE);
            noDeviceLayout.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            conDevice.setVisibility(View.INVISIBLE);
            noDeviceLayout.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {


    }

    @Override
    public void getPowerDataSuccess(String deviceId, int power) {
        //获取到电量
        LogUtils.e("设备名称   " + deviceId + "   电量   " + power);
        if (power < 0) {
            power = 0;
        }

        if (homeShowBeans != null && homeShowBeans.size() > 0) {
            for (HomeShowBean device : homeShowBeans) {
                //猫眼
                if (HomeShowBean.TYPE_CAT_EYE == device.getDeviceType()) {
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
                else if (HomeShowBean.TYPE_GATEWAY_LOCK == device.getDeviceType()) {
                    if (device.getDeviceId().equals(device)) {
                        GwLockInfo gwLockInfo = (GwLockInfo) device.getObject();
                        gwLockInfo.setPower(power / 2);
                        LogUtils.e("设置zigbee电量成功" + power);
                        if (gatewayAdapter != null) {
                            gatewayAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }


        }


    }

    @Override
    public void getPowerDataFail(String gatewayId, String deviceId) {
       /* //获取电量失败
        if (homeShowBeans != null && homeShowBeans.size() > 0) {
            for (HomeShowBean device : homeShowBeans) {
                //猫眼电量
                if (HomeShowBean.TYPE_CAT_EYE == device.getDeviceType()) {
                    if (device.getDeviceId().equals(deviceId)) {
                        CateEyeInfo cateEyeInfo = (CateEyeInfo) device.getObject();
                        if ("online".equals(cateEyeInfo.getServerInfo().getEvent_str())) {
                            cateEyeInfo.getServerInfo().setEvent_str("offline");
                            if (gatewayAdapter != null) {
                                gatewayAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else if (HomeShowBean.TYPE_GATEWAY_LOCK == device.getDeviceType()) {
                    if (device.getDeviceId().equals(deviceId)) {
                        GwLockInfo gwLockInfo = (GwLockInfo) device.getObject();
                        if ("online".equals(gwLockInfo.getServerInfo().getEvent_str())) {
                            gwLockInfo.getServerInfo().setEvent_str("offline");
                            if (gatewayAdapter != null) {
                                gatewayAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        }*/


    }

    @Override
    public void getPowerThrowable() {

    }

    @Override
    public void gatewayStatusChange(String gatewayId, String eventStr) {
        //网关状态发生改变
        LogUtils.e("GatewayActivity网关状态发生改变");
        //当前网关
        LogUtils.e("改变网关id是  " + gatewayId + "当前的网关id是  " + gatewayInfo.getServerInfo().getDeviceSN());
        if (gatewayInfo != null) {
            if (gatewayInfo.getServerInfo().getDeviceSN().equals(gatewayId)) {
                LogUtils.e("监听网关Device的状态      " + gatewayId);
                gatewayInfo.setEvent_str(eventStr);
                changeGatewayStatus(eventStr);
                //获取网关下绑定的设备,把网关下的设备设置为离线.网关离线设备也离线
                if ("offline".equals(eventStr)) {
                    if (homeShowBeans != null && homeShowBeans.size() > 0) {
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
                if (gatewayAdapter != null) {
                    gatewayAdapter.notifyDataSetChanged();
                }
            }
        }

    }


    @Override
    public void deviceStatusChange(String gatewayId, String deviceId, String eventStr) {
//网关状态发生改变
        LogUtils.e("Gateway设备状态发生改变");
        if (homeShowBeans != null && homeShowBeans.size() > 0) {
            for (HomeShowBean homeShowBean : homeShowBeans) {
                if (deviceId.equals(homeShowBean.getDeviceId())) {
                    switch (homeShowBean.getDeviceType()) {
                        //猫眼上线
                        case HomeShowBean.TYPE_CAT_EYE:
                            CateEyeInfo cateEyeInfo = (CateEyeInfo) homeShowBean.getObject();
                            if (cateEyeInfo.getGwID().equals(gatewayId)) {
                                if ("online".equals(eventStr)) {
                                    cateEyeInfo.getServerInfo().setEvent_str("online");
                                } else {
                                    cateEyeInfo.getServerInfo().setEvent_str("offline");
                                }
                                if (gatewayAdapter != null) {
                                    gatewayAdapter.notifyDataSetChanged();
                                }
                                LogUtils.e("猫眼上线下线了   " + eventStr + "猫眼的设备id  " + deviceId);
                            }
                            break;
                        //网关锁上线
                        case HomeShowBean.TYPE_GATEWAY_LOCK:
                            GwLockInfo gwLockInfo = (GwLockInfo) homeShowBean.getObject();
                            if (gwLockInfo.getGwID().equals(gatewayId)) {
                                if ("online".equals(eventStr)) {
                                    gwLockInfo.getServerInfo().setEvent_str("online");
                                } else if ("offline".equals(eventStr)) {
                                    gwLockInfo.getServerInfo().setEvent_str("offline");
                                }
                                if (gatewayAdapter != null) {
                                    gatewayAdapter.notifyDataSetChanged();
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
    public void unbindGatewaySuccess() {
        //解绑成功
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void unbindGatewayFail() {

    }

    @Override
    public void unbindGatewayThrowable(Throwable throwable) {

    }

    @Override
    public void unbindTestGatewaySuccess() {
        //解绑成功
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void unbindTestGatewayFail() {

    }

    @Override
    public void unbindTestGatewayThrowable(Throwable throwable) {

    }

    @Override
    public void networkChangeSuccess() {
        if (gatewayAdapter != null) {
            gatewayAdapter.notifyDataSetChanged();
        }
    }


    @OnClick({R.id.unbindGateway, R.id.testunbindGateway, R.id.back, R.id.see_more, R.id.share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //解绑网关
            case R.id.unbindGateway:
                if (gatewayInfo != null) {
                    mPresenter.unBindGateway(MyApplication.getInstance().getUid(), gatewayInfo.getServerInfo().getDeviceSN());
                }
                break;
            //测试解绑网关
            case R.id.testunbindGateway:
                if (gatewayInfo != null) {
                    mPresenter.testUnbindGateway(MyApplication.getInstance().getUid(), gatewayInfo.getServerInfo().getDeviceSN(), gatewayInfo.getServerInfo().getDeviceSN());
                }
                break;
            case R.id.back:
                finish();
                break;
            case R.id.see_more:
                //基本信息
                if (gatewayInfo!=null) {
                    Intent intent = new Intent(this, GatewaySettingActivity.class);
                 //   intent.putExtra(KeyConstants.GATEWAY_NICKNAME, gatewayInfo.getServerInfo().getDeviceNickName());
                    if(!TextUtils.isEmpty(gatewayNickName.getText())){
                        intent.putExtra(KeyConstants.GATEWAY_NICKNAME, gatewayNickName.getText().toString());
                    }else {
                        intent.putExtra(KeyConstants.GATEWAY_NICKNAME, gatewayInfo.getServerInfo().getDeviceNickName());
                    }
                    intent.putExtra(KeyConstants.GATEWAY_NICKNAME, gatewayNickName.getText().toString());
                    intent.putExtra(KeyConstants.GATEWAY_ID, gatewayInfo.getServerInfo().getDeviceSN());
                    intent.putExtra(KeyConstants.IS_ADMIN, gatewayInfo.getServerInfo().getIsAdmin());
                    startActivityForResult(intent, KeyConstants.GATEWAY_NICK_NAME);
                }
                break;
            case R.id.share:
               /* //分享
                Intent shareIntent = new Intent(this, GatewaySharedActivity.class);
                shareIntent.putExtra(KeyConstants.GATEWAY_ID, gatewayInfo.getServerInfo().getDeviceSN());
                startActivity(shareIntent);*/
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getIntent() != null) {
            getIntent().removeExtra(KeyConstants.GATEWAY_INFO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KeyConstants.GATEWAY_NICK_NAME) {
            if (resultCode == Activity.RESULT_OK) {
                String name = data.getStringExtra(KeyConstants.GATEWAY_NICKNAME);
                if (name != null) {
                    if (gatewayNickName != null) {
                        gatewayNickName.setText(name);
                    }
                }
            }
        }
    }
}
